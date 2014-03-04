package com.sintef_energy.ubisolar.structs;

/**
 * Created by thb on 19.02.14.
 */
public class Device
{
    private int device_id;
    private String name;
    private String description;
    private int user_id;

    private Device()
    {

    }

    public Device(int device_id, String name, String description, int user_id) {
        this.device_id = device_id;
        this.name = name;
        this.description = description;
        this.user_id = user_id;
    }

    public int getDevice_id() {
        return device_id;
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

    public String toString()
    {
        return name;
    }
}
