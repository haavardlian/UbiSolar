package com.sintef_energy.ubisolar.structs;

import com.yammer.dropwizard.json.JsonSnakeCase;

/**
 * Created by Håvard on 05.03.14.
 */
@JsonSnakeCase
public class TipRating {
    private int id;
    private int tipId;
    private short rating;
    private long userId;

    public TipRating() {
    }

    public TipRating(int id, int tipId, short rating, long userId) {
        this.id = id;
        this.tipId = tipId;
        this.rating = rating;
        this.userId = userId;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getTipId() {
        return tipId;
    }

    public void setTipId(int tipId) {
        this.tipId = tipId;
    }

    public short getRating() {
        return rating;
    }

    public void setRating(short rating) {
        this.rating = rating;
    }

    public long getUserId() {
        return userId;
    }

    public void setUserId(long userId) {
        this.userId = userId;
    }
}
