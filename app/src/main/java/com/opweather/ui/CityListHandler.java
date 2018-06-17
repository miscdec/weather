package com.opweather.ui;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;

import com.opweather.db.CityWeatherDB;


public class CityListHandler extends Handler {
    public static final int MESSAGE_DELETE_COMPLETE = -1;
    private Context mContext;

    public CityListHandler(Looper looper, Context context) {
        super(looper);
        this.mContext = context;
    }

    public void handleMessage(Message msg) {
        if (!hasMessages(msg.what)) {
            CityWeatherDB.getInstance(mContext).deleteCity((Long) msg.obj);
        }
    }
}
