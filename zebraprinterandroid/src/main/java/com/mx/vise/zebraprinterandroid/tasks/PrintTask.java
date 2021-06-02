package com.mx.vise.zebraprinterandroid.tasks;

import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Looper;
import android.util.Log;


import com.mx.vise.zebraprinterandroid.PrinterHelper;
import com.mx.vise.zebraprinterandroid.R;
import com.mx.vise.zebraprinterandroid.entities.PrintObject;
import com.mx.vise.zebraprinterandroid.enums.PrintStatus;
import com.mx.vise.zebraprinterandroid.interfaces.OnPrintListener;
import com.mx.vise.zebraprinterandroid.utils.TinyDB;
import com.zebra.sdk.comm.BluetoothConnectionInsecure;
import com.zebra.sdk.comm.Connection;
import com.zebra.sdk.comm.ConnectionException;
import com.zebra.sdk.graphics.internal.ZebraImageAndroid;
import com.zebra.sdk.printer.ZebraPrinter;
import com.zebra.sdk.printer.ZebraPrinterFactory;
import com.zebra.sdk.printer.ZebraPrinterLanguageUnknownException;

import java.util.ArrayList;

import static android.content.ContentValues.TAG;


/**
 * **************************VISE*******************************
 * *******************DEPARTAMENTO DE T.I.**********************
 * <p>
 * Creado por aloza el lunes 15 de octubre del 2018
 *
 * @author Angelo de Jesus Loza Martinez
 * @version acarreosandroid
 */
public class PrintTask extends AsyncTask<ArrayList<PrintObject>, Void, PrintStatus> {

    private final Context mContext;
    private ProgressDialog mProgressDialog;
    private OnPrintListener mOnPrintListener;
    private boolean mPrintOnce;

    public PrintTask(Context context, boolean printOnce) {
        this.mPrintOnce = printOnce;
        this.mContext = context;
    }


    @Override
    protected void onPreExecute() {
        mProgressDialog = new ProgressDialog(mContext);
        mProgressDialog.setMessage(mContext.getString(R.string.printing));
        mProgressDialog.setCancelable(false);
        mProgressDialog.show();
    }

    @Override
    protected PrintStatus doInBackground(ArrayList<PrintObject>... arrayLists) {
        // Instantiate insecure connection for given Bluetooth&reg; MAC Address.
        TinyDB tinyDb = new TinyDB(mContext);

        String macAddress = tinyDb.getString(mContext.getString(R.string.printer_mac));
        String deviceName = tinyDb.getString(mContext.getString(R.string.printer_name));


        boolean isTSC = false;
        if(deviceName != null)
            isTSC = deviceName.contains("RF");


        Connection printerConnection = null;
        if (macAddress != null)
            printerConnection = new BluetoothConnectionInsecure(macAddress);
        else
            return PrintStatus.MAC_DOES_NOT_DECLARED;

        if (Looper.myLooper() == null) {
            Looper.prepare();
        }

        // Open the connection - physical connection is established here.
        try {
            printerConnection.open();
            ZebraPrinter zebraPrinter = ZebraPrinterFactory.getInstance(printerConnection);
            PrinterHelper printerHelper = new PrinterHelper();

            int image =!isTSC ?  R.drawable.ic_vise_white : R.drawable.ic_vise_white_inverted;

            printerHelper.setConnection(printerConnection);

            // Send the data to printer as a byte array.
            String dataToPrint = printerHelper.formatObjectToPrinter(isTSC, arrayLists[0]);
            Bitmap bitmap = arrayLists[0].get(arrayLists[0].size() - 1).getImage();


            Bitmap viseBtimap = BitmapFactory.decodeResource(mContext.getResources(),image);

            viseBtimap = (Bitmap.createScaledBitmap(viseBtimap, 350, 200, false));
            if (bitmap != null) {
                printerConnection.write("^XA^PON^PW400^MNN^LL180LH0,0\r\n^XZ".getBytes());
                Log.i(TAG, "doInBackground: width:" + bitmap.getWidth() + "," + bitmap.getHeight());
                zebraPrinter.printImage(new ZebraImageAndroid(bitmap), 0, 0, bitmap.getWidth(), bitmap.getHeight(), false);
            }
            Thread.sleep(500);

            printerConnection.write(dataToPrint.getBytes());

            Thread.sleep(500);

            printerConnection.write("^XA^PON^PW400^MNN^LL250LH0,0\r\n^XZ".getBytes());
            //printImage(ZebraImageI image, int x, int y, int width, int height, boolean insideFormat)
            zebraPrinter.printImage(new ZebraImageAndroid(viseBtimap), 20, 35, viseBtimap.getWidth(), viseBtimap.getHeight(), isTSC);
            //Make sure the data got to the printer before closing the connection
            Thread.sleep(1000);

            if (!mPrintOnce) {

                printerConnection.write(dataToPrint.getBytes());

                Thread.sleep(500);
                if(!isTSC) {
                    printerConnection.write("^XA^PON^PW400^MNN^LL250LH0,0\r\n^XZ".getBytes());
                    zebraPrinter.printImage(new ZebraImageAndroid(viseBtimap), 20, 35, viseBtimap.getWidth(), viseBtimap.getHeight(), false);
                }
                //Make sure the data got to the printer before closing the connection
                Thread.sleep(500);
            }
            // Close the insecure connection to release resources.

            printerConnection.close();

            Looper.myLooper().quit();
        } catch (ConnectionException e) {
            String msg = e.getMessage();
            if(msg.contains("is not a valid Bluetooth address")){
                return PrintStatus.MAC_DOES_NOT_DECLARED;
            } else if (msg.contains("socket might closed or timeout")){
                return PrintStatus.CAN_NOT_CONNECT_TO_ESTABLISHED_PRINTER;
            }
            e.printStackTrace();

        } catch (InterruptedException e) {
            e.printStackTrace();
            return PrintStatus.MAC_DOES_NOT_DECLARED;
        } catch (ZebraPrinterLanguageUnknownException e) {
            e.printStackTrace();
            return PrintStatus.MAC_DOES_NOT_DECLARED;
        }
        return PrintStatus.OK;
    }

    @Override
    protected void onPostExecute(PrintStatus status) {

        mProgressDialog.dismiss();
        if (status == PrintStatus.OK) {
            if (mOnPrintListener != null)
                mOnPrintListener.onPrintSuccess();
        } else {
            mOnPrintListener.onPrintFailed(status);
        }
    }

    public OnPrintListener getOnPrintListener() {
        return mOnPrintListener;
    }

    public PrintTask setOnPrintListener(OnPrintListener onPrintListener) {
        this.mOnPrintListener = onPrintListener;
        return this;
    }

}
