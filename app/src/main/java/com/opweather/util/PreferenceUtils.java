package com.opweather.util;

import android.content.Context;
import android.content.SharedPreferences.Editor;
import android.preference.PreferenceManager;

public class PreferenceUtils {
    private PreferenceUtils() {
    }

    public static void applyString(Context context, String key, String value) {
        Editor editor = PreferenceManager.getDefaultSharedPreferences(context).edit();
        editor.putString(key, value);
        editor.apply();
    }

    public static boolean commitString(Context context, String key, String value) {
        Editor editor = PreferenceManager.getDefaultSharedPreferences(context).edit();
        editor.putString(key, value);
        return editor.commit();
    }

    public static String getString(Context context, String key, String defaultValue) {
        return PreferenceManager.getDefaultSharedPreferences(context).getString(key, defaultValue);
    }

    public static void remove(Context context, String key) {
        Editor editor = PreferenceManager.getDefaultSharedPreferences(context).edit();
        editor.remove(key);
        editor.apply();
    }

    public static boolean contains(Context context, String key) {
        return PreferenceManager.getDefaultSharedPreferences(context).contains(key);
    }

    public static void applyLone(Context context, String key, long value) {
        Editor editor = PreferenceManager.getDefaultSharedPreferences(context).edit();
        editor.putLong(key, value);
        editor.apply();
    }

    public static boolean commitLong(Context context, String key, long value) {
        Editor editor = PreferenceManager.getDefaultSharedPreferences(context).edit();
        editor.putLong(key, value);
        return editor.commit();
    }

    public static long getLong(Context context, String key, long defaultValue) {
        return PreferenceManager.getDefaultSharedPreferences(context).getLong(key, defaultValue);
    }

    public static void applyInt(Context context, String key, int value) {
        Editor editor = PreferenceManager.getDefaultSharedPreferences(context).edit();
        editor.putInt(key, value);
        editor.apply();
    }

    public static boolean commitInt(Context context, String key, int value) {
        Editor editor = PreferenceManager.getDefaultSharedPreferences(context).edit();
        editor.putInt(key, value);
        return editor.commit();
    }

    public static int getInt(Context context, String key, int defaultValue) {
        return PreferenceManager.getDefaultSharedPreferences(context).getInt(key, defaultValue);
    }

    public static boolean getBoolean(Context context, String key) {
        return PreferenceManager.getDefaultSharedPreferences(context).getBoolean(key, false);
    }
}
