package com.mx.vise.cubicaciones.tasks;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.mx.vise.cubicaciones.dao.DAO;
import com.mx.vise.cubicaciones.entities.CubagePhotos;
import com.mx.vise.cubicaciones.pojos.CubagePOJO;
import com.mx.vise.cubicaciones.pojos.CubageProcessPOJO;
import com.mx.vise.cubicaciones.webservice.WebServiceConstants;
import com.mx.vise.androidwscon.webservice.ConWs;
import com.mx.vise.login.pojos.EmployeePojo;


import org.springframework.http.ResponseEntity;

import java.util.ArrayList;
import java.util.List;


/**
 * **************************VISE*******************************
 * *******************DEPARTAMENTO DE T.I.**********************
 * <p>
 * Creado por aloza el lunes 24 de septiembre del 2018
 *
 * @author Angelo de Jesus Loza Martinez
 * @version acarreosandroid
 */

public class SyncData extends AsyncTask<EmployeePojo, Progress, SyncStatus> implements OnProgressUpdate {


    private final Context mContext;
    private Preference mPreference;
    public ProgressDialog mProgressDialog;
    private String mImei;
    public static final String TAG = "VISE";
    private OnSyncListener mSyncListener;
    private List<CubageProcessPOJO> mCubagesToSend;
    private ArrayList<CubageProcessPOJO> mCubagesFailed;


    public SyncData(Preference preference, Context context, String imei) {
        this.mPreference = preference;
        this.mContext = context;
        this.mImei = imei;
    }

    public SyncData(Context context, String imei) {
        this.mContext = context;
        this.mImei = imei;
    }

    @Override
    protected void onPreExecute() {
        // instantiate it within the onCreate method
        mProgressDialog = new ProgressDialog(mContext);
        mProgressDialog.setMessage("Sincronizando datos...");
        mProgressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
        mProgressDialog.setCancelable(false);
        mProgressDialog.show();
        mCubagesFailed = new ArrayList<>();

        super.onPreExecute();
    }

    public SyncData setOnSyncListener(OnSyncListener onSyncListener) {
        this.mSyncListener = onSyncListener;
        return this;
    }

    @Override
    protected SyncStatus doInBackground(EmployeePojo... employeePojos) {
        /*
         * Busca si hay cubicaciones por enviar
         *
         * */
        SyncStatus syncStatus = new SyncStatus();
        /*
         * Lista de ids de las cubicaciones
         * */
        ArrayList<CubagePhotos> cubagesDispatched = new ArrayList<>();
        try {
            /*
             * Obtiene las cubicaciones a enviar
             * */
            mCubagesToSend = DAO.getCubageToSend(mContext, this, cubagesDispatched);
        } catch (Exception e) {
            e.printStackTrace();
        }
        /*
         * Muestra el progreso de las N cubicaciones que se vayan a subir, inicializalo segun
         * la cantidad de cubicaciones
         * */
        int totalElements = mCubagesToSend.size();
        Log.i(TAG, "doInBackground: Procesando cubicaciones");
        publishProgress(new Progress(0, 0, totalElements, "Subiendo cubicaciones, por favor espere..."));
        /*
         *  Envíalas (si las hay)
         * */
        syncStatus.setUploadSuccessful(true);
        int i = 0;//contador de cubicaciones enviadas
        for (CubageProcessPOJO cubage : mCubagesToSend) {

            ConWs<Boolean, CubageProcessPOJO> con = new ConWs(WebServiceConstants.URL_PUT_DATA, this.mImei);

            ResponseEntity<Boolean> responseEntity = con.getObject(cubage, Boolean.class);


            if (responseEntity != null) {
                if (responseEntity.getStatusCode().value() == 200) {
                    if (responseEntity.getBody() != null) {


                        /*
                         * Agrega las cubicaciones enviadas
                         * */
                        DAO.changeCubageStatus(mContext, "B", cubagesDispatched.get(i));


                    } else {
                        mCubagesFailed.add(cubage);
                    }
                } else  {
                    mCubagesFailed.add(cubage);
                }
            } else {
                mCubagesFailed.add(cubage);
            }
            i++;
            publishProgress(i * 100 / totalElements, i, totalElements);

        }
        publishProgress(new Progress(0, 0, totalElements, "Descargando sindicatos y marcas..."));
        /*
         * Obtiene los datos (marcas, sindicatos y tags)
         * */
        ConWs<CubagePOJO, Object> con = new ConWs(WebServiceConstants.URL_GET_DATA, this.mImei);

        ResponseEntity<CubagePOJO> responseEntity = con.getObject(null, CubagePOJO.class);

        if (responseEntity != null) {
            if (responseEntity.getStatusCode().value() == 200) {
                if (responseEntity.getBody() != null){
                    DAO.addSyncDataToDatabase(responseEntity.getBody(), mContext, this);
                    syncStatus.setDownloadSuccessful(true);
                }
            }
        }
        return syncStatus;
    }

    @Override
    protected void onPostExecute(SyncStatus syncSuccessful) {
        mProgressDialog.dismiss();
        if (mPreference != null)
            mPreference.setSummary("Actualizado hace 1 minuto");
        if (syncSuccessful.isDownloadSuccessful() && syncSuccessful.isUploadSuccessful()) {
            if (mSyncListener != null)
                mSyncListener.onSyncSuccessful(mCubagesFailed);
        } else {
            if (mSyncListener != null)
                mSyncListener.onSyncFailed(syncSuccessful, mCubagesFailed);
        }
    }

    @Override
    protected void onProgressUpdate(final Progress... values) {
        mProgressDialog.setIndeterminate(false);
        if (values[0].getText() != null) {
            if (mContext instanceof PreferenceActivity) {
                ((PreferenceActivity) mContext).runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        mProgressDialog.setMessage(values[0].getText());
                    }
                });
            } else if(mContext instanceof AppCompatActivity){
                ((AppCompatActivity) mContext).runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        mProgressDialog.setMessage(values[0].getText());
                    }
                });
            }
        }
        mProgressDialog.setProgress(values[0].getProgress());
        mProgressDialog.setSecondaryProgress(values[0].getSecondaryProgress());
        mProgressDialog.setMax(values[0].getTotalElements());
    }

    @Override
    public void publishProgress(int progress, int secondaryProgress, int totalElements) {
        onProgressUpdate(new Progress[]{new Progress(progress, secondaryProgress, totalElements)});
    }

    @Override
    public void publishProgress(Progress progress) {
        onProgressUpdate(new Progress[]{progress});
    }
}
