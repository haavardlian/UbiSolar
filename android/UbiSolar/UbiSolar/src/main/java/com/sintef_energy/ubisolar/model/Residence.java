/*
 * Licensed to UbiCollab.org under one or more contributor
 * license agreements.  See the NOTICE file distributed
 * with this work for additional information regarding
 * copyright ownership. UbiCollab.org licenses this file
 * to you under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance
 * with the License. You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

package com.sintef_energy.ubisolar.model;

/**
 * Created by Lars Erik on 18.03.14.
 */
public class Residence {
    protected String houseName;
    protected String description;
    protected int residents;
    protected int area;
    protected int zipCode;
    protected char energyClass;
    protected long userId;

    protected String status;

    public Residence(String houseName,String description, int residents, int area,
                     int zipCode, char energyClass, long userId) {
        this.houseName = houseName;
        this.description = description;
        this.residents = residents;
        this.area = area;
        this.zipCode = zipCode;
        this.energyClass = energyClass;
        this.status = houseName;
        this.userId = userId;
    }

    public Residence() {}

    public String getHouseName() {
        return houseName;
    }

    public void setHouseName(String houseName){
        this.houseName = houseName;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public char getEnergyClass() {return energyClass;}

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

    public long getUserId() {return userId;}

    public void setUserId(long userId) {
        this.userId = userId;
    }

    public void setStatus(String status) {this.status = status;}

    public String getStatus() {return status;}

    public String toString() {
        return getHouseName();
    }
}
