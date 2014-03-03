package com.sintef_energy.ubisolar.fragments.graphs;

import android.app.Activity;
import android.app.Fragment;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.sintef_energy.ubisolar.IView.ITotalEnergyView;
import com.sintef_energy.ubisolar.R;
import com.sintef_energy.ubisolar.activities.DrawerActivity;
import com.sintef_energy.ubisolar.database.energy.EnergyUsageModel;
import com.sintef_energy.ubisolar.presenter.TotalEnergyPresenter;

/**
 * Created by perok on 2/11/14.
 */
public class UsageGraphPieFragment extends Fragment implements ITotalEnergyView {
    /**
     * The fragment argument representing the section number for this
     * fragment.
     */
    private static final String ARG_SECTION_NUMBER = "section_number";

    TotalEnergyPresenter presenter;

    /**
     * Returns a new instance of this fragment for the given section
     * number.
     */
    public static UsageGraphPieFragment newInstance() {
        UsageGraphPieFragment fragment = new UsageGraphPieFragment();
        Bundle args = new Bundle();
        //args.putInt(ARG_SECTION_NUMBER, sectionNumber);
        fragment.setArguments(args);
        return fragment;
    }

    public UsageGraphPieFragment() {
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
        View rootView = inflater.inflate(R.layout.fragment_usage_graph_pie, container, false);
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

//        PieGraph pg = (PieGraph)getActivity().findViewById(R.id.graph);
//        PieSlice slice = new PieSlice();
//        slice.setColor(Color.parseColor("#99CC00"));
//        slice.setValue(2);
//        pg.addSlice(slice);
//        slice = new PieSlice();
//        slice.setColor(Color.parseColor("#FFBB33"));
//        slice.setValue(3);
//        pg.addSlice(slice);
//        slice = new PieSlice();
//        slice.setColor(Color.parseColor("#AA66CC"));
//        slice.setValue(8);
//        pg.addSlice(slice);
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

        if(presenter != null)
            presenter.unregisterListener(this);
    }

    public void registerTotalEnergyPresenter(TotalEnergyPresenter presenter){
        this.presenter = presenter;
    }

    @Override
    public void dataRefresh() {

    }

    @Override
    public void newData(EnergyUsageModel euModel) {

    }
}
