package com.opweather.widget;

import android.content.Context;
import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.FrameLayout;

import com.opweather.R;
import com.opweather.adapter.HourForecastAdapter;
import com.opweather.api.helper.DateUtils;
import com.opweather.bean.HourForecastsWeather;
import com.opweather.bean.HourForecastsWeatherData;
import com.opweather.api.nodes.DailyForecastsWeather;
import com.opweather.api.nodes.Sun;
import com.opweather.api.nodes.Temperature;
import com.opweather.util.DateTimeUtils;
import com.opweather.util.StringUtils;
import com.opweather.util.SystemSetting;
import com.opweather.util.WeatherResHelper;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class HourForecastView extends FrameLayout {
    private HourForecastAdapter mAdapter;
    private Context mContext;
    private LinearLayoutManager mLinearLayoutManager;
    private RecyclerView mRecyclerView;
    private SpacesItemDecoration mSpacesItemDecoration;

    public class AsyncTaskHourLoad extends AsyncTask<Void, Void, List<HourForecastsWeatherData>> {

        private HourForecastAdapter adapter;
        private List<HourForecastsWeather> dataset;
        private int mCurrentTemp;
        private Sun mSun;

        public AsyncTaskHourLoad(HourForecastAdapter adapter, List<HourForecastsWeather> dataset,
                                 List<DailyForecastsWeather> dailyDate, int currentTemp, String timeZoneStr) {
            this.adapter = null;
            this.dataset = null;
            this.mSun = null;
            this.adapter = adapter;
            this.dataset = dataset;
            mCurrentTemp = currentTemp;
            DailyForecastsWeather today = DailyForecastsWeather.getTodayForecast(dailyDate, DateUtils.getTimeZone
                    (timeZoneStr));
            if (today != null) {
                this.mSun = today.getRealSun(dailyDate, DateUtils.getTimeZone(timeZoneStr));
            }
        }

        @Override
        protected List<HourForecastsWeatherData> doInBackground(Void... voids) {
            List<HourForecastsWeatherData> result = new ArrayList();
            for (int i = 0; i < dataset.size(); i++) {
                int iconId;
                String tempText;
                HourForecastsWeather weather = dataset.get(i);
                Date time = weather.getTime();
                String hourText = getHourText(time, mSun);
                if (mSun != null && checkSunTime(mSun.getSet(), time)) {
                    iconId = R.drawable.ic_sunset;
                    tempText = mContext.getString(R.string.sunset);
                } else if (mSun == null || !checkSunTime(mSun.getRise(), time)) {
                    iconId = WeatherResHelper.getWeatherIconResID(WeatherResHelper.weatherToResID(mContext, weather.getWeatherId()));
                    Temperature temperature = weather.getTemperature();
                    if (temperature == null || temperature.getCentigradeValue() == Double.NaN) {
                        tempText = StringUtils.EMPTY_STRING;
                    } else {
                        int temp;
                        float f;
                        if (hourText.equals(HourForecastView.this.mContext.getString(R.string.now))) {
                            temp = this.mCurrentTemp;
                        } else {
                            temp = (int) Math.floor(temperature.getCentigradeValue());
                        }
                        boolean cOrf = SystemSetting.getTemperature(mContext);
                        String tempUnit = cOrf ? "°" : "°";
                        if (cOrf) {
                            f = (float) temp;
                        } else {
                            f = SystemSetting.celsiusToFahrenheit((float) temp);
                        }
                        tempText = ((int) f) + tempUnit;
                    }
                } else {
                    iconId = R.drawable.ic_sunrise;
                    tempText = mContext.getString(R.string.sunrise);
                }
                if (result != null) {
                    result.add(new HourForecastsWeatherData(hourText, weather.getWeatherId(), iconId, tempText));
                }
            }
            return null;
        }
    }

    public HourForecastView(@NonNull Context context) {
        this(context, null);
    }

    public HourForecastView(@NonNull Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public HourForecastView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        ((LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE)).inflate(R.layout
                .hour_forecast_layout, this, true);
        mRecyclerView = findViewById(R.id.recyclerView);
        mRecyclerView.setHasFixedSize(true);
        mLinearLayoutManager = createLayoutManager(context);
        mRecyclerView.setLayoutManager(mLinearLayoutManager);
        mAdapter = new HourForecastAdapter(context);
        mRecyclerView.setAdapter(mAdapter);
        mSpacesItemDecoration = new SpacesItemDecoration(mLinearLayoutManager.getOrientation(), context.getResources()
                .getDimensionPixelSize(R.dimen.dimen_14), context.getResources().getDimensionPixelSize(R.dimen
                .dimen_23));
        mRecyclerView.addItemDecoration(mSpacesItemDecoration);
    }

    public void updateForecastData(List<HourForecastsWeather> dataset, List<DailyForecastsWeather> dailyDate, int
            currentTemp, String timeZone) {
        if (mAdapter != null) {
            new AsyncTaskHourLoad(mAdapter, dataset, dailyDate, currentTemp, timeZone).execute(new Void[0]);
        }
    }

    private LinearLayoutManager createLayoutManager(Context context) {
        LinearLayoutManager manager = new HourForecastLinearLayoutManager(context);
        manager.setOrientation(0);
        return manager;
    }

    private String getHourText(Date time, Sun mSun) {
        if (time == null) {
            return StringUtils.EMPTY_STRING;
        }
        if (mSun == null || !checkSunTime(mSun.getRise(), time)) {
            return (mSun == null || !checkSunTime(mSun.getSet(), time)) ? DateTimeUtils.DateTimeToHourMinute(time,
                    null) : DateTimeUtils.DateTimeToHourMinute(mSun.getSet(), null);
        } else {
            return DateTimeUtils.DateTimeToHourMinute(mSun.getRise(), null);
        }
    }

    private boolean isNow(Date time) {
        Calendar calendar1 = Calendar.getInstance();
        calendar1.setTime(time);
        Calendar calendar2 = Calendar.getInstance();
        return calendar1.get(Calendar.YEAR) == calendar2.get(Calendar.YEAR) && calendar1.get(Calendar.MONTH) ==
                calendar2.get(Calendar.MONTH) && calendar1.get(Calendar.DATE) == calendar2.get(Calendar.DATE) &&
                calendar1.get(Calendar.HOUR_OF_DAY) == calendar2.get(Calendar.HOUR_OF_DAY);
    }

    private boolean checkSunTime(Date sunTime, Date hourTime) {
        Calendar calendar1 = Calendar.getInstance();
        calendar1.setTime(sunTime);
        Calendar calendar2 = Calendar.getInstance();
        if (hourTime != null) {
            calendar2.setTime(hourTime);
        }
        if (calendar1.get(Calendar.YEAR) == calendar2.get(Calendar.YEAR) && calendar1.get(Calendar.MONTH) ==
                calendar2.get(Calendar.MONTH) && (calendar1.get(Calendar.DATE) ==
                calendar2.get(Calendar.DATE) || calendar1.get(Calendar.DATE) - calendar2.get(Calendar.DATE) == -1)) {
            return calendar1.get(Calendar.HOUR_OF_DAY) == calendar2.get(Calendar.HOUR_OF_DAY);
        } else {
            return false;
        }
    }

    private boolean checkNow(Date time) {
        return DateTimeUtils.distanceOfHour(time) == 1;
    }
}
