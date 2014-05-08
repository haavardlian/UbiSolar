package com.sintef_energy.ubisolar.model;

/**
 * Created by Lars Erik on 18.03.14.
 */
public class User {

    private String userId;
    private String name;
    private byte age;
    private byte zipCode;
    private Residence[] residences;
    private String country;

    public User (String userId, byte zipCode, byte age, String name,
                     Residence[] residences, String country) {
        this.userId = userId;
        this.zipCode = zipCode;
        this.age = age;
        this.name = name;
        this.residences = residences;
        this.country = country;
    }

    public User(String name) {
        this.name = name;
    }

    public User(String id, String name) {
        this.userId = id;
        this.name = name;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public byte getAge() {
        return age;
    }

    public void setAge(byte age) {
        this.age = age;
    }

    public byte getZipCode() {
        return zipCode;
    }

    public void setZipCode (byte zipCode) {
        this.zipCode = zipCode;
    }

    public Residence[] getResidences() {
        return residences;
    }

    public void setResidences(Residence[] residences) {
        this.residences = residences;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }
}
