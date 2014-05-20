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
 * Created by thb on 19.02.14.
 *
 * A replica of the model on the backend.
 */
public class DeviceUsage
{
    private long id;
    private long deviceId;
    private long timestamp;
    private double powerUsage;
    private boolean deleted;
    private long lastUpdated;

    public DeviceUsage()
    {

    }

    public DeviceUsage(long id, long deviceId, long timestamp, double powerUsage, boolean deleted, long lastUpdated) {
        this.id = id;
        this.powerUsage = powerUsage;
        this.deviceId = deviceId;
        this.deleted = false;
        this.timestamp = timestamp;
        this.deleted = deleted;
        this.lastUpdated = lastUpdated;
    }

    public DeviceUsage(long id, long deviceId, long timestamp, double powerUsage) {
        this.id = id;
        this.powerUsage = powerUsage;
        this.deviceId = deviceId;
        this.deleted = false;
        this.timestamp = timestamp;
    }

    public void updateLastUpdated(){
        setLastUpdated(System.currentTimeMillis() / 1000L);
    }

    public double getPowerUsage() {
        return powerUsage;
    }

    public long getDeviceId() {

        return deviceId;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public void setDeviceId(long deviceId) {
        this.deviceId = deviceId;
    }

    public void setPowerUsage(double powerUsage) {
        this.powerUsage = powerUsage;
    }

    public boolean isDeleted() {
        return deleted;
    }

    public void setDeleted(boolean isDeleted){
        this.deleted = isDeleted;
    }

    public long getLastUpdated() {
        return lastUpdated;
    }

    public void setLastUpdated(long lastUpdated) {
        this.lastUpdated = lastUpdated;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }
}
