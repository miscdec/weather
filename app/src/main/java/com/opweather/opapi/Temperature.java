package com.opweather.opapi;

public class Temperature extends AbstractWeather{
    private final double mCentigradeValue;
    private final double mFahrenheitValue;

    public Temperature(String areaCode, String dataSource, double centigradeValue, double fahrenheitValue) {
        this(areaCode, null, dataSource, centigradeValue, fahrenheitValue);
    }

    public Temperature(String areaCode, String areaName, String dataSource, double centigradeValue, double fahrenheitValue) {
        super(areaCode, areaName, dataSource);
        mCentigradeValue = centigradeValue;
        mFahrenheitValue = fahrenheitValue;
    }

    public String getWeatherName() {
        return "Temperature";
    }

    public double getCentigradeValue() {
        return mCentigradeValue;
    }

    public double getFahrenheitValue() {
        return mFahrenheitValue;
    }
}
