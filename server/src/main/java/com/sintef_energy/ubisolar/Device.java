package com.sintef_energy.ubisolar;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.yammer.dropwizard.json.JsonSnakeCase;
import org.skife.jdbi.v2.StatementContext;
import org.skife.jdbi.v2.tweak.ResultSetMapper;

import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Created by haavard on 2/19/14.
 */
@JsonSnakeCase
public class Device {
    @JsonProperty
    private int deviceId;
    @JsonProperty
    private String name;
    @JsonProperty
    private String description;
    @JsonProperty
    private int userId;

    public Device() {

    }

    public Device(int userId, String name, String description, int deviceId) {
        this.userId = userId;
        this.name = name;
        this.description = description;
        this.deviceId = deviceId;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public int getUserId() {
        return userId;
    }

    public int getDeviceId() {
        return deviceId;
    }

    public void setDeviceId(int deviceId) {
        this.deviceId = deviceId;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }
}

