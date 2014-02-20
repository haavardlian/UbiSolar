package com.sintef_energy.ubisolar;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.yammer.dropwizard.json.JsonSnakeCase;

import java.util.Date;

/**
 * Created by haavard on 2/19/14.
 */
@JsonSnakeCase
public class TotalUsage {
    @JsonProperty
    private Date datetime;
    @JsonProperty
    private double powerUsage;
    @JsonProperty
    private int userId;

    public TotalUsage(int userId, Date datetime, double powerUsage) {
        this.powerUsage = powerUsage;
        this.userId = userId;
        this.datetime = datetime;
    }

    public Date getDatetime() {
        return datetime;
    }

    public void setDatetime(Date datetime) {
        this.datetime = datetime;
    }

    public double getPowerUsage() {
        return powerUsage;
    }

    public void setPowerUsage(double powerUsage) {
        this.powerUsage = powerUsage;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }
}

