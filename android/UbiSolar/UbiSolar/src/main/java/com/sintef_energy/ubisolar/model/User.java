package com.sintef_energy.ubisolar.model;

/**
 * Created by Lars Erik on 18.03.14.
 */
public class User {

    private long userId;
    private String name;


    public User(String name) {
        this.name = name;
    }

    public User(long id, String name) {
        this.userId = id;
        this.name = name;
    }

    public User() {}
    public long getUserId() {
        return userId;
    }

    public void setUserId(long userId) {
        this.userId = userId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

}
