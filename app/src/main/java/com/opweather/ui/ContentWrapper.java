package com.opweather.ui;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.location.LocationListener;
import android.net.Uri;
import android.os.Build;
import android.os.Build.VERSION;
import android.os.Handler;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.AutoScrollHelper;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v4.widget.SwipeRefreshLayout.OnRefreshListener;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.support.v7.widget.helper.ItemTouchHelper.Callback;
import android.text.TextUtils;
import android.util.Log;
import android.view.GestureDetector;
import android.view.GestureDetector.SimpleOnGestureListener;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.ViewGroup.MarginLayoutParams;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout.LayoutParams;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.opweather.R;
import com.opweather.api.WeatherException;
import com.opweather.api.nodes.CurrentWeather;
import com.opweather.api.nodes.DailyForecastsWeather;
import com.opweather.api.nodes.RootWeather;
import com.opweather.bean.CityData;
import com.opweather.constants.GlobalConfig;
import com.opweather.db.CityWeatherDB;
import com.opweather.provider.LocationProvider;
import com.opweather.provider.LocationProvider.OnLocationListener;
import com.opweather.ui.MainActivity.OnViewPagerScrollListener;
import com.opweather.util.DateTimeUtils;
import com.opweather.util.NetUtil;
import com.opweather.util.PermissionUtil;
import com.opweather.util.StringUtils;
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
import com.opweather.widget.WeatherScrollView.ScrollViewListener;
import com.opweather.widget.WeatherSingleInfoView;
import com.opweather.widget.WeatherTemperatureView;
import com.opweather.widget.openglbase.RainSurfaceView;
import com.opweather.widget.widget.WidgetHelper;

import java.util.ArrayList;
import java.util.List;

public class ContentWrapper implements OnViewPagerScrollListener, OnRefreshUnitListener, OnLocationListener {
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
    private boolean mHasLocation;
    private boolean mIsFling;
    private boolean mLoading;
    private LocationProvider mLocationProvider;
    private boolean mMoved;
    private int mNeedMoveOffset;
    private float mOffset;
    private OnLocationListener mOnLocationListener;
    private OnResponseListener mOnResponseListener;
    Handler mScrollHandler;
    private boolean mSuccess;
    private SwipeRefreshLayout mSwipeRefreshLayout;
    private TextView mToolbar_subTitle;
    private OnUIChangedListener mUIListener;
    private boolean mUp;
    private RootWeather mWeatherData;

    @Override
    public void onError(int i) {
        if (mOnLocationListener != null) {
            mOnLocationListener.onError(i);
        }
    }

    @Override
    public void onLocationChanged(CityData cityData) {
        if (mOnLocationListener != null) {
            mOnLocationListener.onLocationChanged(cityData);
        }
    }

    public interface OnUIChangedListener {
        void ChangePathMenuResource(int i, boolean z, boolean z2);

        void onChangedCurrentWeather();

        void onError();

        void onScrollViewChange(float f);

        void onWeatherDataUpdate(int i);
    }

    class ScrollViewGestureListener extends SimpleOnGestureListener {
        ScrollViewGestureListener() {
        }

