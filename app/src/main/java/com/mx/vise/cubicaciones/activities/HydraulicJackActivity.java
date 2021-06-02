package com.mx.vise.cubicaciones.activities;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.PorterDuff;
import android.os.AsyncTask;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.google.gson.Gson;
import com.mx.vise.androiduihelper.UIHelper;
import com.mx.vise.camerahelper.util.ImageHelper;
import com.mx.vise.cubicaciones.R;
import com.mx.vise.cubicaciones.dao.DAO;
import com.mx.vise.cubicaciones.entities.FlowObject;
import com.mx.vise.cubicaciones.entities.PrintObject;
import com.mx.vise.cubicaciones.entities.Signature;
import com.mx.vise.cubicaciones.pojos.BrandPOJO;
import com.mx.vise.cubicaciones.settings.SettingsActivity;
import com.mx.vise.cubicaciones.singleton.Singleton;
import com.mx.vise.cubicaciones.tasks.OnPrintListener;
import com.mx.vise.cubicaciones.tasks.PrintTask;
import com.mx.vise.cubicaciones.util.ColorUtils;
import com.mx.vise.cubicaciones.util.PrintStatus;
import com.mx.vise.cubicaciones.util.PrintType;
import com.mx.vise.cubicaciones.util.PrinterHelper;
import com.mx.vise.cubicaciones.util.VolumeCalculator;
import com.mx.vise.cubicaciones.util.exceptions.TicketIndexOutOfBoundsException;
import com.mx.vise.login.pojos.EmployeePojo;
import com.mx.vise.uhf.UHFHelper;
import com.mx.vise.uhf.entities.Tag;
import com.mx.vise.uhf.interfaces.OnTagWriteInfo;
import com.mx.vise.uhf.interfaces.OnTagWriteListener;
import com.mx.vise.uhf.interfaces.OnUHFDetectListener;
import com.mx.vise.uhf.tag.TagData;

import java.io.File;
import java.math.BigDecimal;
import java.util.ArrayList;

import static com.mx.vise.cubicaciones.activities.MainActivity.FLOW_OBJECT_EXTRA;
import static com.mx.vise.cubicaciones.activities.SignatureActivity.SIGNATURE_BITMAP_EXTRA;
import static com.mx.vise.uhf.UHFTagReadWrite.ERROR_CODE;

public class HydraulicJackActivity extends AppCompatActivity implements OnPrintListener, OnUHFDetectListener {

    private static final String TAG = "VISE";
    public static final int SIGNATURE_REQUEST_CODE = 100;
    private FlowObject mFlowObject;
    private EditText[] mAdvancedMeasuresEditTexts;

    private EditText mAdjustmentEditText, mRemarksEditText;
    private Button mAdjustmentButton, mAddSignatureButton, mNextButton;
    private TextView mAdjustmentTextView;
    private boolean mSignatureCaptured;
    private Bitmap mSignatureBitmap;
    private float[] mHydraulicJackDimensions;
    private VolumeCalculator mVolumeCalculator;
    private TextView mSubtitleTextView;
    private Button mWriteTagButton;
    private String mEpc;
    private UHFHelper mUHFHelper;
    private boolean mTagGenerated;
    private String mTID;
    private Button mPrintButton;

    //private Button mAdjustmentButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_hydraulic_jack);

        Bundle extras = getIntent().getExtras();

        if (extras != null) {
            mFlowObject = (FlowObject) extras.get(FLOW_OBJECT_EXTRA);
            Log.i(TAG, "onCreate: FLOWOBJECT: " + new Gson().toJson(mFlowObject));

        } else {
            mFlowObject = new FlowObject();
            mFlowObject.setSelectedUnitType("OLLA CONCRETO");
            mFlowObject.setSelectedBuilding("OBRAS-1634 - BACHEO QRO SAN LUIS (Ing. Abelardo)");
            mFlowObject.setFrontPlateManual("GTW-81-21");
            mFlowObject.setRearPlateWritedManual("TEST-10");
            BrandPOJO brandPOJO = new BrandPOJO();
            brandPOJO.setName("INTERNATIONAL");
            mFlowObject.setSelectedBrand(brandPOJO);
            mFlowObject.setSelectedBoxColor("#FF731A");
            mFlowObject.setSelectedTractorColor("#ABC512");
            mFlowObject.setIncrease(3.71f);
            mFlowObject.hasDimensions(true);
            if (mFlowObject.hasDimensions()) {
                float[] dimen = new float[]{2.38f, 3.37f, 2.21f, 0, 0, 0, 2.12f};

                mFlowObject.setBasicMeasures(dimen);
            }


            mFlowObject.setCirculationCardInserted("TA62819AHSA81AS");
            EmployeePojo employeePojo = new EmployeePojo();
            employeePojo.setEmployeeName("Angelo de Jesus Loza Martinez");
            mFlowObject.setSession(employeePojo);
            mFlowObject.setWritedOperator("Juan Jesus Perez Oliva");

        }


