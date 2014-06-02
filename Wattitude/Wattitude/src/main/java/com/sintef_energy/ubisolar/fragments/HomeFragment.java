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
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.facebook.Session;
import com.sintef_energy.ubisolar.R;
import com.sintef_energy.ubisolar.activities.DrawerActivity;
import com.sintef_energy.ubisolar.adapter.NewsFeedAdapter;
import com.sintef_energy.ubisolar.model.NewsFeedPost;
import com.sintef_energy.ubisolar.presenter.RequestManager;

import java.util.ArrayList;

public class HomeFragment extends DefaultTabFragment {
    public static final String TAG = HomeFragment.class.getName();

    /**
     * Returns a new instance of this fragment for the given section
     * number.
     */
    public static HomeFragment newInstance(int sectionNumber) {
        HomeFragment fragment = new HomeFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_SECTION_NUMBER, sectionNumber);
        fragment.setArguments(args);
        return fragment;
    }

    /**
     * The first call to a created fragment
     * @param activity The context
     */
    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        //Callback to activity
        ((DrawerActivity) activity).onSectionAttached(getArguments().getInt(ARG_SECTION_NUMBER));
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        //Load the news feed items
        View view = inflater.inflate(R.layout.fragment_home, container, false);
        ArrayList<NewsFeedPost> newsFeed = new ArrayList<>();
        NewsFeedAdapter wallAdapter = new NewsFeedAdapter(getActivity(),R.layout.home_wall_item, newsFeed);
        final ListView friendsList = (ListView) view.findViewById(R.id.news_feed_list);
        friendsList.setAdapter(wallAdapter);

        if(Session.getActiveSession().isOpened())
            RequestManager.getInstance().doFacebookRequest().populateWall(wallAdapter, this);

        return view;
    }
}