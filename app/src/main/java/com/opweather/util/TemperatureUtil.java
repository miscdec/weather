package com.opweather.util;

import android.content.Context;
import com.opweather.R;

public class TemperatureUtil {
    private static final int NO_TEMP_DATA_FLAG = -2000;

    public static String getCurrentTemperature(Context context, int cTemp) {
        float f;
        String tempUnit = "°";
        if (SystemSetting.getTemperature(context)) {
            f = (float) cTemp;
        } else {
            f = SystemSetting.celsiusToFahrenheit((float) cTemp);
        }
        int curTemp = (int) f;
        String str = "--";
        return curTemp < -2000 ? "--" + tempUnit : curTemp + tempUnit;
    }

    public static String getHighTemperature(Context context, int hTemp) {
        float f;
        String tempUnit = "°";
        if (SystemSetting.getTemperature(context)) {
            f = (float) hTemp;
        } else {
            f = SystemSetting.celsiusToFahrenheit((float) hTemp);
        }
        int highTemp = (int) f;
        String str = "--";
        return highTemp < -2000 ? "--" + tempUnit : highTemp + tempUnit;
    }

    public static String getLowTemperature(Context context, int lTemp) {
        float f;
        String tempUnit = "°";
        if (SystemSetting.getTemperature(context)) {
            f = (float) lTemp;
        } else {
            f = SystemSetting.celsiusToFahrenheit((float) lTemp);
        }
        int lowTemp = (int) f;
        String str = "--";
        return lowTemp < -2000 ? "--" + tempUnit : lowTemp + tempUnit;
    }

    public static String getTemperatureUnit(Context context) {
        return SystemSetting.getTemperature(context) ? context.getString(R.string.c) : context.getString(R.string.f);
    }
}
