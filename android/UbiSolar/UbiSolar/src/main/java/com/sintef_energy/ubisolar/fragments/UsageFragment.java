package com.sintef_energy.ubisolar.fragments;

import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.sintef_energy.ubisolar.R;
import com.sintef_energy.ubisolar.activities.AddDeviceEnergyActivity;
import com.sintef_energy.ubisolar.activities.DrawerActivity;
import com.sintef_energy.ubisolar.database.energy.EnergyUsageModel;
import com.sintef_energy.ubisolar.fragments.graphs.UsageGraphLineFragment;
import com.sintef_energy.ubisolar.fragments.graphs.UsageGraphPieFragment;
import com.sintef_energy.ubisolar.presenter.TotalEnergyPresenter;

import java.util.Calendar;

/**
 * Created by perok on 2/11/14.
 */
public class UsageFragment extends Fragment {

    private static final String LOG = UsageFragment.class.getName();

    /**
     * The fragment argument representing the section number for this
     * fragment.
     */
    private static final String ARG_SECTION_NUMBER = "section_number";

    /**
     * Boolean telling if the graph showing total usage is shown, or the pie graph for devices.
     */
    private boolean showingTotalUsage = true;

    /**
     * Presenter
     */
    private TotalEnergyPresenter totalEnergyPresenter;

    /**
     * Returns a new instance of this fragment for the given section
     * number.
     */
    public static UsageFragment newInstance(int sectionNumber) {
        UsageFragment fragment = new UsageFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_SECTION_NUMBER, sectionNumber);
        fragment.setArguments(args);
        return fragment;
    }

    public UsageFragment() {
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
        //return super.onCreateView(inflater, container, savedInstanceState);
        View rootView = inflater.inflate(R.layout.fragment_usage, container, false);
        //TextView textView = (TextView) rootView.findViewById(R.id.section_label);
        //textView.setText(Integer.toString(getArguments().getInt(ARG_SECTION_NUMBER)));
        return rootView;
    }


    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        if (savedInstanceState != null) {
            // Restore last state for checked position.
        }

        totalEnergyPresenter = new TotalEnergyPresenter();

        /* Show fragment */
        UsageGraphLineFragment fragment = UsageGraphLineFragment.newInstance();
        fragment.registerTotalEnergyPresenter(totalEnergyPresenter);
        FragmentTransaction ft = getFragmentManager().beginTransaction();
        ft.replace(R.id.fragment_usage_tab_graph_placeholder, fragment);
        ft.commit();

        /* Button listeners*/
        Button periodButton = (Button)getActivity().findViewById(R.id.fragment_usage_btn_change_date);
        periodButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });

        final Button deviceButton = (Button)getActivity().findViewById(R.id.fragment_usage_btn_devices);
        deviceButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(showingTotalUsage){
                    showingTotalUsage = false;
                    UsageGraphPieFragment fragment = UsageGraphPieFragment.newInstance();
                    fragment.registerTotalEnergyPresenter(totalEnergyPresenter);
                    FragmentTransaction ft = getFragmentManager().beginTransaction();
                    ft.replace(R.id.fragment_usage_tab_graph_placeholder, fragment);
                    ft.commit();

                    deviceButton.setText(getResources().getString(R.string.totalUsage));
                }
                else{
                    showingTotalUsage = true;
                    UsageGraphLineFragment fragment = UsageGraphLineFragment.newInstance();
                    fragment.registerTotalEnergyPresenter(totalEnergyPresenter);
                    FragmentTransaction ft = getFragmentManager().beginTransaction();
                    ft.replace(R.id.fragment_usage_tab_graph_placeholder, fragment);
                    ft.commit();
                    deviceButton.setText(getResources().getString(R.string.devices));
                }
            }
        });


        Intent intent = new Intent(this.getActivity(), AddDeviceEnergyActivity.class);

        startActivityForResult(intent, 0);
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch(requestCode) {
            case (0) : {
                if (resultCode == Activity.RESULT_OK) {
                    // TODO Extract the data returned from the child Activity.
                    double value = data.getDoubleExtra(AddDeviceEnergyActivity.INTENT_KWH, -1);
                    Log.v(LOG, String.valueOf(value));

                    Calendar calendar = Calendar.getInstance();

                    EnergyUsageModel euModel = new EnergyUsageModel();
                    euModel.setDateStart(calendar.getTimeInMillis());

                    calendar.add(Calendar.MONTH, 4);
                    euModel.setDateEnd(calendar.getTimeInMillis());
                    euModel.setPower(value);

                    totalEnergyPresenter.addEnergyData(euModel);
                }
                break;
            }
            default:

        }
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
}
