package com.sintef_energy.ubisolar;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.skife.jdbi.v2.StatementContext;
import org.skife.jdbi.v2.tweak.ResultSetMapper;

import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Created by haavard on 2/19/14.
 */
public class Device {
    @JsonProperty
    private int device_id;
    @JsonProperty
    private String name;
    @JsonProperty
    private String description;
    @JsonProperty
    private int user_id;

    public Device()
    {

    }

    public Device(int user_id, String name, String description, int device_id) {
        this.user_id = user_id;
        this.name = name;
        this.description = description;
        this.device_id = device_id;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public int getUser_id() {
        return user_id;
    }

    public int getDevice_id() {
        return device_id;
    }
}

