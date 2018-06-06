package com.opweather.ui;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.opweather.R;
import com.opweather.bean.CityWeather;
import com.opweather.bean.WeatherData1;
import com.opweather.contract.MainContract;
import com.opweather.api.nodes.RootWeather;
import com.opweather.presenter.MainPresenter;
import com.opweather.widget.HourForecastView;
import com.opweather.widget.RefreshWeatherUnitView;

/**
 * Created by leeyh on 3/14.
 */

public class MainFragment extends Fragment implements MainContract.View {

    private MainPresenter mPresenter;
    private TextView mTemperatureTv;
    private CityWeather mCityWeather;
    private TextView mRealfeelTemperature;
    private RefreshWeatherUnitView content;
    private RefreshWeatherUnitView mContent;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mPresenter = new MainPresenter(this);
        mPresenter.getCityWeatherData(getContext());
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle
            savedInstanceState) {
        mContent = (RefreshWeatherUnitView) inflater.inflate(R.layout.weather_info_layout, container, false);
        return mContent;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        mTemperatureTv = view.findViewById(R.id.temperature_tv);
        mRealfeelTemperature = view.findViewById(R.id.realfeel_temperature);
    }

    @Override
    public void showLoading() {

    }

    @Override
    public void dismissLoading() {

    }

    @Override
    public void showCityWeatherData(WeatherData1 weatherData) {
        mRealfeelTemperature.setText(weatherData.getInfo().getCurrent().getTemp());
        Log.d("1111", "showCityWeatherData: " + weatherData.getInfo().getCurrent().getTemp());
    }

    public void updateHourForecastView(RootWeather data, String timeZone) {
        if (data == null || data.getHourForecastsWeather() == null || data.getHourForecastsWeather().size() <= 0) {
            getChild(R.id.hourForecastView).setVisibility(View.GONE);
            getChild(R.id.hourForecastViewline1).setVisibility(View.GONE);
            getChild(R.id.hourForecastViewline2).setVisibility(View.GONE);
            return;
        }
        getChild(R.id.hourForecastView).setVisibility(View.VISIBLE);
        getChild(R.id.hourForecastViewline1).setVisibility(View.VISIBLE);
        getChild(R.id.hourForecastViewline2).setVisibility(View.VISIBLE);
        ((HourForecastView) getChild(R.id.hourForecastView)).updateForecastData(data.getHourForecastsWeather(), data.getDailyForecastsWeather(), 1, timeZone);
    }

    public View getChild(int id) {
        return mContent.findViewById(id);
    }

}
