package com.opweather.widget.widget;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.Intent;
import android.database.Cursor;
import android.os.Handler;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.RemoteViews;

import com.opweather.R;
import com.opweather.bean.CityData;
import com.opweather.db.CityWeatherDB;
import com.opweather.db.CityWeatherDBHelper;
import com.opweather.db.CityWeatherDBHelper.CityListEntry;
import com.opweather.api.nodes.CurrentWeather;
import com.opweather.api.nodes.DailyForecastsWeather;
import com.opweather.api.nodes.RootWeather;
import com.opweather.api.WeatherException;
import com.opweather.ui.CityListActivity;
import com.opweather.util.DateTimeUtils;
import com.opweather.util.PreferenceUtils;
import com.opweather.util.StringUtils;
import com.opweather.util.TemperatureUtil;
import com.opweather.util.WeatherClientProxy;
import com.opweather.util.WeatherClientProxy.CacheMode;
import com.opweather.util.WeatherLog;
import com.opweather.util.WeatherResHelper;
import com.opweather.widget.openglbase.RainSurfaceView;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Iterator;
import java.util.List;
import java.util.TimeZone;

public class WidgetHelper extends ContextWrapper {
    public static final String ACTION_REFRESH = "net.oneplus.weather.widget.REFRESH";
    public static final String NEED_REFRESH = "need_refresh";
    public static final String TAG = "WidgetHelper";
    public static final String WIDGET_ID = "widget_id";
    public static final String WIDGET_ID_PREFIX = "widget_";
    private static volatile AppWidgetManager sAppWidgetManager;
    private static volatile WidgetHelper sWidgetHelper;
    private final Handler mHandler;
    private RemoteViews mRemoteViews;

    private WidgetHelper(Context base) {
        super(base);
        mHandler = new Handler();
        mRemoteViews = new RemoteViews(getPackageName(), R.layout.widget_weather);
    }

    public static WidgetHelper getInstance(Context context) {
        if (sWidgetHelper == null) {
            synchronized (WidgetHelper.class) {
                if (sWidgetHelper == null) {
                    sWidgetHelper = new WidgetHelper(context);
                    sAppWidgetManager = AppWidgetManager.getInstance(context);
                }
            }
        }
        return sWidgetHelper;
    }

    public void updateAllWidget(boolean isForce) {
      /*  for (int id : sAppWidgetManager.getAppWidgetIds(new ComponentName(this, WeatherWidgetProvider.class))) {
            updateWidgetById(id, isForce);
        }*/
    }

    public void updateWidgetById(int widgetId, boolean isForce) {
        if (widgetId == -1) {
            throw new IllegalArgumentException("citydata can't be empty");
        }
        updateDataThenWidget(null, widgetId, isForce);
    }

    private void updateDataThenWidget(CityData cityData, int widgetId, boolean isForce) {
        if (cityData == null && widgetId != -1) {
            CityWeatherDB instance = CityWeatherDB.getInstance(this);
            int locationId = PreferenceUtils.getInt(this, WIDGET_ID_PREFIX + widgetId, -1);
            if (locationId == -1) {
                WeatherLog.e("locationId is null ");
                setWidgetFailException(widgetId);
                return;
            }
            WeatherLog.d("locationId is :" + locationId);
            cityData = getCityByID(this, locationId);
            if (cityData == null) {
                setWidgetFailException(widgetId);
                return;
            }
        }
        if (cityData != null && widgetId == -1) {
            String locationId2 = cityData.getLocationId();
            if (TextUtils.isEmpty(locationId2)) {
                WeatherLog.e("locationId is null ");
                setWidgetFailException(widgetId);
                return;
            }
            widgetId = PreferenceUtils.getInt(this, WIDGET_ID_PREFIX + locationId2, -1);
        }
        if (cityData == null || widgetId == -1) {
            WeatherLog.e("cityData or widgetId is null ");
            return;
        }
        mRemoteViews.setViewVisibility(R.id.weather_widget, View.GONE);
        mRemoteViews.setViewVisibility(R.id.widget_refreshing_group, View.VISIBLE);
        mRemoteViews.setTextViewText(R.id.widget_refreshing_city, cityData.getLocalName());
        mRemoteViews.setImageViewResource(R.id.widget_refreshing_bar, R.mipmap.btn_refresh);
        mRemoteViews.setTextViewText(R.id.widget_refreshing_text, getString(R.string.widget_refreshing));
        Log.d(TAG, "updateDataThenWidget getConfiguration: " + getResources().getConfiguration().toString());
        sAppWidgetManager.updateAppWidget(widgetId, mRemoteViews);
        final CityData finalCityData = cityData;
        final int finalWidgetId = widgetId;
        new WeatherClientProxy(this).setCacheMode(isForce ? CacheMode.LOAD_NO_CACHE : CacheMode
                        .LOAD_CACHE_ELSE_NETWORK).requestWeatherInfo(cityData, new WeatherClientProxy
                .OnResponseListener() {
            @Override
            public void onCacheResponse(final RootWeather rootWeather) {
                mHandler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        updateWeatherWidget(rootWeather, finalCityData, finalWidgetId);
                    }
                }, 500);
            }

