package com.opweather.ui;

import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Build.VERSION;
import android.os.Handler;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v4.widget.SwipeRefreshLayout.OnRefreshListener;
import android.text.TextUtils;
import android.util.Log;
import android.view.GestureDetector;
import android.view.GestureDetector.SimpleOnGestureListener;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.view.ViewGroup.MarginLayoutParams;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.opweather.R;
import com.opweather.api.WeatherException;
import com.opweather.api.nodes.CurrentWeather;
import com.opweather.api.nodes.DailyForecastsWeather;
import com.opweather.api.nodes.RootWeather;
import com.opweather.bean.CityData;
import com.opweather.constants.WeatherDescription;
import com.opweather.db.CityWeatherDB;
import com.opweather.provider.LocationProvider;
import com.opweather.provider.LocationProvider.OnLocationListener;
import com.opweather.ui.MainActivity.OnViewPagerScrollListener;
import com.opweather.util.DateTimeUtils;
import com.opweather.util.NetUtil;
import com.opweather.util.PermissionUtil;
import com.opweather.util.SystemSetting;
import com.opweather.util.TemperatureUtil;
import com.opweather.util.UIUtil;
import com.opweather.util.WeatherClientProxy;
import com.opweather.util.WeatherClientProxy.CacheMode;
import com.opweather.util.WeatherClientProxy.OnResponseListener;
import com.opweather.util.WeatherResHelper;
import com.opweather.widget.AqiView;
import com.opweather.widget.HourForecastView;
import com.opweather.widget.RefreshWeatherUnitView;
import com.opweather.widget.RefreshWeatherUnitView.OnRefreshUnitListener;
import com.opweather.widget.WeatherScrollView;
import com.opweather.widget.WeatherSingleInfoView;
import com.opweather.widget.WeatherTemperatureView;
import com.opweather.widget.widget.WidgetHelper;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.TimeZone;


public class ContentWrapper implements OnViewPagerScrollListener, OnRefreshUnitListener {
    private static final int NO_TEMP_DATA_FLAG = -2000;
    public static final String TAG = "ContentWrapper";
    private int cacheWeatherID;
    private RefreshWeatherUnitView content;
    private int index;
    private LayoutInflater inflater;
    private CityData mCityData;
    private Context mContext;
    public int mCurrentTemp;
    private GestureDetector mGestureDetector;
    private boolean mHasLocation = false;
    private boolean mIsFling = false;
    private boolean mLoading;
    private LocationProvider mLocationProvider;
    private boolean mMoved = false;
    private int mNeedMoveOffset;
    private float mOffset = 0.0f;
    private OnLocationListener mOnLocationListener;
    private OnResponseListener mOnResponseListener;
    Handler mScrollHandler = new Handler();
    private boolean mSuccess;
    private SwipeRefreshLayout mSwipeRefreshLayout;
    private TextView mToolbar_subTitle;
    private OnUIChangedListener mUIListener;
    private boolean mUp = false;
    private RootWeather mWeatherData;

    public interface OnUIChangedListener {
        void ChangePathMenuResource(int i, boolean z, boolean z2);

        void onChangedCurrentWeather();

        void onError();

        void onScrollViewChange(float f);

        void onWeatherDataUpdate(int i);
    }

    class ScrollViewGestureListener extends SimpleOnGestureListener {

        @Override
        public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
            return true;
        }

