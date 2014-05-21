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

package com.sintef_energy.ubisolar.fragments;

import android.app.Activity;
import android.app.Application;
import android.app.LoaderManager;
import android.content.CursorLoader;
import android.content.Loader;
import android.database.Cursor;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.CursorAdapter;
import android.widget.EditText;
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

public class AddUsageFragment extends DefaultTabFragment implements LoaderManager.LoaderCallbacks<Cursor>, IDateCallback {

    private Calendar mCurrentMonth;
    private View mRootView;
    private RelativeLayout mRelativeLayout;

    private Spinner mSpinnerDevice;
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
        mRootView = inflater.inflate(R.layout.fragment_add_usage, container, false);

        //Set the calendar
        mCurrentMonth = Calendar.getInstance();
        mCurrentMonth.set(Calendar.MINUTE, 0);
        mCurrentMonth.set(Calendar.HOUR_OF_DAY, 0);

        /* Fetch view */
        mSpinnerDevice = (Spinner) mRootView.findViewById(R.id.dialog_add_usage_spinner);
        mRelativeLayout = (RelativeLayout) mRootView.findViewById(R.id.fragment_add_usage_rl_date);
        final TextView mTextDate = (TextView) mRootView.findViewById(R.id.fragment_add_usage_text_date);
        final TextView mKwhField = (EditText) mRootView.findViewById(R.id.dialog_add_usage_edittext_kwh);
        final DatePickerFragment datePicker = new DatePickerFragment();

        Button mButtonAddUsage = (Button) mRootView.findViewById(R.id.btnAddUsage);
        datePicker.setTargetFragment(this, 0);

        /* Set up listeners */
        mButtonAddUsage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String text = mKwhField.getText().toString();

                if(text.length() < 1){
                    Utils.makeLongToast(getActivity().getApplicationContext(),
                            getString(R.string.add_usage_no_usage));
                    return;
                }

                Double value = Double.valueOf(text);
                int pos = mSpinnerDevice.getSelectedItemPosition();

                if(pos == Spinner.INVALID_POSITION){
                    Utils.makeLongToast(getActivity().getApplicationContext(),
                            getString(R.string.add_usage_no_device));
                   return;
                }

                Cursor item = mDeviceAdapter.getCursor();
                item.moveToPosition(pos);
                pos = item.getColumnIndex(DeviceModel.DeviceEntry._ID);

                try {

                    SimpleDateFormat formatter = new SimpleDateFormat("dd/MM-yyyy");

                    //If in the past, remove milliseconds resolution
                    if (!isSameDay(mCurrentMonth, Calendar.getInstance()))
                        mCurrentMonth.set(Calendar.MILLISECOND, 0);

                    EnergyUsageModel euModel = new EnergyUsageModel();
                    euModel.setTimeStampFromDate(formatter.parse(mTextDate.getText().toString()));
                    euModel.setDeviceId(item.getLong(pos));
                    euModel.setPowerUsage(value);
                    euModel.setDeleted(false);

                    if (mTotalEnergyPresenter.addEnergyData(getActivity().getContentResolver(), euModel) != null)

                    Utils.makeLongToast(getActivity().getApplicationContext(),
                            getString(R.string.add_usage_added) + " " + item.getString(
                            item.getColumnIndex(DeviceModel.DeviceEntry.COLUMN_NAME)));

                } catch (ParseException e) {
                    e.printStackTrace();
                    Utils.makeShortToast(getActivity().getApplicationContext(), "Unable to parse the date");
                }

                InputMethodManager imm = (InputMethodManager)getActivity().getSystemService(
                        Application.INPUT_METHOD_SERVICE);
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

//        mButtonKwhDown.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                String temp = String.valueOf(mKwhField.getText());
//                Double value = 1.;
//
//                if(!temp.equals(""))
//                    value = Double.valueOf(temp);
//
//                if(value >= 1.)
//                    value--;
//                mKwhField.setText(String.valueOf(value));
//            }
//        });
//
//        mButtonKwhUp.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                String temp = String.valueOf(mKwhField.getText());
//
//                Double value = 0.;
//
//                if(!temp.equals(""))
//                    value = Double.valueOf(temp);
//
//                value++;
//                mKwhField.setText(String.valueOf(value));
//            }
//        });

        /* Fill spinner with data*/
        mDeviceAdapter = new SimpleCursorAdapter(
                getActivity(),
                R.layout.spinner_layout,
                null,
                new String[]{DeviceModel.DeviceEntry.COLUMN_NAME},
                new int[]{R.id.spinnerTarget},
                CursorAdapter.FLAG_REGISTER_CONTENT_OBSERVER);
        mDeviceAdapter.setDropDownViewResource(R.layout.spinner_layout);

        mSpinnerDevice.setEnabled(false);
        mSpinnerDevice.setAdapter(mDeviceAdapter);

        //mButtonAddUsage.setEnabled(false);

        getLoaderManager().initLoader(0, null, this);

        updateDateText();
        return mRootView;
    }

    private void updateDateText(){
        TextView dateView = (TextView) mRootView.findViewById(R.id.fragment_add_usage_text_date);
        SimpleDateFormat formatter = new SimpleDateFormat("dd/MM-yyyy");
        dateView.setText(formatter.format(mCurrentMonth.getTime()));
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
        mCurrentMonth.set(Calendar.YEAR, year);
        mCurrentMonth.set(Calendar.MONTH, month);
        mCurrentMonth.set(Calendar.DAY_OF_MONTH, day);
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
            DeviceModel.DeviceEntry._ID + " ASC"
        );
    }

    @Override
    public void onLoadFinished(Loader<Cursor> cursorLoader, Cursor cursor) {
        mDeviceAdapter.swapCursor(cursor);

        // Only enable adding of data if we have devices to add data to.
        boolean enableAdding = (cursor.getCount() > 0);

        mSpinnerDevice.setEnabled(enableAdding);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> cursorLoader) {
        mDeviceAdapter.swapCursor(null);
    }
}