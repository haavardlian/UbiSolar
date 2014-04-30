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
import com.sintef_energy.ubisolar.presenter.DevicePresenter;
import com.sintef_energy.ubisolar.presenter.TotalEnergyPresenter;

/**
 * Created by pialindkjolen on 29.04.14.
 */
public class EditDeviceDialog extends DialogFragment implements LoaderManager.LoaderCallbacks<Cursor> {

    private DevicePresenter devicePresenter;
    private View view;
    private TextView description, name;
    private Spinner categorySpinner;
    private DeviceModel dm;
    private ArrayAdapter<String> categoryAdapter;
    public static final String TAG = EditDeviceDialog.class.getName();

    public EditDeviceDialog(DeviceModel dm){
        this.dm = dm;
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
        name = (EditText) view.findViewById(R.id.edit_name);
        description = (EditText) view.findViewById(R.id.edit_description);
        categorySpinner = (Spinner) view.findViewById(R.id.dialog_edit_device_category_spinner);

        //Get the existing data for the model
        dm.getId();
        name.setText(dm.getName());
        description.setText(dm.getDescription());
        categorySpinner.setSelection(dm.getCategory());

        builder.setView(view)
                // Add action buttons
                .setPositiveButton(R.string.editDeviceDialog_buttonText, new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        /*Edit the deviceModel*/
                        dm.setDescription(description.getText().toString());
                        dm.setName(name.getText().toString());
                        dm.setCategory(categorySpinner.getSelectedItemPosition());
                        devicePresenter.editDevice(getActivity().getContentResolver(), dm);
                    }
                })
                .setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        EditDeviceDialog.this.getDialog().cancel();
                    }
                })
                .setTitle(R.string.addDeviceDialog_title);

        /*Fill spinner with categories*/
        categoryAdapter = new ArrayAdapter<String>(
                getActivity(),
                android.R.layout.simple_spinner_item,
                android.R.id.text1,
                getResources().getStringArray(R.array.device_categories)
        );

        categorySpinner.setAdapter(categoryAdapter);

        getLoaderManager().initLoader(0, null, this);
        AlertDialog alertDialog = builder.create();

        return alertDialog;

    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        return null;
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {

    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {

    }
}
