package com.opweather.api;

import com.opweather.bean.CityWeather;

import io.reactivex.Observable;
import retrofit2.http.GET;


/**
 * Created by lyh on 3/12.
 */

public interface ApiService {

    @GET("forecast?location=CN101010100&key=2a814ee447aa412496cd865520cf07c9")
    Observable<CityWeather> getCityWeatherData();

}
