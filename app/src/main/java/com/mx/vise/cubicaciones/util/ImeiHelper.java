package com.mx.vise.cubicaciones.util;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.provider.Settings;
import android.support.v4.app.ActivityCompat;
import android.telephony.TelephonyManager;

/**
 * **************************VISE*******************************
 * *******************DEPARTAMENTO DE T.I.**********************
 * Creado por aloza
 *
 * @author Angelo de Jesus Loza Martinez
 * @version CombustibleVISEandroidv1.0
 *          el lunes 02 de abril del 2018
 */

public class ImeiHelper {
    private String Imei;

    public Context mContext;
    private TelephonyManager tm;


    public ImeiHelper(Context context) {
        this.mContext = context;
        tm = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);

    }

    public String getImei() {
        TelephonyManager telephonyManager = (TelephonyManager) mContext.getSystemService(Context.TELEPHONY_SERVICE);
        if (ActivityCompat.checkSelfPermission(mContext, Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.

            return null;
        }
        String deviceId = telephonyManager.getDeviceId().trim();
        if (deviceId == null) {
            String androidId = Settings.Secure.getString(mContext.getContentResolver(),
                    Settings.Secure.ANDROID_ID);
            deviceId = android.os.Build.SERIAL + "#" + androidId;
        }
        return deviceId.trim();
    }
}
