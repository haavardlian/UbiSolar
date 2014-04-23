package com.sintef_energy.ubisolar.preferences;

import android.content.Context;
import android.content.SharedPreferences;

import java.util.Date;

/**
 * Based on http://yakivmospan.wordpress.com/2014/03/11/best-practice-sharedpreferences/?utm_source=Android+Weekly&utm_campaign=60136c0692-Android_Weekly_94&utm_medium=email&utm_term=0_4eb677ad19-60136c0692-337821861
 */
public class PreferencesManager {
 
    private static final String PREF_NAME = PreferencesManager.class.getName() + ".PREFS";
    public static final String KEY_ACCESS_TOKEN = PreferencesManager.class.getName() + ".KEY_ACCESS_TOKEN";
    public static final String KEY_ACCESS_TOKEN_EXPIRES = PreferencesManager.class.getName() + ".KEY_ACCESS_TOKEN_EXPIRES";

    @Deprecated /** Not needed. UID auth is done in the server */
    public static final String KEY_FACEBOOK_UID = PreferencesManager.class.getName() + ".KEY_FACEBOOK_UID";

    private static PreferencesManager sInstance;
    private final SharedPreferences mPref;

    private PreferencesManager(Context context) {
        mPref = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
    }

    public static synchronized void initializeInstance(Context context) {
        if (sInstance == null) {
            sInstance = new PreferencesManager(context);
        }
    }
 
    public static synchronized PreferencesManager getInstance() {
        if (sInstance == null) {
            throw new IllegalStateException(PreferencesManager.class.getSimpleName() +
                " is not initialized, call initializeInstance(..) method first.");
        }
        return sInstance;
    }

    public void setAccessToken(String value) {
        mPref.edit()
            .putString(KEY_ACCESS_TOKEN, value)
            .apply();
    }

    public void setKeyAccessTokenExpires(Date date){
       mPref.edit()
            .putFloat(KEY_ACCESS_TOKEN_EXPIRES, date.getTime())
                .apply();
    }

    public void setKeyFacebookUid(String uid){
        mPref.edit()
                .putString(KEY_FACEBOOK_UID, uid)
                .apply();
    }
 
    public String getAccessToken()  {
        return mPref.getString(KEY_ACCESS_TOKEN, "");
    }

    public Date getAccessTokenExpires(){
        return new Date(mPref.getLong(KEY_ACCESS_TOKEN_EXPIRES, 0));
    }

    public String getKeyFacebookUid(){
        return mPref.getString(KEY_FACEBOOK_UID, "");
    }

    public void clearFacebookSessionData(){
        remove(KEY_ACCESS_TOKEN);
        remove(KEY_ACCESS_TOKEN_EXPIRES);
    }
    /**
     * Removes a given key
     * @param key The key of the value to be removed
     */
    public void remove(String key) {
        mPref.edit()
        .remove(key)
        .apply();
    }

    /**
     * Removes everything!
     *
     * @return success
     */
    public boolean clear() {
        return mPref.edit()
        .clear()
        .commit();
    }
}