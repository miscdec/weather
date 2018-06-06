package com.opweather.api.helper;

import android.content.Context;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.text.TextUtils;

import com.opweather.R;
import com.opweather.api.nodes.Alarm;
import com.opweather.constants.WeatherDescription;
import com.opweather.constants.WeatherType;
import com.opweather.api.nodes.DailyForecastsWeather;
import com.opweather.api.nodes.RootWeather;
import com.opweather.util.StringUtils;
import com.opweather.widget.WeatherCircleView;
import com.opweather.widget.WidgetUpdateJob;
import com.opweather.widget.openglbase.RainSurfaceView;

import java.net.URLEncoder;
import java.security.InvalidKeyException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

public final class WeatherUtils {
    private static final char[] encodeTable;
    private static final char last2byte;
    private static final char last4byte;
    private static final char last6byte;
    private static final char lead2byte;
    private static final char lead4byte;
    private static final char lead6byte;

    static {
        last2byte = (char) Integer.parseInt("00000011", RainSurfaceView.RAIN_LEVEL_SHOWER);
        last4byte = (char) Integer.parseInt("00001111", RainSurfaceView.RAIN_LEVEL_SHOWER);
        last6byte = (char) Integer.parseInt("00111111", RainSurfaceView.RAIN_LEVEL_SHOWER);
        lead6byte = (char) Integer.parseInt("11111100", RainSurfaceView.RAIN_LEVEL_SHOWER);
        lead4byte = (char) Integer.parseInt("11110000", RainSurfaceView.RAIN_LEVEL_SHOWER);
        lead2byte = (char) Integer.parseInt("11000000", RainSurfaceView.RAIN_LEVEL_SHOWER);
        encodeTable = new char[]{'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'I', 'J', 'K', 'L', 'M', 'N', 'O', 'P', 'Q',
                'R', 'S', 'T', 'U', 'V', 'W', 'X', 'Y', 'Z', 'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'i', 'j', 'k',
                'l', 'm', 'n', 'o', 'p', 'q', 'r', 's', 't', 'u', 'v', 'w', 'x', 'y', 'z', '0', '1', '2', '3', '4',
                '5', '6', '7', '8', '9', '+', '/'};
    }

    private WeatherUtils() {
    }

    public static String swaStandardURLEncoder(String data, String key) {
        String urlEncoder = StringUtils.EMPTY_STRING;
        try {
            Mac mac = Mac.getInstance("HmacSHA1");
            mac.init(new SecretKeySpec(key.getBytes(), "HmacSHA1"));
            byte[] byteHMAC = mac.doFinal(data.getBytes());
            if (byteHMAC == null) {
                return urlEncoder;
            }
            String oauth = encode(byteHMAC);
            return oauth != null ? URLEncoder.encode(oauth, "utf8") : urlEncoder;
        } catch (InvalidKeyException e1) {
            e1.printStackTrace();
            return urlEncoder;
        } catch (Exception e2) {
            e2.printStackTrace();
            return urlEncoder;
        }
    }

    private static String encode(byte[] from) {
        int i;
        StringBuffer to = new StringBuffer(((int) (((double) from.length) * 1.34d)) + 3);
        int num = 0;
        char c = '\u0000';
        for (i = 0; i < from.length; i++) {
            num %= 8;
            while (num < 8) {
                switch (num) {
                    case RainSurfaceView.RAIN_LEVEL_DRIZZLE:
                        c = (char) (((char) (from[i] & lead6byte)) >>> 2);
                        break;
                    case RainSurfaceView.RAIN_LEVEL_SHOWER:
                        c = (char) (from[i] & last6byte);
                        break;
                    case RainSurfaceView.RAIN_LEVEL_RAINSTORM:
                        c = (char) (((char) (from[i] & last4byte)) << 2);
                        if (i + 1 < from.length) {
                            c = (char) (((from[i + 1] & lead2byte) >>> 6) | c);
                        }
                    case 6:
                        c = (char) (((char) (from[i] & last2byte)) << 4);
                        if (i + 1 < from.length) {
                            c = (char) (((from[i + 1] & lead4byte) >>> 4) | c);
                        }
                    default:
                        break;
                }
                to.append(encodeTable[c]);
                num += 6;
            }
        }
        if (to.length() % 4 != 0) {
            for (i = 4 - (to.length() % 4); i > 0; i--) {
                to.append("=");
            }
        }
        return to.toString();
    }

    public static String getSwaWeatherTextById(Context context, int id) {
        switch (id) {
            case WeatherType.SWA_WEATHER_SUNNY:
                return context.getString(R.string.api_china_weather_00);
            case WeatherType.SWA_WEATHER_CLOUDY:
                return context.getString(R.string.api_china_weather_01);
            case WeatherType.SWA_WEATHER_OVERCAST:
                return context.getString(R.string.api_china_weather_02);
            case WeatherType.SWA_WEATHER_SHOWER:
                return context.getString(R.string.api_china_weather_03);
            case WeatherType.SWA_WEATHER_THUNDERSHOWER:
                return context.getString(R.string.api_china_weather_04);
            case WeatherType.SWA_WEATHER_THUNDERSHOWER_WITH_HAIL:
                return context.getString(R.string.api_china_weather_05);
            case WeatherType.SWA_WEATHER_SLEET:
                return context.getString(R.string.api_china_weather_06);
            case WeatherType.SWA_WEATHER_LIGHT_RAIN:
                return context.getString(R.string.api_china_weather_07);
            case WeatherType.SWA_WEATHER_MODERATE_RAIN:
                return context.getString(R.string.api_china_weather_08);
            case WeatherType.SWA_WEATHER_HEAVY_RAIN:
                return context.getString(R.string.api_china_weather_09);
            case WeatherType.SWA_WEATHER_STORM:
                return context.getString(R.string.api_china_weather_10);
            case WeatherType.SWA_WEATHER_HEAVY_STORM:
                return context.getString(R.string.api_china_weather_11);
            case WeatherType.SWA_WEATHER_SEVERE_STORM:
                return context.getString(R.string.api_china_weather_12);
            case WeatherType.SWA_WEATHER_SNOW_FLURRY:
                return context.getString(R.string.api_china_weather_13);
            case WeatherType.SWA_WEATHER_LIGHT_SNOW:
                return context.getString(R.string.api_china_weather_14);
            case WeatherType.SWA_WEATHER_MODERATE_SNOW:
                return context.getString(R.string.api_china_weather_15);
            case WeatherType.SWA_WEATHER_HEAVY_SNOW:
                return context.getString(R.string.api_china_weather_16);
            case WeatherType.SWA_WEATHER_SNOWSTORM:
                return context.getString(R.string.api_china_weather_17);
            case WeatherType.SWA_WEATHER_FOGGY:
                return context.getString(R.string.api_china_weather_18);
            case WeatherType.SWA_WEATHER_ICE_RAIN:
                return context.getString(R.string.api_china_weather_19);
            case WeatherType.SWA_WEATHER_DUSTSTORM:
                return context.getString(R.string.api_china_weather_20);
            case WeatherType.SWA_WEATHER_LIGHT_TO_MODERATE_RAIN:
                return context.getString(R.string.api_china_weather_21);
            case WeatherType.SWA_WEATHER_MODERATE_TO_HEAVY_RAIN:
                return context.getString(R.string.api_china_weather_22);
            case WeatherType.SWA_WEATHER_HEAVY_RAIN_TO_STORM:
                return context.getString(R.string.api_china_weather_23);
            case WeatherType.SWA_WEATHER_STORM_TO_HEAVY_STORM:
                return context.getString(R.string.api_china_weather_24);
            case WeatherType.SWA_WEATHER_HEAVY_TO_SEVERE_STORM:
                return context.getString(R.string.api_china_weather_25);
            case WeatherType.SWA_WEATHER_LIGHT_TO_MODERATE_SNOW:
                return context.getString(R.string.api_china_weather_26);
            case WeatherType.SWA_WEATHER_MODERATE_TO_HEAVY_SNOW:
                return context.getString(R.string.api_china_weather_27);
            case WeatherType.SWA_WEATHER_HEAVY_SNOW_TO_SNOWSTORM:
                return context.getString(R.string.api_china_weather_28);
            case WeatherType.SWA_WEATHER_DUST:
                return context.getString(R.string.api_china_weather_29);
            case WeatherType.SWA_WEATHER_SAND:
                return context.getString(R.string.api_china_weather_30);
            case WeatherType.SWA_WEATHER_SANDSTORM:
                return context.getString(R.string.api_china_weather_31);
            case WeatherType.SWA_WEATHER_DENSE_FOGGY:
                return context.getString(R.string.api_china_weather_32);
            case WeatherType.SWA_WEATHER_SNOW:
                return context.getString(R.string.api_china_weather_33);
            case WeatherType.SWA_WEATHER_STRONG_DENSE_FOGGY:
                return context.getString(R.string.api_china_weather_49);
            case WeatherType.SWA_WEATHER_HAZE:
                return context.getString(R.string.api_china_weather_53);
            case WeatherType.SWA_WEATHER_MODERATE_HAZE:
                return context.getString(R.string.api_china_weather_54);
            case WeatherType.SWA_WEATHER_HEAVY_HAZE:
                return context.getString(R.string.api_china_weather_55);
            case WeatherType.SWA_WEATHER_SEVERE_HAZE:
                return context.getString(R.string.api_china_weather_56);
            case WeatherType.SWA_WEATHER_HEAVY_FOGGY:
                return context.getString(R.string.api_china_weather_57);
            case WeatherType.SWA_WEATHER_SEVERE_FOGGY:
                return context.getString(R.string.api_china_weather_58);
            default:
                return context.getString(R.string.api_weather_unknown);
        }
    }

