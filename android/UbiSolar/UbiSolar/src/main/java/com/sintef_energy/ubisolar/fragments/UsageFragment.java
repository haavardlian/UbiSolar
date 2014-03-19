package com.sintef_energy.ubisolar.fragments;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Fragment;
import android.app.FragmentTransaction;
import android.app.LoaderManager;
import android.content.ContentResolver;

import android.content.CursorLoader;
import android.content.DialogInterface;
import android.content.Loader;
import android.database.Cursor;
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

import com.sintef_energy.ubisolar.IView.ITotalEnergyView;
import com.sintef_energy.ubisolar.R;
import com.sintef_energy.ubisolar.activities.DrawerActivity;
import com.sintef_energy.ubisolar.database.energy.DeviceModel;
import com.sintef_energy.ubisolar.database.energy.EnergyContract;
import com.sintef_energy.ubisolar.database.energy.EnergyDataSource;
import com.sintef_energy.ubisolar.database.energy.EnergyUsageModel;
import com.sintef_energy.ubisolar.dialogs.AddUsageDialog;
import com.sintef_energy.ubisolar.fragments.graphs.UsageGraphLineFragment;
import com.sintef_energy.ubisolar.fragments.graphs.UsageGraphPieFragment;
import com.sintef_energy.ubisolar.presenter.TotalEnergyPresenter;
import com.sintef_energy.ubisolar.structs.Device;
import com.sintef_energy.ubisolar.structs.DeviceUsage;
import com.sintef_energy.ubisolar.structs.DeviceUsageList;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Random;

/**
 * Created by perok on 2/11/14.
 */
public class UsageFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor>{

    private static final String TAG = UsageFragment.class.getName();

    /**
     * The fragment argument representing the section number for this
     * fragment.
     */
    private static final String ARG_SECTION_NUMBER = "section_number";

    private TotalEnergyPresenter totalEnergyPresenter;

    private boolean[] mSelectedItems;

    /** List of all devices */
    private ArrayList<DeviceModel> mDevices;

    /** List of the seleceted devices*/
    private ArrayList<DeviceModel> mSelectedDevices;

    /** List of usage from devices */
    private ArrayList<DeviceUsageList> mDeviceUsageList;

    /** Callback for graphs */
    private ITotalEnergyView graphView;

    /* Graphs fragments */
    private UsageGraphPieFragment usageGraphPieFragment;
    private UsageGraphLineFragment usageGraphLineFragment;

    private static final int LOADER_DEVICES = 0;
    private static final int LOADER_USAGE = 1;


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

        mDevices = new ArrayList<>();
        mSelectedDevices = new ArrayList<>();
        mDeviceUsageList = new ArrayList<>();

        if (savedInstanceState != null) {
            mSelectedItems = (boolean[]) savedInstanceState.getSerializable("mSelectedIndexes");
        }
        else
            //TODO fix
            mSelectedItems = new boolean[20];

//        clearDatabase();

