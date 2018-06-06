package com.opweather.api.nodes;

import android.content.Context;

import com.opweather.api.helper.WeatherUtils;
import com.opweather.api.impl.SwaRequest;

import java.util.Date;

public class SwaCurrentWeather extends CurrentWeather {
    private final Date mObservationDate;
    private final int mRelativeHumidity;
    private final Temperature mTemperature;
    private final int mUVIndex;
    private final String mUVIndexText;
    private final int mWeatherId;
    private String mWeatherText;
    private final Wind mWind;

    public SwaCurrentWeather(String areaCode, int weatherId, Date observationDate, Temperature temperature, int
            relativeHumidity, Wind wind, int UVIndex, String UVIndexText) {
        super(areaCode, null, SwaRequest.DATA_SOURCE_NAME);
        this.mWeatherId = weatherId;
        this.mObservationDate = observationDate;
        this.mTemperature = temperature;
        this.mRelativeHumidity = relativeHumidity;
        this.mWind = wind;
        this.mUVIndex = UVIndex;
        this.mUVIndexText = UVIndexText;
    }

    public String getWeatherName() {
        return "Swa Current Weather";
    }

    public Date getObservationDate() {
        return this.mObservationDate;
    }

    public int getWeatherId() {
        return this.mWeatherId;
    }

    public String getLocalTimeZone() {
        return null;
    }

    public String getWeatherText(Context context) {
        if (this.mWeatherText == null) {
            this.mWeatherText = WeatherUtils.getSwaWeatherTextById(context, this.mWeatherId);
        }
        return this.mWeatherText;
    }

    public Temperature getTemperature() {
        return this.mTemperature;
    }

    public int getRelativeHumidity() {
        return this.mRelativeHumidity;
    }

    public Wind getWind() {
        return this.mWind;
    }

    public int getUVIndex() {
        return this.mUVIndex;
    }

    public String getUVIndexText() {
        return this.mUVIndexText;
    }

    public String getMainMoblieLink() {
        return null;
    }
}
