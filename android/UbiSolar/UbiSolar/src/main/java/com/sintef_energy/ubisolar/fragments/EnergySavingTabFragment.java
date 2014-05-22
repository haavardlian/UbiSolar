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
import com.sintef_energy.ubisolar.adapter.YourTipAdapter;

/**
 * Created by perok on 21.03.14.
 *
 * The parent fragment for the tip fragments
 */
public class EnergySavingTabFragment extends DefaultTabFragment {

    private TipsPagerAdapter mPagerAdapter;

    public static EnergySavingTabFragment newInstance(int sectionNumber) {
        EnergySavingTabFragment fragment = new EnergySavingTabFragment();
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
        View view = inflater.inflate(R.layout.fragment_energy_saving_tab, container, false);

        if(mPagerAdapter == null)
            mPagerAdapter = new TipsPagerAdapter(getFragmentManager());

        // Initialize the ViewPager and set an adapter
        final ViewPager pager = (ViewPager) view.findViewById(R.id.fragment_energy_saving_pager);
        pager.setAdapter(mPagerAdapter);

        // Bind the tabs to the ViewPager
        PagerSlidingTabStrip tabs = (PagerSlidingTabStrip) view.findViewById(R.id.fragment_energy_saving_tabs);
        tabs.setViewPager(pager);
        tabs.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                switch(position) {
                    case 0:
                        ((TipsPagerAdapter)pager.getAdapter()).getTipsFragment().getAdapter().notifyDataSetChanged();
                        break;
                    case 1:
                        ((TipsPagerAdapter)pager.getAdapter()).getYourFragment().getAdapter().notifyDataSetChanged();
                    default:
                        break;
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
        return view;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        setRetainInstance(true);
    }

    /**
     * Returns the page adapter
     * @return TipsPagerAdapter
     */
    public TipsPagerAdapter getAdapter() { return mPagerAdapter; }

    /**
     * The pager adapter for swapping tip fragment between tips and your tips
     */
    public class TipsPagerAdapter extends FragmentStatePagerAdapter {
        private String[] titles;
        private TipsFragment tipsFragment;
        private YourTipFragment yourFragment;
        public TipsPagerAdapter(FragmentManager fragmentManager) {
            super(fragmentManager);

            titles = getResources().getStringArray(R.array.energy_saving_tabs);
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return titles[position];
        }

        @Override
        public int getCount() {
            return titles.length;
        }

        public TipsFragment getTipsFragment() {
            return tipsFragment;
        }

        public YourTipFragment getYourFragment() {
            return yourFragment;
        }

        @Override
        public Fragment getItem(int position) {
            switch(position) {
                case 0:
                    tipsFragment = TipsFragment.newInstance(0);
                    tipsFragment.setTargetFragment(EnergySavingTabFragment.this, 0);
                    return tipsFragment;
                case 1:
                    yourFragment = YourTipFragment.newInstance(0);
                    yourFragment.setTargetFragment(EnergySavingTabFragment.this, 0);
                    return yourFragment;
                default:
                    return null;
            }
        }
    }
}
