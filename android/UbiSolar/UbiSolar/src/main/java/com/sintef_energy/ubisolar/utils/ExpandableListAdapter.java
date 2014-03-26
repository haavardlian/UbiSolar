package com.sintef_energy.ubisolar.utils;


import java.util.List;
import java.util.Map;

import android.app.Activity;
import android.content.Context;
import android.graphics.Typeface;
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

    public ExpandableListAdapter(Activity context, List<DeviceModel> devices) {
        this.context = context;
        this.devices = devices;
    }

    @Override
    public Object getChild(int groupPosition, int childPosition) {
        return devices.get(groupPosition);
    }

    public String getDescription(int groupPosition) {
        return devices.get(groupPosition).getDescription();
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
        final DeviceModel device = (DeviceModel)getChild(groupPosition, childPosition);

        LayoutInflater inflater = context.getLayoutInflater();

        if (convertView == null) {
            convertView = inflater.inflate(R.layout.device_child_item, null);
        }

        TextView descriptionView = (TextView) convertView.findViewById(R.id.deviceDescription);
        TextView idView = (TextView) convertView.findViewById(R.id.deviceID);

        descriptionView.setText("Description: " + device.getDescription());
        idView.setText("ID: " + device.getDevice_id());
        return convertView;
    }



    public Object getGroup(int groupPosition) {
        return devices.get(groupPosition);
    }

    public int getGroupCount() {
        return devices.size();
    }

    public long getGroupId(int groupPosition) {
        return groupPosition;
    }

    public View getGroupView(int groupPosition, boolean isExpanded,View convertView, ViewGroup parent) {
        String deviceName =  getGroup(groupPosition).toString();
        if (convertView == null) {
            LayoutInflater infalInflater = (LayoutInflater) context
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = infalInflater.inflate(R.layout.device_list_item,
                    null);
        }
        TextView item = (TextView) convertView.findViewById(R.id.device);
        item.setTypeface(null, Typeface.BOLD);
        item.setText(deviceName);
        return convertView;
    }

    public boolean hasStableIds() {
        return true;
    }

    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return true;
    }
}