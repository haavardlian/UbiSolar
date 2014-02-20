package com.sintef_energy.ubisolar.activities;

import android.app.Activity;
import android.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.sintef_energy.ubisolar.R;
import com.sintef_energy.ubisolar.database.energy.EnergyContract;

import java.text.SimpleDateFormat;
import java.util.Calendar;

public class AddDeviceEnergyActivity extends Activity {

    private static final String LOG = AddDeviceEnergyActivity.class.getName();

    public static final String INTENT_KWH = "com.sintef_energy_ubisolar.intent.kwh";
    public static final String INTENT_START = "com.sintef_energy_ubisolar.intent.start";
    public static final String INTENT_END = "com.sintef_energy_ubisolar.intent.end";

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
     */
    public static class PlaceholderFragment extends Fragment {

        private static final String TAG = PlaceholderFragment.class.getName();

        private Calendar currentMonth;
        private Calendar now;

        private TextView text;

        private SimpleDateFormat formatter;

        public PlaceholderFragment() {
        }

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
            currentMonth.set(Calendar.DAY_OF_MONTH, 1);
            currentMonth.set(Calendar.MINUTE, 0);
            currentMonth.set(Calendar.HOUR_OF_DAY, 0);
            currentMonth.set(Calendar.MILLISECOND, 0);

            now = Calendar.getInstance();

            formatter = new SimpleDateFormat("yyyy MM dd");

            Button buttonLeft = (Button)getActivity().findViewById(R.id.fragment_add_device_energy_button_left);
            Button buttonRight = (Button)getActivity().findViewById(R.id.fragment_add_device_energy_button_right);
            Button button = (Button)getActivity().findViewById(R.id.fragment_add_device_energy_button_submit);
            text = (TextView)getActivity().findViewById(R.id.fragment_add_device_energy_textview);

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
                        float value = Float.valueOf(text);
                        resultInt.putExtra(INTENT_KWH, value);
                        resultInt.putExtra(INTENT_START, currentMonth.getTimeInMillis());
                        currentMonth.add(Calendar.MONTH, 1);
                        resultInt.putExtra(INTENT_END, currentMonth.getTimeInMillis());
                        currentMonth.add(Calendar.MONTH, -1);
                    }
                    getActivity().setResult(Activity.RESULT_OK, resultInt);
                    getActivity().finish();
                }
            });

            updateDateText();
        }

        private void updateDateText(){
            text.setText(formatter.format(currentMonth.getTime()));
        }
    }

}
