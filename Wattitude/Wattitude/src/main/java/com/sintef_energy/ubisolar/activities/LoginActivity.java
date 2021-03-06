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

package com.sintef_energy.ubisolar.activities;

import android.accounts.Account;
import android.accounts.AccountAuthenticatorActivity;
import android.accounts.AccountManager;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.facebook.Request;
import com.facebook.Response;
import com.facebook.Session;
import com.facebook.SessionState;
import com.facebook.model.GraphUser;
import com.sintef_energy.ubisolar.Interfaces.ILoginCallback;
import com.sintef_energy.ubisolar.R;
import com.sintef_energy.ubisolar.utils.Global;
import com.sintef_energy.ubisolar.utils.Utils;

/**
 * Handles the generation of a Facebook session. Be it a token that has expired, or setup of
 * permissions.
 */
public class LoginActivity extends AccountAuthenticatorActivity implements ILoginCallback{
    private static final String TAG = LoginActivity.class.getName();

    // Constants
    // An account type, in the form of a domain name
    private static String ACCOUNT_TYPE;
    // The account name
    private static String ACCOUNT;

    private FacebookSessionStatusCallback mFacebookSessionStatusCallback;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        /* Setup dummy account */
        ACCOUNT_TYPE = getResources().getString(R.string.auth_account_type);
        ACCOUNT = getResources().getString(R.string.app_name);

        mFacebookSessionStatusCallback = new FacebookSessionStatusCallback(this);


        /* Is there internet? */
        if(!Utils.isNetworkOn(getApplicationContext())){
            Toast.makeText(getApplicationContext(), R.string.no_internet, Toast.LENGTH_SHORT).show();
        }

        // Setup session state and start session login if necessary
        Session session = Session.getActiveSession();
        if (session == null) {
            if (savedInstanceState != null) {
                session = Session.restoreSession(this, null, mFacebookSessionStatusCallback, savedInstanceState);
            }
            if (session == null) {
                session = new Session(this);
            }
            Session.setActiveSession(session);
        }

