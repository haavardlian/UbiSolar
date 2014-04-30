package com.sintef_energy.ubisolar.model;

import java.util.Date;

/**
 * Created by thb on 19.02.14.
 *
 * A replica of the model on the backend.
 */
public class DeviceUsage
{
    private long id;
    private long deviceId;
    private Date datetime;
    private double powerUsage;
    private boolean deleted;
    protected long lastUpdated;

    public DeviceUsage()
    {

    }

    public DeviceUsage(long id, long deviceId, Date datetime, double powerUsage) {
        this.id = id;
        this.powerUsage = powerUsage;
        this.datetime = datetime;
        this.deviceId = deviceId;
        this.deleted = false;
    }

    protected void updateLastUpdated(){
        setLastUpdated(System.currentTimeMillis() / 1000L);
    }


    public double getPowerUsage() {
        return powerUsage;
    }

    public Date getDatetime() {
        return datetime;
    }

    public long getDeviceId() {

        return deviceId;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
        updateLastUpdated();
    }

    public void setDeviceId(long deviceId) {
        this.deviceId = deviceId;
        updateLastUpdated();
    }

    public void setDatetime(Date datetime) {
        this.datetime = datetime;
        updateLastUpdated();
    }

    public void setPowerUsage(double powerUsage) {
        this.powerUsage = powerUsage;
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
