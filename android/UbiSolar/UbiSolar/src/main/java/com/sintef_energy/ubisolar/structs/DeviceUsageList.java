package com.sintef_energy.ubisolar.structs;

import java.util.ArrayList;

/**
 * Created by thb on 13.03.14.
 */
public class DeviceUsageList
{

    private Device device;
    private ArrayList<DeviceUsage> usageList;
    private int totalUsage;
    private int percentage;

    public DeviceUsageList()
    {
        usageList = new ArrayList<>();
    }

    public DeviceUsageList(Device device) {
        this.device = device;
        usageList = new ArrayList<>();
    }

    public ArrayList<DeviceUsage> getUsage() {
        return usageList;
    }

    public void setUsage(ArrayList<DeviceUsage> usageList) {
        this.usageList = usageList;
    }

    public int size()
    {
        if(usageList == null)
            return 0;

        return usageList.size();
    }

    public DeviceUsage get(int index)
    {
        return usageList.get(index);
    }

    public void add(DeviceUsage usage)
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

    public Device getDevice()
    {
        return device;
    }
}