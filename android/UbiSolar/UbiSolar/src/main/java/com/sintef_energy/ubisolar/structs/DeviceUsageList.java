package com.sintef_energy.ubisolar.structs;

import com.sintef_energy.ubisolar.database.energy.DeviceModel;
import com.sintef_energy.ubisolar.database.energy.EnergyUsageModel;

import java.util.ArrayList;

/**
 * Created by thb on 13.03.14.
 */
public class DeviceUsageList
{

    private DeviceModel device;
    private ArrayList<EnergyUsageModel> usageList;
    private int totalUsage;
    private int percentage;

    public DeviceUsageList()
    {
        usageList = new ArrayList<>();
    }

    public DeviceUsageList(DeviceModel device) {
        this.device = device;
        usageList = new ArrayList<>();
    }

    public ArrayList<EnergyUsageModel> getUsage() {
        return usageList;
    }

    public void setUsage(ArrayList<EnergyUsageModel> usageList) {
        this.usageList = usageList;
    }

    public int size()
    {
        if(usageList == null)
            return 0;

        return usageList.size();
    }

    public EnergyUsageModel get(int index)
    {
        return usageList.get(index);
    }

    public void add(EnergyUsageModel usage)
    {
        usageList.add(usage);
    }

    public int getTotalUsage()
    {
        return totalUsage;
    }

    public void calculateTotalUsage()
    {
        totalUsage = 0;
        for(DeviceUsage usage : usageList)
            totalUsage += usage.getPower_usage();
    }

    public int getPercentage() {
        return percentage;
    }

    public void setPercentage(int percentage) {
        this.percentage = percentage;
    }

    public DeviceModel getDevice()
    {
        return device;
    }
}