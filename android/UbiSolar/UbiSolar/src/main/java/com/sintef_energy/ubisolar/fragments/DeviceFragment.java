package com.sintef_energy.ubisolar.fragments;

import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import android.widget.ExpandableListView;

import com.sintef_energy.ubisolar.R;
import com.sintef_energy.ubisolar.database.energy.DeviceModel;
import com.sintef_energy.ubisolar.dialogs.AddDeviceDialog;
import com.sintef_energy.ubisolar.dialogs.AddUsageDialog;
import com.sintef_energy.ubisolar.utils.ExpandableListAdapter;

import java.util.ArrayList;

/**
 * Created by perok on 2/11/14.
 */
public class DeviceFragment extends DefaultTabFragment {
    /**
     * The fragment argument representing the section number for this
     * fragment.
     */
    public static final String TAG = DeviceFragment.class.getName();
    private View mRootview;
    private ExpandableListView expListView;
    private ArrayList<DeviceModel> devices;

    public static DeviceFragment newInstance(int sectionNumber) {
        DeviceFragment fragment = new DeviceFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_SECTION_NUMBER, sectionNumber);
        fragment.setArguments(args);
        return fragment;
    }

    public DeviceFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.add_device, menu);
        inflater.inflate(R.menu.add_device_energy, menu);
        super.onCreateOptionsMenu(menu,inflater);
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item){
        switch (item.getItemId()){
            case R.id.menu_add_device:
                AddDeviceDialog addDeviceDialog = new AddDeviceDialog();
                addDeviceDialog.show(getFragmentManager(), "addDevice");
                return true;
            case R.id.menu_add_usage:
                AddUsageDialog addUsageDialog = new AddUsageDialog();
                addUsageDialog.show(getFragmentManager(), "addUsage");
            default:
                return super.onOptionsItemSelected(item);
        }
    };

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        setHasOptionsMenu(true);
        mRootview =  inflater.inflate(R.layout.fragment_device_expandablelist, container, false);
        setupList();
        return mRootview;
    }

    private void setupList()
    {
        createGroupList();

        expListView = (ExpandableListView) mRootview.findViewById(R.id.devicesListView);
        final ExpandableListAdapter expListAdapter = new ExpandableListAdapter(getActivity(), devices);
        setGroupIndicatorToRight();
        expListView.setAdapter(expListAdapter);
    }

    private void createGroupList() {
        devices = new ArrayList<DeviceModel>();
        devices.add(new DeviceModel(1, "TV", "Stue 1 etg", 1));
        devices.add(new DeviceModel(2, "Oven", "In kitchen", 1));
        devices.add(new DeviceModel(3, "Warm water", "-", 1));
        devices.add(new DeviceModel(4, "Dishwasher", "Kitchen", 1));
        devices.add(new DeviceModel(5, "Heating", "Main heating 2 floor", 1));
        devices.add(new DeviceModel(6, "Radio", "Radio livingroom", 1));
    }

    private void setGroupIndicatorToRight() {
        /* Get the screen width */
        DisplayMetrics dm = new DisplayMetrics();
        getActivity().getWindowManager().getDefaultDisplay().getMetrics(dm);
        int width = dm.widthPixels;

        expListView.setIndicatorBounds(width - getDipsFromPixel(35), width
                - getDipsFromPixel(5));
    }

    public int getDipsFromPixel(float pixels) {
        // Get the screen's density scale
        final float scale = getResources().getDisplayMetrics().density;
        // Convert the dps to pixels, based on density scale
        return (int) (pixels * scale + 0.5f);
    }

}