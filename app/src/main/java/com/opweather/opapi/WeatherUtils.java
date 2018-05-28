package com.opweather.opapi;

public class WeatherUtils {
    public static DailyForecastsWeather getTodayForecast(RootWeather weather) {
        return DailyForecastsWeather.getTodayForecast(weather.getDailyForecastsWeather(), DateUtils.getTimeZone(weather.getCurrentWeather().getLocalTimeZone()));
    }
}
