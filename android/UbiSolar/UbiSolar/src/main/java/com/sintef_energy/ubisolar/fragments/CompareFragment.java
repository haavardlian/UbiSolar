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

import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.os.Bundle;

import android.support.v13.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.astuetz.PagerSlidingTabStrip;
import com.sintef_energy.ubisolar.R;
import com.sintef_energy.ubisolar.activities.DrawerActivity;

/**
 * Created by perok on 21.03.14.
 */
public class CompareFragment extends DefaultTabFragment {

    private static final String TAG = CompareFragment.class.getName();

    private View mRoot;
    private ViewPager pager;

    private MyPagerAdapter mAdapter;

    private PagerSlidingTabStrip mTabs;

    public static CompareFragment newInstance(int sectionNumber) {
        CompareFragment fragment = new CompareFragment();
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
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        if(mRoot == null)
            mRoot = inflater.inflate(R.layout.fragment_social_tab, container, false);

        // Initialize the ViewPager and set an adapter
        if(pager == null)
            pager = (ViewPager) mRoot.findViewById(R.id.fragment_social_pager);

        if(mAdapter == null) {
            mAdapter = new MyPagerAdapter(getFragmentManager());
            pager.setAdapter(mAdapter);
        }

        // Bind the tabs to the ViewPager
        if(mTabs == null) {
            mTabs = (PagerSlidingTabStrip) mRoot.findViewById(R.id.fragment_social_tabs);
            mTabs.setViewPager(pager);
        }
        return mRoot;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }

    /*End lifecycle*/
    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
    }

    public class MyPagerAdapter extends FragmentStatePagerAdapter {

        private final String[] TITLES = { "Friends", "Similar profiles"};
        public MyPagerAdapter(FragmentManager fm) {
            super(fm);
        }


        @Override
        public CharSequence getPageTitle(int position) {
            return TITLES[position];
        }

        @Override
        public int getCount() {
            return TITLES.length;
        }

        @Override
        public Fragment getItem(int position) {
            switch(position) {
                case 0:
                    return CompareFriendsListFragment.newInstance(0);
                case 1:
                    return CompareSimilarFragment.newInstance(1);
                default:
                    return null;
            }
        }
    }
}
