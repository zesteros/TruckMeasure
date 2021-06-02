package com.mx.vise.cubicaciones.adapters;

import android.content.Context;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.mx.vise.cubicaciones.R;
import com.mx.vise.cubicaciones.pojos.SyndicatePOJO;

import java.util.ArrayList;
import java.util.List;

public class SyndicateAdapter extends ArrayAdapter<SyndicatePOJO> {

    private static final String TAG = "VISE";
    private Context mContext;
    private List<SyndicatePOJO> mSyndicateList = new ArrayList<>();

    public SyndicateAdapter(@NonNull Context context, ArrayList<SyndicatePOJO> list) {
        super(context, R.layout.spinner_custom_item, list);
        mContext = context;
        mSyndicateList = list;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        View listItem = convertView;
        if (listItem == null)
            listItem = LayoutInflater.from(mContext).inflate(R.layout.spinner_custom_item, parent, false);

        SyndicatePOJO currentSyndicate = mSyndicateList.get(position);

        TextView name = listItem.findViewById(R.id.spinnerItemTextView);
        name.setText(currentSyndicate.getName());


        return listItem;
    }

    public View getDropDownView(int position, View convertView, ViewGroup parent) {
        View listItem = convertView;
        if (listItem == null)
            listItem = LayoutInflater.from(mContext).inflate(R.layout.spinner_custom_dropdown_item, parent, false);

        SyndicatePOJO currentSyndicate = mSyndicateList.get(position);
        TextView name = listItem.findViewById(R.id.spinnerItemTextView);
        name.setText(currentSyndicate.getName());


        return listItem;
    }

}
