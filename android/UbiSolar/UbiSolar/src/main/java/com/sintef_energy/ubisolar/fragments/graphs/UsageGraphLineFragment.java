package com.sintef_energy.ubisolar.fragments.graphs;

import android.app.Activity;
import android.app.Fragment;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Parcelable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.view.ViewGroup.LayoutParams;

import com.sintef_energy.ubisolar.IView.IUsageView;
import com.sintef_energy.ubisolar.R;
import com.sintef_energy.ubisolar.database.energy.EnergyUsageModel;
import com.sintef_energy.ubisolar.fragments.UsageFragment;
import com.sintef_energy.ubisolar.model.DeviceUsage;
import com.sintef_energy.ubisolar.model.DeviceUsageList;

import org.achartengine.ChartFactory;
import org.achartengine.GraphicalView;
import org.achartengine.chart.PointStyle;
import org.achartengine.model.XYMultipleSeriesDataset;
import org.achartengine.model.XYSeries;
import org.achartengine.renderer.XYMultipleSeriesRenderer;
import org.achartengine.renderer.XYSeriesRenderer;
import org.achartengine.tools.PanListener;
import org.achartengine.tools.ZoomEvent;
import org.achartengine.tools.ZoomListener;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

/**
 * Created by perok on 2/11/14.
 */
public class UsageGraphLineFragment extends Fragment implements IUsageView {

    public static final String TAG = UsageGraphLineFragment.class.getName();
    private static final String ARG_SECTION_NUMBER = "section_number";
    private static final String STATE_euModels = "STATE_euModels";

    private static final int POINT_DISTANCE = 15;
    private static final int GRAPH_MARGIN = 20;
    private static final int NUMBER_OF_POINTS = 9;


    private XYMultipleSeriesDataset mDataset = new XYMultipleSeriesDataset();
    private XYMultipleSeriesRenderer mRenderer = new XYMultipleSeriesRenderer();
    private GraphicalView mChartView;
    private ArrayList<DeviceUsageList> mBaseUsageList;
    private ArrayList<DeviceUsageList> mActiveUsageList;
    private String mTitleLabel;
    private String mTitleFormat;
    private String mDataResolution;
    private int mActiveDateIndex = 0;
    private int[] colors = new int[] { Color.GREEN, Color.BLUE,Color.MAGENTA, Color.CYAN, Color.RED,
            Color.YELLOW};
    private int mColorIndex;

    private Bundle mSavedState;
    private UsageFragment mUsageFragment;

