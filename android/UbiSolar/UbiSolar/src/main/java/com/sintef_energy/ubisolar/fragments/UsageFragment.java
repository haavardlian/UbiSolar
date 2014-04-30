package com.sintef_energy.ubisolar.fragments;

import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.LoaderManager;
import android.content.ContentResolver;

import android.content.ContentValues;
import android.content.CursorLoader;
import android.content.Loader;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v13.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.astuetz.PagerSlidingTabStrip;
import com.sintef_energy.ubisolar.IView.IUsageView;
import com.sintef_energy.ubisolar.R;
import com.sintef_energy.ubisolar.activities.DrawerActivity;
import com.sintef_energy.ubisolar.database.energy.DeviceModel;
import com.sintef_energy.ubisolar.database.energy.EnergyContract;
import com.sintef_energy.ubisolar.database.energy.EnergyDataSource;
import com.sintef_energy.ubisolar.database.energy.EnergyUsageModel;
import com.sintef_energy.ubisolar.dialogs.SelectDevicesDialog;
import com.sintef_energy.ubisolar.fragments.graphs.UsageGraphLineFragment;
import com.sintef_energy.ubisolar.fragments.graphs.UsageGraphPieFragment;
import com.sintef_energy.ubisolar.model.Device;
import com.sintef_energy.ubisolar.model.DeviceUsageList;
import com.sintef_energy.ubisolar.utils.Resolution;
import com.sintef_energy.ubisolar.utils.ScrollViewPager;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Random;

/**
 * Created by perok on 2/11/14.
 *
 * BUG: Backstack for usage behaves weired.
 */
public class UsageFragment extends DefaultTabFragment implements LoaderManager.LoaderCallbacks<Cursor>{

    private static final String TAG = UsageFragment.class.getName();

    private View mRootView;
    private Bundle mSavedState;

    /** List of all devices */
    private LinkedHashMap<Long, DeviceModel> mDevices;

    /** List of usage from devices */
    private ArrayList<DeviceUsageList> mDeviceUsageList;

    /** Callback for graphs */
    private IUsageView graphView;

    public static final int LOADER_DEVICES = 0;
    public static final int LOADER_USAGE = 1;
    public static final int LOADER_USAGE_DAY = 2;
    public static final int LOADER_USAGE_WEEK= 3;
    public static final int LOADER_USAGE_MONTH = 4;
    public static final int LOADER_USAGE_YEAR = 5;

    private UsageFragmentStatePageAdapter mUsageFragmentStatePageAdapter;

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

    public UsageFragment() {
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
    public void onCreate(Bundle bundle){
        //We can alter the option menu
        setHasOptionsMenu(true);

        super.onCreate(bundle);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mRootView = inflater.inflate(R.layout.fragment_usage, container, false);

        if(mUsageFragmentStatePageAdapter == null)
            mUsageFragmentStatePageAdapter = new UsageFragmentStatePageAdapter(getFragmentManager());

        // Initialize the ViewPager and set an adapter
        ScrollViewPager pager = (ScrollViewPager) mRootView.findViewById(R.id.fragment_usage_tabs_pager);
        pager.setAdapter(mUsageFragmentStatePageAdapter);
        pager.setSwipeable(false); //TODO: Should be enabled/ disabled on MotionEvents for LineGraph

        // Bind the tabs to the ViewPager
        PagerSlidingTabStrip tabs = (PagerSlidingTabStrip) mRootView.findViewById(R.id.fragment_usage_tabs);

        tabs.setViewPager(pager);

        tabs.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {}

            @Override
            public void onPageSelected(int position) {
                graphView = (IUsageView) mUsageFragmentStatePageAdapter.getFragment(position);
                graphView.setDeviceSize(mDevices.size());
                loadUsage();
            }

            @Override
            public void onPageScrollStateChanged(int state) {}
        });

