package com.opweather.api.impl;

import android.content.Context;
import android.os.AsyncTask;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.util.Log;

import com.opweather.api.WeatherRequest;
import com.opweather.api.WeatherRequest.CacheMode;
import com.opweather.api.helper.NetworkHelper;
import com.opweather.api.parser.ParseException;
import com.opweather.api.cache.WeatherCache;
import com.opweather.api.helper.LogUtils;
import com.opweather.api.nodes.RootWeather;
import com.opweather.api.WeatherException;
import com.opweather.api.WeatherResponse;

import java.util.Arrays;

public class WeatherRequestExecuter extends AbstractExecuter {
    private static final String TAG = "WeatherRequestExecuter";
    private Context mContext;

    private class CacheBox {
        boolean error;
        private int mRequestedType;
        RootWeather mResult;

        private CacheBox() {
            error = false;
            mRequestedType = 0;
        }

        void addResponse(RootWeather weather, int type) {
            mRequestedType |= type;
            if (weather != null) {
                if (mResult == null) {
                    mResult = weather;
                } else {
                    switch (type) {
                        case 1:
                            mResult.setCurrentWeather(weather.getCurrentWeather());
                        case 2:
                            mResult.setHourForecastsWeather(weather.getHourForecastsWeather());
                        case 4:
                            mResult.setDailyForecastsWeather(weather.getDailyForecastsWeather());
                            mResult.setFutureLink(weather.getFutureLink());
                        case 8:
                            mResult.setAqiWeather(weather.getAqiWeather());
                        case 16:
                            mResult.setLifeIndexWeather(weather.getLifeIndexWeather());
                        case 32:
                            mResult.setWeatherAlarms(weather.getWeatherAlarms());
                        default:
                            break;
                    }
                }
            }
        }

        boolean isRequested(int type) {
            boolean hasData = true;
            switch (type) {
                case 1:
                    if (mResult == null || mResult.getCurrentWeather() == null) {
                        hasData = false;
                    } else {
                        hasData = true;
                    }
                    break;
                case 2:
                    if (mResult == null || mResult.getHourForecastsWeather() == null) {
                        hasData = false;
                    } else {
                        hasData = true;
                    }
                    break;
                case 4:
                    if (mResult == null || mResult.getDailyForecastsWeather() == null) {
                        hasData = false;
                    } else {
                        hasData = true;
                    }
                    break;
                case 8:
                    if (mResult == null || mResult.getAqiWeather() == null) {
                        hasData = false;
                    } else {
                        hasData = true;
                    }
                    break;
                case 16:
                    if (mResult == null || mResult.getLifeIndexWeather() == null) {
                        hasData = false;
                    } else {
                        hasData = true;
                    }
                    break;
                case 32:
                    hasData = mResult != null && mResult.getWeatherAlarms() != null;
                    break;
            }
            return (mRequestedType & type) == type || hasData;
        }

        RootWeather getResult() {
            return mResult;
        }
    }

    private class CacheParserWorkerTask extends AsyncTask<String, Void, Void> {
        private final CacheBox mCacheBox;
        private final WeatherRequest mRequest;
        private final int mRequestType;

        public CacheParserWorkerTask(int type, WeatherRequest request, CacheBox cacheBox) {
            mRequestType = type;
            mRequest = request;
            mCacheBox = cacheBox;
        }

        @Override
        protected Void doInBackground(String... key) {
            byte[] data = WeatherCache.getInstance(mContext).getFromDiskCache(key[0]);
            Log.d(TAG, "doInBackground: data " + Arrays.toString(data));
            try {
                RootWeather rootWeather;
                if (mRequestType == 8) {
                    rootWeather = mRequest.getResponseParser().parseAqi(data);
                } else if (mRequestType == 1) {
                    rootWeather = mRequest.getResponseParser().parseCurrent(data);
                } else if (mRequestType == 16) {
                    rootWeather = mRequest.getResponseParser().parseLifeIndex(data);
                } else if (mRequestType == 2) {
                    rootWeather = mRequest.getResponseParser().parseHourForecasts(data);
                } else if (mRequestType == 4) {
                    rootWeather = mRequest.getResponseParser().parseDailyForecasts(data);
                } else if (mRequestType == 32) {
                    rootWeather = mRequest.getResponseParser().parseAlarm(data);
                } else {
                    throw new WeatherException("Unsupport request type!");
                }
                mCacheBox.addResponse(rootWeather, mRequestType);
            } catch (WeatherException e) {
                if (e instanceof ParseException) {
                    mCacheBox.addResponse(null, mRequestType);
                } else {
                    mCacheBox.error = true;
                }
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            requestOrDeliverCache(mRequest, mCacheBox);
        }
    }

    private class CacheTask extends AsyncTask<String, Void, Void> {
        private final WeatherRequest mRequest;
        private final int mRequestType;
        private final WeatherResponse mResponse;

        public CacheTask(int type, WeatherRequest request, WeatherResponse response) {
            mRequestType = type;
            mRequest = request;
            mResponse = response;
        }

