package com.opweather.opapi;

import android.content.Context;

import java.util.Date;

public abstract class CurrentWeather extends AbstractWeather {
    public abstract String getLocalTimeZone();

    public abstract String getMainMoblieLink();

    public abstract Date getObservationDate();

    public abstract int getRelativeHumidity();

    public abstract Temperature getTemperature();

    public abstract int getUVIndex();

    public abstract String getUVIndexText();

    public abstract int getWeatherId();

    public abstract String getWeatherText(Context context);

    public abstract Wind getWind();

    public CurrentWeather(String areaCode, String areaName, String dataSource) {
        super(areaCode, areaName, dataSource);
    }

    public String getWeatherName() {
        return "Current Conditions";
    }
}

