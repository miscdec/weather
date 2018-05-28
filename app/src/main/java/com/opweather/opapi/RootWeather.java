package com.opweather.opapi;

import com.opweather.bean.HourForecastsWeather;

import java.util.Date;
import java.util.List;

public class RootWeather extends AbstractWeather {

    private static final String TAG = "RootWeather";
    private AqiWeather mAqiWeather;
    private CurrentWeather mCurrentWeather;
    private List<DailyForecastsWeather> mDailyForecastsWeather;
    private Date mDate;
    private String mFutureLink;
    private List<HourForecastsWeather> mHourForecastsWeather;
    private LifeIndexWeather mLifeIndexWeather;
    private boolean mSuccess;
    private List<Alarm> mWeatherAlarms;

    public RootWeather(String areaCode, String dataSource) {
        this(areaCode, null, dataSource);
    }

    public RootWeather(String areaCode, String areaName, String dataSource) {
        super(areaCode, areaName, dataSource);
        mSuccess = true;
        mDate = new Date();
    }

    public DailyForecastsWeather getTodayForecast() {
        return WeatherUtils.getTodayForecast(this);
    }

    public String getWeatherName() {
        return "Head Weather";
    }

    public Date getDate() {
        return this.mDate;
    }

    public AqiWeather getAqiWeather() {
        return this.mAqiWeather;
    }

    public LifeIndexWeather getLifeIndexWeather() {
        return this.mLifeIndexWeather;
    }

    public CurrentWeather getCurrentWeather() {
        return this.mCurrentWeather;
    }

    public List<HourForecastsWeather> getHourForecastsWeather() {
        return this.mHourForecastsWeather;
    }

    public List<DailyForecastsWeather> getDailyForecastsWeather() {
        return this.mDailyForecastsWeather;
    }

    public List<Alarm> getWeatherAlarms() {
        return this.mWeatherAlarms;
    }

    public void setAqiWeather(AqiWeather weather) {
        this.mAqiWeather = weather;
    }

    public void setLifeIndexWeather(LifeIndexWeather lifeIndexWeather) {
        this.mLifeIndexWeather = lifeIndexWeather;
    }

    public void setCurrentWeather(CurrentWeather weather) {
        this.mCurrentWeather = weather;
    }

    public void setHourForecastsWeather(List<HourForecastsWeather> list) {
        this.mHourForecastsWeather = list;
    }

    public void setDailyForecastsWeather(List<DailyForecastsWeather> list) {
        this.mDailyForecastsWeather = list;
    }

    public int getCurrentWeatherId() {
        return getCurrentWeather() != null ? getCurrentWeather().getWeatherId() : 0;
    }

    public boolean isFromOppoChina() {
        return getDataSource().equals("Oppo.China");
    }

    public boolean isFromSwa() {
        return getDataSource().equals("HuaFeng");
    }

    public boolean isFromChina() {
        return isFromOppoChina() || isFromSwa();
    }

    public boolean getRequestIsSuccess() {
        return this.mSuccess;
    }

    public boolean setRequestIsSuccess(boolean isSuccess) {
        this.mSuccess = isSuccess;
        return isSuccess;
    }
}
