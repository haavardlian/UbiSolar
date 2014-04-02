package com.sintef_energy.ubisolar.adapter;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

import com.sintef_energy.ubisolar.R;
import com.sintef_energy.ubisolar.model.Residence;
import com.sintef_energy.ubisolar.model.ResidenceAttributes;

import java.util.List;

/**
 * Created by baier on 3/25/14.
 */
public class ComparisonAdapter extends ArrayAdapter<ResidenceAttributes> {

    protected Context context;
    protected int resource;
    List<ResidenceAttributes> residenceAttr = null;

    public ComparisonAdapter(Context context, int resource, List<ResidenceAttributes> residenceAttr) {
        super(context, resource);

        this.context = context;
        this.resource = resource;
        this.residenceAttr = residenceAttr;
    }

    @Override
    public void add(ResidenceAttributes object) {
        residenceAttr.add(object);
        notifyDataSetChanged();
    }

    @Override
    public void clear() {
        residenceAttr.clear();
        notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        return residenceAttr.size();
    }

    @Override
    public ResidenceAttributes getItem(int position) {
        return residenceAttr.get(position);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View row = convertView;
        ComparisonHolder holder = null;

        if(row == null) {

            LayoutInflater inflater = ((Activity)context).getLayoutInflater();
            row = inflater.inflate(resource, parent, false);

            holder = new ComparisonHolder();
            holder.name = (TextView)row.findViewById(R.id.social_similar_row_name);
            holder.checkBox = (CheckBox) row.findViewById(R.id.checkBox);

            row.setTag(holder);
        } else {
            holder = (ComparisonHolder)row.getTag();
        }

        if(!residenceAttr.isEmpty()) {
            ResidenceAttributes residenceAttribute = residenceAttr.get(position);
            holder.name.setText(residenceAttribute.getResidenceAttributeLabel());
        }
        return row;
    }

    static class ComparisonHolder {
        TextView name;
        CheckBox checkBox;
    }

}