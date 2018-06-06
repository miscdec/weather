package com.opweather.api.nodes;

import android.content.Context;

import com.opweather.R;
import com.opweather.api.helper.WeatherUtils;
import com.opweather.api.helper.StringUtils;
import com.opweather.widget.openglbase.RainSurfaceView;

import java.util.Date;

public class OppoChinaDailyForecastsWeather extends DailyForecastsWeather {
    private static String DEFAULT_INDEX_TEXT = StringUtils.EMPTY_STRING;
    private final Temperature mBodytemp;
    private final String mClothingValue;
    private final Date mDate;
    private final int mDayWeatherId;
    private String mDayWeatherText;
    private final Temperature mMaxTemperature;
    private final Temperature mMinTemperature;
    private final String mMobileLink;
    private final int mNightWeatherId;
    private String mNightWeatherText;
    private final int mPressure;
    private final String mSportsValue;
    private final Sun mSun;
    private final int mVisibility;
    private final String mWashcarValue;

    public OppoChinaDailyForecastsWeather(String areaCode, String dataSource, int dayWeatherId, int nightWeatherId,
                                          Date date, Temperature minTemperature, Temperature maxTemperature, Sun sun,
                                          String sportsValue, String washcarValue, String clothingValue, String
                                                  mobileLink) {
        this(areaCode, null, dataSource, dayWeatherId, nightWeatherId, date, minTemperature, maxTemperature, sun,
                sportsValue, washcarValue, clothingValue, mobileLink);
    }

    public OppoChinaDailyForecastsWeather(String areaCode, String areaName, String dataSource, int dayWeatherId, int
            nightWeatherId, Date date, Temperature minTemperature, Temperature maxTemperature, Sun sun, String
                                                  sportsValue, String washcarValue, String clothingValue, String
                                                  mobileLink) {
        this(areaCode, areaName, dataSource, dayWeatherId, nightWeatherId, date, minTemperature, maxTemperature, sun,
                sportsValue, washcarValue, clothingValue, null, Integer.MIN_VALUE, Integer.MIN_VALUE, mobileLink);
    }

    public OppoChinaDailyForecastsWeather(String areaCode, String areaName, String dataSource, int dayWeatherId, int
            nightWeatherId, Date date, Temperature minTemperature, Temperature maxTemperature, Sun sun, Temperature
                                                  bodytemp, int pressure, int visibility, String mobileLink) {
        this(areaCode, areaName, dataSource, dayWeatherId, nightWeatherId, date, minTemperature, maxTemperature, sun,
                null, null, null, bodytemp, pressure, visibility, mobileLink);
    }

    public OppoChinaDailyForecastsWeather(String areaCode, String areaName, String dataSource, int dayWeatherId, int
            nightWeatherId, Date date, Temperature minTemperature, Temperature maxTemperature, Sun sun, String
                                                  sportsValue, String washcarValue, String clothingValue, Temperature
                                                  bodytemp, int pressure, int
                                                  visibility, String mobileLink) {
        super(areaCode, areaName, dataSource);
        mDayWeatherId = dayWeatherId;
        mNightWeatherId = nightWeatherId;
        mDate = date;
        mMinTemperature = minTemperature;
        mMaxTemperature = maxTemperature;
        mSun = sun;
        mSportsValue = sportsValue;
        mWashcarValue = washcarValue;
        mClothingValue = clothingValue;
        mBodytemp = bodytemp;
        mPressure = pressure;
        mVisibility = visibility;
        mMobileLink = mobileLink;
    }

    public String getWeatherName() {
        return "Oppo China Daily Forecasts Weather";
    }

    public Date getDate() {
        return mDate;
    }

    public int getDayWeatherId() {
        return mDayWeatherId;
    }

    public String getMobileLink() {
        return mMobileLink;
    }

    public String getDayWeatherText(Context context) {
        if (mDayWeatherText == null) {
            mDayWeatherText = WeatherUtils.getOppoChinaWeatherTextById(context, mDayWeatherId);
        }
        return mDayWeatherText;
    }

    public int getNightWeatherId() {
        return mNightWeatherId;
    }

