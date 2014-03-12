package com.sintef_energy.ubisolar.fragments.graphs;

import android.app.Activity;
import android.app.Fragment;
import android.app.LoaderManager;
import android.content.Context;
import android.content.CursorLoader;
import android.content.Loader;
import android.database.Cursor;
import android.graphics.Color;
import android.graphics.Paint;
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
import com.sintef_energy.ubisolar.database.energy.EnergyContract;
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
public class UsageGraphLineFragment extends Fragment implements ITotalEnergyView, LoaderManager.LoaderCallbacks<Cursor> {

    public static final String TAG = UsageGraphLineFragment.class.getName();
    private static final String ARG_SECTION_NUMBER = "section_number";
    private static final String STATE_euModels = "STATE_euModels";

    private static final int POINT_DISTANCE = 15;
    private static final int GRAPH_MARGIN = 20;
    private static final int NUMBER_OF_POINTS = 9;


    private XYMultipleSeriesDataset mDataset = new XYMultipleSeriesDataset();
    private XYMultipleSeriesRenderer mRenderer = new XYMultipleSeriesRenderer();
    private XYSeries mCurrentSeries;
    private XYSeriesRenderer mCurrentRenderer;
    private GraphicalView mChartView;
    private ArrayList<TotalUsage> mTotalUsageList;
    private ArrayList<TotalUsage> mCurrentUsageList;
    private String mTitleLabel;
    private String mTitleFormat;
    private String mDataResolution;
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

        View rootView = inflater.inflate(R.layout.fragment_usage_graph_line, container, false);
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

        //Define mRenderer.
        setupLineGraph();

        //Define the render view.
        createLineGraph();

        mCurrentRenderer = addSeries("Total");

        mTotalUsageList = new ArrayList<>();
        //mTotalUsageList.clear();
        //mTotalUsageList.addAll(createData());

        //populateGraph(mTotalUsageList, "HH", "EEEE dd/MM", mTotalUsageList.size());

        getLoaderManager().initLoader(0, null, this);
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
        mRenderer.setPointSize(10);
        mRenderer.setXLabels(0);
        mRenderer.setXLabelsPadding(10);
        mRenderer.setYLabelsPadding(20);
        mRenderer.setMargins(new int[]{ 20, 40, 20, 20 });

