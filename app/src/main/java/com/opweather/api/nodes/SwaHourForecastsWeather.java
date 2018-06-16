package com.opweather.api.nodes;

import android.content.Context;

import com.opweather.api.helper.DateUtils;
import com.opweather.api.helper.NumberUtils;
import com.opweather.api.helper.WeatherUtils;
import com.opweather.api.impl.SwaRequest;

import java.util.Date;

public class SwaHourForecastsWeather extends HourForecastsWeather {
    private final Temperature mTemperature;
    private final Date mTime;
    private final int mWeatherId;
    private String mWeatherText;

    public SwaHourForecastsWeather(String areaCode, int weatherId, Date time, Temperature temperature) {
        this(areaCode, null, SwaRequest.DATA_SOURCE_NAME, weatherId, time, temperature);
    }

    public SwaHourForecastsWeather(String areaCode, String areaName, String dataSource, int weatherId, Date time,
                                   Temperature temperature) {
        super(areaCode, areaName, dataSource);
        mWeatherId = weatherId;
        mTime = time;
        mTemperature = temperature;
    }

    public String getWeatherName() {
        return "Huafeng Hour Forecasts Weather";
    }

    public Date getTime() {
        return mTime;
    }

    public int getWeatherId() {
        return mWeatherId;
    }

    public String getWeatherText(Context context) {
        if (mWeatherText == null) {
            mWeatherText = WeatherUtils.getSwaWeatherTextById(context, mWeatherId);
        }
        return mWeatherText;
    }

    public Temperature getTemperature() {
        return mTemperature;
    }

    public static final SwaHourForecastsWeather buildFromString(String areaCode, String weatherId, String time,
                                                                String temperature) {
        int iWeatherId = WeatherUtils.swaWeatherIdToWeatherId(weatherId);
        Temperature iTemperature = null;
        double iCentigradeValue = NumberUtils.valueToDouble(temperature);
        double iFahrenheitValue = WeatherUtils.centigradeToFahrenheit(iCentigradeValue);
        if (!NumberUtils.isNaN(iCentigradeValue)) {
            iTemperature = new Temperature(areaCode, SwaRequest.DATA_SOURCE_NAME, iCentigradeValue, iFahrenheitValue);
        }
        return new SwaHourForecastsWeather(areaCode, iWeatherId, DateUtils.parseSwaAqiDate(time), iTemperature);
    }
}
