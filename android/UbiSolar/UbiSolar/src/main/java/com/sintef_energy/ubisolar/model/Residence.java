package com.sintef_energy.ubisolar.model;

/**
 * Created by Lars Erik on 18.03.14.
 */
public class Residence {
    private String houseId;
    private int area;
    private int residents;
    private int zipCode;
    private char energyClass;

    public Residence(String houseId, int residents, int area,
                     int zipCode, char energyClass) {
        this.houseId = houseId;
        this.residents = residents;
        this.area = area;
        this.zipCode = zipCode;
        this.energyClass = energyClass;
    }

    public String getHouseId() {
        return houseId;
    }

    public void setHouseId(String houseId) {
        this.houseId = houseId;
    }

    public char getEnergyClass() {
        return energyClass;
    }

    public void setEnergyClass(char energyClass) {
        this.energyClass = energyClass;
    }

    public int getZipCode() {
        return zipCode;
    }

    public void setZipCode(byte zipCode) {
        this.zipCode = zipCode;
    }

    public int getResidents() {
        return residents;
    }

    public void setResidents(byte residents) {
        this.residents = residents;
    }

    public int getArea() {
        return area;
    }

    public void setArea(int area) {
        this.area = area;
    }
}
