package com.mx.vise.cubicaciones.activities;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import com.mx.vise.camerahelper.activities.PhotoActivity;
import com.mx.vise.camerahelper.util.ImageHelper;
import com.mx.vise.cubicaciones.R;
import com.mx.vise.cubicaciones.adapters.SyndicateAdapter;
import com.mx.vise.cubicaciones.dao.DAO;
import com.mx.vise.cubicaciones.entities.FlowObject;
import com.mx.vise.cubicaciones.pojos.CubageProcessPOJO;
import com.mx.vise.cubicaciones.pojos.SyndicatePOJO;
import com.mx.vise.cubicaciones.settings.SettingsActivity;
import com.mx.vise.androiduihelper.UIHelper;
import com.mx.vise.cubicaciones.singleton.Singleton;
import com.mx.vise.cubicaciones.tasks.OnSyncListener;
import com.mx.vise.cubicaciones.tasks.SyncData;
import com.mx.vise.cubicaciones.tasks.SyncStatus;
import com.mx.vise.cubicaciones.util.ImeiHelper;
import com.mx.vise.login.pojos.BuildingPojo;
import com.mx.vise.login.pojos.EmployeePojo;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import static com.mx.vise.cubicaciones.activities.MainActivity.FLOW_OBJECT_EXTRA;


/**
 * Clase para alojar la actividad de datos de obra (donde se obtienen los datos del proveedor para proceder la siguiente pantalla)
 */
public class BuildingDataActivity extends PhotoActivity implements View.OnClickListener {

    private static final String TAG = "VISE";
    public static final String BUILD_ID_SEPARATOR = " - ";
    public static final String SESSION_EXTRA = "VISE";