        //BUG: onPageChangeLIstener does not set graphView the first time.
        //This is an ugly fix
        graphView = (IUsageView)mUsageFragmentStatePageAdapter.instantiateItem(pager, 0);

        return mRootView;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        Button zoomInButton = (Button) mRootView.findViewById(R.id.zoomInButton);
        Button zoomOutButton = (Button) mRootView.findViewById(R.id.zoomOutButton);
        zoomInButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                zoomIn();
            }
        });
        zoomOutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                zoomOut();
            }
        });

        mDevices = new LinkedHashMap<>();



        if(savedInstanceState != null && mSavedState == null)
            mSavedState = savedInstanceState.getBundle("mSavedState");

        if (mSavedState != null) {
            mDeviceUsageList = mSavedState.getParcelableArrayList("mDeviceUsageList");
            getLoaderManager().restartLoader(LOADER_DEVICES, null, this);
        }
        else {
            mDeviceUsageList = new ArrayList<>();
            getLoaderManager().initLoader(LOADER_DEVICES, null, this);
        }


        Log.v(TAG, "EnergyModel data sie in DB: " + EnergyDataSource.getEnergyModelSize(getActivity().getContentResolver()));
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.usage, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection
        switch (item.getItemId()) {
            case R.id.fragment_usage_menu_action_devices:
                SelectDevicesDialog dialog = SelectDevicesDialog.newInstance(
                        new ArrayList<DeviceModel>(mDevices.values()),
                        graphView.getSelectedDialogItems());
                dialog.setTargetFragment(this, 0);
                dialog.show(getFragmentManager(), "selectDeviceDialog");
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

    private Bundle saveState(){
        Bundle state = new Bundle();

        state.putParcelableArrayList("mDeviceUsageList", mDeviceUsageList);

        return state;
    }

    public void onDestroyView(){
        super.onDestroy();

        mSavedState = saveState();
        Log.v(TAG, " onDestroyView()");
    }

    @Override
    public void onDestroy(){
        super.onDestroy();
    }

    public void selectedDevicesCallback(String[] selectedItems, boolean[] itemsSelected){
//        graphView.setSelectedItems(selectedItems);
        graphView.setSelectedDialogItems(itemsSelected);

        //Clear the graph if no devices are selected
        if(selectedItems.length > 0)
            getLoaderManager().restartLoader(LOADER_USAGE, null, this);
        else
            graphView.clearDevices();
    }

    public void loadUsage()
    {
        if(!graphView.isLoaded()) {
            if(mDevices.size() > 0)
                getLoaderManager().restartLoader(LOADER_USAGE, null, this);
        }
    }

    private String[] getSelectedDevicesIDs()
    {
        boolean[] selectedItems = graphView.getSelectedDialogItems();
        ArrayList<String> ids = new ArrayList<>();

        int i = 0;

        for(Device device : mDevices.values())
        {
            if(selectedItems.length > i) {
                if (selectedItems[i]) {
                    ids.add("" + device.getDevice_id());
                }
                i++;
            }
        }
        return ids.toArray(new String[ids.size()]);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {
        Uri.Builder builder;

        switch (i){
            case LOADER_DEVICES:
                return new CursorLoader(
                        getActivity(),
                        EnergyContract.Devices.CONTENT_URI,
                        EnergyContract.Devices.PROJECTION_ALL,
                        null,
                        null,
                        DeviceModel.DeviceEntry._ID + " ASC"
                );
            case LOADER_USAGE:
                return new CursorLoader(
                        getActivity(),
                        EnergyContract.Energy.CONTENT_URI,
                        EnergyContract.Energy.PROJECTION_ALL,
                        sqlWhereDevices(),
                        getSelectedDevicesIDs(),
                        EnergyUsageModel.EnergyUsageEntry.COLUMN_DATETIME + " ASC"
                );
            case LOADER_USAGE_DAY:
                builder = EnergyContract.Energy.CONTENT_URI.buildUpon();
                builder.appendPath(EnergyContract.Energy.Date.Day);

                return new CursorLoader(
                        getActivity(),
                        builder.build(),
                        null,
                        sqlWhereDevices(),
                        getSelectedDevicesIDs(),
                        null
                );
            case LOADER_USAGE_WEEK:
                builder = EnergyContract.Energy.CONTENT_URI.buildUpon();
                builder.appendPath(EnergyContract.Energy.Date.Week);

                return new CursorLoader(
                        getActivity(),
                        builder.build(),
                        null,
                        sqlWhereDevices(),
                        getSelectedDevicesIDs(),
                        null
                );
            case LOADER_USAGE_MONTH:
                builder = EnergyContract.Energy.CONTENT_URI.buildUpon();
                builder.appendPath(EnergyContract.Energy.Date.Month);

                return new CursorLoader(
                        getActivity(),
                        builder.build(),
                        null,
                        sqlWhereDevices(),
                        getSelectedDevicesIDs(),
                        null
                );
            case LOADER_USAGE_YEAR:
                builder = EnergyContract.Energy.CONTENT_URI.buildUpon();
                builder.appendPath(EnergyContract.Energy.Date.Year);

                return new CursorLoader(
                        getActivity(),
                        builder.build(),
                        null,
                        sqlWhereDevices(),
                        getSelectedDevicesIDs(),
                        null
                );
        }
        return null;
    }

    private String sqlWhereDevices(){

        String where = "";

        boolean[] selectedItems = graphView.getSelectedDialogItems();


        ArrayList<String> queries = new ArrayList<>();

        for(int i = 0; i < selectedItems.length; i++)
        {
            if(selectedItems[i]) {
                System.out.println(EnergyUsageModel.EnergyUsageEntry.COLUMN_DEVICE_ID);
                queries.add(EnergyUsageModel.EnergyUsageEntry.COLUMN_DEVICE_ID + "=?");
            }
        }

        int i;

        for(i = 0; i < queries.size() -1; i++) {
            System.out.println(queries.get(i));
            where += queries.get(i) + " OR ";
        }

        where += queries.get(i);

//        for(int n = 0; n < graphView.getSelectedItems().length; n++){
//            where += EnergyUsageModel.EnergyUsageEntry.COLUMN_DEVICE_ID + " = ? ";
//            if(n != graphView.getSelectedItems().length - 1)
//                where += " OR ";
//        }

        return where;
    }


    @Override
    public void onLoadFinished(Loader<Cursor> cursorLoader, Cursor cursor){
        switch (cursorLoader.getId()) {
            /* Fetch all device data. Is used to show list of devices and choose which to see. */
            case LOADER_DEVICES:
                mDevices.clear();
                cursor.moveToFirst();
                if (cursor.getCount() != 0)
                    do {
                        DeviceModel model = new DeviceModel(cursor);
                        mDevices.put(model.getDevice_id(), model);
                    } while (cursor.moveToNext());
                graphView.setDeviceSize(mDevices.size());
                if(mDevices.size() > 0)
                    getLoaderManager().initLoader(LOADER_USAGE, null, this);
                break;
            /* Load usage */
            case LOADER_USAGE:
            case LOADER_USAGE_DAY:
            case LOADER_USAGE_WEEK:
            case LOADER_USAGE_MONTH:
            case LOADER_USAGE_YEAR:
                populateDeviceUsageList(cursor);
                //TODO: Implement logic to handle this correctly.
                //Maybe populate.. can take in format argument?
                break;
        }
    }

    /**
     * Populate the graphview view usage data.
     *
     * @param data
     */
    private void populateDeviceUsageList(Cursor data){
        //Hashmap containing all DevicesUsage
        HashMap<Long, DeviceUsageList> devices = new HashMap<>();

        /* Get data from cursor and add */
        data.moveToFirst();
        if(data.getCount() >= 1) {
            do {
                EnergyUsageModel model = new EnergyUsageModel(data, true);
                DeviceUsageList deviceUsageList = devices.get(model.getDevice_id());

                if (deviceUsageList == null) {
                    deviceUsageList = new DeviceUsageList(mDevices.get(model.getDevice_id()));
                    devices.put(Long.valueOf(deviceUsageList.getDevice().getDevice_id()), deviceUsageList);
                }

                deviceUsageList.add(model);
            }
            while (data.moveToNext());
        }

        graphView.clearDevices();


        mDeviceUsageList.clear();
        mDeviceUsageList.addAll(devices.values());

        if(mDeviceUsageList.size() > 0)
            graphView.addDeviceUsage(mDeviceUsageList);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> cursorLoader) {}

    private class UsageFragmentStatePageAdapter extends FragmentStatePagerAdapter {

        private String titles[];

        private HashMap<Integer, Fragment> fragmentReferenceMap;

        public UsageFragmentStatePageAdapter(FragmentManager fm){
            super(fm);

            titles = getResources().getStringArray(R.array.fragment_usage_tabs);

            fragmentReferenceMap = new HashMap<>();
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return titles[position];
        }

        @Override
        public Fragment getItem(int position) {

            Fragment fragment = null;

            switch (position){
                case 0:
                    fragment = UsageGraphLineFragment.newInstance();
                    break;
                case 1:
                    fragment = UsageGraphPieFragment.newInstance();
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

    private void zoomIn()
    {
        if(graphView.getResolution() == Resolution.DAYS) {
            graphView.setFormat(Resolution.HOURS);
            graphView.setActiveIndex(graphView.getActiveIndex() * 24);
            getLoaderManager().initLoader(UsageFragment.LOADER_USAGE, null, this);

            Button zoomInButton = (Button) mRootView.findViewById(R.id.zoomInButton);
            zoomInButton.setEnabled(false);
        }
        else if(graphView.getResolution() == Resolution.WEEKS) {
            graphView.setFormat(Resolution.DAYS);
            graphView.setActiveIndex(graphView.getActiveIndex() * 7);
            getLoaderManager().initLoader(UsageFragment.LOADER_USAGE_DAY, null, this);
        }
        else if(graphView.getResolution() == Resolution.MONTHS) {
            graphView.setFormat(Resolution.WEEKS);
            graphView.setActiveIndex(graphView.getActiveIndex() * 4);
            getLoaderManager().initLoader(UsageFragment.LOADER_USAGE_WEEK, null, this);

            Button zoomOutButton = (Button) mRootView.findViewById(R.id.zoomOutButton);
            zoomOutButton.setEnabled(true);
        }
    }

    private void zoomOut()
    {
        if(graphView.getResolution() == Resolution.HOURS) {
            graphView.setFormat(Resolution.DAYS);
            graphView.setActiveIndex(graphView.getActiveIndex() / 24);
            getLoaderManager().initLoader(UsageFragment.LOADER_USAGE_DAY, null, this);
            Button zoomInButton = (Button) mRootView.findViewById(R.id.zoomInButton);
            zoomInButton.setEnabled(true);
        }
        else if(graphView.getResolution() == Resolution.DAYS) {
            graphView.setFormat(Resolution.WEEKS);
            graphView.setActiveIndex(graphView.getActiveIndex() / 7);
            getLoaderManager().initLoader(UsageFragment.LOADER_USAGE_WEEK, null, this);

        }
        else if(graphView.getResolution() == Resolution.WEEKS) {
            graphView.setFormat(Resolution.MONTHS);
            graphView.setActiveIndex(graphView.getActiveIndex() / 4);
            getLoaderManager().initLoader(UsageFragment.LOADER_USAGE_MONTH, null, this);

            Button zoomOutButton = (Button) mRootView.findViewById(R.id.zoomOutButton);
            zoomOutButton.setEnabled(false);
        }
    }
}