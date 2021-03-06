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
import android.app.ActionBar;
import android.app.Fragment;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.TypedArray;
import android.os.Handler;
import android.support.v4.app.ActionBarDrawerToggle;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import com.sintef_energy.ubisolar.Interfaces.IDrawerItem;
import com.sintef_energy.ubisolar.R;
import com.sintef_energy.ubisolar.adapter.NavDrawerListAdapter;
import com.sintef_energy.ubisolar.drawer.DrawerHeader;
import com.sintef_energy.ubisolar.drawer.DrawerIDrawerItem;
import com.sintef_energy.ubisolar.preferences.PreferencesManager;
import com.sintef_energy.ubisolar.utils.Global;

import java.util.ArrayList;

/**
 * Fragment used for managing interactions for and presentation of a navigation drawer.
 * See the <a href="https://developer.android.com/design/patterns/navigation-drawer.html#Interaction">
 * design guidelines</a> for a complete explanation of the behaviors implemented here.
 *
 *
 * Modifications by perok:
 *  Followed these steps: http://www.androidhive.info/2013/11/android-sliding-menu-using-navigation-drawer/
 */
public class NavigationDrawerFragment extends Fragment {

    private static final String TAG = NavigationDrawerFragment.class.getName();

    /**
     * Remember the position of the selected item.
     */
    private static final String STATE_SELECTED_POSITION = "selected_navigation_drawer_position";

    /**
     * Per the design guidelines, you should show the drawer on launch until the user manually
     * expands it. This shared preference tracks this.
     */
    private static final String PREF_USER_LEARNED_DRAWER = "navigation_drawer_learned";

    /**
     * A pointer to the current callbacks instance (the Activity).
     */
    private NavigationDrawerCallbacks mCallbacks;

    /**
     * Helper component that ties the action bar to the navigation drawer.
     */
    private ActionBarDrawerToggle mDrawerToggle;

    private DrawerLayout mDrawerLayout;
    private View mFragmentContainerView;

    private ListView mDrawerList;
    private String[] mNavMenuTitles;
    private ArrayList<IDrawerItem> mNavDrawerItems;
    private NavDrawerListAdapter mAdapter;

    private DrawerIDrawerItem mUsageDrawerItem;

    /** The fault startup tab */
    private int mCurrentSelectedPosition = 0;

    private boolean mFromSavedInstanceState;
    private boolean mUserLearnedDrawer;

    private PreferencesManager mPreferenceManager;
    private DataBroadCastReceiver mDataBroadcastManager;

    public NavigationDrawerFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Read in the flag indicating whether or not the user has demonstrated awareness of the
        // drawer. See PREF_USER_LEARNED_DRAWER for details.
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(getActivity());
        mUserLearnedDrawer = sp.getBoolean(PREF_USER_LEARNED_DRAWER, false);

        if (savedInstanceState != null) {
            mCurrentSelectedPosition = savedInstanceState.getInt(STATE_SELECTED_POSITION);
            mFromSavedInstanceState = true;
        }

        // Select either the default item (0) or the last selected item.
        selectItem(mCurrentSelectedPosition);

