package com.sintef_energy.ubisolar.model;

import java.util.Date;

/**
 * Created by thb on 19.02.14.
 *
 * A replica of the model on the backend.
 */
public abstract class DeviceUsage
{
    private long id;
    private long device_id;
    private Date datetime;
    private double power_usage;
    private boolean deleted;
    protected long lastUpdated;

    public DeviceUsage()
    {

    }

    public DeviceUsage(long id, long device_id, Date datetime, double power_usage) {
        this.id = id;
        this.power_usage = power_usage;
        this.datetime = datetime;
        this.device_id = device_id;
        this.deleted = false;
    }

    protected void updateLastUpdated(){
        setLastUpdated(System.currentTimeMillis() / 1000L);
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
        updateLastUpdated();
    }

    public void setDevice_id(long device_id) {
        this.device_id = device_id;
        updateLastUpdated();
    }

    public void setDatetime(Date datetime) {
        this.datetime = datetime;
        updateLastUpdated();
    }

    public void setPower_usage(double power_usage) {
        this.power_usage = power_usage;
        updateLastUpdated();
    }

    public boolean isDeleted() {
        return deleted;
    }

    public void setDeleted(boolean isDeleted){
        this.deleted = isDeleted;
        updateLastUpdated();
    }

    public long getLastUpdated() {
        return lastUpdated;
    }

    public void setLastUpdated(long lastUpdated) {
        this.lastUpdated = lastUpdated;
    }
}
