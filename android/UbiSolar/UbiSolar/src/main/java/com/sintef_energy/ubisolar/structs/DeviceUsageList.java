package com.sintef_energy.ubisolar.structs;

import java.util.ArrayList;

/**
 * Created by thb on 13.03.14.
 */
public class DeviceUsageList
{
    private long id;
    private String name;
    private ArrayList<DeviceUsage> usageList;

    public DeviceUsageList()
    {
        usageList = new ArrayList<>();
    }

    public DeviceUsageList(long id, String name) {
        this.id = id;
        this.name = name;
        usageList = new ArrayList<>();
    }

    public long getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
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
}