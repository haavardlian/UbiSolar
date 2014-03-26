package com.sintef_energy.ubisolar.adapter;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.sintef_energy.ubisolar.R;
import com.sintef_energy.ubisolar.model.Residence;

import java.util.List;

/**
 * Created by baier on 3/25/14.
 */
public class SimilarAdapter extends ArrayAdapter<Residence> {

    protected Context context;
    protected int resource;
    List<Residence> residences = null;

    public SimilarAdapter(Context context, int resource, List<Residence> residences) {
        super(context, resource, residences);

        this.context = context;
        this.resource = resource;
        this.residences = residences;
    }

    @Override
    public void add(Residence object) {
        residences.add(object);
        notifyDataSetChanged();
    }

    @Override
    public void clear() {
        residences.clear();
        notifyDataSetChanged();
    }

    @Override
    public Residence getItem(int position) {
        return residences.get(position);
    }

    @Override
    public int getCount() {
        return residences.size();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View row = convertView;
        SimilarHolder holder = null;

        if(row == null) {
            LayoutInflater inflater = ((Activity)context).getLayoutInflater();
            row = inflater.inflate(resource, parent, false);

            holder = new SimilarHolder();
            holder.name = (TextView)row.findViewById(R.id.social_user_name);

            row.setTag(holder);
        } else {
            holder = (SimilarHolder)row.getTag();
        }

        if(!residences.isEmpty()) {
            Residence residence = residences.get(position);
            holder.name.setText(residence.getHouseId());
        }
        return row;
    }

    static class SimilarHolder {
        TextView name;
    }

}

