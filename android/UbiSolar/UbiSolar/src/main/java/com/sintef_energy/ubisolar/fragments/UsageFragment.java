package com.sintef_energy.ubisolar.fragments;

import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.LoaderManager;

import android.content.CursorLoader;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v13.app.FragmentStatePagerAdapter;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RadioGroup;

import com.astuetz.PagerSlidingTabStrip;
import com.sintef_energy.ubisolar.IView.IUsageView;
import com.sintef_energy.ubisolar.R;
import com.sintef_energy.ubisolar.database.energy.DeviceModel;
import com.sintef_energy.ubisolar.database.energy.EnergyContract;
import com.sintef_energy.ubisolar.dialogs.SelectDevicesDialog;
import com.sintef_energy.ubisolar.dialogs.ShareDialog;
import com.sintef_energy.ubisolar.fragments.graphs.UsageGraphLineFragment;
import com.sintef_energy.ubisolar.fragments.graphs.UsageGraphPieFragment;
import com.sintef_energy.ubisolar.model.DeviceUsageList;
import com.sintef_energy.ubisolar.preferences.PreferencesManager;
import com.sintef_energy.ubisolar.utils.Global;
import com.sintef_energy.ubisolar.utils.ScrollViewPager;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;

import info.hoang8f.android.segmented.SegmentedGroup;

public class UsageFragment extends DefaultTabFragment implements LoaderManager.LoaderCallbacks<Cursor>{

    public static final int LOADER_DEVICES = 0;

    private static final String TAG = UsageFragment.class.getName();

    private View mRootView;
    private Bundle mSavedState;
    private LinkedHashMap<Long, DeviceModel> mDevices;
    private ArrayList<DeviceUsageList> mDeviceUsageList;
    private IUsageView graphView;
    private UsageFragmentStatePageAdapter mUsageFragmentStatePageAdapter;
    private PreferencesManager mPreferenceManager;
    private boolean mFragmentSwap = false;
    private ScrollViewPager mPager;

    public UsageFragment() {
    }

