package com.opweather.api.nodes;

import android.content.Context;

import com.opweather.api.helper.WeatherUtils;

import java.util.Date;

public class OppoForeignCurrentWeather extends CurrentWeather {
    private final Date mObservationDate;
    private final int mRelativeHumidity;
    private final Temperature mTemperature;
    private final int mUVIndex;
    private final String mUVIndexText;
    private final int mWeatherId;
    private String mWeatherText;
    private final Wind mWind;

    public OppoForeignCurrentWeather(String areaCode, String dataSource, int weatherId, Date observationDate,
                                     Temperature temperature, int relativeHumidity, Wind wind, int UVIndex, String
                                             UVIndexText) {
        this(areaCode, null, dataSource, weatherId, observationDate, temperature, relativeHumidity, wind, UVIndex,
                UVIndexText);
    }

    public OppoForeignCurrentWeather(String areaCode, String areaName, String dataSource, int weatherId, Date
            observationDate, Temperature temperature, int relativeHumidity, Wind wind, int UVIndex, String
            UVIndexText) {
        super(areaCode, areaName, dataSource);
        mWeatherId = weatherId;
        mObservationDate = observationDate;
        mTemperature = temperature;
        mRelativeHumidity = relativeHumidity;
        mWind = wind;
        mUVIndex = UVIndex;
        mUVIndexText = UVIndexText;
    }

    public String getWeatherName() {
        return "Oppo Foreign Current Weather";
    }

    public int getWeatherId() {
        return mWeatherId;
    }

    public String getLocalTimeZone() {
        return null;
    }

    public Date getObservationDate() {
        return mObservationDate;
    }

    public String getWeatherText(Context context) {
        if (mWeatherText == null) {
            mWeatherText = WeatherUtils.getOppoForeignWeatherTextById(context, mWeatherId);
        }
        return mWeatherText;
    }

    public Temperature getTemperature() {
        return mTemperature;
    }

    public int getRelativeHumidity() {
        return mRelativeHumidity;
    }

    public Wind getWind() {
        return mWind;
    }

    public int getUVIndex() {
        return mUVIndex;
    }

    public String getUVIndexText() {
        return mUVIndexText;
    }

    public String getMainMoblieLink() {
        return null;
    }
}
