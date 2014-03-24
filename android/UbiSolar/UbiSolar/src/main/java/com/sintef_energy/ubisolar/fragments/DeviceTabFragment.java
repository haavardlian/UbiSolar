package com.sintef_energy.ubisolar.fragments;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import android.app.Activity;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.Menu;
import android.view.View;
import android.widget.ExpandableListView;
import android.widget.ExpandableListView.OnChildClickListener;
import android.widget.Toast;
import com.sintef_energy.ubisolar.R;
import com.sintef_energy.ubisolar.database.energy.DeviceModel;
import com.sintef_energy.ubisolar.utils.ExpandableListAdapter;

public class DeviceTabFragment extends Activity {

    List<DeviceModel> devices;
    ExpandableListView expListView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_device_list_test);

        createGroupList();

        expListView = (ExpandableListView) findViewById(R.id.devicesListView);
        final ExpandableListAdapter expListAdapter = new ExpandableListAdapter(this, devices);
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
        getWindowManager().getDefaultDisplay().getMetrics(dm);
        int width = dm.widthPixels;

        expListView.setIndicatorBounds(width - getDipsFromPixel(35), width
                - getDipsFromPixel(5));
    }

    // Convert pixel to dip
    public int getDipsFromPixel(float pixels) {
        // Get the screen's density scale
        final float scale = getResources().getDisplayMetrics().density;
        // Convert the dps to pixels, based on density scale
        return (int) (pixels * scale + 0.5f);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        return true;
    }
}
