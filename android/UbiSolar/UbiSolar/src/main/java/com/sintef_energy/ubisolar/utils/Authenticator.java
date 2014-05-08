package com.sintef_energy.ubisolar.utils;

import android.accounts.AbstractAccountAuthenticator;
import android.accounts.Account;
import android.accounts.AccountAuthenticatorResponse;
import android.accounts.AccountManager;
import android.accounts.NetworkErrorException;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import com.sintef_energy.ubisolar.activities.LoginActivity;

/**
 * Created by perok on 26.03.14.
 *
 * Stub authenticator. Do not use..
 */
public class Authenticator extends AbstractAccountAuthenticator{
    private static final String TAG = Authenticator.class.getName();

    private Context mContext;

    public Authenticator(Context context){
        super(context);
        mContext = context;
    }

    @Override
    public Bundle editProperties(AccountAuthenticatorResponse accountAuthenticatorResponse, String s) {
        throw new UnsupportedOperationException();
    }

    @Override
    public Bundle addAccount(AccountAuthenticatorResponse response, String accountType, String authTokenType, String[] requiredFeatures, Bundle options) throws NetworkErrorException {

        // Create a Intent that points to LoginActivity with the given data so it can log in to FB.

        Log.v(TAG, "accType: " + accountType + "\nAuthTokenType: " + authTokenType);

        Bundle reply = new Bundle();

        Intent i = new Intent(mContext, LoginActivity.class);
        i.setAction(Global.ACTION_LOGIN_FB);
        i.putExtra(Global.DATA_LOGIN_ACCOUNT_TYPE, accountType);
        i.putExtra(Global.DATA_LOGIN_AUTH_TOKEN_TYPE, authTokenType);
        i.putExtra(AccountManager.KEY_ACCOUNT_AUTHENTICATOR_RESPONSE, response);
        reply.putParcelable(AccountManager.KEY_INTENT, i);

        return reply;
    }

    @Override
    public Bundle confirmCredentials(AccountAuthenticatorResponse accountAuthenticatorResponse, Account account, Bundle bundle) throws NetworkErrorException {
        return null;
    }

    @Override
    public Bundle getAuthToken(AccountAuthenticatorResponse accountAuthenticatorResponse, Account account, String s, Bundle bundle) throws NetworkErrorException {
        throw new UnsupportedOperationException();
    }

    @Override
    public String getAuthTokenLabel(String s){
        throw new UnsupportedOperationException();
    }

    @Override
    public Bundle updateCredentials(AccountAuthenticatorResponse accountAuthenticatorResponse, Account account, String s, Bundle bundle) throws NetworkErrorException {
        throw new UnsupportedOperationException();
    }

    @Override
    public Bundle hasFeatures(AccountAuthenticatorResponse accountAuthenticatorResponse, Account account, String[] strings) throws NetworkErrorException {
        throw new UnsupportedOperationException();
    }
}
