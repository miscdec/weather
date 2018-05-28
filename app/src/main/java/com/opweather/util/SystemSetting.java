package com.opweather.util;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.text.TextUtils;
import com.opweather.bean.CityData;

import java.util.*;

public class SystemSetting {

    private static List<OnDataChangeListener> mListeners =  mListeners = new ArrayList();

    public interface OnDataChangeListener {
        void onHumidityChanged(boolean z);

        void onTemperatureChanged(boolean z);

        void onUnitChanged(boolean z);

        void onWindChanged(boolean z);
    }

    public static void addOnDataChangeListener(OnDataChangeListener l) {
        mListeners.clear();
        mListeners.add(l);
    }

    public static void removeOnDataChangeListener(OnDataChangeListener l) {
        mListeners.remove(l);
    }

    public static void removeAllDataListener() {
        if (mListeners != null && mListeners.size() > 0) {
            mListeners.clear();
        }
    }

    public static void notifyWeatherDataChange(Context context) {
//        WeatherLog.d("notifyWeatherDataChange");
//        context.getContentResolver().notifyChange(WeatherDataSharedProvider.CONTENT_URI, null);
//        Intent intent = new Intent(WidgetTypeUtil.ACTION_APPWIDGET_REFRESH);
//        intent.setPackage(WidgetTypeUtil.WIDGET_PKG_NAME);
//        context.sendBroadcast(intent, WidgetTypeUtil.ACTION_APPWIDGET_REFRESH_PERMISSION);
    }

    public static boolean getTemperature(Context context) {
        return read(context, "Temperature");
    }

    public static boolean getWind(Context context) {
        return read(context, "Wind");
    }

    public static boolean getHumidity(Context context) {
        return read(context, "Humidity");
    }

    public static boolean isWeatherWarningEnabled(Context context) {
        return read(context, "TheWeatherWarn");
    }

    public static boolean isWeatherAlarmActive(Context context) {
        return read(context, "WeatherAlarmActive", false);
    }

    public static HashMap<String, Object> getAlarmInfo(Context context) {
        HashMap<String, Object> alarmMap = new HashMap();
        alarmMap.put("WeatherAlarmCity", read(context, "WeatherAlarmCity", null));
        alarmMap.put("WeatherAlarmType", read(context, "WeatherAlarmType", null));
        alarmMap.put("WeatherAlarmCount", Integer.valueOf(read(context, "WeatherAlarmCount", 0)));
        return alarmMap;
    }

    public static void setAlarmInfo(Context context, String alarmCity, String alarmType, int count) {
        if (!TextUtils.isEmpty(alarmType) && !TextUtils.isEmpty(alarmCity)) {
            write(context, "WeatherAlarmCity", alarmCity);
            write(context, "WeatherAlarmType", alarmType);
            write(context, "WeatherAlarmCount", count);
        }
    }

    public static void setLocationOrDefaultCity(Context context, CityData city) {
        if (city != null && !TextUtils.isEmpty(city.getLocationId()) && !city.getLocationId().equals("0")) {
            write(context, "city_name", city.getName());
            write(context, "city_localname", city.getLocalName());
            write(context, "city_provider", city.getProvider());
            write(context, "city_locationid", city.getLocationId());
            write(context, "city_locatedcity", city.isLocated());
            write(context, "city_defaultcity", city.isDefault());
        }
    }

    public static CityData getLocationOrDefaultCity(Context context) {
        CityData city = new CityData();
        city.setName(read(context, "city_name", null));
        city.setLocalName(read(context, "city_localname", null));
//        city.setProvider(read(context, "city_provider", (int) CitySearchProvider.PROVIDER_WEATHER_CHINA));
        city.setLocationId(read(context, "city_locationid", null));
        city.setLocated(read(context, "city_locatedcity", true));
        city.setDefault(read(context, "city_defaultcity", false));
        return city;
    }

    public static void setTemperature(Context context, boolean checked) {
        if (getTemperature(context) != checked) {
            write(context, "Temperature", checked);
            if (mListeners != null) {
                for (OnDataChangeListener l : mListeners) {
                    l.onUnitChanged(true);
                }
            }
        }
    }

    public static void setWind(Context context, boolean checked) {
        if (getWind(context) != checked) {
            write(context, "Wind", checked);
            if (mListeners != null) {
                for (OnDataChangeListener l : mListeners) {
                    l.onWindChanged(checked);
                }
            }
        }
    }

    public static void setHumidity(Context context, boolean checked) {
        if (getHumidity(context) != checked) {
            write(context, "Humidity", checked);
            if (mListeners != null) {
                for (OnDataChangeListener l : mListeners) {
                    l.onHumidityChanged(checked);
                }
            }
        }
    }

    public static void setWeatherWarningEnabled(Context context, boolean checked) {
        write(context, "TheWeatherWarn", checked);
    }

    public static void setWeatherAlarmActive(Context context, boolean isActive) {
        write(context, "WeatherAlarmActive", isActive);
    }

    private static void write(Context context, String key, boolean value) {
        if (!TextUtils.isEmpty(key)) {
            SharedPreferences.Editor editor = context.getSharedPreferences("setting", 0).edit();
            editor.putBoolean(key, value);
            editor.apply();
        }
    }

    private static void write(Context context, String key, long value) {
        SharedPreferences.Editor editor = context.getSharedPreferences("setting", 0).edit();
        editor.putLong(key, value);
        editor.commit();
    }

    private static void write(Context context, String key, String value) {
        SharedPreferences.Editor editor = context.getSharedPreferences("setting", 0).edit();
        editor.putString(key, value);
        editor.commit();
    }

    private static void write(Context context, String key, int value) {
        SharedPreferences.Editor editor = context.getSharedPreferences("setting", 0).edit();
        editor.putInt(key, value);
        editor.commit();
    }

    public static float kmToMp(float km) {
        return 0.621f * km;
    }

    public static float celsiusToFahrenheit(float degree) {
        return ((9.0f * degree) / 5.0f) + 32.0f;
    }

    private static boolean read(Context context, String key) {
        return read(context, key, true);
    }

    private static boolean read(Context context, String key, boolean defaultVal) {
        return context.getSharedPreferences("setting", 0).getBoolean(key, defaultVal);
    }

    private static String read(Context context, String key, String defaultVal) {
        return context.getSharedPreferences("setting", 0).getString(key, defaultVal);
    }

    private static int read(Context context, String key, int defaultVal) {
        return context.getSharedPreferences("setting", 0).getInt(key, defaultVal);
    }

    private static long read(Context context, String key, long defaultVal) {
        return context.getSharedPreferences("setting", 0).getLong(key, defaultVal);
    }

    public static void setRefreshTime(Context context, String locationId, long time) {
        write(context, locationId, time);
    }

    public static long getRefreshTime(Context context, String locationId) {
        return context.getSharedPreferences("setting", 0).getLong(locationId, 0);
    }

    public static boolean isChina() {
        String id = TimeZone.getDefault().getID();
        return id.equals("Asia/Shanghai") || id.equals("Asia/Chongqing") || id.equals("Asia/Urumqi") || id.equals("Asia/Macao") || id.equals("Asia/Hong_Kong");
    }

    public static String getLocale(Context context) {
        return read(context, "locale", null);
    }

    public static void setLocale(Context context) {
        write(context, "locale", Locale.getDefault().toString());
    }
}
