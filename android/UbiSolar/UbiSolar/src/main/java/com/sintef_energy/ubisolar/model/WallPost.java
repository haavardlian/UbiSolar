package com.sintef_energy.ubisolar.model;

/**
 * Created by Håvard on 09.05.2014.
 */
public class WallPost {
    private int id;
    private long userId;
    private  int message;
    private long timestamp;

    public WallPost(int id, long userId, int message, long timestamp) {
        this.id = id;
        this.userId = userId;
        this.message = message;
        this.timestamp = timestamp;
    }

    public WallPost() {
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public long getUserId() {
        return userId;
    }

    public void setUserId(long userId) {
        this.userId = userId;
    }

    public int getMessage() {
        return message;
    }

    public void setMessage(int message) {
        this.message = message;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }
}
