package com.sintef_energy.ubisolar.structs;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.yammer.dropwizard.json.JsonSnakeCase;

import java.util.Date;

/**
 * Created by haavard on 2/19/14.
 */
@JsonSnakeCase
public class DeviceUsage {
    private long id;
    private long deviceId;
    private Date datetime;
    @JsonProperty
    private long timestamp;
    @JsonProperty
    private double powerUsage;
    private boolean deleted;
    private long lastUpdated;

    public DeviceUsage() {}

    public DeviceUsage(long id, long deviceId, long timestamp, double powerUsage, boolean deleted, long lastUpdated) {
        this.id = id;
        this.powerUsage = powerUsage;
        this.deviceId = deviceId;
        this.timestamp = timestamp;
        this.deleted = deleted;
        this.lastUpdated = lastUpdated;
    }

    public boolean isDeleted() {
        return deleted;
    }

    public void setDeleted(boolean deleted) {
        this.deleted = deleted;
    }

    public long getLastUpdated() {
        return lastUpdated;
    }

    public void setLastUpdated(long lastUpdated) {
        this.lastUpdated = lastUpdated;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    public double getPowerUsage() {
        return powerUsage;
    }

    public void setPowerUsage(double powerUsage) {
        this.powerUsage = powerUsage;
    }

    public long getDeviceId() {
        return deviceId;
    }

    public void setDeviceId(long deviceId) {
        this.deviceId = deviceId;
    }
}

