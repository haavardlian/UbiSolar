package com.sintef_energy.ubisolar.dialogs;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.app.LoaderManager;
import android.content.DialogInterface;
import android.content.Loader;
import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;

import com.sintef_energy.ubisolar.IView.IDeviceView;
import com.sintef_energy.ubisolar.IView.IPresenterCallback;
import com.sintef_energy.ubisolar.R;
import com.sintef_energy.ubisolar.database.energy.DeviceModel;
import com.sintef_energy.ubisolar.database.energy.EnergyDataSource;
import com.sintef_energy.ubisolar.database.energy.EnergyUsageModel;
import com.sintef_energy.ubisolar.presenter.DevicePresenter;
import com.sintef_energy.ubisolar.presenter.TotalEnergyPresenter;

import java.util.ArrayList;
import java.util.Date;

/**
 * Created by pialindkjolen on 13.03.14.
 */
public class AddDeviceDialog extends DialogFragment implements LoaderManager.LoaderCallbacks<Cursor> {

    private TextView nameField, descriptionField;
    private DevicePresenter devicePresenter;
    private View view;

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

        view = inflater.inflate(R.layout.dialog_add_device, null);
        builder.setView(view)
                // Add action buttons
                .setPositiveButton(R.string.addDeviceDialog_buttonText, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {

                        DeviceModel deviceModel = new DeviceModel();
                        deviceModel.setUser_id(System.currentTimeMillis());
                        deviceModel.setDevice_id(System.currentTimeMillis());
                        deviceModel.setDescription(descriptionField.getText().toString());
                        deviceModel.setName(nameField.getText().toString());
                        devicePresenter.addDevice(deviceModel, getActivity().getContentResolver());


                    }
                })
                .setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        AddDeviceDialog.this.getDialog().cancel();
                    }
                })
                .setTitle(R.string.addDeviceDialog_title);


        nameField = (EditText) view.findViewById(R.id.edit_name);
        descriptionField = (EditText) view.findViewById(R.id.edit_description);
        //usageField = (EnergyUsageModel) getActivity().findViewById(R.id.edit_usage);
        //addButton = (Button) getActivity().findViewById(R.id.add_button);

        //EnergyDataSource.deleteAll(getActivity().getContentResolver());

        getLoaderManager().initLoader(0, null, this);
        AlertDialog alertDialog = builder.create();

        return alertDialog;

    };

    @Override
    public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {
        return null;
    }

    @Override
    public void onLoadFinished(Loader<Cursor> cursorLoader, Cursor cursor) {

    }

    @Override
    public void onLoaderReset(Loader<Cursor> cursorLoader) {

    }
}
