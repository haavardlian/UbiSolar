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

package com.sintef_energy.ubisolar.adapter;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.content.AbstractThreadedSyncAdapter;
import android.content.ContentProviderClient;
import android.content.Context;
import android.content.Intent;
import android.content.SyncResult;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import com.sintef_energy.ubisolar.database.energy.DeviceModel;
import com.sintef_energy.ubisolar.database.energy.EnergyDataSource;
import com.sintef_energy.ubisolar.database.energy.EnergyUsageModel;
import com.sintef_energy.ubisolar.preferences.PreferencesManager;
import com.sintef_energy.ubisolar.presenter.RequestManager;
import com.sintef_energy.ubisolar.utils.Global;

import java.util.ArrayList;

/**
 * Created by perok on 26.03.14.
 *
 * The sync provider for devices and usage.
 *
 * http://udinic.wordpress.com/2013/07/24/write-your-own-android-sync-adapter/
 *
 * Synchronization is based on delete bits and timestamp.
 *
 * Step 1: Init files
 * Redundant: Check for network. END if no net. (SyncAdapter runs only when net is present)
 *
 * Step 2: get time and uid
 * Get last frontend sync timestamp
 * Get last backend sync timestamp
 * Get time now
 * END if no UID
 *
 * Step 3: Get all new  and deleted frontend data from local db
 *
 * Step 4: Ask server for all new and deleted data
 *
 * Step 5: Send all local new and delete data to server
 *
 * Step 6: Insert all new data. Delete all delete data for good.
 *
 * Step 7: Update timestamps
 *
 */
public class UsageSyncAdapter extends AbstractThreadedSyncAdapter{

    private static final String TAG = UsageSyncAdapter.class.getName();

    private final AccountManager mAccountManager;

    public UsageSyncAdapter(Context context, boolean autoInitialize) {
        super(context, autoInitialize);
        mAccountManager = AccountManager.get(context);
    }

