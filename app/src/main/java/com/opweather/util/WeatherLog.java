package com.opweather.util;

import android.util.Log;

public class WeatherLog {
    private static boolean DEBUG = true;
    private static final String TAG = "OPWeather";

    public static void v(String msg) {
        if (DEBUG) {
            Log.v(TAG, msg);
        }
    }

    public static void d(String msg) {
        if (DEBUG) {
            Log.d(TAG, msg);
        }
    }

    public static void i(String msg) {
        if (DEBUG) {
            Log.i(TAG, msg);
        }
    }

    public static void w(String msg) {
        if (DEBUG) {
            Log.w(TAG, msg);
        }
    }

    public static void e(String msg) {
        if (DEBUG) {
            Log.e(TAG, msg);
        }
    }

    public static void v(String tag, String msg) {
        if (DEBUG) {
            Log.v(TAG, tag + " [" + msg + "]");
        }
    }

    public static void d(String tag, String msg) {
        if (DEBUG) {
            Log.d(TAG, tag + " [" + msg + "]");
        }
    }

    public static void i(String tag, String msg) {
        if (DEBUG) {
            Log.i(TAG, tag + " [" + msg + "]");
        }
    }

    public static void w(String tag, String msg) {
        if (DEBUG) {
            Log.w(TAG, tag + " [" + msg + "]");
        }
    }

    public static void e(String tag, String msg) {
        if (DEBUG) {
            Log.e(TAG, tag + " [" + msg + "]");
        }
    }

    public static void v(String tag, String msg, Exception e) {
        if (DEBUG) {
            Log.v(TAG, tag + " [" + msg + "/n" + e.toString() + "]");
        }
    }

    public static void d(String tag, String msg, Exception e) {
        if (DEBUG) {
            Log.d(TAG, tag + " [" + msg + "/n" + e.toString() + "]");
        }
    }

    public static void e(String tag, String msg, Exception e) {
        if (DEBUG) {
            Log.e(TAG, tag + " [" + msg + "/n" + e.toString() + "]");
        }
    }

    public static void w(String tag, String msg, Exception e) {
        if (DEBUG) {
            Log.w(TAG, tag + " [" + msg + "/n" + e.toString() + "]");
        }
    }

    public static void i(String tag, String msg, Exception e) {
        if (DEBUG) {
            Log.i(TAG, tag + " [" + msg + "/n" + e.toString() + "]");
        }
    }
}
