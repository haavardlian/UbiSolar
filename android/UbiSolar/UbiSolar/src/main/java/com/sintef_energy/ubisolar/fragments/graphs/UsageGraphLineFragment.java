package com.sintef_energy.ubisolar.fragments.graphs;

import android.app.Activity;
import android.app.Fragment;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.echo.holographlibrary.Line;
import com.echo.holographlibrary.LineGraph;
import com.echo.holographlibrary.LinePoint;
import com.sintef_energy.ubisolar.IView.ITotalEnergyView;
import com.sintef_energy.ubisolar.R;
import com.sintef_energy.ubisolar.activities.DrawerActivity;
import com.sintef_energy.ubisolar.database.energy.EnergyUsageModel;
import com.sintef_energy.ubisolar.presenter.TotalEnergyPresenter;

import java.util.ArrayList;
import java.util.Calendar;

/**
 * Created by perok on 2/11/14.
 */
public class UsageGraphLineFragment extends Fragment implements ITotalEnergyView{
    /**
     * The fragment argument representing the section number for this
     * fragment.
     */
    private static final String ARG_SECTION_NUMBER = "section_number";

    TotalEnergyPresenter presenter;

    ArrayList<EnergyUsageModel> euModels;

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
            // Restore last state for checked position.
        }

        createLineGraph();
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
        presenter.registerListner(this);
        euModels = presenter.getEnergyData();
    }

    @Override
    public void dataRefresh() {

    }

    @Override
    public void newData(EnergyUsageModel euModel) {
        euModels.add(euModel);
        createLineGraph();
    }


    private void createLineGraph(){

        if (euModels.size() < 1)
            return;

        Line l = new Line();

        int minx = Integer.MAX_VALUE;
        int maxx = Integer.MIN_VALUE;

        for(EnergyUsageModel euModel : euModels){
            LinePoint p = new LinePoint();
            Calendar calendar = Calendar.getInstance();
            calendar.setTimeInMillis(euModel.getDateStart());
            int days = calendar.get(Calendar.DAY_OF_YEAR);

            if(days < minx)
                minx = days;

            if(days > maxx)
                maxx = days;

            p.setX(days);
            p.setY(0);

            l.addPoint(p);

            p = new LinePoint();

            calendar.setTimeInMillis(euModel.getDateEnd());
            days = calendar.get(Calendar.DAY_OF_YEAR);
            p.setX(days);
            p.setY(euModel.getPower());

            l.addPoint(p);
            if(days < minx)
                minx = days;

            if(days > maxx)
                maxx = days;
        }
        /*
        LinePoint p = new LinePoint();
        p.setX(0);
        p.setY(5);
        l.addPoint(p);
        p = new LinePoint();
        p.setX(8);
        p.setY(8);
        l.addPoint(p);
        p = new LinePoint();
        p.setX(10);
        p.setY(4);
        l.addPoint(p);*/

        l.setColor(Color.parseColor("#FFBB33"));

        LineGraph li = (LineGraph)getActivity().findViewById(R.id.graph);
        li.addLine(l);
        li.setRangeX(minx, maxx);
        li.setRangeY(0, 50);
        li.setLineToFill(0);
    }

}
