package com.opweather.api.nodes;

import android.content.Context;

import com.opweather.api.helper.WeatherUtils;

import java.util.Date;

public class AccuDailyForecastsWeather extends DailyForecastsWeather {
    private final Date mDate;
    private final int mDayWeatherId;
    private String mDayWeatherText;
    private final Temperature mMaxTemperature;
    private final Temperature mMinTemperature;
    private final String mMobileLink;
    private final int mNightWeatherId;
    private String mNightWeatherText;
    private final Sun mSun;

    public AccuDailyForecastsWeather(String areaCode, String dataSource, int dayWeatherId, String dayWeatherText, int
            nightWeatherId, String nightWeatherText, Date date, Temperature minTemperature, Temperature
                                             maxTemperature, Sun sun, String mobileLink) {
        this(areaCode, null, dataSource, dayWeatherId, dayWeatherText, nightWeatherId, nightWeatherText, date,
                minTemperature, maxTemperature, sun, mobileLink);
    }

    public AccuDailyForecastsWeather(String areaCode, String areaName, String dataSource, int dayWeatherId, String
            dayWeatherText, int nightWeatherId, String nightWeatherText, Date date, Temperature minTemperature,
                                     Temperature maxTemperature, Sun sun, String mobileLink) {
        super(areaCode, areaName, dataSource);
        mDayWeatherId = dayWeatherId;
        mDayWeatherText = dayWeatherText;
        mNightWeatherId = nightWeatherId;
        mNightWeatherText = nightWeatherText;
        mDate = date;
        mMinTemperature = minTemperature;
        mMaxTemperature = maxTemperature;
        mSun = sun;
        mMobileLink = mobileLink;
    }

    public String getWeatherName() {
        return "Accu Daily Forecasts Weather";
    }

    public Date getDate() {
        return mDate;
    }

    public int getDayWeatherId() {
        return mDayWeatherId;
    }

    public String getMobileLink() {
        return mMobileLink;
    }

    public String getDayWeatherText(Context context) {
        if (mDayWeatherText == null) {
            mDayWeatherText = WeatherUtils.getAccuWeatherTextById(context, mDayWeatherId);
        }
        return mDayWeatherText;
    }

    public int getNightWeatherId() {
        return mNightWeatherId;
    }

    public String getNightWeatherText(Context context) {
        if (mNightWeatherText == null) {
            mNightWeatherText = WeatherUtils.getAccuWeatherTextById(context, mNightWeatherId);
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
