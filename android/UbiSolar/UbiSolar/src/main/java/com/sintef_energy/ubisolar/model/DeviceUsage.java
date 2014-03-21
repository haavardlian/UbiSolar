package com.sintef_energy.ubisolar.model;

import java.util.Date;

/**
 * Created by thb on 19.02.14.
 */
public abstract class DeviceUsage
{
    private long id;
    private long device_id;
    private Date datetime;
    private double power_usage;

    public DeviceUsage()
    {

    }

    public DeviceUsage(long id, long device_id, Date datetime, double power_usage) {
        this.id = id;
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

    public long getDevice_id() {

        return device_id;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public void setDevice_id(long device_id) {
        this.device_id = device_id;
    }

    public void setDatetime(Date datetime) {
        this.datetime = datetime;
    }

    public void setPower_usage(double power_usage) {
        this.power_usage = power_usage;
    }
}
