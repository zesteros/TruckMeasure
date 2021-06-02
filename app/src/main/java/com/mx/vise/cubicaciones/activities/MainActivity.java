package com.mx.vise.cubicaciones.activities;

import android.content.Intent;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.util.Log;

import com.google.common.primitives.Ints;
import com.mx.vise.cubicaciones.R;
import com.mx.vise.androiduihelper.UIHelper;
import com.mx.vise.cubicaciones.entities.FlowObject;
import com.mx.vise.login.activities.LoginActivity;
import com.mx.vise.login.exceptions.ModulesHasNotDeclaredException;
import com.mx.vise.login.pojos.EmployeePojo;
import com.mx.vise.login.util.PermissionsHelper;

import java.util.ArrayList;

public class MainActivity extends LoginActivity {


    private static final String TAG = "VISE";
    public static final String FLOW_OBJECT_EXTRA = "flowObject";

    public PermissionsHelper mPermissionsHelper;

    @Override
    public void onCreateLogin() {

        /*Para cambiar el nombre de la interfaz instanciar y luego asignar*/

        mAppName = findViewById(R.id.appName);
        mLoginButton = findViewById(R.id.loginButton);
        getSupportActionBar().setTitle(R.string.app_name_cubage);

        mAppName.setText(R.string.cubage_upper_case);
        /*Asigna on click al boton*/
        mLoginButton.setOnClickListener(this);

        int[] projects = getResources().getIntArray(R.array.projects);

        mProjects = new ArrayList(Ints.asList(projects));

        /*A donde se irá cuando ingrese exitosamente*/
        mIntent = new Intent(this, BuildingDataActivity.class);

        mPermissionsHelper = new PermissionsHelper();
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    public void onLogin(EmployeePojo employeePojo) {

        int[] modules = getResources().getIntArray(R.array.modules);

        try {
            ArrayList<Integer> permissionsGranted = mPermissionsHelper.hasAnyPermission(employeePojo, modules);

            if (permissionsGranted.isEmpty()) {
                UIHelper.showSnackbar(this, "No tienes permisos para acceder.", true);
                return;
            }
            for (Integer module : permissionsGranted)
                Log.i(TAG, "onLogin: Modulo con permiso:" + module.toString());

            FlowObject flowObject = new FlowObject();
            flowObject.setSession(employeePojo);
            mIntent.putExtra(FLOW_OBJECT_EXTRA, flowObject);
            startActivity(mIntent);

        } catch (ModulesHasNotDeclaredException e) {
            e.printStackTrace();
        }

    }
}
