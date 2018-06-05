package com.opweather.util;

import android.content.Context;

import com.google.android.gms.location.GeofenceStatusCodes;
import com.opweather.constants.WeatherDescription;
import com.opweather.widget.AbsWeather;
import com.opweather.widget.WidgetUpdateJob;
import com.opweather.widget.openglbase.RainSurfaceView;

public class WeatherViewCreator {
    public static AbsWeather getViewFromDescription(Context context, int description, boolean isDay) {
        switch (description) {
           /* case GeofenceStatusCodes.GEOFENCE_TOO_MANY_GEOFENCES:
                return new SunnyView(context, isDay);
            case GeofenceStatusCodes.GEOFENCE_TOO_MANY_PENDING_INTENTS:
                return new SunnyView(context, isDay);
            case WeatherDescription.WEATHER_DESCRIPTION_CLOUDY:
                return new CloudyView(context, isDay);
            case WeatherDescription.WEATHER_DESCRIPTION_OVERCAST:
                return new OverCastView(context, isDay);
            case WeatherDescription.WEATHER_DESCRIPTION_DRIZZLE:
                return OpenGLUtil.isSupportGLES20(context) ? new RainView(context, isDay, 0) : new RainSurfaceView(context, 0, isDay);
            case WeatherDescription.WEATHER_DESCRIPTION_RAIN:
                return OpenGLUtil.isSupportGLES20(context) ? new RainView(context, isDay, 1) : new RainSurfaceView(context, 1, isDay);
            case WeatherDescription.WEATHER_DESCRIPTION_SHOWER:
                return new RainSurfaceView(context, 2, isDay);
            case WeatherDescription.WEATHER_DESCRIPTION_DOWNPOUR:
                return new RainSurfaceView(context, 3, isDay);
            case WeatherDescription.WEATHER_DESCRIPTION_RAINSTORM:
                return new RainSurfaceView(context, 4, isDay);
            case WidgetUpdateJob.UPDATE_JOBID:
                return new RainSurfaceView(context, 0, isDay);
            case WeatherDescription.WEATHER_DESCRIPTION_FLURRY:
                return OpenGLUtil.isSupportGLES20(context) ? new SnowView(context, isDay, 0) : new SnowSurfaceView(context, isDay);
            case WeatherDescription.WEATHER_DESCRIPTION_SNOW:
                return OpenGLUtil.isSupportGLES20(context) ? new SnowView(context, isDay, 1) : new SnowSurfaceView(context, isDay);
            case WeatherDescription.WEATHER_DESCRIPTION_SNOWSTORM:
                return OpenGLUtil.isSupportGLES20(context) ? new SnowView(context, isDay, 2) : new SnowSurfaceView(context, isDay);
            case WeatherDescription.WEATHER_DESCRIPTION_HAIL:
                return new HailView(context);
            case WeatherDescription.WEATHER_DESCRIPTION_THUNDERSHOWER:
                return new RainSurfaceView(context, 5, isDay);
            case WeatherDescription.WEATHER_DESCRIPTION_SANDSTORM:
                return OpenGLUtil.isSupportGLES20(context) ? new SandStormView(context, isDay) : new net.oneplus.weather.widget.SandStormView(context);
            case WeatherDescription.WEATHER_DESCRIPTION_FOG:
                return new FogView(context, isDay);
            case WeatherDescription.WEATHER_DESCRIPTION_HURRICANE:
                return new SunnyView(context, isDay);
            case WeatherDescription.WEATHER_DESCRIPTION_HAZE:
                return OpenGLUtil.isSupportGLES20(context) ? new HazeView(context, isDay) : new FogSurfaceView(context, isDay);
            default:
                return new SunnyView(context, isDay);*/
        }
        return null;
    }
}
