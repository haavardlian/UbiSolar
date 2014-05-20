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

package com.sintef_energy.ubisolar.presenter;

import android.content.ContentResolver;
import android.net.Uri;
import android.util.Log;

import com.sintef_energy.ubisolar.database.energy.EnergyDataSource;
import com.sintef_energy.ubisolar.database.energy.EnergyUsageModel;

/**
 * Created by perok on 2/19/14.
 *
 * The Presenter class for energy usage.
 *
 * Contains ALL logic related to business logic manupulation, syncing, etc.
 * NOT for getting data the view. That should be done through cursors.
 *
 */
public class TotalEnergyPresenter {

    private static final String TAG = TotalEnergyPresenter.class.getName();

    public TotalEnergyPresenter(){
    }

    /**
     * Assigns and ID to the EnergyUsageModel and adds it to the database and internal list.
     * Notifies all listeners.
     * @param euModel The EnergyUsageModel to add.
     * @return Uri to resource, or null
     */
    public Uri addEnergyData(ContentResolver resolver, EnergyUsageModel euModel){
        euModel.setId(System.currentTimeMillis());
        euModel.updateLastUpdated();

        Uri uri = EnergyDataSource.addEnergyModel(resolver, euModel);

        Log.v(TAG, "addEnergyData: " + uri);

        return uri;
    }
}
