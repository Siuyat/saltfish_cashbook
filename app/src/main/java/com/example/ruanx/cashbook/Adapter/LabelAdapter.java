package com.example.ruanx.cashbook.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.example.ruanx.cashbook.R;

/**
 * Created by SIRIUS on 2017/12/28.
 */

public class LabelAdapter extends ArrayAdapter<String> {
    private int resoureId;
    public LabelAdapter(Context context, int textViewResourceId, String[] objects){
        super(context,textViewResourceId,objects);
        resoureId = textViewResourceId;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent){
        String str = getItem(position);
        View view = LayoutInflater.from(getContext()).inflate(resoureId,parent,false);
        TextView labeltv = (TextView)view.findViewById(R.id.label_ll_tv);
        labeltv.setText(str);
        return view;
    }
}
