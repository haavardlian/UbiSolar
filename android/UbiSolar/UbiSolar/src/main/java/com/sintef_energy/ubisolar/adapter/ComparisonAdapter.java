package com.sintef_energy.ubisolar.adapter;

import android.app.Activity;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.TextView;

import com.sintef_energy.ubisolar.R;
import com.sintef_energy.ubisolar.model.ResidenceAttributes;
import com.sintef_energy.ubisolar.preferences.PreferencesManager;

import java.util.List;

/**
 * Created by baier on 3/25/14.
 */
public class ComparisonAdapter extends ArrayAdapter<ResidenceAttributes> {

    private static final String TAG = ComparisonAdapter.class.getName();

    protected Context context;
    protected int resource;
    List<ResidenceAttributes> residenceAttr = null;
    private static final String AREA = "Area";
    private static final String SIZE = "Resident size";
    private static final String RES = "Number of residents";
    private static final String ENERGY = "Energy class";


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
            holder.name = (TextView)row.findViewById(R.id.comparison_setting_label);
            holder.checkBox = (CheckBox) row.findViewById(R.id.checkBox);


            if(!residenceAttr.isEmpty()) {
                ResidenceAttributes residenceAttribute = residenceAttr.get(position);
                holder.name.setText(residenceAttribute.getResidenceAttributeLabel());
                holder.checkBox.setSelected(residenceAttribute.isSelected());
            }


            holder.checkBox.setTag(R.id.checkBox, holder.name.getText());
            holder.checkBox.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    CheckBox cb = (CheckBox) v;

                    setTextSetting((String)v.getTag(R.id.checkBox), cb.isChecked());
                }
            });

            row.setTag(holder);

        } else {
            holder = (ComparisonHolder)row.getTag();
        }

        loadPreferences(holder.checkBox, (String) holder.name.getText());

        return row;
    }

    static class ComparisonHolder {
        TextView name;
        CheckBox checkBox;
    }


    private void setTextSetting(String text, boolean state){

        Log.v(TAG, text + "  " + state);

        PreferencesManager pref = PreferencesManager.getInstance();

        if(AREA.equals(text)) {
            pref.setComparisonAreaChecked(state);
        }
        else if(SIZE.equals(text)) {
            pref.setComparisonSizeChecked(state);
        }
        else if(ENERGY.equals(text)) {
            pref.setComparisonEnergyChecked(state);
        }
        else if(RES.equals(text)) {
            pref.setComparisonResidentsChecked(state);
        }
    }

    public void loadPreferences (CheckBox cb, String text) {
        PreferencesManager pref = PreferencesManager.getInstance();

        Log.v(TAG, "CHECKING " + text);

        boolean value = false;

        if(AREA.equals(text)) {
            value = pref.getComparisonAreaChecked();
        }
        else if(SIZE.equals(text)) {
            value = pref.getComparisonSizeChecked();
        }
        else if(ENERGY.equals(text)) {
            value = pref.getComparisonEnergyChecked();
        }
        else if(RES.equals(text)) {
            value = pref.getComparisonResidentsChecked();
        }

        cb.setChecked(value);
    }

}