package com.sintef_energy.ubisolar.model;

/**
 * Created by thb on 19.02.14.
 *
 * A replica of the model on the backend.
 */
public class Device
{
    protected long id;
    protected long userId;
    protected String name;
    protected String description;
    protected int category;
    protected boolean deleted;
    protected long lastUpdated;

    public Device()
    {

    }

    public Device(long id, long userId, String name, String description, int category, boolean deleted, long lastUpdated) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.userId = userId;
        this.category = category;
        this.deleted = deleted;
        this.lastUpdated = lastUpdated;
    }


    public void updateLastUpdate(){
        setLastUpdated(System.currentTimeMillis() / 1000L);
    }

    public long getId() {
        return id;
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

    public int getCategory() { return category; }

    public String toString() { return name; }

    public void setId(long id) {
        this.id = id;
    }

    public void setUserId(long userId) {
        this.userId = userId;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setCategory(int category) {
        this.category = category;
    }

    public boolean isDeleted() {
        return deleted;
    }

    public void setDeleted(boolean isDeleted) {
        this.deleted = isDeleted;
    }

    public long getLastUpdated() {
        return lastUpdated;
    }

    public void setLastUpdated(long lastUpdated) {
        this.lastUpdated = lastUpdated;
    }
}
