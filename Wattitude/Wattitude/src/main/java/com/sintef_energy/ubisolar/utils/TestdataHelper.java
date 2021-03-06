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

package com.sintef_energy.ubisolar.utils;

import android.content.ContentResolver;
import android.database.Cursor;
import android.net.Uri;
import android.util.Log;

import com.sintef_energy.ubisolar.database.energy.DeviceModel;
import com.sintef_energy.ubisolar.database.energy.EnergyContract;
import com.sintef_energy.ubisolar.preferences.PreferencesManager;

/**
 * HELPER DATA GENERATION!!! Avoid.
 * Created by perok on 29.04.14.
 */
public class TestdataHelper {

    private  static final String TAG = TestdataHelper.class.getName();

    public static void createDevices(ContentResolver cr)
    {
        addDevice(cr, "Total", "-", 0);
        addDevice(cr, "TV", "Livingroom", 1);
        addDevice(cr, "Radio", "Kitchen", 1);
        addDevice(cr, "Heater", "Second floor", 1);
        addDevice(cr, "Oven", "Kitchen", 2);
    }

    private static void addDevice(ContentResolver contentResolver,
                                  String name, String description, int category) {
        PreferencesManager preferencesManager = PreferencesManager.getInstance();
        DeviceModel device = new DeviceModel(
                System.currentTimeMillis(),
                Long.valueOf(preferencesManager.getKeyFacebookUid()),
                name,
                description,
                category,
                false,
                System.currentTimeMillis() / 1000L);

        contentResolver.insert(
                EnergyContract.Devices.CONTENT_URI, device.getContentValues());

        //mDevices.put(device.getDeviceId(), device);
        Log.v(TAG, "Created device: " + device.getName());
    }

    /*
    private static void createEnergyUsage(ContentResolver cr)
    {
        EnergyUsageModel usageModel;
        int n = 1000;
        int nDevices = mDevices.size();
        ContentValues[] values = new ContentValues[n * nDevices];

        Calendar cal = Calendar.getmInstance();
        Random random = new Random();
        Date date = new Date();
        int idCount = 1337;
        int y = 0;
        for(Device device : mDevices.values()) {
            cal.setTime(date);
            Log.v(TAG, "Creating data for: " + device.getName());
            for (int i = 0; i < n; i++) {
                cal.add(Calendar.HOUR_OF_DAY, 1);

                usageModel = new EnergyUsageModel(
                        idCount++,
                        device.getDeviceId(),
                        cal.getTime(),
                        random.nextInt(151) + 50);//(200 - 50) + 1) + 50);
                values[i + (y * n)] = usageModel.getContentValues();
                //EnergyDataSource.addEnergyModel(cr, usageModel);
            }
            y++;
        }
        Log.v(TAG, "Starting to add data to DB.");
        EnergyDataSource.addBatchEnergyModel(cr, values);
        Log.v(TAG, "Done adding data to DB");
    }*/

    public static void clearDatabase(ContentResolver cr){

        Uri.Builder builder = EnergyContract.Devices.CONTENT_URI.buildUpon();
        builder.appendPath(EnergyContract.DELETE);
        int it = cr.delete(builder.build(), null, null);
        Log.v(TAG, "EMPTY DATABASE: " + it);

        builder = EnergyContract.Energy.CONTENT_URI.buildUpon();
        builder.appendPath(EnergyContract.DELETE);
        it = cr.delete(builder.build(), null, null);
        Log.v(TAG, "EMPTY DATABASE: " + it);
    }

    public static void testDateQuery(ContentResolver cr){
        Uri.Builder builder = EnergyContract.Energy.CONTENT_URI.buildUpon();
        builder.appendPath(EnergyContract.Energy.Date.Month);
        Cursor c = cr.query(builder.build(), null, null, null, null);

        Log.v(TAG, "TESTQUERY: " + c.getCount());
        c.moveToFirst();
        int i = 0;
        do{
            Log.v(TAG, "TABLE: " + i++ + " -> " + c.getLong(0) + " " + c.getLong(1) + " " + c.getLong(2) + " " + c.getLong(3));
        } while(c.moveToNext());

        c.close();
    }
}
