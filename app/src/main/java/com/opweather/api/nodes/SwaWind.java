package com.opweather.api.nodes;

import android.content.Context;

import com.opweather.R;
import com.opweather.api.impl.SwaRequest;

public class SwaWind extends Wind {
    private final String mWindPower;

    public SwaWind(String areaCode, String direction, String power) {
        super(areaCode, SwaRequest.DATA_SOURCE_NAME, toSwaDirection(direction));
        this.mWindPower = power;
    }

    public String getWindPower(Context context) {
        String str = mWindPower;
        if (str.equals("0")) {
            return context.getString(R.string.api_wind_power_level_zero);
        } else if (str.equals("1")) {
            return context.getString(R.string.api_wind_power_level_one);
        } else if (str.equals("2")) {
            return context.getString(R.string.api_wind_power_level_two);
        } else if (str.equals("3")) {
            return context.getString(R.string.api_wind_power_level_three);
        } else if (str.equals("4")) {
            return context.getString(R.string.api_wind_power_level_four);
        } else if (str.equals("5")) {
            return context.getString(R.string.api_wind_power_level_five);
        } else if (str.equals("6")) {
            return context.getString(R.string.api_wind_power_level_six);
        } else if (str.equals("7")) {
            return context.getString(R.string.api_wind_power_level_seven);
        } else if (str.equals("8")) {
            return context.getString(R.string.api_wind_power_level_eight);
        } else if (str.equals("9")) {
            return context.getString(R.string.api_wind_power_level_nine);
        } else {
            return context.getString(R.string.api_wind_power_level_zero);
        }
    }


    private static Direction toSwaDirection(String dirction) {
        return Wind.getDirectionFromSwa(dirction);
    }
}
