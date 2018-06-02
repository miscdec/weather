package com.opweather.opapi;

import com.opweather.api.parser.ParseException;

public interface ResponseParser {

    interface WeatherBuilder {
        RootWeather build() throws BuilderException;
    }

    RootWeather parseAlarm(byte[] bArr) throws ParseException;

    RootWeather parseAqi(byte[] bArr) throws ParseException;

    RootWeather parseCurrent(byte[] bArr) throws ParseException;

    RootWeather parseDailyForecasts(byte[] bArr) throws ParseException;

    RootWeather parseHourForecasts(byte[] bArr) throws ParseException;

    RootWeather parseLifeIndex(byte[] bArr) throws ParseException;
}

