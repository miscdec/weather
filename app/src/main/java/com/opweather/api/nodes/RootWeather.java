package com.opweather.api.nodes;

import android.content.Context;
import android.util.Log;

import com.opweather.api.WeatherRequest;
import com.opweather.api.helper.DateUtils;
import com.opweather.api.helper.LogUtils;
import com.opweather.api.helper.WeatherUtils;
import com.opweather.bean.HourForecastsWeather;
import com.opweather.api.cache.Cache;

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

    public String getCurrentWeatherText(Context context) {
        return getCurrentWeather() != null ? getCurrentWeather().getWeatherText(context) : null;
    }

    public int getTodayCurrentTemp(RootWeather weather) {
        return (weather.getCurrentWeather() == null || weather.getCurrentWeather().getTemperature() == null) ?
                Integer.MIN_VALUE : (int) Math.floor(weather.getCurrentWeather()
                .getTemperature().getCentigradeValue());
    }

    public DailyForecastsWeather getTodayForecast(RootWeather weather) {
        return DailyForecastsWeather.getTodayForecast(weather.getDailyForecastsWeather(), DateUtils.getTimeZone
                (weather.getCurrentWeather().getLocalTimeZone()));
    }

    public int getTodayLowTemperature(RootWeather weather) {
        DailyForecastsWeather today = getTodayForecast(weather);
        return (today == null || today.getMinTemperature() == null) ? Integer.MIN_VALUE :
                (int) Math.floor(today.getMinTemperature().getCentigradeValue());
    }

    public int getTodayHighTemperature(RootWeather weather) {
        DailyForecastsWeather today = getTodayForecast(weather);
        return (today == null || today.getMaxTemperature() == null) ? Integer.MIN_VALUE : (int) Math.floor(today
                .getMaxTemperature().getCentigradeValue());
    }

    public int getTodayCurrentTemp() {
        return getTodayCurrentTemp(this);
    }

    public int getTodayHighTemperature() {
        return getTodayHighTemperature(this);
    }

    public int getTodayLowTemperature() {
        return getTodayLowTemperature(this);
    }

    public DailyForecastsWeather getTodayForecast() {
        return WeatherUtils.getTodayForecast(this);
    }

    public String getWeatherName() {
        return "Head Weather";
    }

    public Date getDate() {
        return mDate;
    }

    public void setDate(Date date) {
        mDate = date;
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

    public String getFutureLink() {
        return mFutureLink;
    }

    public void setFutureLink(String futureLink) {
        mFutureLink = futureLink;
    }

    public void setWeatherAlarms(List<Alarm> weatherAlarms) {
        mWeatherAlarms = WeatherUtils.getAlarmsRes(weatherAlarms);
    }

    public boolean getRequestIsSuccess() {
        return this.mSuccess;
    }

    public boolean setRequestIsSuccess(boolean isSuccess) {
        mSuccess = isSuccess;
        return isSuccess;
    }

    public void writeMemoryCache(WeatherRequest request, Cache cache) {
        String key = getKeyForMemory(request);
        RootWeather weather = cache.getFromMemCache(key);
        if (weather == null) {
            Log.d(TAG, "Write weather entity to memory, key = " + key);
            cache.putToMemory(key, this);
            return;
        }
        boolean z = false;
        if (mAqiWeather != null) {
            weather.setAqiWeather(mAqiWeather);
            z = true;
        }
        if (mLifeIndexWeather != null) {
            weather.setLifeIndexWeather(mLifeIndexWeather);
            z = true;
        }
        if (mCurrentWeather != null) {
            weather.setCurrentWeather(mCurrentWeather);
            z = true;
        }
        if (mHourForecastsWeather != null) {
            weather.setHourForecastsWeather(mHourForecastsWeather);
            z = true;
        }
        if (this.mDailyForecastsWeather != null) {
            weather.setDailyForecastsWeather(mDailyForecastsWeather);
            z = true;
        }
        if (mFutureLink != null) {
            weather.setFutureLink(mFutureLink);
            z = true;
        }
        if (mWeatherAlarms != null) {
            weather.setWeatherAlarms(mWeatherAlarms);
            z = true;
        }
        if (mDate != null) {
            weather.setDate(mDate);
            z = true;
        }
        if (z) {
            LogUtils.d(TAG, "Modify weather entity in memory, key = " + key, new Object[0]);
            cache.putToMemory(key, weather);
        }
    }

    private String getKeyForMemory(WeatherRequest request) {
        return request.getMemCacheKey();
    }
}
