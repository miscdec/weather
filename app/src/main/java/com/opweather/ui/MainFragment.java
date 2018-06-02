package com.opweather.ui;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.opweather.R;
import com.opweather.bean.CityWeather;
import com.opweather.contract.MainContract;
import com.opweather.presenter.MainPresenter;

/**
 * Created by leeyh on 3/14.
 */

public class MainFragment extends Fragment implements MainContract.View {

    private MainPresenter mPresenter;
    private TextView mTemperatureTv;
    private CityWeather mCityWeather;

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
        View view = inflater.inflate(R.layout.weather_info_layout, container, false);
        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        mTemperatureTv = view.findViewById(R.id.temperature_tv);
    }

    @Override
    public void showLoading() {

    }

    @Override
    public void dismissLoading() {

    }

    @Override
    public void showCityWeatherData(CityWeather cityWeather) {
        mTemperatureTv.setText(cityWeather.getHeWeather6().get(0).getDaily_forecast().get(0).getTmp_max());
        mTemperatureTv.setCompoundDrawables(null, null, getResources().getDrawable(R.mipmap.ic_200), null);
    }
}
