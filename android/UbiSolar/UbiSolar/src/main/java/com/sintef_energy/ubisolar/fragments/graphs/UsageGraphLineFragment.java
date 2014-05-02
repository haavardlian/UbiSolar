package com.sintef_energy.ubisolar.fragments.graphs;

import android.app.Activity;
import android.app.Fragment;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.os.Parcelable;
import android.os.StrictMode;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.view.ViewGroup.LayoutParams;
import android.widget.Toast;

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

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

public class UsageGraphLineFragment extends Fragment implements IUsageView{
    public static final String TAG = UsageGraphLineFragment.class.getName();
    private static final String STATE_euModels = "STATE_euModels";

    private static final int POINT_DISTANCE = 15;
    private static final int GRAPH_MARGIN = 20;
    private static final int NUMBER_OF_POINTS = 9;

    private XYMultipleSeriesDataset mDataset = new XYMultipleSeriesDataset();
    private XYMultipleSeriesRenderer mRenderer = new XYMultipleSeriesRenderer();
    private GraphicalView mChartView;
    private ArrayList<DeviceUsageList> mActiveUsageList;
    private String mTitleLabel;
    private int[] colors;

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

        String colorStringArray[] = getResources().getStringArray(R.array.colorArray);
        this.colors = new int[colorStringArray.length];

        for(int i = 0; i < colorStringArray.length; i++) {
            this.colors[i] = Color.parseColor(colorStringArray[i]);
        }
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

        /* If the Fragment was destroyed inbetween (screen rotation),
            we need to recover the mSavedState first
            However, if it was not, it stays in the instance from the last onDestroyView()
            and we don't want to overwrite it
        */
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
            mRenderer = setupLineGraph();