        public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
            return false;
        }

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
        return index;
    }

    public void setOnUIChangedListener(OnUIChangedListener mUIListener) {
        this.mUIListener = mUIListener;
    }

    public ContentWrapper(Context context, CityData city, OnResponseListener l, TextView textView) {
        mMoved = false;
        mUp = false;
        mOffset = 0.0f;
        mIsFling = false;
        mHasLocation = false;
        mScrollHandler = new Handler();
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
        return mCityData != null ? mCityData.isLocatedCity() : false;
    }

    public RootWeather getCityWeather() {
        return mCityData != null ? mCityData.getWeathers() : null;
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
            public void onCacheResponse(RootWeather rootWeather) {
                mSuccess = true;
                mLoading = false;
                mCityData.setWeathers(rootWeather);
                mWeatherData = rootWeather;
                refreshLocatindLayout(false);
                updateCurrentWeatherUI();
                if (mSwipeRefreshLayout != null && mSwipeRefreshLayout
                        .isRefreshing()) {
                    mSwipeRefreshLayout.setRefreshing(false);
                }
                if (!(rootWeather == null || mOnResponseListener == null)) {
                    mOnResponseListener.onCacheResponse(rootWeather);
                }
                if (mUIListener != null) {
                    mUIListener.onWeatherDataUpdate(index);
                }
                if (!(rootWeather == null || rootWeather.getCurrentWeather() == null)) {
                    cacheWeatherID = rootWeather.getCurrentWeather().getWeatherId();
                }
                if (mCityData.isLocatedCity()) {
                    SystemSetting.notifyWeatherDataChange(mContext.getApplicationContext());
                    SystemSetting.setLocale(mContext);
                }
            }

            @Override
            public void onErrorResponse(WeatherException weatherException) {
                mSuccess = false;
                mLoading = false;
                refreshLocatindLayout(false);
                updateRefreshLayout();
                updateCurrentWeatherUI();
                if (mSwipeRefreshLayout != null && mSwipeRefreshLayout.isRefreshing()) {
                    mSwipeRefreshLayout.setRefreshing(false);
                }
                if (mOnResponseListener != null) {
                    mOnResponseListener.onErrorResponse(weatherException);
                }
            }

            @Override
            public void onNetworkResponse(RootWeather rootWeather) {
                mLoading = false;
                SystemSetting.setRefreshTime(mContext, city.getLocationId(), System.currentTimeMillis());
                mSuccess = true;
                mCityData.setWeathers(rootWeather);
                if (rootWeather != null) {
                    cacheWeatherID = rootWeather.getCurrentWeatherId();
                    mWeatherData = rootWeather;
                }
                refreshLocatindLayout(false);
                updateCurrentWeatherUI();
                CityWeatherDB.getInstance(mContext).updateLastRefreshTime(mCityData.getLocationId(), DateTimeUtils
                        .longTimeToRefreshTime(mContext, System.currentTimeMillis()));
                System.out.println("LastRefreshTime:" + CityWeatherDB.getInstance(mContext)
                        .getLastRefreshTime(mCityData.getLocationId()));
                if (mSwipeRefreshLayout != null && mSwipeRefreshLayout
                        .isRefreshing()) {
                    mSwipeRefreshLayout.setRefreshing(false);
                }
                if (mOnResponseListener != null) {
                    mOnResponseListener.onNetworkResponse(rootWeather);
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
                if (PermissionUtil.check((Activity) mContext, new String[]{"android.permission" +
                        ".ACCESS_FINE_LOCATION", "android.permission.WRITE_EXTERNAL_STORAGE"}, 1)) {
                    mHasLocation = true;
                    if (mLocationProvider != null) {
                        mLocationProvider.stopLocation();
                        mLocationProvider = null;
                    }
                    mLocationProvider = new LocationProvider(mContext);
                    setOnLocationListener(this);
                    mLocationProvider.setOnLocationListener(new OnLocationListener() {
                        @Override
                        public void onError(int error) {
                            Log.i("1111", "onLocationChanged onError");
                            if (mOnLocationListener != null) {
                                mOnLocationListener.onError(error);
                            }
                            mHasLocation = false;
                            requestWeather(mCityData, mode);
                            if (mOnResponseListener != null) {
                                mOnResponseListener.onErrorResponse(new WeatherException("location error"));
                            }
                            if (mUIListener != null) {
                                mUIListener.onError();
                            }
                            changePathMenuResource(false, mLoading);
                        }

                        @Override
                        public void onLocationChanged(CityData cityData) {
                            Log.i("1111", "onLocationChanged name = " + cityData.getLocalName());
                            if (mOnLocationListener != null) {
                                mOnLocationListener.onLocationChanged(cityData);
                            }
                            mCityData.copy(cityData);
                            CityWeatherDB.getInstance(mContext).addCurrentCity(mCityData);
                            requestWeather(mCityData, mode);
                        }
                    });
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
        ((WeatherSingleInfoView) getChild(resId)).setInfoType(title).setInfoLevel(value).setInfoIcon(iconId);
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
            mlp.setMargins(Callback.DEFAULT_SWIPE_ANIMATION_DURATION, 170, 0, 0);
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
        return (Build.PRODUCT.equals("A0001") && VERSION.RELEASE.equals("4.3")) ? -(heightDp + 20) : -(heightDp - 10);
    }

    public void updateCurrentWeatherUI() {
        if (mCityData != null) {
            Log.e(TAG, "updateCurrentWeatherUI: " + mCityData.getLocalName());
            changeTopViewTextColor(10);
            updateRefreshLayout();
            System.out.println("mWeatherInfoView.getHeight():" + UIUtil.px2dip(mContext, (float) getChild(R.id
                    .opweather_info).getHeight()));
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
                    setText(R.id.current_hight_temperature, TemperatureUtil.getHighTemperature(mContext, highTemp)
                            .replace("°", StringUtils.EMPTY_STRING));
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
                    String str = "--";
                    if (humidity < -2000) {
                        str = "--" + humUnit;
                    } else {
                        str = humidity + humUnit;
                    }
                    if (isChinaCity) {
                        getChild(R.id.opweather_detail).setVisibility(View.VISIBLE);
                        setIndexValue(R.id.single_humidity_view, str);
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

    /* JADX WARNING: inconsistent code. */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    private void updateForecastWeatherUI(List<DailyForecastsWeather> r37_data, String
            r38_timeZone) {
        throw new UnsupportedOperationException("Method not decompiled: weather.app.ContentWrapper" +
                ".updateForecastWeatherUI(java.util.List, java.lang.String):void");
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
        List<Integer> averageHighTemp = new ArrayList();
        for (int i = 0; i < data.size(); i++) {
            if (i < 7) {
                DailyForecastsWeather w = (DailyForecastsWeather) data.get(i);
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
        return averageHighTemp.size() == 0 ? 0 : highTempSum / averageHighTemp.size();
    }

    public int getAverageLowTemp(List<DailyForecastsWeather> data) {
        List<Integer> averageLowTemp = new ArrayList<>();
        for (int i = 0; i < data.size(); i++) {
            DailyForecastsWeather w = (DailyForecastsWeather) data.get(i);
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
        return averageLowTemp.size() == 0 ? 0 : lowTempSum / averageLowTemp.size();
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
            mSwipeRefreshLayout.setProgressViewOffset(true, 0, GlobalConfig.MESSAGE_ACCU_GET_LOCATION_SUCC);
            mSwipeRefreshLayout.setOnRefreshListener(new OnRefreshListener() {
                public void onRefresh() {
                    mLoading = true;
                    new Handler().post(new Runnable() {
                        public void run() {
                            if (NetUtil.isNetworkAvailable(mContext)) {
                                if (mWeatherData != null) {
                                    mToolbar_subTitle.setTextColor(ContextCompat.getColor
                                            (mContext, getTitleColor()));
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
        return (mCityData == null || mCityData.getWeathers() == null || !needGrayColor(WeatherResHelper
                .weatherToResID(mContext, mCityData.getWeathers().getCurrentWeatherId()))) ? R.color
                .oneplus_contorl_text_color_disable_dark : R.color.oneplus_contorl_text_color_disable_light;
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

    @SuppressLint("ClickableViewAccessibility")
    public void initWeatherScrollView() {
        WeatherScrollView mWeatherScrollView = (WeatherScrollView) getChild(R.id.weather_scrollview);
        FrameLayout mBackground = (FrameLayout) mWeatherScrollView.findViewById(R.id.current_opweather_overlay);
        LayoutParams bgParams = (LayoutParams) mBackground.getLayoutParams();
        bgParams.height = UIUtil.getWindowHeight(mContext) - ((int) mContext.getResources().getDimension(R
                .dimen.dimen_top_info_view));
        mBackground.setLayoutParams(bgParams);
        mWeatherScrollView.setOverScrollMode(RainSurfaceView.RAIN_LEVEL_SHOWER);
        mWeatherScrollView.setOnTouchListener(new OnTouchListener() {
            float downY;
            float upY;

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                mGestureDetector.onTouchEvent(event);
                switch (event.getAction()) {
                    case RainSurfaceView.RAIN_LEVEL_DRIZZLE:
                        mUp = false;
                        mIsFling = false;
                        downY = event.getY();
                        break;
                    case RainSurfaceView.RAIN_LEVEL_NORMAL_RAIN:
                    case RainSurfaceView.RAIN_LEVEL_DOWNPOUR:
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
        mWeatherScrollView.setScrollViewListener(new ScrollViewListener() {
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
                dy = AutoScrollHelper.RELATIVE_UNSPECIFIED;
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
        int y = (int) getChild(R.id.opweather_info).getY();
        if (mCityData.getWeathers() == null) {
            changePathMenuResource(false, mLoading);
            return;
        }
        int weatherId = WeatherResHelper.weatherToResID(mContext, mCityData.getWeathers()
                .getCurrentWeatherId());
        if (weatherId == 9999) {
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
            int weatherId = WeatherResHelper.weatherToResID(mContext, mCityData.getWeathers()
                    .getCurrentWeatherId());
            if (weatherId == 9999) {
                weatherId = WeatherResHelper.weatherToResID(mContext, cacheWeatherID);
            }
            View secondView = getChild(R.id.opweather_info);
            RootWeather data = mCityData.getWeathers();
            boolean isChinaCity = true;
            if (data != null) {
                isChinaCity = data.isFromChina();
            }
            if (mOffset >= ((float) (isChinaCity ? (int) secondView.getY() : 1000))) {
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
        return weatherId == 1003 && isDay;
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
            @Override
            public void run() {
                ((ScrollView) getChild(R.id.weather_scrollview)).smoothScrollTo(0, offset);
            }
        });
    }

    public int getWeatherColor(Context context, int weatherId, boolean isDay) {
        return context.getResources().getColor(WeatherResHelper.getWeatherColorStringID(weatherId, isDay));
    }

    public boolean isDay() {
        if (mWeatherData == null) {
            return true;
        }
        try {
            return mCityData.isDay(mWeatherData);
        } catch (IllegalAccessException e) {
            e.printStackTrace();
            return true;
        }
    }

    @Override
    public void onScrolled(float a, int position) {
        if (index >= position - 1 && index <= position + 1) {
            changeTopColor();
            float f = a;
            if (index == position) {
                if (a < 1.0f && ((double) a) >= 0.5d) {
                    f = (2.0f * a) - 1.0f;
                } else if (((double) a) < 0.5d) {
                }
            } else if (index < position) {
                f = (1.0f - a) * 2.0f;
            } else if (index > position) {
                float a1 = a * 2.0f;
                if (a <= 0.5f) {
                    f = 1.0f - a1;
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
            ((ScrollView) content.findViewById(R.id.weather_scrollview)).scrollTo(0, 0);
        }
    }

    @Override
    public void refreshUnit() {
        updateCurrentWeatherUI();
    }
}
