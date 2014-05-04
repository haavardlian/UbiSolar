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
    private long timestamp;
    private double powerUsage;
    private boolean deleted;
    protected long lastUpdated;

    public DeviceUsage()
    {

    }

    public DeviceUsage(long id, long deviceId, long timestamp, double powerUsage, boolean deleted, long lastUpdated) {
        this.id = id;
        this.powerUsage = powerUsage;
        this.deviceId = deviceId;
        this.deleted = false;
        this.timestamp = timestamp;
        this.deleted = deleted;
        this.lastUpdated = lastUpdated;
    }

    public DeviceUsage(long id, long deviceId, long timestamp, double powerUsage) {
        this.id = id;
        this.powerUsage = powerUsage;
        this.deviceId = deviceId;
        this.deleted = false;
        this.timestamp = timestamp;
    }

    public void updateLastUpdated(){
        setLastUpdated(System.currentTimeMillis() / 1000L);
    }

    public double getPowerUsage() {
        return powerUsage;
    }

    public long getDeviceId() {

        return deviceId;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public void setDeviceId(long deviceId) {
        this.deviceId = deviceId;
    }

    public void setPowerUsage(double powerUsage) {
        this.powerUsage = powerUsage;
    }

    public boolean isDeleted() {
        return deleted;
    }

    public void setDeleted(boolean isDeleted){
        this.deleted = isDeleted;
    }

    public long getLastUpdated() {
        return lastUpdated;
    }

    public void setLastUpdated(long lastUpdated) {
        this.lastUpdated = lastUpdated;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }
}
