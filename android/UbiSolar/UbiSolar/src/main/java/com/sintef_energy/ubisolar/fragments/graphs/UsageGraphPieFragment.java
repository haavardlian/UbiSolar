package com.sintef_energy.ubisolar.fragments.graphs;

import android.app.Activity;
import android.app.LoaderManager;
import android.content.CursorLoader;
import android.content.Loader;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.view.ViewGroup.LayoutParams;
import android.widget.RadioGroup;
import android.widget.TextView;

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
import org.achartengine.model.CategorySeries;
import org.achartengine.model.SeriesSelection;
import org.achartengine.renderer.DefaultRenderer;
import org.achartengine.renderer.SimpleSeriesRenderer;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;

import antistatic.spinnerwheel.AbstractWheel;
import antistatic.spinnerwheel.OnWheelChangedListener;
import antistatic.spinnerwheel.adapters.ArrayWheelAdapter;
import info.hoang8f.android.segmented.SegmentedGroup;

public class UsageGraphPieFragment extends ProgressFragment implements IUsageView, LoaderManager.LoaderCallbacks<Cursor> {

    public static final String TAG = UsageGraphLineFragment.class.getName();

    private final int DEFAULT_RESOLUTION = Resolution.MONTHS;

    private View mRootView;

    private int[] colors;
    private CategorySeries mSeries = new CategorySeries("");
    private DefaultRenderer mRenderer = new DefaultRenderer();
    private GraphicalView mChartView;
    private ArrayList<DeviceUsageList> mDeviceUsageList;
    private Bundle mSavedState;
    private int mSelected = -1;

    private Resolution resolution;
    private Date mSelectedDate;
    private boolean[] mSelectedDialogItems;

    private TextView nameView;
    private TextView powerUsageView;
    private TextView dateView;

    private LinkedHashMap<Long, DeviceModel> mDevices;
    private AbstractWheel mDateWheel;
    private ArrayWheelAdapter<String> mDateAdapter;
    private ArrayList<Date> mDates;

    private int mActiveIndex = -1;

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

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);

    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        //Setup colors
        String colorStringArray[] = getResources().getStringArray(R.array.colorArray);
        this.colors = new int[colorStringArray.length];

        for(int i = 0; i < colorStringArray.length; i++) {
            this.colors[i] = Color.parseColor(colorStringArray[i]);
        }

        super.onActivityCreated(savedInstanceState);
        setContentView(R.layout.fragment_usage_graph_pie);
        mRootView = getContentView();
        setContentShown(false);
        setEmptyText(getResources().getString(R.string.usage_no_content));

        mChartView = null;

