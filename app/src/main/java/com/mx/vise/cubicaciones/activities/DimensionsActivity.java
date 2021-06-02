package com.mx.vise.cubicaciones.activities;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

import com.mx.vise.androiduihelper.UIHelper;
import com.mx.vise.cubicaciones.R;
import com.mx.vise.cubicaciones.entities.FlowObject;

import static com.mx.vise.cubicaciones.activities.MainActivity.FLOW_OBJECT_EXTRA;

public class DimensionsActivity extends AppCompatActivity {

    private FlowObject mFlowObject;

    /*
     * Arreglo de Edit Texts para simplificar el formulario de medidas
     * POSICIÓN EN ARRAY -> EDIT TEXT
     * (0,1,2,3,4,5,6) - > (L1,A1,H1,L2,A2,H2,LC)
     *
     * */
    private EditText[] mMeasuresEditText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_calculation);
        getSupportActionBar().setTitle("Dimensiones");


        Bundle extras = getIntent().getExtras();

        int[] ids = getSizesResourceID();

        mMeasuresEditText = new EditText[ids.length];

        for (int i = 0; i < mMeasuresEditText.length; i++)
            mMeasuresEditText[i] = findViewById(ids[i]);


        if (extras != null)
            mFlowObject = (FlowObject) extras.get(FLOW_OBJECT_EXTRA);

    }

    public void nextOnClick(View v) {
        for (int i = 0; i < mMeasuresEditText.length; i++) {
            if (!UIHelper.validateField(this,mMeasuresEditText[i], mMeasuresEditText[i].getHint().toString())) {
                for (int j = 0; j < mMeasuresEditText.length; j++) {
                    if (j != i)
                        UIHelper.returnToNormalField(this, mMeasuresEditText[j]);
                }
                return;
            }
        }
        Intent intent = new Intent(this, HydraulicJackActivity.class);

        float[] measures = new float[mMeasuresEditText.length];
        for (int i = 0; i < mMeasuresEditText.length; i++) {
            try {
                measures[i] = Float.parseFloat(mMeasuresEditText[i].getText().toString());
            } catch (NumberFormatException e) {
                measures[i] = 0;
                e.printStackTrace();
            }
        }

        mFlowObject.setBasicMeasures(measures);

        intent.putExtra(FLOW_OBJECT_EXTRA, mFlowObject);

        startActivity(intent);


    }

    public int[] getSizesResourceID() {
        return new int[]{
                R.id.l1BoxEditText,
                R.id.a1BoxEditText,
                R.id.h1BoxEditText,
                R.id.l2BoxEditText,
                R.id.a2BoxEditText,
                R.id.h2BoxEditText,
                R.id.lcEditText
        };
    }
}