    public static String getAccuWeatherTextById(Context context, int id) {
        switch (id) {
            case 1000:
                return context.getString(R.string.api_accu_weather_01);
            case 1001:
                return context.getString(R.string.api_accu_weather_02);
            case 1002:
                return context.getString(R.string.api_accu_weather_03);
            case WeatherDescription.WEATHER_DESCRIPTION_CLOUDY:
                return context.getString(R.string.api_accu_weather_04);
            case WeatherDescription.WEATHER_DESCRIPTION_OVERCAST:
                return context.getString(R.string.api_accu_weather_05);
            case WeatherDescription.WEATHER_DESCRIPTION_DRIZZLE:
                return context.getString(R.string.api_accu_weather_06);
            case WeatherDescription.WEATHER_DESCRIPTION_RAIN:
                return context.getString(R.string.api_accu_weather_07);
            case WeatherDescription.WEATHER_DESCRIPTION_SHOWER:
                return context.getString(R.string.api_accu_weather_08);
            case WeatherDescription.WEATHER_DESCRIPTION_DOWNPOUR:
                return context.getString(R.string.api_accu_weather_11);
            case WeatherDescription.WEATHER_DESCRIPTION_RAINSTORM:
                return context.getString(R.string.api_accu_weather_12);
            case WidgetUpdateJob.UPDATE_JOBID:
                return context.getString(R.string.api_accu_weather_13);
            case WeatherDescription.WEATHER_DESCRIPTION_FLURRY:
                return context.getString(R.string.api_accu_weather_14);
            case WeatherDescription.WEATHER_DESCRIPTION_SNOW:
                return context.getString(R.string.api_accu_weather_15);
            case WeatherDescription.WEATHER_DESCRIPTION_SNOWSTORM:
                return context.getString(R.string.api_accu_weather_16);
            case WeatherDescription.WEATHER_DESCRIPTION_HAIL:
                return context.getString(R.string.api_accu_weather_17);
            case WeatherDescription.WEATHER_DESCRIPTION_THUNDERSHOWER:
                return context.getString(R.string.api_accu_weather_18);
            case WeatherDescription.WEATHER_DESCRIPTION_SANDSTORM:
                return context.getString(R.string.api_accu_weather_19);
            case WeatherDescription.WEATHER_DESCRIPTION_FOG:
                return context.getString(R.string.api_accu_weather_20);
            case WeatherDescription.WEATHER_DESCRIPTION_HURRICANE:
                return context.getString(R.string.api_accu_weather_21);
            case WeatherDescription.WEATHER_DESCRIPTION_HAZE:
                return context.getString(R.string.api_accu_weather_22);
            case WeatherType.ACCU_WEATHER_MOSTLY_CLOUDY_W_SNOW:
                return context.getString(R.string.api_accu_weather_23);
            case WeatherType.ACCU_WEATHER_ICE:
                return context.getString(R.string.api_accu_weather_24);
            case WeatherType.ACCU_WEATHER_SLEET:
                return context.getString(R.string.api_accu_weather_25);
            case WeatherType.ACCU_WEATHER_FREEZING_RAIN:
                return context.getString(R.string.api_accu_weather_26);
            case WeatherType.ACCU_WEATHER_RAIN_AND_SNOW:
                return context.getString(R.string.api_accu_weather_29);
            case WeatherType.ACCU_WEATHER_HOT:
                return context.getString(R.string.api_accu_weather_30);
            case WeatherType.ACCU_WEATHER_COLD:
                return context.getString(R.string.api_accu_weather_31);
            case WeatherType.ACCU_WEATHER_WINDY:
                return context.getString(R.string.api_accu_weather_32);
            case WeatherType.ACCU_WEATHER_CLEAR:
                return context.getString(R.string.api_accu_weather_33);
            case WeatherType.ACCU_WEATHER_MOSTLY_CLEAR:
                return context.getString(R.string.api_accu_weather_34);
            case WeatherType.ACCU_WEATHER_PARTLY_CLOUDY:
                return context.getString(R.string.api_accu_weather_35);
            case WeatherType.ACCU_WEATHER_INTERMITTENT_CLOUDS_NIGHT:
                return context.getString(R.string.api_accu_weather_36);
            case WeatherType.ACCU_WEATHER_HAZY_MOONLIGHT:
                return context.getString(R.string.api_accu_weather_37);
            case WeatherType.ACCU_WEATHER_MOSTLY_CLOUDY_NIGHT:
                return context.getString(R.string.api_accu_weather_38);
            case WeatherType.ACCU_WEATHER_PARTLY_CLOUDY_W_SHOWERS:
                return context.getString(R.string.api_accu_weather_39);
            case WeatherType.ACCU_WEATHER_MOSTLY_CLOUDY_W_SHOWERS_NIGHT:
                return context.getString(R.string.api_accu_weather_40);
            case WeatherType.ACCU_WEATHER_PARTLY_CLOUDY_W_T_STORMS:
                return context.getString(R.string.api_accu_weather_41);
            case WeatherType.ACCU_WEATHER_MOSTLY_CLOUDY_W_T_STORMS_NIGHT:
                return context.getString(R.string.api_accu_weather_42);
            case WeatherType.ACCU_WEATHER_MOSTLY_CLOUDY_W_FLURRIES_NIGHT:
                return context.getString(R.string.api_accu_weather_43);
            case WeatherType.ACCU_WEATHER_MOSTLY_CLOUDY_W_SNOW_NIGHT:
                return context.getString(R.string.api_accu_weather_44);
            default:
                return context.getString(R.string.api_weather_unknown);
        }
    }

    public static String getOppoChinaWeatherTextById(Context context, int id) {
        switch (id) {
            case WeatherType.OPPO_CHINA_WEATHER_SUNNY:
                return context.getString(R.string.api_china_weather_00);
            case WeatherType.OPPO_CHINA_WEATHER_CLOUDY:
                return context.getString(R.string.api_china_weather_01);
            case WeatherType.OPPO_CHINA_WEATHER_OVERCAST:
                return context.getString(R.string.api_china_weather_02);
            case WeatherType.OPPO_CHINA_WEATHER_SHOWER:
                return context.getString(R.string.api_china_weather_03);
            case WeatherType.OPPO_CHINA_WEATHER_THUNDERSHOWER:
                return context.getString(R.string.api_china_weather_04);
            case WeatherType.OPPO_CHINA_WEATHER_THUNDERSHOWER_WITH_HAIL:
                return context.getString(R.string.api_china_weather_05);
            case WeatherType.OPPO_CHINA_WEATHER_SLEET:
                return context.getString(R.string.api_china_weather_06);
            case WeatherType.OPPO_CHINA_WEATHER_LIGHT_RAIN:
                return context.getString(R.string.api_china_weather_07);
            case WeatherType.OPPO_CHINA_WEATHER_MODERATE_RAIN:
                return context.getString(R.string.api_china_weather_08);
            case WeatherType.OPPO_CHINA_WEATHER_HEAVY_RAIN:
                return context.getString(R.string.api_china_weather_09);
            case WeatherType.OPPO_CHINA_WEATHER_STORM:
                return context.getString(R.string.api_china_weather_10);
            case WeatherType.OPPO_CHINA_WEATHER_HEAVY_STORM:
                return context.getString(R.string.api_china_weather_11);
            case WeatherType.OPPO_CHINA_WEATHER_SEVERE_STORM:
                return context.getString(R.string.api_china_weather_12);
            case WeatherType.OPPO_CHINA_WEATHER_SNOW_FLURRY:
                return context.getString(R.string.api_china_weather_13);
            case WeatherType.OPPO_CHINA_WEATHER_LIGHT_SNOW:
                return context.getString(R.string.api_china_weather_14);
            case WeatherType.OPPO_CHINA_WEATHER_MODERATE_SNOW:
                return context.getString(R.string.api_china_weather_15);
            case WeatherType.OPPO_CHINA_WEATHER_HEAVY_SNOW:
                return context.getString(R.string.api_china_weather_16);
            case WeatherType.OPPO_CHINA_WEATHER_SNOWSTORM:
                return context.getString(R.string.api_china_weather_17);
            case WeatherType.OPPO_CHINA_WEATHER_FOGGY:
                return context.getString(R.string.api_china_weather_18);
            case WeatherType.OPPO_CHINA_WEATHER_ICE_RAIN:
                return context.getString(R.string.api_china_weather_19);
            case WeatherType.OPPO_CHINA_WEATHER_DUSTSTORM:
                return context.getString(R.string.api_china_weather_20);
            case WeatherType.OPPO_CHINA_WEATHER_LIGHT_TO_MODERATE_RAIN:
                return context.getString(R.string.api_china_weather_21);
            case WeatherType.OPPO_CHINA_WEATHER_MODERATE_TO_HEAVY_RAIN:
                return context.getString(R.string.api_china_weather_22);
            case WeatherType.OPPO_CHINA_WEATHER_HEAVY_RAIN_TO_STORM:
                return context.getString(R.string.api_china_weather_23);
            case WeatherType.OPPO_CHINA_WEATHER_STORM_TO_HEAVY_STORM:
                return context.getString(R.string.api_china_weather_24);
            case WeatherType.OPPO_CHINA_WEATHER_HEAVY_TO_SEVERE_STORM:
                return context.getString(R.string.api_china_weather_25);
            case WeatherType.OPPO_CHINA_WEATHER_LIGHT_TO_MODERATE_SNOW:
                return context.getString(R.string.api_china_weather_26);
            case WeatherType.OPPO_CHINA_WEATHER_MODERATE_TO_HEAVY_SNOW:
                return context.getString(R.string.api_china_weather_27);
            case WeatherType.OPPO_CHINA_WEATHER_HEAVY_SNOW_TO_SNOWSTORM:
                return context.getString(R.string.api_china_weather_28);
            case WeatherType.OPPO_CHINA_WEATHER_DUST:
                return context.getString(R.string.api_china_weather_29);
            case WeatherType.OPPO_CHINA_WEATHER_SAND:
                return context.getString(R.string.api_china_weather_30);
            case WeatherType.OPPO_CHINA_WEATHER_SANDSTORM:
                return context.getString(R.string.api_china_weather_31);
            case WeatherType.OPPO_CHINA_WEATHER_DENSE_FOGGY:
                return context.getString(R.string.api_china_weather_32);
            case WeatherType.OPPO_CHINA_WEATHER_SNOW:
                return context.getString(R.string.api_china_weather_33);
            case WeatherType.OPPO_CHINA_WEATHER_STRONG_DENSE_FOGGY:
                return context.getString(R.string.api_china_weather_49);
            case WeatherType.OPPO_CHINA_WEATHER_HAZE:
                return context.getString(R.string.api_china_weather_53);
            case WeatherType.OPPO_CHINA_WEATHER_MODERATE_HAZE:
                return context.getString(R.string.api_china_weather_54);
            case WeatherType.OPPO_CHINA_WEATHER_HEAVY_HAZE:
                return context.getString(R.string.api_china_weather_55);
            case WeatherType.OPPO_CHINA_WEATHER_SEVERE_HAZE:
                return context.getString(R.string.api_china_weather_56);
            case WeatherType.OPPO_CHINA_WEATHER_HEAVY_FOGGY:
                return context.getString(R.string.api_china_weather_57);
            case WeatherType.OPPO_CHINA_WEATHER_SEVERE_FOGGY:
                return context.getString(R.string.api_china_weather_58);
            default:
                return context.getString(R.string.api_weather_unknown);
        }
    }