        @Override
        public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
            if (velocityY < -200.0f) {
                mIsFling = true;
            }
            return false;
        }
    }

    public void setIndex(int index) {
        this.index = index;
    }

    public int getIndex() {
        return this.index;
    }

    public void setOnUIChangedListener(OnUIChangedListener onUIChangedListener) {
        this.mUIListener = onUIChangedListener;
    }

    public ContentWrapper(Context context, CityData city, OnResponseListener l, TextView textView) {
        mContext = context;
        mCityData = city;
        setOnResponseListener(l);
        mGestureDetector = new GestureDetector(mContext, new ScrollViewGestureListener());
        mToolbar_subTitle = textView;
    }

    public void setOnResponseListener(OnResponseListener listener) {
        mOnResponseListener = listener;
    }

    public View getContent() {
        return content;
    }

    public CityData getCityData() {
        return mCityData;
    }

    public void setOnLocationListener(OnLocationListener l) {
        mOnLocationListener = l;
    }

    public RootWeather getCurrentWeather() {
        return mCityData.getWeathers();
    }

    public void setCurrentWeather(RootWeather currentWeather) {
        mCityData.setWeathers(currentWeather);
    }

    public boolean isSuccess() {
        return mSuccess;
    }

    public void setSuccess(boolean success) {
        mSuccess = success;
    }

    public boolean isLoading() {
        return mLoading;
    }

    public boolean isLocation() {
        if (mCityData != null) {
            return mCityData.isLocatedCity();
        }
        return false;
    }

    public RootWeather getCityWeather() {
        if (mCityData != null) {
            return mCityData.getWeathers();
        }
        return null;
    }

    public void requestWeather(final CityData city, CacheMode mode) {
        if (city == null || TextUtils.isEmpty(city.getLocationId()) || city.getLocationId().equals("0")) {
            if (mSwipeRefreshLayout != null && mSwipeRefreshLayout.isRefreshing()) {
                mSwipeRefreshLayout.setRefreshing(false);
            }
            if (mUIListener != null) {
                mUIListener.onError();
                return;
            }
            return;
        }
        mLoading = true;
        new WeatherClientProxy(mContext).setCacheMode(mode).requestWeatherInfo(city, new OnResponseListener() {
            @Override
            public void onCacheResponse(RootWeather response) {
                mSuccess = true;
                mLoading = false;
                mCityData.setWeathers(response);
                mWeatherData = response;
                refreshLocatindLayout(false);
                updateCurrentWeatherUI();
                if (mSwipeRefreshLayout != null && mSwipeRefreshLayout.isRefreshing()) {
                    mSwipeRefreshLayout.setRefreshing(false);
                }
                if (!(response == null || mOnResponseListener == null)) {
                    mOnResponseListener.onCacheResponse(response);
                }
                if (mUIListener != null) {
                    mUIListener.onWeatherDataUpdate(index);
                }
                if (!(response == null || response.getCurrentWeather() == null)) {
                    cacheWeatherID = response.getCurrentWeather().getWeatherId();
                }
                if (mCityData.isLocatedCity()) {
                    SystemSetting.notifyWeatherDataChange(mContext.getApplicationContext());
                    SystemSetting.setLocale(mContext);
                }
            }

            @Override
            public void onNetworkResponse(RootWeather response) {
                mLoading = false;
                SystemSetting.setRefreshTime(mContext, city.getLocationId(), System.currentTimeMillis());
                mSuccess = true;
                mCityData.setWeathers(response);
                if (response != null) {
                    cacheWeatherID = response.getCurrentWeatherId();
                    mWeatherData = response;
                }
                refreshLocatindLayout(false);
                updateCurrentWeatherUI();
                CityWeatherDB.getInstance(mContext).updateLastRefreshTime(mCityData.getLocationId(), DateTimeUtils
                        .longTimeToRefreshTime(mContext, System.currentTimeMillis()));
                Log.d(TAG, "LastRefreshTime:" + CityWeatherDB.getInstance(mContext).getLastRefreshTime(mCityData
                        .getLocationId()));
                if (mSwipeRefreshLayout != null && mSwipeRefreshLayout.isRefreshing()) {
                    mSwipeRefreshLayout.setRefreshing(false);
                }
                if (mOnResponseListener != null) {
                    mOnResponseListener.onNetworkResponse(response);
                }
                if (mUIListener != null) {
                    mUIListener.onWeatherDataUpdate(index);
                }
                if (mCityData.isDefault()) {
                    SystemSetting.setLocationOrDefaultCity(mContext, city);
                    SystemSetting.notifyWeatherDataChange(mContext.getApplicationContext());
                    SystemSetting.setLocale(mContext);
                }
            }

            @Override
            public void onErrorResponse(WeatherException exception) {
                mSuccess = false;
                mLoading = false;
                refreshLocatindLayout(false);
                updateRefreshLayout();
                updateCurrentWeatherUI();
                if (mSwipeRefreshLayout != null && mSwipeRefreshLayout.isRefreshing()) {
                    mSwipeRefreshLayout.setRefreshing(false);
                }
                if (mOnResponseListener != null) {
                    mOnResponseListener.onErrorResponse(exception);
                }
            }
        });
    }

    public void updateWeatherInfo(CacheMode mode) {
        refreshLocatindLayout(false);
        initWeatherScrollView();
        if (mCityData.isLocatedCity()) {
            loadCurrentPositionWeatherInfo(mode);
        } else {
            requestWeather(mCityData, mode);
        }
    }

    public void loadCurrentPositionWeatherInfo(final CacheMode mode) {
        if (!mHasLocation || mode == CacheMode.LOAD_NO_CACHE) {
            if (mSwipeRefreshLayout == null || !(mSwipeRefreshLayout == null || mSwipeRefreshLayout.isRefreshing())) {
                requestWeather(mCityData, CacheMode.LOAD_CACHE_ONLY);
            }
            if (NetUtil.isNetworkAvailable(mContext)) {
                if (PermissionUtil.check((Activity) mContext, new String[]{"android.permission.ACCESS_FINE_LOCATION",
                        "android.permission.WRITE_EXTERNAL_STORAGE"}, 1)) {
                    mHasLocation = true;
                    if (mLocationProvider != null) {
                        mLocationProvider.stopLocation();
                        mLocationProvider = null;
                    }
                    mLocationProvider = new LocationProvider(mContext.getApplicationContext());
                    mLocationProvider.setOnLocationListener(new OnLocationListener() {
                        @Override
                        public void onLocationChanged(CityData data) {
                            Log.i("zhangyuan", "onLocationChanged name = " + data.getLocalName());
                            if (mOnLocationListener != null) {
                                mOnLocationListener.onLocationChanged(data);
                            }
                            mCityData.copy(data);
                            CityWeatherDB.getInstance(mContext).addCurrentCity(mCityData);
                            requestWeather(mCityData, mode);
                        }

                        @Override
                        public void onError(int error) {
                            Log.i("zhangyuan", "onLocationChanged onError");
                            if (mOnLocationListener != null) {
                                mOnLocationListener.onError(error);
                            }
                            mHasLocation = false;
                            requestWeather(mCityData, mode);
                            if (mOnResponseListener != null) {
                                mOnResponseListener.onErrorResponse(new WeatherException
                                        ("location error"));
                            }
                            if (mUIListener != null) {
                                mUIListener.onError();
                            }
                            changePathMenuResource(false, mLoading);
                        }
                    });
                    mLocationProvider.initLocation();
                    mLocationProvider.startLocation();
                    return;
                }
            }
            changePathMenuResource(false, mLoading);
            return;
        }
        requestWeather(mCityData, mode);
        if (mUIListener != null) {
            mUIListener.onError();
        }
    }

    public View getChild(int id) {
        return content.findViewById(id);
    }

    private void setAlpha(int resId, float alpha) {
        getChild(resId).setAlpha(alpha);
    }

    private void setText(int resId, String temp) {
        ((TextView) getChild(resId)).setText(temp);
    }

    private void setTextColor(int resId, int color) {
        ((TextView) getChild(resId)).setTextColor(color);
    }

    private int getTextColor(int resId) {
        try {
            return ((TextView) getChild(resId)).getCurrentTextColor();
        } catch (Exception e) {
            Log.e(TAG, "get textcolor error");
            return mContext.getResources().getColor(R.color.white);
        }
    }

    public void setImage(int resId, int imageId) {
        ((ImageView) getChild(resId)).setImageResource(imageId);
    }

    public void setVisibility(int resId, int visibility) {
        getChild(resId).setVisibility(visibility);
    }

    private void setIndex(int resId, String title, String value) {
        ((WeatherSingleInfoView) getChild(resId)).setInfoType(title).setInfoLevel(value);
    }

    private void setIndex(int resId, String title, String value, int iconId) {
        ((WeatherSingleInfoView) getChild(resId)).setInfoType(title).setInfoLevel(value)
                .setInfoIcon(iconId);
    }

    private void setIndexValue(int resId, String value) {
        ((WeatherSingleInfoView) getChild(resId)).setInfoLevel(value);
    }

    private void setAqiValue(int resId, int value, String type) {
        ((AqiView) getChild(resId)).setAqiValue(value);
        ((AqiView) getChild(resId)).setAqiType(type);
    }

    public void setTextAndAdjustMargin(int resId, String temp) {
        int length = temp.length();
        TextView tv = (TextView) getChild(resId);
        MarginLayoutParams mlp = (MarginLayoutParams) tv.getLayoutParams();
        if (length < 2) {
            mlp.setMargins(250, 170, 0, 0);
        } else if (length > 2) {
            mlp.setMargins(130, 170, 0, 0);
        }
        tv.setText(temp);
    }

    public int getWeatherInfoViewTopMargin(int height) {
        int heightDp = UIUtil.px2dip(mContext, (float) height);
        if (heightDp == 0) {
            heightDp = 151;
        }
        if (Build.PRODUCT.equals("A0001") && VERSION.RELEASE.equals("4.3")) {
            return -(heightDp + 20);
        }
        return -(heightDp - 10);
    }

    public void updateCurrentWeatherUI() {
        if (mCityData != null) {
            Log.e(TAG, "updateCurrentWeatherUI: " + mCityData.getLocalName());
            changeTopViewTextColor(10);
            updateRefreshLayout();
            Log.d("TAG", "mWeatherInfoView.getHeight():" + UIUtil.px2dip(mContext, (float) getChild
                    (R.id.opweather_info).getHeight()));
            RootWeather data = mCityData.getWeathers();
            if (data != null) {
                if (data.isFromAccu()) {
                    setVisibility(R.id.accu_logo, View.VISIBLE);
                } else {
                    setVisibility(R.id.accu_logo, View.INVISIBLE);
                }
                CurrentWeather current = data.getCurrentWeather();
                if (current != null) {
                    String humUnit;
                    float relativeHumidity;
                    boolean isChinaCity = data.isFromChina();
                    int curTemp = data.getTodayCurrentTemp();
                    int highTemp = data.getTodayHighTemperature();
                    int lowTemp = data.getTodayLowTemperature();
                    mCurrentTemp = curTemp;
                    setText(R.id.current_low_temperature, TemperatureUtil.getLowTemperature(mContext, lowTemp) +
                            (SystemSetting.getTemperature(mContext) ? "C" : "F"));
                    setText(R.id.current_hight_temperature, TemperatureUtil.getHighTemperature(mContext, highTemp));
                    setText(R.id.current_weather_type, current.getWeatherText(mContext));
                    setText(R.id.realfeel_temperature, TemperatureUtil.getCurrentTemperature(mContext, curTemp));
                    setImage(R.id.current_weather_icon, WeatherResHelper.getWeatherIconResID(WeatherResHelper
                            .weatherToResID(mContext, current.getWeatherId())));
                    boolean percentOrGm = SystemSetting.getHumidity(mContext);
                    if (percentOrGm) {
                        humUnit = mContext.getString(R.string.percent);
                    } else {
                        humUnit = mContext.getString(R.string.gm);
                    }
                    if (percentOrGm) {
                        relativeHumidity = (float) current.getRelativeHumidity();
                    } else {
                        relativeHumidity = SystemSetting.kmToMp((float) current.getRelativeHumidity());
                    }
                    int humidity = (int) relativeHumidity;
                    String humidityString = "--";
                    if (humidity < NO_TEMP_DATA_FLAG) {
                        humidityString = "--" + humUnit;
                    } else {
                        humidityString = humidity + humUnit;
                    }
                    if (isChinaCity) {
                        getChild(R.id.opweather_detail).setVisibility(View.VISIBLE);
                        setIndexValue(R.id.single_humidity_view, humidityString);
                        setIndex(R.id.single_wind_view, data.getCurrentWindDir(mContext), data.getCurrentWindPower
                                (mContext));
                        setIndex(R.id.single_wind_view, data.getCurrentWindDir(mContext), data.getCurrentWindPower
                                (mContext), WeatherResHelper.getWindIconId(mContext, current.getWind() != null ?
                                current.getWind().getDirection() : null));
                        setIndexValue(R.id.single_uv_view, data.getUvTextTransformSimlpeValue(mContext));
                        setAqiValue(R.id.aqiView, data.getAqiPM25Value(), data.getAqiTypeTransformSimlpe(mContext));
                        setIndexValue(R.id.single_bodytemp_view, TemperatureUtil.getLowTemperature(mContext, data
                                .getTodayBodytemp()));
                        setIndexValue(R.id.single_pressure_view, data.getTodayPressureTransformSimpleValue(mContext));
                        setIndexValue(R.id.single_visibility_view, data.getTodayVisibilityTransformSimpleValue
                                (mContext));
                    } else {
                        getChild(R.id.opweather_detail).setVisibility(View.GONE);
                    }
                }
                String timeZone = null;
                if (current != null) {
                    timeZone = current.getLocalTimeZone();
                }
                if (timeZone == null) {
                    timeZone = DateTimeUtils.CHINA_OFFSET;
                }
                updateForecastWeatherUI(data.getDailyForecastsWeather(), timeZone);
                updateHourForecastView(data, timeZone);
                if (mUIListener != null) {
                    mUIListener.onChangedCurrentWeather();
                }
            }
        }
    }

    private void updateForecastWeatherUI(List<DailyForecastsWeather> data, String timeZone) {
        if (data != null && data.size() > 0) {
            int count;
            ArrayList<Integer> arrayList = new ArrayList<>(6);
            ArrayList<Integer> mLowTemp = new ArrayList<>(6);
            long realCurrentTime = DateTimeUtils.getTimeByTimeZone();
            int averageHighTemp = getAverageHighTemp(data);
            int averageLowTemp = getAverageLowTemp(data);
            int realCurrentdate = DateTimeUtils.timeToDay(mContext, realCurrentTime, timeZone);
            Iterator<DailyForecastsWeather> iterator = data.iterator();
            while (iterator.hasNext()) {
                DailyForecastsWeather d = iterator.next();
                if (DateTimeUtils.timeToDay(mContext, d.getDate().getTime(), timeZone) <
                        realCurrentdate) {
                    iterator.remove();
                }
            }
            LinearLayout forecastLayoutContainer = (LinearLayout) getChild(R.id.forecast_weather);
            forecastLayoutContainer.removeAllViews();
            final List<DailyForecastsWeather> list = data;
            forecastLayoutContainer.setOnTouchListener(new OnTouchListener() {
                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    boolean isMove = false;
                    switch (event.getAction()) {
                        case MotionEvent.ACTION_DOWN:
                            ((ViewGroup) getChild(R.id.weather_scrollview)).requestDisallowInterceptTouchEvent(true);
                            break;
                        case MotionEvent.ACTION_UP:
                            break;
                        case MotionEvent.ACTION_MOVE:
                            isMove = true;
                            break;
                    }
                    if (isMove) {
                        ((ViewGroup) getChild(R.id.weather_scrollview)).requestDisallowInterceptTouchEvent(false);
                        return false;
                    }
                    int position = (int) Math.ceil((double) (((int) event.getRawX()) / (UIUtil.getScreenWidth(v
                            .getContext()) / 6)));
                    if (position > list.size() - 1) {
                        Log.e(ContentWrapper.TAG, "position > data.size()");
                    } else {
                        String url = list.get(position).getMobileLink();
                        if (url == null || TextUtils.isEmpty(url)) {
                            Log.e(ContentWrapper.TAG, "url is null");
                        } else {
                            clickUrl(url, v.getContext());
                        }
                    }
                    return true;
                }
            });
            for (int i = 0; i < 6; i++) {
                View dailyWeatherView = inflater.inflate(R.layout.forecast_daily_weather, null);
                if (i < data.size()) {
                    DailyForecastsWeather w = data.get(i);
                    Calendar c = Calendar.getInstance();
                    c.setTimeZone(TimeZone.getTimeZone("GMT" + timeZone));
                    long time = w.getDate().getTime();
                    c.setTimeInMillis(time);
                    String day = DateTimeUtils.getDayString(mContext, c.get(Calendar.DAY_OF_WEEK));
                    if (i == 0 && DateTimeUtils.longTimeToMMdd(mContext, time, timeZone).equals(DateTimeUtils
                            .longTimeToMMdd(mContext, realCurrentTime, null))) {
                        day = mContext.getString(R.string.totay);
                    }
                    if (!DateTimeUtils.longTimeToMMdd(mContext, time, timeZone).equals(DateTimeUtils.longTimeToMMdd
                            (mContext, realCurrentTime, null))) {
                        day = DateTimeUtils.getDayString(mContext, c.get(Calendar.DAY_OF_WEEK));
                    }
                    ((TextView) dailyWeatherView.findViewById(R.id.day_date)).setText(DateTimeUtils.longTimeToMMddTwo
                            (mContext, time, timeZone));
                    ((TextView) dailyWeatherView.findViewById(R.id.day)).setText(day);
                    ((ImageView) dailyWeatherView.findViewById(R.id.forecast_daily_weather_icon)).setImageResource
                            (WeatherResHelper.getWeatherIconResID(WeatherResHelper.weatherToResID(mContext, w
                                    .getDayWeatherId())));
                    if (w.getMaxTemperature() == null || w.getMaxTemperature().getCentigradeValue() < -2000.0d) {
                        arrayList.add(averageHighTemp);
                    } else {
                        arrayList.add(Math.max((int) w.getMaxTemperature().getCentigradeValue(), (int) w
                                .getMinTemperature().getCentigradeValue()));
                    }
                    if (w.getMinTemperature() == null || w.getMinTemperature().getCentigradeValue() < -2000.0d) {
                        mLowTemp.add(averageLowTemp);
                    } else {
                        mLowTemp.add(Math.min((int) w.getMaxTemperature().getCentigradeValue(), (int) w
                                .getMinTemperature().getCentigradeValue()));
                    }
                } else {
                    ((TextView) dailyWeatherView.findViewById(R.id.day_date)).setText(R.string.na);
                    ((TextView) dailyWeatherView.findViewById(R.id.day)).setText(R.string.na);
                    ((ImageView) dailyWeatherView.findViewById(R.id.forecast_daily_weather_icon)).setImageResource
                            (WeatherResHelper.getWeatherIconResID(0));
                    if (mLowTemp.size() > 0 && arrayList.size() > 0) {
                        mLowTemp.add(mLowTemp.get(mLowTemp.size() - 1));
                        arrayList.add(arrayList.get(arrayList.size() - 1));
                    }
                }
                LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(-1, -1);
                layoutParams.weight = 1.0f;
                forecastLayoutContainer.addView(dailyWeatherView, layoutParams);
            }
            if (data.size() < 6) {
                count = data.size();
            } else {
                count = 6;
            }
            try {
                int highTemp = Collections.max(arrayList);
                int lowTemp = Collections.min(mLowTemp);
                if (highTemp - lowTemp > 45) {
                    int size;
                    int j;
                    if (highTemp - averageHighTemp >= averageLowTemp - lowTemp) {
                        size = arrayList.size();
                        for (j = 0; j < size; j++) {
                            if (highTemp - arrayList.get(j) <= 5) {
                                arrayList.set(j, averageHighTemp);
                            }
                        }
                    } else {
                        size = mLowTemp.size();
                        for (j = 0; j < size; j++) {
                            if (mLowTemp.get(j) - lowTemp <= 5) {
                                mLowTemp.set(j, averageLowTemp);
                            }
                        }
                    }
                }
            } catch (Exception e) {
                Log.e("contentwrapper", "check temp error");
            }
            if (arrayList.size() > 0 && mLowTemp.size() > 0) {
                updateTempView(arrayList, mLowTemp, count);
            }
        }
    }

    private void clickUrl(String url, Context context) {
        try {
            context.startActivity(new Intent("android.intent.action.VIEW", Uri.parse(url)));
        } catch (ActivityNotFoundException e) {
            Toast.makeText(context, context.getString(R.string.browser_not_found), Toast.LENGTH_SHORT).show();
            e.printStackTrace();
        }
    }

    public int getAverageHighTemp(List<DailyForecastsWeather> data) {
        List<Integer> averageHighTemp = new ArrayList<>();
        for (int i = 0; i < data.size(); i++) {
            if (i < 7) {
                DailyForecastsWeather w = data.get(i);
                if (w == null || w.getMaxTemperature() == null) {
                    if (w == null || w.getMinTemperature() == null) {
                        averageHighTemp.add(0);
                    } else {
                        averageHighTemp.add((int) w.getMinTemperature().getCentigradeValue());
                    }
                } else if (w.getMaxTemperature().getCentigradeValue() > -2000.0d) {
                    averageHighTemp.add((int) w.getMaxTemperature().getCentigradeValue());
                }
            }
        }
        int highTempSum = 0;
        for (int j = 0; j < averageHighTemp.size(); j++) {
            highTempSum += averageHighTemp.get(j);
        }
        if (averageHighTemp.size() == 0) {
            return 0;
        }
        return highTempSum / averageHighTemp.size();
    }

    public int getAverageLowTemp(List<DailyForecastsWeather> data) {
        List<Integer> averageLowTemp = new ArrayList<>();
        for (int i = 0; i < data.size(); i++) {
            DailyForecastsWeather w = data.get(i);
            if (w == null || w.getMinTemperature() == null) {
                if (w == null || w.getMinTemperature() == null) {
                    averageLowTemp.add(0);
                } else {
                    averageLowTemp.add((int) w.getMaxTemperature().getCentigradeValue());
                }
            } else if (w.getMinTemperature().getCentigradeValue() > -2000.0d) {
                averageLowTemp.add((int) w.getMinTemperature().getCentigradeValue());
            }
        }
        int lowTempSum = 0;
        for (int j = 0; j < averageLowTemp.size(); j++) {
            lowTempSum += averageLowTemp.get(j);
        }
        if (averageLowTemp.size() == 0) {
            return 0;
        }
        return lowTempSum / averageLowTemp.size();
    }

    private void refreshLocatindLayout(boolean locating) {
        if (content == null) {
            inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            content = (RefreshWeatherUnitView) inflater.inflate(R.layout.weather_info_layout, null);
            content.setOnRefreshUnitListener(this);
        }
        updateRefreshLayout();
    }

    public void updateRefreshLayout() {
        if (mSwipeRefreshLayout == null) {
            mSwipeRefreshLayout = (SwipeRefreshLayout) getChild(R.id.swipeRefreshLayout);
            mSwipeRefreshLayout.setColorSchemeResources(android.R.color.holo_blue_light, android.R.color
                    .holo_red_light, android.R.color.holo_orange_light, android.R.color.holo_green_light);
            mSwipeRefreshLayout.setProgressViewOffset(true, 0, 200);
            mSwipeRefreshLayout.setOnRefreshListener(new OnRefreshListener() {
                @Override
                public void onRefresh() {
                    mLoading = true;
                    new Handler().post(new Runnable() {
                        @Override
                        public void run() {
                            if (NetUtil.isNetworkAvailable(mContext)) {
                                if (mWeatherData != null) {
                                    mToolbar_subTitle.setTextColor(ContextCompat.getColor(mContext, getTitleColor()));
                                }
                                updateWeatherInfo(CacheMode.LOAD_NO_CACHE);
                                WidgetHelper.getInstance(mContext).updateAllWidget(true);
                                return;
                            }
                            if (mSwipeRefreshLayout != null) {
                                mSwipeRefreshLayout.setRefreshing(false);
                            }
                            mLoading = false;
                            Toast.makeText(mContext, mContext.getString(R.string.no_network), Toast.LENGTH_LONG).show();
                        }
                    });
                }
            });
        }
    }

    private int getTitleColor() {
        if (mCityData == null || mCityData.getWeathers() == null || !needGrayColor(WeatherResHelper.weatherToResID
                (mContext, mCityData.getWeathers().getCurrentWeatherId()))) {
            return R.color.oneplus_contorl_text_color_disable_dark;
        }
        return R.color.oneplus_contorl_text_color_disable_light;
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
        ((HourForecastView) getChild(R.id.hourForecastView)).updateForecastData(data.getHourForecastsWeather(), data
                .getDailyForecastsWeather(), mCurrentTemp, timeZone);
    }

    public void updateTempView(ArrayList<Integer> mHighTemp, ArrayList<Integer> mLowTemp, int realCount) {
        ((WeatherTemperatureView) getChild(R.id.weather_temp_view)).initTemp(mHighTemp, mLowTemp, realCount);
    }

    public int getWeatherNightArcColor() {
        int weatherId = 0;
        if (mCityData.getWeathers() != null) {
            weatherId = WeatherResHelper.weatherToResID(mContext, mCityData.getWeathers().getCurrentWeatherId());
        }
        return Color.parseColor(mContext.getString(WeatherResHelper.getWeatherNightArcColorID(weatherId)));
    }

    public void initWeatherScrollView() {
        WeatherScrollView mWeatherScrollView = (WeatherScrollView) getChild(R.id.weather_scrollview);
        FrameLayout mBackground = (FrameLayout) mWeatherScrollView.findViewById(R.id.current_opweather_overlay);
        RelativeLayout.LayoutParams bgParams = (RelativeLayout.LayoutParams) mBackground.getLayoutParams();
        bgParams.height = UIUtil.getWindowHeight(mContext) - ((int) mContext.getResources().getDimension(R.dimen
                .dimen_top_info_view));
        mBackground.setLayoutParams(bgParams);
        mWeatherScrollView.setOverScrollMode(2);
        mWeatherScrollView.setOnTouchListener(new OnTouchListener() {
            float downY = 0.0f;
            float upY = 0.0f;

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                mGestureDetector.onTouchEvent(event);
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        mUp = false;
                        mIsFling = false;
                        downY = event.getY();
                        break;
                    case MotionEvent.ACTION_UP:
                    case MotionEvent.ACTION_CANCEL:
                        upY = event.getY();
                        mUp = true;
                        float moveInstance = upY - downY;
                        if (mIsFling || moveInstance >= 0.0f) {
                            mIsFling = false;
                        } else {
                            doScroll();
                        }
                        downY = 0.0f;
                        upY = 0.0f;
                        break;
                }
                return mGestureDetector.onTouchEvent(event);
            }
        });
        mWeatherScrollView.setScrollViewListener(new WeatherScrollView.ScrollViewListener() {
            @Override
            public void onScrollChanged(WeatherScrollView scrollView, int x, int y, int oldx, int oldy) {
                mOffset = (float) y;
                if (mOffset == 0.0f) {
                    mMoved = false;
                }
                changeTopColor();
            }
        });
    }

    public void changeTopColor() {
        int margin;
        View secondView = getChild(R.id.opweather_info);
        int moveToOffset = (int) secondView.getY();
        RootWeather data = mCityData.getWeathers();
        boolean isChinaCity = true;
        if (data != null) {
            isChinaCity = data.isFromChina();
        }
        if (isChinaCity) {
            changeTopViewTextColor(moveToOffset);
        } else {
            changeTopViewTextColor(1000);
        }
        if (mOffset > 0.0f) {
            float dy = ((float) moveToOffset) - mOffset;
            if (dy < 0.0f) {
                dy = 0.0f;
            }
            float alpha = dy / ((float) moveToOffset);
            if (mUIListener != null) {
                mUIListener.onScrollViewChange(alpha);
            }
        }
        int marginMove = (int) ((((float) secondView.getHeight()) * mOffset) / ((float) moveToOffset));
        if (marginMove > secondView.getHeight()) {
            margin = secondView.getHeight();
        } else {
            margin = marginMove;
        }
        getChild(R.id.current_opweather_overlay).scrollTo(0, margin * 2);
    }

    public void resetScrollView() {
        mOffset = 0.0f;
        content.findViewById(R.id.weather_scrollview).scrollTo(0, 0);
        changeTopViewTextColor(10);
    }

    public void changeTopViewTextColor(int moveToOffset) {
        int moveTo = (int) getChild(R.id.opweather_info).getY();
        if (mCityData.getWeathers() == null) {
            changePathMenuResource(false, mLoading);
            return;
        }
        int weatherId = WeatherResHelper.weatherToResID(mContext, mCityData.getWeathers().getCurrentWeatherId());
        if (weatherId == WeatherDescription.WEATHER_DESCRIPTION_UNKNOWN) {
            weatherId = WeatherResHelper.weatherToResID(mContext, cacheWeatherID);
        }
        if (!needGrayColor(weatherId)) {
            changePathMenuResource(false, mLoading);
        } else if (mOffset >= ((float) moveToOffset)) {
            changePathMenuResource(false, mLoading);
        } else {
            changePathMenuResource(true, mLoading);
        }
    }

    public void resetTopViewTextColor() {
        if (mCityData.getWeathers() != null) {
            int moveTo;
            int weatherId = WeatherResHelper.weatherToResID(mContext, mCityData.getWeathers().getCurrentWeatherId());
            if (weatherId == WeatherDescription.WEATHER_DESCRIPTION_UNKNOWN) {
                weatherId = WeatherResHelper.weatherToResID(mContext, cacheWeatherID);
            }
            View secondView = getChild(R.id.opweather_info);
            RootWeather data = mCityData.getWeathers();
            boolean isChinaCity = true;
            if (data != null) {
                isChinaCity = data.isFromChina();
            }
            if (isChinaCity) {
                moveTo = (int) secondView.getY();
            } else {
                moveTo = 1000;
            }
            if (mOffset >= ((float) moveTo)) {
                return;
            }
            if (needGrayColor(weatherId)) {
                changePathMenuResource(true, mLoading);
            } else {
                changePathMenuResource(false, mLoading);
            }
        }
    }

    public boolean needGrayColor(int weatherId) {
        RootWeather data = mCityData.getWeathers();
        boolean isDay = true;
        if (data != null) {
            try {
                isDay = mCityData.isDay(data);
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        }
        if (weatherId == 1003 && isDay) {
            return true;
        }
        return false;
    }

    public void doScroll() {
        View firstView = getChild(R.id.current_opweather_overlay);
        View secondView = getChild(R.id.opweather_info);
        int moveToOffset = (int) secondView.getY();
        mNeedMoveOffset = (firstView.getHeight() - secondView.getHeight()) / 5;
        if (mOffset <= ((float) mNeedMoveOffset) || mOffset >= ((float) (mNeedMoveOffset * 5))) {
            if (mOffset <= ((float) mNeedMoveOffset)) {
                startScroll(0);
                mMoved = false;
            }
        } else if (!mMoved && mUp) {
            startScroll(moveToOffset);
            mMoved = true;
        }
    }

    public void startScroll(final int offset) {
        mScrollHandler.post(new Runnable() {
            public void run() {
                ((ScrollView) getChild(R.id.weather_scrollview)).smoothScrollTo(0, offset);
            }
        });
    }

    public int getWeatherColor(Context context, int weatherId, boolean isDay) {
        return context.getResources().getColor(WeatherResHelper.getWeatherColorStringID(weatherId, isDay));
    }

    public boolean isDay() {
        boolean isDay = true;
        if (mWeatherData == null) {
            return isDay;
        }
        try {
            return mCityData.isDay(mWeatherData);
        } catch (IllegalAccessException e) {
            e.printStackTrace();
            return isDay;
        }
    }

    public void onScrolled(float a, int position) {
        if (index >= position - 1 && index <= position + 1) {
            changeTopColor();
            float alpha = a;
            if (index == position) {
                if (a < 1.0f && ((double) a) >= 0.5d) {
                    alpha = (2.0f * a) - 1.0f;
                } else if (((double) a) < 0.5d) {
                }
            } else if (index < position) {
                alpha = (1.0f - a) * 2.0f;
            } else if (index > position) {
                float a1 = a * 2.0f;
                if (a <= 0.5f) {
                    alpha = 1.0f - a1;
                }
            }
            resetTopViewTextColor();
        }
    }

    private void changePathMenuResource(boolean isBlack, boolean isLoading) {
        if (mUIListener != null) {
            mUIListener.ChangePathMenuResource(index, isBlack, isLoading);
        }
    }

    @Override
    public void onSelected(int position) {
        if (index >= position - 1 && index <= position + 1) {
            resetScrollView();
            content.findViewById(R.id.weather_scrollview).scrollTo(0, 0);
        }
    }

    @Override
    public void refreshUnit() {
        updateCurrentWeatherUI();
    }
}
