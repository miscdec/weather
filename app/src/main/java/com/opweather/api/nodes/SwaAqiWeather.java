package com.opweather.api.nodes;

import com.opweather.api.helper.DateUtils;
import com.opweather.api.helper.NumberUtils;
import com.opweather.api.impl.SwaRequest;

import java.util.Date;

public class SwaAqiWeather extends AqiWeather {
    private int mAqiValue;
    private Date mDate;
    private int mPm2_5;

    public SwaAqiWeather(String areaCode, String areaName, String dataSource) {
        super(areaCode, areaName, dataSource);
        mPm2_5 = Integer.MIN_VALUE;
        mAqiValue = Integer.MIN_VALUE;
    }

    public int getAqiValue() {
        return mAqiValue;
    }

    public Date getPublicDate() {
        return mDate;
    }

    public int getPm2_5Value() {
        return mPm2_5;
    }

    public static SwaAqiWeather newInstance(String areaCode, String time, String pm2_5, String aqi) {
        SwaAqiWeather aqiWeather = new SwaAqiWeather(areaCode, null, SwaRequest.DATA_SOURCE_NAME);
        aqiWeather.mDate = DateUtils.parseSwaAqiDate(time);
        aqiWeather.mAqiValue = NumberUtils.valueToInt(aqi);
        aqiWeather.mPm2_5 = NumberUtils.valueToInt(pm2_5);
        return (NumberUtils.isNaN(aqiWeather.mAqiValue) || NumberUtils.isNaN(aqiWeather.mPm2_5) || aqiWeather.mDate
                == null) ? null : aqiWeather;
    }
}
