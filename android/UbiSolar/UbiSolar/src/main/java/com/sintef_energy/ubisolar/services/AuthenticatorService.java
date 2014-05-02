package com.sintef_energy.ubisolar.services;

/**
 * Created by perok on 27.03.14.
 */

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

import com.sintef_energy.ubisolar.utils.Authenticator;

/**
 * A bound Service that instantiates the authenticator
 * when started.
 */
public class AuthenticatorService extends Service {

    private static final Object sSyncAdapterLock = new Object();
    // Instance field that stores the authenticator object
    private static Authenticator mAuthenticator;
    @Override
    public void onCreate() {
        // Create a new authenticator object
        synchronized (sSyncAdapterLock) {
            if (mAuthenticator == null)
                mAuthenticator = new Authenticator(this);
        }
    }
    /*
     * When the system binds to this Service to make the RPC call
     * return the authenticator's IBinder.
     */
    @Override
    public IBinder onBind(Intent intent) {
        return mAuthenticator.getIBinder();
    }
}