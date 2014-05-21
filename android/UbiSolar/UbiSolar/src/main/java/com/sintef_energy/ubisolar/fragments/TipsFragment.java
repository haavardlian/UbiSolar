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

package com.sintef_energy.ubisolar.fragments;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import com.sintef_energy.ubisolar.R;
import com.sintef_energy.ubisolar.adapter.TipAdapter;
import com.sintef_energy.ubisolar.dialogs.AddTipDialog;
import com.sintef_energy.ubisolar.dialogs.TipDialog;
import com.sintef_energy.ubisolar.model.Tip;
import com.sintef_energy.ubisolar.presenter.RequestManager;

import java.util.ArrayList;

public class TipsFragment extends Fragment {
    private static final String ARG_POSITION = "position";
    private TipAdapter tipAdapter;

    public static TipsFragment newInstance(int position) {
        TipsFragment fragment = new TipsFragment();
        Bundle b = new Bundle();
        b.putInt(ARG_POSITION, position);
        fragment.setArguments(b);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        setHasOptionsMenu(true);

        View rootView = inflater.inflate(R.layout.fragment_tip_list, container, false);
        tipAdapter = new TipAdapter(getActivity(), R.layout.fragment_tip_row, new ArrayList<Tip>());

        ListView tipsView = (ListView) rootView.findViewById(R.id.tipList);
        tipsView.setAdapter(tipAdapter);
        tipsView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                TipDialog dialog = new TipDialog(tipAdapter.getItem(i));
                dialog.setTargetFragment(TipsFragment.this, 0);
                dialog.show(getFragmentManager(), "tipDialog");
            }
        });

        //Get all tips from server asynchronously
        RequestManager.getInstance().doTipRequest().getAllTips(tipAdapter, this);

        return rootView;
    }

    public TipAdapter getAdapter() { return this.tipAdapter; }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.add_tip, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection
        switch (item.getItemId()) {
        /* Moved to deviceTab */
            case R.id.menu_add_tip:
                AddTipDialog dialog = new AddTipDialog();
                dialog.setTargetFragment(this, 0);
                dialog.show(getFragmentManager(), "addTipDialog");
            default:
                return super.onOptionsItemSelected(item);
        }
    }

}
