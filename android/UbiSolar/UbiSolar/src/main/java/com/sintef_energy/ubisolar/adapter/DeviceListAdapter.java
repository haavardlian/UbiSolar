package com.sintef_energy.ubisolar.adapter;


import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;

import android.widget.FrameLayout;
import android.widget.ImageView;

import android.widget.ExpandableListView;
import android.widget.TextView;

import com.sintef_energy.ubisolar.R;
import com.sintef_energy.ubisolar.database.energy.DeviceModel;

public class DeviceListAdapter extends BaseExpandableListAdapter {

    private Activity context;
    private List<DeviceModel> devices;
    private String[] categories;
    public static final String TAG = DeviceListAdapter.class.getName();

    public DeviceListAdapter(Activity context, List<DeviceModel> devices) {
        this.context = context;
        this.devices = devices;
        this.categories = context.getResources().getStringArray(R.array.device_categories);
    }

    @Override
    public DeviceModel getChild(int groupPosition, int childPosition) {
        ArrayList<DeviceModel> children = new ArrayList<>();
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
        final DeviceModel device = getChild(groupPosition, childPosition);

        LayoutInflater inflater = context.getLayoutInflater();

        if (convertView == null) {
            convertView = inflater.inflate(R.layout.device_child_item, parent, false);
        }

        TextView nameView = (TextView) convertView.findViewById(R.id.device_name);
        nameView.setText(device.getName());
        //TextView idView = (TextView) convertView.findViewById(R.id.deviceID);

        //Only show description if the device actually got a description
        if (device.getDescription().length() > 1){
            TextView descriptionView = (TextView) convertView.findViewById(R.id.device_description);
            descriptionView.setText(device.getDescription());
        }

        //idView.setText("ID: " + device.getId());
        return convertView;
    }



    public String getGroup(int groupPosition) {
        return categories[groupPosition];
    }

    public int getGroupCount() { return categories.length; }

    public long getGroupId(int groupPosition) {
        return groupPosition;
    }

    public View getGroupView(int groupPosition, boolean isExpanded,View convertView, ViewGroup parent) {

        int childrenCount = getChildrenCount(groupPosition);

        if (convertView == null) {
            LayoutInflater layoutInflater = (LayoutInflater) context
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = layoutInflater.inflate(R.layout.device_list_item, null);
        }

        TextView category = (TextView) convertView.findViewById(R.id.device_category);
        ImageView icon = (ImageView) convertView.findViewById(R.id.item_icon);
        TextView counter = (TextView) convertView.findViewById(R.id.device_group_counter);

        //Set the icon to the right icon
        switch (groupPosition) {
            case 0:
                icon.setImageResource(R.drawable.appliances);
                break;
            case 1:
                icon.setImageResource(R.drawable.heat);
                break;
            case 2:
                icon.setImageResource(R.drawable.home_entertainment);
                break;
            case 3:
                //TODO Need lighting icon, default for now
                icon.setImageResource(R.drawable.default_device);
                break;
            case 4:
                icon.setImageResource(R.drawable.default_device);
                break;
            case 5:
                icon.setImageResource(R.drawable.power_production);
                break;

        }
        category.setText(categories[groupPosition]);
        if(childrenCount < 1)
            counter.setVisibility(View.INVISIBLE);
        else {
            counter.setText("" + childrenCount);
            counter.setVisibility(View.VISIBLE);
        }

        return convertView;
    }

    public boolean hasStableIds() {
        return true;
    }

    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return false;
    }
}