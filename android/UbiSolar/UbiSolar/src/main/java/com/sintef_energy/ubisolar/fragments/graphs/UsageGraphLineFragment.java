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

import com.sintef_energy.ubisolar.IView.ITotalEnergyView;
import com.sintef_energy.ubisolar.R;
import com.sintef_energy.ubisolar.activities.DrawerActivity;
import com.sintef_energy.ubisolar.database.energy.EnergyUsageModel;
import com.sintef_energy.ubisolar.presenter.TotalEnergyPresenter;
import com.sintef_energy.ubisolar.structs.TotalUsage;

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
import java.util.Calendar;
import java.util.Date;
import java.util.Random;

/**
 * Created by perok on 2/11/14.
 */
public class UsageGraphLineFragment extends Fragment implements ITotalEnergyView{

    public static final String TAG = UsageGraphLineFragment.class.getName();
    private static final String ARG_SECTION_NUMBER = "section_number";
    private static final String STATE_euModels = "STATE_euModels";

    private static final int POINT_DISTANCE = 15;
    private static final int GRAPH_MARGIN = 10;
    private static final int NUMBER_OF_POINTS = 9;

    private View rootView;

    private XYMultipleSeriesDataset mDataset = new XYMultipleSeriesDataset();
    private XYMultipleSeriesRenderer mRenderer = new XYMultipleSeriesRenderer();
    private XYSeries mCurrentSeries;
    private XYSeriesRenderer mCurrentRenderer;
    private GraphicalView mChartView;
    private ArrayList<TotalUsage> mTotalUsageList;
    private ArrayList<TotalUsage> mCurrentUsageList;
    private String mTitleLabel;
    private String mTitleFormat;
    private int mZoomLevel = 0;
    private boolean mZoomIn;
    private int mActiveDateIndex = 0;

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
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        rootView = inflater.inflate(R.layout.fragment_usage_graph_line, container, false);
        return rootView;
    }

    @Override
    public void onActivityCreated(Bundle savedState) {
        super.onActivityCreated(savedState);

        if (savedState != null) {
            mDataset = (XYMultipleSeriesDataset) savedState.getSerializable("mDataset");
            mRenderer = (XYMultipleSeriesRenderer) savedState.getSerializable("mRenderer");
            mCurrentSeries = (XYSeries) savedState.getSerializable("current_series");
            mCurrentRenderer = (XYSeriesRenderer) savedState.getSerializable("current_renderer");
        }
        setupLineGraph();
        createLineGraph();
        addSeries("Total");
        createData();
        populateGraph(mTotalUsageList, "HH", "EEEE dd/MM");
    }

    /*End lifecycle*/
    @Override
    public void onSaveInstanceState(Bundle outState) {

        ArrayList<Parcelable> usageModelState = new ArrayList<>();
        for(EnergyUsageModel euModel : euModels)
            usageModelState.add(euModel);

        outState.putParcelableArrayList(STATE_euModels, usageModelState);
        super.onSaveInstanceState(outState);
        outState.putSerializable("mDataset", mDataset);
        outState.putSerializable("mRenderer", mRenderer);
        outState.putSerializable("current_series", mCurrentSeries);
        outState.putSerializable("current_renderer", mCurrentRenderer);
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
    public void newData(EnergyUsageModel euModel) {
        //euModels.add(euModel);
//        createLineGraph();
    }


    private void setupLineGraph(){
        mRenderer.setChartTitle("Power usage");
        mRenderer.setYTitle("KWh");
        mRenderer.setXTitle("Date");

        mRenderer.setAxisTitleTextSize(16);
        mRenderer.setChartTitleTextSize(20);
        mRenderer.setLabelsTextSize(15);
        mRenderer.setLegendTextSize(15);
        mRenderer.setPointSize(10);
        mRenderer.setXLabels(0);
        mRenderer.setXLabelsPadding(10);
        mRenderer.setYLabelsPadding(10);

        setColors(Color.WHITE, Color.BLACK);
    }

    private void createLineGraph()
    {
        if (mChartView == null) {
            LinearLayout layout = (LinearLayout) rootView.findViewById(R.id.lineChartView);
            mChartView = ChartFactory.getLineChartView(rootView.getContext(), mDataset, mRenderer);
            mChartView.addZoomListener(new ZoomListener() {
                @Override
                public void zoomApplied(ZoomEvent zoomEvent) {
                    double zoom = mRenderer.getXAxisMax()- mRenderer.getXAxisMin();
                    if(zoom > 50 && zoom < 70 && mZoomLevel > 0)
                    {
                        mZoomLevel--;
                        mZoomIn = true;
                        changeDataset();
                    }
                    if(zoom > 250 && zoom < 270 && mZoomLevel <= 3)
                    {
                        mZoomLevel++;
                        mZoomIn = false;
                        changeDataset();
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
                    if (!mTitleLabel.equals(formatDate(mCurrentUsageList.get(mActiveDateIndex).getDatetime(), mTitleFormat))) {
                        mTitleLabel = formatDate(mCurrentUsageList.get(mActiveDateIndex).getDatetime(), mTitleFormat);
                        setLabels(formatDate(mCurrentUsageList.get(mActiveDateIndex).getDatetime(), mTitleFormat));
                    }
                }
            });
            layout.addView(mChartView, new LayoutParams(LayoutParams.MATCH_PARENT,
                    LayoutParams.MATCH_PARENT));
        } else {
            mChartView.repaint();
        }
    }

    private void addSeries(String seriesName)
    {
        XYSeries series = new XYSeries(seriesName);
        mDataset.addSeries(series);
        mCurrentSeries = series;

        XYSeriesRenderer seriesRenderer = new XYSeriesRenderer();
        mRenderer.addSeriesRenderer(seriesRenderer);
        seriesRenderer.setPointStyle(PointStyle.CIRCLE);
        seriesRenderer.setFillPoints(true);
        //Enable to display chart values on the graph
//        seriesRenderer.setDisplayChartValues(true);
//        seriesRenderer.setChartValuesTextSize(25);
//        seriesRenderer.setChartValuesSpacing(25);
//        mRenderer.setDisplayChartValuesDistance(10);
        seriesRenderer.setLineWidth(3);
        mCurrentRenderer = seriesRenderer;
    }

    private void populateGraph( ArrayList<TotalUsage> totalUsage, String labelFormat, String titleFormat)
    {
        mTitleFormat = titleFormat;
        mCurrentUsageList = totalUsage;
        double max = 0;
        double min = 0;
        int y = 0;
        mCurrentSeries.clear();
        mRenderer.clearXTextLabels();
        for(TotalUsage usage : totalUsage)
        {
            mRenderer.addXTextLabel(y, formatDate(usage.getDatetime(), labelFormat));
            mCurrentSeries.add(y, usage.getPower_usage());
            y += POINT_DISTANCE;
            max = Math.max(max, usage.getPower_usage());
            min = Math.min(min, usage.getPower_usage());
        }

        setRange(min, max, totalUsage.size());
        setLabels(formatDate(totalUsage.get(mActiveDateIndex).getDatetime(), titleFormat));

//        mChartView.zoomOut();
        if( mChartView != null)
            mChartView.repaint();
    }

    private void setRange(double min, double max, int count)
    {
        int pointsToShow;
        if(NUMBER_OF_POINTS > count)
            pointsToShow = count;
        else
            pointsToShow = NUMBER_OF_POINTS;
        int end = count * POINT_DISTANCE;
        // Set the graph range to the end if there is no zoom information
        if(mActiveDateIndex < 1)
        {
            mRenderer.setRange(new double[]{end - (pointsToShow * POINT_DISTANCE) - GRAPH_MARGIN,
                end + GRAPH_MARGIN, min - GRAPH_MARGIN, max + GRAPH_MARGIN});
            mRenderer.setPanLimits(new double[]{0,
                    end + GRAPH_MARGIN, min - GRAPH_MARGIN, max + GRAPH_MARGIN});
        }
        else
        {
            if(mZoomLevel == 0 && mZoomIn == true)
            {
                mActiveDateIndex = mActiveDateIndex * 24;
                int centerPoint = mActiveDateIndex * POINT_DISTANCE;
                int start = centerPoint - (POINT_DISTANCE * pointsToShow) / 2;
                mRenderer.setRange(new double[]{start - GRAPH_MARGIN,
                    start + (pointsToShow * POINT_DISTANCE), min - GRAPH_MARGIN, max + GRAPH_MARGIN});
                mRenderer.setPanLimits(new double[]{0,
                        start + (pointsToShow * POINT_DISTANCE), min - GRAPH_MARGIN, max + GRAPH_MARGIN});
            }
            else if(mZoomLevel == 1 && mZoomIn == false)
            {
                mActiveDateIndex = mActiveDateIndex / 24;
                int centerPoint = mActiveDateIndex  * POINT_DISTANCE;
                int start = centerPoint - (POINT_DISTANCE * pointsToShow) / 2;
                mRenderer.setRange(new double[]{start - GRAPH_MARGIN,
                        start + (pointsToShow * POINT_DISTANCE), min - GRAPH_MARGIN, max + GRAPH_MARGIN});
                mRenderer.setPanLimits(new double[]{0,
                        start + (pointsToShow * POINT_DISTANCE), min - GRAPH_MARGIN, max + GRAPH_MARGIN});
            }
            else if(mZoomLevel == 1 && mZoomIn == true)
            {
                mActiveDateIndex = mActiveDateIndex * 7;
                int centerPoint = mActiveDateIndex * POINT_DISTANCE;
                int start = centerPoint - (POINT_DISTANCE * pointsToShow) / 2;
                mRenderer.setRange(new double[]{start - GRAPH_MARGIN,
                        start + (pointsToShow * POINT_DISTANCE), min - GRAPH_MARGIN, max + GRAPH_MARGIN});
            }
            else if(mZoomLevel == 2 && mZoomIn == false)
            {
                mActiveDateIndex = mActiveDateIndex / 7;
                int centerPoint = mActiveDateIndex * POINT_DISTANCE;
                int start = centerPoint - (POINT_DISTANCE * pointsToShow) / 2;
                mRenderer.setRange(new double[]{start - GRAPH_MARGIN,
                        start + (pointsToShow * POINT_DISTANCE), min - GRAPH_MARGIN, max + GRAPH_MARGIN});
            }
            else if(mZoomLevel == 2 && mZoomIn == true)
            {
                mActiveDateIndex = mActiveDateIndex * 4;
                int centerPoint = mActiveDateIndex * POINT_DISTANCE;
                int start = centerPoint - (POINT_DISTANCE * pointsToShow) / 2;
                mRenderer.setRange(new double[]{start - GRAPH_MARGIN,
                        start + (pointsToShow * POINT_DISTANCE), min - GRAPH_MARGIN, max + GRAPH_MARGIN});
            }
            else if(mZoomLevel == 3 && mZoomIn == false)
            {
                mActiveDateIndex = mActiveDateIndex / 4;
                int centerPoint = mActiveDateIndex * POINT_DISTANCE;
                int start = centerPoint - (POINT_DISTANCE * pointsToShow) / 2;
                mRenderer.setRange(new double[]{start - GRAPH_MARGIN,
                        start + (pointsToShow * POINT_DISTANCE), min - GRAPH_MARGIN, max + GRAPH_MARGIN});
            }
        }
    }

    private void changeDataset()
    {
        switch (mZoomLevel)
        {
            case 0:
                populateGraph(mTotalUsageList, "HH", "EEEE dd/MM");
                break;
            case 1:
                populateGraph(changeResolution(mTotalUsageList, "dd"), "dd", "MMMM");
                break;
            case 2:
                populateGraph(changeResolution(mTotalUsageList, "w"), "w", "y");
                break;
            case 3:
                populateGraph(changeResolution(mTotalUsageList, "MMMM"), "MMMM", "y");
                break;
        }
    }

    private ArrayList<TotalUsage> changeResolution(ArrayList<TotalUsage> list, String format)
    {
        ArrayList<TotalUsage> compactList = new ArrayList<>();
        String date = formatDate(list.get(0).getDatetime(), format);
        double powerUsage = 0;
        Date oldDate = new Date();
        for(TotalUsage usage : list)
        {
            if(!date.equals(formatDate(usage.getDatetime(), format)))
            {
                date = formatDate(usage.getDatetime(), format);
                compactList.add(new TotalUsage(1, oldDate, powerUsage));
                powerUsage = 0;
            }

            oldDate = usage.getDatetime();
            powerUsage += usage.getPower_usage();
        }
        return compactList;
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
        mRenderer.setXTitle(label);
        mTitleLabel = label;
    }

    // Temp code for generating data

    private ArrayList<TotalUsage> createData()
    {
        TotalUsage usage;
        mTotalUsageList = new ArrayList<>();
        Date date;
        Random random = new Random();
        Calendar cal = Calendar.getInstance();

        for(int i = 0; i < 1000; i++)
        {
            date = new Date();
            cal.setTime(date);
            cal.add(Calendar.HOUR_OF_DAY, i);
            date = cal.getTime();
            usage = new TotalUsage(1, date, random.nextInt((200 - 50) + 1) + 50);
            mTotalUsageList.add(usage);
        }

        return mTotalUsageList;
    }
}