        if (session.getState().equals(SessionState.CREATED_TOKEN_LOADED)) {
            Log.v(TAG, "CREATED_TOKEN_LOADED");
            //session.openForRead(new Session.OpenRequest(this).setCallback(mFacebookSessionStatusCallback));
            Session.openActiveSession(this, false, mFacebookSessionStatusCallback);
        }
        else if (!session.isOpened() && !session.isClosed()) {
            Log.v(TAG, "Facebook in limbo. Setting up request");
            Session.OpenRequest request = new Session.OpenRequest(this);
            request.setPermissions(Global.FACEBOOK_READ_PERMISSIONS);
            request.setCallback(mFacebookSessionStatusCallback);
            session.openForRead(request);
        } else {
            Log.v(TAG, "No session data. Opening login window if necessary");
            Session.openActiveSession(this, true, mFacebookSessionStatusCallback);
        }
     }

    /**
     * The view has been created.
     * Authentication will be handled here.
     */
    @Override
    public void onStart(){
        super.onStart();
        Session.getActiveSession().addCallback(mFacebookSessionStatusCallback);
    }

    @Override
    public void onStop() {
        super.onStop();
        Session.getActiveSession().removeCallback(mFacebookSessionStatusCallback);
    }

    @Override
    public void onDestroy(){
        super.onDestroy();
        cleanUpReferences();
    }

    /**
     * Everything is nulled out so the GC can collect the fragment instance.
     */
    private void cleanUpReferences(){
        mFacebookSessionStatusCallback = null;
        ACCOUNT_TYPE = null;
        ACCOUNT = null;
    }


    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        Session session = Session.getActiveSession();
        Session.saveSession(session, outState);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Session.getActiveSession().onActivityResult(this, requestCode, resultCode, data);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.login, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    /**
     * Used when the login session is acquired by the status callback.
     * Will quit the Activity and return to the app.
     *
     * @param data
     */
    @Override
    public void loginFinished(Bundle data) {
        //Something failed if there is no data
        if(data != null) {
            String accountType = this.getIntent().getStringExtra(Global.DATA_LOGIN_ACCOUNT_TYPE);
            if (accountType == null) {
                accountType = ACCOUNT_TYPE;
            }

            AccountManager accMgr = AccountManager.get(this);

            String name = data.getString(Global.DATA_FB_UID);

            // This is the magic that adds the account to the Android Account Manager
            final Account account = new Account(name, accountType); //Account as ID?

            if (accMgr.addAccountExplicitly(account, null, data)){
                Log.v(TAG, "CreateSyncAccount successful");
            } else {
                Log.v(TAG, "CreateSyncAccount failed: Most probably because account already exists.");
            }

            // Return the result to the caller
            final Intent intent = new Intent();
            intent.putExtra(AccountManager.KEY_ACCOUNT_NAME, data.getString(Global.DATA_FB_UID));
            intent.putExtra(AccountManager.KEY_ACCOUNT_TYPE, accountType);
            intent.putExtra(AccountManager.KEY_AUTHTOKEN, data.getString(Global.DATA_AUTH_TOKEN));

            this.setAccountAuthenticatorResult(intent.getExtras());
            this.setResult(RESULT_OK, intent);
            this.finish();
        } else {
            Log.e(TAG, "loginFinished: Error: No data. Login failed.");
            this.setResult(RESULT_CANCELED, null);
            this.finish();
        }
     }

    /**
     * Handles the login to FB
     */
    private class FacebookSessionStatusCallback implements Session.StatusCallback {
        private final String TAG = FacebookSessionStatusCallback.class.getName();

        private ILoginCallback mLoginCallback;
        private Context mContext;

        FacebookSessionStatusCallback(Activity loginCallback){
            mLoginCallback = (ILoginCallback)loginCallback;
            mContext = loginCallback.getApplicationContext();
        }

        // callback when session changes state
        @Override
        public void call(Session session, SessionState state, Exception exception) {

            final Bundle data = new Bundle();

            // There is no publish rights
            if(session.isOpened() && state == SessionState.OPENED && !session.getPermissions().contains("publish_stream")){
                session.requestNewPublishPermissions(new Session.NewPermissionsRequest(LoginActivity.this, Global.FACEBOOK_PUBLISH_PERMISSIONS ));
            }
            else if (session.isOpened() && (state == SessionState.OPENED_TOKEN_UPDATED || state == SessionState.CREATED_TOKEN_LOADED)) {

                Log.v(TAG, "Facebook logged in.");

                /* Set session data */
                data.putString(Global.DATA_AUTH_TOKEN, session.getAccessToken());
                data.putString(Global.DATA_EXPIRATION_DATE, ""+session.getExpirationDate().getTime());

                Toast.makeText(mContext, mContext.getResources().getString(R.string.fb_login), Toast.LENGTH_LONG).show();

                /* How to do the callback?
                 * Handle the response or user object: Can get cached data
                 * -> Is it needed? Data should only be fetched, when the possibility for new data is there.
                 *  Don't need to store cached data.
                 */
                Request.newMeRequest(session, new Request.GraphUserCallback() {
                    @Override
                    public void onCompleted(GraphUser user, Response response) {
                        if (response.getConnection() != null || response.getIsFromCache()) {
                            data.putString(Global.DATA_FB_UID, user.getId());

                            mLoginCallback.loginFinished(data);
                        } else {
                            Log.e(TAG, "No facebook data return on newMeRequest.");
                            mLoginCallback.loginFinished(null);
                        }

                    }
                }).executeAsync();
            }
            // Login failed
            else if(session.getState() == SessionState.CLOSED_LOGIN_FAILED){
                Log.v(TAG, "Facebook login failed, or window closed. Returning to app.");
                //Go back to app
                mLoginCallback.loginFinished(null);
            }
            else {
                Log.v(TAG, "Facebook state lost in space.");
            }
        }
    }
}
