package com.sintef_energy.ubisolar.dialogs;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.app.LoaderManager;
import android.content.CursorLoader;
import android.content.DialogInterface;
import android.content.Loader;
import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.CursorAdapter;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.SimpleCursorAdapter;
import android.widget.Spinner;

import com.sintef_energy.ubisolar.IView.IPresenterCallback;
import com.sintef_energy.ubisolar.IView.IDateCallback;
import com.sintef_energy.ubisolar.R;
import com.sintef_energy.ubisolar.database.energy.DeviceModel;
import com.sintef_energy.ubisolar.database.energy.EnergyContract;
import com.sintef_energy.ubisolar.database.energy.EnergyUsageModel;
import com.sintef_energy.ubisolar.presenter.TotalEnergyPresenter;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * Created by perok on 12.03.14.
 */
public class AddUsageDialog extends DialogFragment implements LoaderManager.LoaderCallbacks<Cursor>, IDateCallback {
    private static final String TAG = AddUsageDialog.class.getName();

    private Calendar currentMonth;
    private SimpleDateFormat formatter;

    private EditText mDateField;
    private EditText mKwhField;
    private ImageButton mButtonCalendar;
    private ImageButton mButtonKwhUp;
    private ImageButton mButtonKwhDown;

    private Spinner spinnerDevice;
    private SimpleCursorAdapter mDeviceAdapter;

    private TotalEnergyPresenter mTotalEnergyPresenter;

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            mTotalEnergyPresenter = ((IPresenterCallback) activity).getmTotalEnergyPresenter();
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

        View view = inflater.inflate(R.layout.dialog_add_usage, null);
        builder.setView(view)
                // Add action buttons
                .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        String text = mKwhField.getText().toString();

                        Log.v(TAG, "Textfield value: " + text);

                        if(text.length() > 0){
                            Double value = Double.valueOf(text);

                            int pos = spinnerDevice.getSelectedItemPosition();
                            Cursor item = mDeviceAdapter.getCursor();
                            item.moveToPosition(pos);
                            pos = item.getColumnIndex(DeviceModel.DeviceEntry.COLUMN_NAME);

                            EnergyUsageModel euModel = new EnergyUsageModel();
                            euModel.setDatetime(new Date(currentMonth.getTimeInMillis()));
                            euModel.setDevice_id(item.getInt(pos));
                            euModel.setPower_usage(value);

                            mTotalEnergyPresenter.addEnergyData(getActivity().getContentResolver(), euModel);
                        }
                    }
                })
                .setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        AddUsageDialog.this.getDialog().cancel();
                    }
                })
                .setTitle(R.string.addUsageDialog_title);

        //Create the dialog view

        //Set the calendar
        currentMonth = Calendar.getInstance();
        currentMonth.set(Calendar.MINUTE, 0);
        currentMonth.set(Calendar.HOUR_OF_DAY, 0);
        currentMonth.set(Calendar.MILLISECOND, 0);

        formatter = new SimpleDateFormat("dd/MM-yyyy");

        /* Fetch view */
        spinnerDevice = (Spinner)view.findViewById(R.id.dialog_add_usage_spinner);
        mDateField = (EditText)view.findViewById(R.id.dialog_add_usage_edit_date);
        mKwhField = (EditText)view.findViewById(R.id.dialog_add_usage_edittext_kwh);
        mButtonCalendar = (ImageButton)view.findViewById(R.id.dialog_add_usage_button_calendar);
        mButtonKwhDown = (ImageButton)view.findViewById(R.id.dialog_add_usage_usage_down);
        mButtonKwhUp = (ImageButton)view.findViewById(R.id.dialog_add_usage_usage_up);

        final DatePickerFragment datePicker = new DatePickerFragment();
        datePicker.setTargetFragment(this, 0);

        /* Set up listeners */
        mButtonCalendar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mButtonCalendar.setEnabled(false);
                Calendar calender = Calendar.getInstance();
                Bundle args = new Bundle();
                args.putInt("year", calender.get(Calendar.YEAR));
                args.putInt("month", calender.get(Calendar.MONTH));
                args.putInt("day", calender.get(Calendar.DAY_OF_MONTH));
                datePicker.setArguments(args);

                datePicker.show(getFragmentManager(), "datePicker");
            }
        });

        mButtonKwhDown.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String temp = String.valueOf(mKwhField.getText());
                Double value = 1.;

                if(!temp.equals(""))
                    value = Double.valueOf(temp);

                if(value >= 1.)
                    value--;
                mKwhField.setText(String.valueOf(value));
            }
        });

        mButtonKwhUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String temp = String.valueOf(mKwhField.getText());

                Double value = 0.;

                if(!temp.equals(""))
                    value = Double.valueOf(temp);

                value++;
                mKwhField.setText(String.valueOf(value));
            }
        });

        /* Fill spinner with data*/
        mDeviceAdapter = new SimpleCursorAdapter(
                getActivity(),
                android.R.layout.simple_spinner_item,
                null,
                new String[]{DeviceModel.DeviceEntry.COLUMN_NAME},
                new int[]{android.R.id.text1},
                CursorAdapter.FLAG_REGISTER_CONTENT_OBSERVER);

        spinnerDevice.setEnabled(false);
        spinnerDevice.setAdapter(mDeviceAdapter);

        getLoaderManager().initLoader(0, null, this);

        updateDateText();

        //Disable positive button until data is ready.
        AlertDialog alertDialog = builder.create(); //TODO: Nullpointer
        //alertDialog.getButton(AlertDialog.BUTTON_POSITIVE).setEnabled(false);
        return alertDialog;
    }


    private void updateDateText(){
        mDateField.setText(formatter.format(currentMonth.getTime()));
    }

    @Override
    public void setDate(int year, int month, int day) {
        currentMonth.set(Calendar.YEAR, year);
        currentMonth.set(Calendar.MONTH, month);
        currentMonth.set(Calendar.DAY_OF_MONTH, day);
        updateDateText();
        mButtonCalendar.setEnabled(true);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {
        return new CursorLoader(
            getActivity(),
            EnergyContract.Devices.CONTENT_URI,
            new String[]{DeviceModel.DeviceEntry._ID, DeviceModel.DeviceEntry.COLUMN_NAME},
            null,
            null,
            DeviceModel.DeviceEntry.COLUMN_NAME + " ASC"
        );
    }

    @Override
    public void onLoadFinished(Loader<Cursor> cursorLoader, Cursor cursor) {
        mDeviceAdapter.swapCursor(cursor);
            if(cursor.getCount() > 0) {
                spinnerDevice.setEnabled(true);
                ((AlertDialog)AddUsageDialog.this.getDialog()).getButton(AlertDialog.BUTTON_POSITIVE).setEnabled(true);
            }
            else {
                spinnerDevice.setEnabled(false);
                ((AlertDialog)AddUsageDialog.this.getDialog()).getButton(AlertDialog.BUTTON_POSITIVE).setEnabled(false);
            }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> cursorLoader) {
        mDeviceAdapter.swapCursor(null);
    }
}