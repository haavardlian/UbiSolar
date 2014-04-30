package com.sintef_energy.ubisolar.adapter;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.content.AbstractThreadedSyncAdapter;
import android.content.ContentProviderClient;
import android.content.Context;
import android.content.SyncResult;
import android.os.Bundle;
import android.util.Log;

import com.sintef_energy.ubisolar.database.energy.DeviceModel;
import com.sintef_energy.ubisolar.database.energy.EnergyDataSource;
import com.sintef_energy.ubisolar.preferences.PreferencesManager;
import com.sintef_energy.ubisolar.preferences.PreferencesManagerSync;
import com.sintef_energy.ubisolar.presenter.RequestManager;

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
 * TODO: Check for network. END if no net.
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
            Log.v(TAG, "Starting sync operation");

            /* STEP 1: SETUP FILES */
            PreferencesManager preferencesManager;
            PreferencesManagerSync prefManagerSyn;
            RequestManager requestManager;
            ArrayList<DeviceModel> serverDeviceModels;
            ArrayList<DeviceModel> serverDeviceModelsError;
            ArrayList<DeviceModel> localDeviceModels;

            try{
                preferencesManager = PreferencesManager.getInstance();
            } catch (IllegalStateException ex){
                preferencesManager = PreferencesManager.initializeInstance(getContext());
            }

            try{
                prefManagerSyn = PreferencesManagerSync.getInstance();
            } catch (IllegalStateException ex){
                prefManagerSyn = PreferencesManagerSync.initializeInstance(getContext());
            }

            try{
                requestManager = RequestManager.getInstance();
            } catch (IllegalStateException ex){
                requestManager = RequestManager.getInstance(getContext());
            }

            /* STEP 2: Init */
            long lastTimestamServerDevice = prefManagerSyn.getBackendDeviceSyncTimestamp();
            long newTimestampServerDevice = System.currentTimeMillis() / 1000L;
            long uid = Long.valueOf(preferencesManager.getKeyFacebookUid());

            //If user is not authorized with an id, end.
            if(uid < 0) {
                Log.v(TAG, "No user id. Sync aborted");
                return;
            }

            /* STEP 3: Get new data from local db */
            localDeviceModels = EnergyDataSource.getAllSyncDevicec(getContext().getContentResolver(), lastTimestamServerDevice);
            // TODO: Energy

            /* STEP 4: DEVICE get backend */
            Log.v(TAG, "Time is: " + newTimestampServerDevice + ". Syncing for date: " + lastTimestamServerDevice + ". For UID: " + uid);

            serverDeviceModels = requestManager.doSyncRequest().getBackendDeviceSync(uid, lastTimestamServerDevice);
            //TODO ENERGY

            /* STEP 5: Send all local to server */
            serverDeviceModelsError = requestManager.doSyncRequest().putFrontendDeviceSync(uid, localDeviceModels);
            if(serverDeviceModelsError.size() > 0)
                Log.e(TAG, "Frontend sync to server failed with # of models: " + serverDeviceModelsError.size());
            //TODO ENERGY

            /* STEP 6: Insert and delete */
            ArrayList<DeviceModel> deleteDeviceModels = new ArrayList<>();

            // Find deleted
            if(serverDeviceModels != null){
                if(serverDeviceModels.size() > 0) {

                    Log.v(TAG, "Device id's on the server: " + serverDeviceModels.size());
                    //prefManagerSyn.setBackendDeviceSyncTimestamp(newTimestampServerDevice);

                    for(DeviceModel dm : serverDeviceModels){
                        Log.v(TAG, ""+dm);
                        if(dm.isDeleted()){
                            deleteDeviceModels.add(dm);
                            serverDeviceModels.remove(dm);
                        }
                    }
                }
            }
            else
                Log.e(TAG, "Device sync failed");

            for(DeviceModel dm : localDeviceModels)
                if(dm.isDeleted())
                    deleteDeviceModels.add(dm);

            /* TODO USAGE */

            int nDevice = EnergyDataSource.addBatchDeviceModel(getContext().getContentResolver(), serverDeviceModels);
            boolean deleteSuccess = EnergyDataSource.batchDeleteDeviceSyncOp(getContext().getContentResolver(), deleteDeviceModels);

            /* STEP 7 */
            prefManagerSyn.setBackendDeviceSyncTimestamp(newTimestampServerDevice);

            Log.v(TAG, "Synchronization complete.");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
