package com.opweather.ui;

import android.opengl.GLSurfaceView;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.FrameLayout;
import android.widget.Toast;

import com.opweather.R;
import com.opweather.constants.WeatherDescription;
import com.opweather.util.UIUtil;
import com.opweather.util.WeatherViewCreator;
import com.opweather.widget.AbsWeather;


public class ShowWeatherActivity extends BaseActivity {
    private long mStartTime;
    private View mWeather;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        UIUtil.setWindowStyle(this);
        int type = getIntent().getIntExtra("type", 0);
        if (type == 0) {
            finish();
        }
        int alert = 0;
        int color = 0;
        switch (type) {
            case WeatherDescription.WEATHER_DESCRIPTION_THUNDERSHOWER:
                alert = R.string.rain_alert;
                color = R.color.weather_thunder_shower_rain;
                break;
            case WeatherDescription.WEATHER_DESCRIPTION_SANDSTORM:
                alert = R.string.sand_alert;
                color = R.color.weather_dust;
                break;
            case WeatherDescription.WEATHER_DESCRIPTION_FOG:
                alert = R.string.fog_alert;
                color = R.color.weather_fog;
                break;
            case WeatherDescription.WEATHER_DESCRIPTION_HAZE:
                alert = R.string.haze_alert;
                color = R.color.weather_haze;
                break;
        }
        mWeather = (View) WeatherViewCreator.getViewFromDescription(this, type, true);
        mStartTime = System.currentTimeMillis();
        FrameLayout frameLayout = new FrameLayout(this);
        frameLayout.setLayoutParams(new LayoutParams(-1, -1));
        frameLayout.setBackgroundResource(color);
        ((AbsWeather) mWeather).startAnimate();
        frameLayout.addView(mWeather);
        setContentView(frameLayout);
        mWeather.setOnClickListener(new OnClickListener() {
            public void onClick(View view) {
                if (System.currentTimeMillis() - mStartTime > 10000) {
                    finish();
                }
            }
        });
        //Toast.makeText(this, alert, Toast.LENGTH_LONG).show();
    }

    protected void onPause() {
        super.onPause();
        if (mWeather != null) {
            ((AbsWeather) mWeather).onViewPause();
        }
        if (mWeather != null && (mWeather instanceof GLSurfaceView)) {
            ((GLSurfaceView) mWeather).onPause();
        }
        if (mWeather != null) {
            ((AbsWeather) mWeather).onViewPause();
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (mWeather != null) {
            ((AbsWeather) mWeather).onViewStart();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (mWeather != null && (mWeather instanceof GLSurfaceView)) {
            ((GLSurfaceView) mWeather).onResume();
        }
    }
}
