package com.sintef_energy.ubisolar.utils;


import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import android.app.Activity;
import android.content.Context;
import android.graphics.Typeface;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.TextView;

import com.sintef_energy.ubisolar.R;
import com.sintef_energy.ubisolar.database.energy.DeviceModel;

public class ExpandableListAdapter extends BaseExpandableListAdapter {

    private Activity context;
    private List<DeviceModel> devices;
    //Changing categories from string[] to a list - or create help method to add all the categories to devices?
    private String[] categories;
    public static final String TAG = ExpandableListAdapter.class.getName();

    //Denne skal ta inn cursoren i steden for lista i seg selv?
    public ExpandableListAdapter(Activity context, List<DeviceModel> devices) {
        this.context = context;
        this.devices = devices;
        this.categories = context.getResources().getStringArray(R.array.device_categories);
    }

    @Override
    public Object getChild(int groupPosition, int childPosition) {
        //TODO fix this
        //Trying to create a list of all children in this group and picking the right one
        ArrayList<DeviceModel> children = new ArrayList<DeviceModel>();
        for (DeviceModel deviceModel : devices){
            if (deviceModel.getCategory() == groupPosition)
                children.add(deviceModel);
        }
        return children.get(childPosition);
    }

    public String getDescription(int groupPosition) {
        return devices.get(groupPosition).getDescription();
    }

    @Override
    public int getChildrenCount(int groupPosition) {
        int count = 0;
        for (DeviceModel deviceModel : devices){
            if (deviceModel.getCategory() == groupPosition){
                count++;
            }
        }
        return count;

    }

    @Override
    public long getChildId(int groupPosition, int childPosition) {
        return childPosition;
    }

    public View getChildView(final int groupPosition, final int childPosition,
                             boolean isLastChild, View convertView, ViewGroup parent) {
        final DeviceModel device = (DeviceModel)getChild(groupPosition, childPosition);

        LayoutInflater inflater = context.getLayoutInflater();

        if (convertView == null) {
            convertView = inflater.inflate(R.layout.device_child_item, null);
        }

        TextView nameView = (TextView) convertView.findViewById(R.id.deviceName);
        nameView.setText(device.getName());
        //TextView idView = (TextView) convertView.findViewById(R.id.deviceID);

        //Only show description if the device actually got it
        if (device.getDescription().length() > 1){
            TextView descriptionView = (TextView) convertView.findViewById(R.id.deviceDescription);
            descriptionView.setText("Description: " + device.getDescription());
        }

        //idView.setText("ID: " + device.getDevice_id());
        return convertView;
    }



    public Object getGroup(int groupPosition) {
        //TODO fix this
        return devices.get(groupPosition);
    }

    public int getGroupCount() { return categories.length; }

    public long getGroupId(int groupPosition) {
        return groupPosition;
    }

    public View getGroupView(int groupPosition, boolean isExpanded,View convertView, ViewGroup parent) {

        if (convertView == null) {
            LayoutInflater infalInflater = (LayoutInflater) context
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = infalInflater.inflate(R.layout.device_list_item,
                    null);
        }
        TextView item = (TextView) convertView.findViewById(R.id.device);
        item.setText(categories[groupPosition]);
        item.setTypeface(null, Typeface.BOLD);

        return convertView;

    }

    public boolean hasStableIds() {
        return true;
    }

    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return true;
    }
}