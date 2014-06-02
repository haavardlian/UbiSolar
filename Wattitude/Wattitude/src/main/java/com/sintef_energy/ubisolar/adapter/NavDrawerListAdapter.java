/*
 * Licensed to UbiCollab.org under one or more contributor
 * license agreements.  See the NOTICE file distributed
 * with this work for additional information regarding
 * copyright ownership. UbiCollab.org licenses this file
 * to you under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance
 * with the License. You may obtain a copy of the License at
 *
 * 	http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

package com.sintef_energy.ubisolar.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;

import com.sintef_energy.ubisolar.Interfaces.IDrawerItem;

import java.util.List;

/**
 * Adapter for managing the navigation drawer
 */
public class NavDrawerListAdapter extends ArrayAdapter<IDrawerItem> {
    private LayoutInflater mInflater;
    private List<IDrawerItem> mItems;
    public enum RowType { LIST_ITEM, HEADER_ITEM }

    public NavDrawerListAdapter(Context context, List<IDrawerItem> items) {
        super(context, 0, items);
        this.mItems = items;
        mInflater = LayoutInflater.from(context);
    }

    @Override
    public int getViewTypeCount() {
        return RowType.values().length;
    }

    @Override
    public int getItemViewType(int position) {
        return getItem(position).getViewType();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        return getItem(position).getView(mInflater, convertView, parent);
    }

    @Override
    public IDrawerItem getItem(int position) {
        return mItems.get(position);
    }

    @Override
    public boolean isEnabled(int position) {
        if(mItems.get(position).getViewType() == RowType.HEADER_ITEM.ordinal())
            return false;
        else
            return true;
    }
}
