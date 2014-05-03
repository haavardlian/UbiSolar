package com.sintef_energy.ubisolar.fragments.graphs;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.view.ViewGroup.LayoutParams;
import android.widget.TextView;

import com.devspark.progressfragment.ProgressFragment;
import com.sintef_energy.ubisolar.IView.IUsageView;
import com.sintef_energy.ubisolar.R;
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
import java.util.Date;

public class UsageGraphPieFragment extends ProgressFragment implements IUsageView {

    public static final String TAG = UsageGraphLineFragment.class.getName();

    private View mRootView;

    private static int[] colors;
    private CategorySeries mSeries = new CategorySeries("");
    private DefaultRenderer mRenderer = new DefaultRenderer();
    private GraphicalView mChartView;
    private ArrayList<DeviceUsageList> mDeviceUsageList;
    private Bundle mSavedState;
    private int mSelected = -1;

    private Resolution resolution;

    private Date mSelectedDate;

    private boolean[] mSelectedDialogItems;

    private boolean mLoaded = false;
    private int mDeviceSize;

    private TextView nameView;
    private TextView descriptionView;
    private TextView powerUsageView;
    private TextView usagePieLabel;

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

        String colorStringArray[] = getResources().getStringArray(R.array.colorArray);
        this.colors = new int[colorStringArray.length];

        for(int i = 0; i < colorStringArray.length; i++) {
            this.colors[i] = Color.parseColor(colorStringArray[i]);
        }
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        setContentView(R.layout.fragment_usage_graph_pie);
        mRootView = getContentView();
        setContentShown(false);
        setEmptyText(getResources().getString(R.string.usage_no_content));

        mChartView = null;

        nameView = (TextView) mRootView.findViewById(R.id.pieDetailsName);
        descriptionView = (TextView) mRootView.findViewById(R.id.pieDetailsDescription);
        powerUsageView = (TextView) mRootView.findViewById(R.id.pieDetailsPowerUsage);
        usagePieLabel = (TextView) mRootView.findViewById(R.id.usagePieLabel);

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

        resolution = new Resolution(Resolution.DAYS);
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

    private void setupPieGraph(){
        mRenderer.setChartTitle(getResources().getString(R.string.usage_pie_graph_title));
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
    @Override
    public void addDeviceUsage(ArrayList<DeviceUsageList> usageList){
        clearDevices();

        mDeviceUsageList = usageList;
        setSelectedDate();
        for(DeviceUsageList u : mDeviceUsageList)
            u.calculateTotalUsage(formatDate(mSelectedDate, resolution.getPieFormat()) , resolution.getPieFormat());

        populatePieChart();

        if(mSelectedDate != null) {
           usagePieLabel.setText(resolution.getPreLabel() + formatDate(mSelectedDate, resolution.getPieFormat()));
        }

        setContentShown(true);
    }

    public void setSelectedDate(){
        if(mSelectedDate == null) {
            if (mDeviceUsageList != null) {
                DeviceUsageList dul = mDeviceUsageList.get(mDeviceUsageList.size() - 1);
                mSelectedDate = dul.get(dul.size() - 1).toDate();
            }
        }
    }

    /**
     * Clears current devices.
     */
    @Override
    public void clearDevices() {
        mRenderer.removeAllRenderers();
        mSeries.clear();
        usagePieLabel.setText("");
        mChartView.repaint();
    }

    private void updateDetails(){
        if(mSelected > -1 && mSelected < mDeviceUsageList.size()) {

            DeviceUsageList usageList = mDeviceUsageList.get(mSelected);

            nameView.setText(usageList.getDevice().getName());
            descriptionView.setText(usageList.getDevice().getDescription());
            powerUsageView.setText(usageList.getTotalUsage() + " kWh (" + usageList.getPercentage() + "%)");
        }
    }

    private void clearDetails(){
        nameView.setText("");
        descriptionView.setText("");
        powerUsageView.setText("");
    }

    public void setFormat(int mode){
      resolution.setFormat(mode);
    }

    public int getResolution()
    {
        return resolution.getMode();
    }

    public boolean[] getSelectedDialogItems() {
        if(mSelectedDialogItems == null) {
            mSelectedDialogItems = new boolean[mDeviceSize];
            if (mDeviceSize > 0)
                if (mDeviceSize > 0)
                    Arrays.fill(mSelectedDialogItems, Boolean.TRUE);

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

    public void setActiveIndex(int index){
        //TODO set selected date
    }

    public int getActiveIndex(){
        return 0;
    }

    @Override
    public void setDataLoading(boolean state) {
        setContentShown(state);
    }

    public boolean isLoaded(){
        if(!mLoaded) {
            mLoaded = true;
            return false;
        }
        return true;
    }

    public void setDeviceSize(int size){
        mDeviceSize = size;
    }

    public Bitmap createImage(){
        Bitmap bitmap = Bitmap.createBitmap(mChartView.getWidth(), mChartView.getHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        mChartView.draw(canvas);

        return bitmap;
    }
}
