package com.opweather.api.nodes;

import android.content.Context;

import com.opweather.R;
import com.opweather.api.helper.StringUtils;


public class OppoWind extends Wind {
    private final String mWindPower;

    public OppoWind(String areaCode, String dataSource, Direction direction, String windPower) {
        super(areaCode, dataSource, direction);
        this.mWindPower = windPower;
    }

    public OppoWind(String areaCode, String areaName, String dataSource, Direction direction, String windPower) {
        super(areaCode, areaName, dataSource, direction);
        this.mWindPower = windPower;
    }

    public String getWindPower() {
        return this.mWindPower;
    }

    public String getWindPower(Context context) {
        return toInternationalWind(context);
    }

    private String toInternationalWind(Context context) {
        if (StringUtils.isBlank(mWindPower)) {
            return StringUtils.EMPTY_STRING;
        }
        String str = mWindPower;
        if (str.equals("0级") || str.equals("1级") || str.equals("2级") || str.equals("3级") || str.equals("微风")) {
            return context.getString(R.string.api_wind_power_level_zero);
        } else if (str.equals("4级") || str.equals("3-4级")) {
            return context.getString(R.string.api_wind_power_level_one);
        } else if (str.equals("5级") || str.equals("4-5级")) {
            return context.getString(R.string.api_wind_power_level_two);
        } else if (str.equals("6级") || str.equals("5-6级")) {
            return context.getString(R.string.api_wind_power_level_three);
        } else if (str.equals("7级") || str.equals("6-7级")) {
            return context.getString(R.string.api_wind_power_level_four);
        } else if (str.equals("8级") || str.equals("7-8级")) {
            return context.getString(R.string.api_wind_power_level_five);
        } else if (str.equals("9级") || str.equals("8-9级")) {
            return context.getString(R.string.api_wind_power_level_six);
        } else if (str.equals("10级") || str.equals("9-10级")) {
            return context.getString(R.string.api_wind_power_level_seven);
        } else if (str.equals("11级") || str.equals("10-11级")) {
            return context.getString(R.string.api_wind_power_level_eight);
        } else if (str.equals("12级") || str.equals("11-12级")) {
            return context.getString(R.string.api_wind_power_level_nine);
        } else {
            return this.mWindPower;
        }
    }
}