        @Override
        protected Void doInBackground(String... key) {
            byte[] data = WeatherCache.getInstance(mContext).getFromDiskCache(key[0]);
            try {
                RootWeather rootWeather;
                if (mRequestType == 8) {
                    rootWeather = mRequest.getResponseParser().parseAqi(data);
                } else if (mRequestType == 1) {
                    rootWeather = mRequest.getResponseParser().parseCurrent(data);
                } else if (mRequestType == 16) {
                    rootWeather = mRequest.getResponseParser().parseLifeIndex(data);
                } else if (mRequestType == 2) {
                    rootWeather = mRequest.getResponseParser().parseHourForecasts(data);
                } else if (mRequestType == 4) {
                    rootWeather = mRequest.getResponseParser().parseDailyForecasts(data);
                } else if (mRequestType == 32) {
                    rootWeather = mRequest.getResponseParser().parseAlarm(data);
                } else {
                    throw new WeatherException("Unsupport request type!");
                }
                if (rootWeather != null) {
                    rootWeather.setRequestIsSuccess(true);
                }
                mResponse.addResponse(rootWeather, mRequestType);
            } catch (WeatherException e) {
                if (e instanceof ParseException) {
                    mResponse.addResponse(null, mRequestType);
                } else {
                    mResponse.setError(new WeatherException(e.getMessage()));
                }
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            requestOrDeliverNetwork(mRequest, mResponse);
        }
    }

    private class NetworkParserWorkerTask extends AsyncTask<byte[], Void, Void> {
        private final WeatherRequest mRequest;
        private final int mRequestType;
        private final WeatherResponse mResponse;

        public NetworkParserWorkerTask(int type, WeatherRequest request, WeatherResponse response) {
            mRequestType = type;
            mRequest = request;
            mResponse = response;
        }

        @Override
        protected Void doInBackground(byte[]... params) {
            byte[] data = params[0];
            try {
                RootWeather rootWeather;
                if (mRequestType == 8) {
                    rootWeather = mRequest.getResponseParser().parseAqi(data);
                } else if (mRequestType == 1) {
                    rootWeather = mRequest.getResponseParser().parseCurrent(data);
                } else if (mRequestType == 16) {
                    rootWeather = mRequest.getResponseParser().parseLifeIndex(data);
                } else if (mRequestType == 2) {
                    rootWeather = mRequest.getResponseParser().parseHourForecasts(data);
                } else if (mRequestType == 4) {
                    rootWeather = mRequest.getResponseParser().parseDailyForecasts(data);
                } else if (mRequestType == 32) {
                    rootWeather = mRequest.getResponseParser().parseAlarm(data);
                } else {
                    throw new WeatherException("Unsupport request type!");
                }
                if (rootWeather != null) {
                    rootWeather.setRequestIsSuccess(true);
                }
                mResponse.addResponse(rootWeather, mRequestType);
            } catch (WeatherException e) {
                if (e instanceof ParseException) {
                    mResponse.addResponse(null, mRequestType);
                } else {
                    mResponse.setError(new WeatherException(e.getMessage()));
                }
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            requestOrDeliverNetwork(mRequest, mResponse);
        }
    }

    public WeatherRequestExecuter(Context context) {
        mContext = context;
    }

    @Override
    public void execute(WeatherRequest weatherRequest) {
        if (weatherRequest == null) {
            throw new AssertionError();
        } else if (weatherRequest.getCacheMode() == CacheMode.LOAD_DEFAULT) {
            fetchCache(weatherRequest);
            fetchNetwork(weatherRequest);
        } else if (weatherRequest.getCacheMode() == CacheMode.LOAD_NO_CACHE) {
            fetchNetwork(weatherRequest);
        } else {
            fetchCache(weatherRequest);
        }
    }

    private void fetchNetwork(WeatherRequest request) {
        requestOrDeliverNetwork(request, new WeatherResponse());
    }

    private void fetchCache(WeatherRequest request) {
        RootWeather weather = WeatherCache.getInstance(mContext).getFromMemCache(request.getMemCacheKey());
        if (weather == null || !WeatherResponse.containRequestedData(request.getRequestType(), weather)) {
            fetchDiskCache(request);
            return;
        }
        request.deliverCacheResponse(weather);
        LogUtils.d(TAG, "命中内存缓存，区域id：" + (weather.getAreaCode() != null ? weather.getAreaCode() : "null") + "，区域名称："
                + (weather.getAreaName() != null ? weather.getAreaName() : "null"), new Object[0]);
    }

    private void fetchDiskCache(WeatherRequest request) {
        requestOrDeliverCache(request, new CacheBox());
    }

