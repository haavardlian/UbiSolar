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

package com.sintef_energy.ubisolar.structs;

import com.yammer.dropwizard.json.JsonSnakeCase;

/**
 * Created by Håvard on 05.03.14.
 *
 * Hold info about a rating for a Tip
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

    public String toString() {
        return "Id: " + this.id + "\n" +
               "Tip: " + this.tipId + "\n" +
               "User" + this.userId;
    }
}
