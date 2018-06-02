package com.opweather.presenter;

import android.content.Context;
import android.util.Log;

import com.opweather.api.RetrofitClient;
import com.opweather.api.helper.NetworkHelper;
import com.opweather.api.impl.WeatherRequestExecuter;
import com.opweather.base.BasePresenter;
import com.opweather.bean.CityWeather;
import com.opweather.contract.MainContract;
import com.opweather.util.WeatherClientProxy;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;


/**
 * Created by lyh on 3/12.
 */

public class MainPresenter extends BasePresenter<MainContract.View> implements MainContract.Presenter {


    private MainContract.View mView;

    public MainPresenter(MainContract.View rootView) {
        super(rootView);
        mView = rootView;
    }

    @Override
    public void getCityWeatherData(Context context) {
        /*RetrofitClient.getRetrofitClientInstance().getCityWeatherData()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<CityWeather>() {
                    @Override
                    public void accept(CityWeather cityWeather) throws Exception {
                        Log.d("1111", "accept: " + cityWeather.toString());
                        mView.showCityWeatherData(cityWeather);
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {
                        Log.d("1111", "accept: " + throwable.getMessage());
                    }
                });*/
        new WeatherClientProxy(context).setCacheMode(C).requestWeatherInfo(ConnectionResult.INTERRUPTED, city, new
                AnonymousClass_2(context, city, isCheckAlarm));
    }

    @Override
    public void attachView(MainContract.View mRootView) {

    }

    @Override
    public void detachView() {

    }

}
