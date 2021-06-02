package com.mx.vise.uhf;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.media.MediaPlayer;
import android.os.Build;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.mx.vise.androiduihelper.UIHelper;
import com.mx.vise.uhf.entities.Key;
import com.mx.vise.uhf.entities.Tag;
import com.mx.vise.uhf.interfaces.OnTagCleanListener;
import com.mx.vise.uhf.interfaces.OnTagReadListener;
import com.mx.vise.uhf.interfaces.OnTagWriteListener;
import com.mx.vise.uhf.interfaces.OnUHFDetectListener;
import com.mx.vise.uhf.tag.Sector;
import com.mx.vise.uhf.tag.TagData;
import com.mx.vise.uhf.tasks.ReadUHFTask;
import com.mx.vise.uhf.tasks.ReadingTagTask;
import com.mx.vise.uhf.tasks.WriteUHFTask;
import com.mx.vise.uhf.util.AESencrp;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import android_serialport_api.SerialPortManager;
import android_serialport_api.UHFHXAPI;

import static com.mx.vise.uhf.UHFTagReadWrite.DEFAULT_AP;
import static com.mx.vise.uhf.UHFTagReadWrite.ERROR_CODE;
import static com.mx.vise.uhf.tasks.ReadingTagTask.CODE;


/**
 * **************************VISE*******************************
 * *******************DEPARTAMENTO DE T.I.**********************
 * <p>
 * Creado por aloza el martes 22 de mayo del 2018
 * <p>
 * Refactorado el día 5 de noviembre de 2018 por @aloza
 *
 * @author Angelo de Jesus Loza Martinez
 * @version combustible
 */

public class UHFHelper {

    public static final int MSG_SHOW_EPC_INFO = 1;
    protected static final int MSG_START_READING_TID_ONLY = 2;
    protected static final int INVENTORY_OVER = 3;
    public static final int TIMEOUT = 4;
    public static final int MSG_TAG_WRITE_SUCCESS = 5;
    public static final int MSG_SHOW_TID = 6;
    public static final int TIMEOUT_WRITE = 7;
    public static final int CANNOT_OVERWRITE = 8;
    private static final int MSG_START_WRITING = 100;
    private static final int MSG_START_COMPARE_TIDS = 102;
    private static final int MSG_SHOW_TID_ONCE = 106;
    private static final long ERASE_TIMEOUT = 30000;
    private static final String TAG = "VISE";
    public static long WRITE_TIMEOUT = 90000;
    public static final short LIMIT_READ_WRITE = 127;

    private final Context mContext;
    private final MediaPlayer mediaPlayer;
    public final UHFHXAPI mApi;
    private OnUHFDetectListener mDetectListener;
    private StartHander mHandler;
    private ReadUHFTask mReadTask;
    private WriteUHFTask mWriteTask;
    private ExecutorService mExecutor;
    private ArrayList<String> mCapturedTags;


    public static ProgressDialog dialog;
    public static UIHelper.CustomProgressBar progressBar;
    private List<String> mVoucherUhfs;
    private int mNoTagsRead;
    private boolean mCanOverwrite;
    public static boolean PROCEED_WRITING_FLAG = true;
    private OnTagCleanListener mCleanTagListener;

    public UHFHelper setCleanTagListener(OnTagCleanListener mCleanTagListener) {
        this.mCleanTagListener = mCleanTagListener;
        return this;
    }


    /**
     * Clase estática para el procesamiento central de información de eventos, como la visualización
     *
     * @author chenshanjing
     */
    private class StartHander extends Handler {


        private static final String TAG = "VISE";