            @Override
            public void onErrorResponse(WeatherException weatherException) {
                setWidgetFail(finalWidgetId);
                WeatherLog.e("update error" + weatherException);
            }

            @Override
            public void onNetworkResponse(final RootWeather rootWeather) {
                mHandler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        updateWeatherWidget(rootWeather, finalCityData, finalWidgetId);
                    }
                }, 500);
            }
        });
    }

    private CityData getCityFromCoursor(Cursor cursor, int index) {
        if (cursor == null || cursor.getCount() < index) {
            return null;
        }
        cursor.moveToPosition(index);
        int provider = cursor.getInt(cursor.getColumnIndex(CityListEntry.COLUMN_1_PROVIDER));
        String cityName = cursor.getString(cursor.getColumnIndex(CityListEntry.COLUMN_2_NAME));
        String cityDisplayName = cursor.getString(cursor.getColumnIndex(CityListEntry.COLUMN_3_DISPLAY_NAME));
        String cityLocationId = cursor.getString(cursor.getColumnIndex(CityWeatherDBHelper.WeatherEntry.COLUMN_1_LOCATION_ID));
        CityData city = new CityData();
        city.setProvider(provider);
        city.setName(cityName);
        city.setLocalName(cityDisplayName);
        city.setLocationId(cityLocationId);
        return city;
    }

    private void updateWeatherWidget(RootWeather response, CityData cityData, int widgetId) {
        if (response == null || cityData == null) {
            setWidgetFailException(widgetId);
            return;
        }
        String timeZone = DateTimeUtils.CHINA_OFFSET;
        if (response.getCurrentWeather() != null) {
            CurrentWeather currentWeather = response.getCurrentWeather();
            mRemoteViews.setViewVisibility(R.id.weather_widget, View.VISIBLE);
            mRemoteViews.setViewVisibility(R.id.widget_refreshing_group, View.GONE);
            timeZone = currentWeather.getLocalTimeZone();
            mRemoteViews.setTextViewText(R.id.widget_city_name, cityData.getLocalName());
            RemoteViews remoteViews = mRemoteViews;
            Object[] objArr = new Object[1];
            objArr[0] = DateTimeUtils.dateToHourMinute(response.getDate(), null);
            remoteViews.setTextViewText(R.id.widget_refresh_time, getString(R.string.updated, objArr));
            mRemoteViews.setTextViewText(R.id.widget_weather_des, currentWeather.getWeatherText(this));
            mRemoteViews.setTextViewText(R.id.widget_weather_temp, TemperatureUtil.getCurrentTemperature(this,
                    response.getTodayCurrentTemp()));
            int highTemp = response.getTodayHighTemperature();
            int lowTemp = response.getTodayLowTemperature();
            Log.d(TAG, "highTemp:" + highTemp);
            Log.d(TAG, "lowTemp:" + lowTemp);
            String hTemp = TemperatureUtil.getHighTemperature(this, highTemp);
            mRemoteViews.setTextViewText(R.id.widget_high_low_temp, hTemp.replace("°", StringUtils
                    .EMPTY_STRING) + " / " + TemperatureUtil.getHighTemperature(this, lowTemp));
           /* try {
                mRemoteViews.setImageViewResource(R.id.widget_bkg, WeatherTypeUtil.getWidgetWeatherTypeResID
                        (WeatherResHelper.weatherToResID(this, response.getCurrentWeatherId()), cityData.isDay
                                (response)));
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }*/
        }
        if (response.getDailyForecastsWeather() != null && response.getDailyForecastsWeather().size() >= 3) {
            List<DailyForecastsWeather> dailyForecastsWeathers = new ArrayList();
            dailyForecastsWeathers.addAll(response.getDailyForecastsWeather());
            int realCurrentdate = DateTimeUtils.timeToDay(this, System.currentTimeMillis(), timeZone);
            Iterator<DailyForecastsWeather> iterator = dailyForecastsWeathers.iterator();
            while (iterator.hasNext()) {
                if (DateTimeUtils.timeToDay(this, ((DailyForecastsWeather) iterator.next()).getDate().getTime(),
                        timeZone) <= realCurrentdate) {
                    iterator.remove();
                }
            }
            int loopCount = RainSurfaceView.RAIN_LEVEL_DOWNPOUR;
            if (dailyForecastsWeathers.size() < 3) {
                loopCount = dailyForecastsWeathers.size();
            }
            for (int i = 0; i < loopCount; i++) {
                DailyForecastsWeather weather = (DailyForecastsWeather) dailyForecastsWeathers.get(i);
                Calendar c = Calendar.getInstance();
                c.setTimeZone(TimeZone.getTimeZone("GMT" + timeZone));
                c.setTimeInMillis(weather.getDate().getTime());
                String day = DateTimeUtils.getDayString(this, c.get(Calendar.DAY_OF_WEEK));
                if (i == 0) {
                    mRemoteViews.setTextViewText(R.id.daily1_date_text, day);
                    mRemoteViews.setImageViewResource(R.id.daily1_weather_icon, WeatherResHelper.getWeatherIconResID
                            (WeatherResHelper.weatherToResID(this, weather.getDayWeatherId())));
                    mRemoteViews.setTextViewText(R.id.daily1_weather_temp, TemperatureUtil.getHighTemperature(this,
                            (int)
                                    weather.getMaxTemperature().getCentigradeValue()).replace("°", StringUtils
                            .EMPTY_STRING) + " / " + TemperatureUtil.getHighTemperature(this, (int) weather
                            .getMinTemperature().getCentigradeValue()));
                }
                if (i == 1) {
                    mRemoteViews.setTextViewText(R.id.daily2_date_text, day);
                    mRemoteViews.setImageViewResource(R.id.daily2_weather_icon, WeatherResHelper.getWeatherIconResID
                            (WeatherResHelper.weatherToResID(this, weather.getDayWeatherId())));
                    mRemoteViews.setTextViewText(R.id.daily2_weather_temp, TemperatureUtil.getHighTemperature(this,
                            (int)
                                    weather.getMaxTemperature().getCentigradeValue()).replace("°", StringUtils
                            .EMPTY_STRING) + " / " + TemperatureUtil.getHighTemperature(this, (int) weather
                            .getMinTemperature().getCentigradeValue()));
                }
                if (i == 2) {
                    mRemoteViews.setTextViewText(R.id.daily3_date_text, day);
                    mRemoteViews.setImageViewResource(R.id.daily3_weather_icon, WeatherResHelper.getWeatherIconResID
                            (WeatherResHelper.weatherToResID(this, weather.getDayWeatherId())));
                    mRemoteViews.setTextViewText(R.id.daily3_weather_temp, TemperatureUtil.getHighTemperature(this,
                            (int)
                                    weather.getMaxTemperature().getCentigradeValue()).replace("°", StringUtils
                            .EMPTY_STRING) + " / " + TemperatureUtil.getHighTemperature(this, (int) weather
                            .getMinTemperature().getCentigradeValue()));
                }
            }
        }
        mRemoteViews.setOnClickPendingIntent(R.id.widget_refresh, getRefreshPendingIntent(this, widgetId));
        mRemoteViews.setOnClickPendingIntent(R.id.weather_widget, getClickPendingIntent(this, widgetId));
        sAppWidgetManager.updateAppWidget(widgetId, mRemoteViews);
    }

    private PendingIntent getClickPendingIntent(Context context, int widgetId) {
        Intent intentClick = getPackageManager().getLaunchIntentForPackage(getPackageName());
        intentClick.putExtra(WIDGET_ID, widgetId);
        intentClick.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        return PendingIntent.getActivity(context, widgetId, intentClick, PendingIntent.FLAG_UPDATE_CURRENT);
    }

    private PendingIntent getRefreshPendingIntent(Context context, int widgetId) {
        /*Intent refreshIntent = new Intent(context, WidgetReceiver.class);
        refreshIntent.putExtra(NEED_REFRESH, true);
        refreshIntent.putExtra(WIDGET_ID, widgetId);
        refreshIntent.setAction(ACTION_REFRESH);
        return PendingIntent.getBroadcast(context, widgetId, refreshIntent, PendingIntent.FLAG_UPDATE_CURRENT);*/
        return null;
    }

    private void setWidgetFail(int widgetId) {
        mRemoteViews.setViewVisibility(R.id.weather_widget, View.GONE);
        mRemoteViews.setViewVisibility(R.id.widget_refreshing_group, View.VISIBLE);
        mRemoteViews.setTextViewText(R.id.widget_refreshing_text, getString(R.string.widget_refresh_fail));
        mRemoteViews.setOnClickPendingIntent(R.id.widget_refreshing_bar, getRefreshPendingIntent(this, widgetId));
        sAppWidgetManager.updateAppWidget(widgetId, mRemoteViews);
    }

    private void setWidgetFailException(int widgetId) {
        mRemoteViews.setViewVisibility(R.id.weather_widget, View.GONE);
        mRemoteViews.setViewVisibility(R.id.widget_refreshing_group, View.VISIBLE);
        mRemoteViews.setImageViewResource(R.id.widget_bkg, R.mipmap.widget_bkg_sunny);
        mRemoteViews.setImageViewResource(R.id.widget_refreshing_bar, R.drawable.ic_add_city);
        mRemoteViews.setTextViewText(R.id.widget_refreshing_text, getString(R.string.widget_refresh_fail_add));
        mRemoteViews.setTextViewText(R.id.widget_refreshing_city, getString(R.string.app_name));
        Intent chooseIntent = new Intent(this, CityListActivity.class);
        chooseIntent.putExtra(NEED_REFRESH, true);
        chooseIntent.putExtra("appWidgetId", widgetId);
        chooseIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        mRemoteViews.setOnClickPendingIntent(R.id.widget_refreshing_bar, PendingIntent.getActivity(this,
                widgetId, chooseIntent, PendingIntent.FLAG_UPDATE_CURRENT));
        sAppWidgetManager.updateAppWidget(widgetId, mRemoteViews);
    }

    public void setCityByID(Context context, CityData cityData) {
        if (cityData == null) {
            throw new IllegalArgumentException("cityData can't empty");
        }
        String locationId = cityData.getLocationId();
        PreferenceUtils.applyString(context, locationId + "city_name", cityData.getName());
        PreferenceUtils.applyString(context, locationId + "city_localname", cityData.getLocalName());
        PreferenceUtils.applyInt(context, locationId + "city_provider", cityData.getProvider());
        PreferenceUtils.applyString(context, locationId + "city_locationid", cityData.getLocationId());
    }

    public CityData getCityByID(Context context, int locationId) {
        CityData city = new CityData();
        city.setName(PreferenceUtils.getString(context, locationId + "city_name", getString(R.string
                .current_location)));
        city.setLocalName(PreferenceUtils.getString(context, locationId + "city_localname", getString(R.string
                .current_location)));
        city.setProvider(PreferenceUtils.getInt(context, locationId + "city_provider", -1));
        city.setLocationId(PreferenceUtils.getString(context, locationId + "city_locationid", "-1"));
        return city;
    }
}
