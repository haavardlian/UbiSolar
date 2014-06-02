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

package com.sintef_energy.ubisolar.dialogs;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;

import com.sintef_energy.ubisolar.R;
import com.sintef_energy.ubisolar.database.energy.DeviceModel;
import com.sintef_energy.ubisolar.fragments.UsageFragment;

import java.util.ArrayList;

/**
 * Dialog for device selection
 */
public class SelectDevicesDialog extends DialogFragment {

    private ArrayList<DeviceModel>  mDevices;
    private boolean[]               mSelectedItems;

    private static final String ARG_DEVICES = "ARG_DEVICES";
    private static final String ARG_SELECTED = "ARG_SELECTED";

    /**
     * Returns a new instance of this fragment for the given section
     * number.
     */
    public static SelectDevicesDialog newInstance(ArrayList<DeviceModel> devices, boolean[] selectedItems) {
        SelectDevicesDialog fragment = new SelectDevicesDialog();
        Bundle args = new Bundle();
        args.putParcelableArrayList(ARG_DEVICES, devices);
        args.putBooleanArray(ARG_SELECTED, selectedItems);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState){
        //Get the data
        mDevices = this.getArguments().getParcelableArrayList(ARG_DEVICES);
        mSelectedItems = this.getArguments().getBooleanArray(ARG_SELECTED);

        if(mSelectedItems.length != mDevices.size()) {
            mSelectedItems = new boolean[mDevices.size()];
        }


        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setIcon(R.drawable.devices);

        builder.setTitle(R.string.usage_select_devices);
        builder.setPositiveButton(getString(R.string.device_ok), new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                ArrayList<Long> selectedDeviceIDs = new ArrayList<>();

                for (int i = 0; i < mSelectedItems.length; i++) {
                    if (mSelectedItems[i]) {
                        selectedDeviceIDs.add(mDevices.get(i).getId());
                    }
                }

                //Perform callback
                UsageFragment target = (UsageFragment)getTargetFragment();
                target.selectedDevicesCallback(mSelectedItems);
            }
        });

        ArrayList<String> deviceNames = new ArrayList<>();

        for(DeviceModel device : mDevices)
            deviceNames.add(device.getName());

        //Load the options
        builder.setMultiChoiceItems(deviceNames.toArray(new String[deviceNames.size()]), mSelectedItems,
            new DialogInterface.OnMultiChoiceClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which, boolean isChecked) {
                    mSelectedItems[which]=isChecked;
                }
            });

        return builder.create();
    }
}
