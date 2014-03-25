package com.sintef_energy.ubisolar.fragments;

import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentTransaction;
import android.app.LoaderManager;
import android.content.ContentResolver;

import android.content.ContentValues;
import android.content.CursorLoader;
import android.content.Loader;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;

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

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Random;

/**
 * Created by perok on 2/11/14.
 *
 * BUG: Backstack for usage behaves weired.
 */
public class UsageFragment extends DefaultTabFragment implements LoaderManager.LoaderCallbacks<Cursor>{

    private static final String TAG = UsageFragment.class.getName();

    private Bundle mSavedState;

    /** List of all devices */
    private HashMap<Long, DeviceModel> mDevices;

    /** List of usage from devices */
    private ArrayList<DeviceUsageList> mDeviceUsageList;

    /** Callback for graphs */
    private IUsageView graphView;

    /* Graphs fragments */
    private UsageGraphPieFragment usageGraphPieFragment;
    private UsageGraphLineFragment usageGraphLineFragment;

    public static final int LOADER_DEVICES = 0;
    public static final int LOADER_USAGE = 1;
    public static final int LOADER_USAGE_DAY = 2;
    public static final int LOADER_USAGE_MONTH = 3;
    public static final int LOADER_USAGE_YEAR = 4;


    /** The first fragment is added to the view. Should not be added to the backstack */
    private boolean mFirstFragmentAdd = false;


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
        View rootView = inflater.inflate(R.layout.fragment_usage, container, false);
        return rootView;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        mDevices = new HashMap<>();

        //clearDatabase();

        //Populate the database if it's empty
        if(EnergyDataSource.getEnergyModelSize(getActivity().getContentResolver()) == 0) {
            createDevices();
            createEnergyUsage();
        }


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

