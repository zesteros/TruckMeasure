package com.mx.vise.cubicaciones.activities;

import android.content.Intent;
import android.graphics.BitmapFactory;
import android.graphics.PorterDuff;
import android.os.AsyncTask;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.mx.vise.androiduihelper.UIHelper;
import com.mx.vise.camerahelper.util.ImageHelper;
import com.mx.vise.cubicaciones.R;
import com.mx.vise.camerahelper.activities.PhotoActivity;
import com.mx.vise.cubicaciones.entities.FlowObject;
import com.mx.vise.cubicaciones.singleton.Singleton;

import org.openalpr.OpenALPR;
import org.openalpr.model.Results;
import org.openalpr.model.ResultsError;

import java.io.File;

import static com.mx.vise.cubicaciones.activities.MainActivity.FLOW_OBJECT_EXTRA;


public class CaptureLicensePlateActivity extends PhotoActivity implements View.OnClickListener, TextWatcher {

    private static final String TAG = "VISE";
    /*
     * Directorio de los datos del LPR
     * */
    private static String ANDROID_DATA_DIR;
    private Button mNextButton, mTakePhotoButton;
    private EditText mLicencePlateEditText;
    private ImageView mTruckImageView;


    private TextView mMessageTextView;
    private boolean mCleared;
    private boolean mIsLicencePlateCorrect;

