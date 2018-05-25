package com.opweather.opapi;

import android.content.Context;

import java.util.Date;
import java.util.List;
import java.util.TimeZone;

public abstract class DailyForecastsWeather extends AbstractWeather {
    public abstract Date getDate();

    public abstract int getDayWeatherId();

    public abstract String getDayWeatherText(Context context);

    public abstract Temperature getMaxTemperature();

    public abstract Temperature getMinTemperature();

    public abstract String getMobileLink();

    public abstract int getNightWeatherId();

    public abstract String getNightWeatherText(Context context);

    public abstract Sun getSun();

    public static DailyForecastsWeather findWeatherByDate(List<DailyForecastsWeather> list, Date date, TimeZone timeZone) {
        DailyForecastsWeather result = null;
        if (list == null || date == null || list.size() <= 0) {
            return null;
        }
        for (DailyForecastsWeather item : list) {
            if (item.isSameDay(date, timeZone)) {
                return item;
            }
            result = list.get(0);
        }
        return result;
    }

    public static DailyForecastsWeather getTodayForecast(List<DailyForecastsWeather> list, TimeZone timeZone) {
        return findWeatherByDate(list, new Date(), timeZone);
    }

    public DailyForecastsWeather(String areaCode, String areaName, String dataSource) {
        super(areaCode, areaName, dataSource);
    }

    public String getWeatherName() {
        return "Daily Forecasts Weather";
    }

    public boolean isSameDay(Date date, TimeZone timeZone) {
        return getDate() == null ? false : DateUtils.isSameDay(date, getDate(), timeZone);
    }

    public Sun getRealSun(List<DailyForecastsWeather> list, TimeZone timeZone) {
        Date rise = getSun().getRise();
        Date set = getSun().getSet();
        try {
            if (DateUtils.distanceOfHour(rise, null) > 0 || DateUtils.distanceOfHour(rise, null) == Integer.MIN_VALUE) {
                return getSun();
            }
            DailyForecastsWeather dailyWeather = findWeatherByDate(list, DateUtils.getDistanceDate(new Date(), 1),
                    timeZone);
            if (DateUtils.distanceOfHour(rise, null) >= 0 || DateUtils.distanceOfHour(set, null) <= 0) {
                if (DateUtils.distanceOfHour(set, null) < 0) {
                    rise = dailyWeather.getSun().getRise();
                    set = dailyWeather.getSun().getSet();
                }
                return new Sun(getAreaCode(), getAreaName(), getDataSource(), rise, set);
            }
            rise = dailyWeather.getSun().getRise();
            return new Sun(getAreaCode(), getAreaName(), getDataSource(), rise, set);
        } catch (Exception e) {
            rise = getSun().getRise();
            set = getSun().getSet();
        }
        return null;
    }
}
