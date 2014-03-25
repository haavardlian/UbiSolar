package com.sintef_energy.ubisolar.fragments.graphs;

import android.app.Activity;
import android.app.Fragment;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.view.ViewGroup.LayoutParams;
import android.widget.TextView;

import com.sintef_energy.ubisolar.IView.IUsageView;
import com.sintef_energy.ubisolar.R;
import com.sintef_energy.ubisolar.database.energy.EnergyUsageModel;
import com.sintef_energy.ubisolar.model.DeviceUsageList;

import org.achartengine.ChartFactory;
import org.achartengine.GraphicalView;
import org.achartengine.model.CategorySeries;
import org.achartengine.model.SeriesSelection;
import org.achartengine.renderer.DefaultRenderer;
import org.achartengine.renderer.SimpleSeriesRenderer;

import java.util.ArrayList;

/**
 * Created by perok on 2/11/14.
 */
public class UsageGraphPieFragment extends Fragment implements IUsageView {

    private static final String ARG_SECTION_NUMBER = "section_number";
    public static final String TAG = UsageGraphLineFragment.class.getName();

    private View rootView;

    private static int[] COLORS = new int[] { Color.GREEN, Color.BLUE,Color.MAGENTA, Color.CYAN,
            Color.RED, Color.YELLOW};
    private CategorySeries mSeries = new CategorySeries("");
    private DefaultRenderer mRenderer = new DefaultRenderer();
    private GraphicalView mChartView;
    private ArrayList<DeviceUsageList> mDeviceUsageList;
    private Bundle mSavedState;
    private int mSelected = -1;

    private String[] selectedItems;
    private boolean[] selectedDialogItems;
    /**
     * Returns a new instance of this fragment for the given section
     * number.
     */
    public static UsageGraphPieFragment newInstance() {
        UsageGraphPieFragment fragment = new UsageGraphPieFragment();
        Bundle args = new Bundle();
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
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        rootView = inflater.inflate(R.layout.fragment_usage_graph_pie, container, false);
        return rootView;
    }


    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        mChartView = null;

        if(savedInstanceState != null && mSavedState == null)
            mSavedState = savedInstanceState.getBundle("mSavedState");


        if (mSavedState != null) {
            mDeviceUsageList = (ArrayList<DeviceUsageList>) mSavedState.getSerializable("mDeviceUsageList");
            mRenderer = (DefaultRenderer) mSavedState.getSerializable("mRenderer");
            mSeries = (CategorySeries) mSavedState.getSerializable("mSeries");
            mSelected = mSavedState.getInt("mSelected");
        }
        else
        {
            setupPieGraph();
        }

        createPieGraph();
        updateDetails();
//        ArrayList<ArrayList<DeviceUsage>> usageList = createDeviceUsage(mDevices);
//        populatePieChart(mDevices, usageList);
    }

    /*End lifecycle*/
    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putBundle("mSavedState", mSavedState != null ? mSavedState : saveState());
    }

    private Bundle saveState(){
        Bundle state = new Bundle();

        state.putSerializable("mDeviceUsageList", mDeviceUsageList);
        state.putSerializable("mRenderer", mRenderer);
        state.putSerializable("mSeries", mSeries);
        state.putInt("mSelected", mSelected);

        return state;
    }

    @Override
    public void onDestroy(){
        super.onDestroy();
   }

    @Override
    public void onDestroyView(){
        super.onDestroy();

        mSavedState = saveState();
        Log.v(TAG, " onDestroyView()");
    }

    @Override
    public void newData(EnergyUsageModel euModel) {

    }

    private void setupPieGraph()
    {
        mRenderer.setChartTitle(getString(R.string.pie_chart_title));
        mRenderer.setChartTitleTextSize(20);
        mRenderer.setLabelsTextSize(15);
        mRenderer.setLegendTextSize(15);

        mRenderer.setApplyBackgroundColor(true);
        mRenderer.setBackgroundColor(Color.WHITE);
        mRenderer.setLabelsColor(Color.BLACK);

        mRenderer.setMargins(new int[] { 20, 30, 15, 0 });
        mRenderer.setStartAngle(90);
        mRenderer.setZoomEnabled(false);
        mRenderer.setPanEnabled(false);
        mRenderer.setShowLegend(false);
        mRenderer.setClickEnabled(true);
        mRenderer.setSelectableBuffer(10);
    }

    private void createPieGraph()
    {
        if (mChartView == null) {
            LinearLayout layout = (LinearLayout) rootView.findViewById(R.id.pieChartView);
            mChartView = ChartFactory.getPieChartView(rootView.getContext(), mSeries, mRenderer);

            mChartView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    SeriesSelection seriesSelection = mChartView.getCurrentSeriesAndPoint();

                    if (seriesSelection != null) {
                        for (int i = 0; i < mSeries.getItemCount(); i++) {
                            if(mRenderer.getSeriesRendererAt(i).isHighlighted() && i == seriesSelection.getPointIndex())
                            {
                                mRenderer.getSeriesRendererAt(i).setHighlighted(false);
                                mSelected = -1;
                                clearDetails();
                            }
                            else if(i == seriesSelection.getPointIndex())
                            {
                                mRenderer.getSeriesRendererAt(i).setHighlighted(true);
                                mSelected = seriesSelection.getPointIndex();
                                updateDetails();
                            }
                            else
                            {
                                mRenderer.getSeriesRendererAt(i).setHighlighted(false);
                            }
                        }
                        mChartView.repaint();
                    }
                }
            });

