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

/**
 * Created by pialindkjolen on 13.03.14.
 */
public class AddDeviceDialog extends DialogFragment implements LoaderManager.LoaderCallbacks<Cursor> {

    private TextView nameField, descriptionField;
    private DevicePresenter devicePresenter;
    private Spinner categorySpinner;
    private View view;
    private ArrayAdapter<String> categoryAdapter;
    public static final String TAG = AddDeviceDialog.class.getName();

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

        final long uid = Long.valueOf(PreferencesManager.getInstance().getKeyFacebookUid());

        view = inflater.inflate(R.layout.dialog_add_device, null);
        builder.setView(view)
                // Add action buttons
                .setPositiveButton(R.string.addDeviceDialog_buttonText, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        /*Create new device model*/
                        DeviceModel deviceModel = new DeviceModel();
                        deviceModel.setUserId(uid);
                        deviceModel.setId(System.currentTimeMillis());

                        deviceModel.setDescription(descriptionField.getText().toString());
                        deviceModel.setName(nameField.getText().toString());

                        /*Get selected category*/
                        deviceModel.setCategory(categorySpinner.getSelectedItemPosition());
                        devicePresenter.addDevice(deviceModel, getActivity().getContentResolver());

                    }
                })
                .setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        AddDeviceDialog.this.getDialog().cancel();
                    }
                })
                .setTitle(R.string.addDeviceDialog_title);

        /*Set up views*/
        nameField = (EditText) view.findViewById(R.id.edit_name);
        descriptionField = (EditText) view.findViewById(R.id.edit_description);
        categorySpinner = (Spinner) view.findViewById(R.id.dialog_add_device_category_spinner);


        /*Fill spinner with categories*/
        categoryAdapter = new ArrayAdapter<String>(
                getActivity(),
                android.R.layout.simple_spinner_item,
                android.R.id.text1,
                getResources().getStringArray(R.array.device_categories)
                );

        categorySpinner.setAdapter(categoryAdapter);
        //TODO: Remove Loadmanager?
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
