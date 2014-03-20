package com.sintef_energy.ubisolar;

import android.app.Activity;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.sintef_energy.ubisolar.structs.Tip;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Håvard on 20.03.14.
 */
public class TipAdapter extends ArrayAdapter<Tip> {
    Context context;
    int layoutResourceId;
    List<Tip> data = null;

    public TipAdapter(Context context, int layoutResourceId, ArrayList<Tip> data) {
        super(context, layoutResourceId);
        this.context = context;
        this.layoutResourceId = layoutResourceId;
        this.data = data;
    }

    @Override
    public void add(Tip object) {
        data.add(object);
    }

    @Override
    public void clear() {
        data.clear();
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
            holder.name = (TextView)row.findViewById(R.id.tipRowName);
            holder.description = (TextView)row.findViewById(R.id.tipRowDescription);
            holder.rating = (TextView)row.findViewById(R.id.tipRowRating);

            row.setTag(holder);
        } else {
            holder = (TipHolder)row.getTag();
        }

        if(!data.isEmpty()) {
            Tip tip = data.get(position);
            holder.name.setText(tip.getName());
            holder.description.setText(tip.getDescription());
            Log.d("ADAPTER", holder.name.getText().toString());
            holder.rating.setText("Rating: " + tip.getAverageRating());
        }
        return row;
    }

    static class TipHolder {
        TextView name;
        TextView description;
        TextView rating;
    }
}
