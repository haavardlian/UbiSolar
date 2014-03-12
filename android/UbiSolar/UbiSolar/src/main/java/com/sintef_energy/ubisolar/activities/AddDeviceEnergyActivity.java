package com.sintef_energy.ubisolar.activities;

import android.app.Activity;
import android.app.Fragment;
import android.app.LoaderManager;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CursorAdapter;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.SimpleCursorAdapter;
import android.widget.Spinner;
import android.widget.TextView;

import com.sintef_energy.ubisolar.IView.IDateCallback;
import com.sintef_energy.ubisolar.R;
import com.sintef_energy.ubisolar.database.energy.DeviceModel;
import com.sintef_energy.ubisolar.database.energy.EnergyContract;
import com.sintef_energy.ubisolar.dialogs.DatePickerFragment;

import java.text.SimpleDateFormat;
import java.util.Calendar;

@Deprecated
public class AddDeviceEnergyActivity extends Activity {

    private static final String LOG = AddDeviceEnergyActivity.class.getName();

    public static final String INTENT_KWH = "com.sintef_energy_ubisolar.intent.kwh";
    public static final String INTENT_DATETIME = "com.sintef_energy_ubisolar.intent.start";
    public static final String INTENT_DEVICE_ID = "com.sintef_energy_ubisolar.intent.device_id";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_add_device_energy);

        if (savedInstanceState == null) {
            getFragmentManager().beginTransaction()
                    .add(R.id.container, new PlaceholderFragment())
                    .commit();
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.add_device_energy, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }




    /**
     * A placeholder fragment containing a simple view.
     * TODO: Remove old buttons that are currently set as gone.
     * TODO: Rewrite to dialog.
     */
    public static class PlaceholderFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor>, IDateCallback {

        private static final String TAG = PlaceholderFragment.class.getName();

        private Calendar currentMonth;
        private Calendar now;

        private TextView text;
        private Spinner spinnerDevice;
        private SimpleDateFormat formatter;
        private Button buttonLeft;
        private Button buttonRight;
        private Button button;
        private ImageButton mButtonDate;

        private SimpleCursorAdapter mDeviceAdapter;

        public PlaceholderFragment() {}

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.fragment_add_device_energy, container, false);
            return rootView;
        }

        @Override
        public void onActivityCreated(Bundle savedInstanceState) {
            super.onActivityCreated(savedInstanceState);

            currentMonth = Calendar.getInstance();
            //currentMonth.set(Calendar.DAY_OF_MONTH, 1);
            currentMonth.set(Calendar.MINUTE, 0);
            currentMonth.set(Calendar.HOUR_OF_DAY, 0);
            currentMonth.set(Calendar.MILLISECOND, 0);

            now = Calendar.getInstance();

            formatter = new SimpleDateFormat("yyyy MM dd");

            /* Fetch view */
            buttonLeft = (Button)getActivity().findViewById(R.id.fragment_add_device_energy_button_left);
            buttonRight = (Button)getActivity().findViewById(R.id.fragment_add_device_energy_button_right);
            button = (Button)getActivity().findViewById(R.id.fragment_add_device_energy_button_submit);
            text = (TextView)getActivity().findViewById(R.id.fragment_add_device_energy_textview);
            spinnerDevice = (Spinner)getActivity().findViewById(R.id.fragment_add_device_energy_spinner);
            mButtonDate = (ImageButton)getActivity().findViewById(R.id.fragment_add_device_energy_button_set_date);

            /* Set up listeners */
            buttonLeft.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    currentMonth.add(Calendar.MONTH, -1);
                    updateDateText();
                }
            });

            buttonRight.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    currentMonth.add(Calendar.MONTH, 1);

                    if(currentMonth.getTimeInMillis() > now.getTimeInMillis()){
                        currentMonth.add(Calendar.MONTH, -1);
                    }
                    else
                        updateDateText();
                }
            });


            button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent resultInt = new Intent();

                    EditText edittext = (EditText) getActivity().findViewById(R.id.fragment_add_device_energy_edittext_kwh);

                    String text = edittext.getText().toString();
                    Log.v(LOG, "Textfield value: " + text);

                    if(text.length() > 0){
                        Double value = Double.valueOf(text);
                        resultInt.putExtra(INTENT_KWH, value);
                        //TODO: Added only on month accuracy
                        resultInt.putExtra(INTENT_DATETIME, currentMonth.getTimeInMillis());

                        int pos = spinnerDevice.getSelectedItemPosition();
                        Cursor item = mDeviceAdapter.getCursor();
                        item.moveToPosition(pos);
                        pos = item.getColumnIndex(DeviceModel.DeviceEntry.COLUMN_NAME);

                        resultInt.putExtra(INTENT_DEVICE_ID, item.getInt(pos));
                    }
                    getActivity().setResult(Activity.RESULT_OK, resultInt);
                    getActivity().finish();
                }
            });

            //Only enable when we have device data.
            button.setEnabled(false);

            final DatePickerFragment datePicker = new DatePickerFragment();
            datePicker.setTargetFragment(this, 0);

            mButtonDate.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Calendar calender = Calendar.getInstance();
                    Bundle args = new Bundle();
                    args.putInt("year", calender.get(Calendar.YEAR));
                    args.putInt("month", calender.get(Calendar.MONTH));
                    args.putInt("day", calender.get(Calendar.DAY_OF_MONTH));
                    datePicker.setArguments(args);

                    datePicker.show(getFragmentManager(), "datePicker");
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
        }

        private void updateDateText(){
            text.setText(formatter.format(currentMonth.getTime()));
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
                button.setEnabled(true);
            }
            else {
                spinnerDevice.setEnabled(false);
                button.setEnabled(false);
            }
        }

        @Override
        public void onLoaderReset(Loader<Cursor> cursorLoader) {
            mDeviceAdapter.swapCursor(null);
        }

        @Override
        public void setDate(int year, int month, int day) {
            currentMonth.set(Calendar.YEAR, year);
            currentMonth.set(Calendar.MONTH, month);
            currentMonth.set(Calendar.DAY_OF_MONTH, day);

            updateDateText();
        }
    }

}
