package com.mx.vise.cubicaciones.adapters;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.mx.vise.cubicaciones.R;
import com.mx.vise.cubicaciones.pojos.BrandPOJO;

import java.util.ArrayList;
import java.util.List;

public class BrandAdapter extends ArrayAdapter<BrandPOJO> {
    private static final String TAG = "VISE";
    private Context mContext;
    private List<BrandPOJO> list = new ArrayList<>();

    public BrandAdapter(@NonNull Context context, ArrayList<BrandPOJO> list) {
        super(context, R.layout.spinner_custom_item, list);
        mContext = context;
        this.list = list;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        View listItem = convertView;
        if (listItem == null)
            listItem = LayoutInflater.from(mContext).inflate(R.layout.spinner_custom_item, parent, false);

        BrandPOJO currentItem = list.get(position);

        TextView name = listItem.findViewById(R.id.spinnerItemTextView);
        name.setText(currentItem.getName());


        return listItem;
    }

    public View getDropDownView(int position, View convertView, ViewGroup parent) {
        View listItem = convertView;
        if (listItem == null)
            listItem = LayoutInflater.from(mContext).inflate(R.layout.spinner_custom_dropdown_item, parent, false);

        BrandPOJO currentItem = list.get(position);
        TextView name = listItem.findViewById(R.id.spinnerItemTextView);
        name.setText(currentItem.getName());


        return listItem;
    }

}
