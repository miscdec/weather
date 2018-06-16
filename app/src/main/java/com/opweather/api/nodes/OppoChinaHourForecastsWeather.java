package com.opweather.api.nodes;

import android.content.Context;

import com.opweather.api.helper.WeatherUtils;
import com.opweather.api.impl.OppoChinaRequest;
import com.opweather.bean.HourForecastsWeather;


import java.util.Date;

public class OppoChinaHourForecastsWeather extends HourForecastsWeather {
    private final Temperature mTemperature;
    private final Date mTime;
    private final int mWeatherId;
    private String mWeatherText;

    public OppoChinaHourForecastsWeather(String areaCode, int weatherId, Date time, Temperature temperature) {
        this(areaCode, null, OppoChinaRequest.DATA_SOURCE_NAME, weatherId, time, temperature);
    }

    public OppoChinaHourForecastsWeather(String areaCode, String areaName, String dataSource, int weatherId, Date time, Temperature temperature) {
        super(areaCode, areaName, dataSource);
        this.mWeatherId = weatherId;
        this.mTime = time;
        this.mTemperature = temperature;
    }

    public String getWeatherName() {
        return "Oppo China Hour Forecasts Weather";
    }

    public Date getTime() {
        return this.mTime;
    }

    public int getWeatherId() {
        return this.mWeatherId;
    }

    public String getWeatherText(Context context) {
        if (this.mWeatherText == null) {
            this.mWeatherText = WeatherUtils.getOppoChinaWeatherTextById(context, this.mWeatherId);
        }
        return this.mWeatherText;
    }

    public Temperature getTemperature() {
        return this.mTemperature;
    }

    public static final OppoChinaHourForecastsWeather buildFromString(String areaCode, String weatherId, String time, String temperature) {
        return null;
    }
}
