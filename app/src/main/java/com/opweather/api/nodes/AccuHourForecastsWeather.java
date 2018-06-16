package com.opweather.api.nodes;

import android.content.Context;

import java.util.Date;

public class AccuHourForecastsWeather extends HourForecastsWeather {
    public AccuHourForecastsWeather(String areaCode, String dataSource) {
        this(areaCode, null, dataSource);
    }

    public AccuHourForecastsWeather(String areaCode, String areaName, String dataSource) {
        super(areaCode, areaName, dataSource);
    }

    public String getWeatherName() {
        return "Accu Hour Forecasts Weather";
    }

    public Date getTime() {
        return null;
    }

    public int getWeatherId() {
        return 0;
    }

    public String getWeatherText(Context context) {
        return null;
    }

    public Temperature getTemperature() {
        return null;
    }
}
