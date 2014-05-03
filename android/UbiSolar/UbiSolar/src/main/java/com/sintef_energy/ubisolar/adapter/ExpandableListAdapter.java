package com.sintef_energy.ubisolar.adapter;


import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.graphics.Typeface;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;

import android.widget.ImageView;

import android.widget.ExpandableListView;
import android.widget.TextView;

import com.sintef_energy.ubisolar.R;
import com.sintef_energy.ubisolar.database.energy.DeviceModel;
import com.sintef_energy.ubisolar.dialogs.EditDeviceDialog;

public class ExpandableListAdapter extends BaseExpandableListAdapter implements ExpandableListView.OnChildClickListener {

    private Activity context;
    private List<DeviceModel> devices;
    private String[] categories;
    public static final String TAG = ExpandableListAdapter.class.getName();

    public ExpandableListAdapter(Activity context, List<DeviceModel> devices) {
        this.context = context;
        this.devices = devices;
        this.categories = context.getResources().getStringArray(R.array.device_categories);
    }

    @Override
    public DeviceModel getChild(int groupPosition, int childPosition) {
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

        //Only show description if the device actually got a description
        if (device.getDescription().length() > 1){
            TextView descriptionView = (TextView) convertView.findViewById(R.id.deviceDescription);
            descriptionView.setText("Description: " + device.getDescription());
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

        if (convertView == null) {
            LayoutInflater infalInflater = (LayoutInflater) context
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = infalInflater.inflate(R.layout.device_list_item,
                    null);
        }
        TextView item = (TextView) convertView.findViewById(R.id.device);
        ImageView icon = (ImageView) convertView.findViewById(R.id.item_icon);

        //Set the icon to the right icon
        //Log.d(TAG, "The groupPosition is: " + groupPosition + "The category corresponding is: " + categories[groupPosition]);
        switch (groupPosition){
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

    public boolean onChildClick(ExpandableListView expListView, View view, int groupPosition, int childPosition, long id) {
        // TODO Auto-generated method stub
        Log.d(TAG, "The devicemodel selected: " + getChild(groupPosition, childPosition).getName());
        EditDeviceDialog editDeviceDialog = new EditDeviceDialog(getChild(groupPosition, childPosition));
        editDeviceDialog.show(context.getFragmentManager(), TAG);
        return true;
    }
}