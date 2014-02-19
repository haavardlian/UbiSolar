package com.sintef_energy.ubisolar;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.Date;

/**
 * Created by haavard on 2/19/14.
 */
public class TotalUsage {
    @JsonProperty
    private Date datetime;
    @JsonProperty
    private double power_usage;
    @JsonProperty
    private int user_id;

    public TotalUsage(int user_id, Date datetime, double power_usage) {
        this.power_usage = power_usage;
        this.user_id = user_id;
        this.datetime = datetime;
    }

    public Date getDatetime() {
        return datetime;
    }

    public double getPower_usage() {
        return power_usage;
    }

    public int getUser_id() {
        return user_id;
    }
}

