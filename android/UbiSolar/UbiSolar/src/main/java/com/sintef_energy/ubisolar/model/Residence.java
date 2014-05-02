package com.sintef_energy.ubisolar.model;

/**
 * Created by Lars Erik on 18.03.14.
 */
public class Residence {
    protected String houseId;
    protected String description;
    protected int residents;
    protected int area;
    protected int zipCode;
    protected char energyClass;

    protected String status;

    public Residence(String houseId,String description, int residents, int area,
                     int zipCode, char energyClass) {
        this.houseId = houseId;
        this.description = description;
        this.residents = residents;
        this.area = area;
        this.zipCode = zipCode;
        this.energyClass = energyClass;
        this.status = houseId;
    }

    public Residence() {

    }

    public String getHouseId() {
        return houseId;
    }

    public void setHouseId(String houseId) {
        this.houseId = houseId;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public char getEnergyClass() {
        return energyClass;
    }

    public void setEnergyClass(char energyClass) {
        this.energyClass = energyClass;
    }

    public void setEnergyClass(String energyClass) {
        this.energyClass = energyClass.charAt(0);
    }

    public int getZipCode() {
        return zipCode;
    }

    public void setZipCode(int zipCode) {
        this.zipCode = zipCode;
    }

    public int getResidents() {
        return residents;
    }

    public void setResidents(int residents) {
        this.residents = residents;
    }

    public int getArea() {
        return area;
    }

    public void setArea(int area) {
        this.area = area;
    }

    public String toString() {
        return getHouseId();
    }

    public void setStatus(String status) {this.status = status;}

    public String getStatus() {return status;}
}
