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

import com.sintef_energy.ubisolar.R;
import com.sintef_energy.ubisolar.adapter.NavDrawerListAdapter;

/**
 * Created by Håvard on 02.04.14.
 */
public class DrawerItem implements Item {
    private String         str1;
    private String         str2;

    private DrawerHolder holder;

    public DrawerItem(String title, String count) {
        this.str1 = title;
        this.str2 = count;
    }

    public DrawerItem(String title) {
        this.str1 = title;
    }

    @Override
    public int getViewType() {
        return NavDrawerListAdapter.RowType.LIST_ITEM.ordinal();
    }

    @Override
    public View getView(LayoutInflater inflater, View convertView, ViewGroup parent) {
        View view;
        holder = new DrawerHolder();

        if (convertView == null) {
            view = inflater.inflate(R.layout.drawer_list_item, parent, false);
            // Do some initialization
        } else {
            view = convertView;
        }

        holder.text = (TextView) view.findViewById(R.id.title);
        holder.count = (TextView) view.findViewById(R.id.device_group_counter);
        holder.text.setText(str1);

        setCount(str2);

        return view;
    }

    public void setTitle(String str1) {
        this.str1 = str1;
    }

    public void setCount(String str){
        str2 = str;

        if(holder == null)
            return;

        if(holder.count == null)
            return;

        if(str != null && str.length() > 0) {
            holder.count.setVisibility(View.VISIBLE);
            holder.count.setText(str);
        }
        else
            holder.count.setVisibility(View.GONE);
    }

    static class DrawerHolder{
        TextView text;
        TextView count;
    }
}
