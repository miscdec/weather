package com.opweather.constants;

import android.content.Context;

import com.opweather.R;

public class WeatherDescription {
    public static final int WEATHER_DESCRIPTION_CLOUDY = 1003;
    public static final int WEATHER_DESCRIPTION_DOWNPOUR = 1008;
    public static final int WEATHER_DESCRIPTION_DRIZZLE = 1005;
    public static final int WEATHER_DESCRIPTION_FLURRY = 1011;
    public static final int WEATHER_DESCRIPTION_FOG = 1017;
    public static final int WEATHER_DESCRIPTION_HAIL = 1014;
    public static final int WEATHER_DESCRIPTION_HAZE = 1019;
    public static final int WEATHER_DESCRIPTION_HURRICANE = 1018;
    public static final int WEATHER_DESCRIPTION_OVERCAST = 1004;
    public static final int WEATHER_DESCRIPTION_RAIN = 1006;
    public static final int WEATHER_DESCRIPTION_RAINSTORM = 1009;
    public static final int WEATHER_DESCRIPTION_SANDSTORM = 1016;
    public static final int WEATHER_DESCRIPTION_SHOWER = 1007;
    public static final int WEATHER_DESCRIPTION_SLEET = 1010;
    public static final int WEATHER_DESCRIPTION_SNOW = 1012;
    public static final int WEATHER_DESCRIPTION_SNOWSTORM = 1013;
    public static final int WEATHER_DESCRIPTION_SUNNY = 1001;
    public static final int WEATHER_DESCRIPTION_SUNNY_INTERVALS = 1002;
    public static final int WEATHER_DESCRIPTION_THUNDERSHOWER = 1015;
    public static final int WEATHER_DESCRIPTION_UNKNOWN = 9999;

    public static String getWeatherDescription(Context context, int id) {
        switch (id) {
            case WEATHER_DESCRIPTION_SUNNY:
                return context.getString(R.string.weather_description_sunny);
            case WEATHER_DESCRIPTION_SUNNY_INTERVALS:
                return context.getString(R.string.weather_description_sunny_intervals);
            case WEATHER_DESCRIPTION_CLOUDY:
                return context.getString(R.string.weather_description_cloudy);
            case WEATHER_DESCRIPTION_OVERCAST:
                return context.getString(R.string.weather_description_overcast);
            case WEATHER_DESCRIPTION_DRIZZLE:
                return context.getString(R.string.weather_description_drizzle);
            case WEATHER_DESCRIPTION_RAIN:
                return context.getString(R.string.weather_description_rain);
            case WEATHER_DESCRIPTION_SHOWER:
                return context.getString(R.string.weather_description_shower);
            case WEATHER_DESCRIPTION_DOWNPOUR:
                return context.getString(R.string.weather_description_downpour);
            case WEATHER_DESCRIPTION_RAINSTORM:
                return context.getString(R.string.weather_description_rainstorm);
            case WEATHER_DESCRIPTION_SLEET:
                return context.getString(R.string.weather_description_sleet);
            case WEATHER_DESCRIPTION_FLURRY:
                return context.getString(R.string.weather_description_flurry);
            case WEATHER_DESCRIPTION_SNOW:
                return context.getString(R.string.weather_description_snow);
            case WEATHER_DESCRIPTION_SNOWSTORM:
                return context.getString(R.string.weather_description_snowstorm);
            case WEATHER_DESCRIPTION_HAIL:
                return context.getString(R.string.weather_description_hail);
            case WEATHER_DESCRIPTION_THUNDERSHOWER:
                return context.getString(R.string.weather_description_thundershower);
            case WEATHER_DESCRIPTION_SANDSTORM:
                return context.getString(R.string.weather_description_sandstorm);
            case WEATHER_DESCRIPTION_FOG:
                return context.getString(R.string.weather_description_fog);
            case WEATHER_DESCRIPTION_HURRICANE:
                return context.getString(R.string.weather_description_typhoon);
            case WEATHER_DESCRIPTION_HAZE:
                return context.getString(R.string.weather_description_haze);
            default:
                return context.getString(R.string.weather_description_unknown);
        }
    }
}
