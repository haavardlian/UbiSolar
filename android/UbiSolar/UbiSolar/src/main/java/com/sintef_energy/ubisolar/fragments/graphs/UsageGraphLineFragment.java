package com.sintef_energy.ubisolar.fragments.graphs;

import android.app.Activity;
import android.app.Fragment;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Parcelable;
import android.text.format.DateUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.view.ViewGroup.LayoutParams;

import com.sintef_energy.ubisolar.IView.IUsageView;
import com.sintef_energy.ubisolar.R;
import com.sintef_energy.ubisolar.database.energy.EnergyUsageModel;
import com.sintef_energy.ubisolar.model.DeviceUsage;
import com.sintef_energy.ubisolar.model.DeviceUsageList;
import com.sintef_energy.ubisolar.utils.Resolution;

import org.achartengine.ChartFactory;
import org.achartengine.GraphicalView;
import org.achartengine.chart.PointStyle;
import org.achartengine.model.XYMultipleSeriesDataset;
import org.achartengine.model.XYSeries;
import org.achartengine.renderer.XYMultipleSeriesRenderer;
import org.achartengine.renderer.XYSeriesRenderer;
import org.achartengine.tools.PanListener;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.concurrent.TimeUnit;

/**
 * Created by perok on 2/11/14.
 */
public class UsageGraphLineFragment extends Fragment implements IUsageView{

    public static final String TAG = UsageGraphLineFragment.class.getName();
    private static final String ARG_SECTION_NUMBER = "section_number";
    private static final String STATE_euModels = "STATE_euModels";

    private static final int POINT_DISTANCE = 15;
    private static final int GRAPH_MARGIN = 20;
    private static final int NUMBER_OF_POINTS = 9;


    private XYMultipleSeriesDataset mDataset = new XYMultipleSeriesDataset();
    private XYMultipleSeriesRenderer mRenderer = new XYMultipleSeriesRenderer();
    private GraphicalView mChartView;
    private ArrayList<DeviceUsageList> mActiveUsageList;
    private String mTitleLabel;
    private int[] colors = new int[] { Color.GREEN, Color.BLUE,Color.MAGENTA, Color.CYAN, Color.RED,
            Color.YELLOW};
    private int mColorIndex;

    private Bundle mSavedState;
    private View mRootView;

    private Resolution resolution;

    private boolean[] mSelectedDialogItems;
    private int mActiveDateIndex = 0;

    private boolean mLoaded = false;
    private int mDeviceSize;

    private ArrayList<Date> mDates = new ArrayList<>();

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