//        nameView = (TextView) mRootView.findViewById(R.id.pieDetailsName);
//        powerUsageView = (TextView) mRootView.findViewById(R.id.pieDetailsPowerUsage);
        nameView = new TextView(getActivity());
        powerUsageView = new TextView(getActivity());
        dateView = (TextView) mRootView.findViewById(R.id.pieDateTitle);

        if(savedInstanceState != null && mSavedState == null)
            mSavedState = savedInstanceState.getBundle("mSavedState");

        if (mSavedState != null) {
//            mDeviceUsageList = (ArrayList<DeviceUsageList>) mSavedState.getSerializable("mDeviceUsageList");
            mRenderer = (DefaultRenderer) mSavedState.getSerializable("mRenderer");
            mSeries = (CategorySeries) mSavedState.getSerializable("mSeries");
            mSelected = mSavedState.getInt("mSelected");
            mSelectedDialogItems = mSavedState.getBooleanArray("mSelectedDialogItems");
        }
        else{
            setupPieGraph();
        }

        mDateWheel = (AbstractWheel) mRootView.findViewById(R.id.usage_date_wheel);

        mDateWheel.addChangingListener(new OnWheelChangedListener() {
            @Override
            public void onChanged(AbstractWheel wheel, int oldValue, int newValue) {
                mActiveIndex = newValue;
                mSelectedDate = mDates.get(mActiveIndex);
                for(DeviceUsageList deviceUsageList : mDeviceUsageList){

                    deviceUsageList.calculateTotalUsage(
                            formatDate(mSelectedDate,
                                    resolution.getCompareFormat()) ,
                            resolution.getCompareFormat(),
                            mActiveIndex);

                    dateView.setText(formatDate(mSelectedDate, resolution.getTitleFormat()) );
                    clearDevices();
                    populatePieChart();
                }
            }
        });

        resolution = new Resolution(DEFAULT_RESOLUTION);

        setupSegments();
        createPieGraph();
        updateDetails();
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
        state.putBooleanArray("mSelectedDialogItems", mSelectedDialogItems);

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

    private void setupSegments(){
        SegmentedGroup segment = (SegmentedGroup) mRootView.findViewById(R.id.usage_segment);
        segment.setTintColor(Color.DKGRAY);
        segment.check(segment.getChildAt(getResolution() -1).getId());

        segment.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int i) {
                SegmentedGroup segment = (SegmentedGroup) mRootView.findViewById(R.id.usage_segment);
                setResolution(segment.indexOfChild(segment.findViewById(segment.getCheckedRadioButtonId())) +1);
                pullData();
            }
        });
    }

    private void setupPieGraph(){
//        mRenderer.setChartTitle(getResources().getString(R.string.usage_pie_graph_title));
//        mRenderer.setChartTitleTextSize(40);
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

    private void createPieGraph(){
        if (mChartView == null) {
            LinearLayout layout = (LinearLayout) mRootView.findViewById(R.id.pieChartView);
            mChartView = ChartFactory.getPieChartView(getActivity().getApplicationContext(), mSeries, mRenderer);

            mChartView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    SeriesSelection seriesSelection = mChartView.getCurrentSeriesAndPoint();

                    if (seriesSelection != null) {
                        for (int i = 0; i < mSeries.getItemCount(); i++) {
                            if(mRenderer.getSeriesRendererAt(i).isHighlighted() && i == seriesSelection.getPointIndex()){
                                mRenderer.getSeriesRendererAt(i).setHighlighted(false);
                                mSelected = -1;
                                clearDetails();
                            }
                            else if(i == seriesSelection.getPointIndex()){
                                mRenderer.getSeriesRendererAt(i).setHighlighted(true);
                                mSelected = seriesSelection.getPointIndex();
                                updateDetails();
                            }
                            else{
                                mRenderer.getSeriesRendererAt(i).setHighlighted(false);
                            }
                        }
                        mChartView.repaint();
                    }
                }
            });
            layout.addView(mChartView, new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));
        }
        else {
            mChartView.repaint();
        }
    }

    private void populatePieChart(){
        clearDetails();
        double totalPowerUsage = 0;

        for(DeviceUsageList deviceUsageList : mDeviceUsageList)
            totalPowerUsage += deviceUsageList.getTotalUsage();

        for(DeviceUsageList deviceUsageList : mDeviceUsageList){
            int percentage = (int) ((deviceUsageList.getTotalUsage() / totalPowerUsage) * 100);
            deviceUsageList.setPercentage(percentage);

            mSeries.add(deviceUsageList.getDevice().getName() + " - " + percentage + "%",
                    deviceUsageList.getTotalUsage());
            SimpleSeriesRenderer renderer = new SimpleSeriesRenderer();
            renderer.setColor(colors[(mSeries.getItemCount() - 1) % colors.length]);
            mRenderer.addSeriesRenderer(renderer);
        }
        mChartView.repaint();
    }

    /**
     * Clears devices automatically before adding more data.
     * @param usageList
     */
    public void addDeviceUsage(ArrayList<DeviceUsageList> usageList){
//        clearDevices();

        mDeviceUsageList = usageList;
//        setSelectedDate();

        mDates = new ArrayList<>();
        ArrayList<String> wheelItems = new ArrayList<>();

        for(DeviceUsageList deviceUsageList : mDeviceUsageList){
            for(EnergyUsageModel u : deviceUsageList.getUsage())
                if(!mDates.contains(u.toDate()))
                    mDates.add(u.toDate());
        }

        Collections.sort(mDates);

        for(Date date : mDates){
            String item = formatDate(date, resolution.getResolutionFormat());
            wheelItems.add(item);
        }

        mActiveIndex = getActiveIndex();

        mDateAdapter =
                new ArrayWheelAdapter<>(getActivity(), wheelItems.toArray(new String[wheelItems.size()]));
        mDateAdapter.setItemResource(R.layout.wheel_text_centered);
        mDateAdapter.setItemTextResource(R.id.wheel_text);
        mDateWheel.setViewAdapter(mDateAdapter);
        mDateWheel.setCurrentItem(mActiveIndex);

        for(DeviceUsageList deviceUsageList : mDeviceUsageList){
            deviceUsageList.calculateTotalUsage(
                    formatDate(mDates.get(mActiveIndex),
                            resolution.getCompareFormat()) ,
                    resolution.getCompareFormat(),
                    mActiveIndex);

            dateView.setText(formatDate(mDates.get(mActiveIndex), resolution.getTitleFormat()) );
            clearDevices();
            populatePieChart();
        }

        setContentShown(true);
    }

    /**
     * Clears current devices.
     */
    public void clearDevices() {
        mRenderer.removeAllRenderers();
        mSeries.clear();
        mChartView.repaint();
    }

    private void updateDetails(){
        if(mSelected > -1 && mSelected < mDeviceUsageList.size()) {

            DeviceUsageList usageList = mDeviceUsageList.get(mSelected);

            nameView.setText(usageList.getDevice().getName());
            powerUsageView.setText(usageList.getTotalUsage() + " kWh (" + usageList.getPercentage() + "%)");
        }
    }

    private void clearDetails(){
        nameView.setText("");
        powerUsageView.setText("");
    }

    public void setResolution(int mode){
      resolution.setFormat(mode);
    }

    public int getResolution()
    {
        return resolution.getMode();
    }

    public boolean[] getSelectedDialogItems() {
        if(mSelectedDialogItems == null) {
            mSelectedDialogItems = new boolean[mDevices.size()];
            if (mDevices.size() > 0) {
                Arrays.fill(mSelectedDialogItems, Boolean.TRUE);
                mSelectedDialogItems[0] = false;
            }
        }
        return mSelectedDialogItems;
    }

    public void setSelectedDialogItems(boolean[] mSelectedDialogItems) {
        this.mSelectedDialogItems = mSelectedDialogItems;
    }

    private String formatDate(Date date, String format){
        SimpleDateFormat formater = new SimpleDateFormat (format);
        if(date != null)
            return formater.format(date);
        else
            return null;
    }

    public int getActiveIndex(){
        if(mActiveIndex < 0)
            return mDates.size() -1;

        String date = formatDate(mSelectedDate, resolution.getCompareFormat());

        for(int i = mDates.size() -1; i >= 0; i--)
            if(formatDate(mDates.get(i), resolution.getCompareFormat()).equals(date))
                return i;

        return mDates.size() -1;
    }

    @Override
    public void setDataLoading(boolean state) {
        setContentShown(state);
    }

    public Bitmap createImage(){
        Bitmap bitmap = Bitmap.createBitmap(mChartView.getWidth(), mChartView.getHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        mChartView.draw(canvas);

        return bitmap;
    }


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
            case Resolution.YEARS:
                builder = EnergyContract.Energy.CONTENT_URI.buildUpon();
                builder.appendPath(EnergyContract.Energy.Date.Year);

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


        //To get data points between two dates
        String betweenTime = "strftime('%Y-%m-%d %H:%M', datetime(`" +
                EnergyUsageModel.EnergyUsageEntry.COLUMN_TIMESTAMP + "`, 'unixepoch', 'localtime'))";
//
//        where += " AND " + betweenTime + " BETWEEN ? AND ? ";

        return where;
    }

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

