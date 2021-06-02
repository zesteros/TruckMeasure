package com.mx.vise.cubicaciones.activities;

import android.content.Intent;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;

import com.google.gson.Gson;
import com.mx.vise.androiduihelper.UIHelper;
import com.mx.vise.camerahelper.activities.PhotoActivity;
import com.mx.vise.camerahelper.util.ImageHelper;
import com.mx.vise.cubicaciones.R;
import com.mx.vise.cubicaciones.entities.FlowObject;

import org.w3c.dom.Text;

import static com.mx.vise.cubicaciones.activities.MainActivity.FLOW_OBJECT_EXTRA;

public class CirculationCardActivity extends PhotoActivity implements View.OnClickListener {

    private static final String TAG = "VISE " + CirculationCardActivity.class.getName();
    private FlowObject mFlowObject;

    private EditText mCirculationCardEditText;
    private Button mTakeCardPhotoButton, mNextButton;
    private String mCirculationCardPhotoPath;
    private CheckBox mIncreaseCheckBox;
    private TextView mIncreaseTextView;
    private boolean mHasDimensions = true;
    private EditText mIncreaseEditText;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_circulation_card);

        getSupportActionBar().setTitle("Capturar unidad");

        Bundle extras = getIntent().getExtras();

        mCirculationCardEditText = findViewById(R.id.circulationCardEditText);
        mTakeCardPhotoButton = findViewById(R.id.takeCirculationCardPhotoButton);
        mNextButton = findViewById(R.id.nextButtonCirculationCard);
        mIncreaseCheckBox = findViewById(R.id.increaseCheckBox);
        mIncreaseTextView = findViewById(R.id.increaseLabel);
        mIncreaseEditText = findViewById(R.id.increaseEditText);

        /*
         * VALIDA AQUI SI ES PIPA U OLLA CONCRETO
         *
         * */
        if (extras != null) {
            mFlowObject = (FlowObject) extras.get(FLOW_OBJECT_EXTRA);
            if (
                    mFlowObject.getSelectedUnitType().equals(getString(R.string.pipe)) ||
                            mFlowObject.getSelectedUnitType().equals(getString(R.string.concrete))
                    ) {
                mIncreaseTextView.setVisibility(View.INVISIBLE);
                mIncreaseCheckBox.setVisibility(View.INVISIBLE);
                mIncreaseEditText.setVisibility(View.INVISIBLE);
                mHasDimensions = false;
            }
            //Gson gson = new Gson();
            //String json = gson.toJson(mFlowObject);
            //Log.i(TAG, "onCreate: " + json);
        }

        mTakeCardPhotoButton.setOnClickListener(this);
        mNextButton.setOnClickListener(this);

        setupCameraPhoto();
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.takeCirculationCardPhotoButton:
                launchCameraActivity();
                break;
            case R.id.nextButtonCirculationCard:
                validateFields();
                break;
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    private void validateFields() {
        if (!UIHelper.validateField(this, mCirculationCardEditText, "Ingresa una tarjeta de circulación"))
            return;
        if (mCirculationCardPhotoPath == null) {
            UIHelper.showSnackbar(this, getString(R.string.take_a_photo_of_card_circulation));
            UIHelper.returnToNormalField(this, mCirculationCardEditText);
            mTakeCardPhotoButton.setBackgroundTintList(getResources().getColorStateList(R.color.red));
            return;
        }
        Intent intent = new Intent(this, DimensionsActivity.class);

        mFlowObject.setCirculationCardInserted(mCirculationCardEditText.getText().toString());
        mFlowObject.setCirculationCardPhotoPath(mCirculationCardPhotoPath);

        mFlowObject.hasDimensions(mHasDimensions);
        if (mHasDimensions) {
            if (!UIHelper.validateField(this, mIncreaseEditText, "Ingresa un aumento agregado")) {
                return;
            }
            mFlowObject.haveAnIncrease(mIncreaseCheckBox.isChecked());
            float increase = 0;
            try{
                increase = Float.parseFloat(mIncreaseEditText.getText().toString());
            }catch (Exception e){
                e.printStackTrace();
            }
            mFlowObject.setIncrease(increase);
        } else
            intent = new Intent(this, HydraulicJackActivity.class);


        intent.putExtra(FLOW_OBJECT_EXTRA, mFlowObject);


        startActivity(intent);
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    public void onImageCaptured(String photoPath) {
        if (ImageHelper.imageExists(photoPath)) {
            mCirculationCardPhotoPath = photoPath;
            mTakeCardPhotoButton.setBackgroundTintList(getResources().getColorStateList(R.color.green));
            mTakeCardPhotoButton.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_ok, 0, 0, 0);
            mTakeCardPhotoButton.setEnabled(false);
        }
    }
}
