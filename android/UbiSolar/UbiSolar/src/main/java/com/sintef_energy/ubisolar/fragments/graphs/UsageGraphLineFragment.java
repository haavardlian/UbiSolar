package com.sintef_energy.ubisolar.fragments.graphs;

import android.app.Activity;
import android.app.Fragment;
import android.graphics.Color;
import android.graphics.Point;
import android.os.Bundle;
import android.os.Parcelable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.sintef_energy.ubisolar.IView.ITotalEnergyView;
import com.sintef_energy.ubisolar.R;
import com.sintef_energy.ubisolar.activities.DrawerActivity;
import com.sintef_energy.ubisolar.database.energy.EnergyUsageModel;
import com.sintef_energy.ubisolar.presenter.TotalEnergyPresenter;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;

/**
 * Created by perok on 2/11/14.
 */
public class UsageGraphLineFragment extends Fragment implements ITotalEnergyView{
    public static final String TAG = UsageGraphLineFragment.class.getName();

    /**
     * The fragment argument representing the section number for this
     * fragment.
     */
    private static final String ARG_SECTION_NUMBER = "section_number";

    TotalEnergyPresenter presenter;

    ArrayList<EnergyUsageModel> euModels;
    private static final String STATE_euModels = "STATE_euModels";

    private final static long MILLISECS_PER_DAY = 24 * 60 * 60 * 1000;

    /**
     * Returns a new instance of this fragment for the given section
     * number.
     */
    public static UsageGraphLineFragment newInstance() {
        UsageGraphLineFragment fragment = new UsageGraphLineFragment();
        Bundle args = new Bundle();
        //args.putInt(ARG_SECTION_NUMBER, sectionNumber);
        fragment.setArguments(args);
        return fragment;
    }

    public UsageGraphLineFragment() {
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
        View rootView = inflater.inflate(R.layout.fragment_usage_graph_line, container, false);
        //TextView textView = (TextView) rootView.findViewById(R.id.section_label);
        //textView.setText(Integer.toString(getArguments().getInt(ARG_SECTION_NUMBER)));
        return rootView;
    }


    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        if (savedInstanceState != null) {
            ArrayList<Parcelable> state = savedInstanceState.getParcelableArrayList(STATE_euModels);
            if(state != null){
                euModels = new ArrayList<>();
                for(Parcelable p : state){}
                    //euModels.add(EnergyUsageModel.CREATOR.createFromParcel(p.));
            }

            // Restore last state for checked position.
        }

        createLineGraph();
    }

    /*End lifecycle*/
    @Override
    public void onSaveInstanceState(Bundle outState) {

        ArrayList<Parcelable> usageModelState = new ArrayList<>();
        for(EnergyUsageModel euModel : euModels)
            usageModelState.add(euModel);

        outState.putParcelableArrayList(STATE_euModels, usageModelState);
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
        presenter.registerListner(this);
        euModels = presenter.getEnergyData();

        Log.v(TAG, "registerTotalEnergypresenter: " + euModels.size());
    }

    @Override
    public void dataRefresh() {

    }

    @Override
    public void newData(EnergyUsageModel euModel) {
        //euModels.add(euModel);
        createLineGraph();
    }


    private void createLineGraph(){

        Log.v(TAG, "createLineGraph: " + euModels.size());
        if (euModels.size() < 1)
            return;

//        Line l = new Line();
//
//        float maxy = Float.MIN_VALUE;
//        LinePoint p;

        int days = 0;
        float power;

        long currentMillies = 0;

        SimpleDateFormat sFormatter = new SimpleDateFormat("yyyy MM dd");

        for(EnergyUsageModel euModel : euModels){

//            Log.v(TAG, "Start: " + sFormatter.format(euModel.getDateStart()) + "  End: " + sFormatter.format(euModel.getDateEnd()));
//            power = euModel.getPower();
//            if(power > maxy)
//                maxy = euModel.getPower();
//
//            //Correct for first run.
//            if(currentMillies == 0)
//                currentMillies = euModel.getDateStart();
//
//            long nowMillies = euModel.getDateStart();
//            days += Math.abs((nowMillies - currentMillies) / MILLISECS_PER_DAY);
//            currentMillies = nowMillies;
//
//            //Add start
//            p = new LinePoint(days, power);
//            Log.v(TAG, "A : " + days);
//            l.addPoint(p);

            /* END */

//            nowMillies = euModel.getDateEnd();
//            days += Math.abs((nowMillies - currentMillies) / MILLISECS_PER_DAY);
//            currentMillies = nowMillies;
//            //Add end
//            p = new LinePoint(days, power);
//            Log.v(TAG, "B : " + days);
//            l.addPoint(p);
        }

//        l.setColor(Color.parseColor("#FFBB33"));
//
//        LineGraph li = (LineGraph)getActivity().findViewById(R.id.graph);
//        li.removeAllLines();
//        li.addLine(l);
//        li.setRangeX(0, days + 1);
//        li.setRangeY(0, maxy + (maxy / 10));
//        li.setLineToFill(0);
    }
}