    private void requestOrDeliverNetwork(WeatherRequest request, WeatherResponse response) {
        if (!response.isSuccess()) {
            WeatherResponse.deliverResponse(mContext, request, response);
        } else if (request.containRequest(1) && !response.isRequested(1)) {
            requestNetworkData(1, request, response);
        } else if (request.containRequest(8) && !response.isRequested(8)) {
            requestNetworkData(8, request, response);
        } else if (request.containRequest(16) && !response.isRequested(16)) {
            requestNetworkData(16, request, response);
        } else if (request.containRequest(2) && !response.isRequested(2)) {
            requestNetworkData(2, request, response);
        } else if (request.containRequest(4) && !response.isRequested(4)) {
            requestNetworkData(4, request, response);
        } else if (!request.containRequest(32) || response.isRequested(32)) {
            WeatherResponse.deliverResponse(mContext, request, response);
        } else {
            requestNetworkData(32, request, response);
        }
    }

    private void requestNetworkData(final int type, final WeatherRequest request, final WeatherResponse response) {
        String url = request.getRequestUrl(type);
        Log.d(TAG, "Request url: " + url);
        NetworkHelper.getInstance(mContext).get(url, new NetworkHelper.ResponseListener() {
            @Override
            public void onError(WeatherException weatherException) {
                Log.e(TAG, "onError: " + weatherException.toString());
                RootWeather weather = WeatherCache.getInstance(mContext).getFromMemCache(request.getMemCacheKey());
                if (weather == null || !WeatherResponse.containRequestedData(request.getRequestType(), weather)) {
                    String key = request.getDiskCacheKey(type);
                    new CacheTask(type, request, response).execute(new String[]{key});
                    return;
                }
                RootWeather emptyWeather = new RootWeather(weather.getAreaCode(), weather.getDataSourceName());
                WeatherRequestExecuter.this.setRootWeather(emptyWeather, weather, type);
                emptyWeather.setRequestIsSuccess(true);
                response.addResponse(emptyWeather, type);
                requestOrDeliverNetwork(request, response);
            }

            @Override
            public void onResponse(byte[] data, String str) {
                Log.d(TAG, "onResponse: str = " + str);
                addToDiskCache(mContext, request.getDiskCacheKey(type), data);
                new NetworkParserWorkerTask(type, request, response).execute(data);
            }
        }, request.getHttpCacheEnable());
    }

    public RootWeather setRootWeather(RootWeather targetWeather, RootWeather weather, int type) {
        if (weather != null) {
            switch (type) {
                case 1:
                    targetWeather.setCurrentWeather(weather.getCurrentWeather());
                    break;
                case 2:
                    targetWeather.setHourForecastsWeather(weather.getHourForecastsWeather());
                    break;
                case 4:
                    targetWeather.setDailyForecastsWeather(weather.getDailyForecastsWeather());
                    targetWeather.setFutureLink(weather.getFutureLink());
                    break;
                case 8:
                    targetWeather.setAqiWeather(weather.getAqiWeather());
                    break;
                case 16:
                    targetWeather.setLifeIndexWeather(weather.getLifeIndexWeather());
                    break;
                case 32:
                    targetWeather.setWeatherAlarms(weather.getWeatherAlarms());
                    break;
            }
        }
        return targetWeather;
    }

    private void requestOrDeliverCache(WeatherRequest request, CacheBox box) {
        if (box.error) {
            request.deliverCacheResponse(null);
        } else if (request.containRequest(1) && !box.isRequested(1)) {
            requestCacheData(1, request, box);
        } else if (request.containRequest(8) && !box.isRequested(8)) {
            requestCacheData(8, request, box);
        } else if (request.containRequest(16) && !box.isRequested(16)) {
            requestCacheData(16, request, box);
        } else if (request.containRequest(2) && !box.isRequested(2)) {
            requestCacheData(2, request, box);
        } else if (request.containRequest(4) && !box.isRequested(4)) {
            requestCacheData(4, request, box);
        } else if (!request.containRequest(32) || box.isRequested(32)) {
            RootWeather result = box.getResult();
            if (result != null) {
                result.writeMemoryCache(request, WeatherCache.getInstance(mContext));
                LogUtils.d(TAG, "命中磁盘缓存，区域id：" + (result.getAreaCode() != null ? result.getAreaCode() : "null") +
                        "，区域名称：" + (result.getAreaName() != null ? result.getAreaName() : "null"));
            } else {
                LogUtils.d(TAG, "缓存中不存在，地区id：" + request.getRequestKey());
                if (request.getCacheMode() == CacheMode.LOAD_CACHE_ELSE_NETWORK) {
                    fetchNetwork(request);
                }
            }
            request.deliverCacheResponse(result);
        } else {
            requestCacheData(ItemTouchHelper.END, request, box);
        }
    }

    private void requestCacheData(int type, WeatherRequest request, CacheBox box) {
        LogUtils.d(TAG, "Cache key: " + request.getDiskCacheKey(type));
        LogUtils.d(TAG, "Cache key: " + request.getMemCacheKey());
        LogUtils.d(TAG, "Cache key: " + request.getCacheMode());
        LogUtils.d(TAG, "Cache key: " + request.getRequestUrl(type));
        LogUtils.d(TAG, "Cache key: " + request.getRequestType());
        LogUtils.d(TAG, "Cache key: " + request.getLocale());
        new CacheParserWorkerTask(type, request, box).execute(request.getDiskCacheKey(type));
    }
}