        /* Button listeners*/
        ImageButton graphButton = (ImageButton)getActivity().findViewById(R.id.usage_button_swap_graph);
        graphButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                toggleGraph();
            }
        });

        Button deviceButton = (Button)getActivity().findViewById(R.id.usage_button_devices);
        deviceButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                displayDeviceFilter();
            }
        });

        ImageButton button = (ImageButton) getActivity().findViewById(R.id.usage_button_swap_graph);
        setLineChart(button);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.fragment_usage_menu, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection
        switch (item.getItemId()) {
            /* Moved to deviceTab
            case R.id.fragment_usage_menu_add:
                AddUsageDialog addUsageDialog = new AddUsageDialog();
                addUsageDialog.show(getFragmentManager(), "addUsage");
                return true;*/
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

//        state.putStringArray("mSelectedItems", mSelectedItems);
//        state.putStringArray("mSelectedLineItems", mSelectedLineItems);
//        state.putStringArray("mSelectedPieItems", mSelectedPieItems);
//        state.putBooleanArray("mSelectDialogItems", mSelectDialogItems);
//        state.putBooleanArray("mSelectedLineDialogItems", mSelectedLineDialogItems);
//        state.putBooleanArray("mSelectedPieDialogItems", mSelectedPieDialogItems);

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

    /**
     * Helper method for switching graph type
     */
    public void toggleGraph(){
        ImageButton button = (ImageButton) getActivity().findViewById(R.id.usage_button_swap_graph);

        if(button.getTag(R.string.TAG_GRAPH_TYPE).equals("pie")){
            setPieChart(button);
        }
        else{
            setLineChart(button);
        }
    }

    private void setPieChart(ImageButton button){
        button.setImageResource(R.drawable.line);
        button.setTag(R.string.TAG_GRAPH_TYPE, "line");

        //If fragment have not been created.
        if(usageGraphPieFragment == null) {
            usageGraphPieFragment = UsageGraphPieFragment.newInstance();
        }

        graphView = usageGraphPieFragment;

        addFragment(usageGraphPieFragment, "usageGraphPieFragment");
    }

    private void setLineChart(ImageButton button){
        button.setImageResource(R.drawable.pie);
        button.setTag(R.string.TAG_GRAPH_TYPE, "pie");

        //If fragment have not been created.
        if(usageGraphLineFragment == null) {
            System.out.println("New line graph");
            usageGraphLineFragment = UsageGraphLineFragment.newInstance();
        }

        graphView = usageGraphLineFragment;

        usageGraphLineFragment.setUsageFragment(this);

        addFragment(usageGraphLineFragment, "usageGraphLineFragment");
    }

    /**
     * Created an AlertDialog for choosing what devices to filer on.
     *
     */
    public void displayDeviceFilter(){
        SelectDevicesDialog dialog = SelectDevicesDialog.newInstance(
                new ArrayList<>(mDevices.values()), graphView.getSelectedDialogItems());
        dialog.setTargetFragment(this, 0);
        dialog.show(getFragmentManager(), "selectDeviceDialog");
    }

    public void selectedDevicesCallback(String[] selectedItems, boolean[] itemsSelected){
        Log.v(TAG, "# SELECTED ITEMS: " + selectedItems.length);
        graphView.setSelectedItems(selectedItems);
        graphView.setSelectedDialogItems(itemsSelected);


        //Clear the graph if no devices are selected
        if(selectedItems.length > 0)
            getLoaderManager().restartLoader(LOADER_USAGE, null, this);
        else
            graphView.clearDevices();
    }

    /**
     * Helper method for chaning the fragments.
     *
     * @param fragment
     * @param tag
     */
    private void addFragment(Fragment fragment, String tag){
        FragmentTransaction ft = getFragmentManager().beginTransaction();

        if(fragment.isAdded())
            ft.show(fragment);
        else{
            //Do not add the first time.
            if(!mFirstFragmentAdd) {
                ft.addToBackStack(tag);
                mFirstFragmentAdd = true;
            }

            ft.replace(R.id.fragment_usage_tab_graph_placeholder, fragment);
        }

        ft.commit();
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
                String where = "";

                //TODO: BUG: How to handle when user selects no devices?

                for(int n = 0; n < graphView.getSelectedItems().length; n++){
                    where += EnergyUsageModel.EnergyUsageEntry.COLUMN_DEVICE_ID + " = ? ";
                    if(n != graphView.getSelectedItems().length - 1)
                        where += " OR ";
                }

                return new CursorLoader(
                        getActivity(),
                        EnergyContract.Energy.CONTENT_URI,
                        EnergyContract.Energy.PROJECTION_ALL,
                        where,
                        graphView.getSelectedItems(),
                        EnergyUsageModel.EnergyUsageEntry.COLUMN_DATETIME + " ASC"
                );
            case LOADER_USAGE_DAY:
                builder = EnergyContract.Energy.CONTENT_URI.buildUpon();
                builder.appendPath(EnergyContract.Energy.Date.Day);

                return new CursorLoader(
                        getActivity(),
                        builder.build(),
                        null,
                        null,
                        null,
                        null
                );
            case LOADER_USAGE_MONTH:
                builder = EnergyContract.Energy.CONTENT_URI.buildUpon();
                builder.appendPath(EnergyContract.Energy.Date.Month);

                return new CursorLoader(
                        getActivity(),
                        builder.build(),
                        null,
                        null,
                        null,
                        null
                );
            case LOADER_USAGE_YEAR:
                builder = EnergyContract.Energy.CONTENT_URI.buildUpon();
                builder.appendPath(EnergyContract.Energy.Date.Year);

                return new CursorLoader(
                        getActivity(),
                        builder.build(),
                        null,
                        null,
                        null,
                        null
                );
        }
        return null;
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
                    }
                    while (cursor.moveToNext());
                break;
            /* Load usage */
            case LOADER_USAGE:
                populateDeviceUsageList(cursor);
                break;
            case LOADER_USAGE_DAY:
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
        //Hashmap containt all DevicesUsage
        HashMap<Long, DeviceUsageList> devices = new HashMap<>();

        /* Only run if devices is selected */
        if(data.getCount() < 1){
            Log.v(TAG, "No devices selected");
            return;
        }

        /* Get data from cursor and add */
        data.moveToFirst();
        if(data.getCount() >= 1)
            do{
                EnergyUsageModel eum = new EnergyUsageModel(data);

                DeviceUsageList dList = devices.get(eum.getDevice_id());

                if(dList == null){
                    dList = new DeviceUsageList(mDevices.get(eum.getDevice_id()));
                    devices.put(Long.valueOf(dList.getDevice().getDevice_id()), dList);
                }

                /* Add Energy usage to device */
                if(dList != null){
                    dList.add(eum);
                }
            }
            while(data.moveToNext());

        graphView.clearDevices();

        mDeviceUsageList.clear();
        mDeviceUsageList.addAll(devices.values());
        Log.v(TAG, "TOTAL : " + mDeviceUsageList.size());
        graphView.addDeviceUsage(mDeviceUsageList);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> cursorLoader) {

    }


    /* HELPER DATA GENERATION!!! Avoid. */

    private void createDevices()
    {
        addDevice("Total", "-");
        addDevice("TV", "Livingroom");
        addDevice("Radio", "Kitchen");
        addDevice("Heater", "Second floor");
        addDevice("Oven", "Kitchen");
    }

    private void addDevice(String name, String description)
    {
        DeviceModel device = new DeviceModel(System.currentTimeMillis(),
                name, description, System.currentTimeMillis());

        getActivity().getContentResolver().insert(
                EnergyContract.Devices.CONTENT_URI, device.getContentValues());

        mDevices.put(device.getDevice_id(), device);
        Log.v(TAG, "Created device: " + device.getName());
    }

    private void createEnergyUsage()
    {
        ContentResolver cr = getActivity().getContentResolver();
        EnergyUsageModel usageModel;
        int n = 1000;
        int nDevices = mDevices.size();
        ContentValues[] values = new ContentValues[n * nDevices];

        Calendar cal = Calendar.getInstance();
        Random random = new Random();
        Date date = new Date();
        int idCount = 1337;
        int y = 0;
        for(Device device : mDevices.values()) {
            cal.setTime(date);
            Log.v(TAG, "Creating data for: " + device.getName());
            for (int i = 0; i < n; i++) {
                cal.add(Calendar.HOUR_OF_DAY, 1);

                usageModel = new EnergyUsageModel(
                        idCount++,
                        device.getDevice_id(),
                        cal.getTime(),
                        random.nextInt(151) + 50);//(200 - 50) + 1) + 50);
                values[i + (y * n)] = usageModel.getContentValues();
                //EnergyDataSource.addEnergyModel(cr, usageModel);
            }
            y++;
        }
        Log.v(TAG, "Starting to add data to DB.");
        EnergyDataSource.addBatchEnergyModel(cr, values);
        Log.v(TAG, "Done adding data to DB");
    }

    private void clearDatabase()
    {
        int it = getActivity().getContentResolver().delete(EnergyContract.Devices.CONTENT_URI, null, null);
        Log.v(TAG, "EMPTY DATABASE: " + it);

        it = getActivity().getContentResolver().delete(EnergyContract.Energy.CONTENT_URI, null, null);
        Log.v(TAG, "EMPTY DATABASE: " + it);
    }

    private void testDateQuery(){
        Uri.Builder builder = EnergyContract.Energy.CONTENT_URI.buildUpon();
        builder.appendPath(EnergyContract.Energy.Date.Month);
        Cursor c = getActivity().getContentResolver().query(builder.build(), null, null, null, null);

        Log.v(TAG, "TESTQUERY: " + c.getCount());
        c.moveToFirst();
        int i = 0;
        do{
            Log.v(TAG, "TABLE: " + i++ + " -> " + c.getLong(0) + " " + c.getLong(1) + " " + c.getLong(2) + " " + c.getLong(3));
        } while(c.moveToNext());

        c.close();
    }
}