        mPreferenceManager = PreferencesManager.getInstance();
        mDataBroadcastManager = new DataBroadCastReceiver();
    }

    @Override
    public void onActivityCreated (Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        // Indicate that this fragment would like to influence the set of actions in the action bar.
        setHasOptionsMenu(true);

        updateUsageDrawItemCount(0);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_navigation_drawer, container, false);

        // load slide menu items
        mNavMenuTitles = getResources().getStringArray(R.array.nav_drawer_items);

        // nav drawer icons from resources
        TypedArray navMenuIcons = getResources().obtainTypedArray(R.array.nav_drawer_icons);
        mDrawerList = (ListView) rootView.findViewById(R.id.navigation_drawer_list);

        mNavDrawerItems = new ArrayList<>();

        // adding nav drawer items to array
        // Home
        //mNavDrawerItems.add(new NavDrawerItem(mNavMenuTitles[0], navMenuIcons.getResourceId(0, -1), true));
        mNavDrawerItems.add(new DrawerIDrawerItem(mNavMenuTitles[0]));
        // View
        //mNavDrawerItems.add(new NavDrawerItem(mNavMenuTitles[1], navMenuIcons.getResourceId(1, -1), true));
        mNavDrawerItems.add(new DrawerHeader(mNavMenuTitles[1]));
        mUsageDrawerItem = new DrawerIDrawerItem(mNavMenuTitles[2]);
        mNavDrawerItems.add(mUsageDrawerItem);
        mNavDrawerItems.add(new DrawerIDrawerItem(mNavMenuTitles[3]));
        //Manage
        mNavDrawerItems.add(new DrawerHeader(mNavMenuTitles[4]));
        mNavDrawerItems.add(new DrawerIDrawerItem(mNavMenuTitles[5]));
        mNavDrawerItems.add(new DrawerIDrawerItem(mNavMenuTitles[6]));
        //Socialize
        mNavDrawerItems.add(new DrawerHeader(mNavMenuTitles[7]));
        mNavDrawerItems.add(new DrawerIDrawerItem(mNavMenuTitles[8]));
        //Settings
        mNavDrawerItems.add(new DrawerHeader(mNavMenuTitles[9]));
        mNavDrawerItems.add(new DrawerIDrawerItem(mNavMenuTitles[10]));
        mNavDrawerItems.add(new DrawerIDrawerItem(mNavMenuTitles[11]));
        // Recycle the typed array
        navMenuIcons.recycle();

        // setting the nav drawer list mAdapter
        mAdapter = new NavDrawerListAdapter(
                getActivity().getApplicationContext(),
                mNavDrawerItems);
        mDrawerList.setAdapter(mAdapter);

        mDrawerList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, final int position, long id) {
                //For smoother open fragment animation, we first close the drawer, then do a delayed
                //fragment transaction.
                mDrawerLayout.closeDrawer(mFragmentContainerView);
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                    selectItem(position); // your fragment transactions go here
                    }
                }, 200);
                //selectItem(position);
            }
        });

        return rootView;
    }

    @Override
    public void onResume(){
        super.onResume();
        IntentFilter filter = new IntentFilter();
        filter.addAction(Global.BROADCAST_NAV_DRAWER);
        LocalBroadcastManager.getInstance(this.getActivity()).registerReceiver(mDataBroadcastManager, filter);
        updateUsageDrawItemCount(0);
    }

    @Override
    public void onPause(){
        super.onPause();
        LocalBroadcastManager.getInstance(this.getActivity()).unregisterReceiver(mDataBroadcastManager);
    }

    /**
     * Returns true if the drawer is active
     * @return
     */
    public boolean isDrawerOpen() {
        return mDrawerLayout != null && mDrawerLayout.isDrawerOpen(mFragmentContainerView);
    }

    /**
     * Users of this fragment must call this method to set up the navigation drawer interactions.
     *
     * @param fragmentId   The android:id of this fragment in its activity's layout.
     * @param drawerLayout The DrawerLayout containing this fragment's UI.
     */
    public void setUp(int fragmentId, DrawerLayout drawerLayout) {
        mFragmentContainerView = getActivity().findViewById(fragmentId);
        mDrawerLayout = drawerLayout;

        // set a custom shadow that overlays the main content when the drawer opens
        mDrawerLayout.setDrawerShadow(R.drawable.drawer_shadow, GravityCompat.START);
        // set up the drawer's list view with items and click listener

        ActionBar actionBar = getActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setHomeButtonEnabled(true);

        // ActionBarDrawerToggle ties together the the proper interactions
        // between the navigation drawer and the action bar app icon.

        mDrawerToggle = new ActionBarDrawerToggle(
                getActivity(),                    /* host Activity */
                mDrawerLayout,                    /* DrawerLayout object */
                R.drawable.ic_drawer,             /* nav drawer image to replace 'Up' caret */
                R.string.navigation_drawer_open,  /* "open drawer" description for accessibility */
                R.string.navigation_drawer_close  /* "close drawer" description for accessibility */
        ) {
            @Override
            public void onDrawerClosed(View drawerView) {
                super.onDrawerClosed(drawerView);
                if (!isAdded()) {
                    return;
                }

                getActivity().invalidateOptionsMenu(); // calls onPrepareOptionsMenu()
            }

            @Override
            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);
                if (!isAdded()) {
                    return;
                }

                if (!mUserLearnedDrawer) {
                    // The user manually opened the drawer; store this flag to prevent auto-showing
                    // the navigation drawer automatically in the future.
                    mUserLearnedDrawer = true;
                    SharedPreferences sp = PreferenceManager
                            .getDefaultSharedPreferences(getActivity());
                    sp.edit().putBoolean(PREF_USER_LEARNED_DRAWER, true).apply();
                }

                getActivity().invalidateOptionsMenu(); // calls onPrepareOptionsMenu()
            }
        };

        // If the user hasn't 'learned' about the drawer, open it to introduce them to the drawer,
        // per the navigation drawer design guidelines.
        if (!mUserLearnedDrawer && !mFromSavedInstanceState) {
            mDrawerLayout.openDrawer(mFragmentContainerView);
        }

        // Defer code dependent on restoration of previous instance state.
        mDrawerLayout.post(new Runnable() {
            @Override
            public void run() {
                mDrawerToggle.syncState();
            }
        });

        mDrawerLayout.setDrawerListener(mDrawerToggle);
    }

    private void selectItem(int position) {
        mCurrentSelectedPosition = position;
        if (mDrawerList != null) {
            mDrawerList.setItemChecked(position, true);
        }
        if (mDrawerLayout != null) {
            mDrawerLayout.closeDrawer(mFragmentContainerView);
        }
        if (mCallbacks != null) {
            mCallbacks.onNavigationDrawerItemSelected(position);
        }
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            mCallbacks = (NavigationDrawerCallbacks) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException("Activity must implement NavigationDrawerCallbacks.");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mCallbacks = null;
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt(STATE_SELECTED_POSITION, mCurrentSelectedPosition);
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        // Forward the new configuration the drawer toggle component.
        mDrawerToggle.onConfigurationChanged(newConfig);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        // If the drawer is open, show the global app actions in the action bar. See also
        // showGlobalContextActionBar, which controls the top-left area of the action bar.
        if (mDrawerLayout != null && isDrawerOpen()) {
            inflater.inflate(R.menu.global, menu);
            showGlobalContextActionBar();
        }
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (mDrawerToggle.onOptionsItemSelected(item)) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    /**
     * Per the navigation drawer design guidelines, updates the action bar to show the global app
     * 'context', rather than just what's in the current screen.
     */
    private void showGlobalContextActionBar() {
        ActionBar actionBar = getActionBar();
        actionBar.setDisplayShowTitleEnabled(true);
        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_STANDARD);
        actionBar.setTitle(R.string.app_name);
    }

    private ActionBar getActionBar() {
        return getActivity().getActionBar();
    }

    /**
     * Callbacks interface that all activities using this fragment must implement.
     */
    public static interface NavigationDrawerCallbacks {
        /**
         * Called when an item in the navigation drawer is selected.
         */
        void onNavigationDrawerItemSelected(int position);
    }

    public IDrawerItem getNavDrawerItem(int position){
        return mNavDrawerItems.get(position);
    }

    public NavDrawerListAdapter getNavDrawerListAdapter(){
        return mAdapter;
    }

    /**
     * Hides or shows the original ActionBar. Used in conjunction with the back stack.
     *
     * @param state
     */
    public void setNavDrawerToggle(boolean state){
        mDrawerToggle.setDrawerIndicatorEnabled(state);
    }

    /**
     * Updated the usage nav drawer with the correct usage.
     */
    private void updateUsageDrawItemCount(int newValue){
        int value = mPreferenceManager.getNavDrawerUsage();
        value += newValue;
        if(value > 0) {
            mUsageDrawerItem.setCount(String.valueOf(value));
        }
        else
            mUsageDrawerItem.setCount("");
    }

    //TODO Rewrite to static and use WeakReference
    public class DataBroadCastReceiver extends BroadcastReceiver{

        @Override
        public void onReceive(Context context, Intent intent) {

            String action = intent.getAction();

            if(action.equals(Global.BROADCAST_NAV_DRAWER)){
                if(intent.hasExtra(Global.DATA_B_NAV_DRAWER_USAGE)) {
                    int value = intent.getIntExtra(Global.DATA_B_NAV_DRAWER_USAGE, -1);

                    updateUsageDrawItemCount(value);
                }
            }
        }
    }
}