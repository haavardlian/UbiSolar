package com.sintef_energy.ubisolar.structs;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.yammer.dropwizard.json.JsonSnakeCase;

/**
 * Created by haavard on 2/19/14.
 */
@JsonSnakeCase
public class Device {
    @JsonProperty
    private long id;
    @JsonProperty
    private String name;
    @JsonProperty
    private String description;
    @JsonProperty
    private long userId;
    @JsonProperty
    private long lastUpdated;
    @JsonProperty
    private boolean deleted;
    private int category;

    public Device() {

    }

    public Device(long id, long userId, String name, String description, long lastUpdated, boolean deleted, int category) {

        this.id = id;
        this.userId = userId;
        this.name = name;
        this.description = description;
        this.lastUpdated = lastUpdated;
        this.deleted = deleted;
        this.category = category;
    }

    public int getCategory() {
        return category;
    }

    public void setCategory(int category) {
        this.category = category;
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

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public long getUserId() {
        return userId;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setUserId(long userId) {
        this.userId = userId;
    }
}

