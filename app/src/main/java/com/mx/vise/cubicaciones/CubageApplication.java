package com.mx.vise.cubicaciones;

import android.app.Application;
import android.content.Context;
import android.content.res.Configuration;
import android.widget.Toast;

import com.mx.vise.cubicaciones.singleton.Singleton;

import org.acra.ACRA;
import org.acra.annotation.AcraDialog;
import org.acra.annotation.AcraToast;
import org.acra.config.CoreConfigurationBuilder;
import org.acra.config.DialogConfigurationBuilder;
import org.acra.config.MailSenderConfigurationBuilder;
import org.acra.config.ToastConfigurationBuilder;
import org.acra.data.StringFormat;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * **************************VISE*******************************
 * *******************DEPARTAMENTO DE T.I.**********************
 * <p>
 * com.mx.vise.cubicaciones
 * Creado por Angelo el viernes 01 de febrero del 2019 a las 04:42 PM
 *
 * @author Angelo de Jesus Loza Martinez
 * @version cubicaciones
 */

public class CubageApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
    }

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        String imei = Singleton.getInstance().getImei() != null ? Singleton.getInstance().getImei() : "N/A";

        SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
        // The following line triggers the initialization of ACRA
        CoreConfigurationBuilder builder = new CoreConfigurationBuilder(this)
                .setBuildConfigClass(BuildConfig.class)
                .setReportFormat(StringFormat.JSON);
        builder.getPluginConfigurationBuilder(MailSenderConfigurationBuilder.class)
                .setMailTo(getString(R.string.developer_email))
                .setReportFileName(getString(R.string.file_name))
                .setSubject(getString(R.string.subject_report_email)+" - IMEI: "+imei)
                .setEnabled(true);
        builder.getPluginConfigurationBuilder(ToastConfigurationBuilder.class)
                .setEnabled(true)
                .setLength(Toast.LENGTH_LONG)
                .setText(getString(R.string.text_report_toast));
        ACRA.init(this, builder);
    }
}