        @Override
        public void handleMessage(final Message msg) {

            switch (msg.what) {
                case MSG_SHOW_EPC_INFO:
                    showTID((String) msg.obj, true);
                    break;
                case MSG_SHOW_TID:
                    showReadingUHFDialog();
                    showTID((Tag) msg.obj);
                    stopUHFCapture();
                    break;
                case MSG_START_WRITING:
                    showReadingUHFDialog();
                    startCapture(((SimpleReadingHandlerMessage) msg.obj).getTagData());
                    break;
                case MSG_START_COMPARE_TIDS:
                    showReadingUHFDialog();
                    if ((Boolean) msg.obj)
                        startCapture(false);
                    else stopUHFCapture(false);
                    break;
                case MSG_START_READING_TID_ONLY:
                    showReadingUHFDialog();
                    if ((Boolean) msg.obj)
                        startCapture(true);
                    else stopUHFCapture(false);
                    break;
                case INVENTORY_OVER:
                    stopUHFCapture();
                    break;
                case TIMEOUT:
                    stopUHFCapture(false);
                    mDetectListener.onTagReadTimeout();
                    break;
                case MSG_TAG_WRITE_SUCCESS:
                    if ((Boolean) msg.obj) {
                        playSound();
                        stopUHFCapture();
                        mDetectListener.onTagWriteSuccess();
                    } else {
                        mDetectListener.onTagWriteFailed(false);
                        stopUHFCapture();
                    }
                    break;
                case TIMEOUT_WRITE:
                    stopUHFCapture();
                    break;
                case CANNOT_OVERWRITE:
                    mDetectListener.onTagWriteFailed(true);
                    stopUHFCapture();
                    break;
            }
        }
    }

    /*
     *
     *
     * */

    public UHFHelper(Context context, OnUHFDetectListener detectListener, boolean read) {
        this.mContext = context;
        this.mediaPlayer = MediaPlayer.create(context, com.authentication.activity.R.raw.ok);
        this.mDetectListener = detectListener;
        mApi = new UHFHXAPI();
        mHandler = new StartHander();
        mExecutor = Executors.newSingleThreadExecutor();
        if (read)
            mReadTask = new ReadUHFTask(mApi, mHandler, mExecutor);
        else
            mWriteTask = new WriteUHFTask(mApi, mHandler, mExecutor);
        openSerial();
    }

    public UHFHelper(Context context, ExecutorService executor, UHFHXAPI api) {
        this.mContext = context;
        mediaPlayer = null;
        this.mApi = api;
        this.mExecutor = executor;
    }

    public boolean open() {
        openSerial();
        return mApi.open();
    }

    public void close() {
        mApi.close();
        closeSerial();
    }

    public void canOverwrite(boolean canOverwrite) {
        this.mCanOverwrite = canOverwrite;
    }

    private void openSerial() {
        if (SerialPortManager.getInstance().isOpen())
            SerialPortManager.getInstance().closeSerialPort();
        SerialPortManager.getInstance().openSerialPort();
    }

    private void closeSerial() {
        if (SerialPortManager.getInstance().isOpen())
            SerialPortManager.getInstance().closeSerialPort();
    }