            mActiveUsageList = new ArrayList<>();
        }
        resolution = new Resolution(Resolution.DAYS);
        createLineGraph();

        AsyncTaskRunner lol = new AsyncTaskRunner();
        lol.execute(new ArrayList<DeviceUsageList>());

        mSavedState = null;
    }

    /*End lifecycle*/
    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
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
    }

    /**
     * Configure Graph
     */
    private XYMultipleSeriesRenderer setupLineGraph(){
        XYMultipleSeriesRenderer renderer = new XYMultipleSeriesRenderer();

        renderer.setChartTitle(getResources().getString(R.string.usage_line_graph_title));
        renderer.setAxisTitleTextSize(25);
        renderer.setChartTitleTextSize(20);
        renderer.setLabelsTextSize(15);
        renderer.setLegendTextSize(15);
        renderer.setPointSize(5);
        renderer.setXLabels(0);
        renderer.setXLabelsPadding(10);
        renderer.setYLabelsPadding(20);
        renderer.setMargins(new int[]{ 20, 40, 35, 20 });

        setColors(renderer, Color.WHITE, Color.BLACK);

        return renderer;
    }

    private void setColors(XYMultipleSeriesRenderer renderer, int backgroundColor, int labelColor){
        renderer.setApplyBackgroundColor(true);
        renderer.setBackgroundColor(backgroundColor);
        renderer.setMarginsColor(backgroundColor);
        renderer.setLabelsColor(labelColor);
        renderer.setXLabelsColor(labelColor);
        renderer.setYLabelsColor(0, labelColor);
    }

    /**
     * Set up the ChartView with the dataset and renderer and add listeners.
     */
    private void createLineGraph(){
        if (mChartView == null) {
            LinearLayout layout = (LinearLayout) mRootView.findViewById(R.id.lineChartView);
            Log.v(TAG, "createLineGraph: # Datasets to render: " + mDataset.getSeriesCount());

            mChartView = ChartFactory.getLineChartView(getActivity(), mDataset, mRenderer);

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

                    if(mDates.size() == 1)
                        return;

                    if (!mTitleLabel.equals(formatDate(mDates.get(mActiveDateIndex), resolution.getTitleFormat()))) {
                        mTitleLabel = formatDate(mDates.get(mActiveDateIndex), resolution.getTitleFormat());
                        setLabels(formatDate(mDates.get(mActiveDateIndex), resolution.getTitleFormat()));
                    }
                }
            });
            Log.v(TAG, "createLineGraph: Adding mChartView to layout");
            layout.addView(mChartView, new LayoutParams(LayoutParams.MATCH_PARENT,
                    LayoutParams.MATCH_PARENT));
        } else {
            mChartView.repaint();
        }
    }



    /**
     * All manupulation of new graph is done here..
     */
    private class AsyncTaskRunner extends AsyncTask<ArrayList<DeviceUsageList>, Integer, Integer>{

        ArrayList<DeviceUsageList> activeUsageList;

        XYMultipleSeriesRenderer renderer;
        XYMultipleSeriesDataset dataset;

        ArrayList<Date> dates;

        double max;
        double min;

        boolean abort = false;

        /**
         * (non-Javadoc)
         *
         * @see android.os.AsyncTask#onPreExecute()
         */
        @Override
        protected void onPreExecute() {
            // Things to be done before execution of long running operation. For
            // example showing ProgessDialog

            // execution of result of Long time consuming operation
            //finalResult.setText(result);
            activeUsageList = mActiveUsageList;
            dataset = mDataset;
            dates = mDates;
            renderer = mRenderer;

            max = 0;
            min = Integer.MAX_VALUE;

        }

        @Override
        protected Integer doInBackground(ArrayList<DeviceUsageList>... usageList) {
            Log.v(TAG, "Doing #: " + usageList.length + " inBackground");
            for(int i = 0; i < usageList[0].size(); i++){
                activeUsageList.add(usageList[0].get(i));
                addSeries(usageList[0].get(i).getDevice().getName(), true, false);
            }

            populateGraphAsync();

            return null;
        }



        /*
         * (non-Javadoc)
         *
         * @see android.os.AsyncTask#onProgressUpdate(Progress[])
         */
        @Override
        protected void onProgressUpdate(Integer... text) {
            //finalResult.setText(text[0]);
            // Things to be done while execution of long running operation is in
            // progress. For example updating ProgessDialog
        }


        private void populateGraphAsync(){
            int y = 0;
            int index = 0;

            //Clear old data
            renderer.clearXTextLabels();
            dates.clear();

            //Abort if no data.
            if(activeUsageList.size() < 1) {
                abort = true;
                return;
            }

            Date first = getFirstPoint().toDate();
            Date last = getLastPoint().toDate();
            int numberOfPoints = resolution.getTimeDiff(first, last);

            Calendar calendar = Calendar.getInstance();
            calendar.setTime(first);

            Log.v(TAG, "# of points for new render: " + numberOfPoints);
            //Create the labels for the dates
            for(int i = 0; i < numberOfPoints; i++){
                dates.add(calendar.getTime());
                renderer.addXTextLabel(y, formatDate(calendar.getTime(), resolution.getResolutionFormat()));
                y += POINT_DISTANCE;
                resolution.getNextPoint(calendar);
            }

            // Go through the datasets (each device)
            for(DeviceUsageList usageList : activeUsageList) {
                Log.v(TAG, "Rendering new dataset: " + usageList.getDevice().getName());
                Log.v(TAG, "Dataset has # points: " + usageList.getUsage().size());

                XYSeries series = dataset.getSeriesAt(index);
                series.clear(); //@torrib ??
                y = 0;

                //Add the usage
                for (EnergyUsageModel usage : usageList.getUsage()) {
                    while(y < dates.size()){
                        if(compareDates(usage.toDate(), dates.get(y))){
                            series.add(y * POINT_DISTANCE, usage.getPowerUsage());
                            max = Math.max(max, usage.getPowerUsage());
                            min = Math.min(min, usage.getPowerUsage());
                            break;
                        }
                        y++;
                    }
                }
                index++;
            }
        }


        /**
         * (non-Javadoc)
         *
         * @see android.os.AsyncTask#onPostExecute(java.lang.Object)
         */
        @Override
        protected void onPostExecute(Integer lol) {
            if(abort)
                return;


            Log.v(TAG, "LOL TO RENDER: " + dataset.getSeriesCount());

            // Set the new values
            mActiveUsageList.addAll(activeUsageList);
            mRenderer = renderer;
            mDataset = dataset;
            mDates = dates;

            Log.v(TAG, "ROFL TO RENDER: " + mDataset.getSeriesCount());

            setRange(min, max, mDates.size());

            if(mActiveDateIndex <= mDates.size())
                mActiveDateIndex = mDates.size() -1;

            setLabels(formatDate(mDates.get(mActiveDateIndex), resolution.getTitleFormat()));

            Log.v(TAG, "Rendering the new ChartView");
            //mChartView = null;

            createLineGraph();

            if( mChartView != null)
                mChartView.repaint();
        }

        /**
         * Define the series renderer for the mDataset and mRenderer
         *
         * @param seriesName The name of the series
         */
        private void addSeries(String seriesName, boolean displayPoints, boolean displayPointValues){
            XYSeries series = new XYSeries(seriesName);
            dataset.addSeries(series);

            XYSeriesRenderer seriesRenderer = new XYSeriesRenderer();
            seriesRenderer.setLineWidth(3);
            seriesRenderer.setColor(colors[mColorIndex++ % colors.length]);
            seriesRenderer.setShowLegendItem(true);

            renderer.addSeriesRenderer(seriesRenderer);
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


        /**
         *
         * Defines the range for the renderer graph
         *
         * @param minY
         * @param maxY
         * @param points
         */
        private void setRange(double minY, double maxY, int points){
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

            renderer.setRange(new double[]{start - GRAPH_MARGIN,
                    start + (pointsToShow * POINT_DISTANCE), minY - GRAPH_MARGIN, maxY + GRAPH_MARGIN * 2});
            renderer.setPanLimits(new double[]{0 - GRAPH_MARGIN,
                    end + GRAPH_MARGIN, minY - GRAPH_MARGIN, maxY + GRAPH_MARGIN * 2});
        }

        private EnergyUsageModel getFirstPoint(){
            DeviceUsageList usage = activeUsageList.get(0);

            for(DeviceUsageList usageList : activeUsageList) {
                if(usageList.get(0).toDate().before(usage.get(0).toDate()))
                    usage = usageList;

            }
            return usage.get(0);
        }

        private EnergyUsageModel getLastPoint(){
            DeviceUsageList usage = activeUsageList.get(activeUsageList.size() -1);

            for(DeviceUsageList usageList : activeUsageList) {
                if(usageList.get(usageList.size() -1).toDate().after(usage.get(usage.size() - 1).toDate()))
                    usage = usageList;

            }
            return usage.get(usage.size() -1);
        }

    }

    /**
     * Here be the actual calculations. Very CPU.
     *//*
    private void populateGraph(){
        double max = 0;
        double min = Integer.MAX_VALUE;
        int y = 0;
        int index = 0;

        mRenderer.clearXTextLabels();
        mDates.clear();

        if( mActiveUsageList.size() <= 0)
            return;

        Date first = getFirstPoint().toDate();
        Date last = getLastPoint().toDate();
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
            for (EnergyUsageModel usage : usageList.getUsage()) {
                while(y < mDates.size()){
                    if(compareDates(usage.toDate(), mDates.get(y))){
                        series.add(y * POINT_DISTANCE, usage.getPowerUsage());
                        max = Math.max(max, usage.getPowerUsage());
                        min = Math.min(min, usage.getPowerUsage());
                        break;
                    }
                    y++;
                }
            }
            index++;
        }

        setRange(min, max, mDates.size());
        if(mActiveDateIndex <= mDates.size())
            mActiveDateIndex = mDates.size() -1;

        setLabels(formatDate(mDates.get(mActiveDateIndex), resolution.getTitleFormat()));

        if( mChartView != null)
            mChartView.repaint();
    }*/

    private boolean compareDates(Date date1, Date date2){
        return formatDate(date1, resolution.getCompareFormat())
                .equals(formatDate(date2, resolution.getCompareFormat()));
    }



    private void setLabels(String label)
    {
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
        long now = System.currentTimeMillis();
        clearDevices();
        AsyncTaskRunner lol = new AsyncTaskRunner();
        lol.execute(usageList);

        Log.v(TAG, "addDeviceUsage: Milliseconds usage populating graph: " + (System.currentTimeMillis() - now));
    }
/*  OLD
    @Override
    public void addDeviceUsage(ArrayList<DeviceUsageList> usageList) {
        for(DeviceUsageList usage : usageList){
            mActiveUsageList.add(usage);
            addSeries(usage.getDevice().getName(), true, false);
        }
        long now = System.currentTimeMillis();
        populateGraph();
        Log.v(TAG, "addDeviceUsage: Milliseconds usage populating graph: " + (System.currentTimeMillis() - now));
    }*/

    private String formatDate(Date date, String format){
        SimpleDateFormat formatter = new SimpleDateFormat (format);
        if(date != null)
            return formatter.format(date);
        else
            return null;
    }

    @Override
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

    @Override
    public void setActiveIndex(int index){
        mActiveDateIndex = index;
    }

    @Override
    public int getActiveIndex(){
        if(mActiveDateIndex == 0)
            return mDates.size();
        return mActiveDateIndex;
    }

    public boolean isLoaded(){
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

    public Bitmap createImage(){
        Bitmap bitmap = Bitmap.createBitmap(mChartView.getWidth(), mChartView.getHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        mChartView.draw(canvas);

        return bitmap;
    }

    private void writeImage(Bitmap bitmap){
        StrictMode.ThreadPolicy old = StrictMode.getThreadPolicy();
        StrictMode.setThreadPolicy(new StrictMode.ThreadPolicy.Builder(old)
                .permitAll()
                .build());
        StrictMode.setThreadPolicy(old);

        File imagesFolder = new File(Environment.getExternalStorageDirectory(), "Wattitude");
        imagesFolder.mkdirs();
        String fileName = "graph.jpg";
        File output = new File(imagesFolder, fileName);
        Uri uriSavedImage = Uri.fromFile(output);


        OutputStream imageFileOS;
        try {
            ByteArrayOutputStream bytes = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.PNG, 40, bytes);
            imageFileOS = getActivity().getContentResolver().openOutputStream(uriSavedImage);
            imageFileOS.write(bytes.toByteArray());
            imageFileOS.flush();
            imageFileOS.close();

            Toast.makeText(getActivity(),
                    "Image saved: ",
                    Toast.LENGTH_LONG).show();

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
