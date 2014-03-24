package com.sintef_energy.ubisolar.adapter;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.TextView;

import com.sintef_energy.ubisolar.R;
import com.sintef_energy.ubisolar.model.Tip;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Håvard on 20.03.14.
 */
public class YourAdapter extends ArrayAdapter<Tip> {
    Context context;
    int layoutResourceId;
    List<Tip> data = null;

    public YourAdapter(Context context, int layoutResourceId, ArrayList<Tip> data) {
        super(context, layoutResourceId);
        this.context = context;
        this.layoutResourceId = layoutResourceId;
        this.data = data;
    }

    public Activity getActivity() {
        return (Activity) context;
    }

    @Override
    public void add(Tip object) {
        data.add(object);
        notifyDataSetChanged();
    }

    @Override
    public void remove(Tip object) {
        data.remove(object);
        notifyDataSetChanged();
    }

    @Override
    public void clear() {
        data.clear();
        notifyDataSetChanged();
    }

    @Override
    public Tip getItem(int position) {
        return data.get(position);
    }

    @Override
    public int getCount() {
        return data.size();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View row = convertView;
        TipHolder holder = null;

        if(row == null) {
            LayoutInflater inflater = ((Activity)context).getLayoutInflater();
            row = inflater.inflate(layoutResourceId, parent, false);

            holder = new TipHolder();
            holder.name = (TextView)row.findViewById(R.id.yourTipsRowName);
            holder.checked = (CheckBox) row.findViewById(R.id.yourTipsRowChecked);

            row.setTag(holder);
        } else {
            holder = (TipHolder)row.getTag();
        }

        if(!data.isEmpty()) {
            Tip tip = data.get(position);
            holder.name.setText(tip.getName());
            holder.checked.setChecked(true);
        }

        return row;
    }

    static class TipHolder {
        TextView name;
        CheckBox checked;
    }
}