    private Button mNextButton;
    private EditText mOperatorET;
    private EmployeePojo mEmployeePojo;
    private Spinner mBuildingSpinner;
    private Spinner mSyndicateSpinner;
    private Button mTakeLicenceCardPhotoButton;
    private String mLicenceCardPhotoPath;
    private FlowObject mFlowObject;
    private ImeiHelper mImeiHelper;
    public final float CUBAGE_UPLOAD_APROX_DURATION = 6.34f;

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().setTitle(R.string.welcome_title);
        setContentView(R.layout.activity_building_data);
        /*
         * Instancia los botones de seguimiento y el spinner de proveedores
         * */
        mNextButton = findViewById(R.id.nextHydraulicJackButton);
        mOperatorET = findViewById(R.id.operatorET);
        mSyndicateSpinner = findViewById(R.id.syndicateSpinner);
        mTakeLicenceCardPhotoButton = findViewById(R.id.licenceCardPhotoButton);
        mBuildingSpinner = findViewById(R.id.buildingSpinner);
        setBuildingData();
        requestPermissions();
        setupCameraPhoto();
        /*
        * Si no se pueden cargar los sindicatos
        * */
        if (!loadSyndicates()) {
            /*
            * Solicita sincronizar
            * */
            UIHelper
                    .showDialog(this, getString(R.string.there_are_not_syndicates),
                            getString(R.string.please_sync_data_with_server),
                            false,
                            false,
                            new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    String imei = mImeiHelper != null ? mImeiHelper.getImei() : "";
                                    final SyncData syncData = new SyncData(BuildingDataActivity.this,imei);
                                    syncData.setOnSyncListener(new OnSyncListener() {
                                        @Override
                                        public void onSyncSuccessful(ArrayList<CubageProcessPOJO> mCubagesFailed) {
                                            loadSyndicates();
                                        }

                                        @Override
                                        public void onSyncFailed(SyncStatus syncStatus, ArrayList<CubageProcessPOJO> mCubagesFailed) {

                                        }
                                    });
                                   syncData.execute();

                                }
                            });
        }


        mNextButton.setOnClickListener(this);
        mTakeLicenceCardPhotoButton.setOnClickListener(this);

    }

    /**
     * request required permissions
     */
    public void requestPermissions() {
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.READ_PHONE_STATE)
                != PackageManager.PERMISSION_GRANTED

                || ContextCompat.checkSelfPermission(this,
                Manifest.permission.CAMERA)
                != PackageManager.PERMISSION_GRANTED

                || ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_NETWORK_STATE)
                != PackageManager.PERMISSION_GRANTED

                || ContextCompat.checkSelfPermission(this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED

                || ContextCompat.checkSelfPermission(this,
                Manifest.permission.READ_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED

                || ContextCompat.checkSelfPermission(this,
                Manifest.permission.BLUETOOTH_ADMIN)
                != PackageManager.PERMISSION_GRANTED

                || ContextCompat.checkSelfPermission(this,
                Manifest.permission.BLUETOOTH)
                != PackageManager.PERMISSION_GRANTED

                || ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED

                || ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_COARSE_LOCATION)
                != PackageManager.PERMISSION_GRANTED
                ) {

            // Should we show an explanation?
            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    Manifest.permission.READ_PHONE_STATE)) {

                // Show an expanation to the user *asynchronously* -- don't block
                // this thread waiting for the user's response! After the user
                // sees the explanation, try again to request the permission.

            } else {

                // No explanation needed, we can request the permission.

                ActivityCompat.requestPermissions(this,
                        new String[]{
                                Manifest.permission.READ_PHONE_STATE,
                                Manifest.permission.CAMERA,
                                Manifest.permission.ACCESS_NETWORK_STATE,
                                Manifest.permission.WRITE_EXTERNAL_STORAGE,
                                Manifest.permission.READ_EXTERNAL_STORAGE,
                                Manifest.permission.BLUETOOTH,
                                Manifest.permission.BLUETOOTH_ADMIN,
                                Manifest.permission.ACCESS_FINE_LOCATION,
                                Manifest.permission.ACCESS_COARSE_LOCATION,
                        }, 100);

                // MY_PERMISSIONS_REQUEST_READ_CONTACTS is an
                // app-defined int constant. The callback method gets the
                // result of the request.
            }
        } else {

            mImeiHelper = new ImeiHelper(this);

            if (mImeiHelper.getImei() != null) {
                Singleton.getInstance().setImei(mImeiHelper.getImei());
                if (mFlowObject == null) mFlowObject = new FlowObject();
                mFlowObject.setImei(mImeiHelper.getImei());
                mFlowObject.setStartCaptureDate(new Date());
            }
            //mFlowObject.setImei(mImeiHelper.getImei());
            //Log.d("VISE", "Phone imei: " + mImeiHelper.getImei());
        }
    }

    /**
     * @return if loading syndicates was successful
     */
    public boolean loadSyndicates() {
        ArrayList<SyndicatePOJO> syndicates = DAO.getSyndicates(this);

        if (syndicates != null) {
            SyndicateAdapter adapter = new SyndicateAdapter(this, syndicates);
            mSyndicateSpinner.setAdapter(adapter);
           /* if (mFlowObject != null)
                for (int i = 0; i < syndicates.length; i++)
                    if (syndicates..equals(mFlowObject.getSelectedSyndicate())) {
                        mSyndicateSpinner.setSelection(i);
                        break;
                    }*/
            return true;
        }
        return false;
    }


    /**
     * @param v
     */
    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.nextHydraulicJackButton) {
            if (!validateFields()) return;
            Intent intent = new Intent(this, UnitPhotosAcivity.class);
            mFlowObject.setSelectedSyndicate(((SyndicatePOJO) mSyndicateSpinner.getSelectedItem()));
            mFlowObject.setWritedOperator(mOperatorET.getText().toString());
            mFlowObject.isRearLicencePlate(true);
            mFlowObject.setSelectedBuilding((String) mBuildingSpinner.getSelectedItem());
            mFlowObject.setLicenceCardPhotoPath(mLicenceCardPhotoPath);

            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyyddMM");
            String date = simpleDateFormat.format(new Date());
            String sheetNumber =
                   date +
                            mFlowObject.getSelectedBuilding().split("-")[1] + + mFlowObject.getSession().getEmployeeId()+"1";
            sheetNumber = sheetNumber.replace(" ", "");
            mFlowObject.setSheetNumber(sheetNumber);
            intent.putExtra(FLOW_OBJECT_EXTRA, mFlowObject);
            startActivity(intent);
        } else {
            launchCameraActivity();
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    public void onImageCaptured(String photoPath) {
        if (ImageHelper.imageExists(photoPath)) {
            mLicenceCardPhotoPath = photoPath;
            changeButtonStateToCaptured();
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public void changeButtonStateToCaptured() {
        mTakeLicenceCardPhotoButton.setBackgroundTintList(getResources().getColorStateList(R.color.green));
        mTakeLicenceCardPhotoButton.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_ok, 0, 0, 0);
        mTakeLicenceCardPhotoButton.setEnabled(false);
    }


    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public boolean validateFields() {
        if (!UIHelper.validateField(this, mOperatorET, getString(R.string.introduce_a_operator))) {
            mTakeLicenceCardPhotoButton.setBackgroundTintList(getResources().getColorStateList(R.color.primary));
            return false;
        }
        if (!UIHelper.validateSpinner(this, mBuildingSpinner, getString(R.string.select_building))) {
            UIHelper.returnToNormalField(this, mOperatorET);
            mTakeLicenceCardPhotoButton.setBackgroundTintList(getResources().getColorStateList(R.color.primary));
            return false;
        }
        if (mSyndicateSpinner.getAdapter() != null) {
            if (!validateSyndicate()) {
                UIHelper.returnToNormalField(this, mOperatorET);
                mTakeLicenceCardPhotoButton.setBackgroundTintList(getResources().getColorStateList(R.color.primary));
                return false;
            }
        } else {
            UIHelper.showSnackbar(this, "Por favor, sincroniza los sindicatos.");
            return false;
        }


        if (mLicenceCardPhotoPath == null) {
            UIHelper.showSnackbar(this, getString(R.string.take_a_photo_of_operator_licence));
            UIHelper.returnToNormalField(this, mOperatorET);
            mTakeLicenceCardPhotoButton.setBackgroundTintList(getResources().getColorStateList(R.color.red));
            return false;
        }


        return true;
    }

    public boolean validateSyndicate() {
        if (((SyndicatePOJO) mSyndicateSpinner.getSelectedItem()).getName().toString().equals(
                getString(R.string.select_a_syndicate))) {
            mSyndicateSpinner.requestFocus();
            mSyndicateSpinner.performClick();
            return false;
        }
        return true;
    }

    /**
     * Inicializa texto en etiquetas desde datos de login (extras)
     */
    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public void setBuildingData() {

        Bundle extras = getIntent().getExtras();

        TextView superintendent = findViewById(R.id.buildingManagerTV);
        TextView employee = findViewById(R.id.welcomeEmployeeTV);
        ArrayAdapter<CharSequence> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item);
        adapter.add(getString(R.string.select_building));

        if (extras != null) {


            if (extras.get(FLOW_OBJECT_EXTRA) == null) {
                mFlowObject = new FlowObject();
                mEmployeePojo = (EmployeePojo) extras.get(SESSION_EXTRA);

                mFlowObject.setSession(mEmployeePojo);
            } else {
                mFlowObject = (FlowObject) extras.get(FLOW_OBJECT_EXTRA);
                mOperatorET.setText(mFlowObject.getWritedOperator());
                mLicenceCardPhotoPath = mFlowObject.getLicenceCardPhotoPath();
                changeButtonStateToCaptured();
                mEmployeePojo = mFlowObject.getSession();
            }


            if (mEmployeePojo != null) {
                employee.setText(String.valueOf(mEmployeePojo.getEmployeeName().split(" ")[0].toUpperCase()));


                ArrayList<BuildingPojo> buildings = mEmployeePojo.getBuildings();
                if (buildings.isEmpty()) {
                    UIHelper.showDialog(this, "Sin obras",
                            "Lo sentimos, tu superintendente no tiene obras asignadas",
                            false,
                            false,
                            new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    finish();
                                }
                            });
                }
                int index = 0;
                int selectedBuilding = 0;
                for (BuildingPojo buildingPojo : buildings) {
                    String building = buildingPojo.getBuildingId() + BUILD_ID_SEPARATOR + buildingPojo.getBuildingDescription();
                    if (mFlowObject != null)
                        if (mFlowObject.getSelectedBuilding() != null)
                            if (mFlowObject.getSelectedBuilding().equals(building))
                                selectedBuilding = index;
                    adapter.add(building);
                    superintendent.setText(buildingPojo.getSuperintendent().toUpperCase());
                    index++;
                }
                mBuildingSpinner.setAdapter(adapter);
                if (mFlowObject != null)
                    mBuildingSpinner.setSelection(selectedBuilding);


            } else {
                adapter.add("Obra ejemplo - 1828");
                mBuildingSpinner.setAdapter(adapter);
                superintendent.setText("SUPERINTENDENTE NOMBRE");
            }
        } else {
            adapter.add("Obra ejemplo - 1828");
            mBuildingSpinner.setAdapter(adapter);
            superintendent.setText("SUPERINTENDENTE NOMBRE");
            employee.setText("EJEMPLO NOMBRE");
        }

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
