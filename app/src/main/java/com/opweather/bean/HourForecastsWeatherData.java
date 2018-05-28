package com.opweather.bean;

public class HourForecastsWeatherData {
    private String hourText;
    private String temperature;
    private int weatherIconId;
    private int weatherId;

    public HourForecastsWeatherData(String hourText, int weatherId, int weatherIconId, String temperature) {
        this.hourText = hourText;
        this.temperature = temperature;
        this.weatherIconId = weatherIconId;
        this.weatherId = weatherId;
    }

    public String getHourText() {
        return hourText;
    }

    public void setHourText(String hourText) {
        this.hourText = hourText;
    }

    public String getTemperature() {
        return temperature;
    }

    public void setTemperature(String temperature) {
        this.temperature = temperature;
    }

    public int getWeatherIconId() {
        return weatherIconId;
    }

    public void setWeatherIconId(int weatherIconId) {
        this.weatherIconId = weatherIconId;
    }

    public int getWeatherId() {
        return weatherId;
    }

    public void setWeatherId(int weatherId) {
        this.weatherId = weatherId;
    }
}
