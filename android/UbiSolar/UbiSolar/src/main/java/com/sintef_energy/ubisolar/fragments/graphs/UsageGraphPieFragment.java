package com.sintef_energy.ubisolar.fragments.graphs;

import android.app.Activity;
import android.app.Fragment;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.view.ViewGroup.LayoutParams;
import android.widget.TextView;

import com.sintef_energy.ubisolar.IView.ITotalEnergyView;
import com.sintef_energy.ubisolar.R;
import com.sintef_energy.ubisolar.database.energy.DeviceModel;
import com.sintef_energy.ubisolar.database.energy.EnergyUsageModel;
import com.sintef_energy.ubisolar.presenter.TotalEnergyPresenter;
import com.sintef_energy.ubisolar.structs.Device;
import com.sintef_energy.ubisolar.structs.DeviceUsage;

import org.achartengine.ChartFactory;
import org.achartengine.GraphicalView;
import org.achartengine.model.CategorySeries;
import org.achartengine.model.SeriesSelection;
import org.achartengine.renderer.DefaultRenderer;
import org.achartengine.renderer.SimpleSeriesRenderer;

import java.util.ArrayList;
import java.util.Date;
import java.util.Random;

/**
 * Created by perok on 2/11/14.
 */
public class UsageGraphPieFragment extends Fragment implements ITotalEnergyView {

    private static final String ARG_SECTION_NUMBER = "section_number";
    TotalEnergyPresenter presenter;

    private View rootView;

    private static int[] COLORS = new int[] { Color.GREEN, Color.BLUE,Color.MAGENTA, Color.CYAN, Color.RED, Color.YELLOW};
    private CategorySeries mSeries = new CategorySeries("");
    private DefaultRenderer mRenderer = new DefaultRenderer();
    private GraphicalView mChartView;
    private ArrayList<Device> devices;

    /**
     * Returns a new instance of this fragment for the given section
     * number.
     */
    public static UsageGraphPieFragment newInstance() {
        UsageGraphPieFragment fragment = new UsageGraphPieFragment();
        Bundle args = new Bundle();
        //args.putInt(ARG_SECTION_NUMBER, sectionNumber);
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

        if (savedInstanceState != null) {
            // Restore last state for checked position.
        }

        setupPieGraph();
        createPieGraph();
        devices = createDevices();
        ArrayList<ArrayList<DeviceUsage>> usageList = createDeviceUsage(devices);
        populatePieChart(devices, usageList);
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
    }

    @Override
    public void newData(EnergyUsageModel euModel) {

    }

    private void setupPieGraph()
    {
        mRenderer.setApplyBackgroundColor(true);
        mRenderer.setBackgroundColor(Color.WHITE);
        mRenderer.setChartTitleTextSize(20);
        mRenderer.setLabelsTextSize(15);
        mRenderer.setLegendTextSize(15);
        mRenderer.setMargins(new int[] { 20, 30, 15, 0 });
        mRenderer.setStartAngle(90);
        mRenderer.setZoomEnabled(false);
        mRenderer.setPanEnabled(false);
        mRenderer.setChartTitle("Power overview");
        mRenderer.setLabelsColor(Color.BLACK);
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
                                clearDetails();
                            }
                            else if(i == seriesSelection.getPointIndex())
                            {
                                mRenderer.getSeriesRendererAt(i).setHighlighted(true);
                                updateDetails(devices.get(seriesSelection.getPointIndex()), String.valueOf(seriesSelection.getValue()));
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

    private void populatePieChart(ArrayList<Device> devices,
                                  ArrayList<ArrayList<DeviceUsage>> usageList)
    {
        double totalPowerUsage = 0;

        for(int i = 0; i < devices.size(); i++)
        {
            ArrayList<DeviceUsage> usage = usageList.get(i);
            for(DeviceUsage u : usage)
                totalPowerUsage += u.getPower_usage();
        }

        for(int i = 0; i < devices.size(); i++)
        {
            ArrayList<DeviceUsage> usage = usageList.get(i);
            double powerUsage = 0;

            for(DeviceUsage u : usage)
                powerUsage += u.getPower_usage();


            double percentage = (powerUsage / totalPowerUsage) * 100;

            mSeries.add(devices.get(i).getName() + " - " + (int) percentage + "%", powerUsage);
            SimpleSeriesRenderer renderer = new SimpleSeriesRenderer();
            renderer.setColor(COLORS[(mSeries.getItemCount() - 1) % COLORS.length]);
            mRenderer.addSeriesRenderer(renderer);
        }
    }

    // Create data
    private ArrayList<Device> createDevices()
    {
        ArrayList<Device> devices = new ArrayList<>();
        devices.add(new DeviceModel(1, "TV", "-", 1));
        devices.add(new DeviceModel(2, "Radio", "-", 1));
        devices.add(new DeviceModel(3, "PC", "-", 1));
        devices.add(new DeviceModel(4, "Oven", "-", 1));
        devices.add(new DeviceModel(5, "Heater", "-", 1));
        return devices;
    }

    private ArrayList<ArrayList<DeviceUsage>> createDeviceUsage(ArrayList<Device> devices)
    {
        ArrayList<ArrayList<DeviceUsage>> collection = new ArrayList<>();
        Date date;
        Random random = new Random();


        for(Device device : devices)
        {
            ArrayList<DeviceUsage> usage = new ArrayList<>();
            for(int i = 0; i < 100; i++)
            {
                date = new Date(System.currentTimeMillis() + (i * 60 * 60 * 1000));
                usage.add(new EnergyUsageModel(System.currentTimeMillis(), device.getDevice_id(), date, random.nextInt((50 - 0) + 1) + 50));
            }
            collection.add(usage);
        }
        return collection;
    }

    private void updateDetails(Device device, String powerUsage)
    {
        TextView nameView = (TextView) rootView.findViewById(R.id.pieDetailsName);
        TextView descriptionView = (TextView) rootView.findViewById(R.id.pieDetailsDescription);
        TextView powerUsageView = (TextView) rootView.findViewById(R.id.pieDetailsPowerUsage);


        nameView.setText(device.getName());
        descriptionView.setText(device.getDescription());
        powerUsageView.setText(powerUsage + " kWh");
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
}