    public static String getOppoForeignWeatherTextById(Context context, int id) {
        switch (id) {
            case WeatherType.OPPO_FOREIGN_WEATHER_SUNNY:
                return context.getString(R.string.api_china_weather_00);
            case WeatherType.OPPO_FOREIGN_WEATHER_CLOUDY:
                return context.getString(R.string.api_china_weather_01);
            case WeatherType.OPPO_FOREIGN_WEATHER_OVERCAST:
                return context.getString(R.string.api_china_weather_02);
            case WeatherType.OPPO_FOREIGN_WEATHER_SHOWER:
                return context.getString(R.string.api_china_weather_03);
            case WeatherType.OPPO_FOREIGN_WEATHER_THUNDERSHOWER:
                return context.getString(R.string.api_china_weather_04);
            case WeatherType.OPPO_FOREIGN_WEATHER_THUNDERSHOWER_WITH_HAIL:
                return context.getString(R.string.api_china_weather_05);
            case WeatherType.OPPO_FOREIGN_WEATHER_SLEET:
                return context.getString(R.string.api_china_weather_06);
            case WeatherType.OPPO_FOREIGN_WEATHER_LIGHT_RAIN:
                return context.getString(R.string.api_china_weather_07);
            case WeatherType.OPPO_FOREIGN_WEATHER_MODERATE_RAIN:
                return context.getString(R.string.api_china_weather_08);
            case WeatherType.OPPO_FOREIGN_WEATHER_HEAVY_RAIN:
                return context.getString(R.string.api_china_weather_09);
            case WeatherType.OPPO_FOREIGN_WEATHER_STORM:
                return context.getString(R.string.api_china_weather_10);
            case WeatherType.OPPO_FOREIGN_WEATHER_HEAVY_STORM:
                return context.getString(R.string.api_china_weather_11);
            case WeatherType.OPPO_FOREIGN_WEATHER_SEVERE_STORM:
                return context.getString(R.string.api_china_weather_12);
            case WeatherType.OPPO_FOREIGN_WEATHER_SNOW_FLURRY:
                return context.getString(R.string.api_china_weather_13);
            case WeatherType.OPPO_FOREIGN_WEATHER_LIGHT_SNOW:
                return context.getString(R.string.api_china_weather_14);
            case WeatherType.OPPO_FOREIGN_WEATHER_MODERATE_SNOW:
                return context.getString(R.string.api_china_weather_15);
            case WeatherType.OPPO_FOREIGN_WEATHER_HEAVY_SNOW:
                return context.getString(R.string.api_china_weather_16);
            case WeatherType.OPPO_FOREIGN_WEATHER_SNOWSTORM:
                return context.getString(R.string.api_china_weather_17);
            case WeatherType.OPPO_FOREIGN_WEATHER_FOGGY:
                return context.getString(R.string.api_china_weather_18);
            case WeatherType.OPPO_FOREIGN_WEATHER_ICE_RAIN:
                return context.getString(R.string.api_china_weather_19);
            case WeatherType.OPPO_FOREIGN_WEATHER_DUSTSTORM:
                return context.getString(R.string.api_china_weather_20);
            case WeatherType.OPPO_FOREIGN_WEATHER_LIGHT_TO_MODERATE_RAIN:
                return context.getString(R.string.api_china_weather_21);
            case WeatherType.OPPO_FOREIGN_WEATHER_MODERATE_TO_HEAVY_RAIN:
                return context.getString(R.string.api_china_weather_22);
            case WeatherType.OPPO_FOREIGN_WEATHER_HEAVY_RAIN_TO_STORM:
                return context.getString(R.string.api_china_weather_23);
            case WeatherType.OPPO_FOREIGN_WEATHER_STORM_TO_HEAVY_STORM:
                return context.getString(R.string.api_china_weather_24);
            case WeatherType.OPPO_FOREIGN_WEATHER_HEAVY_TO_SEVERE_STORM:
                return context.getString(R.string.api_china_weather_25);
            case WeatherType.OPPO_FOREIGN_WEATHER_LIGHT_TO_MODERATE_SNOW:
                return context.getString(R.string.api_china_weather_26);
            case WeatherType.OPPO_FOREIGN_WEATHER_MODERATE_TO_HEAVY_SNOW:
                return context.getString(R.string.api_china_weather_27);
            case WeatherType.OPPO_FOREIGN_WEATHER_HEAVY_SNOW_TO_SNOWSTORM:
                return context.getString(R.string.api_china_weather_28);
            case WeatherType.OPPO_FOREIGN_WEATHER_DUST:
                return context.getString(R.string.api_china_weather_29);
            case WeatherType.OPPO_FOREIGN_WEATHER_SAND:
                return context.getString(R.string.api_china_weather_30);
            case WeatherType.OPPO_FOREIGN_WEATHER_SANDSTORM:
                return context.getString(R.string.api_china_weather_31);
            case WeatherType.OPPO_FOREIGN_WEATHER_DENSE_FOGGY:
                return context.getString(R.string.api_china_weather_32);
            case WeatherType.OPPO_FOREIGN_WEATHER_SNOW:
                return context.getString(R.string.api_china_weather_33);
            case WeatherType.OPPO_FOREIGN_WEATHER_STRONG_DENSE_FOGGY:
                return context.getString(R.string.api_china_weather_49);
            case WeatherType.OPPO_FOREIGN_WEATHER_HAZE:
                return context.getString(R.string.api_china_weather_53);
            case WeatherType.OPPO_FOREIGN_WEATHER_MODERATE_HAZE:
                return context.getString(R.string.api_china_weather_54);
            case WeatherType.OPPO_FOREIGN_WEATHER_HEAVY_HAZE:
                return context.getString(R.string.api_china_weather_55);
            case WeatherType.OPPO_FOREIGN_WEATHER_SEVERE_HAZE:
                return context.getString(R.string.api_china_weather_56);
            case WeatherType.OPPO_FOREIGN_WEATHER_HEAVY_FOGGY:
                return context.getString(R.string.api_china_weather_57);
            case WeatherType.OPPO_FOREIGN_WEATHER_SEVERE_FOGGY:
                return context.getString(R.string.api_china_weather_58);
            default:
                return context.getString(R.string.api_weather_unknown);
        }
    }

