package com.opweather.api.nodes;

import android.content.Context;

import com.opweather.api.helper.WeatherUtils;
import com.opweather.api.impl.SwaRequest;

import java.util.Date;

public class SwaDailyForecastsWeather extends DailyForecastsWeather {
    private final Date mDate;
    private final int mDayWeatherId;
    private String mDayWeatherText;
    private final Temperature mMaxTemperature;
    private final Temperature mMinTemperature;
    private final int mNightWeatherId;
    private String mNightWeatherText;
    private final Sun mSun;

    public SwaDailyForecastsWeather(String areaCode, String areaName, int dayWeatherId, int nightWeatherId, Date
            date, Temperature maxTemperature, Temperature minTemperature, Sun sun) {
        super(areaCode, areaName, SwaRequest.DATA_SOURCE_NAME);
        mDayWeatherId = dayWeatherId;
        mNightWeatherId = nightWeatherId;
        mDate = date;
        mMaxTemperature = maxTemperature;
        mMinTemperature = minTemperature;
        mSun = sun;
    }

    public String getWeatherName() {
        return "Swa Daily Forecasts Weather";
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
            mDayWeatherText = WeatherUtils.getSwaWeatherTextById(context, mDayWeatherId);
        }
        return mDayWeatherText;
    }

    public int getNightWeatherId() {
        return mNightWeatherId;
    }

    public String getNightWeatherText(Context context) {
        if (mNightWeatherText == null) {
            mNightWeatherText = WeatherUtils.getSwaWeatherTextById(context, mNightWeatherId);
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
