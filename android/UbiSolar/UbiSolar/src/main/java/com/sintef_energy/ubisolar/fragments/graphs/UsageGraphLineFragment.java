/*
 * Licensed to UbiCollab.org under one or more contributor
 * license agreements.  See the NOTICE file distributed
 * with this work for additional information regarding
 * copyright ownership. UbiCollab.org licenses this file
 * to you under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance
 * with the License. You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package com.sintef_energy.ubisolar.fragments.graphs;

import android.app.LoaderManager;
import android.content.CursorLoader;
import android.content.Loader;
import android.content.res.TypedArray;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Parcelable;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.view.ViewGroup.LayoutParams;
import android.widget.RadioGroup;

import com.devspark.progressfragment.ProgressFragment;
import com.sintef_energy.ubisolar.IView.IUsageView;
import com.sintef_energy.ubisolar.R;
import com.sintef_energy.ubisolar.database.energy.DeviceModel;
import com.sintef_energy.ubisolar.database.energy.EnergyContract;
import com.sintef_energy.ubisolar.database.energy.EnergyUsageModel;
import com.sintef_energy.ubisolar.model.Device;
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
import java.util.HashMap;
import java.util.LinkedHashMap;

import info.hoang8f.android.segmented.SegmentedGroup;

public class UsageGraphLineFragment extends ProgressFragment implements IUsageView, LoaderManager.LoaderCallbacks<Cursor> {
    private static final String TAG = UsageGraphLineFragment.class.getName();

    private final int IMAGE_RENDER_WIDTH = 960;
    private final int IMAGE_RENDER_HEIGHT = 540;

    private final int DEFAULT_RESOLUTION = Resolution.DAYS;

    private static final String STATE_euModels = "STATE_euModels";

    private static final int POINT_DISTANCE = 20;
    private static final int GRAPH_MARGIN = 20;
    private static final int NUMBER_OF_POINTS = 4;

    private XYMultipleSeriesDataset mDataset = new XYMultipleSeriesDataset();
    private XYMultipleSeriesRenderer mRenderer = new XYMultipleSeriesRenderer();
    private GraphicalView mChartView;
    private ArrayList<DeviceUsageList> mActiveUsageList;
    private String mTitleLabel;

    private int[] mColors;
    private int mColorIndex;
    private Bundle mSavedState;
    private View mRootView;

    private Resolution mResolution;

    private boolean[] mSelectedDialogItems;
    private int mActiveDateIndex = -1;

    private LinkedHashMap<Long, DeviceModel> mDevices;
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

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(mSavedState);
        setContentView(R.layout.fragment_usage_graph_line);
        mRootView = getContentView();


        //Loads the colors from strings
        String colorStringArray[] = getResources().getStringArray(R.array.colorArray);
        this.mColors = new int[colorStringArray.length];

        for (int i = 0; i < colorStringArray.length; i++) {
            this.mColors[i] = Color.parseColor(colorStringArray[i]);
        }

        //ProgressFragment show progresswheel
        setContentShown(false);

        /* If the Fragment was destroyed inbetween (screen rotation),
            we need to recover the mSavedState first
            However, if it was not, it stays in the instance from the last onDestroyView()
            and we don't want to overwrite it
        */
        if (savedInstanceState != null && mSavedState == null)
            mSavedState = savedInstanceState.getBundle("mSavedState");

        mChartView = null;

        //Restore data
        if (mSavedState != null) {
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

        //Configure and create graph
        mResolution = new Resolution(DEFAULT_RESOLUTION);
        setupSegments();
        createLineGraph();

        //Load the graph
        AsyncTaskRunner asyncGraphCreator = new AsyncTaskRunner();
        asyncGraphCreator.execute(new ArrayList<DeviceUsageList>());
        mSavedState = null;
    }

    /*End lifecycle*/
    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putBundle("mSavedState", mSavedState != null ? mSavedState : saveState());
    }

    /**
     * onDestroyView is run when fragment is replaced. Save state here.
     */
    @Override
    public void onDestroyView() {
        super.onDestroy();

        mSavedState = saveState();
    }

    private Bundle saveState() {
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
    public void onDestroy() {
        super.onDestroy();
        cleanUpReferences();
    }

    /**
     * Everything is nulled out so the GC can collect the fragment instance.
     */
    private void cleanUpReferences(){
        mRootView = null;
        mSavedState = null;
        mDataset = null;
        mRenderer = null;
        mChartView = null;
        mActiveUsageList = null;
        mTitleLabel = null;
        mColors = null;
        mSavedState = null;
        mResolution = null;
        mSelectedDialogItems = null;
        mDates = null;
    }

    /**
     * Creates the segments for selecting the data resolution
     */
    private void setupSegments(){
        SegmentedGroup segment = (SegmentedGroup) mRootView.findViewById(R.id.usage_segment);
        segment.setTintColor(Color.DKGRAY);
        segment.check(segment.getChildAt(getResolution()).getId());

        segment.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int i) {
                int index = radioGroup.indexOfChild(radioGroup.findViewById(i));
                setActiveIndex(mResolution.getMode(), index);
                mResolution.setFormat(index);
                pullData();
            }
        });
    }

    /**
     * Configure the Graph
     */
    private XYMultipleSeriesRenderer setupLineGraph() {
        XYMultipleSeriesRenderer renderer = new XYMultipleSeriesRenderer();

        renderer.setAxisTitleTextSize(25);
        renderer.setLabelsTextSize(15);
        renderer.setLegendTextSize(15);
        renderer.setPointSize(5);
        renderer.setXLabels(0);
        renderer.setXLabelsPadding(10);
        renderer.setYLabelsPadding(20);
        renderer.setMargins(new int[]{20, 40, 35, 20});

        setColors(renderer, Color.WHITE, Color.BLACK);

        return renderer;
    }

    /**
     * Helper method for setting up the graph colors
     *
     * @param renderer
     * @param backgroundColor
     * @param labelColor
     */
    private void setColors(XYMultipleSeriesRenderer renderer, int backgroundColor, int labelColor) {
        renderer.setApplyBackgroundColor(false);
        renderer.setBackgroundColor(backgroundColor);

        TypedArray array = getActivity().getTheme().obtainStyledAttributes(new int[]{
                android.R.attr.colorBackground,
        });


        int bgColor = array.getColor(0, 0xFF00FF);
        array.recycle();

        renderer.setMarginsColor(bgColor);

        renderer.setLabelsColor(labelColor);
        renderer.setXLabelsColor(labelColor);
        renderer.setYLabelsColor(0, labelColor);
    }

    /**
     * Set up the ChartView with datasets and listeners
     */
    private void createLineGraph() {
        if (mChartView == null) {
            LinearLayout layout = (LinearLayout) mRootView.findViewById(R.id.lineChartView);
            mChartView = ChartFactory.getLineChartView(getActivity(), mDataset, mRenderer);


            //Logic to change title text to match the centered point
            mChartView.addPanListener(new PanListener() {
                @Override
                public void panApplied() {
                    int centerPoint = (int) (mRenderer.getXAxisMin() + mRenderer.getXAxisMax()) / 2;
                    mActiveDateIndex = centerPoint / POINT_DISTANCE;

                    if (mTitleLabel == null || mDates.size() <= 1 || mActiveDateIndex < 0)
                        return;

                    // If the center point does not match the label, swap it with the new label
                    String newTitle = formatDate(mDates.get(mActiveDateIndex), mResolution.getTitleFormat());
                    if (!mTitleLabel.equals(newTitle)) {
                        mTitleLabel = newTitle;
                        updateLabels();
                    }
                }
            });
            layout.addView(mChartView, new LayoutParams(
                    LayoutParams.MATCH_PARENT,
                    LayoutParams.MATCH_PARENT));
        } else {
            mChartView.repaint();
        }
    }

    /**
     * Async handling of data processing and graph drawing
     */
    private class AsyncTaskRunner extends AsyncTask<ArrayList<DeviceUsageList>, Void, Void>{
        double max;
        double min;

        boolean abort = false;
        long startTime = 0;

        @Override
        protected void onPreExecute() {
            //Hides the graph while processing
            setViewState(false, false);

            startTime = System.currentTimeMillis();

            max = 0;
            min = Integer.MAX_VALUE;

            //Clear old data
            mRenderer.clearXTextLabels();
            mDates.clear();
        }

        @SafeVarargs
        @Override
        protected final Void doInBackground(final ArrayList<DeviceUsageList>... dataUsageList) {
            int y = 0;
            int index = 0;

            for (int i = 0; i < dataUsageList[0].size(); i++) {
                mActiveUsageList.add(dataUsageList[0].get(i));
                addSeries(dataUsageList[0].get(i).getDevice().getName(), true, false);
            }

            //Abort if there is no data
            if (mActiveUsageList.size() < 1) {
                abort = true;
                return null;
            }


            //Logic to draw points that has no data, this is slow for low resolutions with many points.
            Date first = getFirstPoint().toDate();
            Date last = getLastPoint().toDate();
            int numberOfPoints = mResolution.getTimeDiff(first, last);

            Calendar calendar = Calendar.getInstance();
            calendar.setTime(first);

            //Create the labels for the dates
            for (int i = 0; i < numberOfPoints; i++) {
                mDates.add(calendar.getTime());
                mRenderer.addXTextLabel(y, formatDate(calendar.getTime(), mResolution.getResolutionFormat()));
                y += POINT_DISTANCE;
                mResolution.getNextPoint(calendar);
            }

            // Go through the datasets for each device
            for (DeviceUsageList usageList : mActiveUsageList) {
                XYSeries series = mDataset.getSeriesAt(index);
                series.clear();
                y = 0;

                // Because of date incompatibilities between week converting on sqllite and  the java date object,
                // data around the 1 week might be wrong in the week view.
                for (EnergyUsageModel usage : usageList.getUsage()) {
                    while (y < mDates.size()) {
                        if (compareDates(usage.toDate(), mDates.get(y))) {
                            series.add(y * POINT_DISTANCE, usage.getPowerUsage());
                            max = Math.max(max, usage.getPowerUsage());
                            min = Math.min(min, usage.getPowerUsage());
                            y++;
                            break;
                        }
                        //Quick fix to handle the date incompatibilities
                        if(usage.toDate().before(mDates.get(y)))
                            break;
                        y++;
                    }
                }
                index++;
            }

            //return if there are no points
            if(mDates.size() <= 0) {
                abort = true;
                return null;
            }

            if (mActiveDateIndex >= mDates.size() || mActiveDateIndex < 0)
                mActiveDateIndex = mDates.size() - 1;


            setRange(min, max, mDates.size());
            mTitleLabel = formatDate(mDates.get(mActiveDateIndex), mResolution.getTitleFormat());
            updateLabels();
            return null;
        }

        /*
         * (non-Javadoc)
         *
         * @see android.os.AsyncTask#onProgressUpdate(Progress[])
         */
        @Override
        protected void onProgressUpdate(Void... values) {
        }

        /**
         * (non-Javadoc)
         *
         * @see android.os.AsyncTask#onPostExecute(java.lang.Object)
         */
        @Override
        protected void onPostExecute(Void values) {

            if (mChartView != null)
                mChartView.repaint();

            if(abort)
                setViewState(true, false);
            else
                setViewState(true, true);
        }

        /**
         * Define the series renderer for the mDataset and mRenderer
         *
         * @param seriesName The name of the series (Device name)
         */
        private void addSeries(String seriesName, boolean displayPoints, boolean displayPointValues) {
            XYSeries series = new XYSeries(seriesName);
            mDataset.addSeries(series);

            XYSeriesRenderer seriesRenderer = new XYSeriesRenderer();
            seriesRenderer.setLineWidth(3);
            seriesRenderer.setColor(mColors[mColorIndex++ % mColors.length]);
            seriesRenderer.setShowLegendItem(true);

            mRenderer.addSeriesRenderer(seriesRenderer);
            if (displayPoints) {
                seriesRenderer.setPointStyle(PointStyle.CIRCLE);
                seriesRenderer.setFillPoints(true);
            }

            if (displayPointValues) {
                seriesRenderer.setDisplayChartValues(true);
                seriesRenderer.setChartValuesTextSize(25);
                seriesRenderer.setChartValuesSpacing(25);
            }
        }

        /**
         * Defines the range and pan limits for the graph
         *
         * @param minY The lowest value to display
         * @param maxY The highest value to display
         * @param points The total number of points to draw
         */
        private void setRange(double minY, double maxY, int points) {
            int end = points * POINT_DISTANCE;
            int start;
            int pointsToShow;


            // if the amount of points is less than the desired amount of points at the screen
            // reduce  the amount of points to display.
            if (NUMBER_OF_POINTS > points)
                pointsToShow = points;
            else
                pointsToShow = NUMBER_OF_POINTS;

            //IF the active index is close to or the last element, find a new center index.
            if (points - mActiveDateIndex < pointsToShow)
                mActiveDateIndex = points - pointsToShow / 2;

            int centerPoint = mActiveDateIndex * POINT_DISTANCE;
            start = centerPoint - (POINT_DISTANCE * pointsToShow) / 2;

            if (start < 0)
                start = 0;

            mRenderer.setRange(new double[]{start - GRAPH_MARGIN,
                start + (pointsToShow * POINT_DISTANCE), minY - GRAPH_MARGIN, maxY + GRAPH_MARGIN * 2});

            mRenderer.setPanLimits(new double[]{0 - GRAPH_MARGIN,
                end + GRAPH_MARGIN, minY - GRAPH_MARGIN, maxY + GRAPH_MARGIN * 2});
        }


        /**
         * Get the earliest usage object in a set
         * @return EnergyUsageModel
         */
        private EnergyUsageModel getFirstPoint() {
            DeviceUsageList usage = mActiveUsageList.get(0);

            for (DeviceUsageList usageList : mActiveUsageList) {
                if (usageList.get(0).toDate().before(usage.get(0).toDate()))
                    usage = usageList;

            }
            return usage.get(0);
        }

        /**
         * Get the last usage object in a set
         * @return EnergyUsageModel
         */
        private EnergyUsageModel getLastPoint() {
            DeviceUsageList usage = mActiveUsageList.get(mActiveUsageList.size() - 1);

            for (DeviceUsageList usageList : mActiveUsageList) {
                if (usageList.get(usageList.size() - 1).toDate().after(usage.get(usage.size() - 1).toDate()))
                    usage = usageList;

            }
            return usage.get(usage.size() - 1);
        }

        /**
         * Compares two dates using the resolution
         * @param date1
         * @param date2
         * @return
         */
        private boolean compareDates(Date date1, Date date2) {
            return formatDate(date1, mResolution.getCompareFormat())
                    .equals(formatDate(date2, mResolution.getCompareFormat()));
        }

    }

    /**
     * Enables/ disables part of the view when data loads.
     */
    private void setViewState(boolean state, boolean isData) {
        mChartView.setEnabled(state);
        setContentShown(state);
        setContentEmpty(!isData);
    }

    private void updateLabels() {
        mRenderer.setXTitle(mTitleLabel);
    }

    /**
     * Clears all data.
     */
    public void clearDevices() {
        mActiveUsageList.clear();
        mDataset.clear();
        mRenderer.removeAllRenderers();
        mChartView.repaint();
        mColorIndex = 0;
    }

    /**
     * Creates a string based on a dateobject and format
     * @param date
     * @param format
     * @return String
     */
    private String formatDate(Date date, String format) {
        SimpleDateFormat formatter = new SimpleDateFormat(format);
        if (date != null)
            return formatter.format(date);
        else
            return null;
    }

    /**
     * Changes the new index used to set the range when the resolution is changed
     * @param oldMode The previous resolution
     * @param newMode The new resolution
     */
    private void setActiveIndex(int oldMode, int newMode){
        switch(oldMode){
            case Resolution.HOURS:
                if(newMode == Resolution.DAYS)
                    mActiveDateIndex = mActiveDateIndex / 24;
                else if(newMode == Resolution.WEEKS)
                    mActiveDateIndex = mActiveDateIndex / 24 / 7;
                else if(newMode == Resolution.MONTHS)
                    mActiveDateIndex = mActiveDateIndex / 24 / 7 / 4;
                break;
            case Resolution.DAYS:
                if(newMode == Resolution.HOURS)
                    mActiveDateIndex = mActiveDateIndex * 24;
                else if(newMode == Resolution.WEEKS)
                    mActiveDateIndex = mActiveDateIndex / 7;
                else if(newMode == Resolution.MONTHS)
                    mActiveDateIndex = mActiveDateIndex / 7 / 4;
                break;
            case Resolution.WEEKS:
                if(newMode == Resolution.HOURS)
                    mActiveDateIndex = mActiveDateIndex * 7 * 24;
                else if(newMode == Resolution.DAYS)
                    mActiveDateIndex = mActiveDateIndex * 7;
                else if(newMode == Resolution.MONTHS)
                    mActiveDateIndex = mActiveDateIndex / 4;
                break;
            case Resolution.MONTHS:
                if(newMode == Resolution.HOURS)
                    mActiveDateIndex = mActiveDateIndex * 4 * 7 * 24;
                else if(newMode == Resolution.DAYS)
                    mActiveDateIndex = mActiveDateIndex * 7 * 4;
                else if(newMode == Resolution.WEEKS)
                    mActiveDateIndex = mActiveDateIndex * 4;
                break;
        }
    }

    /**
     * Returns a boolean array representing the selected devices.
     * If the object is null, the first element will be selected by default
     * @return boolean[]
     */
    @Override
    public boolean[] getSelectedDialogItems() {
        if (mSelectedDialogItems == null) {
            mSelectedDialogItems = new boolean[mDevices.size()];
            if (mDevices.size() > 0)
                mSelectedDialogItems[0] = true;
        }
        return mSelectedDialogItems;
    }

    /**
     * Sets the selected devices
     * @param mSelectedDialogItems
     */
    @Override
    public void setSelectedDialogItems(boolean[] mSelectedDialogItems) {
        this.mSelectedDialogItems = mSelectedDialogItems;
    }

    @Override
    public void setDataLoading(boolean state) {
        setContentShown(!state);
    }

    /**
     * Returns the resolution
     * If no resolution has been set, the default resolution is returned
     * @return
     */
    public int getResolution() {
        if(mResolution == null)
            return DEFAULT_RESOLUTION;

        return mResolution.getMode();
    }

    /**
     * Creates an image of the graph
     * @return Bitmap
     */
    @Override
    public Bitmap createImage() {
        Bitmap bitmap = Bitmap.createBitmap(IMAGE_RENDER_WIDTH, IMAGE_RENDER_HEIGHT, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        mChartView.draw(canvas);

        return bitmap;
    }

    @Override
    public void setDevices(LinkedHashMap<Long, DeviceModel> devices){
        mDevices = devices;
    }

    /**
     * Queries the database for data based on the selected devices and the resolution
     */
    @Override
    public void pullData(){

        //If no items are selected, clear the graph
        if(mDevices == null)
            return;

        //If any items is selected query the database
        for(boolean selected : getSelectedDialogItems())
            if(selected) {
                getLoaderManager().restartLoader(mResolution.getMode(), null, this);
                return;
            }
        clearDevices();
    }

    /**
     * Creates a query to the database
     * @param mode Int deciding what query to make
     * @param bundle
     * @return Loader<Cursor>
     */
    @Override
    public Loader<Cursor> onCreateLoader(int mode, Bundle bundle) {
        Uri.Builder builder;

        switch (mode){
            case Resolution.HOURS:
                return new CursorLoader(
                        getActivity(),
                        EnergyContract.Energy.CONTENT_URI,
                        EnergyContract.Energy.PROJECTION_ALL,
                        sqlWhereDevices(),
                        getSelectedDevicesIDs(),
                        EnergyUsageModel.EnergyUsageEntry.COLUMN_TIMESTAMP + " ASC");
            case Resolution.DAYS:
                builder = EnergyContract.Energy.CONTENT_URI.buildUpon();
                builder.appendPath(EnergyContract.Energy.Date.Day);

                return new CursorLoader(
                        getActivity(),
                        builder.build(),
                        null,
                        sqlWhereDevices(),
                        getSelectedDevicesIDs(),
                        null);
            case Resolution.WEEKS:
                builder = EnergyContract.Energy.CONTENT_URI.buildUpon();
                builder.appendPath(EnergyContract.Energy.Date.Week);

                return new CursorLoader(
                        getActivity(),
                        builder.build(),
                        null,
                        sqlWhereDevices(),
                        getSelectedDevicesIDs(),
                        null);
            case Resolution.MONTHS:
                builder = EnergyContract.Energy.CONTENT_URI.buildUpon();
                builder.appendPath(EnergyContract.Energy.Date.Month);

                return new CursorLoader(
                        getActivity(),
                        builder.build(),
                        null,
                        sqlWhereDevices(),
                        getSelectedDevicesIDs(),
                        null);
        }
        return null;
    }

    /**
     * Creates a string for the selected devices
     * @return String
     */
    private String sqlWhereDevices(){
        String where = "";

        boolean[] selectedItems = getSelectedDialogItems();
        ArrayList<String> queries = new ArrayList<>();

        for(boolean selectedItem : selectedItems)
            if(selectedItem)
                queries.add(EnergyUsageModel.EnergyUsageEntry.COLUMN_DEVICE_ID + "=?");

        if(queries.size() < 1)
            return "";

        // The last part of the query shall not be succeeded by an OR.
        int i;
        for(i = 0; i < queries.size() - 1; i++)
            where += queries.get(i) + " OR ";
        where += queries.get(i);

        return where;
    }

    /**
     * Returns the IDs of the selected devices
     * @return String[]
     */
    private String[] getSelectedDevicesIDs() {
        boolean[] selectedItems = getSelectedDialogItems();
        ArrayList<String> queryValues = new ArrayList<>();

        int i = 0;

        for(Device device : mDevices.values()){
            if(selectedItems.length > i) {
                if (selectedItems[i]) {
                    queryValues.add("" + device.getId());
                }
                i++;
            }
        }
        return queryValues.toArray(new String[queryValues.size()]);
    }

    /**
     * Create models with data from the database and populates the graph
     * @param cursorLoader
     * @param cursor
     */
    @Override
    public void onLoadFinished(Loader<Cursor> cursorLoader, Cursor cursor){

        //Hashmap containing all DevicesUsage
        HashMap<Long, DeviceUsageList> devices = new HashMap<>();

        //Populate the models with data from the database.
        cursor.moveToFirst();
        if(cursor.getCount() >= 1) {
            do {
                EnergyUsageModel model = new EnergyUsageModel(cursor, true);
                DeviceUsageList deviceUsageList = devices.get(model.getDeviceId());

                if (deviceUsageList == null) {
                    deviceUsageList = new DeviceUsageList(mDevices.get(model.getDeviceId()));
                    devices.put(deviceUsageList.getDevice().getId(), deviceUsageList);
                }

                deviceUsageList.add(model);
            }
            while (cursor.moveToNext());
        }

        ArrayList<DeviceUsageList> deviceUsageLists = new ArrayList<>();
        deviceUsageLists.addAll(devices.values());

        if(deviceUsageLists.size() > 0) {
            clearDevices();

            //Populates the graph
            AsyncTaskRunner asyncGraphCreator = new AsyncTaskRunner();
            asyncGraphCreator.execute(deviceUsageLists);
        }
        else
            setViewState(true, false);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> cursorLoader) {}

}
