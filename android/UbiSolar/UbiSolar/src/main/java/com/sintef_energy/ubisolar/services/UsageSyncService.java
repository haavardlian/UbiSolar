package com.sintef_energy.ubisolar.services;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

import com.sintef_energy.ubisolar.adapter.UsageSyncAdapter;

/**
 * Created by perok on 26.03.14.
 */
public class UsageSyncService extends Service {

    private static final Object sSyncAdapterLock = new Object();
    private static UsageSyncAdapter sSyncAdapter = null;

    @Override
    public void onCreate() {
        synchronized (sSyncAdapterLock) {
            if (sSyncAdapter == null)
                sSyncAdapter = new UsageSyncAdapter(getApplicationContext(), true);
        }
    }

    @Override
    public IBinder onBind(Intent intent) {
        return sSyncAdapter.getSyncAdapterBinder();
    }
}