    public static int swaWeatherIdToWeatherId(String weatherId) {

        if (weatherId.equals("00")) {
            return WeatherType.SWA_WEATHER_SUNNY;
        } else if (weatherId.equals("01")) {
            return WeatherType.SWA_WEATHER_CLOUDY;
        } else if (weatherId.equals("02")) {
            return WeatherType.SWA_WEATHER_OVERCAST;
        } else if (weatherId.equals("03")) {
            return WeatherType.SWA_WEATHER_SHOWER;
        } else if (weatherId.equals("04")) {
            return WeatherType.SWA_WEATHER_THUNDERSHOWER;
        } else if (weatherId.equals("05")) {
            return WeatherType.SWA_WEATHER_THUNDERSHOWER_WITH_HAIL;
        } else if (weatherId.equals("06")) {
            return WeatherType.SWA_WEATHER_SLEET;
        } else if (weatherId.equals("07")) {
            return WeatherType.SWA_WEATHER_LIGHT_RAIN;
        } else if (weatherId.equals("08")) {
            return WeatherType.SWA_WEATHER_MODERATE_RAIN;
        } else if (weatherId.equals("09")) {
            return WeatherType.SWA_WEATHER_HEAVY_RAIN;
        } else if (weatherId.equals("10")) {
            return WeatherType.SWA_WEATHER_STORM;
        } else if (weatherId.equals("11")) {
            return WeatherType.SWA_WEATHER_HEAVY_STORM;
        } else if (weatherId.equals("12")) {
            return WeatherType.SWA_WEATHER_SEVERE_STORM;
        } else if (weatherId.equals("13")) {
            return WeatherType.SWA_WEATHER_SNOW_FLURRY;
        } else if (weatherId.equals("14")) {
            return WeatherType.SWA_WEATHER_LIGHT_SNOW;
        } else if (weatherId.equals("15")) {
            return WeatherType.SWA_WEATHER_MODERATE_SNOW;
        } else if (weatherId.equals("16")) {
            return WeatherType.SWA_WEATHER_HEAVY_SNOW;
        } else if (weatherId.equals("17")) {
            return WeatherType.SWA_WEATHER_SNOWSTORM;
        } else if (weatherId.equals("18")) {
            return WeatherType.SWA_WEATHER_FOGGY;
        } else if (weatherId.equals("19")) {
            return WeatherType.SWA_WEATHER_ICE_RAIN;
        } else if (weatherId.equals("20")) {
            return WeatherType.SWA_WEATHER_DUSTSTORM;
        } else if (weatherId.equals("21")) {
            return WeatherType.SWA_WEATHER_LIGHT_TO_MODERATE_RAIN;
        } else if (weatherId.equals("22")) {
            return WeatherType.SWA_WEATHER_MODERATE_TO_HEAVY_RAIN;
        } else if (weatherId.equals("23")) {
            return WeatherType.SWA_WEATHER_HEAVY_RAIN_TO_STORM;
        } else if (weatherId.equals("24")) {
            return WeatherType.SWA_WEATHER_STORM_TO_HEAVY_STORM;
        } else if (weatherId.equals("25")) {
            return WeatherType.SWA_WEATHER_HEAVY_TO_SEVERE_STORM;
        } else if (weatherId.equals("26")) {
            return WeatherType.SWA_WEATHER_LIGHT_TO_MODERATE_SNOW;
        } else if (weatherId.equals("27")) {
            return WeatherType.SWA_WEATHER_MODERATE_TO_HEAVY_SNOW;
        } else if (weatherId.equals("28")) {
            return WeatherType.SWA_WEATHER_HEAVY_SNOW_TO_SNOWSTORM;
        } else if (weatherId.equals("29")) {
            return WeatherType.SWA_WEATHER_DUST;
        } else if (weatherId.equals("30")) {
            return WeatherType.SWA_WEATHER_SAND;
        } else if (weatherId.equals("31")) {
            return WeatherType.SWA_WEATHER_SANDSTORM;
        } else if (weatherId.equals("32")) {
            return WeatherType.SWA_WEATHER_DENSE_FOGGY;
        } else if (weatherId.equals("33")) {
            return WeatherType.SWA_WEATHER_SNOW;
        } else if (weatherId.equals("49")) {
            return WeatherType.SWA_WEATHER_STRONG_DENSE_FOGGY;
        } else if (weatherId.equals("53")) {
            return WeatherType.SWA_WEATHER_HAZE;
        } else if (weatherId.equals("54")) {
            return WeatherType.SWA_WEATHER_MODERATE_HAZE;
        } else if (weatherId.equals("55")) {
            return WeatherType.SWA_WEATHER_HEAVY_HAZE;
        } else if (weatherId.equals("56")) {
            return WeatherType.SWA_WEATHER_SEVERE_HAZE;
        } else if (weatherId.equals("57")) {
            return WeatherType.SWA_WEATHER_HEAVY_FOGGY;
        } else if (weatherId.equals("58")) {
            return WeatherType.SWA_WEATHER_SEVERE_FOGGY;
        }  else {
            return 0;
        }
    }

    public static int accuWeatherIconToWeatherId(int weatherIcon) {
        switch (weatherIcon) {
            case RainSurfaceView.RAIN_LEVEL_NORMAL_RAIN:
                return 1000;
            case RainSurfaceView.RAIN_LEVEL_SHOWER:
                return 1001;
            case RainSurfaceView.RAIN_LEVEL_DOWNPOUR:
                return 1002;
            case RainSurfaceView.RAIN_LEVEL_RAINSTORM:
                return WeatherDescription.WEATHER_DESCRIPTION_CLOUDY;
            case RainSurfaceView.RAIN_LEVEL_THUNDERSHOWER:
                return WeatherDescription.WEATHER_DESCRIPTION_OVERCAST;
            case 6:
                return WeatherDescription.WEATHER_DESCRIPTION_DRIZZLE;
            case 7:
                return WeatherDescription.WEATHER_DESCRIPTION_RAIN;
            case 8:
                return WeatherDescription.WEATHER_DESCRIPTION_SHOWER;
            case 11:
                return WeatherDescription.WEATHER_DESCRIPTION_DOWNPOUR;
            case 12:
                return WeatherDescription.WEATHER_DESCRIPTION_RAINSTORM;
            case 13:
                return WidgetUpdateJob.UPDATE_JOBID;
            case 14:
                return WeatherDescription.WEATHER_DESCRIPTION_FLURRY;
            case 15:
                return WeatherDescription.WEATHER_DESCRIPTION_SNOW;
            case 16:
                return WeatherDescription.WEATHER_DESCRIPTION_SNOWSTORM;
            case 17:
                return WeatherDescription.WEATHER_DESCRIPTION_HAIL;
            case 18:
                return WeatherDescription.WEATHER_DESCRIPTION_THUNDERSHOWER;
            case 19:
                return WeatherDescription.WEATHER_DESCRIPTION_SANDSTORM;
            case 20:
                return WeatherDescription.WEATHER_DESCRIPTION_FOG;
            case 21:
                return WeatherDescription.WEATHER_DESCRIPTION_HURRICANE;
            case 22:
                return WeatherDescription.WEATHER_DESCRIPTION_HAZE;
            case 23:
                return WeatherType.ACCU_WEATHER_MOSTLY_CLOUDY_W_SNOW;
            case 24:
                return WeatherType.ACCU_WEATHER_ICE;
            case 25:
                return WeatherType.ACCU_WEATHER_SLEET;
            case 26:
                return WeatherType.ACCU_WEATHER_FREEZING_RAIN;
            case 29:
                return WeatherType.ACCU_WEATHER_RAIN_AND_SNOW;
            case 30:
                return WeatherType.ACCU_WEATHER_HOT;
            case 31:
                return WeatherType.ACCU_WEATHER_COLD;
            case 32:
                return WeatherType.ACCU_WEATHER_WINDY;
            case 33:
                return WeatherType.ACCU_WEATHER_CLEAR;
            case 34:
                return WeatherType.ACCU_WEATHER_MOSTLY_CLEAR;
            case 35:
                return WeatherType.ACCU_WEATHER_PARTLY_CLOUDY;
            case 36:
                return WeatherType.ACCU_WEATHER_INTERMITTENT_CLOUDS_NIGHT;
            case 37:
                return WeatherType.ACCU_WEATHER_HAZY_MOONLIGHT;
            case 38:
                return WeatherType.ACCU_WEATHER_MOSTLY_CLOUDY_NIGHT;
            case 39:
                return WeatherType.ACCU_WEATHER_PARTLY_CLOUDY_W_SHOWERS;
            case 40:
                return WeatherType.ACCU_WEATHER_MOSTLY_CLOUDY_W_SHOWERS_NIGHT;
            case 41:
                return WeatherType.ACCU_WEATHER_PARTLY_CLOUDY_W_T_STORMS;
            case 42:
                return WeatherType.ACCU_WEATHER_MOSTLY_CLOUDY_W_T_STORMS_NIGHT;
            case 43:
                return WeatherType.ACCU_WEATHER_MOSTLY_CLOUDY_W_FLURRIES_NIGHT;
            case 44:
                return WeatherType.ACCU_WEATHER_MOSTLY_CLOUDY_W_SNOW_NIGHT;
            default:
                return 0;
        }
    }

