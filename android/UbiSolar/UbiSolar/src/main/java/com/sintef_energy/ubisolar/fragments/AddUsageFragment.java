package com.sintef_energy.ubisolar.fragments;

import android.app.Activity;
import android.app.LoaderManager;
import android.content.CursorLoader;
import android.content.Loader;
import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
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
import com.sintef_energy.ubisolar.dialogs.DatePickerFragment;
import com.sintef_energy.ubisolar.presenter.TotalEnergyPresenter;
import com.sintef_energy.ubisolar.utils.Utils;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * Created by perok on 12.03.14.
 */
public class AddUsageFragment extends DefaultTabFragment implements LoaderManager.LoaderCallbacks<Cursor>, IDateCallback {

    private static final String TAG = AddUsageFragment.class.getName();

    private Calendar currentMonth;
    private SimpleDateFormat formatter;

    private EditText mDateField;
    private EditText mKwhField;
    private ImageButton mButtonCalendar;
    private ImageButton mButtonKwhUp;
    private ImageButton mButtonKwhDown;
    private ImageButton mButtonAddUsage;

    private Spinner spinnerDevice;
    private SimpleCursorAdapter mDeviceAdapter;

    private TotalEnergyPresenter mTotalEnergyPresenter;

    public static AddUsageFragment newInstance(int sectionNumber) {
        AddUsageFragment fragment = new AddUsageFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_SECTION_NUMBER, sectionNumber);
        fragment.setArguments(args);
        return fragment;
    }

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
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate and set the layout for the dialog
        // Pass null as the parent view because its going in the dialog layout

        View view = inflater.inflate(R.layout.fragment_add_usage, null);

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
        mButtonAddUsage = (ImageButton)view.findViewById(R.id.btnAddUsage);
        final DatePickerFragment datePicker = new DatePickerFragment();
        datePicker.setTargetFragment(this, 0);

        /* Set up listeners */
        mButtonAddUsage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String text = mKwhField.getText().toString();

                if (text.length() > 0) {
                    Double value = Double.valueOf(text);

                    int pos = spinnerDevice.getSelectedItemPosition();
                    Cursor item = mDeviceAdapter.getCursor();
                    item.moveToPosition(pos);
                    pos = item.getColumnIndex(DeviceModel.DeviceEntry.COLUMN_NAME);

                    EnergyUsageModel euModel = new EnergyUsageModel();
                    euModel.setDatetime(new Date(currentMonth.getTimeInMillis()));
                    euModel.setDevice_id(item.getInt(pos));
                    euModel.setPower_usage(value);

                    //TODO: Make the actual adding of usage work
                    if(mTotalEnergyPresenter.addEnergyData(getActivity().getContentResolver(), euModel) != null)
                        Utils.makeShortToast(getActivity().getApplicationContext(), "Usage added");
                }
            }
        });

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
        //alertDialog.getButton(AlertDialog.BUTTON_POSITIVE).setEnabled(false);
        return view;
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
                //((AlertDialog)AddUsageDialog.this.getDialog()).getButton(AlertDialog.BUTTON_POSITIVE).setEnabled(true);
            }
            else {
                spinnerDevice.setEnabled(false);
                //((AlertDialog)AddUsageDialog.this.getDialog()).getButton(AlertDialog.BUTTON_POSITIVE).setEnabled(false);
            }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> cursorLoader) {
        mDeviceAdapter.swapCursor(null);
    }
}