    @Override
    public void onPerformSync(Account account, Bundle bundle, String s, ContentProviderClient provider, SyncResult syncResult) {
        Log.d(TAG, "onPerformSync for account[" + account.name + "]");
        StringBuilder builder = new StringBuilder();
        try {
            //TODO:Get the auth token for the current account
            //String authToken = mAccountManager.blockingGetAuthToken(account, AccountGeneral.AUTHTOKEN_TYPE_FULL_ACCESS, true);

            String accUid = mAccountManager.getUserData(account, Global.DATA_FB_UID);
            String timestamp = mAccountManager.getUserData(account, Global.ACC_SYNC_LAST_TIMESTAMP);

            if(accUid == null){
                Log.v(TAG, "No FB UID for sync account. Aborting");
                return;
            }

            /* STEP 1: SETUP FILES */
            Log.v(TAG, "Starting sync operation");

            RequestManager requestManager;
            ArrayList<DeviceModel> serverDeviceModels;
            ArrayList<DeviceModel> serverDeviceModelsError;
            ArrayList<DeviceModel> localDeviceModels;
            ArrayList<EnergyUsageModel> serverUsageModels;
            ArrayList<EnergyUsageModel> serverUsageModelsError;
            ArrayList<EnergyUsageModel> localUsageModels;
            builder.append("Synchronization started");

            try {
                requestManager = RequestManager.getInstance();
            } catch (IllegalStateException ex) {
                requestManager = RequestManager.getInstance(getContext().getApplicationContext());
            }

            /* STEP 2: Init */
            long lastTimestamp = 0;
            if(timestamp != null)
                lastTimestamp = Long.valueOf(timestamp);
            long newTimestamp = System.currentTimeMillis() / 1000L;
            long uid = Long.valueOf(accUid);

            builder.append("\nlastTimestamp: "); builder.append(lastTimestamp);
            builder.append("\nnewTimestamp: "); builder.append(newTimestamp);

            //If user is not authorized with an id, end.
            if (uid < 0) {
                builder.append("\nNo user id. Sync aborted");
                Log.v(TAG, builder.toString());
                return;
            }
            builder.append("\nUser id: "); builder.append(uid);

            //TODO Set all timestamps through Date object with GMT timezone
            // Must also fix ask for currenTime on server and use correct offset

            /* STEP 3: Get new data from local db */
            localDeviceModels = EnergyDataSource.getAllSyncDevices(getContext().getContentResolver(), lastTimestamp, uid);
            localUsageModels = EnergyDataSource.getAllSyncUsage(getContext().getContentResolver(), lastTimestamp, uid);

            /* STEP 4: DEVICE get backend */

            serverDeviceModels = requestManager.doSyncRequest().getBackendDeviceSync(uid, lastTimestamp);
            serverUsageModels = requestManager.doSyncRequest().getBackendUsageSync(uid, lastTimestamp);

            /* STEP 5: Send all local to server */
            if(localDeviceModels == null)
                builder.append("\nERROR: Local DeviceModels query error.");
            else if (localDeviceModels.size() > 0) {
                builder.append("\nSending # DeviceModels to server: "); builder.append(localDeviceModels.size());
                serverDeviceModelsError = requestManager.doSyncRequest().putFrontendDeviceSync(uid, localDeviceModels);
                if (serverDeviceModelsError.size() > 0)
                    builder.append("\nERROR: Frontend sync with devices to server failed with # of models: "); builder.append(serverDeviceModelsError.size());
            }

            if(localUsageModels == null)
                builder.append("\nERROR: Local usage query error.");
            else if (localUsageModels.size() > 0) {
                builder.append("\nSending # EnergyUsageModels to server: " + localUsageModels.size());
                serverUsageModelsError = requestManager.doSyncRequest().putFrontendUsageSync(uid, localUsageModels);
                if (serverUsageModelsError.size() > 0)
                    builder.append("\nERROR: Frontend sync with usage to server failed with # of models: "); builder.append(serverUsageModelsError.size());
            }

            /* STEP 6: Insert and delete */
            ArrayList<DeviceModel> deleteDeviceModels = new ArrayList<>();
            ArrayList<EnergyUsageModel> deleteUsageModels = new ArrayList<>();

            // SIDESTEP Find deleted
            if (serverDeviceModels != null){
                if (serverDeviceModels.size() > 0) {
                    Log.v(TAG, "Device id's on the server: " + serverDeviceModels.size());

                    for (DeviceModel dm : serverDeviceModels) {
                        if (dm.isDeleted()) {
                            deleteDeviceModels.add(dm);
                            serverDeviceModels.remove(dm);
                        }
                    }
                }
            }
            else
                Log.e(TAG, "Device server get sync failed");

            for(DeviceModel dm : localDeviceModels)
                if(dm.isDeleted())
                    deleteDeviceModels.add(dm);

            if (serverUsageModels != null){
                if (serverUsageModels.size() > 0) {
                    Log.v(TAG, "Usage id's on the server: " + serverUsageModels.size());

                    for (EnergyUsageModel dm : serverUsageModels) {
                        if (dm.isDeleted()) {
                            deleteUsageModels.add(dm);
                            serverUsageModels.remove(dm);
                        }
                    }
                }
            }
            else
                Log.e(TAG, "Usage server get sync failed");

            for(EnergyUsageModel dm : localUsageModels)
                if(dm.isDeleted())
                    deleteUsageModels.add(dm);

            int nDevice = EnergyDataSource.addBatchDeviceModel(getContext().getContentResolver(), serverDeviceModels);
            int nUsage = EnergyDataSource.addBatchEnergyModel(getContext().getContentResolver(), serverUsageModels);

            boolean deleteSuccess = true;
            boolean deleteUsageSuccess = true;

            if(deleteDeviceModels.size() > 0)
                deleteSuccess = EnergyDataSource.batchDeleteDeviceSyncOp(getContext().getContentResolver(), deleteDeviceModels);

            if(deleteUsageModels.size() > 0)
                deleteUsageSuccess = EnergyDataSource.batchDeleteUsageSyncOp(getContext().getContentResolver(), deleteUsageModels);

            /* STEP Update time TODO: Only set if everything was success */
            if(deleteSuccess && deleteUsageSuccess) {
                mAccountManager.setUserData(account, Global.ACC_SYNC_LAST_TIMESTAMP, String.valueOf(newTimestamp));
                builder.append("\nNew timstamp added to account");
            } else
                builder.append("\nNOT UPDATED NEW TIMESTAMP TO ACCOUNT");

            /* SEND UPDATED */
            //Send the new usage to the navdrawer
            if(serverUsageModels != null && serverUsageModels.size() > 0) {
                Log.v(TAG, "Broadcasting usage update: " + serverUsageModels.size());
                Intent i = new Intent(Global.BROADCAST_NAV_DRAWER);
                i.putExtra(Global.DATA_B_NAV_DRAWER_USAGE, serverUsageModels.size());
                LocalBroadcastManager.getInstance(this.getContext()).sendBroadcast(i);
            }

            builder.append("\nAdded DevicesModels to local DB: "); builder.append(nDevice);
            builder.append("\nAdded EnergyUsageModels to local DB: "); builder.append(nUsage);

            if(localDeviceModels != null){
                builder.append("\nAdded DeviceModels to server: "); builder.append(localDeviceModels.size());
                //builder.append("\n\tError #: " + serverDeviceModelsError.size());
            }
            if(localUsageModels != null){builder.append("\nAdded EnergyUsageModel to server: "); builder.append(localUsageModels.size());}

            builder.append("\nDeviceModels deletion state: "); builder.append(deleteSuccess);
            builder.append("\nEnergyUsageModels deletion state: "); builder.append(deleteUsageSuccess);

            Log.v(TAG, builder.toString());
        } catch (Exception e) {
            e.printStackTrace();
            Log.e(TAG, builder.toString());
        }
    }
}