    public String getNightWeatherText(Context context) {
        if (mNightWeatherText == null) {
            mNightWeatherText = WeatherUtils.getOppoChinaWeatherTextById(context, mNightWeatherId);
        }
        return mNightWeatherText;
    }

    public Temperature getMinTemperature() {
        return mMinTemperature;
    }

    public Temperature getMaxTemperature() {
        return mMaxTemperature;
    }

    public Sun getSun() {
        return mSun;
    }

    public Temperature getBodytemp() {
        return mBodytemp;
    }

    public int getPressure() {
        return mPressure;
    }

    public String getPressureTransformSimlpeValue(Context mContext) {
        return mPressure + StringUtils.EMPTY_STRING + mContext.getString(R.string.api_pressure);
    }

    public int getVisibility() {
        return mVisibility;
    }

    public String getVisibilityTransformSimlpeValue(Context mContext) {
        try {
            return mVisibility >= 1000 ? (mVisibility / 1000) + mContext.getString(R.string.api_km) : this
                    .mVisibility + mContext.getString(R.string.api_m);
        } catch (IllegalArgumentException e) {
            return mVisibility + mContext.getString(R.string.api_m);
        }
    }

    public String getSportsValue() {
        return mSportsValue;
    }

    public String getWashcarValue() {
        return mWashcarValue;
    }

    public String getClothingValue() {
        return mClothingValue;
    }

    public String getSportsTransformSimlpeValue(Context context) {
        String simpleValue = toSimple(mSportsValue);
        String result = DEFAULT_INDEX_TEXT;
        if (StringUtils.isBlank(simpleValue)) {
            return result;
        }
        if (simpleValue.equals("适宜")) {
            return context.getString(R.string.motion_index_level_one);
        } else if (simpleValue.equals("较适宜")) {
            return context.getString(R.string.motion_index_level_two);
        } else if (simpleValue.equals("较不宜")) {
            return context.getString(R.string.motion_index_level_three);
        } else if (simpleValue.equals("不宜")) {
            return context.getString(R.string.motion_index_level_four);
        } else {
            return result;
        }
    }

    public String getCarwashTransformSimlpeValue(Context context) {
        String simpleValue = toSimple(mWashcarValue);
        String result = DEFAULT_INDEX_TEXT;
        if (StringUtils.isBlank(simpleValue)) {
            return result;
        }
        if (simpleValue.equals("适宜")) {
            return context.getString(R.string.carwash_index_level_one);
        } else if (simpleValue.equals("较适宜")) {
            return context.getString(R.string.carwash_index_level_two);
        } else if (simpleValue.equals("较不宜")) {
            return context.getString(R.string.carwash_index_level_three);
        } else if (simpleValue.equals("不宜")) {
            return context.getString(R.string.carwash_index_level_four);
        } else {
            return result;
        }
    }

    public String getClothingTransformSimlpeValue(Context context) {
        String simpleValue = toSimple(mClothingValue);
        String result = DEFAULT_INDEX_TEXT;
        if (StringUtils.isBlank(simpleValue)) {
            return result;
        }

        if (simpleValue.equals("冷")) {
            return context.getString(R.string.dress_index_scarf);
        } else if (simpleValue.equals("热")) {
            return context.getString(R.string.dress_index_short_sleeve);
        } else if (simpleValue.equals("寒冷")) {
            return context.getString(R.string.dress_index_earmuff);
        } else if (simpleValue.equals("炎热")) {
            return context.getString(R.string.dress_index_waistcoat);
        } else if (simpleValue.equals("舒适")) {
            return context.getString(R.string.dress_index_fleece);
        } else if (simpleValue.equals("较冷")) {
            return context.getString(R.string.dress_index_sweater);
        } else if (simpleValue.equals("较舒适")) {
            return context.getString(R.string.dress_index_jacket);
        } else {
            return result;
        }
    }

    public static String toSimple(String value) {
        if (StringUtils.isBlank(value)) {
            return DEFAULT_INDEX_TEXT;
        }
        String[] sp = value.split("\u3002");
        return sp.length == 0 ? DEFAULT_INDEX_TEXT : sp[0];
    }
}
