package com.opweather.ui;

import android.app.Application;
import android.content.Context;

public class BaseApplication extends Application {
    private static Context mContext;

    public void onCreate() {
        super.onCreate();
        mContext = getApplicationContext();
    }

    public void onTerminate() {
        super.onTerminate();
    }

    public void onLowMemory() {
        super.onLowMemory();
    }

    public static Context getContext() {
        return mContext;
    }
}
