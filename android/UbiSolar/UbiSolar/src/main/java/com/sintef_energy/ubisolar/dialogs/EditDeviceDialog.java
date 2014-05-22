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

package com.sintef_energy.ubisolar.dialogs;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import com.sintef_energy.ubisolar.IView.IPresenterCallback;
import com.sintef_energy.ubisolar.R;
import com.sintef_energy.ubisolar.database.energy.DeviceModel;
import com.sintef_energy.ubisolar.preferences.PreferencesManager;
import com.sintef_energy.ubisolar.presenter.DevicePresenter;
import com.sintef_energy.ubisolar.presenter.TotalEnergyPresenter;
import com.sintef_energy.ubisolar.utils.Utils;

/**
 * Dialog for creating and editing devices
 */
public class EditDeviceDialog extends DialogFragment {
    public static final String TAG = EditDeviceDialog.class.getName();

    private DevicePresenter mDevicePresenter;
    private DeviceModel mDevice;
    private String mDialogTitle;
    private boolean mNewDevice;

    /**
     * Constructor for editing a device
     *
     * @param device The device to edit
     * @param title The Dialog title
     */
    public EditDeviceDialog(DeviceModel device, String title){
        this.mDevice = device;
        this.mDialogTitle = title;
        mNewDevice = false;
    }

    /**
     * Constructor for adding a device
     * @param title The dialog title
     */
    public EditDeviceDialog(String title){
        this.mDialogTitle = title;
        this.mDevice = new DeviceModel();
        mDevice.setUserId(Long.valueOf(PreferencesManager.getInstance().getKeyFacebookUid()));
        mDevice.setId(System.currentTimeMillis());
        mNewDevice = true;
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            mDevicePresenter = ((IPresenterCallback) activity).getDevicePresenter();
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString() + " must implement " + TotalEnergyPresenter.class.getName());
        }
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        // Inflate and set the layout for the dialog
        // Pass null as the parent view because its going in the dialog layout
        LayoutInflater inflater = getActivity().getLayoutInflater();
        View deviceDialog = inflater.inflate(R.layout.dialog_edit_device, null);

        //Set up views
        final TextView name = (EditText) deviceDialog.findViewById(R.id.device_edit_name);
        final TextView description = (EditText) deviceDialog.findViewById(R.id.device_edit_description);
        final Spinner categorySpinner = (Spinner) deviceDialog.findViewById(R.id.device_edit_category);


        //Fill spinner with categories
        ArrayAdapter<String> categoryAdapter = new ArrayAdapter<>(
                getActivity(),
                R.layout.spinner_layout,
                R.id.spinnerTarget,
                getResources().getStringArray(R.array.device_categories)
        );
        categoryAdapter.setDropDownViewResource(R.layout.spinner_layout);
        categorySpinner.setAdapter(categoryAdapter);

        //Get the existing data for the model
        if(mDevice != null) {
            name.setText(mDevice.getName());
            description.setText(mDevice.getDescription());
            categorySpinner.setSelection(mDevice.getCategory());
        }

        builder.setView(deviceDialog)
                // Add action buttons
                .setPositiveButton(R.string.device_edit_save, new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        /*Edit the deviceModel*/
                        mDevice.setDescription(description.getText().toString());
                        mDevice.setName(name.getText().toString());
                        mDevice.setCategory(categorySpinner.getSelectedItemPosition());

                        if(mNewDevice) {
                            mDevicePresenter.addDevice(mDevice, getActivity().getContentResolver());
                            Utils.makeShortToast(getActivity(),
                                    mDevice.getName() + " " + getString(R.string.device_toast_added));
                        }
                        else {
                            mDevicePresenter.editDevice(getActivity().getContentResolver(), mDevice);
                            Utils.makeShortToast(getActivity(),
                                    mDevice.getName() + " " + getString(R.string.device_toast_edited));
                        }
                    }
                })
                .setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        EditDeviceDialog.this.getDialog().cancel();
                    }
                })
                .setTitle(mDialogTitle);

        return builder.create();
    }
}
