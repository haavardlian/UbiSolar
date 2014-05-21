/*
 * Licensed to UbiCollab.org under one or more contributor
 * license agreements.  See the NOTICE file distributed
 * with this work for additional information regarding
 * copyright ownership. UbiCollab.org licenses this file
 * to you under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance
 * with the License. You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

package com.sintef_energy.ubisolar.fragments;

import android.app.AlertDialog;
import android.app.LoaderManager;
import android.content.CursorLoader;
import android.content.DialogInterface;
import android.content.Loader;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.text.Html;
import android.util.DisplayMetrics;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import android.widget.ExpandableListView;

import com.sintef_energy.ubisolar.R;
import com.sintef_energy.ubisolar.adapter.DeviceListAdapter;
import com.sintef_energy.ubisolar.database.energy.DeviceModel;
import com.sintef_energy.ubisolar.database.energy.EnergyContract;
import com.sintef_energy.ubisolar.dialogs.EditDeviceDialog;
import com.sintef_energy.ubisolar.model.Device;
import com.sintef_energy.ubisolar.utils.Utils;

import java.util.ArrayList;

public class DeviceFragment extends DefaultTabFragment implements LoaderManager.LoaderCallbacks<Cursor> {

    private static final String TAG = DeviceFragment.class.getName();
    private View mRootview;
    private ExpandableListView mDeviceList;
    private DeviceListAdapter mDeviceAdapter;
    private ArrayList<DeviceModel> mDevices;

    private DeviceModel mDevice;

    public static DeviceFragment newInstance(int sectionNumber) {
        DeviceFragment fragment = new DeviceFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_SECTION_NUMBER, sectionNumber);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.add_device, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item){
        switch (item.getItemId()){
            case R.id.menu_add_device:
                EditDeviceDialog addDeviceDialog = new EditDeviceDialog(getString(R.string.device_add_title));
                addDeviceDialog.show(getFragmentManager(), "addDevice");
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        setHasOptionsMenu(true);

        mRootview =  inflater.inflate(R.layout.fragment_device_expandablelist, container, false);
        mDeviceList = (ExpandableListView) mRootview.findViewById(R.id.devicesListView);

        mDevices = new ArrayList<>();
        mDeviceAdapter = new DeviceListAdapter(getActivity(), mDevices);
        mDeviceList.setAdapter(mDeviceAdapter);

        registerForContextMenu(mDeviceList);

        return mRootview;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState){
        super.onActivityCreated(savedInstanceState);
        //Loads the devices from the database
        getLoaderManager().initLoader(0, null, this);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {
        return new CursorLoader(
                getActivity(),
                EnergyContract.Devices.CONTENT_URI,
                EnergyContract.Devices.PROJECTION_ALL,
                null,
                null,
                DeviceModel.DeviceEntry._ID + " ASC"
        );
    }

    @Override
    public void onLoadFinished(Loader<Cursor> cursorLoader, Cursor cursor) {
        mDevices.clear();

        cursor.moveToFirst();
        if (cursor.getCount() != 0)
            do {
                DeviceModel model = new DeviceModel(cursor);
                mDevices.add(model);
            } while (cursor.moveToNext());

        mDeviceAdapter.notifyDataSetChanged();
    }

    @Override
    public void onLoaderReset(Loader<Cursor> cursorLoader) {
        mDevices.clear();
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View view,
                                    ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, view, menuInfo);
        ExpandableListView.ExpandableListContextMenuInfo info = (ExpandableListView.ExpandableListContextMenuInfo) menuInfo;
        int group = ExpandableListView.getPackedPositionGroup(info.packedPosition);
        int child = ExpandableListView.getPackedPositionChild(info.packedPosition);
        Device device = mDeviceAdapter.getChild(group, child);

        MenuInflater menuInflater = getActivity().getMenuInflater();
        menu.setHeaderTitle(device.getName());
        menu.setHeaderIcon(R.drawable.devices);
        menuInflater.inflate(R.menu.device_menu, menu);
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        ExpandableListView.ExpandableListContextMenuInfo info =
                (ExpandableListView.ExpandableListContextMenuInfo) item.getMenuInfo();
        int group = ExpandableListView.getPackedPositionGroup(info.packedPosition);
        int child = ExpandableListView.getPackedPositionChild(info.packedPosition);
        mDevice = mDeviceAdapter.getChild(group, child);

        switch(item.getItemId()){
            case R.id.device_edit:
                EditDeviceDialog editDeviceDialog =
                        new EditDeviceDialog(mDevice, getString(R.string.device_edit_title));
                editDeviceDialog.show(getFragmentManager(), TAG);
                break;
            case R.id.device_delete:
                new AlertDialog.Builder(getActivity())
                        .setIcon(android.R.drawable.ic_dialog_alert)
                        .setTitle(getString(R.string.device_dialog_delete))
                        .setMessage(Html.fromHtml(getString(R.string.device_delete_conf1) + " <b>" +
                                mDevice.getName() + "</b> " + getString(R.string.device_delete_conf2)))
                        .setPositiveButton(getString(R.string.device_ok), new DialogInterface.OnClickListener() {

                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                Uri.Builder builer = EnergyContract.Devices.CONTENT_URI.buildUpon();
                                builer.appendPath("" + mDevice.getId());
                                getActivity().getContentResolver().delete(builer.build(), null, null);
                                mDevices.remove(mDevice);
                                Utils.makeShortToast(getActivity(),
                                        mDevice.getName() + " " + getString(R.string.device_toast_deleted));
                            }

                        })
                        .setNegativeButton(getString(R.string.device_cancel), null)
                        .show();

                this.mDeviceAdapter.notifyDataSetChanged();
                return true;
        }
        return super.onContextItemSelected(item);
    }
}