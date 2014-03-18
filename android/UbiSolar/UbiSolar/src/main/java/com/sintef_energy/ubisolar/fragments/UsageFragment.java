package com.sintef_energy.ubisolar.fragments;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Fragment;
import android.app.FragmentTransaction;
import android.app.LoaderManager;
import android.content.ContentResolver;

import android.content.CursorLoader;
import android.content.DialogInterface;
import android.content.Intent;
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
//import com.sintef_energy.ubisolar.activities.AddDeviceEnergyActivity;
import com.sintef_energy.ubisolar.activities.DrawerActivity;
import com.sintef_energy.ubisolar.database.energy.DeviceModel;
import com.sintef_energy.ubisolar.database.energy.EnergyContract;
import com.sintef_energy.ubisolar.database.energy.EnergyDataSource;
import com.sintef_energy.ubisolar.database.energy.EnergyUsageModel;
import com.sintef_energy.ubisolar.fragments.graphs.UsageGraphLineFragment;
import com.sintef_energy.ubisolar.fragments.graphs.UsageGraphPieFragment;
import com.sintef_energy.ubisolar.presenter.TotalEnergyPresenter;
import com.sintef_energy.ubisolar.structs.Device;
import com.sintef_energy.ubisolar.structs.DeviceUsage;
import com.sintef_energy.ubisolar.structs.DeviceUsageList;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
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
    private ArrayList<Device> mDevices;
    private ArrayList<Device> mSelectedDevices;
    private ArrayList<DeviceUsageList> mDeviceUsageList;
    private ITotalEnergyView graphView;

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
        //return super.onCreateView(inflater, container, savedInstanceState);
        View rootView = inflater.inflate(R.layout.fragment_usage, container, false);
        //TextView textView = (TextView) rootView.findViewById(R.id.section_label);
        //textView.setText(Integer.toString(getArguments().getInt(ARG_SECTION_NUMBER)));

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
            createDevies();
            createEnergyUsage();
        }

        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.MONTH, 8);

        totalEnergyPresenter = new TotalEnergyPresenter();
        totalEnergyPresenter.loadEnergyData(getActivity().getContentResolver(),
                0,
                calendar.getTimeInMillis());

        /* Show fragment */
        UsageGraphLineFragment fragment = UsageGraphLineFragment.newInstance();
        graphView = fragment;
        fragment.registerTotalEnergyPresenter(totalEnergyPresenter);
        FragmentTransaction ft = getFragmentManager().beginTransaction();
        ft.replace(R.id.fragment_usage_tab_graph_placeholder, fragment);
        ft.commit();

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

        getLoaderManager().initLoader(0, null, this);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch(requestCode) {
            case (0) : {
                if (resultCode == Activity.RESULT_OK) {
//                    double value = data.getDoubleExtra(AddDeviceEnergyActivity.INTENT_KWH, -1);
//                    Log.v(TAG, String.valueOf(value));
//
//                    EnergyUsageModel euModel = new EnergyUsageModel();
//                    euModel.setDatetime(new Date(data.getLongExtra(AddDeviceEnergyActivity.INTENT_DATETIME, -1)));
//
//                    euModel.setPower_usage(value);
//
//                    totalEnergyPresenter.addEnergyData(getActivity().getContentResolver(), euModel);
                }
                break;
            }
            default:

        }
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
//                Intent intent = new Intent(this.getActivity(), AddDeviceEnergyActivity.class);
//                startActivityForResult(intent, 0);
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

    public void toggleGraph(View view)
    {
        ImageButton button = (ImageButton) getActivity().findViewById(R.id.usage_button_swap_graph);

        if( button.getTag(R.string.graph_tag) == null)
            button.setTag(R.string.graph_tag, "pie");

        //TODO swap graphs
        if(button.getTag(R.string.graph_tag).equals("pie"))
        {
            button.setImageResource(R.drawable.line);
            button.setTag(R.string.graph_tag, "line");
            UsageGraphPieFragment fragment = UsageGraphPieFragment.newInstance();
            graphView = fragment;
            fragment.registerTotalEnergyPresenter(totalEnergyPresenter);
            FragmentTransaction ft = getFragmentManager().beginTransaction();
            ft.replace(R.id.fragment_usage_tab_graph_placeholder, fragment);
            ft.commit();
        }
        else
        {
            button.setImageResource(R.drawable.pie);
            button.setTag(R.string.graph_tag, "pie");
            UsageGraphLineFragment fragment = UsageGraphLineFragment.newInstance();
            graphView = fragment;
            fragment.registerTotalEnergyPresenter(totalEnergyPresenter);
            FragmentTransaction ft = getFragmentManager().beginTransaction();
            ft.replace(R.id.fragment_usage_tab_graph_placeholder, fragment);
            ft.commit();
        }
    }

    public void displayDeviceFilter(View view)
    {
        AlertDialog.Builder builder=new AlertDialog.Builder(getActivity());
        builder.setTitle(R.string.usage_device_dialog_title);
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {

            @Override
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

    private void getSelectedDeviceData()
    {
        getLoaderManager().initLoader(1, null, this);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {

        switch (i)
        {
            case 0:
                return new CursorLoader(
                        getActivity(),
                        EnergyContract.Devices.CONTENT_URI,
                        EnergyContract.Devices.PROJECTION_ALL,
                        null,
                        null,
                        DeviceModel.DeviceEntry._ID + " ASC"
                );
            case 1:
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
            case 0:
                mDevices.clear();
                cursor.moveToFirst();
                if (cursor.getCount() != 0)
                    do {
                        DeviceModel model = new DeviceModel(cursor);
                        mDevices.add(new Device(model.getDevice_id(),
                                model.getName(), model.getDescription(), model.getUser_id()) {
                        });
                    }
                    while (cursor.moveToNext());
                break;
            case 1:
                populateDeviceUsageList(cursor);
                break;
        }
    }

    private void populateDeviceUsageList(Cursor data)
    {
        mDeviceUsageList.clear();
        for(Device device : mSelectedDevices)
        {
            mDeviceUsageList.add(new DeviceUsageList(device.getDevice_id(), device.getName()));
        }

        if(mDeviceUsageList.size() < 1)
        {
            Log.v(TAG, "No devices selected");
            return;
        }

        data.moveToFirst();
        if(data.getCount() != 0)
            do{
                EnergyUsageModel eum = new EnergyUsageModel(data);
                for(DeviceUsageList usageList : mDeviceUsageList) {
                    if (usageList.getId() == eum.getDevice_id()) {
                        usageList.add(new DeviceUsage(eum.getId(), eum.getDevice_id(),
                                eum.getDatetime(), eum.getPower_usage()) {
                        });
                    }
                }


            }
            while(data.moveToNext());

        graphView.clearDevices();
        graphView.addDeviceUsage(mDeviceUsageList);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> cursorLoader) {

    }

    private void createDevies()
    {
        DeviceModel device;
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
        mDevices.add(new Device(device.getDevice_id(),
                device.getName(), device.getDescription(), device.getUser_id()) {
        });
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