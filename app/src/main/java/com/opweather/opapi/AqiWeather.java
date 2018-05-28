package com.opweather.opapi;

public abstract class AqiWeather extends AbstractWeather {
    public abstract int getAqiValue();

    public AqiWeather(String areaCode, String areaName, String dataSource) {
        super(areaCode, areaName, dataSource);
    }

    public String getWeatherName() {
        return "Air Quality";
    }
}
