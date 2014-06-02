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

package com.sintef_energy.ubisolar.utils;

import android.app.ActionBar.Tab;
import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentTransaction;

/**
 *
 * @author perok
 *
 * http://developer.android.com/guide/topics/ui/actionbar.html#Tabs
 * meets
 * http://stackoverflow.com/questions/10494252/tab-content-stays-visible-after-changing-tab-after-orientation-change
 * @param <T>
 */
public class TabListener<T extends Fragment> implements android.app.ActionBar.TabListener {
    private Fragment mFragment;
    private final Activity mActivity;
    private final String mTag;
    private final Class<T> mClass;

    /** Constructor used each time a new tab is created.
      * @param activity  The host Activity, used to instantiate the fragment
      * @param tag  The identifier tag for the fragment
      * @param clz  The fragment's Class, used to instantiate the fragment
      */
    public TabListener(Activity activity, String tag, Class<T> clz) {
        mActivity = activity;
        mTag = tag;
        mClass = clz;
    }

    /* The following are each of the ActionBar.TabListener callbacks */

    public void onTabSelected(Tab tab, FragmentTransaction ft) {

        Fragment preInitializedFragment = mActivity.getFragmentManager().findFragmentByTag(mTag);

        // Check if the fragment is already initialized
        if (mFragment == null && preInitializedFragment == null) {
            // If not, instantiate and add it to the activity
            mFragment = Fragment.instantiate(mActivity, mClass.getName());
        	//mFragment = new FragmentCalendar(bus, mActivity, mTag);
//            ft.add(R.id.lol_container, mFragment, mTag);
        } else if (mFragment != null) {
            // If it exists, simply attach it in order to show it
            ft.attach(mFragment);
        } else if (preInitializedFragment != null) {
            ft.attach(preInitializedFragment);
            mFragment = preInitializedFragment;
        }
    }

    public void onTabUnselected(Tab tab, FragmentTransaction ft) {
        if (mFragment != null) {
            // Detach the fragment, because another one is being attached
            ft.detach(mFragment);
        }
    }

    /**
     * Runs when tab is reselected WHEN currently being viewed..
     */
    public void onTabReselected(Tab tab, FragmentTransaction ft) {
        // User selected the already selected tab. Usually do nothing.
    }
}
