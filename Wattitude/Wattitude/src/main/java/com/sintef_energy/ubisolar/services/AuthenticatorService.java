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