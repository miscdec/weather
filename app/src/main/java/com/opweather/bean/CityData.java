package com.opweather.bean;

import com.opweather.api.helper.DateUtils;
import com.opweather.opapi.DailyForecastsWeather;
import com.opweather.opapi.RootWeather;
import com.opweather.opapi.Sun;
import com.opweather.util.DateTimeUtils;
import com.opweather.util.StringUtils;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class CityData {
    private String administrativeName;
    private String countryName;
    private long id;
    private boolean isDefault;
    private boolean isLocated;
    private String lastRefreshTime;
    private double latitude;
    private String localName;
    private long locationDataRequestedTimestamp;
    private String locationId;
    private double longitude;
    private String name;
    private int provider;
    private RootWeather mWeathers;

    public CityData() {
        this.name = StringUtils.EMPTY_STRING;
        this.localName = StringUtils.EMPTY_STRING;
        this.latitude = 0.0d;
        this.longitude = 0.0d;
        this.locationId = StringUtils.EMPTY_STRING;
        this.lastRefreshTime = StringUtils.EMPTY_STRING;
        this.provider = -1;
        this.countryName = StringUtils.EMPTY_STRING;
        this.administrativeName = StringUtils.EMPTY_STRING;
        this.locationDataRequestedTimestamp = 0;
    }

    public CityData(String name, String localName, double latitude, double longitude, String locationId, String refreshTime) {
        this.name = StringUtils.EMPTY_STRING;
        this.localName = StringUtils.EMPTY_STRING;
        this.latitude = 0.0d;
        this.longitude = 0.0d;
        this.locationId = StringUtils.EMPTY_STRING;
        this.lastRefreshTime = StringUtils.EMPTY_STRING;
        this.provider = -1;
        this.countryName = StringUtils.EMPTY_STRING;
        this.administrativeName = StringUtils.EMPTY_STRING;
        this.locationDataRequestedTimestamp = 0;
        this.name = name;
        this.localName = localName;
        this.latitude = latitude;
        this.locationId = locationId;
        this.lastRefreshTime = refreshTime;
        this.longitude = longitude;
    }

    public String getAdministrativeName() {
        return administrativeName;
    }

    public void setAdministrativeName(String administrativeName) {
        this.administrativeName = administrativeName;
    }

    public String getCountryName() {
        return countryName;
    }

    public void setCountryName(String countryName) {
        this.countryName = countryName;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public boolean isDefault() {
        return isDefault;
    }

    public void setDefault(boolean aDefault) {
        isDefault = aDefault;
    }

    public boolean isLocated() {
        return isLocated;
    }

    public void setLocated(boolean located) {
        isLocated = located;
    }

    public String getLastRefreshTime() {
        return lastRefreshTime;
    }

    public void setLastRefreshTime(String lastRefreshTime) {
        this.lastRefreshTime = lastRefreshTime;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public String getLocalName() {
        return localName;
    }

    public void setLocalName(String localName) {
        this.localName = localName;
    }

    public long getLocationDataRequestedTimestamp() {
        return locationDataRequestedTimestamp;
    }

    public void setLocationDataRequestedTimestamp(long locationDataRequestedTimestamp) {
        this.locationDataRequestedTimestamp = locationDataRequestedTimestamp;
    }

    public String getLocationId() {
        return locationId;
    }

    public void setLocationId(String locationId) {
        this.locationId = locationId;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getProvider() {
        return provider;
    }

    public void setProvider(int provider) {
        this.provider = provider;
    }

    public RootWeather getWeathers() {
        return this.mWeathers;
    }

    public void setWeathers(RootWeather weathers) {
        mWeathers = weathers;
    }

    public boolean isDay(RootWeather weather) throws IllegalAccessException {
        if (weather == null || weather.getCurrentWeather() == null) {
            return false;
        }
        String timeZone = weather.getCurrentWeather().getLocalTimeZone();
        SimpleDateFormat formatter = new SimpleDateFormat("HH:mm:ss", Locale.US);
        DailyForecastsWeather today = weather.getTodayForecast();
        if (today == null) {
            return DateTimeUtils.isTimeMillisDayTime(System.currentTimeMillis(), timeZone);
        }
        Sun extra = today.getSun();
        if (extra == null) {
            return DateTimeUtils.isTimeMillisDayTime(System.currentTimeMillis(), timeZone);
        }
        if (DateTimeUtils.CHINA_OFFSET.equals(timeZone)) {
            formatter.setTimeZone(DateUtils.getTimeZone(DateTimeUtils.CHINA_OFFSET));
        }
        long sun = DateTimeUtils.stringToLong(formatter.format(extra.getRise()));
        long night = DateTimeUtils.stringToLong(formatter.format(extra.getSet()));
        long current = DateTimeUtils.stringToLong(formatter.format(new Date()));
        if (current < sun || current >= night || night <= sun) {
            return ((current >= sun && current > night) || (current <= sun && current < night)) && night < sun;
        } else {
            return true;
        }
    }
}
