package com.opweather.adapter;

import android.content.ContentValues;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v4.view.PagerAdapter;
import android.support.v7.widget.ListPopupWindow;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.TextView;

import com.opweather.R;
import com.opweather.api.WeatherException;
import com.opweather.api.nodes.RootWeather;
import com.opweather.bean.CityData;
import com.opweather.constants.WeatherDescription;
import com.opweather.db.ChinaCityDB;
import com.opweather.db.CityWeatherDB;
import com.opweather.ui.ContentWrapper;
import com.opweather.ui.ContentWrapper.OnUIChangedListener;
import com.opweather.ui.MainActivity;
import com.opweather.ui.MainActivity.OnViewPagerScrollListener;
import com.opweather.util.DateTimeUtils;
import com.opweather.util.SystemSetting;
import com.opweather.util.WeatherClientProxy;
import com.opweather.util.WeatherClientProxy.CacheMode;
import com.opweather.util.WeatherResHelper;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MainPagerAdapter extends PagerAdapter {
    public static final int DEFAULT_WEATHER_INDEX = 0;
    private List<CityData> mCitys;
    private Map<Integer, WeakReference<ContentWrapper>> mContentWrapper;
    private Context mContext;
    private OnUIChangedListener mOnUIChangedListener;
    private List<OnViewPagerScrollListener> mOnViewPagerScrollListener;
    private TextView mTextView;

    public MainPagerAdapter(Context context, List<OnViewPagerScrollListener> l, TextView textView) {
        mContext = context;
        mCitys = new ArrayList<>();
        updateCityList(context);
        mOnViewPagerScrollListener = l;
        mContentWrapper = new HashMap<>();
        mTextView = textView;
    }

    public void loadWeather(int position) {
        loadWeather(position, false);
    }

    public void loadWeather(int position, boolean force) {
        if (this.mContentWrapper.size() > position) {
            WeakReference<ContentWrapper> wp = mContentWrapper.get(position);
            if (wp != null) {
                ContentWrapper cw = (ContentWrapper) wp.get();
                if (cw == null) {
                    return;
                }
                if (!cw.isSuccess() && !cw.isLoading()) {
                    cw.updateWeatherInfo(CacheMode.LOAD_NO_CACHE);
                } else if (!cw.isLoading()) {
                    if (position == 0 && force) {
                        cw.updateWeatherInfo(CacheMode.LOAD_NO_CACHE);
                    } else if (mCitys.size() > position) {
                        String cityId = mCitys.get(position).getLocationId();
                        long rTime = SystemSetting.getRefreshTime(this.mContext, cityId);
                        if (force || DateTimeUtils.checkNeedRefresh(rTime) || WeatherClientProxy.needPullWeather
                                (mContext, cityId, cw.getCityWeather())) {
                            cw.updateWeatherInfo(CacheMode.LOAD_NO_CACHE);
                        }
                    }
                }
            }
        }
    }

    public void setOnUIChangedListener(OnUIChangedListener l) {
        mOnUIChangedListener = l;
    }

    @Override
    public int getCount() {
        return mCitys.size();
    }

    @Override
    public boolean isViewFromObject(@NonNull View arg0, @NonNull Object arg1) {
        return arg0 == ((ContentWrapper) arg1).getContent();
    }

    @NonNull
    @Override
    public Object instantiateItem(@NonNull ViewGroup container, int position) {
        final CityData city = mCitys.get(position);
        WeakReference<ContentWrapper> wr = mContentWrapper.get(position);
        ContentWrapper wrapper = null;
        if (wr != null) {
            wrapper = wr.get();
            if (wrapper == null) {
                mContentWrapper.remove(position);
            }
        }
        if (wrapper == null) {
            wrapper = new ContentWrapper(mContext, city, new WeatherClientProxy.OnResponseListener() {
                @Override
                public void onCacheResponse(RootWeather rootWeather) {
                }

                @Override
                public void onErrorResponse(WeatherException weatherException) {
                }

                @Override
                public void onNetworkResponse(RootWeather rootWeather) {
                    if (rootWeather != null) {
                        CityWeatherDB.getInstance(mContext).updateLastRefreshTime(city.getLocationId(), DateTimeUtils
                                .longTimeToRefreshTime(mContext, System.currentTimeMillis()));
                    }
                }
            }, mTextView);
            wrapper.setOnUIChangedListener(mOnUIChangedListener);
            mContentWrapper.put(position, new WeakReference<>(wrapper));
            wrapper.updateWeatherInfo(CacheMode.LOAD_DEFAULT);
        } else {
            container.removeView(wrapper.getContent());
            wrapper.updateWeatherInfo(CacheMode.LOAD_CACHE_ELSE_NETWORK);
        }
        mOnViewPagerScrollListener.add(wrapper);
        wrapper.setIndex(position);
        container.addView(wrapper.getContent(), new LayoutParams(-1, -1));
        return wrapper;
    }

    @Override
    public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
        container.removeView(((ContentWrapper) object).getContent());
        mOnViewPagerScrollListener.remove(object);
    }

    public int getItemPosition(@NonNull Object object) {
        return ListPopupWindow.WRAP_CONTENT;
    }

    public boolean contains(CityData city) {
        for (CityData c : mCitys) {
            if (c.getId() == city.getId()) {
                return true;
            }
        }
        return false;
    }

    public boolean deleteCity(long id) {
        if (id < 0) {
            return false;
        }
        for (CityData c : mCitys) {
            if (c.getId() == id) {
                mCitys.remove(c);
                return true;
            }
        }
        return false;
    }

    public boolean updateCity(CityData city) {
        if (city == null) {
            return false;
        }
        for (CityData c : mCitys) {
            if (c.getId() == city.getId()) {
                c.copy(city);
                return true;
            }
        }
        return false;
    }

    public void updateCityList(Context context) {
        if (mCitys != null) {
            mCitys.clear();
        }
        if (mContentWrapper != null) {
            mContentWrapper.clear();
        }
        if (mOnViewPagerScrollListener != null) {
            mOnViewPagerScrollListener.clear();
        }
        boolean hasLocation = false;
        for (ContentValues values : CityWeatherDB.getInstance(mContext).getAllCityList()) {
            CityData city = CityData.parse(values);
            if (city.isLocatedCity()) {
                hasLocation = true;
            }
            mCitys.add(city);
            ChinaCityDB.openCityDB(context);
        }
        if (!hasLocation) {
            CityData cityData = new CityData();
            cityData.setLocatedCity(true);
            cityData.setDefault(true);
            cityData.setLocationId("0");
            cityData.setLocalName(mContext.getString(R.string.current_location));
            cityData.setName(mContext.getString(R.string.current_location));
            mCitys.add(0, cityData);
            CityWeatherDB.getInstance(mContext).addCurrentCity(cityData);
        }
    }

    public CityData getLocatedCityData() {
        return (mCitys == null || mCitys.size() == 0) ? null : (CityData) mCitys.get(0);
    }

    public int getWeatherDescriptionId(int position) {
        if (mCitys.size() <= position) {
            return WeatherDescription.WEATHER_DESCRIPTION_UNKNOWN;
        }
        RootWeather cWeather = mCitys.get(position).getWeathers();
        return (cWeather == null || cWeather.getCurrentWeather() == null || cWeather.getTodayForecast() == null) ?
                WeatherDescription.WEATHER_DESCRIPTION_UNKNOWN : WeatherResHelper.weatherToResID(mContext,
                cWeather.getCurrentWeatherId());
    }

    public CityData getCityAtPosition(int position) {
        return (mCitys.size() <= position || position <= -1) ? null : mCitys.get(position);
    }

    public ContentWrapper getContentWrap(int position) {
        if (this.mContentWrapper != null && mContentWrapper.size() > position) {
            WeakReference<ContentWrapper> wr = mContentWrapper.get(position);
            if (wr != null) {
                ContentWrapper cw = wr.get();
                if (cw != null) {
                    return cw;
                }
                mContentWrapper.remove(wr);
                return cw;
            }
        }
        return null;
    }

    public RootWeather getWeatherDataAtPosition(int position) {
        return (mCitys.size() <= position || position == -1) ? null : mCitys.get(position).getWeathers();
    }
}
