package com.mx.vise.cubicaciones.activities;

import android.content.Intent;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.mx.vise.androiduihelper.UIHelper;
import com.mx.vise.camerahelper.activities.PhotoActivity;
import com.mx.vise.camerahelper.util.ImageHelper;
import com.mx.vise.cubicaciones.R;
import com.mx.vise.cubicaciones.entities.FlowObject;

import static com.mx.vise.cubicaciones.activities.MainActivity.FLOW_OBJECT_EXTRA;

public class UnitPhotosAcivity extends PhotoActivity implements View.OnClickListener {

    private Button mFrontalButton;
    private Button mBackButton;
    private Button mSideButton;
    private Button mNextButton;
    private FlowObject mFlowObject;
    private String mFrontalPath;
    private String mSidePath;
    private String mBackPath;
    private boolean mIsFrontal;
    private boolean mIsBack;
    private boolean mIsSide;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_unit_photo);
        getSupportActionBar().setTitle("Fotografías de la unidad");

        Bundle extras = getIntent().getExtras();
        if (extras != null)
            mFlowObject = (FlowObject) extras.get(FLOW_OBJECT_EXTRA);

        mFrontalButton = findViewById(R.id.frontalPhotoButton);
        mBackButton = findViewById(R.id.backPhotoButton);
        mSideButton = findViewById(R.id.sidePhotoButton);
        mNextButton = findViewById(R.id.nextUnitDataButton);

        mFrontalButton.setOnClickListener(this);
        mBackButton.setOnClickListener(this);
        mSideButton.setOnClickListener(this);
        mNextButton.setOnClickListener(this);

        setupCameraPhoto();

    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.frontalPhotoButton:
                launchCameraActivity();
                mIsFrontal = true;
                mIsBack = mIsSide = false;
                break;
            case R.id.backPhotoButton:
                launchCameraActivity();
                mIsBack = true;
                mIsFrontal = mIsSide = false;
                break;
            case R.id.sidePhotoButton:
                launchCameraActivity();
                mIsSide = true;
                mIsBack = mIsFrontal = false;
                break;
            case R.id.nextUnitDataButton:
                validateFields();
                break;
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public void validateFields() {

        if (mFrontalPath == null) {
            UIHelper.showSnackbar(this,"Toma la fotografía del lado frontal.");
            mFrontalButton.setBackgroundTintList(getResources().getColorStateList(R.color.red));
            if (mBackPath == null)
                mBackButton.setBackgroundTintList(getResources().getColorStateList(R.color.primary));
            if (mSidePath == null)
                mSideButton.setBackgroundTintList(getResources().getColorStateList(R.color.primary));
            return;
        }

        if (mSidePath == null) {
            UIHelper.showSnackbar(this, "Toma la fotografía del lado lateral.");
            mSideButton.setBackgroundTintList(getResources().getColorStateList(R.color.red));
            if (mBackPath == null)
                mBackButton.setBackgroundTintList(getResources().getColorStateList(R.color.primary));
            if (mFrontalPath == null)
                mFrontalButton.setBackgroundTintList(getResources().getColorStateList(R.color.primary));
            return;
        }
        if (mBackPath == null) {
            UIHelper.showSnackbar(this, "Toma la fotografía del lado trasero.");
            mBackButton.setBackgroundTintList(getResources().getColorStateList(R.color.red));
            if (mFrontalPath == null)
                mFrontalButton.setBackgroundTintList(getResources().getColorStateList(R.color.primary));
            if (mSidePath == null)
                mSideButton.setBackgroundTintList(getResources().getColorStateList(R.color.primary));
            return;
        }


        Intent intent = new Intent(this, CaptureLicensePlateActivity.class);
        mFlowObject.setUnitFrontalPhotoPath(mFrontalPath);
        mFlowObject.setUnitSidePhotoPath(mSidePath);
        mFlowObject.setUnitBackPhotoPath(mBackPath);
        intent.putExtra(FLOW_OBJECT_EXTRA, mFlowObject);
        startActivity(intent);
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    public void onImageCaptured(String photoPath) {
        String place = mIsSide ? "lateral" : mIsBack ? "trasera" : mIsFrontal ? "frontal" : "";

        if (!ImageHelper.imageExists(photoPath))
            return;

        if (mIsSide) {
            mSidePath = photoPath;
            changeStateOfButton(mSideButton, place);
        } else if (mIsFrontal) {
            mFrontalPath = photoPath;
            changeStateOfButton(mFrontalButton, place);
        } else if (mIsBack) {
            mBackPath = photoPath;
            changeStateOfButton(mBackButton, place);
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public void changeStateOfButton(Button button, String place) {
        button.setBackgroundTintList(getResources().getColorStateList(R.color.green));
        button.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_ok, 0, 0, 0);
        button.setText("IMAGEN " + place.toUpperCase() + " CAPTURADA");
    }
}