    /**
     * Método para comparar una lista de tags con el leído.
     *
     * @param tags la lista de tags a comparar
     */
    public void startReading(List<String> tags) {
        this.mCapturedTags = new ArrayList<>();
        this.mVoucherUhfs = tags;
        showOpeningUHFDialog();
        new Thread() {
            @Override
            public void run() {
                Message closemsg = new Message();
                closemsg.obj = mApi.open();
                closemsg.what = MSG_START_COMPARE_TIDS;
                mHandler.sendMessage(closemsg);
            }
        }.start();
        dialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialogInterface) {
                stopUHFCapture(false);
            }
        });

    }

    /**
     * Método para comenzar la escritura del tag.
     *
     * @param tagData los datos del tag a escribir
     */
    public void startReading(final TagData tagData) {
        showOpeningUHFDialog();
        new Thread() {
            @Override
            public void run() {
                Message closemsg = new Message();
                SimpleReadingHandlerMessage simpleReadingHandlerMessage = new SimpleReadingHandlerMessage();
                simpleReadingHandlerMessage.setOpen(mApi.open());
                simpleReadingHandlerMessage.setTagData(tagData);
                closemsg.obj = simpleReadingHandlerMessage;
                closemsg.what = MSG_START_WRITING;
                mHandler.sendMessage(closemsg);
            }
        }.start();
        dialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialogInterface) {
                stopUHFCapture();
            }
        });
    }


    /**
     * Método para leer solamente el TID
     */
    public void startReading() {
        showOpeningUHFDialog();
        new Thread() {
            @Override
            public void run() {
                Message closemsg = new Message();
                closemsg.obj = mApi.open();
                closemsg.what = MSG_START_READING_TID_ONLY;
                mHandler.sendMessage(closemsg);
            }
        }.start();
        dialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialogInterface) {
                stopUHFCapture(false);
            }
        });
    }

    /**
     * Show the opening UHF dialog
     */
    private void showOpeningUHFDialog() {
        dialog = ProgressDialog.show(mContext, "Abriendo UHF", "Conectando dispositivo, por favor espere...", true, true);
        openSerial();
    }

    /**
     * Show reading UHF Dialog (cancel everything if is cancelled)
     */
    private void showReadingUHFDialog() {
        if (dialog != null)
            dialog.dismiss();
        dialog = ProgressDialog.show(mContext, "Lectura de UHF", "Leyendo UHF...", true, true);
        dialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialogInterface) {
                stopUHFCapture();
            }
        });
    }

    public boolean canOverwrite() {
        return mCanOverwrite;
    }

    private class SimpleReadingHandlerMessage {
        private boolean isOpen;
        private TagData tagData;

        public boolean isOpen() {
            return isOpen;
        }

        public void setOpen(boolean open) {
            isOpen = open;
        }

        public void setTagData(TagData tagData) {
            this.tagData = tagData;
        }

        public TagData getTagData() {
            return tagData;
        }
    }


    public void stopRead() {
        if (mReadTask != null) mReadTask.stopTask();
    }

    /**
     * comienza la captura de tags y su comparación con la lista dada
     */
    private void startCapture(boolean readTIDOnly) {
        mReadTask.setStartTime(System.currentTimeMillis());
        mNoTagsRead = 0;
        mReadTask.readTIDOnly(readTIDOnly);
        mExecutor.execute(mReadTask);
    }

    /**
     * @param tagData comienza la captura de tag y su escritura
     */
    private void startCapture(TagData tagData) {
        mWriteTask.setStartTime(System.currentTimeMillis());
        mWriteTask.canOverwrite(canOverwrite());
        mWriteTask.setTagData(tagData);
        mExecutor.execute(mWriteTask);
    }


    private void stopUHFCapture(boolean isValid) {
        if (mReadTask != null)
            mReadTask.stopTask();
        if (mWriteTask != null)
            mWriteTask.stopTask();
        if (mDetectListener != null)
            mDetectListener.onUHFSDetected(mCapturedTags, isValid);
    }

    private void stopUHFCapture() {
        if (mReadTask != null)
            mReadTask.stopTask();
        if (mWriteTask != null)
            mWriteTask.stopTask();
    }

    /**
     * Mostrar información de etiqueta buscada
     *
     * @param tid
     */
    private void showTID(String tid, boolean isSimple) {
        if (isSimple) {
            //agrega el tag a la lista
            if (tid != null && !tid.equals("")) {
                if (!mCapturedTags.contains(tid)) {
                    mCapturedTags.add(tid);
                    mNoTagsRead++;
                    dialog.setMessage("Leyendo tag...\nTags leídos: " + mNoTagsRead);
                }
                //recorre cada tag de la unidad
                for (String voucherTid : getVoucherUhfs())
                    //si hay un tag de la unidad que concuerde con el leído
                    if (cleanTid(tid).equals(cleanTid(voucherTid))) {
                        playSound();
                        stopUHFCapture(true);
                    }
            }
        } else {
            mDetectListener.onTagRead(new Tag().setTid(tid));
        }

    }

    /**
     * Mostrar información de etiqueta buscada
     *
     * @param tag
     */
    private void showTID(Tag tag) {
        mDetectListener.onTagRead(tag);
    }

    private void playSound() {
        if (mediaPlayer == null)
            return;
        if (mediaPlayer.isPlaying())
            mediaPlayer.seekTo(0);
        else
            mediaPlayer.start();

    }

    private String cleanTid(String tid) {
        return tid.replace(" ", "".toLowerCase());
    }


    private ArrayList<String> getCapturedTags() {
        return mCapturedTags;
    }

    private void setCapturedTags(ArrayList<String> capturedTags) {
        this.mCapturedTags = capturedTags;
    }

    public List<String> getVoucherUhfs() {
        return mVoucherUhfs;
    }

    public void setVoucherUhfs(List<String> voucherUhfs) {
        this.mVoucherUhfs = voucherUhfs;
    }

    public void eraseTagData(final String epc) {
        new Thread(new Runnable() {
            @Override
            public void run() {

                ((AppCompatActivity) mContext).runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        dialog = ProgressDialog.show(mContext, "Leyendo tag", "reestructurando tag...", true, true);
                    }
                });

                String writeData = "";
                for (int i = 0; i < 448; i++)
                    writeData += "0";


                openSerial();
                mApi.open();
                Tag tag = new Tag().setEPC(epc).setAP(new Key());

                final UHFTagReadWrite uhfTagReadWrite = new UHFTagReadWrite(tag, mApi);
                boolean successErased = uhfTagReadWrite.writeToUserData(writeData);
                if (successErased) {
                    ((AppCompatActivity) mContext).runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            dialog.dismiss();
                            if (mCleanTagListener != null)
                                mCleanTagListener.onTagCleanSuccess();
                        }
                    });
                } else {
                    ((AppCompatActivity) mContext).runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            dialog.dismiss();
                            if (mCleanTagListener != null)
                                mCleanTagListener.onTagCleanFailed();
                        }
                    });
                }


                mApi.close();
                closeSerial();
            }
        }).start();

    }


    public void writeDataToTag(final TagData tagData) {

        new Thread(new Runnable() {
            @Override
            public void run() {
                long startTime = System.currentTimeMillis();
                ((AppCompatActivity) mContext).runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        dialog = ProgressDialog.show(mContext, "Escribiendo tag", "Escribiendo datos en tag...", true, true);
                    }
                });

                Tag tag = new Tag().setEPC(tagData.getEPC()).setAP(new Key(DEFAULT_AP));

                final UHFTagReadWrite uhfTagReadWrite = new UHFTagReadWrite(tag, mApi);

                /*
                 * Si no la lee con la contraseña default cambia a la establecida
                 * (ya se sabe que el tag no es virgen)
                 * */
                String ap = uhfTagReadWrite.readAP();

                boolean isVirgin;
                if (ap != null) {
                    if (ap.equals(ERROR_CODE)) {
                        isVirgin = false;
                        /*
                         * Si se ha establecido que se puede sobreescribir entonces sobreescribe.
                         * (es falso por default)
                         * si no regresa error.
                         * */
                        if (canOverwrite()) {
                            tag.setAP(new Key());
                        } else {
                            ((AppCompatActivity) mContext).runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    dialog.dismiss();
                                    mDetectListener.onTagWriteFailed(true);
                                }
                            });
                            return;
                        }
                    } else {
                        isVirgin = true;
                    }
                } else {
                    /*
                     * Si no se puede leer la ap entonces regresa
                     * */
                    ((AppCompatActivity) mContext).runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            dialog.dismiss();
                            mDetectListener.onTagWriteFailed(false);
                        }
                    });
                    return;
                }
                /*
                 * Si no es virgen
                 * */

                if (!isVirgin) {
                    /*
                     * Genera una copia de seguridad de los datos antes de borrar
                     * */
                    String backup = uhfTagReadWrite.readUserData();

                    /*
                     *
                     * Elimina todos sus datos
                     *
                     * */
                    boolean correctErasing = false;
                    String writeData = "";
                    /*
                     *
                     * le caben 448 ceros
                     *
                     * */
                    for (int i = 0; i < 448; i++)
                        writeData += "0";

                    /*
                     * Mientras no se haya borrado correctamente continua intentando por 30 segundos
                     * */
                    while (!correctErasing) {
                        /*
                         * Si sobrepasa los 30 segundos regresa
                         * */
                        if (System.currentTimeMillis() - startTime >= ERASE_TIMEOUT) {
                            ((AppCompatActivity) mContext).runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    dialog.dismiss();
                                    mDetectListener.onTagWriteTimeout();
                                }
                            });
                            /*
                             * Reescribe los datos anteriores
                             * */
                            uhfTagReadWrite.writeData(backup, isVirgin);
                            return;
                        }
                        uhfTagReadWrite.writeData(writeData, isVirgin);

                        try {
                            Thread.sleep(200);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                        String userData = uhfTagReadWrite.readUserData(LIMIT_READ_WRITE);

                        correctErasing = memoryErasedSuccess(userData);
                    }
                }
                /*
                 * Una vez borrada la información que tenga, escribe la nueva con la misma logica anterior
                 *
                 * */
                boolean correctInfo = false;
                while (!correctInfo) {
                    if (System.currentTimeMillis() - startTime >= WRITE_TIMEOUT) {
                        ((AppCompatActivity) mContext).runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                dialog.dismiss();
                                mDetectListener.onTagWriteTimeout();
                            }
                        });

                        return;
                    }
                    uhfTagReadWrite.writeData(tagData.getDataToWrite(), isVirgin);
                    try {
                        Thread.sleep(200);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    String userData = uhfTagReadWrite.readUserData(LIMIT_READ_WRITE);

                    correctInfo = userData != null ? testWritedData(UHFTagReadWrite.hexToString(userData)) : false;
                }

                /*
                 *
                 * Si fue exitoso avisa al usuario
                 *
                 * */
                final boolean finalCorrectInfo = correctInfo;
                ((AppCompatActivity) mContext).runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        dialog.dismiss();
                        if (finalCorrectInfo)
                            mDetectListener.onTagWriteSuccess();
                        else mDetectListener.onTagWriteFailed(false);

                    }
                });
                mApi.close();
                closeSerial();

            }
        }).start();


    }

    private boolean memoryErasedSuccess(String userData) {
        int countZeros = 0;

        if (userData != null) {
            for (int i = 0; i < userData.length(); i++)
                if (userData.charAt(i) == '0')
                    countZeros++;
        }
        return countZeros >= 448;
    }

    private boolean testWritedData(String data) {
        try {
            AESencrp.decrypt(data);
        } catch (Exception e) {
            return false;
        }
        return true;
    }

    public void readTag(OnTagReadListener readListener, long timeout) {
        ReadingTagTask readingTagTask = new ReadingTagTask(mContext, mApi, mExecutor, readListener, timeout);
        readingTagTask.setStartTime(System.currentTimeMillis());
        mExecutor.execute(readingTagTask);
    }

    /**
     * @param tagData tag to write
     * @param flag    the flags or flags could it be "0000","0001","0011", etc.
     *                we will took the last position of tag memory, if the tag have 128 offsets the last
     *                position will be the flags, and close the r/w to that position less one (128-1=127).
     */
    public void writeFlag(final TagData tagData, final String flag, final OnTagWriteListener onTagWriteListener,final boolean manageApi ) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                ((AppCompatActivity) mContext).runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        dialog = ProgressDialog.show(mContext, "Escribiendo tag", "Escribiendo datos en tag...", true, true);
                    }
                });

                if (manageApi) {
                    openSerial();
                    mApi.open();
                }


                Tag tag = new Tag().setEPC(tagData.getEPC()).setAP(new Key(DEFAULT_AP));

                final UHFTagReadWrite uhfTagReadWrite = new UHFTagReadWrite(tag, mApi);

                if (!isVirgin(uhfTagReadWrite)) {

                    //String actualData = uhfTagReadWrite.readUserData((short)127,(short)1);
                    tag.setAP(new Key());


                    while (!uhfTagReadWrite.readUserData((short) 127, (short) 1).equals(flag))
                        uhfTagReadWrite.writeToUserData(flag, (short) 127, (short) 1);
                    ((AppCompatActivity) mContext).runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            dialog.dismiss();
                            onTagWriteListener.onTagWriteSuccess();
                            if (manageApi) {
                                mApi.close();
                                closeSerial();
                            }
                        }
                    });
                } else {
                    ((AppCompatActivity) mContext).runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            dialog.dismiss();
                            onTagWriteListener.onTagWriteFailed(true);
                            if (manageApi) {
                                mApi.close();
                                closeSerial();
                            }
                        }
                    });
                }
            }
        }).start();
    }

    /**
     * @param uhfTagReadWrite the tag write class
     * @return if is the tag is virgin
     */
    public boolean isVirgin(UHFTagReadWrite uhfTagReadWrite) {
        /*
         * Si no la lee con la contraseña default cambia a la establecida
         * (ya se sabe que el tag no es virgen)
         * */
        String ap = null;

        while (ap == null) ap = uhfTagReadWrite.readAP();

        /*
         *
         * Si la ap no es 09 y la ap es la default entonces es virgen
         *
         * */
        return !ap.equals(ERROR_CODE) && ap.equals(new Key(CODE).key);

    }

    /**
     * @param tagData the data to write
     * @param onTagWriteListener the listener
     * @param manageApi if we gonna manage the api or not
     */
    public void writeOnTag(final TagData tagData, final OnTagWriteListener onTagWriteListener, final boolean manageApi) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                final long startTime = System.currentTimeMillis();
                ((AppCompatActivity) mContext).runOnUiThread(new Runnable() {
                    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
                    @Override
                    public void run() {
                        progressBar = UIHelper.createDialogWithBar(mContext);// ProgressDialog.show(mContext, "Escribiendo tag", "Escribiendo datos en tag...", true, false);
                        progressBar.getAlertDialog().show();
                        progressBar.getAlertDialog().setCancelable(false);

                    }
                });

                if (manageApi) {
                    openSerial();
                    mApi.open();
                }

                Tag tag = new Tag().setEPC(tagData.getEPC()).setAP(new Key(CODE));

                final UHFTagReadWrite uhfTagReadWrite = new UHFTagReadWrite(tag, mApi);

                boolean isVirgin = isVirgin(uhfTagReadWrite);

                if (!isVirgin) {
                    if (!canOverwrite()) {
                        ((AppCompatActivity) mContext).runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                progressBar.getAlertDialog().cancel();
                                if (manageApi) {
                                    mApi.close();
                                    closeSerial();
                                }
                                onTagWriteListener.onTagWriteFailed(true);
                            }
                        });
                        return;
                    }

                    /*
                     * Si no es virgen cambia por la nueva
                     * */
                    tag.setAP(new Key());

                }

                /*
                 * muestra si es virgen al usuario
                 * */
                final boolean finalIsVirgin = isVirgin;


                ((AppCompatActivity) mContext).runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (finalIsVirgin)
                            progressBar.getTextView().setText("Tag nuevo detectado, configurando...");
                        else progressBar.getTextView().setText("Tag configurado detectado.");
                    }
                });

                /*
                 * Si no es virgen aseguralo con pass
                 * */
                if (isVirgin)
                    uhfTagReadWrite.secureTag();

                /*
                 * Parametros de configuración
                 * */
                final int maxLength = 127;
                final short length = 1;
                final int amountOfCharByOffset = 4;

                /*
                 * Lista de sectores escritos o su primer intento
                 * */
                final ArrayList<Sector> writedSector = new ArrayList<>();

                /*
                 * obten los datos por escribir
                 * */
                final String[] dataToWrite = partDataInSectors(tagData.getDataToWrite(), amountOfCharByOffset, maxLength);

                /*
                 * Recorre cada sector e intenta escribir
                 * */
                progressBar.getProgressBar().setProgress(0);
                for (short i = 0; i < dataToWrite.length; i++) {

                    final short finalI = i;
                    ((AppCompatActivity) mContext).runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            progressBar.getProgressTextView().setText((finalI *100/dataToWrite.length)+"%");
                            progressBar.getTextView().setText(
                                    "Escribiendo tag..." +
                                           // "\n\nSectores escritos exitosamente: " + getSectorsByStatus(writedSector, true).size() + " de " + dataToWrite.length +
                                            //"\n\nSectores erroneos: " + getSectorsByStatus(writedSector, false).size() +
                                            "\n\n" +
                                            "Tiempo transcurrido " + ((System.currentTimeMillis() - startTime) / 1000) + " segundos.");
                        }
                    });

                    progressBar.getProgressBar().setProgress(i*100/dataToWrite.length);

                    Sector sector = new Sector()
                            .setData(dataToWrite[i])
                            .setLength(length)
                            .setOffset(i);

                    sector.setSuccessWriting(uhfTagReadWrite.writeToUserData(sector));

                    writedSector.add(sector);
                }
                /*
                 * Obtiene los sectores fallados
                 * */
                ArrayList<Sector> failedSectors = getSectorsByStatus(writedSector, false);

                /*Si hay sectores dañados reintenta*/
                if (!failedSectors.isEmpty()) {

                    /*
                     * Mientras la cantidad de sectores fallados sean distintos a cero sigue tratando de escribirlos
                     * */
                    boolean retry = false;
                    int retries = 1;

                    while (!retry) {
                        failedSectors = retryWriteSectors(failedSectors, uhfTagReadWrite, startTime, retries);
                        retry = failedSectors.isEmpty();
                        retries++;
                    }
                }


                ((AppCompatActivity) mContext).runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        progressBar.getAlertDialog().cancel();
                        if (manageApi) {
                            mApi.close();
                            closeSerial();
                        }
                        onTagWriteListener.onTagWriteSuccess();
                    }
                });
            }
        }).

                start();


    }

    private ArrayList<Sector> retryWriteSectors(final ArrayList<Sector> failedSectors, UHFTagReadWrite uhfTagReadWrite, final long startTime, final int retries) {

        final ArrayList<Sector> writedSector = new ArrayList<>();
        int i = 0;

        progressBar.getProgressBar().setProgress(0);

        for (final Sector failedSector : failedSectors) {
            final int finalI = i;
            ((AppCompatActivity) mContext).runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    progressBar.getProgressBar().setProgress(finalI *100/failedSectors.size());
                    progressBar.getProgressTextView().setText((finalI *100/failedSectors.size())+"%");
                    progressBar.getTextView().setText("Reintentando escribir sectores erroneos" +
                            "\n\nReintento " + retries +
                            //"\n\nSectores escritos exitosamente: " + getSectorsByStatus(writedSector, true).size() + " de " + failedSectors.size() +
                            //"\n\nSectores erroneos: " + getSectorsByStatus(writedSector, false).size() +
                            "\nTiempo transcurrido: " +
                            "" + ((System.currentTimeMillis() - startTime) / 1000) + " segundos." +
                            "");
                }
            });

            failedSector.setSuccessWriting(uhfTagReadWrite.writeToUserData(failedSector));

            writedSector.add(failedSector);

            i++;

        }
        return getSectorsByStatus(failedSectors, false);
    }

    private ArrayList<Sector> getSectorsByStatus(ArrayList<Sector> writedSector,
                                                 boolean writed) {
        ArrayList<Sector> sectors = new ArrayList<>();
        for (Sector sector : writedSector)
            if (sector.isSuccessWriting() == writed)
                sectors.add(sector);
        return sectors;
    }

    /**
     * @param data the data to part
     * @return
     */
    private String[] partDataInSectors(String data, int partTo, int sizeOfTag) {
        if (data != null) {
            //Stores the length of the string
            int dataLength = data.length();
            //n determines the variable that divide the string in 'n' equal parts
            int parts = dataLength / partTo;

            while (dataLength % parts != 0) {
                data += "0";
                dataLength = data.length();
            }

            int counter = 0, amountOfChars = dataLength / parts;
            //Stores the array of string
            String[] newData = new String[sizeOfTag];
            //Check whether a string can be divided into n equal parts

            for (int i = 0; i < dataLength; i += amountOfChars) {
                //Dividing string in n equal part using substring()
                String part = data.substring(i, i + amountOfChars);
                newData[counter] = part;
                counter++;
            }
            String fillZeros = "";
            for (int i = 0; i < partTo; i++)
                fillZeros += "0";

            for (int i = counter; i < sizeOfTag; i++)
                newData[i] = fillZeros;

            return newData;
        }
        return null;

    }

}
