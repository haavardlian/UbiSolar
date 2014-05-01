package com.sintef_energy.ubisolar.dialogs;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;
import android.widget.Toast;

import com.sintef_energy.ubisolar.IView.IPresenterCallback;
import com.sintef_energy.ubisolar.R;
import com.sintef_energy.ubisolar.database.energy.DeviceModel;
import com.sintef_energy.ubisolar.fragments.UsageFragment;
import com.sintef_energy.ubisolar.presenter.DevicePresenter;
import com.sintef_energy.ubisolar.presenter.TotalEnergyPresenter;

import java.util.ArrayList;

/**
 * Created by perok on 19.03.14.
 */
public class SelectDevicesDialog extends DialogFragment {
    private DevicePresenter devicePresenter;

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
    public void onAttach(Activity activity) {
        super.onAttach(activity);

        try {
            devicePresenter = ((IPresenterCallback) activity).getDevicePresenter();
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString() + " must implement " + TotalEnergyPresenter.class.getName());
        }
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

        builder.setTitle(R.string.usage_select_devices);
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                ArrayList<Long> selectedDeviceIDs = new ArrayList<>();

                for (int i = 0; i < mSelectedItems.length; i++) {
                    if (mSelectedItems[i]) {
                        selectedDeviceIDs.add(mDevices.get(i).getId());
                    }
                }

                String[] array = new String[selectedDeviceIDs.size()];

                for(int i = 0; i < array.length; i++)
                    array[i]  = String.valueOf(selectedDeviceIDs.get(i));

                //Perform callback
                UsageFragment target = (UsageFragment)getTargetFragment();
                target.selectedDevicesCallback(array, mSelectedItems);
            }
        });

        ArrayList<String> deviceNames = new ArrayList<>();

        for(DeviceModel device : mDevices)
            deviceNames.add(device.getName());


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