        int[] ids = getMeasuresEditTextIds();
        mAdvancedMeasuresEditTexts = new EditText[ids.length];

        mAddSignatureButton = findViewById(R.id.addSignatureButton);
        mAdjustmentEditText = findViewById(R.id.adjustmentEditText);
        mAdjustmentButton = findViewById(R.id.adjustmentButton);
        mAdjustmentTextView = findViewById(R.id.adjustmentLabel);
        mSubtitleTextView = findViewById(R.id.subtitleHydraulicJack);
        mWriteTagButton = findViewById(R.id.readTagButton);
        mRemarksEditText = findViewById(R.id.remarksEditText);
        mNextButton = findViewById(R.id.nextHydraulicJackButton);
        mPrintButton = findViewById(R.id.printButton);


        for (int i = 0; i < ids.length; i++)
            mAdvancedMeasuresEditTexts[i] = findViewById(ids[i]);

        if (!mFlowObject.hasDimensions()) {
            for (int i = 1; i < ids.length; i++)
                mAdvancedMeasuresEditTexts[i].setVisibility(View.GONE);
            mAdvancedMeasuresEditTexts[0].setHint(R.string.set_volume);
            mSubtitleTextView.setText(R.string.volume);

        }

//        if (mFlowObject != null) {
//            mAdjustmentEditText.setVisibility(View.GONE);
//            mAdjustmentTextView.setVisibility(View.GONE);
//            mAdjustmentButton.setVisibility(View.GONE);
//        }

