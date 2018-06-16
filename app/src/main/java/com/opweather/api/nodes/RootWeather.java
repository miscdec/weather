package com.opweather.api.nodes;

import android.content.Context;

import com.opweather.api.WeatherRequest;
import com.opweather.api.cache.Cache;
import com.opweather.api.helper.LogUtils;
import com.opweather.api.helper.WeatherUtils;
import com.opweather.api.impl.AccuRequest;
import com.opweather.api.impl.OppoChinaRequest;
import com.opweather.api.impl.SwaRequest;
import com.opweather.provider.CitySearchProvider;
import com.opweather.util.StringUtils;

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

    public String getWeatherName() {
        return "Head Weather";
    }

    public Date getDate() {
        return mDate;
    }

    public AqiWeather getAqiWeather() {
        return mAqiWeather;
    }

    public LifeIndexWeather getLifeIndexWeather() {
        return mLifeIndexWeather;
    }

    public CurrentWeather getCurrentWeather() {
        return mCurrentWeather;
    }

    public List<HourForecastsWeather> getHourForecastsWeather() {
        return mHourForecastsWeather;
    }

    public List<DailyForecastsWeather> getDailyForecastsWeather() {
        return mDailyForecastsWeather;
    }

    public List<Alarm> getWeatherAlarms() {
        return mWeatherAlarms;
    }

    public void setAqiWeather(AqiWeather weather) {
        mAqiWeather = weather;
    }

    public void setLifeIndexWeather(LifeIndexWeather lifeIndexWeather) {
        mLifeIndexWeather = lifeIndexWeather;
    }

    public void setCurrentWeather(CurrentWeather weather) {
        mCurrentWeather = weather;
    }

    public void setHourForecastsWeather(List<HourForecastsWeather> list) {
        mHourForecastsWeather = list;
    }

    public void setDailyForecastsWeather(List<DailyForecastsWeather> list) {
        mDailyForecastsWeather = list;
    }

    public void setWeatherAlarms(List<Alarm> list) {
        mWeatherAlarms = WeatherUtils.getAlarmsRes(list);
    }

    public void setDate(Date date) {
        mDate = date;
    }

    public String getFutureLink() {
        return mFutureLink;
    }

    public void setFutureLink(String futureLink) {
        mFutureLink = futureLink;
    }

    public void writeMemoryCache(WeatherRequest request, Cache cache) {
        String key = getKeyForMemory(request);
        RootWeather weather = cache.getFromMemCache(key);
        if (weather == null) {
            LogUtils.d(TAG, "Write weather entity to memory, key = " + key);
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
        if (mDailyForecastsWeather != null) {
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
            LogUtils.d(TAG, "Modify weather entity in memory, key = " + key);
            cache.putToMemory(key, weather);
        }
    }

    private String getKeyForMemory(WeatherRequest request) {
        return request.getMemCacheKey();
    }

    public int getTodayCurrentTemp() {
        return WeatherUtils.getTodayCurrentTemp(this);
    }

    public DailyForecastsWeather getTodayForecast() {
        return WeatherUtils.getTodayForecast(this);
    }

    public int getTodayHighTemperature() {
        return WeatherUtils.getTodayHighTemperature(this);
    }

    public int getTodayLowTemperature() {
        return WeatherUtils.getTodayLowTemperature(this);
    }

    public String getCurrentWeatherText(Context context) {
        return getCurrentWeather() != null ? getCurrentWeather().getWeatherText(context) : null;
    }

    public int getCurrentWeatherId() {
        return getCurrentWeather() != null ? getCurrentWeather().getWeatherId() : 0;
    }

    public String getCurrentWindDir(Context context) {
        CurrentWeather current = getCurrentWeather();
        return (current == null || current.getWind() == null || current.getWind().getDirection() == null) ? null :
                current.getWind().getDirection().text(context);
    }

    public String getCurrentWindPower(Context context) {
        CurrentWeather current = getCurrentWeather();
        if (current == null || current.getWind() == null) {
            return null;
        }
        Wind wind = current.getWind();
        return wind instanceof AccuWind ? Double.toString(wind.getSpeed()) : wind.getWindPower(context);
    }

    public int getAqiValue() {
        return getAqiWeather() != null ? getAqiWeather().getAqiValue() : -1;
    }

    public String getAqiType() {
        AqiWeather aqiWeather = getAqiWeather();
        return (aqiWeather == null || !(aqiWeather instanceof OppoChinaAqiWeather)) ? null : ((OppoChinaAqiWeather)
                aqiWeather).getAqiType();
    }

    public String getAqiTypeTransformSimlpe(Context mContext) {
        AqiWeather aqiWeather = getAqiWeather();
        return (aqiWeather == null || !(aqiWeather instanceof OppoChinaAqiWeather)) ? null : ((OppoChinaAqiWeather)
                aqiWeather).getAqiTypeTransformSimlpe(mContext);
    }

    public int getAqiPM25Value() {
        AqiWeather aqiWeather = getAqiWeather();
        return (aqiWeather == null || !(aqiWeather instanceof OppoChinaAqiWeather)) ? -1 : ((OppoChinaAqiWeather)
                aqiWeather).getPM25_Value();
    }

    public Temperature getTodayBodytempSimple() {
        if (getDataSourceName().equals(OppoChinaRequest.DATA_SOURCE_NAME)) {
            DailyForecastsWeather today = getTodayForecast();
            if (today != null && (today instanceof OppoChinaDailyForecastsWeather)) {
                return ((OppoChinaDailyForecastsWeather) today).getBodytemp();
            }
        }
        return null;
    }

    public int getTodayBodytemp() {
        try {
            if (getDataSourceName().equals(OppoChinaRequest.DATA_SOURCE_NAME)) {
                DailyForecastsWeather today = getTodayForecast();
                if (today != null && (today instanceof OppoChinaDailyForecastsWeather)) {
                    return (int) Math.floor(((OppoChinaDailyForecastsWeather) today).getBodytemp().getCentigradeValue
                            ());
                }
            }
        } catch (Exception e) {
        }
        return CitySearchProvider.GET_SEARCH_RESULT_FAIL;
    }

    public int getTodayPressureSimple() {
        if (getDataSourceName().equals(OppoChinaRequest.DATA_SOURCE_NAME)) {
            DailyForecastsWeather today = getTodayForecast();
            if (today != null && (today instanceof OppoChinaDailyForecastsWeather)) {
                return ((OppoChinaDailyForecastsWeather) today).getPressure();
            }
        }
        return -1;
    }

    public String getTodayPressureTransformSimpleValue(Context mContext) {
        if (getDataSourceName().equals(OppoChinaRequest.DATA_SOURCE_NAME)) {
            DailyForecastsWeather today = getTodayForecast();
            if (today != null && (today instanceof OppoChinaDailyForecastsWeather)) {
                return ((OppoChinaDailyForecastsWeather) today).getPressureTransformSimlpeValue(mContext);
            }
        }
        return null;
    }

    public int getTodayVisibilitySimple() {
        if (getDataSourceName().equals(OppoChinaRequest.DATA_SOURCE_NAME)) {
            DailyForecastsWeather today = getTodayForecast();
            if (today != null && (today instanceof OppoChinaDailyForecastsWeather)) {
                return ((OppoChinaDailyForecastsWeather) today).getVisibility();
            }
        }
        return -1;
    }

    public String getTodayVisibilityTransformSimpleValue(Context mContext) {
        if (getDataSourceName().equals(OppoChinaRequest.DATA_SOURCE_NAME)) {
            DailyForecastsWeather today = getTodayForecast();
            if (today != null && (today instanceof OppoChinaDailyForecastsWeather)) {
                return ((OppoChinaDailyForecastsWeather) today).getVisibilityTransformSimlpeValue(mContext);
            }
        }
        return null;
    }

    public String getTodaySportsSimple() {
        String sports = StringUtils.EMPTY_STRING;
        if (getDataSourceName().equals(OppoChinaRequest.DATA_SOURCE_NAME)) {
            DailyForecastsWeather today = getTodayForecast();
            return (today == null || !(today instanceof OppoChinaDailyForecastsWeather)) ? sports :
                    OppoChinaDailyForecastsWeather.toSimple(((OppoChinaDailyForecastsWeather) today).getSportsValue());
        } else if (!getDataSourceName().equals(SwaRequest.DATA_SOURCE_NAME)) {
            return sports;
        } else {
            LifeIndexWeather lifeIndexWeather = getLifeIndexWeather();
            return (lifeIndexWeather == null || !(lifeIndexWeather instanceof SwaLifeIndexWeather)) ? sports : (
                    (SwaLifeIndexWeather) lifeIndexWeather).getSportsIndexText(StringUtils.EMPTY_STRING);
        }
    }

    public String getTodaySportsTransformSimlpeValue(Context context) {
        String sports = StringUtils.EMPTY_STRING;
        if (getDataSourceName().equals(OppoChinaRequest.DATA_SOURCE_NAME)) {
            DailyForecastsWeather today = getTodayForecast();
            return (today == null || !(today instanceof OppoChinaDailyForecastsWeather)) ? sports : (
                    (OppoChinaDailyForecastsWeather) today).getSportsTransformSimlpeValue(context);
        } else if (!getDataSourceName().equals(SwaRequest.DATA_SOURCE_NAME)) {
            return sports;
        } else {
            LifeIndexWeather lifeIndexWeather = getLifeIndexWeather();
            return (lifeIndexWeather == null || !(lifeIndexWeather instanceof SwaLifeIndexWeather)) ? sports : (
                    (SwaLifeIndexWeather) lifeIndexWeather).getSportsIndexInternationalText(context, StringUtils
                    .EMPTY_STRING);
        }
    }

    public String getTodayWashCarSimple() {
        String wash = StringUtils.EMPTY_STRING;
        if (getDataSourceName().equals(OppoChinaRequest.DATA_SOURCE_NAME)) {
            DailyForecastsWeather today = getTodayForecast();
            return (today == null || !(today instanceof OppoChinaDailyForecastsWeather)) ? wash :
                    OppoChinaDailyForecastsWeather.toSimple(((OppoChinaDailyForecastsWeather) today).getWashcarValue());
        } else if (!getDataSourceName().equals(SwaRequest.DATA_SOURCE_NAME)) {
            return wash;
        } else {
            LifeIndexWeather lifeIndexWeather = getLifeIndexWeather();
            return (lifeIndexWeather == null || !(lifeIndexWeather instanceof SwaLifeIndexWeather)) ? wash : (
                    (SwaLifeIndexWeather) lifeIndexWeather).getCarwashIndexText(StringUtils.EMPTY_STRING);
        }
    }

    public String getTodayCarwashTransformSimlpeValue(Context context) {
        String wash = StringUtils.EMPTY_STRING;
        if (getDataSourceName().equals(OppoChinaRequest.DATA_SOURCE_NAME)) {
            DailyForecastsWeather today = getTodayForecast();
            return (today == null || !(today instanceof OppoChinaDailyForecastsWeather)) ? wash : (
                    (OppoChinaDailyForecastsWeather) today).getCarwashTransformSimlpeValue(context);
        } else if (!getDataSourceName().equals(SwaRequest.DATA_SOURCE_NAME)) {
            return wash;
        } else {
            LifeIndexWeather lifeIndexWeather = getLifeIndexWeather();
            return (lifeIndexWeather == null || !(lifeIndexWeather instanceof SwaLifeIndexWeather)) ? wash : (
                    (SwaLifeIndexWeather) lifeIndexWeather).getCarwashIndexInternationalText(context, StringUtils
                    .EMPTY_STRING);
        }
    }

    public String getTodayClothingSimple() {
        String cloth = StringUtils.EMPTY_STRING;
        if (getDataSourceName().equals(OppoChinaRequest.DATA_SOURCE_NAME)) {
            DailyForecastsWeather today = getTodayForecast();
            return (today == null || !(today instanceof OppoChinaDailyForecastsWeather)) ? cloth :
                    OppoChinaDailyForecastsWeather.toSimple(((OppoChinaDailyForecastsWeather) today).getClothingValue
                            ());
        } else if (!getDataSourceName().equals(SwaRequest.DATA_SOURCE_NAME)) {
            return cloth;
        } else {
            LifeIndexWeather lifeIndexWeather = getLifeIndexWeather();
            return (lifeIndexWeather == null || !(lifeIndexWeather instanceof SwaLifeIndexWeather)) ? cloth : (
                    (SwaLifeIndexWeather) lifeIndexWeather).getClothingIndexText(StringUtils.EMPTY_STRING);
        }
    }

    public String getTodayClothingTransformSimlpeValue(Context context) {
        String cloth = StringUtils.EMPTY_STRING;
        if (getDataSourceName().equals(OppoChinaRequest.DATA_SOURCE_NAME)) {
            DailyForecastsWeather today = getTodayForecast();
            return (today == null || !(today instanceof OppoChinaDailyForecastsWeather)) ? cloth : (
                    (OppoChinaDailyForecastsWeather) today).getClothingTransformSimlpeValue(context);
        } else if (!getDataSourceName().equals(SwaRequest.DATA_SOURCE_NAME)) {
            return cloth;
        } else {
            LifeIndexWeather lifeIndexWeather = getLifeIndexWeather();
            return (lifeIndexWeather == null || !(lifeIndexWeather instanceof SwaLifeIndexWeather)) ? cloth : (
                    (SwaLifeIndexWeather) lifeIndexWeather).getClothingIndexInternationalText(context, StringUtils
                    .EMPTY_STRING);
        }
    }

    public String getUvTextTransformSimlpeValue(Context context) {
        String uv = StringUtils.EMPTY_STRING;
        CurrentWeather currentWeather;
        if (getDataSourceName().equals(OppoChinaRequest.DATA_SOURCE_NAME)) {
            currentWeather = getCurrentWeather();
            return (currentWeather == null || !(currentWeather instanceof OppoChinaCurrentWeather)) ? uv : (
                    (OppoChinaCurrentWeather) currentWeather).getUVIndexInternationalText(context);
        } else if (getDataSourceName().equals(SwaRequest.DATA_SOURCE_NAME)) {
            LifeIndexWeather lifeIndexWeather = getLifeIndexWeather();
            return (lifeIndexWeather == null || !(lifeIndexWeather instanceof SwaLifeIndexWeather)) ? uv : (
                    (SwaLifeIndexWeather) lifeIndexWeather).getUVIndexInternationalText(context, StringUtils
                    .EMPTY_STRING);
        } else {
            currentWeather = getCurrentWeather();
            return currentWeather != null ? currentWeather.getUVIndexText() : uv;
        }
    }

    public boolean isFromAccu() {
        return getDataSourceName().equals(AccuRequest.DATA_SOURCE_NAME);
    }

    public boolean isFromOppoChina() {
        return getDataSourceName().equals(OppoChinaRequest.DATA_SOURCE_NAME);
    }

    public boolean isFromSwa() {
        return getDataSourceName().equals(SwaRequest.DATA_SOURCE_NAME);
    }

    public boolean isFromChina() {
        return isFromOppoChina() || isFromSwa();
    }

    public boolean getRequestIsSuccess() {
        return mSuccess;
    }

    public boolean setRequestIsSuccess(boolean isSuccess) {
        mSuccess = isSuccess;
        return isSuccess;
    }
}
