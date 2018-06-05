package com.opweather.provider.apihelper;

import android.content.Context;

import com.opweather.bean.CityData;

public interface IWeatherAPIHelper {
    void getWeatherAPIResponse(Context context, CityData cityData, int i);
}
