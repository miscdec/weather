package com.opweather.util;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;

public class ClockUtils {
    private static ClockUtils mClockUtils;
    private AlarmManager mAlarmManager;
    private Context mContext;

    public static synchronized ClockUtils getInstance(Context mContext) {
        ClockUtils clockUtils;
        synchronized (ClockUtils.class) {
            if (mClockUtils == null) {
                mClockUtils = new ClockUtils(mContext);
            }
            clockUtils = mClockUtils;
        }
        return clockUtils;
    }

    public ClockUtils(Context context) {
        mContext = context;
        if (mAlarmManager == null) {
            mAlarmManager = (AlarmManager) mContext.getSystemService(Context.ALARM_SERVICE);
        }
    }

    public void setClock(PendingIntent intent, long startTime) {
        if (intent != null && mAlarmManager != null) {
            cancleClock(intent);
            mAlarmManager.set(AlarmManager.ELAPSED_REALTIME, startTime, intent);
        }
    }

    public void cancleClock(PendingIntent intent) {
        if (mAlarmManager != null && intent != null) {
            mAlarmManager.cancel(intent);
        }
    }

    public boolean isClockActive() {
        return SystemSetting.isWeatherAlarmActive(mContext);
    }

    public void setClockActive(boolean isActive) {
        SystemSetting.setWeatherAlarmActive(mContext, isActive);
    }
}
