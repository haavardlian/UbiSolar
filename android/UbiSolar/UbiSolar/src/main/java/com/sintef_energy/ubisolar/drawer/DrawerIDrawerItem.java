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

package com.sintef_energy.ubisolar.drawer;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.sintef_energy.ubisolar.Interfaces.IDrawerItem;
import com.sintef_energy.ubisolar.R;
import com.sintef_energy.ubisolar.adapter.NavDrawerListAdapter;

/**
 * Created by Håvard on 02.04.14.
 *
 * IDrawerItem in the navigation bar
 */
public class DrawerIDrawerItem implements IDrawerItem {
    private String mTitle;
    private String mCount;
    private DrawerHolder mHolder;

    public DrawerIDrawerItem(String title) {
        this.mTitle = title;
    }

    @Override
    public int getViewType() {
        return NavDrawerListAdapter.RowType.LIST_ITEM.ordinal();
    }

    @Override
    public View getView(LayoutInflater inflater, View convertView, ViewGroup parent) {
        View view;
        mHolder = new DrawerHolder();

        if (convertView == null) {
            view = inflater.inflate(R.layout.drawer_list_item, parent, false);
        } else {
            view = convertView;
        }

        mHolder.text = (TextView) view.findViewById(R.id.title);
        mHolder.count = (TextView) view.findViewById(R.id.device_group_counter);
        mHolder.text.setText(mTitle);

        setCount(mCount);

        return view;
    }

    public void setTitle(String title) {
        this.mTitle = title;
    }

    public void setCount(String count){
        mCount = count;

        if(mHolder == null)
            return;

        if(mHolder.count == null)
            return;

        if(count != null && count.length() > 0) {
            mHolder.count.setVisibility(View.VISIBLE);
            mHolder.count.setText(count);
        }
        else
            mHolder.count.setVisibility(View.GONE);
    }

    /**
     * Holder for drawer item views
     */
    static class DrawerHolder{
        TextView text;
        TextView count;
    }
}
