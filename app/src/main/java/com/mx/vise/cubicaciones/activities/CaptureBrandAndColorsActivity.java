package com.mx.vise.cubicaciones.activities;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.PorterDuff;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;

import com.mx.vise.androiduihelper.UIHelper;
import com.mx.vise.cubicaciones.R;
import com.mx.vise.cubicaciones.adapters.BrandAdapter;
import com.mx.vise.cubicaciones.dao.DAO;
import com.mx.vise.cubicaciones.entities.FlowObject;
import com.mx.vise.cubicaciones.pojos.BrandPOJO;
import com.mx.vise.cubicaciones.util.ColorUtils;
import com.skydoves.colorpickerview.ColorEnvelope;
import com.skydoves.colorpickerview.ColorPickerDialog;
import com.skydoves.colorpickerview.flag.FlagView;
import com.skydoves.colorpickerview.listeners.ColorEnvelopeListener;

import java.util.ArrayList;

import static com.mx.vise.cubicaciones.activities.MainActivity.FLOW_OBJECT_EXTRA;


public class CaptureBrandAndColorsActivity extends AppCompatActivity implements View.OnClickListener {

    private static final String TAG = "VISE";
    private Spinner mBrandSpinner;
    private Button mTractorUnitColorButton;
    private Button mBoxColorButton;
    private Spinner mUnitTypeSpinner;
    private Button mNextButton;
    private FlowObject mFlowObject;

    public class CustomFlag extends FlagView {

        private TextView textView;
        private View view;

        public CustomFlag(Context context, int layout) {
            super(context, layout);
            textView = findViewById(R.id.flag_color_code);
            view = findViewById(R.id.flag_color_layout);
        }

        @Override
        public void onRefresh(ColorEnvelope colorEnvelope) {
            textView.setText(ColorUtils.getColorNameFromHex("#" + String.format("%06X", (0xFFFFFF & colorEnvelope.getColor()))));
            view.setBackgroundColor(colorEnvelope.getColor());
        }

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().setTitle("Capturar Unidad");
        setContentView(R.layout.activity_capture_unit_data);

        mBrandSpinner = findViewById(R.id.brandSpinner);
        mTractorUnitColorButton = findViewById(R.id.tractorColorButton);
        mBoxColorButton = findViewById(R.id.boxColorButton);
        mUnitTypeSpinner = findViewById(R.id.unitTypeSpinner);
        mNextButton = findViewById(R.id.nextUnitButton);

        Bundle extras = getIntent().getExtras();

        if (extras != null)
            mFlowObject = (FlowObject) extras.get(FLOW_OBJECT_EXTRA);


        ArrayList<BrandPOJO> brands = DAO.getBrands(this);

        String unitTypes[] = getResources().getStringArray(R.array.unit_types);
        BrandAdapter brandAdapter = new BrandAdapter(this, brands);

        ArrayAdapter<String> unitTypeAdapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_spinner_dropdown_item, unitTypes);
        mUnitTypeSpinner.setAdapter(unitTypeAdapter);
        mBrandSpinner.setAdapter(brandAdapter);

        mTractorUnitColorButton.setOnClickListener(this);
        mBoxColorButton.setOnClickListener(this);
        mNextButton.setOnClickListener(this);


    }

    public static void hideKeyboard(AppCompatActivity activity) {
        InputMethodManager imm = (InputMethodManager) activity.getSystemService(AppCompatActivity.INPUT_METHOD_SERVICE);
        //Find the currently focused view, so we can grab the correct window token from it.
        View view = activity.getCurrentFocus();
        //If no view currently has focus, create a new one, just so we can grab a window token from it
        if (view == null) {
            view = new View(activity);
        }
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tractorColorButton:
                showColorDialog(mTractorUnitColorButton, R.string.tractor);
                break;
            case R.id.boxColorButton:
                showColorDialog(mBoxColorButton, R.string.box);
                break;
            case R.id.nextUnitButton:
                validateFields();
                break;

        }
    }
    public boolean validateBrand() {
        if (((BrandPOJO) mBrandSpinner.getSelectedItem()).getName().equals(
                getString(R.string.select_a_brand))) {
            mBrandSpinner.requestFocus();
            mBrandSpinner.performClick();
            return false;
        }
        return true;
    }

    public void validateFields() {

        if (!validateBrand())
            return;
        if (!UIHelper.validateSpinner(this, mUnitTypeSpinner, getString(R.string.select_a_unit_type)))
            return;
        if (mBoxColorButton.getText().toString().equals(getString(R.string.select_box_color))) {
            mBoxColorButton.performClick();
            UIHelper.showToast(this, getString(R.string.select_box_color));
            return;
        }
        if (mTractorUnitColorButton.getText().toString().equals(getString(R.string.select_tractor_color))) {
            mTractorUnitColorButton.performClick();
            UIHelper.showToast(this, getString(R.string.select_tractor_color));
            return;
        }

        Intent intent = new Intent(this, CirculationCardActivity.class);

        if (mFlowObject != null) {

            mFlowObject.setSelectedBrand(((BrandPOJO)mBrandSpinner.getSelectedItem()));
            mFlowObject.setSelectedUnitType((String) mUnitTypeSpinner.getSelectedItem());
            mFlowObject.setSelectedBoxColor(mBoxColorButton.getText().toString());
            mFlowObject.setSelectedTractorColor(mTractorUnitColorButton.getText().toString());

            intent.putExtra(FLOW_OBJECT_EXTRA, mFlowObject);
        }

        startActivity(intent);

    }

    public void showColorDialog(final Button button, int title) {
        final ColorPickerDialog.Builder builder = new ColorPickerDialog.Builder(this);
        builder.setFlagView(new CustomFlag(this, R.layout.dialog_color_flash_view));
        builder.setTitle(getString(R.string.color_dialog_title).replace("%", getString(title)));


        builder.setPositiveButton(getString(R.string.accept), new ColorEnvelopeListener() {
            @Override
            public void onColorSelected(ColorEnvelope envelope, boolean fromUser) {
                button.setText("#" + envelope.getHexCode().substring(2));
                button.getBackground().mutate().setColorFilter(envelope.getColor(), PorterDuff.Mode.SRC_ATOP);
                //button.setBackgroundColor(envelope.getColor());
                hideKeyboard(CaptureBrandAndColorsActivity.this);
            }
        });
        builder.setNegativeButton(getString(R.string.cancel), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                hideKeyboard(CaptureBrandAndColorsActivity.this);
            }
        });
        builder.attachBrightnessSlideBar(); // attach BrightnessSlideBar
        builder.show(); // show dialog
    }
}
