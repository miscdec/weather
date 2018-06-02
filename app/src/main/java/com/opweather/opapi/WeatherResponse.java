package com.opweather.opapi;

import android.content.Context;
import android.support.v7.widget.helper.ItemTouchHelper;

import com.opweather.api.WeatherRequest;
import com.opweather.widget.openglbase.RainSurfaceView;

public class WeatherResponse {
    private WeatherException mError;
    private int mRequestedType;
    private RootWeather mResult;

    public interface CacheListener {
        void onResponse(RootWeather rootWeather);
    }

    public interface NetworkListener {
        void onResponseError(WeatherException weatherException);

        void onResponseSuccess(RootWeather rootWeather);
    }

    public static void deliverResponse(Context context, WeatherRequest request, WeatherResponse response) {
        if (response.isSuccess()) {
            RootWeather weather = response.getResult();
            if (weather == null) {
                request.deliverNetworkError(new WeatherException("Weather response is null!"));
                return;
            } else if (containRequestedData(request.getRequestType(), weather)) {
                weather.writeMemoryCache(request, WeatherCache.getInstance(context));
                request.deliverNetworkResponse(weather);
                return;
            } else {
                request.deliverNetworkError(new WeatherException("Response not contained the request data!"));
                return;
            }
        }
        request.deliverNetworkError(response.getError());
    }

    public static boolean containRequestedData(int type, RootWeather weather) {
        if (WeatherRequest.contain(type, ItemTouchHelper.RIGHT) && weather.getAqiWeather() != null) {
            return true;
        }
        if (WeatherRequest.contain(type, 1) && weather.getCurrentWeather() != null) {
            return true;
        }
        if (WeatherRequest.contain(type, RainSurfaceView.RAIN_LEVEL_RAINSTORM) && weather.getDailyForecastsWeather() != null) {
            return true;
        }
        if (WeatherRequest.contain(type, RainSurfaceView.RAIN_LEVEL_SHOWER) && weather.getHourForecastsWeather() != null) {
            return true;
        }
        if (!WeatherRequest.contain(type, ItemTouchHelper.START) || weather.getLifeIndexWeather() == null) {
            return WeatherRequest.contain(type, ItemTouchHelper.END) && weather.getWeatherAlarms() != null;
        } else {
            return true;
        }
    }

    public void addResponse(RootWeather weather, int type) {
        if (this.mResult == null) {
            this.mResult = weather;
        } else if (weather != null) {
            switch (type) {
                case RainSurfaceView.RAIN_LEVEL_NORMAL_RAIN:
                    this.mResult.setCurrentWeather(weather.getCurrentWeather());
                    break;
                case RainSurfaceView.RAIN_LEVEL_SHOWER:
                    this.mResult.setHourForecastsWeather(weather.getHourForecastsWeather());
                    break;
                case RainSurfaceView.RAIN_LEVEL_RAINSTORM:
                    this.mResult.setDailyForecastsWeather(weather.getDailyForecastsWeather());
                    this.mResult.setFutureLink(weather.getFutureLink());
                    break;
                case ItemTouchHelper.RIGHT:
                    this.mResult.setAqiWeather(weather.getAqiWeather());
                    break;
                case ItemTouchHelper.START:
                    this.mResult.setLifeIndexWeather(weather.getLifeIndexWeather());
                    break;
                case ItemTouchHelper.END:
                    this.mResult.setWeatherAlarms(weather.getWeatherAlarms());
                    break;
                default:
                    break;
            }
        }
        this.mRequestedType |= type;
    }

    public boolean isSuccess() {
        return getError() == null;
    }

    public RootWeather getResult() {
        return this.mResult;
    }

    public WeatherException getError() {
        return this.mError;
    }

    public boolean isRequested(int type) {
        boolean hasData = false;
        switch (type) {
            case RainSurfaceView.RAIN_LEVEL_NORMAL_RAIN:
                if (this.mResult == null || this.mResult.getCurrentWeather() == null) {
                    hasData = false;
                } else {
                    hasData = true;
                }
                break;
            case RainSurfaceView.RAIN_LEVEL_SHOWER:
                if (this.mResult == null || this.mResult.getHourForecastsWeather() == null) {
                    hasData = false;
                } else {
                    hasData = true;
                }
                break;
            case RainSurfaceView.RAIN_LEVEL_RAINSTORM:
                if (this.mResult == null || this.mResult.getDailyForecastsWeather() == null) {
                    hasData = false;
                } else {
                    hasData = true;
                }
                break;
            case ItemTouchHelper.RIGHT:
                if (this.mResult == null || this.mResult.getAqiWeather() == null) {
                    hasData = false;
                } else {
                    hasData = true;
                }
                break;
            case ItemTouchHelper.START:
                if (this.mResult == null || this.mResult.getLifeIndexWeather() == null) {
                    hasData = false;
                } else {
                    hasData = true;
                }
                break;
            case ItemTouchHelper.END:
                hasData = (this.mResult == null || this.mResult.getWeatherAlarms() == null) ? false : true;
                break;
        }
        return (this.mRequestedType & type) == type || hasData;
    }

    public void setError(WeatherException error) {
        this.mError = error;
        this.mResult = null;
        this.mRequestedType = 0;
    }
}

