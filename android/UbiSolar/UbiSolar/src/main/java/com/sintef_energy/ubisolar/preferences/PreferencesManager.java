package com.sintef_energy.ubisolar.preferences;

import android.content.Context;
import android.content.SharedPreferences;
import android.widget.CheckBox;

import java.util.Date;

/**
 * Based on http://yakivmospan.wordpress.com/2014/03/11/best-practice-sharedpreferences/?utm_source=Android+Weekly&utm_campaign=60136c0692-Android_Weekly_94&utm_medium=email&utm_term=0_4eb677ad19-60136c0692-337821861
 */
public class PreferencesManager {
 
    private static final String PREF_NAME = PreferencesManager.class.getName() + ".PREFS";
    public static final String KEY_ACCESS_TOKEN = PreferencesManager.class.getName() + ".KEY_ACCESS_TOKEN";
    public static final String KEY_ACCESS_TOKEN_EXPIRES = PreferencesManager.class.getName() + ".KEY_ACCESS_TOKEN_EXPIRES";

    public static final String COMPARISON_AREA_CHECKED = PreferencesManager.class.getName() + ".COMPARISON_AREA_CHECKED";
    public static final String COMPARISON_RESIDENTS_CHECKED = PreferencesManager.class.getName() + ".COMPARISON_RESIDENTS_CHECKED";
    public static final String COMPARISON_SIZE_CHECKED = PreferencesManager.class.getName() + ".COMPARISON_SIZE_CHECKED";
    public static final String COMPARISON_ENERGY_CHECKED = PreferencesManager.class.getName() + ".COMPARISON_ENERGY_CHECKED";

    public static final String FACEBOOK_NAME = PreferencesManager.class.getName() + ".FACEBOOK_NAME";
    public static final String FACEBOOK_LOCATION = PreferencesManager.class.getName() + ".FACEBOOK_LOCATION";
    public static final String FACEBOOK_AGE = PreferencesManager.class.getName() + ".FACEBOOK_AGE";
    public static final String FACEBOOK_COUNTRY = PreferencesManager.class.getName() + ".FACEBOOK_COUNTRY";

    public static final String SELECTED_RESIDENCE = PreferencesManager.class.getName() + ".SELECTED_RESIDENCE";

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

    public void setSelectedResidence(String value) {
        mPref.edit()
                .putString(SELECTED_RESIDENCE, value)
                .apply();

    }

    public void setFacebookName(String value) {
        mPref.edit()
                .putString(FACEBOOK_NAME, value)
                .apply();
    }

    public void setFacebookLocation(String value) {
        mPref.edit()
                .putString(FACEBOOK_LOCATION, value)
                .apply();
    }

    public void setFacebookAge(String value) {
        mPref.edit()
                .putString(FACEBOOK_AGE, value)
                .apply();
    }

    public void setFacebookCountry(String value) {
        mPref.edit()
                .putString(FACEBOOK_COUNTRY, value)
                .apply();
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

    public void setComparisonAreaChecked(boolean value) {
        mPref.edit()
                .putBoolean(COMPARISON_AREA_CHECKED, value)
                .apply();
    }

    public void setComparisonSizeChecked(boolean value) {
        mPref.edit()
                .putBoolean(COMPARISON_SIZE_CHECKED, value)
                .apply();
    }

    public void setComparisonResidentsChecked(boolean value) {
        mPref.edit()
                .putBoolean(COMPARISON_RESIDENTS_CHECKED, value)
                .apply();
    }
    public void setComparisonEnergyChecked(boolean value) {
        mPref.edit()

                .putBoolean(COMPARISON_ENERGY_CHECKED, value)
                .apply();
    }

    public String getSelectedResidence() {
        return mPref.getString(SELECTED_RESIDENCE, "");
    }

    public String getFacebookName() {
        return mPref.getString(FACEBOOK_NAME, "");
    }

    public String getFacebookLocation() {
        return mPref.getString(FACEBOOK_LOCATION, "");
    }

    public String getFacebookAge() {
        return mPref.getString(FACEBOOK_AGE, "");
    }

    public String getFacebookCountry() {
        return mPref.getString(FACEBOOK_COUNTRY, "");
    }

    public String getAccessToken()  {
        return mPref.getString(KEY_ACCESS_TOKEN, "");
    }

    public boolean getComparisonAreaChecked(){
        return mPref.getBoolean(COMPARISON_AREA_CHECKED, false);
    }

    public boolean getComparisonSizeChecked(){
        return mPref.getBoolean(COMPARISON_SIZE_CHECKED, false);
    }

    public boolean getComparisonResidentsChecked(){
        return mPref.getBoolean(COMPARISON_RESIDENTS_CHECKED, false);
    }

    public boolean getComparisonEnergyChecked(){
        return mPref.getBoolean(COMPARISON_ENERGY_CHECKED, false);
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