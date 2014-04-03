package com.sintef_energy.ubisolar.adapter;


import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.TextView;

import com.sintef_energy.ubisolar.R;
import com.sintef_energy.ubisolar.model.Residence;

public class ResidenceListAdapter extends BaseExpandableListAdapter {

    private Activity context;
    private List<Residence> residences;

    public ResidenceListAdapter (Activity context, List<Residence> residences) {
        this.context = context;
        this.residences = residences;
    }

    @Override
    public Object getChild(int groupPosition, int childPosition) {
        return residences.get(groupPosition);
    }

    public String getDescription(int groupPosition) {
        return residences.get(groupPosition).getDescription();
    }

    @Override
    public int getChildrenCount(int groupPosition) {
        return 1;
    }

    @Override
    public long getChildId(int groupPosition, int childPosition) {
        return childPosition;
    }

    public View getChildView(final int groupPosition, final int childPosition,
                             boolean isLastChild, View convertView, ViewGroup parent) {
        final Residence residence = (Residence)getChild(groupPosition, childPosition);

        LayoutInflater inflater = context.getLayoutInflater();

        if (convertView == null) {
            convertView = inflater.inflate(R.layout.profile_residence_expanded, null);
        }



        TextView descriptionView = (TextView) convertView.findViewById(R.id.residence_description);
        TextView idView = (TextView) convertView.findViewById(R.id.residence_name);
        TextView residentsView = (TextView) convertView.findViewById(R.id.residence_residents);
        TextView areaView = (TextView) convertView.findViewById(R.id.residence_size);

        descriptionView.setText("Description: " + residence.getDescription());
        idView.setText("Name: " + residence.getHouseId());
        areaView.setText("Area: " + residence.getArea() + " square meters");
        residentsView.setText("Residents: " + residence.getResidents());

        return convertView;
    }



    public Object getGroup(int groupPosition) {
        return residences.get(groupPosition);
    }

    public int getGroupCount() {
        return residences.size();
    }

    public long getGroupId(int groupPosition) {
        return groupPosition;
    }

    public View getGroupView(int groupPosition, boolean isExpanded,View convertView, ViewGroup parent) {
        String residenceName =  getGroup(groupPosition).toString();
        if (convertView == null) {
            LayoutInflater infalInflater = (LayoutInflater) context
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = infalInflater.inflate(R.layout.profile_recidence_item,
                    null);
        }
        TextView item = (TextView) convertView.findViewById(R.id.residence_name);
        item.setTypeface(null, Typeface.BOLD);
        item.setText(residenceName);
        return convertView;
    }

    public boolean hasStableIds() {
        return true;
    }

    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return true;
    }
}