    private String[] selectedItems;
    private boolean[] selectedDialogItems;

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
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_usage_graph_line, container, false);
        return rootView;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(mSavedState);

        Log.v(TAG, "onActivityCreated()");

        /* If the Fragment was destroyed inbetween (screen rotation), we need to recover the mSavedState first */
        /* However, if it was not, it stays in the instance from the last onDestroyView() and we don't want to overwrite it */
        if(savedInstanceState != null && mSavedState == null)
            mSavedState = savedInstanceState.getBundle("mSavedState");

        mChartView = null;

        //Restore data
        if(mSavedState != null) {
            mDataset = (XYMultipleSeriesDataset) mSavedState.getSerializable("mDataset");
            mRenderer = (XYMultipleSeriesRenderer) mSavedState.getSerializable("mRenderer");
            mTitleFormat = mSavedState.getString("mTitleFormat");
            mDataResolution = mSavedState.getString("mDataResolution");
            mTitleLabel = mSavedState.getString("mTitleLabel");
            mActiveDateIndex = mSavedState.getInt("mActiveDateIndex");
            mActiveUsageList = (ArrayList<DeviceUsageList>) mSavedState.getSerializable("mActiveUsageList");
            mBaseUsageList = (ArrayList<DeviceUsageList>) mSavedState.getSerializable("mBaseUsageList");
            System.out.println(mActiveDateIndex);
        }
        //Initialize new data
        else {
            setupLineGraph();

            mActiveUsageList = new ArrayList<>();
            mBaseUsageList = new ArrayList<>();
            mTitleFormat = "EEEE dd/MM";
            mDataResolution = "HH";
        }
        createLineGraph();
        populateGraph(mActiveDateIndex);

        mSavedState = null;
    }

    /*End lifecycle*/
    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        System.out.println("Saved");

        /* If onDestroyView() is called first, we can use the previously mSavedState but we can't call saveState() anymore */
        /* If onSaveInstanceState() is called first, we don't have mSavedState, so we need to call saveState() */
        /* => (?:) operator inevitable! */
        outState.putBundle("mSavedState", mSavedState != null ? mSavedState : saveState());
    }

    /**
     * onDestroyView is run when fragment is replaced. Save state here.
     */
    @Override
    public void onDestroyView(){
        super.onDestroy();

        mSavedState = saveState();
        Log.v(TAG, " onDestroyView()");
    }


    private Bundle saveState(){
        Bundle state = new Bundle();

        ArrayList<Parcelable> usageModelState = new ArrayList<>();

        state.putParcelableArrayList(STATE_euModels, usageModelState);
        state.putSerializable("mDataset", mDataset);
        state.putSerializable("mRenderer", mRenderer);
        state.putString("mTitleFormat", mTitleFormat);
        state.putString("mDataResolution", mDataResolution);
        state.putString("mTitleLabel", mTitleLabel);
        state.putInt("mActiveDateIndex", mActiveDateIndex);
        state.putSerializable("mActiveUsageList", mActiveUsageList);
        state.putSerializable("mBaseUsageList", mBaseUsageList);

        return state;
    }

    @Override
    public void onDestroy(){
        super.onDestroy();

        Log.v(TAG, " onDestroy()");
    }

    @Override
    public void newData(EnergyUsageModel euModel) {
        //euModels.add(euModel);
//        createLineGraph();
    }


    /**
     * Sets up XYMultipleSeriesRenderer mRenderer.
     */
    private void setupLineGraph(){
        mRenderer.setChartTitle("Power usage");
//        mRenderer.setYTitle("KWh");

        mRenderer.setAxisTitleTextSize(25);
        mRenderer.setChartTitleTextSize(20);
        mRenderer.setLabelsTextSize(15);
        mRenderer.setLegendTextSize(15);
        mRenderer.setPointSize(5);
        mRenderer.setXLabels(0);
        mRenderer.setXLabelsPadding(10);
        mRenderer.setYLabelsPadding(20);
        mRenderer.setMargins(new int[]{ 20, 40, 35, 20 });

        setColors(Color.WHITE, Color.BLACK);
    }

    /**
     * Set up the ChartView and add listeners.
     */
    private void createLineGraph()
    {
        if (mChartView == null) {
            System.out.println("Creating graph");
            LinearLayout layout = (LinearLayout) getActivity().findViewById(R.id.lineChartView);
            mChartView = ChartFactory.getLineChartView(getActivity(), mDataset, mRenderer);
            mChartView.addZoomListener(new ZoomListener() {
                @Override
                public void zoomApplied(ZoomEvent zoomEvent) {
                    double zoom = mRenderer.getXAxisMax()- mRenderer.getXAxisMin();
//                    System.out.println(zoom);
                    if(zoom > 60 && zoom < 90)
                    {
                        zoomIn();
                        System.out.println(mRenderer.getXAxisMax()- mRenderer.getXAxisMin());
                    }
                    if(zoom > 250 && zoom < 270)
                    {
                        zoomOut();
                        System.out.println(mRenderer.getXAxisMax()- mRenderer.getXAxisMin());
                    }
                }

                @Override
                public void zoomReset() {
                }
            }, true, true);
            mChartView.addPanListener(new PanListener() {
                @Override
                public void panApplied() {
                    int activePoint = (int) (mRenderer.getXAxisMin() + mRenderer.getXAxisMax()) / 2;
                    mActiveDateIndex = (int) activePoint / POINT_DISTANCE;
                    if(mActiveDateIndex < 0)
                        return;
                    // If the center point does not match the label, swap it with the new label

                    if(mTitleLabel == null)
                        return;

                    if (!mTitleLabel.equals(formatDate(getActiveDate(), mTitleFormat))) {
                        mTitleLabel = formatDate(getActiveDate(), mTitleFormat);
                        setLabels(formatDate(getActiveDate(), mTitleFormat));
                    }
                }
            });
            layout.addView(mChartView, new LayoutParams(LayoutParams.MATCH_PARENT,
                    LayoutParams.MATCH_PARENT));
        } else {
            mChartView.repaint();
        }
    }

    private Date getActiveDate()
    {
        //Todo FIX
        if(mActiveUsageList.size() <= 0)
            return null;

        if(mActiveUsageList.get(0).size() <= 0)
            return null;

        return mActiveUsageList.get(0).get(mActiveDateIndex).getDatetime();
    }

    /**
     * Define the series renderer
     * @param seriesName The name of the series
     */
    private void addSeries(String seriesName, boolean displayPoints, boolean displayPointValues)
    {
        XYSeries series = new XYSeries(seriesName);
        mDataset.addSeries(series);

        XYSeriesRenderer seriesRenderer = new XYSeriesRenderer();
        seriesRenderer.setLineWidth(3);
        seriesRenderer.setColor(colors[mColorIndex++ % colors.length]);
        seriesRenderer.setShowLegendItem(true);
        mRenderer.addSeriesRenderer(seriesRenderer);
        if(displayPoints) {
            seriesRenderer.setPointStyle(PointStyle.CIRCLE);
            seriesRenderer.setFillPoints(true);
        }

        if(displayPointValues) {
            seriesRenderer.setDisplayChartValues(true);
            seriesRenderer.setChartValuesTextSize(25);
            seriesRenderer.setChartValuesSpacing(25);
        }
    }

    private void populateGraph(int centerIndex)
    {
        double max = 0;
        double min = Integer.MAX_VALUE;
        int y;
        int index = 0;

        mRenderer.clearXTextLabels();

        if( mActiveUsageList.size() <= 0)
            return;

        for(DeviceUsageList usageList : mActiveUsageList) {
            y = 0;
            XYSeries series =  mDataset.getSeriesAt(index);
            series.clear();
            for (DeviceUsage usage : usageList.getUsage()) {
                mRenderer.addXTextLabel(y, formatDate(usage.getDatetime(), mDataResolution));
                series.add(y, usage.getPower_usage());
                y += POINT_DISTANCE;
                max = Math.max(max, usage.getPower_usage());
                min = Math.min(min, usage.getPower_usage());
            }
            index++;
        }

        setRange(min, max, centerIndex);
        DeviceUsageList largestUsageList = getLargestUsageList();
        setLabels(formatDate(largestUsageList.get(mActiveDateIndex).getDatetime(), mTitleFormat));

        if( mChartView != null)
            mChartView.repaint();
    }

    private DeviceUsageList getLargestUsageList()
    {
        DeviceUsageList largestList = new DeviceUsageList();

        for(DeviceUsageList usageList : mActiveUsageList)
            if( largestList.size() < usageList.size())
                largestList = usageList;

        if(largestList.size() == 0)
            return null;

        return largestList;
    }

    private int getLargestListSize()
    {
        DeviceUsageList usageList = getLargestUsageList();
        if(usageList == null)
            return 0;

        return usageList.size();
    }

    public void setFormat(String labelFormat, String titleFormat)
    {
        mTitleFormat = titleFormat;
        mDataResolution = labelFormat;
    }

    private void setRange(double minY, double maxY, int newIndex)
    {
        mActiveDateIndex = newIndex;
        int largestSeriesSize= getLargestListSize();
        int end = largestSeriesSize * POINT_DISTANCE;
        int start;
        int pointsToShow;

        if(NUMBER_OF_POINTS > largestSeriesSize)
            pointsToShow = largestSeriesSize;
        else
            pointsToShow = NUMBER_OF_POINTS;

        //IF the active index is close to or the last element, find a new center index.
        if(largestSeriesSize - mActiveDateIndex < pointsToShow )
            mActiveDateIndex = largestSeriesSize - pointsToShow / 2;

        int centerPoint = mActiveDateIndex  * POINT_DISTANCE;
        start = centerPoint - (POINT_DISTANCE * pointsToShow) / 2;

        if( start < 0)
            start  = 0;

        mRenderer.setRange(new double[]{start - GRAPH_MARGIN,
                start + (pointsToShow * POINT_DISTANCE), minY - GRAPH_MARGIN, maxY + GRAPH_MARGIN * 2});
        mRenderer.setPanLimits(new double[]{0 - GRAPH_MARGIN,
                end + GRAPH_MARGIN, minY - GRAPH_MARGIN, maxY + GRAPH_MARGIN * 2});
    }

    private void zoomIn()
    {
        if(mDataResolution.equals("dd")) {
            setFormat("HH", "EEEE dd/MM");
//            mActiveDateIndex *= 24;
//            mUsageFragment.getLoaderManager().initLoader(UsageFragment.LOADER_USAGE, null, mUsageFragment);
            changeResolution();
            populateGraph(mActiveDateIndex * 24);
        }
        else if(mDataResolution.equals("w")) {
            setFormat("dd", "MMMM");
//            mActiveDateIndex *= 7;
//            mUsageFragment.getLoaderManager().initLoader(UsageFragment.LOADER_USAGE_DAY, null, mUsageFragment);
            changeResolution();
            populateGraph(mActiveDateIndex * 7);
        }
//        else if(mDataResolution.equals("MMMM")) {
//            setFormat("w", "MMMMM y");
//            changeResolution();
//            populateGraph(mActiveDateIndex * 4);
//        }
    }

    private void zoomOut()
    {
        if(mDataResolution.equals("HH")) {
            setFormat("dd", "MMMM");
//            mActiveDateIndex /= 24;
//            mUsageFragment.getLoaderManager().initLoader(UsageFragment.LOADER_USAGE_DAY, null, mUsageFragment);
            changeResolution();
            populateGraph(mActiveDateIndex / 24);
        }
        else if(mDataResolution.equals("dd")) {
            setFormat("w", "MMMMM y");
//            mActiveDateIndex /= 7;
//            mUsageFragment.getLoaderManager().initLoader(UsageFragment.LOADER_USAGE_MONTH, null, mUsageFragment);

            changeResolution();
            populateGraph(mActiveDateIndex / 7);
        }
//        else if(mDataResolution.equals("w"))
//            populateGraph(changeResolution(mCurrentUsageList, "MMMM"), "MMMM", "y",
//                    mActiveDateIndex / 4);
    }

    private void changeResolution()
    {
        mActiveUsageList.clear();
        DeviceUsageList compactList;


        for(DeviceUsageList usageList : mBaseUsageList) {

            if(mDataResolution.equals("HH"))
                mActiveUsageList.add(usageList);

            else {

                compactList = new DeviceUsageList();
                String date = formatDate(usageList.get(0).getDatetime(), mDataResolution);
                double powerUsage = 0;
                Date oldDate = new Date();

                for (EnergyUsageModel usage : usageList.getUsage()) {
                    if (!date.equals(formatDate(usage.getDatetime(), mDataResolution))) {
                        date = formatDate(usage.getDatetime(), mDataResolution);
                        compactList.add(new EnergyUsageModel(usage.getId(),usageList.getDevice().getDevice_id(), oldDate, powerUsage));
                        powerUsage = 0;
                    } else {
                        oldDate = usage.getDatetime();
                        powerUsage += usage.getPower_usage();
                    }
                }
                mActiveUsageList.add(compactList);
            }
        }
    }

    private void setColors(int backgroundColor, int labelColor)
    {
        mRenderer.setApplyBackgroundColor(true);
        mRenderer.setBackgroundColor(backgroundColor);
        mRenderer.setMarginsColor(backgroundColor);
        mRenderer.setLabelsColor(labelColor);
        mRenderer.setXLabelsColor(labelColor);
        mRenderer.setYLabelsColor(0, labelColor);
    }

    private String formatDate(Date date, String format)
    {
        SimpleDateFormat formater = new SimpleDateFormat (format);
        return formater.format(date);
    }

    private void setLabels(String label)
    {
//        mRenderer.setXTitle("<  " + label + "  >");
        mRenderer.setXTitle(label);
        mTitleLabel = label;
    }

    @Override
    public void clearDevices() {
        mActiveUsageList.clear();
        mBaseUsageList.clear();
        mDataset.clear();
        mRenderer.removeAllRenderers();
        mChartView.repaint();
        mColorIndex = 0;
    }

    @Override
    public void addDeviceUsage(ArrayList<DeviceUsageList> usageList) {
        for(DeviceUsageList usage : usageList)
        {
            mActiveUsageList.add(usage);
            mBaseUsageList.add(usage);
            addSeries(usage.getDevice().getName(), true, false);
        }
//        changeResolution();
        if(mActiveDateIndex > 0)
            populateGraph(mActiveDateIndex);
        else
            populateGraph(getLargestListSize());
    }

    public void setUsageFragment(UsageFragment usageFragment) {
        this.mUsageFragment = usageFragment;
    }

    public String[] getSelectedItems() {
        if(selectedItems == null)
            return new String[0];
        else
            return selectedItems;
    }

    public void setSelectedItems(String[] selectedItems) {
        this.selectedItems = selectedItems;
    }

    public boolean[] getSelectedDialogItems() {
        if(selectedDialogItems == null)
            return new boolean[0];
        else
            return selectedDialogItems;
    }

    public void setSelectedDialogItems(boolean[] selectedDialogItems) {
        this.selectedDialogItems = selectedDialogItems;
    }

    @Override
    public void redraw() {
        if(mChartView != null)
            mChartView.repaint();
    }
}
