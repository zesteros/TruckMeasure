package com.mx.vise.cubicaciones.activities;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.mx.vise.camerahelper.util.ImageHelper;
import com.mx.vise.cubicaciones.R;
import com.mx.vise.cubicaciones.dao.DAO;
import com.mx.vise.cubicaciones.entities.FlowObject;
import com.mx.vise.cubicaciones.entities.Signature;

import java.io.File;
import java.util.Date;

import static com.mx.vise.cubicaciones.activities.BuildingDataActivity.SESSION_EXTRA;
import static com.mx.vise.cubicaciones.activities.HydraulicJackActivity.SIGNATURE_REQUEST_CODE;
import static com.mx.vise.cubicaciones.activities.MainActivity.FLOW_OBJECT_EXTRA;
import static com.mx.vise.cubicaciones.activities.SignatureActivity.SIGNATURE_BITMAP_EXTRA;

public class FinishActivity extends AppCompatActivity {


    private Bitmap mSignatureBitmap;
    private Button mCaptureEmployeeButton;
    private boolean mSignatureCaptured;
    private FlowObject mFlowObject;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_finish);

        mCaptureEmployeeButton = findViewById(R.id.captureEmployeeButton);

        Bundle extras = getIntent().getExtras();

        if (extras != null)
            mFlowObject = (FlowObject) extras.get(FLOW_OBJECT_EXTRA);

    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    public void onCaptureEmployeeButtonClick(View view) {
        if (((Button) view).getText().toString().equals(getString(R.string.capture_employee_sign))) {
            Intent intent = new Intent(this, SignatureActivity.class);
            startActivityForResult(intent, SIGNATURE_REQUEST_CODE);
        } else if (((Button) view).getText().toString().equals(getString(R.string.finish))) {
            Intent intent = new Intent(this, BuildingDataActivity.class);
            intent.putExtra(SESSION_EXTRA, mFlowObject.getSession());
            saveCubageToDatabase();
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
        }

    }

    /**
     * @return if save was success
     */
    public boolean saveCubageToDatabase() {
        if (mFlowObject != null){
            mFlowObject.setEndCaptureDate(new Date());
            return DAO.saveCubage(this, mFlowObject);
        }
        return false;
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

                mCaptureEmployeeButton.setText(R.string.finish);
                File userSignature = ImageHelper.exportBitmapToExternalStorage(mSignatureBitmap);
                mFlowObject.setUserSignaturePath(userSignature.getAbsolutePath());
            }
        }
    }

}
