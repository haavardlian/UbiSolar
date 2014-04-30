package com.sintef_energy.ubisolar.model;

/**
 * Created by thb on 19.02.14.
 *
 * A replica of the model on the backend.
 */
public abstract class Device
{
    protected long id;
    protected long userId;
    protected String name;
    protected String description;
    protected int category;
    protected boolean isTotal;
    protected boolean deleted;
    protected long lastUpdated;

    public Device()
    {

    }

    public Device(long id, String name, String description, long userId,
                  int category) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.userId = userId;
        this.category = category;
        this.isTotal = false;
        this.deleted = false;
    }


    public Device(long id, String name, String description, long userId,
                  int category, boolean isTotal) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.userId = userId;
        this.category = category;
        this.isTotal = isTotal;
        this.deleted = false;
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
        updateLastUpdate();
    }

    public void setUserId(long userId) {
        this.userId = userId;
        updateLastUpdate();
    }

    public void setName(String name) {
        this.name = name;
        updateLastUpdate();
    }

    public void setDescription(String description) {
        this.description = description;
        updateLastUpdate();
    }

    public void setCategory(int category) {
        this.category = category;
        updateLastUpdate();
    }

    public boolean isTotal() {
        return isTotal;
    }

    public void setIsTotal(boolean isTotal) {
        this.isTotal = isTotal;
        updateLastUpdate();
    }

    public boolean isDeleted() {
        return deleted;
    }

    public void setDeleted(boolean isDeleted) {
        this.deleted = isDeleted;
        updateLastUpdate();
    }

    public long getLastUpdated() {
        return lastUpdated;
    }

    public void setLastUpdated(long lastUpdated) {
        this.lastUpdated = lastUpdated;
    }
}
