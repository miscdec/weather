package com.opweather.bean;

import android.content.ContentValues;

import com.opweather.api.nodes.DailyForecastsWeather;
import com.opweather.api.nodes.RootWeather;
import com.opweather.api.nodes.Sun;
import com.opweather.db.CityWeatherDBHelper;
import com.opweather.db.CityWeatherDBHelper.CityListEntry;
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
    private RootWeather mWeathers;
    private String name;
    private int provider;

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

    public CityData(String name, String localName, double latitude, double longitude, String locationId, String
            refreshTime) {
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

    public void setId(long id) {
        this.id = id;
        if (id == 0) {
            setLocatedCity(true);
        } else {
            setLocatedCity(false);
        }
    }

    public long getId() {
        return this.id;
    }

    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getLocalName() {
        return this.localName;
    }

    public void setLocalName(String localName) {
        this.localName = localName;
    }

    public double getLatitude() {
        return this.latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return this.longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public String getLocationId() {
        return this.locationId;
    }

    public void setLocationId(String locationId) {
        this.locationId = locationId;
    }

    public String getLastRefreshTime() {
        return this.lastRefreshTime;
    }

    public void setLastRefreshTime(String lastRefreshTime) {
        this.lastRefreshTime = lastRefreshTime;
    }

    public void setProvider(int provider) {
        this.provider = provider;
    }

    public int getProvider() {
        return this.provider;
    }

    public void setCountryName(String countryName) {
        this.countryName = countryName;
    }

    public String getCountryName() {
        return this.countryName;
    }

    public void setAdministrativeName(String administrativeName) {
        this.administrativeName = administrativeName;
    }

    public String getAdministrativeName() {
        return this.administrativeName;
    }

    public void setLocationDataRequestedTimestamp(long time) {
        this.locationDataRequestedTimestamp = time;
    }

    public long getLocationDataRequestedTimestamp() {
        return this.locationDataRequestedTimestamp;
    }

    public void setLocatedCity(boolean isCurrent) {
        this.isLocated = isCurrent;
    }

    public boolean isLocatedCity() {
        return this.isLocated;
    }

    public RootWeather getWeathers() {
        return this.mWeathers;
    }

    public void setWeathers(RootWeather weathers) {
        this.mWeathers = weathers;
    }

    public String toString() {
        return "City [name=" + this.name + ", localized name=" + this.localName + ", latitude=" + this.latitude + ", " +
                "longitude=" + this.longitude + ", location key=" + this.locationId + "]";
    }

    public static CityData parse(ContentValues values) {
        if (values == null) {
            return null;
        }
        CityData city = new CityData();
        city.setId((long) values.getAsInteger("_id"));
        if (city.getId() != 0) {
            city.setLocatedCity(false);
        } else {
            city.setLocatedCity(true);
        }
        city.setProvider(values.getAsInteger(CityListEntry.COLUMN_1_PROVIDER));
        city.setName(values.getAsString(CityListEntry.COLUMN_2_NAME));
        city.setLocalName(values.getAsString(CityListEntry.COLUMN_3_DISPLAY_NAME));
        city.setLocationId(values.getAsString(CityWeatherDBHelper.WeatherEntry.COLUMN_1_LOCATION_ID));
        city.setLastRefreshTime(values.getAsString(CityListEntry.COLUMN_10_LAST_REFRESH_TIME));
        city.setDefault("-1".equals(values.getAsString(CityListEntry.COLUMN_9_DISPLAY_ORDER)));
        return city;
    }

    public void copy(CityData city) {
        setName(city.getName());
        setLocalName(city.getLocalName());
        setLongitude(city.getLongitude());
        setLatitude(city.getLatitude());
        setLocationId(city.getLocationId());
        setProvider(city.getProvider());
        setLastRefreshTime(city.getLastRefreshTime());
        setCountryName(city.getCountryName());
        setAdministrativeName(city.getAdministrativeName());
        setLocationDataRequestedTimestamp(getLocationDataRequestedTimestamp());
        setWeathers(city.getWeathers());
        setId(city.getId());
        setLocatedCity(city.isLocatedCity());
        setDefault(isDefault());
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
            formatter.setTimeZone(DateTimeUtils.getTimeZone(DateTimeUtils.CHINA_OFFSET));
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

    public boolean isDefault() {
        return this.isDefault;
    }

    public void setDefault(boolean aDefault) {
        this.isDefault = aDefault;
    }
}
