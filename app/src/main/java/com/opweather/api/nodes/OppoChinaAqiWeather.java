package com.opweather.api.nodes;

import android.content.Context;

import com.opweather.R;
import com.opweather.api.helper.StringUtils;

public class OppoChinaAqiWeather extends AqiWeather {
    private static String DEFAULT_INDEX_TEXT = "";
    public String mAqiType;
    private final int mAqiValue;
    public int mPM25_Value;

    public OppoChinaAqiWeather(String areaCode, String dataSource, int aqiValue) {
        this(areaCode, null, dataSource, aqiValue);
    }

    public OppoChinaAqiWeather(String areaCode, String areaName, String dataSource, int aqiValue) {
        this(areaCode, areaName, dataSource, aqiValue, Integer.MIN_VALUE, null);
    }

    public OppoChinaAqiWeather(String areaCode, String areaName, String dataSource, int aqiValue, int pm25Value,
                               String aqi) {
        super(areaCode, areaName, dataSource);
        mAqiValue = aqiValue;
        mPM25_Value = pm25Value;
        mAqiType = aqi;
    }

    public String getWeatherName() {
        return "Oppo China Aqi Weather";
    }

    public int getAqiValue() {
        return mAqiValue;
    }

    public int getPM25_Value() {
        return mPM25_Value;
    }

    public String getAqiType() {
        return mAqiType;
    }

    public String getAqiTypeTransformSimlpe(Context context) {
        String str = DEFAULT_INDEX_TEXT;
        if (StringUtils.isBlank(mAqiType)) {
            return context.getString(R.string.api_aqi_level_na);
        }
        String str2 = mAqiType;
        if (str2.equals("优")) {
            return context.getString(R.string.api_aqi_level_one);
        } else if (str2.equals("良")) {
            return context.getString(R.string.api_aqi_level_two);
        } else if (str2.equals("轻度污染")) {
            return context.getString(R.string.api_aqi_level_three);
        } else if (str2.equals("中度污染")) {
            return context.getString(R.string.api_aqi_level_four);
        } else if (str2.equals("重度污染")) {
            return context.getString(R.string.api_aqi_level_five);
        } else if (str2.equals("严重污染")) {
            return context.getString(R.string.api_aqi_level_six);
        } else {
            return mAqiType;
        }
    }
}
