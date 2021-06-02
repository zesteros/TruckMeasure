package com.mx.vise.cubicaciones.activities;

import android.annotation.TargetApi;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.mx.vise.androiduihelper.UIHelper;
import com.mx.vise.cubicaciones.R;
import com.mx.vise.cubicaciones.dao.DAO;
import com.mx.vise.cubicaciones.entities.FlowObject;
import com.mx.vise.cubicaciones.settings.SettingsActivity;
import com.mx.vise.uhf.UHFHelper;
import com.mx.vise.uhf.entities.Tag;
import com.mx.vise.uhf.interfaces.OnUHFDetectListener;

import java.util.ArrayList;

import static com.mx.vise.cubicaciones.activities.MainActivity.FLOW_OBJECT_EXTRA;
import static com.mx.vise.uhf.UHFTagReadWrite.ERROR_CODE;

public class TagActivity extends AppCompatActivity implements OnUHFDetectListener {

    private static final String TAG = "VISE";
    private Button mWriteTagButton;
    private TextView mTagTextView;
    private UHFHelper mUHFHelper;
    private FlowObject mFlowObject;
    private Snackbar mSnackbar;
    private String mEpc;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tag);

        mWriteTagButton = findViewById(R.id.readWriteTagButton);

        mTagTextView = findViewById(R.id.tagReadTextView);

        mSnackbar = Snackbar.make(
                findViewById(android.R.id.content),
                "Ha ocurrido un error al escribir en el tag.",
                Snackbar.LENGTH_SHORT
        );

        Bundle extras = getIntent().getExtras();
        if (extras != null)
            mFlowObject = (FlowObject) extras.get(FLOW_OBJECT_EXTRA);
        if (mFlowObject == null) {
            mFlowObject = new FlowObject();
            mFlowObject.setImei("8625152625615");
            mFlowObject.setRearPlateWritedManual("tag");
            mFlowObject.setTotalVolume(83.32112323f);
            mFlowObject.setAdjustment(8.12818f);

        }
    }

    boolean executed;



    @Override
    public void onUHFSDetected(ArrayList<String> tids, boolean isValid) {

    }

    @TargetApi(Build.VERSION_CODES.M)
    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    public void onTagWriteSuccess() {
        UIHelper.showSnackbar(this, "Tag escrito exitosamente.", false);
//        mWriteTagButton.setEnabled(false);
//        mWriteTagButton.setText("Tag generado");
//        mWriteTagButton.setBackgroundTintList(getResources().getColorStateList(R.color.button_disabled_tint));
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    public void onTagWriteFailed(boolean overwriteIntent) {
        executed = overwriteIntent;
        if (executed)
            UIHelper.showSnackbar(this, "Lo sentimos, ya hay datos en el tag.", true);
        else
            UIHelper.showSnackbar(this, "Ocurrió un error al escribir en el tag.", true);
        mWriteTagButton.setText(R.string.read_tag);
    }


    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    public void onTagRead(final Tag tag) {
        if (tag != null) {
            if (tag.getEpc() != null) {
                mEpc = tag.getEpc();
            }
            if (tag.getTid() != null) {
                if (!tag.getTid().equals(ERROR_CODE)) {
                    mTagTextView.setText(tag.getTid());
                    new AsyncTask<Void, Void, Boolean>() {
                        private ProgressDialog progressDialog;

                        @Override
                        protected void onPreExecute() {
                            super.onPreExecute();
                            progressDialog = ProgressDialog.show(TagActivity.this, "Validación de tag", "Validando Tag", true, false);
                        }

                        @Override
                        protected Boolean doInBackground(Void... voids) {
                            return DAO.tagExists(TagActivity.this, tag.getTid());
                        }


                        @Override
                        protected void onPostExecute(Boolean tagExists) {
                            progressDialog.dismiss();
                            if (!tagExists)
                                UIHelper.showSnackbar(TagActivity.this, "Lo sentimos, el tag no es válido..", true);
                            else {
                                UIHelper.showSnackbar(TagActivity.this, "Tag con TID: " + tag.getTid() + " validado correctamente.", false);
                                mWriteTagButton.setText(R.string.write_tag);
                            }
                        }
                    }.execute();


                } else {
                    UIHelper.showSnackbar(this, "Ocurrió un error al leer el tag.", true);
                }
            }
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    public void onTagWriteTimeout() {
        UIHelper.showSnackbar(this, "Se ha agotado el tiempo de espera, inténtelo de nuevo.", true);
        mWriteTagButton.setText(R.string.read_tag);
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    public void onTagReadTimeout() {
        UIHelper.showSnackbar(this, "Se ha agotado el tiempo de espera, inténtelo de nuevo.", true);
        mWriteTagButton.setText(R.string.read_tag);
    }



    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            Intent intent = new Intent(this, SettingsActivity.class);
            startActivity(intent);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
