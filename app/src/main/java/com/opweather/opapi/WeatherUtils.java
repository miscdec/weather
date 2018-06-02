package com.opweather.opapi;

import android.text.TextUtils;

import com.opweather.api.helper.DateUtils;
import com.opweather.api.nodes.Alarm;

import java.util.ArrayList;
import java.util.List;

public class WeatherUtils {
    public static DailyForecastsWeather getTodayForecast(RootWeather weather) {
        return DailyForecastsWeather.getTodayForecast(weather.getDailyForecastsWeather(), DateUtils.getTimeZone(weather.getCurrentWeather().getLocalTimeZone()));
    }

    public static List<Alarm> getAlarmsRes(List<Alarm> alarms) {
        if (alarms == null || alarms.size() < 1) {
            return null;
        }
        List<Alarm> resAlarms = new ArrayList();
        resAlarms.add(alarms.get(0));
        int countWeather = alarms.size();
        int i = 1;
        while (i < countWeather) {
            try {
                Alarm tempAlarm = (Alarm) alarms.get(i);
                if (TextUtils.isEmpty(tempAlarm.getTypeName()) || TextUtils.isEmpty(tempAlarm.getContentText()) || tempAlarm.getTypeName().equalsIgnoreCase("None") || tempAlarm.getContentText().equalsIgnoreCase("None")) {
                    break;
                }
                int count = resAlarms.size();
                int j = 0;
                while (j < count && !((Alarm) resAlarms.get(j)).getTypeName().equals(tempAlarm.getTypeName())) {
                    j++;
                }
                if (j >= count) {
                    resAlarms.add(tempAlarm);
                }
                i++;
            } catch (Exception e) {
                return alarms;
            }
        }
        return resAlarms;
    }
}
