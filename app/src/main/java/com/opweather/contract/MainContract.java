package com.opweather.contract;

import android.content.Context;
import com.opweather.base.IBasePresenter;
import com.opweather.base.IBaseView;
import com.opweather.bean.CityWeather;
import com.opweather.bean.WeatherData1;

import io.reactivex.Observable;

/**
 * Created by lyh on 3/12.
 */

public interface MainContract {
    interface Model {
        Observable<CityWeather> getCityWeatherData();
    }

    interface View extends IBaseView {

       void showCityWeatherData(WeatherData1 cityWeather);

    }

    interface Presenter extends IBasePresenter<View>{

        void getCityWeatherData(Context context);

    }
}