    public static String getWeatherTextByWeatherId(Context context, int weatherId) {
        switch (weatherId) {
            case 1000:
            case 1001:
            case 1002:
            case WeatherDescription.WEATHER_DESCRIPTION_FLURRY:
            case WeatherDescription.WEATHER_DESCRIPTION_HAIL:
            case WeatherDescription.WEATHER_DESCRIPTION_HURRICANE:
            case WeatherType.ACCU_WEATHER_HOT:
            case WeatherType.ACCU_WEATHER_COLD:
            case WeatherType.ACCU_WEATHER_WINDY:
            case WeatherType.ACCU_WEATHER_CLEAR:
            case WeatherType.ACCU_WEATHER_MOSTLY_CLEAR:
            case WeatherType.OPPO_CHINA_WEATHER_SUNNY:
            case WeatherType.OPPO_FOREIGN_WEATHER_SUNNY:
            case WeatherType.SWA_WEATHER_SUNNY:
                return context.getString(R.string.api_weather_description_sunny);
            case WeatherDescription.WEATHER_DESCRIPTION_CLOUDY:
            case WeatherDescription.WEATHER_DESCRIPTION_DRIZZLE:
            case WeatherDescription.WEATHER_DESCRIPTION_RAIN:
            case WidgetUpdateJob.UPDATE_JOBID:
            case WeatherDescription.WEATHER_DESCRIPTION_SNOWSTORM:
            case WeatherType.ACCU_WEATHER_MOSTLY_CLOUDY_W_SNOW:
            case WeatherType.ACCU_WEATHER_PARTLY_CLOUDY:
            case WeatherType.ACCU_WEATHER_INTERMITTENT_CLOUDS_NIGHT:
            case WeatherType.ACCU_WEATHER_MOSTLY_CLOUDY_NIGHT:
            case WeatherType.ACCU_WEATHER_PARTLY_CLOUDY_W_SHOWERS:
            case WeatherType.ACCU_WEATHER_MOSTLY_CLOUDY_W_SHOWERS_NIGHT:
            case WeatherType.ACCU_WEATHER_PARTLY_CLOUDY_W_T_STORMS:
            case WeatherType.ACCU_WEATHER_MOSTLY_CLOUDY_W_T_STORMS_NIGHT:
            case WeatherType.ACCU_WEATHER_MOSTLY_CLOUDY_W_FLURRIES_NIGHT:
            case WeatherType.ACCU_WEATHER_MOSTLY_CLOUDY_W_SNOW_NIGHT:
            case WeatherType.OPPO_CHINA_WEATHER_CLOUDY:
            case WeatherType.OPPO_FOREIGN_WEATHER_CLOUDY:
            case WeatherType.SWA_WEATHER_CLOUDY:
                return context.getString(R.string.api_weather_description_cloudy);
            case WeatherDescription.WEATHER_DESCRIPTION_OVERCAST:
            case WeatherType.ACCU_WEATHER_HAZY_MOONLIGHT:
            case WeatherType.OPPO_CHINA_WEATHER_HAZE:
            case WeatherType.OPPO_FOREIGN_WEATHER_HAZE:
            case WeatherType.SWA_WEATHER_HAZE:
                return context.getString(R.string.api_weather_description_haze);
            case WeatherDescription.WEATHER_DESCRIPTION_SHOWER:
            case WeatherType.OPPO_CHINA_WEATHER_OVERCAST:
            case WeatherType.OPPO_FOREIGN_WEATHER_OVERCAST:
            case WeatherType.SWA_WEATHER_OVERCAST:
                return context.getString(R.string.api_weather_description_overcast);
            case WeatherDescription.WEATHER_DESCRIPTION_DOWNPOUR:
            case WeatherType.OPPO_CHINA_WEATHER_FOGGY:
            case WeatherType.OPPO_FOREIGN_WEATHER_FOGGY:
            case WeatherType.SWA_WEATHER_FOGGY:
                return context.getString(R.string.api_weather_description_fog);
            case WeatherDescription.WEATHER_DESCRIPTION_RAINSTORM:
            case WeatherType.OPPO_CHINA_WEATHER_SHOWER:
            case WeatherType.OPPO_FOREIGN_WEATHER_SHOWER:
            case WeatherType.SWA_WEATHER_SHOWER:
                return context.getString(R.string.api_weather_description_shower);
            case WeatherDescription.WEATHER_DESCRIPTION_SNOW:
            case WeatherType.OPPO_CHINA_WEATHER_THUNDERSHOWER:
            case WeatherType.OPPO_CHINA_WEATHER_THUNDERSHOWER_WITH_HAIL:
            case WeatherType.OPPO_FOREIGN_WEATHER_THUNDERSHOWER:
            case WeatherType.OPPO_FOREIGN_WEATHER_THUNDERSHOWER_WITH_HAIL:
            case WeatherType.SWA_WEATHER_THUNDERSHOWER:
            case WeatherType.SWA_WEATHER_THUNDERSHOWER_WITH_HAIL:
                return context.getString(R.string.api_weather_description_thundershower);
            case WeatherDescription.WEATHER_DESCRIPTION_THUNDERSHOWER:
            case WeatherType.ACCU_WEATHER_ICE:
            case WeatherType.ACCU_WEATHER_SLEET:
            case WeatherType.ACCU_WEATHER_FREEZING_RAIN:
            case WeatherType.ACCU_WEATHER_RAIN_AND_SNOW:
            case WeatherType.OPPO_CHINA_WEATHER_HEAVY_RAIN:
            case WeatherType.OPPO_CHINA_WEATHER_HEAVY_RAIN_TO_STORM:
            case WeatherType.OPPO_FOREIGN_WEATHER_HEAVY_RAIN:
            case WeatherType.OPPO_FOREIGN_WEATHER_HEAVY_RAIN_TO_STORM:
            case WeatherType.SWA_WEATHER_HEAVY_RAIN:
            case WeatherType.SWA_WEATHER_HEAVY_RAIN_TO_STORM:
                return context.getString(R.string.api_weather_description_downpour);
            case WeatherDescription.WEATHER_DESCRIPTION_SANDSTORM:
            case WeatherDescription.WEATHER_DESCRIPTION_FOG:
                return context.getString(R.string.api_weather_description_flurry);
            case WeatherDescription.WEATHER_DESCRIPTION_HAZE:
            case WeatherType.OPPO_CHINA_WEATHER_MODERATE_SNOW:
            case WeatherType.OPPO_CHINA_WEATHER_MODERATE_TO_HEAVY_SNOW:
            case WeatherType.OPPO_FOREIGN_WEATHER_MODERATE_SNOW:
            case WeatherType.OPPO_FOREIGN_WEATHER_MODERATE_TO_HEAVY_SNOW:
            case WeatherType.SWA_WEATHER_MODERATE_SNOW:
            case WeatherType.SWA_WEATHER_MODERATE_TO_HEAVY_SNOW:
                return context.getString(R.string.api_weather_description_snow);
            case WeatherType.OPPO_CHINA_WEATHER_SLEET:
            case WeatherType.OPPO_CHINA_WEATHER_MODERATE_RAIN:
            case WeatherType.OPPO_CHINA_WEATHER_ICE_RAIN:
            case WeatherType.OPPO_CHINA_WEATHER_MODERATE_TO_HEAVY_RAIN:
            case WeatherType.OPPO_FOREIGN_WEATHER_SLEET:
            case WeatherType.OPPO_FOREIGN_WEATHER_MODERATE_RAIN:
            case WeatherType.OPPO_FOREIGN_WEATHER_ICE_RAIN:
            case WeatherType.OPPO_FOREIGN_WEATHER_MODERATE_TO_HEAVY_RAIN:
            case WeatherType.SWA_WEATHER_SLEET:
            case WeatherType.SWA_WEATHER_MODERATE_RAIN:
            case WeatherType.SWA_WEATHER_ICE_RAIN:
            case WeatherType.SWA_WEATHER_MODERATE_TO_HEAVY_RAIN:
                return context.getString(R.string.api_weather_description_rain);
            case WeatherType.OPPO_CHINA_WEATHER_LIGHT_RAIN:
            case WeatherType.OPPO_CHINA_WEATHER_LIGHT_TO_MODERATE_RAIN:
            case WeatherType.OPPO_FOREIGN_WEATHER_LIGHT_RAIN:
            case WeatherType.OPPO_FOREIGN_WEATHER_LIGHT_TO_MODERATE_RAIN:
            case WeatherType.SWA_WEATHER_LIGHT_RAIN:
            case WeatherType.SWA_WEATHER_LIGHT_TO_MODERATE_RAIN:
                return context.getString(R.string.api_weather_description_drizzle);
            case WeatherType.OPPO_CHINA_WEATHER_STORM:
            case WeatherType.OPPO_CHINA_WEATHER_HEAVY_STORM:
            case WeatherType.OPPO_CHINA_WEATHER_SEVERE_STORM:
            case WeatherType.OPPO_CHINA_WEATHER_STORM_TO_HEAVY_STORM:
            case WeatherType.OPPO_CHINA_WEATHER_HEAVY_TO_SEVERE_STORM:
            case WeatherType.OPPO_FOREIGN_WEATHER_STORM:
            case WeatherType.OPPO_FOREIGN_WEATHER_HEAVY_STORM:
            case WeatherType.OPPO_FOREIGN_WEATHER_SEVERE_STORM:
            case WeatherType.OPPO_FOREIGN_WEATHER_STORM_TO_HEAVY_STORM:
            case WeatherType.OPPO_FOREIGN_WEATHER_HEAVY_TO_SEVERE_STORM:
            case WeatherType.SWA_WEATHER_STORM:
            case WeatherType.SWA_WEATHER_HEAVY_STORM:
            case WeatherType.SWA_WEATHER_SEVERE_STORM:
            case WeatherType.SWA_WEATHER_STORM_TO_HEAVY_STORM:
            case WeatherType.SWA_WEATHER_HEAVY_TO_SEVERE_STORM:
                return context.getString(R.string.api_weather_description_rainstorm);
            case WeatherType.OPPO_CHINA_WEATHER_SNOW_FLURRY:
            case WeatherType.OPPO_CHINA_WEATHER_HEAVY_SNOW:
            case WeatherType.OPPO_CHINA_WEATHER_SNOWSTORM:
            case WeatherType.OPPO_CHINA_WEATHER_HEAVY_SNOW_TO_SNOWSTORM:
            case WeatherType.OPPO_FOREIGN_WEATHER_SNOW_FLURRY:
            case WeatherType.OPPO_FOREIGN_WEATHER_HEAVY_SNOW:
            case WeatherType.OPPO_FOREIGN_WEATHER_SNOWSTORM:
            case WeatherType.OPPO_FOREIGN_WEATHER_HEAVY_SNOW_TO_SNOWSTORM:
            case WeatherType.SWA_WEATHER_SNOW_FLURRY:
            case WeatherType.SWA_WEATHER_HEAVY_SNOW:
            case WeatherType.SWA_WEATHER_SNOWSTORM:
            case WeatherType.SWA_WEATHER_HEAVY_SNOW_TO_SNOWSTORM:
                return context.getString(R.string.api_weather_description_snowstorm);
            case WeatherType.OPPO_CHINA_WEATHER_LIGHT_SNOW:
            case WeatherType.OPPO_CHINA_WEATHER_LIGHT_TO_MODERATE_SNOW:
            case WeatherType.OPPO_FOREIGN_WEATHER_LIGHT_SNOW:
            case WeatherType.OPPO_FOREIGN_WEATHER_LIGHT_TO_MODERATE_SNOW:
            case WeatherType.SWA_WEATHER_LIGHT_SNOW:
            case WeatherType.SWA_WEATHER_LIGHT_TO_MODERATE_SNOW:
                return context.getString(R.string.api_weather_description_flurry);
            case WeatherType.OPPO_CHINA_WEATHER_DUSTSTORM:
            case WeatherType.OPPO_CHINA_WEATHER_DUST:
            case WeatherType.OPPO_CHINA_WEATHER_SAND:
            case WeatherType.OPPO_CHINA_WEATHER_SANDSTORM:
            case WeatherType.OPPO_FOREIGN_WEATHER_DUSTSTORM:
            case WeatherType.OPPO_FOREIGN_WEATHER_DUST:
            case WeatherType.OPPO_FOREIGN_WEATHER_SAND:
            case WeatherType.OPPO_FOREIGN_WEATHER_SANDSTORM:
            case WeatherType.SWA_WEATHER_DUSTSTORM:
            case WeatherType.SWA_WEATHER_DUST:
            case WeatherType.SWA_WEATHER_SAND:
            case WeatherType.SWA_WEATHER_SANDSTORM:
                return context.getString(R.string.api_weather_description_sandstorm);
            default:
                return context.getString(R.string.api_weather_description_unknown);
        }
    }

