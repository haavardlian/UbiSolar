package com.sintef_energy.ubisolar.structs;

import java.util.Date;

/**
 * Created by thb on 19.02.14.
 */
public class DeviceUsage
{
    private double power_usage;
    private Date datetime;
    private int device_id;

    private DeviceUsage()
    {

    }

    public DeviceUsage(int device_id, Date datetime, double power_usage) {
        this.power_usage = power_usage;
        this.datetime = datetime;
        this.device_id = device_id;
    }


    public double getPower_usage() {
        return power_usage;
    }

    public Date getDatetime() {
        return datetime;
    }

    public int getDevice_id() {
        return device_id;
    }
}
