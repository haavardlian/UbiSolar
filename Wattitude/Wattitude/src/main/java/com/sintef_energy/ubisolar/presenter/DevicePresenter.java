/*
 * Licensed to UbiCollab.org under one or more contributor
 * license agreements.  See the NOTICE file distributed
 * with this work for additional information regarding
 * copyright ownership. UbiCollab.org licenses this file
 * to you under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance
 * with the License. You may obtain a copy of the License at
 *
 * 	http://www.apache.org/licenses/LICENSE-2.0
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

import com.sintef_energy.ubisolar.database.energy.DeviceModel;
import com.sintef_energy.ubisolar.database.energy.EnergyDataSource;
import com.sintef_energy.ubisolar.preferences.PreferencesManager;


/**
 * Created by pialindkjolen on 05.03.14.
 */
public class DevicePresenter {

    private static final String TAG = DevicePresenter.class.getName();

    /* The listeners */
    //private ArrayList<IDeviceView> dmListeners;

    /* Listeners
    public void registerListener(IDeviceView view){
        this.dmListeners.add(view);
    }

    public void unregisterListener(IDeviceView view){
        this.dmListeners.remove(view);
    }*/

    public void addDevice(DeviceModel device, ContentResolver contentResolver){
        String uid = PreferencesManager.getInstance().getKeyFacebookUid();
        device.setUserId(Long.valueOf(uid));
        device.updateLastUpdate();
        EnergyDataSource.insertDevice(contentResolver, device);
        //this.dmModels.add(device);
    }

    public boolean editDevice(ContentResolver contentResolver, DeviceModel dm) {
        //TODO: Add support for actually handling if the update fails.
        String uid = PreferencesManager.getInstance().getKeyFacebookUid();
        dm.setUserId(Long.valueOf(uid));
        dm.updateLastUpdate();
        return EnergyDataSource.editDevice(contentResolver, dm) == 1;
    }
}