        setColors(Color.WHITE, Color.BLACK);
    }

    /**
     * Set up the ChartView and add listeners.
     */
    private void createLineGraph()
    {
        if (mChartView == null) {
            LinearLayout layout = (LinearLayout) getActivity().findViewById(R.id.lineChartView);
            mChartView = ChartFactory.getLineChartView(getActivity(), mDataset, mRenderer);
            mChartView.addZoomListener(new ZoomListener() {
                @Override
                public void zoomApplied(ZoomEvent zoomEvent) {
                    double zoom = mRenderer.getXAxisMax()- mRenderer.getXAxisMin();
                    if(zoom > 70 && zoom < 90)
                    {
                        zoomIn();
                    }
                    if(zoom > 250 && zoom < 270)
                    {
                        zoomOut();
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

    /**
     * Define the series renderer
     * @param seriesName The name of the series
     */
    private XYSeriesRenderer addSeries(String seriesName)
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
        return seriesRenderer;
    }

    /**
     * Clears previous data and populates the list with the usage.
     *
     * @param usage
     * @param labelFormat
     * @param titleFormat
     * @param centerIndex
     */
    private void populateGraph( ArrayList<TotalUsage> usage, String labelFormat,
                                String titleFormat, int centerIndex)
    {
        mTitleFormat = titleFormat;
        mDataResolution = labelFormat;
        mCurrentUsageList = usage;

        mCurrentSeries.clear();
        mRenderer.clearXTextLabels();

        double max = 0;
        double min = Integer.MAX_VALUE;
        int y = 0;

        for(TotalUsage u : usage)
        {
            mRenderer.addXTextLabel(y, formatDate(u.getDatetime(), labelFormat));
            mCurrentSeries.add(y, u.getPower_usage());
            y += POINT_DISTANCE;
            max = Math.max(max, u.getPower_usage());
            min = Math.min(min, u.getPower_usage());
        }

        setRange(min, max, centerIndex);
        if(mActiveDateIndex < usage.size())
            setLabels(formatDate(usage.get(mActiveDateIndex).getDatetime(), titleFormat));

        if( mChartView != null)
            mChartView.repaint();
    }

    private void setRange(double minY, double maxY, int newIndex)
    {
        mActiveDateIndex = newIndex;
        int end = mCurrentUsageList.size() * POINT_DISTANCE;
        int start;
        int pointsToShow;

        if(NUMBER_OF_POINTS > mCurrentUsageList.size())
            pointsToShow = mCurrentUsageList.size();
        else
            pointsToShow = NUMBER_OF_POINTS;

        //IF the active index is close to or the last element, find a new center index.
        if(mCurrentUsageList.size() - mActiveDateIndex < pointsToShow )
            mActiveDateIndex = mCurrentUsageList.size() - pointsToShow / 2;

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
        if(mDataResolution.equals("dd"))
            populateGraph(mTotalUsageList, "HH", "EEEE dd/MM",
                    mActiveDateIndex * 24);
        else if(mDataResolution.equals("w"))
            populateGraph(changeResolution(mTotalUsageList, "dd"), "dd", "MMMM",
                    mActiveDateIndex * 7);
//        else if(mDataResolution.equals("MMMM"))
//            populateGraph(changeResolution(mTotalUsageList, "w"), "w", "MMMMM y",
//                    mActiveDateIndex * 4);
    }

    private void zoomOut()
    {
        if(mDataResolution.equals("HH"))
            populateGraph(changeResolution(mCurrentUsageList, "dd"), "dd", "MMMM",
                    mActiveDateIndex / 24);
        else if(mDataResolution.equals("dd"))
            populateGraph(changeResolution(mCurrentUsageList, "w"), "w", "MMMMM y",
                    mActiveDateIndex / 7);
//        else if(mDataResolution.equals("w"))
//            populateGraph(changeResolution(mCurrentUsageList, "MMMM"), "MMMM", "y",
//                    mActiveDateIndex / 4);
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
//        mRenderer.setXTitle("<  " + label + "  >");
        mRenderer.setXTitle(label);
        mTitleLabel = label;
    }

    // Temp code for generating data

    private ArrayList<TotalUsage> createData()
    {
        TotalUsage usage;
        ArrayList<TotalUsage> tempmTotalUsageList = new ArrayList<>();
        Date date;
        Random random = new Random();
        Calendar cal = Calendar.getInstance();

        for(int i = 0; i < 2000; i++)
        {
            date = new Date();
            cal.setTime(date);
            cal.add(Calendar.HOUR_OF_DAY, i);
            date = cal.getTime();
            usage = new TotalUsage(1, date, random.nextInt((200 - 50) + 1) + 50);
            tempmTotalUsageList.add(usage);
        }

        return tempmTotalUsageList;
    }


    /**
     * Make data that can be rendered from the cursor.
     * @param data
     * @return
     */
    private ArrayList<TotalUsage> createDataFromCursor(Cursor data)
    {
        TotalUsage usage;
        ArrayList<TotalUsage> tempTotalUsageList = new ArrayList<>();
        Date date;
        Random random = new Random();
        Calendar cal = Calendar.getInstance();

        data.moveToFirst();
        if(data.getCount() != 0)
            do{
                EnergyUsageModel eum = new EnergyUsageModel(data);
                tempTotalUsageList.add(new TotalUsage(eum.getDevice_id(), eum.getDatetime(), eum.getPower_usage()));
            }
            while(data.moveToNext());


        return tempTotalUsageList;
    }

    /**
     * Creates the loader. Maybe have i for different months?
     * @param i
     * @param bundle
     * @return
     */
    @Override
    public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {
        return new CursorLoader(
                getActivity(),
                EnergyContract.Energy.CONTENT_URI,
                EnergyContract.Energy.PROJECTION_ALL,
                null,
                null,
                EnergyUsageModel.EnergyUsageEntry.COLUMN_DATETIME + " ASC"
                );
    }

    @Override
    public void onLoadFinished(Loader<Cursor> cursorLoader, Cursor data) {
        //TODO: Async oppdatering av view
        mTotalUsageList.clear();
        mTotalUsageList.addAll(createDataFromCursor(data));
        populateGraph(mTotalUsageList, "HH", "EEEE dd/MM", mTotalUsageList.size());
    }

    @Override
    public void onLoaderReset(Loader<Cursor> cursorLoader) {

    }
}

