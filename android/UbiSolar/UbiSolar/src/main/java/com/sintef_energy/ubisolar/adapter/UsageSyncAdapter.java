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
import com.sintef_energy.ubisolar.preferences.PreferencesManagerSync;
import com.sintef_energy.ubisolar.presenter.RequestManager;
import com.sintef_energy.ubisolar.utils.Global;
import com.sintef_energy.ubisolar.utils.Utils;

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
        try {
            //TODO:Get the auth token for the current account
            //String authToken = mAccountManager.blockingGetAuthToken(account, AccountGeneral.AUTHTOKEN_TYPE_FULL_ACCESS, true);

            /* STEP 1: SETUP FILES */
            Log.v(TAG, "Starting sync operation");

            PreferencesManager preferencesManager;
            PreferencesManagerSync prefManagerSyn;
            RequestManager requestManager;
            ArrayList<DeviceModel> serverDeviceModels;
            ArrayList<DeviceModel> serverDeviceModelsError;
            ArrayList<DeviceModel> localDeviceModels;
            ArrayList<EnergyUsageModel> serverUsageModels;
            ArrayList<EnergyUsageModel> serverUsageModelsError;
            ArrayList<EnergyUsageModel> localUsageModels;

            try {
                preferencesManager = PreferencesManager.getInstance();
            } catch (IllegalStateException ex) {
                preferencesManager = PreferencesManager.initializeInstance(getContext());
            }

            try {
                prefManagerSyn = PreferencesManagerSync.getInstance();
            } catch (IllegalStateException ex) {
                prefManagerSyn = PreferencesManagerSync.initializeInstance(getContext());
            }

            try {
                requestManager = RequestManager.getInstance();
            } catch (IllegalStateException ex) {
                requestManager = RequestManager.getSyncInstance(getContext());
            }

            /* STEP 2: Init */
            long lastTimestamp = prefManagerSyn.getSyncTimestamp();
            long newTimestamp = System.currentTimeMillis() / 1000L;
            long uid = Long.valueOf(preferencesManager.getKeyFacebookUid());

            //If user is not authorized with an id, end.
            if (uid < 0) {
                Log.v(TAG, "No user id. Sync aborted");
                return;
            }

            //TODO Set all timestamps through Date object with GMT timezone
            // Must also fix ask for currenTime on server and use correct offset

            /* STEP 3: Get new data from local db */
            localDeviceModels = EnergyDataSource.getAllSyncDevices(getContext().getContentResolver(), lastTimestamp);
            localUsageModels = EnergyDataSource.getAllSyncUsage(getContext().getContentResolver(), lastTimestamp);

            /* STEP 4: DEVICE get backend */
            Log.v(TAG, "Time is: " + newTimestamp + ". Syncing for date: " + lastTimestamp + ". For UID: " + uid);

            serverDeviceModels = requestManager.doSyncRequest().getBackendDeviceSync(uid, lastTimestamp);
            serverUsageModels = requestManager.doSyncRequest().getBackendUsageSync(uid, lastTimestamp);

            /* STEP 5: Send all local to server */
            if(localDeviceModels == null)
                Log.e(TAG, "Local DeviceModels query error.");
            else if (localDeviceModels.size() > 0) {
                Log.v(TAG, "Sending # DeviceModels to server: " + localDeviceModels.size());
                serverDeviceModelsError = requestManager.doSyncRequest().putFrontendDeviceSync(uid, localDeviceModels);
                if (serverDeviceModelsError.size() > 0)
                    Log.e(TAG, "Frontend sync with devices to server failed with # of models: " + serverDeviceModelsError.size());
            }

            if(localUsageModels == null)
                Log.e(TAG, "Local usage query error.");
            else if (localUsageModels.size() > 0) {
                Log.v(TAG, "Sending # EnergyUsageModels to server: " + localUsageModels.size());
                serverUsageModelsError = requestManager.doSyncRequest().putFrontendUsageSync(uid, localUsageModels);
                if (serverUsageModelsError.size() > 0)
                    Log.e(TAG, "Frontend sync with usage to server failed with # of models: " + serverUsageModelsError.size());
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

            /* STEP Update time */
            prefManagerSyn.setBackendDeviceSyncTimestamp(newTimestamp);

            /* SEND UPDATED */
            //Send the new usage to the navdrawer
            if(serverUsageModels != null && serverUsageModels.size() > 0) {
                Log.v(TAG, "Broadcasting usage update: " + serverUsageModels.size());
                Intent i = new Intent(Global.BROADCAST_NAV_DRAWER);
                i.putExtra(Global.DATA_B_NAV_DRAWER_USAGE, serverUsageModels.size());
                LocalBroadcastManager.getInstance(this.getContext()).sendBroadcast(i);
            }

            Log.v(TAG, "Synchronization complete."
                    + "\nAdded DevicesModels to local DB: " + nDevice
                    + "\nAdded EnergyUsageModels to local DB: " + nUsage
                    + "\nDeviceModels deletion state: " + deleteSuccess
                    + "\nEnergyUsageModels deletion state: " + deleteUsageSuccess);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