    /**
     * Returns a new instance of this fragment for the given section
     * number.
     */
    public static UsageFragment newInstance(int sectionNumber) {
        UsageFragment fragment = new UsageFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_SECTION_NUMBER, sectionNumber);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
    }

    @Override
    public void onCreate(Bundle bundle){
        setHasOptionsMenu(true);

        super.onCreate(bundle);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mRootView = inflater.inflate(R.layout.fragment_usage, container, false);
        mPager = (ScrollViewPager) mRootView.findViewById(R.id.fragment_usage_tabs_pager);

        if(mUsageFragmentStatePageAdapter == null)
            mUsageFragmentStatePageAdapter = new UsageFragmentStatePageAdapter(this, mPager);

        mPreferenceManager = PreferencesManager.getInstance();

        // Initialize the ViewPager and set the adapter
        mPager.setAdapter(mUsageFragmentStatePageAdapter);
        // Makes the tabs non-swipeable. Having the tabs swipeable causes weird behavior because the
        // graphs are also swipeable.
        mPager.setSwipeable(false);

        // Bind the tabs to the ViewPager
        PagerSlidingTabStrip tabs = (PagerSlidingTabStrip)
                mRootView.findViewById(R.id.fragment_usage_tabs);

        tabs.setViewPager(mPager);

        tabs.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {}

            @Override
            public void onPageSelected(int position) {
                mFragmentSwap = true;
                graphView = (IUsageView) mUsageFragmentStatePageAdapter.getFragment(position);
                graphView.setDevices(mDevices);
                SegmentedGroup segment = (SegmentedGroup) getActivity().findViewById(R.id.usage_segment);
                segment.check(segment.getChildAt(graphView.getResolution()).getId());
//                graphView.pullData();
            }

            @Override
            public void onPageScrollStateChanged(int state) {}
        });

        //BUG: onPageChangeListener does not set graphView the first time.
        //This is an ugly fix
        graphView = (IUsageView)mUsageFragmentStatePageAdapter.instantiateItem(mPager, 0);

        SegmentedGroup segment = (SegmentedGroup) mRootView.findViewById(R.id.usage_segment);
        segment.check(segment.getChildAt(graphView.getResolution()).getId());

        return mRootView;
    }

    private static class GraphViewTouchListener implements View.OnTouchListener{

        ScrollViewPager pager;

        GraphViewTouchListener(ScrollViewPager pager){
           this.pager = pager;
        }

        @Override
        public boolean onTouch(View view, MotionEvent motionEvent) {
            //We have a multitouch event
            if(motionEvent.getPointerCount() > 1) {
                pager.setSwipeable(false);
                //requestDisallowInterceptTouchEvent ?
                return true;
            }

            pager.setSwipeable(true);
            return false;
        }
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        SegmentedGroup segment = (SegmentedGroup) mRootView.findViewById(R.id.usage_segment);
        segment.setTintColor(Color.DKGRAY);

        segment.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int i) {
                if(!mFragmentSwap) {
                    SegmentedGroup segment = (SegmentedGroup) mRootView.findViewById(R.id.usage_segment);
                    graphView.setResolution(segment.indexOfChild(segment.findViewById(segment.getCheckedRadioButtonId())));
                    graphView.pullData();
                }
                mFragmentSwap = false;
            }
        });

                mDevices = new LinkedHashMap<>();

        if(savedInstanceState != null && mSavedState == null)
            mSavedState = savedInstanceState.getBundle("mSavedState");

        LoaderManager loaderManager = getLoaderManager();

        if(loaderManager == null){
            Log.e(TAG, "Unable to load the loader manager");
        }

        if (mSavedState != null) {
            mDeviceUsageList = mSavedState.getParcelableArrayList("mDeviceUsageList");
            loaderManager.restartLoader(LOADER_DEVICES, null, this);
        } else {
            mDeviceUsageList = new ArrayList<>();
            loaderManager.initLoader(LOADER_DEVICES, null, this);
        }

        mPreferenceManager.setNavDrawerUsage(0);
        Intent i = new Intent(Global.BROADCAST_NAV_DRAWER);
        i.putExtra(Global.DATA_B_NAV_DRAWER_USAGE, 0);
        LocalBroadcastManager.getInstance(this.getActivity().getApplicationContext()).sendBroadcast(i);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.usage, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        FragmentManager fragmentManager = getFragmentManager();
        if(fragmentManager == null) {
            Log.e(TAG, "Unable to load the fragment manager");
            return false;
        }
        switch (item.getItemId()) {
            case R.id.fragment_usage_menu_action_devices:
                SelectDevicesDialog dialog = SelectDevicesDialog.newInstance(
                        new ArrayList<DeviceModel>(mDevices.values()),
                        graphView.getSelectedDialogItems());
                dialog.setTargetFragment(this, 0);
                dialog.show(fragmentManager, "selectDeviceDialog");
                return true;
            case R.id.share_usage:
                ShareDialog d = new ShareDialog(graphView.createImage());
                d.setTargetFragment(UsageFragment.this, 0);
                d.show(fragmentManager, "shareDialog");
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    /*End lifecycle*/
    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putBundle("mSavedState", mSavedState != null ? mSavedState : saveState());
    }

    public void onDestroyView(){
        super.onDestroy();

        mSavedState = saveState();
        Log.v(TAG, " onDestroyView()");
    }

    @Override
    public void onDestroy(){
        super.onDestroy();
        cleanUpReferences();
    }

    private Bundle saveState(){
        Bundle state = new Bundle();

        state.putParcelableArrayList("mDeviceUsageList", mDeviceUsageList);

        return state;
    }

    /**
     * Everything is nulled out so the GC can collect the fragment instance.
     */
    private void cleanUpReferences(){
        mRootView = null;
        mSavedState = null;
        mDevices = null;
        mDeviceUsageList = null;
        graphView = null;
        mUsageFragmentStatePageAdapter = null;
        mPreferenceManager = null;
        mPager = null;
    }

    public void selectedDevicesCallback(String[] selectedItems, boolean[] itemsSelected){
        graphView.setSelectedDialogItems(itemsSelected);
        graphView.pullData();
    }

    @Override
    public Loader<Cursor> onCreateLoader(int mode, Bundle bundle) {
        return new CursorLoader(
                getActivity(),
                EnergyContract.Devices.CONTENT_URI,
                EnergyContract.Devices.PROJECTION_ALL,
                null,
                null,
                DeviceModel.DeviceEntry._ID + " ASC");
    }

    @Override
    public void onLoadFinished(Loader<Cursor> cursorLoader, Cursor cursor){
        mDevices.clear();
        cursor.moveToFirst();
        if (cursor.getCount() != 0)
            do {
                DeviceModel model = new DeviceModel(cursor);
                mDevices.put(model.getId(), model);
            } while (cursor.moveToNext());
        graphView.setDevices(mDevices);
        graphView.pullData();
    }

    @Override
    public void onLoaderReset(Loader<Cursor> cursorLoader) {}

    private static class UsageFragmentStatePageAdapter extends FragmentStatePagerAdapter {

        private String titles[];
        private HashMap<Integer, Fragment> fragmentReferenceMap;
        private ScrollViewPager mPager;

        public UsageFragmentStatePageAdapter(Fragment fragment, ScrollViewPager pager){
            super(fragment.getFragmentManager());
            titles = fragment.getResources().getStringArray(R.array.fragment_usage_tabs);
            fragmentReferenceMap = new HashMap<>();
            mPager = pager;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return titles[position];
        }

        @Override
        public Fragment getItem(int position) {

            Fragment fragment;

            switch (position){
                case 0:
                    fragment = UsageGraphLineFragment.newInstance();
                    //fragment.getView().setOnTouchListener(new GraphViewTouchListener(mPager));
                    break;
                case 1:
                    fragment = UsageGraphPieFragment.newInstance();
                    //fragment.getView().setOnTouchListener(new GraphViewTouchListener(mPager));
                    break;
                default:
                    return null;
            }

            if(fragment != null ) {
                fragmentReferenceMap.put(position, fragment);
            }

            return fragment;
        }

        @Override
        public int getCount() {
            return titles.length;
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            super.destroyItem(container, position, object);
            fragmentReferenceMap.remove(position);
        }

        public Fragment getFragment(int key) {
            return fragmentReferenceMap.get(key);
        }
    }
}