    private FlowObject mFlowObject;
    /*
     * Variables para guardar las placas y rutas de las fotos
     * */
    private String mRearPhotoPath;
    private String mFrontPhotoPath;
    private String mRearPhotoManual;
    private String mFrontPlateManual;

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_capture_licence_plate);

        mNextButton = findViewById(R.id.nextButtonLicencePlate);

        mLicencePlateEditText = findViewById(R.id.lpEditText);

        mTruckImageView = findViewById(R.id.truckImageView);

        mMessageTextView = findViewById(R.id.lpMessageTextView);

        ANDROID_DATA_DIR = this.getApplicationInfo().dataDir;




        /*
         *
         * Obtiene los datos del intent (el intent puede ser la actividad de BuildingDataActivity/datos de actividad
         * o la misma actividad que se llama a si misma)
         *
         * */
        Bundle extras = getIntent().getExtras();

        if (extras != null) {

            mFlowObject = (FlowObject) extras.get(FLOW_OBJECT_EXTRA);
            /*
             * ¿es la placa trasera?
             * */
            if (mFlowObject.isRearLicencePlate())
                /*
                 * Si si entonces dibuja el icono de la placa trasera
                 * */
                changeUI(R.string.rear, R.drawable.ic_rear_lp);
            else {
                /*
                 * Si no entonces dibuja el icono del camion de la placa delantera
                 * además obtiene los datos de la placa trasera
                 * (OCR, ruta de la imagen y placa escrita manualmente)
                 * */
                changeUI(R.string.front, R.drawable.ic_front_lp);
            }
        }
        mTakePhotoButton = findViewById(R.id.takePhotoButton);
        setupCameraPhoto();

        mNextButton.setOnClickListener(this);
        mTakePhotoButton.setOnClickListener(this);
        mLicencePlateEditText.addTextChangedListener(this);
    }

    /**
     * @param title    the title to change
     * @param drawable the icon to change
     */
    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public void changeUI(int title, int drawable) {
        getSupportActionBar().setTitle(
                getString(R.string.capture_licence_plate)
                        .replace("%", getString(title))
        );
        mLicencePlateEditText.setHint(
                getString(R.string.write_licence_plate).replace("%", getString(title))
        );
        mTruckImageView.setImageDrawable(getDrawable(drawable));

        mTruckImageView.animate().scaleX(0.95f).scaleY(0.95f).setDuration(1100).start();
        mTruckImageView.animate().scaleX(1.5f).scaleY(1.5f).setDuration(1100).start();
    }

    /**
     * view
     *
     * @param v
     */
    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    public void onClick(View v) {
        /*
         * Controla los eventos del boton de siguiente/tomar foto
         * */
        if (v.getId() == R.id.takePhotoButton) {
            /*
             * Si la licencia es correcta si tiene más de 5 caracteres
             * */
            if (mIsLicencePlateCorrect)
                /*
                 * Lanza la actividad de la cámara
                 * */
                launchCameraActivity();
            else {
                /*
                 * Si no, envia mensaje de error
                 * */
                UIHelper.showSnackbar(this, getString(R.string.introduce_a_correct_licence_plate));
                mLicencePlateEditText.getBackground().mutate().setColorFilter(this.getResources().getColor(com.mx.vise.androiduihelper.R.color.red), PorterDuff.Mode.SRC_ATOP);

            }
        } else {
            /*
             * Si no es el boton de tomar foto entonces es el de siguiente, si dice
             * siguiente termina el proceso, si no entonces la actividad se llama a si misma
             * para reutilizar los componentes y capturar la siguiente placa
             * */
            if (mNextButton.getText().toString().equals(getString(R.string.next_licence_plate))) {

                /*
                 * Obtiene la placa escrita
                 * */
                mRearPhotoManual = mLicencePlateEditText.getText().toString();
                /*
                 * Reenvia los datos a esta misma actividad (porque las instancias se eliminan)
                 * */
                Intent intent = new Intent(this, CaptureLicensePlateActivity.class);

                mFlowObject.setRearPlatePhotoPath(mRearPhotoPath);
                mFlowObject.setRearPlateWritedManual(mRearPhotoManual);
                mFlowObject.isRearLicencePlate(false);
                /*
                 * Envia ahora que ya no sera la placa trasera
                 * */
                intent.putExtra(FLOW_OBJECT_EXTRA, mFlowObject);

                startActivity(intent);
                /*
                 * Termina actividad
                 * */
                finish();
            }
            /*
             * Si la actividad es la de siguiente (que ya se capturaron las dos placas)
             * entonces envia a la siguiente actividad con los datos de la placa trasera mas los
             * de la placa delantera
             * */
            else if (mNextButton.getText().toString().equals(getString(R.string.next))) {

                mFrontPlateManual = mLicencePlateEditText.getText().toString();

                Intent intent = new Intent(this, CaptureBrandAndColorsActivity.class);

                mFlowObject.setFrontPlatePhotoPath(mFrontPhotoPath);
                mFlowObject.setFrontPlateWritedManual(mFrontPlateManual);

                intent.putExtra(FLOW_OBJECT_EXTRA, mFlowObject);

                startActivity(intent);
                finish();
            }
        }
    }


    /**
     * @param photoPath la ruta de la foto
     */
    @Override
    public void onImageCaptured(String photoPath) {
        /*
         * Si la foto esta vacia (pesa 0) entonces no se tomo nada y regresa.
         * */
        if (!ImageHelper.imageExists(photoPath)) return;

        /*
         * Procesa en segundo plano el OCR
         * */
        processOCR(photoPath, mFlowObject.isRearLicencePlate());
        /*
         * Conmuta la interfaz y la variable de ruta de la foto conforme a si
         * es frontal o trasera
         * */
        if (mFlowObject.isRearLicencePlate()) {
            mRearPhotoPath = photoPath;
            mTakePhotoButton.setVisibility(View.INVISIBLE);
            mNextButton.setText(R.string.next_licence_plate);
            mNextButton.setVisibility(View.VISIBLE);

        } else {
            mFrontPhotoPath = photoPath;
            mTakePhotoButton.setVisibility(View.INVISIBLE);
            mNextButton.setText(R.string.next);
            mNextButton.setVisibility(View.VISIBLE);
        }
    }

    /**
     * @param path
     * @param isRear
     */
    public void processOCR(final String path, final boolean isRear) {
        // final ProgressDialog progress = ProgressDialog.show(this, "Cargando", "Procesando imagen", true);
        final String openAlprConfFile = ANDROID_DATA_DIR + File.separatorChar + "runtime_data" + File.separatorChar + "openalpr.conf";
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inSampleSize = 10;

        AsyncTask.execute(new Runnable() {
            @Override
            public void run() {
                final String result = OpenALPR.Factory.create(CaptureLicensePlateActivity.this, ANDROID_DATA_DIR).recognizeWithCountryRegionNConfig("us", "", path, openAlprConfFile, 10);

                Log.d("OPEN ALPR", result);
                try {
                    final Results results = new Gson().fromJson(result, Results.class);
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            if (results == null || results.getResults() == null || results.getResults().size() == 0) {
                                Log.i(TAG, "run: NO SE PUDO PROCESAR LA IMAGEN");
                            } else {
                                //new UIHelper(CaptureLicensePlateActivity.this).showSnackbar("Imagen procesada, estatus A");
                                String plate = results.getResults().get(0).getPlate();
                                Log.i(TAG, "run: OCR IMAGEN PROCESADA ES TRASERA?" + mFlowObject.isRearLicencePlate() + ", valor: " + plate);
                                //mLicencePlateEditText.setText(plate);
                                Log.i(TAG, "run processocr: " + plate);
                                if (isRear) {
                                    Singleton.getInstance().setRearLicencePlateByOCR(plate);
                                } else {
                                    Singleton.getInstance().setFrontLicencePlateByOCR(plate);
                                }
                            }
                        }
                    });

                } catch (JsonSyntaxException exception) {
                    final ResultsError resultsError = new Gson().fromJson(result, ResultsError.class);
                    Log.i(TAG, "run: error" + resultsError.getMsg());
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {

                            //resultTextView.setText(resultsError.getMsg());
                        }
                    });
                }
                //progress.dismiss();
            }
        });
    }

    /**
     * @param s
     * @param start
     * @param count
     * @param after
     */
    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

    }

    /**
     * Método para controlar los eventos de la caja de texto de las placas
     *
     * @param s
     * @param start
     * @param before
     * @param count
     */
    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {
        if (s.length() == 0) {
            mIsLicencePlateCorrect = false;
            mMessageTextView.setText("Introduce una placa válida");
            mMessageTextView.setVisibility(View.VISIBLE);

        } else if (s.length() < 8 || s.length() > 9) {
            mIsLicencePlateCorrect = false;
            mMessageTextView.setText("Las placas usualmente tienen 6 o 7 carácteres.");
            mMessageTextView.setVisibility(View.VISIBLE);
        } else {
            mIsLicencePlateCorrect = true;
            mMessageTextView.setVisibility(View.INVISIBLE);
        }
    }

    private static final char space = '-';

    /**
     * @param s
     */
    @Override
    public void afterTextChanged(Editable s) {
        // Remove spacing char
        if (s.length() == 1) mCleared = false;
        if (s.length() > 9)
            s.delete(s.length() - 1, s.length());
        else if (s.length() == 9 && !mCleared) {
            String cleanString = s.toString().replace("-", "");
            StringBuilder stringBuilder = new StringBuilder(cleanString);
            stringBuilder.insert(2, "-");
            stringBuilder.insert(5, "-");
            mCleared = true;
            s.clear();
            s.append(stringBuilder.toString());

        }
        // Insert char where needed.

        if (s.length() > 0) {
            if (s.length() == 4 || s.length() == 7) {
                // Only if its a digit where there should be a space we insert a space
                char c1 = s.charAt(s.length() - 1);
                char c2 = s.charAt(s.length() - 2);
                if (c1 != space && c2 != space)
                    s.insert(s.length() - 1, String.valueOf(space));
            }

        }

    }

}
