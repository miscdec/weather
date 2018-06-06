package com.opweather.api.nodes;

import android.content.Context;

import com.opweather.R;
import com.opweather.api.helper.StringUtils;
import com.opweather.api.helper.WeatherUtils;
import com.opweather.util.DateTimeUtils;
import com.opweather.widget.openglbase.RainSurfaceView;

import java.util.Date;

public class OppoChinaCurrentWeather extends CurrentWeather {
    private final Date mObservationDate;
    private final int mRelativeHumidity;
    private final Temperature mTemperature;
    private final int mUVIndex;
    private final String mUVIndexText;
    private final int mWeatherId;
    private String mWeatherText;
    private final Wind mWind;

    public OppoChinaCurrentWeather(String areaCode, String dataSource, int weatherId, Date observationDate,
                                   Temperature temperature, int relativeHumidity, Wind wind, int UVIndex, String
                                           UVIndexText) {
        this(areaCode, null, dataSource, weatherId, observationDate, temperature, relativeHumidity, wind, UVIndex,
                UVIndexText);
    }

    public OppoChinaCurrentWeather(String areaCode, String areaName, String dataSource, int weatherId, Date
            observationDate, Temperature temperature, int relativeHumidity, Wind wind, int UVIndex, String
                                           UVIndexText) {
        super(areaCode, areaName, dataSource);
        mWeatherId = weatherId;
        mObservationDate = observationDate;
        mTemperature = temperature;
        mRelativeHumidity = relativeHumidity;
        mWind = wind;
        mUVIndex = UVIndex;
        mUVIndexText = UVIndexText;
    }

    public String getWeatherName() {
        return "Oppo China Current Weather";
    }

    public Date getObservationDate() {
        return mObservationDate;
    }

    public int getWeatherId() {
        return mWeatherId;
    }

    public String getLocalTimeZone() {
        return DateTimeUtils.CHINA_OFFSET;
    }

    public String getWeatherText(Context context) {
        return WeatherUtils.getOppoChinaWeatherTextById(context, mWeatherId);
    }

    public Temperature getTemperature() {
        return mTemperature;
    }

    public int getRelativeHumidity() {
        return mRelativeHumidity;
    }

    public Wind getWind() {
        return mWind;
    }

    public int getUVIndex() {
        return mUVIndex;
    }

    public String getUVIndexText() {
        return mUVIndexText;
    }

    public String getMainMoblieLink() {
        return null;
    }

    public String getUVIndexInternationalText(Context context) {
        String simpleValue = getUVIndexText();
        String result = StringUtils.EMPTY_STRING;
        if (StringUtils.isBlank(simpleValue)) {
            return result;
        }
        if (simpleValue.equals("\u5f88\u5f31") || simpleValue.equals("\u6700\u5f31")) {
            return context.getString(R.string.ultraviolet_index_level_one);
        } else if (simpleValue.equals("\u5f31") || simpleValue.equals("\u8f83\u5f31")) {
            return context.getString(R.string.ultraviolet_index_level_two);
        } else if (simpleValue.equals("\u4e2d\u7b49")) {
            return context.getString(R.string.ultraviolet_index_level_three);
        } else if (simpleValue.equals("\u5f3a") || simpleValue.equals("\u8f83\u5f3a")) {
            return context.getString(R.string.ultraviolet_index_level_four);
        } else if (simpleValue.equals("\u6700\u5f3a") || simpleValue.equals("\u5f88\u5f3a")) {
            return context.getString(R.string.ultraviolet_index_level_five);
        } else {
            return result;
        }
    }
}