        mRootView = inflater.inflate(R.layout.fragment_usage_graph_line, container, false);
        return mRootView;
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
            mTitleLabel = mSavedState.getString("mTitleLabel");
            mActiveDateIndex = mSavedState.getInt("mActiveDateIndex");
            mActiveUsageList = (ArrayList<DeviceUsageList>) mSavedState.getSerializable("mActiveUsageList");
            mSelectedDialogItems = mSavedState.getBooleanArray("mSelectedDialogItems");

        }
        //Initialize new data
        else {
            setupLineGraph();

            mActiveUsageList = new ArrayList<>();
        }
        resolution = new Resolution(Resolution.DAYS);
        createLineGraph();
        populateGraph();

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
        state.putString("mTitleLabel", mTitleLabel);
        state.putInt("mActiveDateIndex", mActiveDateIndex);
        state.putSerializable("mActiveUsageList", mActiveUsageList);
        state.putBooleanArray("mSelectedDialogItems", mSelectedDialogItems);

        return state;
    }

    @Override
    public void onDestroy(){
        super.onDestroy();

        Log.v(TAG, " onDestroy()");
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
//            mChartView.addZoomListener(new ZoomListener() {
//                @Override
//                public void zoomApplied(ZoomEvent zoomEvent) {
//                    double zoom = mRenderer.getXAxisMax()- mRenderer.getXAxisMin();
//
//
//                    if(zoom < 90)
//                    {
//                        zoomIn();
//                        System.out.println(mRenderer.getXAxisMax()- mRenderer.getXAxisMin());
//                    }
//                    if(zoom > 300)
//                    {
//                        zoomOut();
//                        System.out.println(mRenderer.getXAxisMax()- mRenderer.getXAxisMin());
//                    }
//                }
//
//                @Override
//                public void zoomReset() {
//                }
//            }, true, true);
            mChartView.addPanListener(new PanListener() {
                @Override
                public void panApplied() {
                    int activePoint = (int) (mRenderer.getXAxisMin() + mRenderer.getXAxisMax()) / 2;
                    mActiveDateIndex = (int) activePoint / POINT_DISTANCE;
                    if(mActiveDateIndex < 0)
                        return;
                    // If the center point does not match the label, swap it with the new label

                    if(mTitleLabel == null || mDates.size() < 1)
                        return;

                    if (!mTitleLabel.equals(formatDate(mDates.get(mActiveDateIndex), resolution.getTitleFormat()))) {
                        mTitleLabel = formatDate(mDates.get(mActiveDateIndex), resolution.getTitleFormat());
                        setLabels(formatDate(mDates.get(mActiveDateIndex), resolution.getTitleFormat()));
                    }
                }
            });
            layout.addView(mChartView, new LayoutParams(LayoutParams.MATCH_PARENT,
                    LayoutParams.MATCH_PARENT));
        } else {
            mChartView.repaint();
        }
    }

    /**
     * Define the series renderer
     * @param seriesName The name of the series
     */
    private void addSeries(String seriesName, boolean displayPoints, boolean displayPointValues)
    {
        XYSeries series = new XYSeries(seriesName);
        mDataset.addSeries(series);

        System.out.println(series.getTitle() + " added");

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

    private void populateGraph()
    {
        double max = 0;
        double min = Integer.MAX_VALUE;
        int y = 0;
        int index = 0;

        mRenderer.clearXTextLabels();
        mDates.clear();

        if( mActiveUsageList.size() <= 0)
            return;

        Date first = getFirstPoint().getDatetime();
        Date last = getLastPoint().getDatetime();
        int numberOfPoints = resolution.getTimeDiff(first, last);

        Calendar calendar = Calendar.getInstance();
        calendar.setTime(first);

        for(int i = 0; i < numberOfPoints; i++){
            mDates.add(calendar.getTime());
            mRenderer.addXTextLabel(y, formatDate(calendar.getTime(), resolution.getResolutionFormat()));
            y += POINT_DISTANCE;
            resolution.getNextPoint(calendar);
        }

        for(DeviceUsageList usageList : mActiveUsageList) {
            XYSeries series =  mDataset.getSeriesAt(index);
            series.clear();
            y = 0;
            for (DeviceUsage usage : usageList.getUsage()) {
                while(y < mDates.size()){
                    if(compareDates(usage.getDatetime(), mDates.get(y))){
                        series.add(y * POINT_DISTANCE, usage.getPower_usage());
                        max = Math.max(max, usage.getPower_usage());
                        min = Math.min(min, usage.getPower_usage());
                        break;
                    }
                    y++;
                }
            }
            index++;
        }

        setRange(min, max, mDates.size());
        setLabels(formatDate(mDates.get(mActiveDateIndex), resolution.getTitleFormat()));

        if( mChartView != null)
            mChartView.repaint();
    }

    private boolean compareDates(Date date1, Date date2){
        return formatDate(date1, resolution.getCompareFormat()).equals(formatDate(date2, resolution.getCompareFormat()));
    }

    private DeviceUsage getFirstPoint()
    {
        DeviceUsageList usage = mActiveUsageList.get(0);

        for(DeviceUsageList usageList : mActiveUsageList) {
            if(usageList.get(0).getDatetime().before(usage.get(0).getDatetime()))
                usage = usageList;

        }
        return usage.get(0);
    }

    private DeviceUsage getLastPoint()
    {
        DeviceUsageList usage = mActiveUsageList.get(mActiveUsageList.size() -1);

        for(DeviceUsageList usageList : mActiveUsageList) {
            if(usageList.get(usageList.size() -1).getDatetime().after(usage.get(usage.size() -1).getDatetime()))
                usage = usageList;

        }
        return usage.get(usage.size() -1);
    }

    private void setRange(double minY, double maxY, int points)
    {
        int end = points * POINT_DISTANCE;
        int start;
        int pointsToShow;

        if(NUMBER_OF_POINTS > points)
            pointsToShow = points;
        else
            pointsToShow = NUMBER_OF_POINTS;

        //IF the active index is close to or the last element, find a new center index.
        if(points - mActiveDateIndex < pointsToShow )
            mActiveDateIndex = points - pointsToShow / 2;

        int centerPoint = mActiveDateIndex  * POINT_DISTANCE;
        start = centerPoint - (POINT_DISTANCE * pointsToShow) / 2;

        if( start < 0)
            start  = 0;

        mRenderer.setRange(new double[]{start - GRAPH_MARGIN,
                start + (pointsToShow * POINT_DISTANCE), minY - GRAPH_MARGIN, maxY + GRAPH_MARGIN * 2});
        mRenderer.setPanLimits(new double[]{0 - GRAPH_MARGIN,
                end + GRAPH_MARGIN, minY - GRAPH_MARGIN, maxY + GRAPH_MARGIN * 2});
    }

//    private void changeResolution()
//    {
//        mActiveUsageList.clear();
//        DeviceUsageList compactList;
//
//
//        for(DeviceUsageList usageList : mBaseUsageList) {
//
//            if(mDataResolution.equals("HH"))
//                mActiveUsageList.add(usageList);
//
//            else {
//
//                compactList = new DeviceUsageList();
//                String date = formatDate(usageList.get(0).getDatetime(), mDataResolution);
//                double powerUsage = 0;
//                Date oldDate = new Date();
//
//                for (EnergyUsageModel usage : usageList.getUsage()) {
//                    if (!date.equals(formatDate(usage.getDatetime(), mDataResolution))) {
//                        date = formatDate(usage.getDatetime(), mDataResolution);
//                        compactList.add(new EnergyUsageModel(usage.getId(),usageList.getDevice().getDevice_id(), oldDate, powerUsage));
//                        powerUsage = 0;
//                    } else {
//                        oldDate = usage.getDatetime();
//                        powerUsage += usage.getPower_usage();
//                    }
//                }
//                mActiveUsageList.add(compactList);
//            }
//        }
//    }

    private void setColors(int backgroundColor, int labelColor)
    {
        mRenderer.setApplyBackgroundColor(true);
        mRenderer.setBackgroundColor(backgroundColor);
        mRenderer.setMarginsColor(backgroundColor);
        mRenderer.setLabelsColor(labelColor);
        mRenderer.setXLabelsColor(labelColor);
        mRenderer.setYLabelsColor(0, labelColor);
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
            addSeries(usage.getDevice().getName(), true, false);
        }

        populateGraph();
    }

