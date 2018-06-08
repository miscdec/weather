package com.opweather.model;

import com.opweather.api.ApiService;
import com.opweather.api.RetrofitClient;
import com.opweather.bean.CityWeather;
import com.opweather.contract.MainContract;

import io.reactivex.Observable;

public class MainModel implements MainContract.Model {

    private static  ApiService apiService;

    @Override
    public Observable<CityWeather> getCityWeatherData() {
//        return RetrofitClient.getRetrofitClientInstance().getCityWeatherData();
        return null;
    }
}
