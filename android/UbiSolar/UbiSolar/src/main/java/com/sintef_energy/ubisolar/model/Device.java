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
public class Device
{
    protected long id;
    protected long userId;
    protected String name;
    protected String description;
    protected int category;
    protected boolean deleted;
    protected long lastUpdated;

    public Device() {}

    public Device(long id, long userId, String name, String description, int category, boolean deleted, long lastUpdated) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.userId = userId;
        this.category = category;
        this.deleted = deleted;
        this.lastUpdated = lastUpdated;
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
    }

    public void setUserId(long userId) {
        this.userId = userId;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setCategory(int category) {
        this.category = category;
    }

    public boolean isDeleted() {
        return deleted;
    }

    public void setDeleted(boolean isDeleted) {
        this.deleted = isDeleted;
    }

    public long getLastUpdated() {
        return lastUpdated;
    }

    public void setLastUpdated(long lastUpdated) {
        this.lastUpdated = lastUpdated;
    }
}