//            mChartView.setOnLongClickListener(new View.OnLongClickListener() {
//                @Override
//                public boolean onLongClick(View v) {
//                    SeriesSelection seriesSelection = mChartView.getCurrentSeriesAndPoint();
//                    if (seriesSelection == null) {
//                        Toast.makeText(PieActivity.this,"No chart element was long pressed", Toast.LENGTH_SHORT);
//                        return false;
//                    } else {
//                        Toast.makeText(PieActivity.this,"Chart element data point index "+ seriesSelection.getPointIndex()+ " was long pressed",Toast.LENGTH_SHORT);
//                        return true;
//                    }
//                }
//            });
            layout.addView(mChartView, new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));
        }
        else {
            mChartView.repaint();
        }
    }

    private void populatePieChart()
    {
        double totalPowerUsage = 0;

        for(DeviceUsageList deviceUsageList : mDeviceUsageList)
            totalPowerUsage += deviceUsageList.getTotalUsage();

        for(DeviceUsageList deviceUsageList : mDeviceUsageList)
        {
            int percentage = (int) ((deviceUsageList.getTotalUsage() / totalPowerUsage) * 100);
            deviceUsageList.setPercentage(percentage);

            mSeries.add(deviceUsageList.getDevice().getName() + " - " + (int) percentage + "%",
                    deviceUsageList.getTotalUsage());
            SimpleSeriesRenderer renderer = new SimpleSeriesRenderer();
            renderer.setColor(COLORS[(mSeries.getItemCount() - 1) % COLORS.length]);
            mRenderer.addSeriesRenderer(renderer);
        }

        mChartView.repaint();
    }

    @Override
    public void addDeviceUsage(ArrayList<DeviceUsageList> usageList)
    {
        mDeviceUsageList = usageList;
        for(DeviceUsageList u : mDeviceUsageList)
            u.calculateTotalUsage();
        populatePieChart();
    }

    @Override
    public void clearDevices() {
        mRenderer.removeAllRenderers();
        mSeries.clear();
        mChartView.repaint();

    }

    private void updateDetails()
    {
        if(mSelected > -1 && mSelected < mDeviceUsageList.size()) {
            TextView nameView = (TextView) rootView.findViewById(R.id.pieDetailsName);
            TextView descriptionView = (TextView) rootView.findViewById(R.id.pieDetailsDescription);
            TextView powerUsageView = (TextView) rootView.findViewById(R.id.pieDetailsPowerUsage);

            DeviceUsageList usageList = mDeviceUsageList.get(mSelected);

            nameView.setText(usageList.getDevice().getName());
            descriptionView.setText(usageList.getDevice().getDescription());
            powerUsageView.setText(usageList.getTotalUsage() + " kWh (" + usageList.getPercentage() + "%)");
        }
    }

    private void clearDetails()
    {
        TextView nameView = (TextView) rootView.findViewById(R.id.pieDetailsName);
        TextView descriptionView = (TextView) rootView.findViewById(R.id.pieDetailsDescription);
        TextView powerUsageView = (TextView) rootView.findViewById(R.id.pieDetailsPowerUsage);

        nameView.setText("");
        descriptionView.setText("");
        powerUsageView.setText("");
    }

    @Override
    public String[] getSelectedItems() {
        if(selectedItems == null)
            return new String[0];
        else
            return selectedItems;
    }

    @Override
    public void setSelectedItems(String[] selectedItems) {
        this.selectedItems = selectedItems;
    }

    @Override
    public boolean[] getSelectedDialogItems() {
        if(selectedDialogItems == null)
            return new boolean[0];
        else
            return selectedDialogItems;
    }

    @Override
    public void setSelectedDialogItems(boolean[] selectedDialogItems) {
        this.selectedDialogItems = selectedDialogItems;
    }

    @Override
    public void redraw() {
        if(mChartView != null)
            mChartView.repaint();
    }
}