        //prepoluate database if it is empty
        if(EnergyDataSource.getEnergyModelSize(getActivity().getContentResolver()) == 0) {
//            clearDatabase();
            createDevices();
            createEnergyUsage();
        }

        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.MONTH, 8);

        totalEnergyPresenter = new TotalEnergyPresenter();
        totalEnergyPresenter.loadEnergyData(getActivity().getContentResolver(),
                0,
                calendar.getTimeInMillis());

        /* Show fragment */
        ImageButton button = (ImageButton) getActivity().findViewById(R.id.usage_button_swap_graph);
        setLineChart(button);

        /* Button listeners*/
        ImageButton graphButton = (ImageButton)getActivity().findViewById(R.id.usage_button_swap_graph);
        graphButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                toggleGraph(view);
            }
        });

        Button deviceButton = (Button)getActivity().findViewById(R.id.usage_button_devices);
        deviceButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                displayDeviceFilter(view);
            }
        });

        getLoaderManager().initLoader(LOADER_DEVICES, null, this);
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
            case R.id.fragment_usage_menu_add:
                AddUsageDialog addUsageDialog = new AddUsageDialog();
                addUsageDialog.show(getFragmentManager(), "addUsage");
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    /*End lifecycle*/
    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putBooleanArray("mSelectedIndexes", mSelectedItems);
    }

    @Override
    public void onDestroy(){
        super.onDestroy();
    }

    /**
     * Helper method for switching graph type
     */
    public void toggleGraph(View view){
        ImageButton button = (ImageButton) getActivity().findViewById(R.id.usage_button_swap_graph);

        if(button.getTag(R.string.TAG_GRAPH_TYPE).equals("pie")){
            setPieChart(button);
        }
        else{
            setLineChart(button);
        }
    }

    /**
     * Created an AlertDialog for choosing what devices to filer on.
     *
     * @param view
     */
    public void displayDeviceFilter(View view){
        AlertDialog.Builder builder=new AlertDialog.Builder(getActivity());
        builder.setTitle(R.string.usage_device_dialog_title);
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                mSelectedDevices.clear();
                for (int i = 0; i < mDevices.size(); i++) {
                    if (mSelectedItems[i]) {
                        mSelectedDevices.add(mDevices.get(i));
                    }
                }
                getSelectedDeviceData();
            }
        });

        ArrayList<String> deviceNames = new ArrayList<>();

        for(Device device : mDevices)
            deviceNames.add(device.getName());

        builder.setMultiChoiceItems(deviceNames.toArray(new String[deviceNames.size()]), mSelectedItems,
                new DialogInterface.OnMultiChoiceClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which, boolean isChecked) {
                        mSelectedItems[which]=isChecked;
                    }
                });
        builder.show();
    }

    /**
     * Change the graph view to a pie chart.
     *
     * @param button
     */
    private void setPieChart(ImageButton button)
    {
        button.setImageResource(R.drawable.line);
        button.setTag(R.string.TAG_GRAPH_TYPE, "line");

        //If fragment have not been created.
        if(usageGraphPieFragment == null) {
            usageGraphPieFragment = UsageGraphPieFragment.newInstance();
            usageGraphPieFragment.registerTotalEnergyPresenter(totalEnergyPresenter);
        }

        graphView = usageGraphPieFragment;

        FragmentTransaction ft = getFragmentManager().beginTransaction();

        if(usageGraphPieFragment.isAdded())
            ft.show(usageGraphPieFragment);
        else{
            ft.addToBackStack("usageGraphPieFragment");
            ft.replace(R.id.fragment_usage_tab_graph_placeholder, usageGraphPieFragment);
        }

        ft.commit();
    }

    /**
     * Set the graph view to line graph.
     *
     * @param button
     */
    private void setLineChart(ImageButton button)
    {
        button.setImageResource(R.drawable.pie);
        button.setTag(R.string.TAG_GRAPH_TYPE, "pie");


        Button deviceButton  = (Button) getActivity().findViewById(R.id.usage_button_devices);
        deviceButton.setVisibility(View.VISIBLE);

        //If fragment have not been created.
        if(usageGraphLineFragment == null) {
            usageGraphLineFragment = UsageGraphLineFragment.newInstance();
            usageGraphLineFragment.registerTotalEnergyPresenter(totalEnergyPresenter);
        }

        graphView = usageGraphLineFragment;

        FragmentTransaction ft = getFragmentManager().beginTransaction();

        if(usageGraphLineFragment.isAdded())
            ft.show(usageGraphLineFragment);
        else{
            ft.addToBackStack("usageGraphLineFragment");
            ft.replace(R.id.fragment_usage_tab_graph_placeholder, usageGraphLineFragment);
        }

        ft.commit();
    }

    private void getSelectedDeviceData()
    {
        getLoaderManager().initLoader(LOADER_USAGE, null, this);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {

        switch (i)
        {
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
                        null,
                        null,
                        EnergyUsageModel.EnergyUsageEntry.COLUMN_DATETIME + " ASC"
                );
        }
        return null;
    }

    @Override
    public void onLoadFinished(Loader<Cursor> cursorLoader, Cursor cursor)
    {
        switch (cursorLoader.getId()) {
            /* Fetch all device data. Is used to show list of devices and choose which to see. */
            case LOADER_DEVICES:
                mDevices.clear();
                cursor.moveToFirst();
                if (cursor.getCount() != 0)
                    do {
                        mDevices.add(new DeviceModel(cursor));
                    }
                    while (cursor.moveToNext());
                break;
            /* Load usage */
            case LOADER_USAGE:
                populateDeviceUsageList(cursor);
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

        /* Clear and update the chosen devices to show. */
        mDeviceUsageList.clear();
        for(DeviceModel device : mSelectedDevices){
            //TODO: remove
            mDeviceUsageList.add(new DeviceUsageList(device));

            //Add devices to use in HashMap
            devices.put(device.getDevice_id(), new DeviceUsageList(device));
        }

        /* Onlye run if devices is selected */
        if(mDeviceUsageList.size() < 1){
            Log.v(TAG, "No devices selected");
            return;
        }

        /* Get data from cursor and add
        * TODO O(n^2), fix! */
        data.moveToFirst();
        if(data.getCount() >= 1)
            do{
                EnergyUsageModel eum = new EnergyUsageModel(data);

                DeviceUsageList dList = devices.get(eum.getDevice_id());

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

        mDevices.add(device);
        Log.v(TAG, "Created device: " + device.getName());
    }

    private void createEnergyUsage()
    {
        ContentResolver cr = getActivity().getContentResolver();
        EnergyUsageModel usageModel;
        Calendar cal = Calendar.getInstance();
        Random random = new Random();
        Date date;

        for(Device device : mDevices) {
            Log.v(TAG, "Creating data for: " + device.getName());
            for (int i = 0; i < 1000; i++) {
                date = new Date();
                cal.setTime(date);
                cal.add(Calendar.HOUR_OF_DAY, i);

                usageModel = new EnergyUsageModel(System.currentTimeMillis(), device.getDevice_id(),
                        cal.getTime(), random.nextInt((200 - 50) + 1) + 50);
                EnergyDataSource.addEnergyModel(cr, usageModel);
            }
        }
    }

    private void clearDatabase()
    {
        int it = getActivity().getContentResolver().delete(EnergyContract.Devices.CONTENT_URI, null, null);
        Log.v(TAG, "EMPTY DATABASE: " + it);

        it = getActivity().getContentResolver().delete(EnergyContract.Energy.CONTENT_URI, null, null);
        Log.v(TAG, "EMPTY DATABASE: " + it);
    }


}