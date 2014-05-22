/*
 * Licensed to UbiCollab.org under one or more contributor
 * license agreements.  See the NOTICE file distributed
 * with this work for additional information regarding
 * copyright ownership. UbiCollab.org licenses this file
 * to you under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance
 * with the License. You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

package com.sintef_energy.ubisolar.adapter;

import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.content.res.TypedArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.sintef_energy.ubisolar.R;
import com.sintef_energy.ubisolar.database.energy.DeviceModel;

/**
 * Adapter managing the the devices
 */
public class DeviceListAdapter extends BaseExpandableListAdapter{
    public static final String TAG = DeviceListAdapter.class.getName();

    private Activity mContext;
    private List<DeviceModel> devices;
    private String[] mCategories;

    public DeviceListAdapter(Activity context, List<DeviceModel> devices) {
        this.mContext = context;
        this.devices = devices;
        this.mCategories = context.getResources().getStringArray(R.array.device_categories);
    }

    @Override
    public DeviceModel getChild(int groupPosition, int childPosition) {
        int count = 0;
        for (DeviceModel deviceModel : devices){
            if (deviceModel.getCategory() == groupPosition) {
                if(count++ >= childPosition)
                    return deviceModel;
            }
        }
        return null;
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

    @Override
    public View getChildView(int groupPosition, int childPosition,
                             boolean isLastChild, View convertView, ViewGroup parent) {

        DeviceModel device = getChild(groupPosition, childPosition);
        LayoutInflater inflater = mContext.getLayoutInflater();

        if (convertView == null) {
            convertView = inflater.inflate(R.layout.device_child_item, parent, false);
        }

        TextView nameView = (TextView) convertView.findViewById(R.id.device_name);
        nameView.setText(device.getName());

        //Only show description if the field is not empty
        if (device.getDescription().length() > 1){
            TextView descriptionView = (TextView) convertView.findViewById(R.id.device_description);
            descriptionView.setText(device.getDescription());
        }

        return convertView;
    }

    @Override
    public String getGroup(int groupPosition) {
        return mCategories[groupPosition];
    }

    @Override
    public int getGroupCount() { return mCategories.length; }

    @Override
    public long getGroupId(int groupPosition) {
        return groupPosition;
    }

    @Override
    public View getGroupView(int groupPosition, boolean isExpanded,View convertView, ViewGroup parent) {
        int childrenCount = getChildrenCount(groupPosition);

        if (convertView == null) {
            LayoutInflater layoutInflater = (LayoutInflater) mContext
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = layoutInflater.inflate(R.layout.device_list_item, parent, false);
        }

        //Populate the fields
        TextView category = (TextView) convertView.findViewById(R.id.device_category);
        ImageView icon = (ImageView) convertView.findViewById(R.id.item_icon);
        TextView counter = (TextView) convertView.findViewById(R.id.device_group_counter);

        TypedArray icons = mContext.getResources().obtainTypedArray(R.array.device_icons);
        icon.setImageResource(icons.getResourceId(groupPosition, 3));
        category.setText(mCategories[groupPosition]);

        //Sets or hides the device counter if it is empty
        if(childrenCount < 1)
            counter.setVisibility(View.INVISIBLE);
        else {
            counter.setText("" + childrenCount);
            counter.setVisibility(View.VISIBLE);
        }

        return convertView;
    }

    @Override
    public boolean hasStableIds() {
        return true;
    }

    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return true;
    }
}