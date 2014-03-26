package com.sintef_energy.ubisolar.fragments;

import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ExpandableListView;

import com.sintef_energy.ubisolar.R;
import com.sintef_energy.ubisolar.database.energy.DeviceModel;
import com.sintef_energy.ubisolar.utils.ExpandableListAdapter;

import java.util.ArrayList;

public class deviceTestFragment extends DefaultTabFragment {

    private View mRootview;
    private ExpandableListView expListView;
    private ArrayList<DeviceModel> devices;

    public static deviceTestFragment newInstance() {
        deviceTestFragment fragment = new deviceTestFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    public deviceTestFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        mRootview =  inflater.inflate(R.layout.activity_device_list_test, container, false);
        setupList();
        return mRootview;
    }

    private void setupList()
    {
        createGroupList();

        expListView = (ExpandableListView) mRootview.findViewById(R.id.devicesListView);
        final ExpandableListAdapter expListAdapter = new ExpandableListAdapter(getActivity(), devices);
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
