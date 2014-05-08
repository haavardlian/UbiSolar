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
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.CursorAdapter;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.SimpleCursorAdapter;
import android.widget.Spinner;
import android.widget.TextView;

import com.sintef_energy.ubisolar.IView.IPresenterCallback;
import com.sintef_energy.ubisolar.IView.IDateCallback;
import com.sintef_energy.ubisolar.R;
import com.sintef_energy.ubisolar.database.energy.DeviceModel;
import com.sintef_energy.ubisolar.database.energy.EnergyContract;
import com.sintef_energy.ubisolar.database.energy.EnergyUsageModel;
import com.sintef_energy.ubisolar.dialogs.DatePickerFragment;
import com.sintef_energy.ubisolar.presenter.TotalEnergyPresenter;
import com.sintef_energy.ubisolar.utils.OnOneOffClickListener;
import com.sintef_energy.ubisolar.utils.Utils;

import java.text.ParseException;
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

    private TextView mTextDate;
    private EditText mKwhField;
    private ImageButton mButtonKwhUp;
    private ImageButton mButtonKwhDown;
    private Button mButtonAddUsage;
    private RelativeLayout mRelativeLayout;

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

        View view = inflater.inflate(R.layout.fragment_add_usage, container, false);

        //Set the calendar
        currentMonth = Calendar.getInstance();
        currentMonth.set(Calendar.MINUTE, 0);
        currentMonth.set(Calendar.HOUR_OF_DAY, 0);


        formatter = new SimpleDateFormat("dd/MM-yyyy");

        /* Fetch view */
        spinnerDevice = (Spinner)view.findViewById(R.id.dialog_add_usage_spinner);
        mRelativeLayout = (RelativeLayout)view.findViewById(R.id.fragment_add_usage_rl_date);
        mTextDate = (TextView)view.findViewById(R.id.fragment_add_usage_text_date);
        mKwhField = (EditText)view.findViewById(R.id.dialog_add_usage_edittext_kwh);
        mButtonKwhDown = (ImageButton)view.findViewById(R.id.dialog_add_usage_usage_down);
        mButtonKwhUp = (ImageButton)view.findViewById(R.id.dialog_add_usage_usage_up);
        mButtonAddUsage = (Button)view.findViewById(R.id.btnAddUsage);
        final DatePickerFragment datePicker = new DatePickerFragment();
        datePicker.setTargetFragment(this, 0);

        /* Set up listeners */
        mButtonAddUsage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String text = mKwhField.getText().toString();

                if(text.length() < 1){
                    Utils.makeLongToast(getActivity().getApplicationContext(), "Error: No usage added");
                    return;
                }

                Double value = Double.valueOf(text);
                int pos = spinnerDevice.getSelectedItemPosition();

                if(pos == Spinner.INVALID_POSITION){
                    Utils.makeLongToast(getActivity().getApplicationContext(), "Error: No device selected");
                   return;
                }

                Cursor item = mDeviceAdapter.getCursor();
                item.moveToPosition(pos);
                pos = item.getColumnIndex(DeviceModel.DeviceEntry._ID);

                try {

                    SimpleDateFormat formatter = new SimpleDateFormat("dd/MM-yyyy");

                    //If in the past, remove milliseconds resolution
                    if (!isSameDay(currentMonth, Calendar.getInstance()))
                        currentMonth.set(Calendar.MILLISECOND, 0);

                    EnergyUsageModel euModel = new EnergyUsageModel();
                    euModel.setTimeStampFromDate(formatter.parse(mTextDate.getText().toString()));
                    euModel.setDeviceId(item.getLong(pos));
                    euModel.setPowerUsage(value);
                    euModel.setDeleted(false);

                    if (mTotalEnergyPresenter.addEnergyData(getActivity().getContentResolver(), euModel) != null)
                        Log.v(TAG, "Added object to database:\n" + euModel);

                    Utils.makeLongToast(getActivity().getApplicationContext(), "Usage added for device: " + item.getString(
                            item.getColumnIndex(DeviceModel.DeviceEntry.COLUMN_NAME)));

                } catch (ParseException e) {
                    e.printStackTrace();
                    Utils.makeShortToast(getActivity().getApplicationContext(), "Unable to parse the date");
                }

                InputMethodManager imm = (InputMethodManager)getActivity().getSystemService(
                        getActivity().getApplicationContext().INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(mKwhField.getWindowToken(), 0);
            }
        });

        // Double clicks will make the app crash. So OnOneOffClickListener is used instead.
        //TODO BUG: The textView within the relativeLayout swallows the onClick
        mRelativeLayout.setOnClickListener(new OnOneOffClickListener() {
            @Override
            public void onOneClick(View v) {
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

        //mButtonAddUsage.setEnabled(false);

        getLoaderManager().initLoader(0, null, this);

        updateDateText();
        return view;
    }

    private void updateDateText(){
        mTextDate.setText(formatter.format(currentMonth.getTime()));
    }

    /**
     * Checks if two Calendar objects is on the same day or not.
     *
     * @param other
     * @param that
     * @return
     */
    private boolean isSameDay(Calendar other, Calendar that){

        return ((other.get(Calendar.YEAR) == that.get(Calendar.YEAR)) &&
                (other.get(Calendar.MONTH) == that.get(Calendar.MONTH)) &&
                (other.get(Calendar.DAY_OF_MONTH) == that.get(Calendar.DAY_OF_MONTH)));
    }

    @Override
    public void setDate(int year, int month, int day) {
        currentMonth.set(Calendar.YEAR, year);
        currentMonth.set(Calendar.MONTH, month);
        currentMonth.set(Calendar.DAY_OF_MONTH, day);
        updateDateText();
        mRelativeLayout.setEnabled(true);
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

        // Only enable adding of data if we have devices to add data to.
        boolean enableAdding = (cursor.getCount() > 0);

        spinnerDevice.setEnabled(enableAdding);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> cursorLoader) {
        mDeviceAdapter.swapCursor(null);
    }
}