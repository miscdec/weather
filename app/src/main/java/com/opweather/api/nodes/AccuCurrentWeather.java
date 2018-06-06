package com.opweather.api.nodes;

import android.content.Context;

import com.opweather.api.helper.WeatherUtils;
import com.opweather.api.impl.AccuRequest;

import java.util.Date;

public class AccuCurrentWeather extends CurrentWeather {
    private String mLocalTimeZone;
    private final String mMainMoblieLink;
    private final Date mObservationDate;
    private final int mRelativeHumidity;
    private final Temperature mTemperature;
    private final int mUVIndex;
    private final String mUVIndexText;
    private final int mWeatherId;
    private String mWeatherText;
    private final Wind mWind;

    public AccuCurrentWeather(String areaCode, int weatherId, String localTimeZone, String weatherText, Date
            observationDate, Temperature temperature, int relativeHumidity, Wind wind, int UVIndex, String
            UVIndexText, String MainMoblieLink) {
        this(areaCode, null, weatherId, localTimeZone, weatherText, observationDate, temperature, relativeHumidity,
                wind, UVIndex, UVIndexText, MainMoblieLink);
    }

    public AccuCurrentWeather(String areaCode, String areaName, int weatherId, String localTimeZone, String
            weatherText, Date observationDate, Temperature temperature, int relativeHumidity, Wind wind, int UVIndex,
                              String UVIndexText, String MainMoblieLink) {
        super(areaCode, areaName, AccuRequest.DATA_SOURCE_NAME);
        mWeatherId = weatherId;
        mLocalTimeZone = localTimeZone;
        mWeatherText = weatherText;
        mObservationDate = observationDate;
        mTemperature = temperature;
        mRelativeHumidity = relativeHumidity;
        mWind = wind;
        mUVIndex = UVIndex;
        mUVIndexText = UVIndexText;
        mMainMoblieLink = MainMoblieLink;
    }

    public String getWeatherName() {
        return "Accu Current Weather";
    }

    public Date getObservationDate() {
        return mObservationDate;
    }

    public int getWeatherId() {
        return mWeatherId;
    }

    public String getLocalTimeZone() {
        return mLocalTimeZone;
    }

    public String getWeatherText(Context context) {
        return WeatherUtils.getWeatherTextByWeatherId(context, mWeatherId);
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
        return mMainMoblieLink;
    }
}
