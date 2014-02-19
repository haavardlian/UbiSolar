package com.sintef_energy.ubisolar;

import java.util.Date;

/**
 * Created by haavard on 2/19/14.
 */
public class DeviceUsage {
    private Date datetime;
    private double power_usage;
    private int id;

    public DeviceUsage(int id, Date datetime, double power_usage) {
        this.power_usage = power_usage;
        this.id = id;
        this.datetime = datetime;
    }

    public Date getDatetime() {
        return datetime;
    }

    public double getPower_usage() {
        return power_usage;
    }

    public int getId() {
        return id;
    }
}

