package com.opweather.api.nodes;

import android.content.Context;

import com.opweather.api.helper.WeatherUtils;

import java.util.Date;

public class OppoForeignDailyForecastsWeather extends DailyForecastsWeather {
    private final Date mDate;
    private final int mDayWeatherId;
    private String mDayWeatherText;
    private final Temperature mMaxTemperature;
    private final Temperature mMinTemperature;
    private final int mNightWeatherId;
    private String mNightWeatherText;
    private final Sun mSun;

    public OppoForeignDailyForecastsWeather(String areaCode, String dataSource, int dayWeatherId, int nightWeatherId,
                                            Date date, Temperature minTemperature, Temperature maxTemperature, Sun
                                                    sun) {
        this(areaCode, null, dataSource, dayWeatherId, nightWeatherId, date, minTemperature, maxTemperature, sun);
    }

    public OppoForeignDailyForecastsWeather(String areaCode, String areaName, String dataSource, int dayWeatherId,
                                            int nightWeatherId, Date date, Temperature minTemperature, Temperature
                                                    maxTemperature, Sun sun) {
        super(areaCode, areaName, dataSource);
        mDayWeatherId = dayWeatherId;
        mNightWeatherId = nightWeatherId;
        mDate = date;
        mMinTemperature = minTemperature;
        mMaxTemperature = maxTemperature;
        mSun = sun;
    }

    public String getWeatherName() {
        return "Oppo Foreign Daily Forecasts Weather";
    }

    public Date getDate() {
        return mDate;
    }

    public int getDayWeatherId() {
        return mDayWeatherId;
    }

    public String getMobileLink() {
        return null;
    }

    public String getDayWeatherText(Context context) {
        if (mDayWeatherText == null) {
            mDayWeatherText = WeatherUtils.getOppoForeignWeatherTextById(context, mDayWeatherId);
        }
        return mDayWeatherText;
    }

    public int getNightWeatherId() {
        return mNightWeatherId;
    }

    public String getNightWeatherText(Context context) {
        if (mNightWeatherText == null) {
            mNightWeatherText = WeatherUtils.getOppoForeignWeatherTextById(context, mNightWeatherId);
        }
        return mNightWeatherText;
    }

    public Temperature getMinTemperature() {
        return mMinTemperature;
    }

    public Temperature getMaxTemperature() {
        return mMaxTemperature;
    }

    public Sun getSun() {
        return mSun;
    }
}