        mVolumeCalculator = new VolumeCalculator();


    }

    public void adjustmentButtonOnClick(View v) {
        if (mAdjustmentButton.getText().toString().equals("+")) {
            mAdjustmentButton.getBackground().mutate().setColorFilter(this.getResources().getColor(R.color.red), PorterDuff.Mode.SRC_ATOP);
            mAdjustmentButton.setText("-");
        } else if (mAdjustmentButton.getText().toString().equals("-")) {
            mAdjustmentButton.getBackground().mutate().setColorFilter(this.getResources().getColor(R.color.green), PorterDuff.Mode.SRC_ATOP);
            mAdjustmentButton.setText("+");
        }
    }

    public int[] getMeasuresEditTextIds() {
        return new int[]{
                R.id.lJackEditText,
                R.id.aJackEditText,
                R.id.hJackEditText
        };
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (requestCode == SIGNATURE_REQUEST_CODE) {
            if (resultCode == Activity.RESULT_OK) {
                Signature result = (Signature) data.getSerializableExtra(SIGNATURE_BITMAP_EXTRA);
                byte[] bitmapdata = result.getSignature();
                Bitmap bitmap = BitmapFactory.decodeByteArray(bitmapdata, 0, bitmapdata.length);

                mSignatureBitmap = (Bitmap.createScaledBitmap(bitmap, 400, 150, false));

                mAddSignatureButton.setText(R.string.sign_captured);
                mAddSignatureButton.setBackgroundTintList(getResources().getColorStateList(R.color.green));
                mAddSignatureButton.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_ok, 0, 0, 0);
                mSignatureCaptured = true;
                //Log.i(TAG, "onActivityResult: "+);
            }
        }
    }//onActivityResult

    public void onCaptureSignatureButtonClick(View v) {
        Intent intent = new Intent(this, SignatureActivity.class);
        startActivityForResult(intent, SIGNATURE_REQUEST_CODE);
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    public void onPrintButtonClick(View v) {

        if (!validNumericFields()) return;

        new PrintTask(this, true)
                .setOnPrintListener(this)
                .execute(getPrintObjects());
    }

    public boolean validNumericFields() {

        mHydraulicJackDimensions = new float[mAdvancedMeasuresEditTexts.length];

        /*
         * Si se tienen dimensiones se asume que es gondola o torton
         *
         * */
        if (mFlowObject.hasDimensions()) {
            for (int i = 0; i < mAdvancedMeasuresEditTexts.length; i++) {
                if (!UIHelper.validateField(this, mAdvancedMeasuresEditTexts[i],
                        mAdvancedMeasuresEditTexts[i].getHint().toString())) {

                    mAdvancedMeasuresEditTexts[i].requestFocus();

                    for (int j = 0; j < mAdvancedMeasuresEditTexts.length; j++) {
                        if (j != i)
                            UIHelper.returnToNormalField(this, mAdvancedMeasuresEditTexts[j]);
                    }
                    return false;
                }
            }


            for (int i = 0; i < mAdvancedMeasuresEditTexts.length; i++) {
                try {
                    mHydraulicJackDimensions[i] =
                            Float.parseFloat(mAdvancedMeasuresEditTexts[i].getText().toString());
                } catch (NumberFormatException e) {
                    mHydraulicJackDimensions[i] = 0;
                    e.printStackTrace();
                }
            }

            mFlowObject.setTotalVolume(mVolumeCalculator.getVolumeTotal(mFlowObject.getBasicMeasures(), mHydraulicJackDimensions, getDiscount()));

        }
        /*
         *
         * Es pipa u olla concreto (valida solo el primero)
         *
         * */
        else {

            if (!UIHelper.validateField(this, mAdvancedMeasuresEditTexts[0],
                    mAdvancedMeasuresEditTexts[0].getHint().toString()))
                return false;
            try {
                mHydraulicJackDimensions[0] =
                        Float.parseFloat(mAdvancedMeasuresEditTexts[0].getText().toString());

                mFlowObject.setTotalVolume(mHydraulicJackDimensions[0]);
            } catch (Exception e) {
                e.printStackTrace();
            }

        }
        return true;
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    public void validateFields() {

        if (!validNumericFields()) return;

        if (!mSignatureCaptured) {
            UIHelper.showSnackbar(this,
                    getString(R.string.operator_sign_does_not_captured),
                    true);
            return;
        }

        if (!mPrintButton.getText().toString().equals(getString(R.string.ticket_printed))) {
            UIHelper.showSnackbar(this,
                    getString(R.string.ticket_doesnt_printed),
                    true);
            return;
        }
        /*if (!mTagGenerated) {
            UIHelper.showSnackbar(this,
                    getString(R.string.tag_doesnt_writed),
                    true);
            return;
        }*/

        String frontPlateOCR = Singleton.getInstance().getFrontLicencePlateByOCR();
        String rearPlateOCR = Singleton.getInstance().getRearLicencePlateByOCR();

        mFlowObject.setFrontPlateByOCR(frontPlateOCR);
        mFlowObject.setRearPlateByOCR(rearPlateOCR);


        File operatorSignature = ImageHelper.exportBitmapToExternalStorage(mSignatureBitmap);

        mFlowObject.setOperatorSignaturePath(operatorSignature.getAbsolutePath());

        Intent intent = new Intent(HydraulicJackActivity.this, FinishActivity.class);

        intent.putExtra(FLOW_OBJECT_EXTRA, mFlowObject);

        startActivity(intent);
        // else {
        //  if (mTagGenerated) {

//            Intent intent = new Intent(HydraulicJackActivity.this, TagActivity.class);
//        } else {

//            UIHelper.showSnackbar(this, "No se ha escrito el tag.", true);
//        }
        //}
    }

    /**
     * @return la lista de objetos a imprimir
     */
    public ArrayList<PrintObject> getPrintObjects() {
        /*
         * Se debe crear una lista de elementos que contendra el ticket
         * */
        ArrayList<PrintObject> objects = new ArrayList<>();


        /*Inserta el título del ticket*/
        PrintObject title = new PrintObject();
        title
                .setPrintType(PrintType.TITLE)
                .setContent("CUBICACIONES")
                .setX(60)//posición en x
                .setY(80);//posición en y
        objects.add(title);

        /*
         * Para las lineas solo decir que es linea y su largo
         * */
        try {
            objects.add(new PrintObject().setPrintType(PrintType.LINE).setLineLengthX(300));
        } catch (TicketIndexOutOfBoundsException e) {
            e.printStackTrace();
        }

        /*Separa el nombre de la obra del numero*/
        String buildingNumber =
                mFlowObject.getSelectedBuilding().split("-")[0] + "-" +
                        mFlowObject.getSelectedBuilding().split("-")[1];

        String buildingName = mFlowObject.getSelectedBuilding().split("-")[2];

        objects.add(new PrintObject().setTitle("Obra").setContent(buildingName).isDoubleLine(true).setDoubleLineX(-40));
        objects.add(new PrintObject().setTitle("No.obra").setContent(buildingNumber).setxSpace(120));
        objects.add(new PrintObject().setTitle("Tipo Camion").setContent(mFlowObject.getSelectedUnitType()).setxSpace(160));
        objects.add(new PrintObject().setTitle("Tarjeta circulacion").setContent(mFlowObject.getCirculationCardInserted()).isDoubleLine(true));
        objects.add(new PrintObject().setTitle("Placa trasera").setContent(mFlowObject.getRearPlateManual()));
        objects.add(new PrintObject().setTitle("Placa delantera").setContent(mFlowObject.getFrontPlateManual()));
        objects.add(new PrintObject().setTitle("Marca").setContent(mFlowObject.getSelectedBrand().getName()).setxSpace(80));
        objects.add(new PrintObject().setTitle("Color caja").setContent(
                PrinterHelper.stripAccents(ColorUtils.getColorNameFromHex(mFlowObject.getSelectedBoxColor()))).setxSpace(130)
        );
        objects.add(new PrintObject().setTitle("Color tracto").setContent(
                PrinterHelper.stripAccents(
                        ColorUtils.getColorNameFromHex(mFlowObject.getSelectedTractorColor()))
                ).setxSpace(130)
        );
        if (mFlowObject.hasDimensions()) {
            String box1 = mVolumeCalculator.getBoxVolumeForTicket(mFlowObject.getBasicMeasures(), 0);

            String box2 = mVolumeCalculator.getBoxVolumeForTicket(mFlowObject.getBasicMeasures(), 3);

            String hydraulicJack = mVolumeCalculator.getBoxVolumeForTicket(mHydraulicJackDimensions, 0);

            String curve = mVolumeCalculator.getCurveForTicket(mFlowObject.getBasicMeasures());

            String volumeTotal = mVolumeCalculator
                    .getVolumeTotalForTicket(
                            mFlowObject.getBasicMeasures(),
                            mHydraulicJackDimensions,
                            getDiscount()
                    );
            mFlowObject.setTotalVolume(mVolumeCalculator.getVolumeTotal(mFlowObject.getBasicMeasures(), mHydraulicJackDimensions, getDiscount()));
            mFlowObject.setAdjustment(getDiscount());
            mFlowObject.setHydraulicJackDimensions(mHydraulicJackDimensions);
            objects.add(
                    new PrintObject().setTitle("Caja 1")
                            .setContent(box1)
                            .isDoubleLine(true)
                            .setDoubleLineX(-45)
            );
            objects.add(new PrintObject().setTitle("Caja 2").setContent(box2).isDoubleLine(true).setDoubleLineX(-45));
            objects.add(new PrintObject().setTitle("Gato").setContent(hydraulicJack).isDoubleLine(true).setDoubleLineX(-45));
            objects.add(new PrintObject().setTitle("Curva").setContent(curve).setxSpace(70));
            objects.add(new PrintObject().setTitle("Monten").setContent(String.format("%.2f", mFlowObject.getIncrease()) + " m3").setxSpace(90));
            objects.add(new PrintObject().setTitle("Ajuste").setContent(String.format("%.2f", getDiscount()) + " m3").setxSpace(80));
            objects.add(new PrintObject().setTitle("Volumen registrado").setContent(volumeTotal).setxSpace(220));
        } else {
            objects.add(new PrintObject().setTitle("Volumen registrado").setContent(String.format("%.2f", mHydraulicJackDimensions[0]) + " m3").setxSpace(220));
        }
        objects.add(new PrintObject().setTitle("Tag ID").setContent(mTID).isDoubleLine(true));
        objects.add(new PrintObject().setTitle("Usuario").setContent(mFlowObject.getSession().getEmployeeName()).isDoubleLine(true).setDoubleLineX(-25));
        objects.add(new PrintObject().setTitle("Operador").setContent(mFlowObject.getWritedOperator()).isDoubleLine(true).setDoubleLineX(-25));
        try {
            objects.add(new PrintObject().setPrintType(PrintType.LINE).setLineLengthX(300));
        } catch (TicketIndexOutOfBoundsException e) {
            e.printStackTrace();
        }
        objects.add(new PrintObject().setTitle("Firma de operador").setContent(""));
        /*if (mSignatureBitmap != null) {
            objects.add(new PrintObject().setPrintType(PrintType.IMAGE).setImage(mSignatureBitmap));
        }*/
        try {
            objects.add(new PrintObject().setPrintType(PrintType.BOX).setLineLengthX(310).setLineLengthY(100));
        } catch (TicketIndexOutOfBoundsException e) {
            e.printStackTrace();
        }


        mFlowObject.setObservations(mRemarksEditText.getText().toString());

        return objects;

    }

    public float getDiscount() {
        float discount = 0;
        try {
            float sign = mAdjustmentButton.getText().toString().equals("-") ? -1 : 1;
            discount = Float.parseFloat(mAdjustmentEditText.getText().toString()) * sign;
        } catch (NumberFormatException e) {
            e.printStackTrace();
        }
        return discount;
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    public void onNextButtonClick(View v) {
        validateFields();

    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public void changeStateOfButton(Button button, String text) {
        button.setBackgroundTintList(getResources().getColorStateList(R.color.green));
        button.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_ok, 0, 0, 0);
        button.setText(text);
    }

    /**
     * Cuando la impresión ha sido exitosa
     */
    @Override
    public void onPrintSuccess() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder
                .setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
                    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        changeStateOfButton(mPrintButton, getString(R.string.ticket_printed));
                    }
                })
                .setMessage(R.string.information_is_correct)
                .setNegativeButton(R.string.print_again, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        new PrintTask(HydraulicJackActivity.this, true)
                                .setOnPrintListener(HydraulicJackActivity.this)
                                .execute(getPrintObjects());
                    }
                })
                .show();
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    public void onPrintFailed(PrintStatus status) {
        switch (status) {
            case MAC_DOES_NOT_DECLARED:
                UIHelper.showSnackbar(this, getString(R.string.printer_does_not_configured), true);
                break;
            case CAN_NOT_CONNECT_TO_ESTABLISHED_PRINTER:
                UIHelper.showSnackbar(this, getString(R.string.cant_connect_to_printer), true);
                break;
        }
    }

    /**
     * Round to certain number of decimals
     *
     * @param d
     * @param decimalPlace
     * @return
     */
    public static float round(float d, int decimalPlace) {
        BigDecimal bd = new BigDecimal(Float.toString(d));
        bd = bd.setScale(decimalPlace, BigDecimal.ROUND_HALF_UP);
        return bd.floatValue();
    }

    /**
     * @param view la vista a la cual se dio click
     */
    public void onWriteTagButtonClick(View view) {
        /*
         * Se basa en determinar si leer o escribir el tag en el texto que tenga el botón
         *
         * Si el botón dice leer
         * */
        if (mWriteTagButton.getText().toString().equals(getString(R.string.read_tag))) {
            /*
             * Comienza la lectura (cuando termine llama a onReadTID)
             * */
            mUHFHelper = new UHFHelper(this, this, true);
            mUHFHelper.startReading();
            /*
             * Si tiene el texto de escribir TAG procede a escribir
             * */
        } else if (mWriteTagButton.getText().toString().equals(getString(R.string.write_tag))) {

            validNumericFields();

            mUHFHelper = new UHFHelper(this, this, false);

            mUHFHelper.canOverwrite(false);
            /*
             * Establece el texto que se escribirá en el tag. El angelo
             * */
            TagData tagData = new TagData(new OnTagWriteInfo() {
                @Override
                public String getDataToWrite() {
                    return "|" + mFlowObject.getRearPlateManual().replace("-", "") + "|" + round(mFlowObject.getTotalVolume(), 2) + "|" + round(mFlowObject.getIncrease(), 2) + "|";
                }
            });
            /*
             * Si el Electronic Product Code se ha leído satisfactoriamente entonces escribe el dato en el tag
             * (Estableciendo el EPC correspondiente para poder leer, segun las reglas del ISO)
             * */
            if (mEpc != null) {
                tagData.setEPC(mEpc);
                mUHFHelper.writeOnTag(tagData, new OnTagWriteListener() {
                    @RequiresApi(api = Build.VERSION_CODES.M)
                    @Override
                    public void onTagWriteSuccess() {
                        UIHelper.showSnackbar(HydraulicJackActivity.this, getString(R.string.tag_successfully_writed), false);
                        /*
                         * Cambia de color
                         * */
                        mWriteTagButton.setBackgroundTintList(getResources().getColorStateList(R.color.green));
                        /*
                         * Cambia el texto del botón
                         * */
                        mWriteTagButton.setText("Tag generado");
                        /*Icono*/
                        mWriteTagButton.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_ok, 0, 0, 0);
                        /*Centinela*/
                        mTagGenerated = true;
                    }

                    @RequiresApi(api = Build.VERSION_CODES.M)
                    @Override
                    public void onTagWriteFailed(boolean overwriteIntent) {
                        /*Reestablece la centinela*/
                        mTagGenerated = false;
                        /*
                         * Reestablece el texto del botón con LEER TAG
                         * */
                        mWriteTagButton.setText(R.string.read_tag);

                        if (overwriteIntent) {
                            UIHelper.showSnackbar(HydraulicJackActivity.this,
                                    "Lo sentimos, el tag ya contiene datos",
                                    true);
                        }
                    }

                    @RequiresApi(api = Build.VERSION_CODES.M)
                    @Override
                    public void onTagWriteTimeout() {
                        mTagGenerated = false;
                        UIHelper.showSnackbar(HydraulicJackActivity.this, getString(R.string.write_tag_timeout), true);
                        mWriteTagButton.setText(R.string.read_tag);
                    }
                }, true);
            }

        }
    }

    /**
     * @param tids    los tids leídos
     * @param isValid si es valida alguno de la lista de tags con los leídos (no se comparan tags)
     */
    @Override
    public void onUHFSDetected(ArrayList<String> tids, boolean isValid) {

    }

    /**
     * Método para saber si la escritura fue exitosa
     */
    @TargetApi(Build.VERSION_CODES.M)
    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    public void onTagWriteSuccess() {
        /*Si fue exitosa entra aquí y muestra al usuario*/

    }

    /**
     * @param overwriteIntent si la escritura ha fallado por medio de una reescritura
     */
    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    public void onTagWriteFailed(boolean overwriteIntent) {

    }


    /**
     * Llama a este método cuando se haya leído un tag
     *
     * @param tag el tag que se ha leído
     */
    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    public void onTagRead(final Tag tag) {
        /*Si es distinto a nulo*/
        if (tag != null) {
            /*Si el EPC es distinto a nulo*/
            if (tag.getEpc() != null) {
                mEpc = tag.getEpc();
            }
            /*
             * Si el TID es dintinto a nulo
             * */
            if (tag.getTid() != null) {
                /*
                 * Si la lectura del TID da el código de error (09) entonces
                 * el tag ya tiene datos o contraseña establecida u otro error
                 * entonces se dice que se dio un error al leer el tag.
                 * */
                if (!tag.getTid().equals(ERROR_CODE)) {
                    /*
                     *
                     * Si no da error entonces corre la tarea de validación
                     * */
                    new AsyncTask<Void, Void, Boolean>() {
                        private ProgressDialog progressDialog;

                        @Override
                        protected void onPreExecute() {
                            super.onPreExecute();
                            /*Muestra diálogo*/
                            progressDialog = ProgressDialog.show(HydraulicJackActivity.this,
                                    getString(R.string.tag_validation), getString(R.string.validating_tag),
                                    true, false);
                        }

                        @Override
                        protected Boolean doInBackground(Void... voids) {
                            /*Regresa si el tag existe en la base de datos interna*/
                            return DAO.tagExists(HydraulicJackActivity.this, tag.getTid());
                        }


                        @Override
                        protected void onPostExecute(Boolean tagExists) {
                            /*Quita el dialogo*/
                            progressDialog.dismiss();
                            /*Si no existe el tag muestra mensaje*/
                            if (!tagExists) {
                                UIHelper.showSnackbar(HydraulicJackActivity.this,
                                        getString(R.string.tag_is_not_valid), true);
                                mTID = null;
                                mTagGenerated = false;
                            }
                            /*Si existe entonces inicializa la variable de TID global de la actividad
                             * Muestra al usuario mediante cambios en la interfaz a verde.*/
                            else {

                                mTID = tag.getTid();
                                mFlowObject.setTag(tag.getTid());
                                UIHelper.showSnackbar(HydraulicJackActivity.this,
                                        getString(R.string.tag_with_id).replace("%", tag.getTid()), false);
                                mWriteTagButton.setText(R.string.write_tag);
                                mWriteTagButton
                                        .setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_signature, 0, 0, 0);
                            }
                        }
                    }.execute();
                } else {
                    UIHelper.showSnackbar(this, getString(R.string.error_happened_writing), true);
                }
            }
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    public void onTagWriteTimeout() {

    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    public void onTagReadTimeout() {
        mTagGenerated = false;
        UIHelper.showSnackbar(this, getString(R.string.write_tag_timeout), true);
        mWriteTagButton.setText(R.string.read_tag);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            Intent intent = new Intent(this, SettingsActivity.class);
            startActivity(intent);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
