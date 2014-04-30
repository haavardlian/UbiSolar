package com.sintef_energy.ubisolar.preferences;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * Based on http://yakivmospan.wordpress.com/2014/03/11/best-practice-sharedpreferences/?utm_source=Android+Weekly&utm_campaign=60136c0692-Android_Weekly_94&utm_medium=email&utm_term=0_4eb677ad19-60136c0692-337821861
 */
public class PreferencesManagerSync {

    private static final String PREF_NAME = PreferencesManagerSync.class.getName() + ".PREFS";
    public static final String KEY_SYNC_TIMESTAMP = PreferencesManagerSync.class.getName() + ".KEY_SYNC_TIMESTAMP";

    private static PreferencesManagerSync sInstance;
    private final SharedPreferences mPref;

    private PreferencesManagerSync(Context context) {
        mPref = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
    }

    public static synchronized PreferencesManagerSync initializeInstance(Context context) {
        if (sInstance == null) {
            sInstance = new PreferencesManagerSync(context);
        }

        return sInstance;
    }
 
    public static synchronized PreferencesManagerSync getInstance() {
        if (sInstance == null) {
            throw new IllegalStateException(PreferencesManagerSync.class.getSimpleName() +
                " is not initialized, call initializeInstance(..) method first.");
        }

        return sInstance;
    }


    public void setBackendDeviceSyncTimestamp(long value) {
        mPref.edit()
            .putLong(KEY_SYNC_TIMESTAMP, value)
            .apply();
    }

    public long getSyncTimestamp()  {
        return mPref.getLong(KEY_SYNC_TIMESTAMP, 0);
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