   /* public static int oppoChinaWeatherTextToWeatherId(String name) {
        Object obj = -1;
        switch (name.hashCode()) {
            case -1236115480:
                if (name.equals("\u96f7\u9635\u96e8\u4f34\u6709\u51b0\u96f9")) {
                    obj = RainSurfaceView.RAIN_LEVEL_THUNDERSHOWER;
                }
                break;
            case -1229291873:
                if (name.equals("\u66b4\u96e8\u5230\u5927\u66b4\u96e8")) {
                    obj = net.oneplus.weather.R.styleable.Toolbar_titleMarginStart;
                }
                break;
            case 26228:
                if (name.equals("\u6674")) {
                    int i = 0;
                }
                break;
            case 38452:
                if (name.equals("\u9634")) {
                    obj = RainSurfaceView.RAIN_LEVEL_SHOWER;
                }
                break;
            case 38634:
                if (name.equals("\u96ea")) {
                    obj = net.oneplus.weather.R.styleable.OneplusTheme_op_borderWidth;
                }
                break;
            case 38654:
                if (name.equals("\u96fe")) {
                    obj = ConnectionResult.SERVICE_UPDATING;
                }
                break;
            case 38718:
                if (name.equals("\u973e")) {
                    obj = net.oneplus.weather.R.styleable.OneplusTheme_op_elevation;
                }
                break;
            case 659035:
                if (name.equals("\u4e2d\u96e8")) {
                    obj = DetectedActivity.RUNNING;
                }
                break;
            case 659037:
                if (name.equals("\u4e2d\u96ea")) {
                    obj = ConnectionResult.INTERRUPTED;
                }
                break;
            case 687245:
                if (name.equals("\u51bb\u96e8")) {
                    obj = ConnectionResult.SERVICE_MISSING_PERMISSION;
                }
                break;
            case 727223:
                if (name.equals("\u591a\u4e91")) {
                    obj = 1;
                }
                break;
            case 746145:
                if (name.equals("\u5927\u96e8")) {
                    obj = ConnectionResult.SERVICE_INVALID;
                }
                break;
            case 746147:
                if (name.equals("\u5927\u96ea")) {
                    obj = ConnectionResult.API_UNAVAILABLE;
                }
                break;
            case 746167:
                if (name.equals("\u5927\u96fe")) {
                    obj = net.oneplus.weather.R.styleable.OneplusTheme_op_pressedTranslationZ;
                }
                break;
            case 769209:
                if (name.equals("\u5c0f\u96e8")) {
                    obj = DetectedActivity.WALKING;
                }
                break;
            case 769211:
                if (name.equals("\u5c0f\u96ea")) {
                    obj = ConnectionResult.TIMEOUT;
                }
                break;
            case 808877:
                if (name.equals("\u626c\u6c99")) {
                    obj = RainDownpour.Z_RANDOM_RANGE;
                }
                break;
            case 853684:
                if (name.equals("\u66b4\u96e8")) {
                    obj = ConnectionResult.DEVELOPER_ERROR;
                }
                break;
            case 853686:
                if (name.equals("\u66b4\u96ea")) {
                    obj = ConnectionResult.SIGN_IN_FAILED;
                }
                break;
            case 892010:
                if (name.equals("\u6d6e\u5c18")) {
                    obj = net.oneplus.weather.R.styleable.OneplusTheme_onePlusTabTextSelectedAlpha;
                }
                break;
            case 906251:
                if (name.equals("\u6d53\u96fe")) {
                    obj = ItemTouchHelper.END;
                }
                break;
            case 1230675:
                if (name.equals("\u9635\u96e8")) {
                    obj = RainSurfaceView.RAIN_LEVEL_DOWNPOUR;
                }
                break;
            case 1230677:
                if (name.equals("\u9635\u96ea")) {
                    obj = ConnectionResult.CANCELED;
                }
                break;
            case 20022341:
                if (name.equals("\u4e2d\u5ea6\u973e")) {
                    obj = net.oneplus.weather.R.styleable.OneplusTheme_op_listItemLayout;
                }
                break;
            case 20420598:
                if (name.equals("\u4e25\u91cd\u973e")) {
                    obj = net.oneplus.weather.R.styleable.OneplusTheme_op_multiChoiceItemLayout;
                }
                break;
            case 22786587:
                if (name.equals("\u5927\u66b4\u96e8")) {
                    obj = ConnectionResult.LICENSE_CHECK_FAILED;
                }
                break;
            case 24333509:
                if (name.equals("\u5f3a\u6d53\u96fe")) {
                    obj = net.oneplus.weather.R.styleable.OneplusTheme_op_buttonPanelSideLayout;
                }
                break;
            case 27473909:
                if (name.equals("\u6c99\u5c18\u66b4")) {
                    obj = ConnectionResult.RESTRICTED_PROFILE;
                }
                break;
            case 36659173:
                if (name.equals("\u91cd\u5ea6\u973e")) {
                    obj = net.oneplus.weather.R.styleable.OneplusTheme_op_listLayout;
                }
                break;
            case 37872057:
                if (name.equals("\u96e8\u5939\u96ea")) {
                    obj = 6;
                }
                break;
            case 38370442:
                if (name.equals("\u96f7\u9635\u96e8")) {
                    obj = RainSurfaceView.RAIN_LEVEL_RAINSTORM;
                }
                break;
            case 617172868:
                if (name.equals("\u4e2d\u5230\u5927\u96e8")) {
                    obj = net.oneplus.weather.R.styleable.Toolbar_titleMarginBottom;
                }
                break;
            case 617172870:
                if (name.equals("\u4e2d\u5230\u5927\u96ea")) {
                    obj = net.oneplus.weather.R.styleable.Toolbar_titleTextAppearance;
                }
                break;
            case 700993117:
                if (name.equals("\u5927\u5230\u66b4\u96e8")) {
                    obj = net.oneplus.weather.R.styleable.Toolbar_titleMarginEnd;
                }
                break;
            case 700993119:
                if (name.equals("\u5927\u5230\u66b4\u96ea")) {
                    obj = net.oneplus.weather.R.styleable.Toolbar_titleTextColor;
                }
                break;
            case 722962972:
                if (name.equals("\u5c0f\u5230\u4e2d\u96e8")) {
                    obj = net.oneplus.weather.R.styleable.Toolbar_titleMargin;
                }
                break;
            case 722962974:
                if (name.equals("\u5c0f\u5230\u4e2d\u96ea")) {
                    obj = net.oneplus.weather.R.styleable.Toolbar_titleMargins;
                }
                break;
            case 753718907:
                if (name.equals("\u5f3a\u6c99\u5c18\u66b4")) {
                    obj = net.oneplus.weather.R.styleable.OneplusTheme_onePlusTextColor;
                }
                break;
            case 895811842:
                if (name.equals("\u7279\u5927\u66b4\u96e8")) {
                    obj = WeatherCircleView.ARC_DIN;
                }
                break;
            case 897358764:
                if (name.equals("\u7279\u5f3a\u6d53\u96fe")) {
                    obj = net.oneplus.weather.R.styleable.OneplusTheme_op_rippleColor;
                }
                break;
            case 1204232695:
                if (name.equals("\u5927\u66b4\u96e8\u5230\u7279\u5927\u66b4\u96e8")) {
                    obj = MessagingStyle.MAXIMUM_RETAINED_MESSAGES;
                }
                break;
        }
        switch (i) {
            case RainSurfaceView.RAIN_LEVEL_DRIZZLE:
                return WeatherType.OPPO_CHINA_WEATHER_SUNNY;
            case RainSurfaceView.RAIN_LEVEL_NORMAL_RAIN:
                return WeatherType.OPPO_CHINA_WEATHER_CLOUDY;
            case RainSurfaceView.RAIN_LEVEL_SHOWER:
                return WeatherType.OPPO_CHINA_WEATHER_OVERCAST;
            case RainSurfaceView.RAIN_LEVEL_DOWNPOUR:
                return WeatherType.OPPO_CHINA_WEATHER_SHOWER;
            case RainSurfaceView.RAIN_LEVEL_RAINSTORM:
                return WeatherType.OPPO_CHINA_WEATHER_THUNDERSHOWER;
            case RainSurfaceView.RAIN_LEVEL_THUNDERSHOWER:
                return WeatherType.OPPO_CHINA_WEATHER_THUNDERSHOWER_WITH_HAIL;
            case 6:
                return WeatherType.OPPO_CHINA_WEATHER_SLEET;
            case DetectedActivity.WALKING:
                return WeatherType.OPPO_CHINA_WEATHER_LIGHT_RAIN;
            case DetectedActivity.RUNNING:
                return WeatherType.OPPO_CHINA_WEATHER_MODERATE_RAIN;
            case ConnectionResult.SERVICE_INVALID:
                return WeatherType.OPPO_CHINA_WEATHER_HEAVY_RAIN;
            case ConnectionResult.DEVELOPER_ERROR:
                return WeatherType.OPPO_CHINA_WEATHER_STORM;
            case ConnectionResult.LICENSE_CHECK_FAILED:
                return WeatherType.OPPO_CHINA_WEATHER_HEAVY_STORM;
            case WeatherCircleView.ARC_DIN:
                return WeatherType.OPPO_CHINA_WEATHER_SEVERE_STORM;
            case ConnectionResult.CANCELED:
                return WeatherType.OPPO_CHINA_WEATHER_SNOW_FLURRY;
            case ConnectionResult.TIMEOUT:
                return WeatherType.OPPO_CHINA_WEATHER_LIGHT_SNOW;
            case ConnectionResult.INTERRUPTED:
                return WeatherType.OPPO_CHINA_WEATHER_MODERATE_SNOW;
            case ConnectionResult.API_UNAVAILABLE:
                return WeatherType.OPPO_CHINA_WEATHER_HEAVY_SNOW;
            case ConnectionResult.SIGN_IN_FAILED:
                return WeatherType.OPPO_CHINA_WEATHER_SNOWSTORM;
            case ConnectionResult.SERVICE_UPDATING:
                return WeatherType.OPPO_CHINA_WEATHER_FOGGY;
            case ConnectionResult.SERVICE_MISSING_PERMISSION:
                return WeatherType.OPPO_CHINA_WEATHER_ICE_RAIN;
            case ConnectionResult.RESTRICTED_PROFILE:
                return WeatherType.OPPO_CHINA_WEATHER_DUSTSTORM;
            case net.oneplus.weather.R.styleable.Toolbar_titleMargin:
                return WeatherType.OPPO_CHINA_WEATHER_LIGHT_TO_MODERATE_RAIN;
            case net.oneplus.weather.R.styleable.Toolbar_titleMarginBottom:
                return WeatherType.OPPO_CHINA_WEATHER_MODERATE_TO_HEAVY_RAIN;
            case net.oneplus.weather.R.styleable.Toolbar_titleMarginEnd:
                return WeatherType.OPPO_CHINA_WEATHER_HEAVY_RAIN_TO_STORM;
            case net.oneplus.weather.R.styleable.Toolbar_titleMarginStart:
                return WeatherType.OPPO_CHINA_WEATHER_STORM_TO_HEAVY_STORM;
            case MessagingStyle.MAXIMUM_RETAINED_MESSAGES:
                return WeatherType.OPPO_CHINA_WEATHER_HEAVY_TO_SEVERE_STORM;
            case net.oneplus.weather.R.styleable.Toolbar_titleMargins:
                return WeatherType.OPPO_CHINA_WEATHER_LIGHT_TO_MODERATE_SNOW;
            case net.oneplus.weather.R.styleable.Toolbar_titleTextAppearance:
                return WeatherType.OPPO_CHINA_WEATHER_MODERATE_TO_HEAVY_SNOW;
            case net.oneplus.weather.R.styleable.Toolbar_titleTextColor:
                return WeatherType.OPPO_CHINA_WEATHER_HEAVY_SNOW_TO_SNOWSTORM;
            case net.oneplus.weather.R.styleable.OneplusTheme_onePlusTabTextSelectedAlpha:
                return WeatherType.OPPO_CHINA_WEATHER_DUST;
            case RainDownpour.Z_RANDOM_RANGE:
                return WeatherType.OPPO_CHINA_WEATHER_SAND;
            case net.oneplus.weather.R.styleable.OneplusTheme_onePlusTextColor:
                return WeatherType.OPPO_CHINA_WEATHER_SANDSTORM;
            case ItemTouchHelper.END:
                return WeatherType.OPPO_CHINA_WEATHER_DENSE_FOGGY;
            case net.oneplus.weather.R.styleable.OneplusTheme_op_borderWidth:
                return WeatherType.OPPO_CHINA_WEATHER_SNOW;
            case net.oneplus.weather.R.styleable.OneplusTheme_op_buttonPanelSideLayout:
                return WeatherType.OPPO_CHINA_WEATHER_STRONG_DENSE_FOGGY;
            case net.oneplus.weather.R.styleable.OneplusTheme_op_elevation:
                return WeatherType.OPPO_CHINA_WEATHER_HAZE;
            case net.oneplus.weather.R.styleable.OneplusTheme_op_listItemLayout:
                return WeatherType.OPPO_CHINA_WEATHER_MODERATE_HAZE;
            case net.oneplus.weather.R.styleable.OneplusTheme_op_listLayout:
                return WeatherType.OPPO_CHINA_WEATHER_HEAVY_HAZE;
            case net.oneplus.weather.R.styleable.OneplusTheme_op_multiChoiceItemLayout:
                return WeatherType.OPPO_CHINA_WEATHER_SEVERE_HAZE;
            case net.oneplus.weather.R.styleable.OneplusTheme_op_pressedTranslationZ:
                return WeatherType.OPPO_CHINA_WEATHER_HEAVY_FOGGY;
            case net.oneplus.weather.R.styleable.OneplusTheme_op_rippleColor:
                return WeatherType.OPPO_CHINA_WEATHER_SEVERE_FOGGY;
            default:
                return 0;
        }
    }*/