//    public void setUsageFragment(UsageFragment usageFragment) {
//        this.mUsageFragment = usageFragment;
//    }

    // On UsageGraph

    private String formatDate(Date date, String format)
    {
        SimpleDateFormat formater = new SimpleDateFormat (format);
        if(date != null)
            return formater.format(date);
        else
            return null;
    }

    public void setFormat(int mode)
    {
        resolution.setFormat(mode);
    }

    public boolean[] getSelectedDialogItems() {
        if(mSelectedDialogItems == null) {
            mSelectedDialogItems = new boolean[mDeviceSize];
            if(mDeviceSize > 0)
                mSelectedDialogItems[0] = true;
        }
        return mSelectedDialogItems;
    }

    public void setSelectedDialogItems(boolean[] mSelectedDialogItems) {
        this.mSelectedDialogItems = mSelectedDialogItems;
    }

    public void setActiveIndex(int index)
    {
        mActiveDateIndex = index;
    }

    public int getActiveIndex()
    {
        return mActiveDateIndex;
    }

    public boolean isLoaded()
    {
        if(!mLoaded) {
            mLoaded = true;
            return false;
        }
        return mLoaded;
    }

    @Override
    public void setDeviceSize(int size) {
        mDeviceSize = size;
    }

    public int getResolution(){
        return resolution.getMode();
    }

}
