package com.sintef_energy.ubisolar.adapter;


import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.ExpandableListView;
import android.widget.TextView;


import com.sintef_energy.ubisolar.R;
import com.sintef_energy.ubisolar.model.Residence;
import com.sintef_energy.ubisolar.preferences.PreferencesManager;

public class ResidenceListAdapter extends BaseExpandableListAdapter implements ExpandableListView.OnChildClickListener{

    private Activity context;
    private List<Residence> residences;
    private Residence selectedResidence;
    private int selectedIndex;

    public ResidenceListAdapter (Activity context, List<Residence> residences) {
        this.context = context;
        this.residences = residences;
        restoreSelctedResidence();
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

        idView.setText("Name: " + residence.getStatus());
        descriptionView.setText("Description: " + residence.getDescription());
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
        Residence res = (Residence) getGroup(groupPosition);
        String residenceName =  res.getStatus();
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

    @Override
    public boolean onChildClick(ExpandableListView parent, View v, int groupPosition,
                                int childPosition, long id) {
        Residence a = (Residence) getChild(groupPosition,childPosition);
        setSelectedResidence(a);
        return true;
    }

    public void setSelectedResidence(Residence residence) {
        for(Residence r : residences) {
            if(r.getHouseName().equals(residence.getHouseName())) {

                if(selectedResidence != null) {
                    residences.get(selectedIndex).setStatus(residences.get(selectedIndex).getHouseName());
                    r.setStatus(r.getHouseName() + " [Selected]");
                    selectedIndex = residences.indexOf(r);
                    PreferencesManager.getInstance().setSelectedResidence(r.getHouseName());
                }
                else {
                    r.setStatus(r.getHouseName() + " [Selected]");
                    selectedResidence = r;
                    selectedIndex = residences.indexOf(r);
                }
            }
        }
    }

    private void restoreSelctedResidence() {
        for(Residence r : residences) {
            if(r.getHouseName().equals(PreferencesManager.getInstance().getSelectedResidence())) {
                setSelectedResidence(r);
                selectedIndex = residences.indexOf(r);
            }
        }


    }
}