    /*public static int oppoForeignWeatherTextToWeatherId(String name) {
        Object obj = -1;
        switch (name.hashCode()) {
            case -1236115480:
                if (name.equals("\u96f7\u9635\u96e8\u4f34\u6709\u51b0\u96f9")) {
                    obj = 6;
                }
                break;
            case -1229291873:
                if (name.equals("\u66b4\u96e8\u5230\u5927\u66b4\u96e8")) {
                    obj = MessagingStyle.MAXIMUM_RETAINED_MESSAGES;
                }
                break;
            case 26228:
                if (name.equals("\u6674")) {
                    int i = 0;
                }
                break;
            case 38452:
                if (name.equals("\u9634")) {
                    obj = RainSurfaceView.RAIN_LEVEL_DOWNPOUR;
                }
                break;
            case 38654:
                if (name.equals("\u96fe")) {
                    obj = ConnectionResult.SERVICE_MISSING_PERMISSION;
                }
                break;
            case 38718:
                if (name.equals("\u973e")) {
                    obj = net.oneplus.weather.R.styleable.OneplusTheme_op_borderWidth;
                }
                break;
            case 659035:
                if (name.equals("\u4e2d\u96e8")) {
                    obj = ConnectionResult.SERVICE_INVALID;
                }
                break;
            case 659037:
                if (name.equals("\u4e2d\u96ea")) {
                    obj = ConnectionResult.INTERRUPTED;
                }
                break;
            case 687245:
                if (name.equals("\u51bb\u96e8")) {
                    obj = ConnectionResult.RESTRICTED_PROFILE;
                }
                break;
            case 727223:
                if (name.equals("\u591a\u4e91")) {
                    obj = RainSurfaceView.RAIN_LEVEL_SHOWER;
                }
                break;
            case 746145:
                if (name.equals("\u5927\u96e8")) {
                    obj = ConnectionResult.DEVELOPER_ERROR;
                }
                break;
            case 746147:
                if (name.equals("\u5927\u96ea")) {
                    obj = ConnectionResult.API_UNAVAILABLE;
                }
                break;
            case 769209:
                if (name.equals("\u5c0f\u96e8")) {
                    obj = DetectedActivity.RUNNING;
                }
                break;
            case 769211:
                if (name.equals("\u5c0f\u96ea")) {
                    obj = ConnectionResult.TIMEOUT;
                }
                break;
            case 808877:
                if (name.equals("\u626c\u6c99")) {
                    obj = RainDownpour.Z_RANDOM_RANGE;
                }
                break;
            case 835893:
                if (name.equals("\u6674\u5929")) {
                    obj = 1;
                }
                break;
            case 853684:
                if (name.equals("\u66b4\u96e8")) {
                    obj = ConnectionResult.LICENSE_CHECK_FAILED;
                }
                break;
            case 853686:
                if (name.equals("\u66b4\u96ea")) {
                    obj = ConnectionResult.SERVICE_UPDATING;
                }
                break;
            case 892010:
                if (name.equals("\u6d6e\u5c18")) {
                    obj = net.oneplus.weather.R.styleable.OneplusTheme_onePlusTextColor;
                }
                break;
            case 1230675:
                if (name.equals("\u9635\u96e8")) {
                    obj = RainSurfaceView.RAIN_LEVEL_RAINSTORM;
                }
                break;
            case 1230677:
                if (name.equals("\u9635\u96ea")) {
                    obj = ConnectionResult.SIGN_IN_FAILED;
                }
                break;
            case 22786587:
                if (name.equals("\u5927\u66b4\u96e8")) {
                    obj = WeatherCircleView.ARC_DIN;
                }
                break;
            case 27473909:
                if (name.equals("\u6c99\u5c18\u66b4")) {
                    obj = net.oneplus.weather.R.styleable.Toolbar_titleMargin;
                }
                break;
            case 37872057:
                if (name.equals("\u96e8\u5939\u96ea")) {
                    obj = DetectedActivity.WALKING;
                }
                break;
            case 38370442:
                if (name.equals("\u96f7\u9635\u96e8")) {
                    obj = RainSurfaceView.RAIN_LEVEL_THUNDERSHOWER;
                }
                break;
            case 617172868:
                if (name.equals("\u4e2d\u5230\u5927\u96e8")) {
                    obj = net.oneplus.weather.R.styleable.Toolbar_titleMarginEnd;
                }
                break;
            case 617172870:
                if (name.equals("\u4e2d\u5230\u5927\u96ea")) {
                    obj = net.oneplus.weather.R.styleable.Toolbar_titleTextColor;
                }
                break;
            case 700993117:
                if (name.equals("\u5927\u5230\u66b4\u96e8")) {
                    obj = net.oneplus.weather.R.styleable.Toolbar_titleMarginStart;
                }
                break;
            case 700993119:
                if (name.equals("\u5927\u5230\u66b4\u96ea")) {
                    obj = net.oneplus.weather.R.styleable.OneplusTheme_onePlusTabTextSelectedAlpha;
                }
                break;
            case 722962972:
                if (name.equals("\u5c0f\u5230\u4e2d\u96e8")) {
                    obj = net.oneplus.weather.R.styleable.Toolbar_titleMarginBottom;
                }
                break;
            case 722962974:
                if (name.equals("\u5c0f\u5230\u4e2d\u96ea")) {
                    obj = net.oneplus.weather.R.styleable.Toolbar_titleTextAppearance;
                }
                break;
            case 753718907:
                if (name.equals("\u5f3a\u6c99\u5c18\u66b4")) {
                    obj = ItemTouchHelper.END;
                }
                break;
            case 895811842:
                if (name.equals("\u7279\u5927\u66b4\u96e8")) {
                    obj = ConnectionResult.CANCELED;
                }
                break;
            case 1204232695:
                if (name.equals("\u5927\u66b4\u96e8\u5230\u7279\u5927\u66b4\u96e8")) {
                    obj = net.oneplus.weather.R.styleable.Toolbar_titleMargins;
                }
                break;
        }
        switch (i) {
            case RainSurfaceView.RAIN_LEVEL_DRIZZLE:
            case RainSurfaceView.RAIN_LEVEL_NORMAL_RAIN:
                return WeatherType.OPPO_FOREIGN_WEATHER_SUNNY;
            case RainSurfaceView.RAIN_LEVEL_SHOWER:
                return WeatherType.OPPO_FOREIGN_WEATHER_CLOUDY;
            case RainSurfaceView.RAIN_LEVEL_DOWNPOUR:
                return WeatherType.OPPO_FOREIGN_WEATHER_OVERCAST;
            case RainSurfaceView.RAIN_LEVEL_RAINSTORM:
                return WeatherType.OPPO_FOREIGN_WEATHER_SHOWER;
            case RainSurfaceView.RAIN_LEVEL_THUNDERSHOWER:
                return WeatherType.OPPO_FOREIGN_WEATHER_THUNDERSHOWER;
            case 6:
                return WeatherType.OPPO_FOREIGN_WEATHER_THUNDERSHOWER_WITH_HAIL;
            case DetectedActivity.WALKING:
                return WeatherType.OPPO_FOREIGN_WEATHER_SLEET;
            case DetectedActivity.RUNNING:
                return WeatherType.OPPO_FOREIGN_WEATHER_LIGHT_RAIN;
            case ConnectionResult.SERVICE_INVALID:
                return WeatherType.OPPO_FOREIGN_WEATHER_MODERATE_RAIN;
            case ConnectionResult.DEVELOPER_ERROR:
                return WeatherType.OPPO_FOREIGN_WEATHER_HEAVY_RAIN;
            case ConnectionResult.LICENSE_CHECK_FAILED:
                return WeatherType.OPPO_FOREIGN_WEATHER_STORM;
            case WeatherCircleView.ARC_DIN:
                return WeatherType.OPPO_FOREIGN_WEATHER_HEAVY_STORM;
            case ConnectionResult.CANCELED:
                return WeatherType.OPPO_FOREIGN_WEATHER_SEVERE_STORM;
            case ConnectionResult.TIMEOUT:
                return WeatherType.OPPO_FOREIGN_WEATHER_LIGHT_SNOW;
            case ConnectionResult.INTERRUPTED:
                return WeatherType.OPPO_FOREIGN_WEATHER_MODERATE_SNOW;
            case ConnectionResult.API_UNAVAILABLE:
                return WeatherType.OPPO_FOREIGN_WEATHER_HEAVY_SNOW;
            case ConnectionResult.SIGN_IN_FAILED:
                return WeatherType.OPPO_FOREIGN_WEATHER_SNOW_FLURRY;
            case ConnectionResult.SERVICE_UPDATING:
                return WeatherType.OPPO_FOREIGN_WEATHER_SNOWSTORM;
            case ConnectionResult.SERVICE_MISSING_PERMISSION:
                return WeatherType.OPPO_FOREIGN_WEATHER_FOGGY;
            case ConnectionResult.RESTRICTED_PROFILE:
                return WeatherType.OPPO_FOREIGN_WEATHER_ICE_RAIN;
            case net.oneplus.weather.R.styleable.Toolbar_titleMargin:
                return WeatherType.OPPO_FOREIGN_WEATHER_DUSTSTORM;
            case net.oneplus.weather.R.styleable.Toolbar_titleMarginBottom:
                return WeatherType.OPPO_FOREIGN_WEATHER_LIGHT_TO_MODERATE_RAIN;
            case net.oneplus.weather.R.styleable.Toolbar_titleMarginEnd:
                return WeatherType.OPPO_FOREIGN_WEATHER_MODERATE_TO_HEAVY_RAIN;
            case net.oneplus.weather.R.styleable.Toolbar_titleMarginStart:
                return WeatherType.OPPO_FOREIGN_WEATHER_HEAVY_RAIN_TO_STORM;
            case MessagingStyle.MAXIMUM_RETAINED_MESSAGES:
                return WeatherType.OPPO_FOREIGN_WEATHER_STORM_TO_HEAVY_STORM;
            case net.oneplus.weather.R.styleable.Toolbar_titleMargins:
                return WeatherType.OPPO_FOREIGN_WEATHER_HEAVY_TO_SEVERE_STORM;
            case net.oneplus.weather.R.styleable.Toolbar_titleTextAppearance:
                return WeatherType.OPPO_FOREIGN_WEATHER_LIGHT_TO_MODERATE_SNOW;
            case net.oneplus.weather.R.styleable.Toolbar_titleTextColor:
                return WeatherType.OPPO_FOREIGN_WEATHER_MODERATE_TO_HEAVY_SNOW;
            case net.oneplus.weather.R.styleable.OneplusTheme_onePlusTabTextSelectedAlpha:
                return WeatherType.OPPO_FOREIGN_WEATHER_HEAVY_SNOW_TO_SNOWSTORM;
            case RainDownpour.Z_RANDOM_RANGE:
                return WeatherType.OPPO_FOREIGN_WEATHER_SAND;
            case net.oneplus.weather.R.styleable.OneplusTheme_onePlusTextColor:
                return WeatherType.OPPO_FOREIGN_WEATHER_DUST;
            case ItemTouchHelper.END:
                return WeatherType.OPPO_FOREIGN_WEATHER_SANDSTORM;
            case net.oneplus.weather.R.styleable.OneplusTheme_op_borderWidth:
                return WeatherType.OPPO_FOREIGN_WEATHER_HAZE;
            default:
                return 0;
        }
    }*/