//        try {
//            Date date1 = new SimpleDateFormat("yyyy-MM-dd HH:mm").parse("2014-05-02 11:00");
//            Timestamp sqlDate1 = new java.sql.Timestamp(date1.getTime());
//
//            Date date2 = new SimpleDateFormat("yyyy-MM-dd HH:mm").parse("2014-05-02 14:00");
//            Timestamp sqlDate2 = new java.sql.Timestamp(date2.getTime());
//
//            queryValues.add(sqlDate1.toString());
//            queryValues.add(sqlDate2.toString());
//        } catch (ParseException e) {
//            e.printStackTrace();
//        }


        return queryValues.toArray(new String[queryValues.size()]);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> cursorLoader, Cursor cursor){

        //Hashmap containing all DevicesUsage
        HashMap<Long, DeviceUsageList> devices = new HashMap<>();

        /* Get data from cursor and add */
        cursor.moveToFirst();
        if(cursor.getCount() >= 1) {
            do {
                EnergyUsageModel model = new EnergyUsageModel(cursor, true);
                DeviceUsageList deviceUsageList = devices.get(model.getDeviceId());

                if (deviceUsageList == null) {
                    deviceUsageList = new DeviceUsageList(mDevices.get(model.getDeviceId()));
                    devices.put(Long.valueOf(deviceUsageList.getDevice().getId()), deviceUsageList);
                }

                deviceUsageList.add(model);
            }
            while (cursor.moveToNext());
        }

        ArrayList<DeviceUsageList> deviceUsageLists = new ArrayList<>();

        deviceUsageLists.addAll(devices.values());

        if(deviceUsageLists.size() > 0)
            addDeviceUsage(deviceUsageLists);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> cursorLoader) {}


    @Override
    public void setDevices(LinkedHashMap<Long, DeviceModel> devices) {
        mDevices = devices;
    }

    @Override
    public void pullData(){

        if(mDevices == null)
            return;

        //If no items are selected, clear te graph
        for(boolean selected : getSelectedDialogItems())
            if(selected) {
                getLoaderManager().restartLoader(resolution.getMode(), null, this);
                return;
            }
    }
}
