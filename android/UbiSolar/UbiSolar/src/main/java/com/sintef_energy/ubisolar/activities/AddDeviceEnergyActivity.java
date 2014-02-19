package com.sintef_energy.ubisolar.activities;

import android.app.Activity;
import android.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import com.sintef_energy.ubisolar.R;
import com.sintef_energy.ubisolar.utils.Log;

public class AddDeviceEnergyActivity extends Activity {

    private static final String LOG = AddDeviceEnergyActivity.class.getName();

    public static final String INTENT_KWH = "com.sintef_energy_ubisolar.intent.kwh";

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

            Button button = (Button)getActivity().findViewById(R.id.fragment_add_device_energy_button_submit);

            button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent resultInt = new Intent();

                    EditText edittext = (EditText) getActivity().findViewById(R.id.fragment_add_device_energy_edittext_kwh);

                    String text = edittext.getText().toString();
                    Log.v(LOG, "Textfield value: " + text);

                    if(text.length() > 0){
                        double value = Double.valueOf(text);
                        resultInt.putExtra(INTENT_KWH, value);
                    }

                    getActivity().setResult(Activity.RESULT_OK, resultInt);
                    getActivity().finish();
                }
            });
        }
    }

}
