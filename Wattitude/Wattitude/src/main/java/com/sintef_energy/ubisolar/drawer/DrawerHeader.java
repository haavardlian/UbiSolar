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

package com.sintef_energy.ubisolar.drawer;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.sintef_energy.ubisolar.Interfaces.IDrawerItem;
import com.sintef_energy.ubisolar.R;
import com.sintef_energy.ubisolar.adapter.NavDrawerListAdapter;

/**
 * The header item in the navigation drawer
 */
public class DrawerHeader implements IDrawerItem {
    private final String mName;

    public DrawerHeader(String name) {
        this.mName = name;
    }

    @Override
    public int getViewType() {
        return NavDrawerListAdapter.RowType.HEADER_ITEM.ordinal();
    }

    @Override
    public View getView(LayoutInflater inflater, View convertView, ViewGroup parent) {
        View view;
        if (convertView == null) {
            view = inflater.inflate(R.layout.drawer_list_header, parent, false);
        } else {
            view = convertView;
        }
        view.setClickable(false);
        view.setFocusable(false);

        TextView text = (TextView) view.findViewById(R.id.title);
        text.setText(mName);

        return view;
    }
}
