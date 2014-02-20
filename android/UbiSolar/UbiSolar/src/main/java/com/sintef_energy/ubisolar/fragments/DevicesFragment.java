package com.sintef_energy.ubisolar.fragments;

import android.app.Activity;
import android.app.Fragment;
import android.app.LoaderManager;
import android.content.CursorLoader;
import android.content.Loader;
import android.database.Cursor;
import android.os.Bundle;
import android.provider.BaseColumns;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;

import com.sintef_energy.ubisolar.R;
import com.sintef_energy.ubisolar.activities.DrawerActivity;
import com.sintef_energy.ubisolar.database.energy.DeviceModel;
import com.sintef_energy.ubisolar.database.energy.EnergyContract;
import com.sintef_energy.ubisolar.database.energy.EnergyDataSource;

import java.util.ArrayList;

/**
 * Created by perok on 2/11/14.
 */
public class DevicesFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor> {
    /**
     * The fragment argument representing the section number for this
     * fragment.
     */
    private static final String ARG_SECTION_NUMBER = "section_number";

    Button addButton;
    EditText nameField, descriptionField, usageField;
    SimpleCursorAdapter adapter;
    ArrayList<DeviceModel> devices;

    /**
     * Returns a new instance of this fragment for the given section
     * number.
     */
    public static DevicesFragment newInstance(int sectionNumber) {
        DevicesFragment fragment = new DevicesFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_SECTION_NUMBER, sectionNumber);
        fragment.setArguments(args);
        return fragment;
    }

    public DevicesFragment() {
    }

    /**
     * The first call to a created fragment
     * @param activity
     */
    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        //Callback to activity
        ((DrawerActivity) activity).onSectionAttached(getArguments().getInt(ARG_SECTION_NUMBER));
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View listView = inflater.inflate(R.layout.fragment_device, container, false);


        return listView;
        //View rootView = inflater.inflate(R.layout.fragment_test, container, false);
        //TextView textView = (TextView) rootView.findViewById(R.id.section_label);
        //textView.setText(Integer.toString(getArguments().getInt(ARG_SECTION_NUMBER)));
        //return rootView;
    }



    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        final ListView listView = (ListView) getActivity().findViewById(R.id.device_list);
        devices = new ArrayList<DeviceModel>();

        adapter = new SimpleCursorAdapter(getActivity().getApplicationContext(),
                android.R.layout.simple_list_item_2,
                null,
                new String[]{DeviceModel.DeviceEntry.COLUMN_NAME},
                new int[]{android.R.id.text2}, 0);
        listView.setAdapter(adapter);

        nameField = (EditText) getActivity().findViewById(R.id.edit_name);
        descriptionField = (EditText) getActivity().findViewById(R.id.edit_description);
        usageField = (EditText) getActivity().findViewById(R.id.edit_usage);
        addButton = (Button) getActivity().findViewById(R.id.add_button);

        EnergyDataSource.deleteAll(getActivity().getContentResolver());

        addButton.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View view) {
                adapter = null;


                DeviceModel deviceModel = new DeviceModel();
                deviceModel.setId(System.currentTimeMillis());
                deviceModel.setDescription(descriptionField.getText().toString());
                deviceModel.setName(nameField.getText().toString());
                devices.add(deviceModel);
                //devices.add(deviceModel);

                EnergyDataSource.insertDevice(getActivity().getContentResolver(), deviceModel);


                adapter = new SimpleCursorAdapter(getActivity().getApplicationContext(),
                        android.R.layout.simple_list_item_2,
                        null,
                        new String[]{DeviceModel.DeviceEntry.COLUMN_NAME},
                        new int[]{android.R.id.text2}, 0);
                listView.setAdapter(adapter);
                adapter.notifyDataSetChanged();
            }
        });






        if (savedInstanceState != null) {
            // Restore last state for checked position.


        }


        getLoaderManager().initLoader(0, null, this);

    }

    /*End lifecycle*/
    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        //outState.putInt("curChoice", mCurCheckPosition);
    }

    @Override
    public void onDestroy(){
        super.onDestroy();
    }

    @Override
    public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {
        return new CursorLoader(getActivity(), EnergyContract.Devices.CONTENT_URI,
                EnergyContract.Devices.PROJECTION_ALL, null, null,
                BaseColumns._ID + " ASC");
    }

    @Override
    public void onLoadFinished(Loader<Cursor> cursorLoader, Cursor cursor) {
        ((SimpleCursorAdapter) this.adapter).swapCursor(cursor);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> cursorLoader) {
        ((SimpleCursorAdapter)this.adapter).swapCursor(null);
    }
}