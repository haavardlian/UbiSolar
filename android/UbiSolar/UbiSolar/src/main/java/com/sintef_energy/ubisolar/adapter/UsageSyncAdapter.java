package com.sintef_energy.ubisolar.adapter;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.content.AbstractThreadedSyncAdapter;
import android.content.ContentProviderClient;
import android.content.Context;
import android.content.SyncResult;
import android.nfc.tech.NfcF;
import android.os.Bundle;
import android.util.Log;

import com.sintef_energy.ubisolar.database.energy.DeviceModel;
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
 * Synchronization is based on the data classes having dirty bits and delete bits.
 *
 * Everything new is dirty. Everything synced is clean. When something is deleted, it is set deleted and dirty
 *
 * Algorithm:
 *
 * Get all dirty
 * Send all dirty to server
 * Everything dirty that was deleted
 *  Delete it locally
 * (server deletes it as well)
 *
 * Ask server for all dirty
 * Update/ insert/ delete all dirty.
 * (server then deletes, it's deleted)
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
            Log.v(TAG, "Starting sync operation");

            PreferencesManager preferencesManager;
            PreferencesManagerSync prefManagerSyn;
            RequestManager requestManager;

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

            /* DEVICE get backend */
            long lastTimestamServerDevice = prefManagerSyn.getBackendDeviceSyncTimestamp();
            long newTimestampServerDevice = System.currentTimeMillis() / 1000L;
            long uid = Long.valueOf(preferencesManager.getKeyFacebookUid());

            Log.v(TAG, "Time is: " + newTimestampServerDevice + ". Syncing for date: " + lastTimestamServerDevice + ". For UID: " + uid);

            ArrayList<DeviceModel> deviceModels = requestManager.doSyncRequest().getBackendSync(uid, lastTimestamServerDevice);
            //TODO MÅ LEGGE TIL LASTMODIFIED TIL DATABASE
            if(deviceModels != null){
                if(deviceModels.size() > 0) {

                    //TODO Insert new data into DB
                    Log.v(TAG, "Device id's on the server: " + deviceModels.size());
                    //prefManagerSyn.setBackendDeviceSyncTimestamp(newTimestampServerDevice);

                    for(DeviceModel dm : deviceModels){
                        Log.e(TAG, ""+dm);
                    }
                }
            }
            else
                Log.e(TAG, "Device sync failed");

            /* TODO DEVICE send frontend */
            /* TODO USAGE */

            //TODO:Get the auth token for the current account
            //String authToken = mAccountManager.blockingGetAuthToken(account, AccountGeneral.AUTHTOKEN_TYPE_FULL_ACCESS, true);

            //TODO: Setup Presenter
            //ParseComServerAccessor parseComService = new ParseComServerAccessor();


            // Get shows from the remote server
            //List remoteTvShows = parseComService.getShows(authToken);

            // Get shows from the local storage
            /*ArrayList localTvShows = new ArrayList();
            Cursor curTvShows = provider.query(EnergyContract.Devices.CONTENT_URI, null, null, null, null);
            if (curTvShows != null) {
                while (curTvShows.moveToNext()) {
                    localTvShows.add(TvShow.fromCursor(curTvShows));
                }
                curTvShows.close();
            }*/

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
