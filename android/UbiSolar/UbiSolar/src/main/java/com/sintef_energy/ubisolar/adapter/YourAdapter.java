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

import android.app.Activity;
import android.app.FragmentManager;
import android.content.Context;
import android.content.SharedPreferences;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.TextView;

import com.sintef_energy.ubisolar.R;
import com.sintef_energy.ubisolar.dialogs.YourDialog;
import com.sintef_energy.ubisolar.model.Tip;
import com.sintef_energy.ubisolar.preferences.PreferencesManager;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Håvard on 20.03.14.
 */
public class YourAdapter extends ArrayAdapter<Tip> {
    private Context context;
    private FragmentManager fragmentManager;
    private int layoutResourceId;
    private List<Tip> data = null;
    private SharedPreferences sharedPreferences;

    public YourAdapter(Context context, int layoutResourceId, ArrayList<Tip> data, FragmentManager fragmentManager) {
        super(context, layoutResourceId);
        this.context = context;
        this.layoutResourceId = layoutResourceId;
        this.data = data;
        this.fragmentManager = fragmentManager;
        this.sharedPreferences = context.getSharedPreferences("com.sintef_energy.ubisolar", Context.MODE_PRIVATE);
    }

    public Activity getActivity() {
        return (Activity) context;
    }

    @Override
    public void add(Tip object) {
        if(!data.contains(object)) {
            data.add(object);
        }
    }

    @Override
    public void remove(Tip object) {
        if(data.contains(object)) {
            data.remove(object);
            sharedPreferences.edit().putInt("Tip"+object.getId(), 0).apply();
            notifyDataSetChanged();
        }
    }

    @Override
    public void clear() {
        data.clear();
    }

    @Override
    public Tip getItem(int position) {
        return data.get(position);
    }

    @Override
    public int getCount() {
        return data.size();
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        View row = convertView;
        final TipHolder holder;

        if(row == null) {
            LayoutInflater inflater = ((Activity)context).getLayoutInflater();
            row = inflater.inflate(layoutResourceId, parent, false);

            holder = new TipHolder();
            holder.name = (TextView)row.findViewById(R.id.yourTipsRowName);
            holder.checked = (CheckBox) row.findViewById(R.id.yourTipsRowChecked);

            row.setTag(holder);
        } else {
            holder = (TipHolder)row.getTag();
        }

        if(!data.isEmpty()) {
            Tip tip = data.get(position);
            holder.name.setText(tip.getName());
            holder.checked.setChecked(PreferencesManager.getInstance().isTipImplemented(tip));
        }


        row.findViewById(R.id.yourTipsRowChecked).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                CheckBox checkBox = (CheckBox) view;
                boolean value = checkBox.isChecked();
                PreferencesManager.getInstance().changeIsTipImplemented(data.get(position), value);
            }
        });

        row.findViewById(R.id.yourTipsRowName).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                YourDialog dialog = new YourDialog(data.get(position), YourAdapter.this);
                dialog.show(fragmentManager, "yourDialog");
            }
        });

        return row;
    }

    static class TipHolder {
        TextView name;
        CheckBox checked;
    }
}
