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

package com.sintef_energy.ubisolar.fragments;

import android.os.Bundle;
import android.view.View;
import android.widget.ListView;

import com.devspark.progressfragment.ProgressFragment;
import com.sintef_energy.ubisolar.R;
import com.sintef_energy.ubisolar.adapter.YourTipAdapter;
import com.sintef_energy.ubisolar.model.Tip;
import com.sintef_energy.ubisolar.presenter.RequestManager;

import java.util.ArrayList;

/**
 * The fragment for displaying the users tips
 *
 * Created by Håvard on 24.03.2014.
 */
public class YourTipFragment extends ProgressFragment {
    private static final String ARG_POSITION = "position";

    private YourTipAdapter mYourAdapter;

    public static YourTipFragment newInstance(int position) {
        YourTipFragment fragment = new YourTipFragment();
        Bundle b = new Bundle();
        b.putInt(ARG_POSITION, position);
        fragment.setArguments(b);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        setContentView(R.layout.fragment_your_tip_list);
        setEmptyText(getString(R.string.energy_saving_your_tip_empty));
        setContentEmpty(true);
        setContentShown(false);

        View rootView =  getContentView();
        ListView yourList = (ListView) rootView.findViewById(R.id.yourList);
        mYourAdapter = new YourTipAdapter(this, R.layout.fragment_your_tip_row,
                new ArrayList<Tip>(), getFragmentManager());
        yourList.setAdapter(mYourAdapter);

        RequestManager.getInstance().doTipRequest().getSavedTips(mYourAdapter, this);

    }

    public YourTipAdapter getAdapter() { return mYourAdapter; }
}