    public static double centigradeToFahrenheit(double value) {
        return NumberUtils.isNaN(value) ? NumberUtils.NAN_DOUBLE : (1.8d * value) + 32.0d;
    }

    public static double fahrenheitToCentigrade(double value) {
        return NumberUtils.isNaN(value) ? NumberUtils.NAN_DOUBLE : (value - 32.0d) / 1.8d;
    }

    public static String getAccuLocale(Context context) {
        return getAccuLocale(context.getResources().getConfiguration().locale);
    }

    public static String getAccuLocale(Locale locale) {
        return locale.getLanguage().concat("-").concat(locale.getCountry());
    }

    public static int getTodayCurrentTemp(RootWeather weather) {
        return (weather.getCurrentWeather() == null || weather.getCurrentWeather().getTemperature() == null) ?
                Integer.MIN_VALUE : (int) Math.floor(weather.getCurrentWeather().getTemperature().getCentigradeValue());
    }

    public static DailyForecastsWeather getTodayForecast(RootWeather weather) {
        return DailyForecastsWeather.getTodayForecast(weather.getDailyForecastsWeather(), DateUtils.getTimeZone
                (weather.getCurrentWeather().getLocalTimeZone()));
    }

    public static int getTodayHighTemperature(RootWeather weather) {
        DailyForecastsWeather today = getTodayForecast(weather);
        return (today == null || today.getMaxTemperature() == null) ? Integer.MIN_VALUE : (int) Math.floor(today
                .getMaxTemperature()
                .getCentigradeValue());
    }

    public static int getTodayLowTemperature(RootWeather weather) {
        DailyForecastsWeather today = getTodayForecast(weather);
        return (today == null || today.getMinTemperature() == null) ? Integer.MIN_VALUE : (int) Math.floor(today
                .getMinTemperature().getCentigradeValue());
    }

    public static List<Alarm> getAlarmsRes(List<Alarm> alarms) {
        if (alarms == null || alarms.size() < 1) {
            return null;
        }
        List<Alarm> resAlarms = new ArrayList();
        resAlarms.add(alarms.get(0));
        int countWeather = alarms.size();
        int i = 1;
        while (i < countWeather) {
            try {
                Alarm tempAlarm = (Alarm) alarms.get(i);
                if (TextUtils.isEmpty(tempAlarm.getTypeName()) || TextUtils.isEmpty(tempAlarm.getContentText()) ||
                        tempAlarm.getTypeName().equalsIgnoreCase("None") || tempAlarm.getContentText()
                        .equalsIgnoreCase("None")) {
                    break;
                }
                int count = resAlarms.size();
                int j = 0;
                while (j < count && !((Alarm) resAlarms.get(j)).getTypeName().equals(tempAlarm.getTypeName())) {
                    j++;
                }
                if (j >= count) {
                    resAlarms.add(tempAlarm);
                }
                i++;
            } catch (Exception e) {
                return alarms;
            }
        }
        return resAlarms;
    }
}
