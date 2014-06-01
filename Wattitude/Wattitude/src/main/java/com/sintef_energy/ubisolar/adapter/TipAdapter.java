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
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.RatingBar;
import android.widget.TextView;

import com.sintef_energy.ubisolar.R;
import com.sintef_energy.ubisolar.model.Tip;

import java.util.ArrayList;
import java.util.List;

/**
 * Adapter for managing tips
 */
public class TipAdapter extends ArrayAdapter<Tip> {
    private Context mContext;
    private int mLayoutResourceId;
    private List<Tip> mTips;

    public TipAdapter(Context context, int layoutResourceId, ArrayList<Tip> tips) {
        super(context, layoutResourceId);
        this.mContext = context;
        this.mLayoutResourceId = layoutResourceId;
        this.mTips = tips;
    }

    public Activity getActivity() {
        return (Activity) mContext;
    }

    @Override
    public void add(Tip object) {
        mTips.add(object);
    }

    @Override
    public void clear() {
        mTips.clear();
    }

    @Override
    public Tip getItem(int position) {
        return mTips.get(position);
    }

    @Override
    public int getCount() {
        return mTips.size();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        TipHolder holder;

        //Load the views
        if(convertView == null) {
            LayoutInflater inflater = ((Activity) mContext).getLayoutInflater();
            convertView = inflater.inflate(mLayoutResourceId, parent, false);

            holder = new TipHolder();
            holder.name = (TextView)convertView.findViewById(R.id.tipRowName);
            holder.rating = (RatingBar)convertView.findViewById(R.id.tipRowRating);

            convertView.setTag(holder);
        } else {
            holder = (TipHolder)convertView.getTag();
        }

        //Populate the views
        if(!mTips.isEmpty()) {
            Tip tip = mTips.get(position);
            holder.name.setText(tip.getName());
            holder.rating.setRating(tip.getAverageRating());
        }
        return convertView;
    }

    /**
     * Static class for holding the tip row views
     */
    static class TipHolder {
        TextView name;
        RatingBar rating;
    }
}
