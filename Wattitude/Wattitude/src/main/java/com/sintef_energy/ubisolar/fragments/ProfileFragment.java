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

import android.app.Activity;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ExpandableListView;
import android.widget.TextView;

import com.facebook.Session;
import com.facebook.widget.ProfilePictureView;
import com.sintef_energy.ubisolar.R;
import com.sintef_energy.ubisolar.activities.DrawerActivity;
import com.sintef_energy.ubisolar.adapter.ResidenceListAdapter;
import com.sintef_energy.ubisolar.dialogs.AddResidenceDialog;
import com.sintef_energy.ubisolar.model.Residence;
import com.sintef_energy.ubisolar.preferences.PreferencesManager;
import com.sintef_energy.ubisolar.utils.Global;

import java.util.ArrayList;

/**
 * Created by perok on 2/11/14.
 *
 * Unfinished fragment for managing the users profile information and residences
 */
public class ProfileFragment extends DefaultTabFragment  {

    public static final String TAG = ProfileFragment.class.getName();
    private View mRootView;
    private ExpandableListView expListView;
    private ArrayList<Residence> residences;
    private PreferencesManager prefs;

    private TextView name, location, age, country;
    private ProfilePictureView profilePicture;
    /**
     * Returns a new instance of this fragment for the given section
     * number.
     */
    public static ProfileFragment newInstance(int sectionNumber) {
        ProfileFragment fragment = new ProfileFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_SECTION_NUMBER, sectionNumber);
        fragment.setArguments(args);
        return fragment;
    }

    /**
     * The first call to a created fragment
     * @param activity
     */
    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        //Callback to activity
        ((DrawerActivity) activity).onSectionAttached(getArguments().getInt(ARG_SECTION_NUMBER));
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        //inflater.inflate(R.menu.add_residence, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item){
        switch (item.getItemId()){
            case R.id.menu_add_residence:
                AddResidenceDialog addResidenceDialog = new AddResidenceDialog();
                addResidenceDialog.show(getFragmentManager(), "addResidence");
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        prefs = PreferencesManager.getInstance();
        setHasOptionsMenu(true);

        mRootView = inflater.inflate(R.layout.fragment_profile, container, false);

        View onlineInlclude = mRootView.findViewById(R.id.fragment_profile_include_user);
        View offlineInlclude = mRootView.findViewById(R.id.fragment_profile_include_offline);

        setupList();

        if(Global.loggedIn) {

            name = (TextView) mRootView.findViewById(R.id.profile_name);
            name.setText(prefs.getFacebookName());
            location = (TextView) mRootView.findViewById(R.id.profile_location);
            location.setText(prefs.getFacebookLocation());
            age = (TextView) mRootView.findViewById(R.id.profile_age);
            age.setText(prefs.getFacebookAge());
            country = (TextView) mRootView.findViewById(R.id.profile_country);
            country.setText(prefs.getFacebookCountry());
            profilePicture = (ProfilePictureView) mRootView.findViewById(R.id.profile_profile_picture);
            profilePicture.setProfileId(prefs.getKeyFacebookUid());
            profilePicture.setPresetSize(ProfilePictureView.LARGE);
            profilePicture.setVisibility(View.VISIBLE);

            Session.getActiveSession();

            onlineInlclude.setVisibility(View.VISIBLE);
            offlineInlclude.setVisibility(View.GONE);
        }
        else {
            onlineInlclude.setVisibility(View.GONE);
            offlineInlclude.setVisibility(View.VISIBLE);
        }

        Session.getActiveSession();
        return mRootView;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        if (savedInstanceState != null) {
            // Restore last state for checked position.
        }
    }

    /*End lifecycle*/
    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        //outState.putInt("curChoice", mCurCheckPosition);
    }

    @Override
    public void onDestroy(){
        super.onDestroy();
    }

    private void setupList()  {
        createGroupList();
        expListView = (ExpandableListView) mRootView.findViewById(R.id.residencesListView);
        final ResidenceListAdapter expListAdapter = new ResidenceListAdapter(getActivity(), residences);
        setGroupIndicatorToRight();
        expListView.setAdapter(expListAdapter);
        expListView.setOnChildClickListener(expListAdapter);
    }

    private void createGroupList() {
        residences = new ArrayList<>();
        residences.add(new Residence("Huset", "Stadsing Dahls gate", 6, 140, 7015,'A',736583709));
        residences.add(new Residence("Hytta", "På fjellet", 2, 40, 4903,'G',736583709));
        residences.add(new Residence("Kontoret","NTNU", 1, 15, 7018, 'B',736583709));
        residences.add(new Residence("Spaniahuset", "Barcelona", 3, 80, 14390, 'D',736583709));
    }

    private void setGroupIndicatorToRight() {
        /* Get the screen width */
        DisplayMetrics dm = new DisplayMetrics();
        getActivity().getWindowManager().getDefaultDisplay().getMetrics(dm);
        int width = dm.widthPixels;

        expListView.setIndicatorBounds(width - getDipsFromPixel(35), width
                - getDipsFromPixel(5));
    }

    public int getDipsFromPixel(float pixels) {
        // Get the screen's density scale
        final float scale = getResources().getDisplayMetrics().density;
        // Convert the dps to pixels, based on density scale
        return (int) (pixels * scale + 0.5f);
    }
}
