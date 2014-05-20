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

package com.sintef_energy.ubisolar.utils;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.StrictMode;
import android.widget.Toast;

import com.sintef_energy.ubisolar.R;
import com.sintef_energy.ubisolar.database.energy.DeviceModel;
import com.sintef_energy.ubisolar.database.energy.EnergyContract;
import com.sintef_energy.ubisolar.database.energy.EnergyDataSource;
import com.sintef_energy.ubisolar.preferences.PreferencesManager;

/**
 * Created by perok on 13.04.14.
 */
public class Utils {

    public static void makeShortToast(Context c, String text){
        Toast.makeText(c, text, Toast.LENGTH_SHORT).show();
    }

    public static void makeLongToast(Context c, String text){
        Toast.makeText(c, text, Toast.LENGTH_LONG).show();
    }

    /**
     * Helper class to check if app has internet connection.
     * @param context
     * @return
     */
    public static boolean isNetworkOn(Context context) {
        ConnectivityManager connMgr =
                (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();

        return (networkInfo != null && networkInfo.isConnected());
    }

    /**
     * Debug with strict mode
     */
    public static void developerMode(ContentResolver cr, boolean devMode, boolean testData){
        if(testData) {
            TestdataHelper.clearDatabase(cr);
//            //Populate the database if it's empty
//            if (EnergyDataSource.getEnergyModelSize(getContentResolver()) == 0) {
//                Log.v(TAG, "Developer mode: Database empty. Populating it.");
                TestdataHelper.createDevices(cr);
//                //            createEnergyUsage();
//            }
        }

        if(devMode){
            StrictMode.setThreadPolicy(
                    new StrictMode.ThreadPolicy.Builder()
                            .detectDiskReads()
                            .detectDiskWrites()
                            .detectNetwork()
                            .penaltyLog()
                            .build());

            StrictMode.setVmPolicy(
                    new StrictMode.VmPolicy.Builder()
            .detectLeakedSqlLiteObjects()
            .detectLeakedClosableObjects()
            .penaltyLog()
            .build());
        }
    }

    public static void createTotal(ContentResolver contentResolver, Activity activity){
        if(EnergyDataSource.getDeviceModelSize(contentResolver) == 0) {
            PreferencesManager preferencesManager = PreferencesManager.getInstance();
            DeviceModel device = new DeviceModel(
                    1,
                    Long.valueOf(preferencesManager.getKeyFacebookUid()),
                    activity.getString(R.string.total_name),
                    activity.getString(R.string.total_description),
                    -1);

            contentResolver.insert(
                    EnergyContract.Devices.CONTENT_URI, device.getContentValues());
        }
    }
}
