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
import com.sintef_energy.ubisolar.model.Device;
import com.sintef_energy.ubisolar.preferences.PreferencesManager;
import com.sintef_energy.ubisolar.presenter.DevicePresenter;
import com.sintef_energy.ubisolar.presenter.TotalEnergyPresenter;

/**
 * Created by pialindkjolen on 29.04.14.
 */
public class EditDeviceDialog extends DialogFragment {
    public static final String TAG = EditDeviceDialog.class.getName();

    private DevicePresenter devicePresenter;
    private View view;
    private TextView description, name;
    private Spinner categorySpinner;
    private DeviceModel device;
    private ArrayAdapter<String> categoryAdapter;
    private String title;
    private boolean newDevice;



    public EditDeviceDialog(DeviceModel device, String title){
        this.device = device;
        this.title = title;
        newDevice = false;
    }

    public EditDeviceDialog(String title){
        this.title = title;
        this.device = new DeviceModel();
        device.setUserId(Long.valueOf(PreferencesManager.getInstance().getKeyFacebookUid()));
        device.setId(System.currentTimeMillis());
        newDevice = true;
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
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        // Get the layout inflater
        LayoutInflater inflater = getActivity().getLayoutInflater();
        // Inflate and set the layout for the dialog
        // Pass null as the parent view because its going in the dialog layout

        view = inflater.inflate(R.layout.dialog_edit_device, null);

        /*Set up views*/
        name = (EditText) view.findViewById(R.id.device_edit_name);
        description = (EditText) view.findViewById(R.id.device_edit_description);
        categorySpinner = (Spinner) view.findViewById(R.id.device_edit_category);


        /*Fill spinner with categories*/
        categoryAdapter = new ArrayAdapter<>(
                getActivity(),
                android.R.layout.simple_spinner_item,
                android.R.id.text1,
                getResources().getStringArray(R.array.device_categories)
        );

        categorySpinner.setAdapter(categoryAdapter);

        //Get the existing data for the model
        if(device != null) {
            name.setText(device.getName());
            description.setText(device.getDescription());
            categorySpinner.setSelection(device.getCategory());
        }

        builder.setView(view)
                // Add action buttons
                .setPositiveButton(R.string.device_edit_save, new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        /*Edit the deviceModel*/
                        device.setDescription(description.getText().toString());
                        device.setName(name.getText().toString());
                        device.setCategory(categorySpinner.getSelectedItemPosition());
                        
                        if(newDevice)
                            devicePresenter.addDevice(device, getActivity().getContentResolver());
                        else
                            devicePresenter.editDevice(getActivity().getContentResolver(), device);
                    }
                })
                .setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        EditDeviceDialog.this.getDialog().cancel();
                    }
                })
                .setTitle(R.string.device_edit_title);

        return builder.create();
    }
}
