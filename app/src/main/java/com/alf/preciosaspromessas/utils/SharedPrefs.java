package com.alf.preciosaspromessas.utils;

import android.content.Context;
import android.content.SharedPreferences;

public class SharedPrefs {
    private SharedPrefs() {}

    private static final String SESSION_PREFS = "com.jgm.preciosaspromessas.session_prefs";

    private static SharedPreferences getSessionPrefs(Context context) {
        return context.getSharedPreferences(SESSION_PREFS, Context.MODE_PRIVATE);
    }

    // CONSENT

    public static int getConsentPrefsInt(Context context, String key) {
        return getSessionPrefs(context).getInt(key, 0);
    }

    public static void setConsentPrefs(Context context, String key, int value) {
        SharedPreferences.Editor editor = getSessionPrefs(context).edit();
        editor.putInt(key, value);
        editor.apply();
    }

    // SOUND

    public static Boolean getSoundStatus(Context context, String key) {
        return getSessionPrefs(context).getBoolean(key, true);
    }

    public static void setSoundPrefs(Context context, String key, Boolean value) {
        SharedPreferences.Editor editor = getSessionPrefs(context).edit();
        editor.putBoolean(key, value);
        editor.apply();
    }

    // CLEAN

    public static void clearPrefs(Context context) {
        SharedPreferences.Editor editor = getSessionPrefs(context).edit();
        editor.clear();
        editor.apply();
    }

}
