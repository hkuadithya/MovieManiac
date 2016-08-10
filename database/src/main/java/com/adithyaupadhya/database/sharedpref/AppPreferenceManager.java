package com.adithyaupadhya.database.sharedpref;

import android.content.Context;
import android.content.SharedPreferences;

import com.adithyaupadhya.database.DBConstants;

/**
 * Created by adithya.upadhya on 26-03-2016.
 */
public class AppPreferenceManager {
    private static final String PREF_FILE_NAME = "com.adithyaupadhya.moviemaniac.APP_PREF_FILE";
    private static AppPreferenceManager mPreferenceManager;
    private final SharedPreferences mPreference;

    private AppPreferenceManager(Context context) {
        mPreference = context.getSharedPreferences(PREF_FILE_NAME, Context.MODE_PRIVATE);
    }

    public static AppPreferenceManager getAppPreferenceInstance(Context context) {
        if (mPreferenceManager == null)
            mPreferenceManager = new AppPreferenceManager(context);
        return mPreferenceManager;
    }

    public void setPreferenceData(String key, String value) {
        mPreference.edit().putString(key, value).apply();
    }

    public String getPreferenceData(String key) {
        return mPreference.getString(key, null);
    }

    public void removePreferenceData(String key) {
        mPreference.edit().remove(key).apply();
    }

    public void clearAllPreferenceData() {
        mPreference.edit().clear().apply();
    }

    public void clearAllUserPreferenceData() {
        mPreference.edit()
                .remove(DBConstants.USER_ID)
                .remove(DBConstants.USER_EMAIL)
                .remove(DBConstants.USER_NAME)
                .remove(DBConstants.USER_PROFILE_PIC)
                .apply();
    }
}
