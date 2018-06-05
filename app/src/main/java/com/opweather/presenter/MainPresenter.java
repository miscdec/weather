package com.opweather.presenter;

import android.content.Context;
import android.util.Log;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.Volley;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.opweather.api.GZipRequest;
import com.opweather.api.RetrofitClient;
import com.opweather.api.helper.DateUtils;
import com.opweather.api.helper.NetworkHelper;
import com.opweather.api.impl.WeatherRequestExecuter;
import com.opweather.base.BasePresenter;
import com.opweather.bean.CityWeather;
import com.opweather.bean.WeatherData;
import com.opweather.bean.WeatherData1;
import com.opweather.contract.MainContract;
import com.opweather.util.DateTimeUtils;
import com.opweather.util.WeatherClientProxy;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Date;



public class MainPresenter extends BasePresenter<MainContract.View> implements MainContract
        .Presenter {


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
       /* new WeatherClientProxy(context).setCacheMode(C).requestWeatherInfo(ConnectionResult
       .INTERRUPTED, city, new
                AnonymousClass_2(context, city, isCheckAlarm));*/
        RequestQueue requestQueue = Volley.newRequestQueue(context.getApplicationContext());
        String nowTimeHours = DateUtils.formatOppoRequestv3DateText(new Date());
        String url = "http://i1.weather.oppomobile.com/chinaWeather/smChinaWeathersGz/" +
                nowTimeHours + "/101281701-" + nowTimeHours + ".json.gz";

        GZipRequest request = new GZipRequest(url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.d("1111", "response:" + response);

                WeatherData1 weatherData = new Gson().fromJson(response, WeatherData1.class);
                mView.showCityWeatherData(weatherData);


            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.d("1111", "error:" + error);
            }
        });
        requestQueue.add(request);
    }

    @Override
    public void attachView(MainContract.View mRootView) {

    }

    @Override
    public void detachView() {

    }

}
