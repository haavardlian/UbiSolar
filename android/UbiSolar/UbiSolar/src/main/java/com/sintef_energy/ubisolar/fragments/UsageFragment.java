package com.sintef_energy.ubisolar.fragments;

import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentTransaction;
import android.content.ContentResolver;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;

import com.sintef_energy.ubisolar.IView.IPresenterCallback;
import com.sintef_energy.ubisolar.R;
import com.sintef_energy.ubisolar.activities.DrawerActivity;
import com.sintef_energy.ubisolar.database.energy.DeviceModel;
import com.sintef_energy.ubisolar.database.energy.EnergyContract;
import com.sintef_energy.ubisolar.database.energy.EnergyDataSource;
import com.sintef_energy.ubisolar.database.energy.EnergyUsageModel;
import com.sintef_energy.ubisolar.dialogs.AddUsageDialog;
import com.sintef_energy.ubisolar.fragments.graphs.UsageGraphLineFragment;
import com.sintef_energy.ubisolar.fragments.graphs.UsageGraphPieFragment;
import com.sintef_energy.ubisolar.presenter.TotalEnergyPresenter;

import java.util.Calendar;
import java.util.Random;

/**
 * Created by perok on 2/11/14.
 */
public class UsageFragment extends Fragment {

    private static final String TAG = UsageFragment.class.getName();

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
    private TotalEnergyPresenter mTotalEnergyPresenter;

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

        try {
            mTotalEnergyPresenter = ((IPresenterCallback) activity).getmTotalEnergyPresenter();
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString() + " must implement " + UsageFragment.class.getName());
        }

    }

    @Override
    public void onCreate(Bundle bundle){
        //We can alter the option menu
        setHasOptionsMenu(true);

        super.onCreate(bundle);
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

        //TODO: Remove. Only for removal of stupid data.//

        //int it = getActivity().getContentResolver().delete(EnergyContract.Energy.CONTENT_URI, null, null);
        //Log.v(TAG, "EMPTY DATABASE: " + it);
        if(EnergyDataSource.getEnergyModelSize(getActivity().getContentResolver()) == 0){
            DeviceModel dv = new DeviceModel(System.currentTimeMillis(), "Best device ever", "This device... Just sayin.", System.currentTimeMillis());
            getActivity().getContentResolver().insert(EnergyContract.Devices.CONTENT_URI, dv.getContentValues());

            ContentResolver cr = getActivity().getContentResolver();

            EnergyUsageModel eum;
            Calendar cal = Calendar.getInstance();
            Random random = new Random();


            for(int i = 0; i < 2000; i++)
            {
                cal.add(Calendar.HOUR_OF_DAY, i);

                eum = new EnergyUsageModel(System.currentTimeMillis(),dv.getDevice_id() , cal.getTime(), random.nextInt((200 - 50) + 1) + 50);
                EnergyDataSource.addEnergyModel(cr, eum);
            }
        }
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.MONTH, 8);

        /* Show fragment */
        UsageGraphLineFragment fragment = UsageGraphLineFragment.newInstance();
        fragment.registerTotalEnergyPresenter(mTotalEnergyPresenter);
        FragmentTransaction ft = getFragmentManager().beginTransaction();
        ft.replace(R.id.fragment_usage_tab_graph_placeholder, fragment);
        ft.commit();

        /* Button listeners*/
        ImageButton periodButton = (ImageButton)getActivity().findViewById(R.id.usage_button_swap_graph);
        periodButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                toggleGraph(view);
            }
        });

//        final Button deviceButton = (Button)getActivity().findViewById(R.id.fragment_usage_btn_devices);
//        deviceButton.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                if(showingTotalUsage){
//                    showingTotalUsage = false;
//                    UsageGraphPieFragment fragment = UsageGraphPieFragment.newInstance();
//                    fragment.registerTotalEnergyPresenter(mTotalEnergyPresenter);
//                    FragmentTransaction ft = getFragmentManager().beginTransaction();
//                    ft.replace(R.id.fragment_usage_tab_graph_placeholder, fragment);
//                    ft.commit();
//
//                    deviceButton.setText(getResources().getString(R.string.totalUsage));
//                }
//                else{
//                    showingTotalUsage = true;
//                    UsageGraphLineFragment fragment = UsageGraphLineFragment.newInstance();
//                    fragment.registerTotalEnergyPresenter(mTotalEnergyPresenter);
//                    FragmentTransaction ft = getFragmentManager().beginTransaction();
//                    ft.replace(R.id.fragment_usage_tab_graph_placeholder, fragment);
//                    ft.commit();
//                    deviceButton.setText(getResources().getString(R.string.devices));
//                }
//            }
//        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        //Not in use anymore
        switch(requestCode) {
            case (0) : {
                if (resultCode == Activity.RESULT_OK) {}
                break;
            }
            default:

        }
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.fragment_usage_menu, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection
        switch (item.getItemId()) {
            case R.id.fragment_usage_menu_add:
                AddUsageDialog addUsageDialog = new AddUsageDialog();
                addUsageDialog.show(getFragmentManager(), "addUsage");
                return true;
            default:
                return super.onOptionsItemSelected(item);
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

    public void toggleGraph(View view)
    {
        ImageButton button = (ImageButton) getActivity().findViewById(R.id.usage_button_swap_graph);

        if( button.getTag(R.string.graph_tag) == null)
            button.setTag(R.string.graph_tag, "pie");

        //TODO swap graphs
        if(button.getTag(R.string.graph_tag).equals("pie"))
        {
            button.setImageResource(R.drawable.line);
            button.setTag(R.string.graph_tag, "line");
            UsageGraphPieFragment fragment = UsageGraphPieFragment.newInstance();
            fragment.registerTotalEnergyPresenter(mTotalEnergyPresenter);
            FragmentTransaction ft = getFragmentManager().beginTransaction();
            ft.replace(R.id.fragment_usage_tab_graph_placeholder, fragment);

            ft.commit();
        }
        else
        {
            button.setImageResource(R.drawable.pie);
            button.setTag(R.string.graph_tag, "pie");
            UsageGraphLineFragment fragment = UsageGraphLineFragment.newInstance();
            fragment.registerTotalEnergyPresenter(mTotalEnergyPresenter);
            FragmentTransaction ft = getFragmentManager().beginTransaction();
            ft.replace(R.id.fragment_usage_tab_graph_placeholder, fragment);
            ft.commit();
        }
    }

}