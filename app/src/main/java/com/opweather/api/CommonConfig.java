package com.opweather.api;

import com.opweather.api.helper.DateUtils;
import com.opweather.api.helper.StringUtils;
import com.opweather.api.helper.WeatherUtils;

import java.util.Date;

public final class CommonConfig {
    static final boolean $assertionsDisabled = CommonConfig.class.desiredAssertionStatus();
    public static final String ACCU_API_KEY = "590df10b57554ac099b44ffbfaafa5bb";
    private static final String ACCU_API_KEY_DEV = "eey3z2dBNI896hIG08j7q1uxXzTxJqkZ";
    private static final String ACCU_API_KEY_PRD = "590df10b57554ac099b44ffbfaafa5bb";
    private static final String ACCU_API_VERSION = "v1";
    public static final String ACCU_HOST_NAME = "http://api.accuweather.com/";
    private static final String ACCU_HOST_NAME_DEV = "http://apidev.accuweather.com/";
    private static final String ACCU_HOST_NAME_PRD = "http://api.accuweather.com/";
    public static final boolean DEBUG = false;
    public static final String EMPTY_STRING = "";
    private static final String LOCAL_US = "en-us";
    public static final String OPPO_REQUEST_URL_CHINA_v1 = "http://weather.myoppo" +
            ".com/chinaWeather/smChinaWeathers/%s/%s-%s.xml";
    public static final String OPPO_REQUEST_URL_CHINA_v2 = "http://weather.myoppo" +
            ".com/chinaWeather/smChinaWeathersGz/%s/%s-%s.xml.gz";
    public static final String OPPO_REQUEST_URL_CHINA_v3 = "http://i1.weather.oppomobile" +
            ".com/chinaWeather/smChinaWeathersGz/%s/%s-%s.json.gz";
    public static final String OPPO_REQUEST_URL_CHINA_v3_BACKUP = "http://i2.weather.oppomobile" +
            ".com/chinaWeather/smChinaWeathersGz/%s/%s-%s.json.gz";
    public static final String OPPO_REQUEST_URL_FOREIGN = "http://newtq.myoppo" +
            ".com/weather_world/worldWeather_new/%s/%s-%s.xml";
    private static final String SWA_HOST_NAME = "http://webapi.weather.com.cn/";
    private static final String SWA_HOST_NAME_WITHOUT_KEY = "http://webapi.weather.com" +
            ".cn/data/?areaid=%s&type=%s&date=%s&appid=%s";
    private static final String SWA_HOST_NAME_WITH_KEY = "http://webapi.weather.com" +
            ".cn/data/?areaid=%s&type=%s&date=%s&appid=%s&key=%s";
    private static final String SWA_OPPO_APPID = "ac845ca00d98401c";
    private static final String SWA_OPPO_SECKEY = "yijia_webapi_data";
    private static final String SWA_TYPE_AIR = "air";
    private static final String SWA_TYPE_ALARM = "alarm";
    private static final String SWA_TYPE_CURRENT = "observe";
    private static final String SWA_TYPE_DAILY_FORECASTS = "forecast7d";
    private static final String SWA_TYPE_HOUR_FORECASTS = "hourfc";
    private static final String SWA_TYPE_INDEX = "index";
    public static final String WEATHER_REQUESTS = "CustomTags:WEATHER_REQUESTS";

    public static String getOppoChinaUrl(String key) {
        if ($assertionsDisabled || key != null) {
            return getOppoChinaUrl_v3(key, new Date(System.currentTimeMillis()));
        }
        throw new AssertionError();
    }

    public static String getOppoChinaUrl_v1(String key, Date date) {
        if ($assertionsDisabled || key != null) {
            String strDate = DateUtils.formatOppoRequestDateText(date);
            return String.format(OPPO_REQUEST_URL_CHINA_v1, new Object[]{strDate, key, strDate});
        }
        throw new AssertionError();
    }

    public static String getOppoChinaUrl_v2(String key, Date date) {
        if ($assertionsDisabled || key != null) {
            String strDate = DateUtils.formatOppoRequestDateText(date);
            return String.format(OPPO_REQUEST_URL_CHINA_v2, new Object[]{strDate, key, strDate});
        }
        throw new AssertionError();
    }

    public static String getOppoForeignUrl(String key) {
        if ($assertionsDisabled || key != null) {
            return getOppoForeignUrl(key, new Date(System.currentTimeMillis()));
        }
        throw new AssertionError();
    }

    public static String getOppoForeignUrl(String key, Date date) {
        if ($assertionsDisabled || key != null) {
            String strDate = DateUtils.formatOppoRequestDateText(date);
            return String.format(OPPO_REQUEST_URL_FOREIGN, new Object[]{strDate, key, strDate});
        }
        throw new AssertionError();
    }

    public static String getOppoChinaUrl_v3(String key, Date date) {
        if ($assertionsDisabled || key != null) {
            if (DateUtils.formatTimetoMinuteInt(date) < 15) {
                date = new Date(System.currentTimeMillis() - 900000);
            }
            String strDate = DateUtils.formatOppoRequestv3DateText(date);
            return String.format(OPPO_REQUEST_URL_CHINA_v3, new Object[]{strDate, key, strDate});
        }
        throw new AssertionError();
    }

    public static String getOppoChinaUrl_v3_backup(String key, Date date) {
        if ($assertionsDisabled || key != null) {
            String strDate = DateUtils.formatOppoRequestv3DateText(date);
            return String.format(OPPO_REQUEST_URL_CHINA_v3_BACKUP, new Object[]{strDate, key, strDate});
        }
        throw new AssertionError();
    }

    public static String getAccuAqiUrl(String key, String locale) {
        if ($assertionsDisabled || key != null) {
            if (StringUtils.isBlank(locale)) {
                locale = LOCAL_US;
            }
            return "http://api.accuweather.com/airquality/v1/observations/" + key + ".json?apikey=" +
                    ACCU_API_KEY_PRD + "&language=" + locale;
        }
        throw new AssertionError();
    }

    public static String getAccuCurrentUrl(String key, String locale) {
        if ($assertionsDisabled || key != null) {
            if (StringUtils.isBlank(locale)) {
                locale = LOCAL_US;
            }
            return "http://api.accuweather.com/currentconditions/v1/" + key + ".json?apikey=" + ACCU_API_KEY_PRD +
                    "&language=" + locale + "&details=true";
        }
        throw new AssertionError();
    }

    public static String getAccuHourForecastsUrl(String key, String locale) {
        if ($assertionsDisabled || key != null) {
            if (StringUtils.isBlank(locale)) {
                locale = LOCAL_US;
            }
            return "http://api.accuweather.com/forecasts/v1/hourly/24hour/" + key + "?apikey=" + ACCU_API_KEY_PRD +
                    "&language=" + locale + "&details=true&metric=true";
        }
        throw new AssertionError();
    }

    public static String getAccuDailyForecastsUrl(String key, String locale) {
        if ($assertionsDisabled || key != null) {
            if (StringUtils.isBlank(locale)) {
                locale = LOCAL_US;
            }
            return "http://api.accuweather.com/forecasts/v1/daily/10day/" + key + "?apikey=" + ACCU_API_KEY_PRD +
                    "&language=" + locale + "&details=true&metric=true";
        }
        throw new AssertionError();
    }

    public static String getAccuAlertsUrl(String key, String locale) {
        if ($assertionsDisabled || key != null) {
            if (StringUtils.isBlank(locale)) {
                locale = LOCAL_US;
            }
            return "http://api.accuweather.com/alerts/v1/" + key + ".json?apikey=" + ACCU_API_KEY_PRD + "&language="
                    + locale + "&details=true";
        }
        throw new AssertionError();
    }

    public static String getSwaAqiUrl(String key) {
        if ($assertionsDisabled || key != null) {
            return getSwaAqiUrl(key, new Date());
        }
        throw new AssertionError();
    }

    public static String getSwaAqiUrl(String key, Date date) {
        if ($assertionsDisabled || key != null) {
            return getSwaUrl(key, SWA_TYPE_AIR, date);
        }
        throw new AssertionError();
    }

    public static String getSwaIndexUrl(String key) {
        if ($assertionsDisabled || key != null) {
            return getSwaIndexUrl(key, new Date());
        }
        throw new AssertionError();
    }

    public static String getSwaIndexUrl(String key, Date date) {
        if ($assertionsDisabled || key != null) {
            return getSwaUrl(key, SWA_TYPE_INDEX, date);
        }
        throw new AssertionError();
    }

    public static String getSwaCurrentUrl(String key) {
        if ($assertionsDisabled || key != null) {
            return getSwaCurrentUrl(key, new Date());
        }
        throw new AssertionError();
    }

    public static String getSwaCurrentUrl(String key, Date date) {
        if ($assertionsDisabled || key != null) {
            return getSwaUrl(key, SWA_TYPE_CURRENT, date);
        }
        throw new AssertionError();
    }

    public static String getSwaHourForecastsUrl(String key) {
        if ($assertionsDisabled || key != null) {
            return getSwaHourForecastsUrl(key, new Date());
        }
        throw new AssertionError();
    }

    public static String getSwaHourForecastsUrl(String key, Date date) {
        if ($assertionsDisabled || key != null) {
            return getSwaUrl(key, SWA_TYPE_HOUR_FORECASTS, date);
        }
        throw new AssertionError();
    }

    public static String getSwaDailyForecastsUrl(String key) {
        if ($assertionsDisabled || key != null) {
            return getSwaDailyForecastsUrl(key, new Date());
        }
        throw new AssertionError();
    }

    public static String getSwaDailyForecastsUrl(String key, Date date) {
        if ($assertionsDisabled || key != null) {
            return getSwaUrl(key, SWA_TYPE_DAILY_FORECASTS, date);
        }
        throw new AssertionError();
    }

    public static String getSwaAlertsUrl(String key) {
        if ($assertionsDisabled || key != null) {
            return getSwaAlertsUrl(key, new Date());
        }
        throw new AssertionError();
    }

    public static String getSwaAlertsUrl(String key, Date date) {
        if ($assertionsDisabled || key != null) {
            return getSwaUrl(key, SWA_TYPE_ALARM, date);
        }
        throw new AssertionError();
    }

    private static String getSwaUrl(String id, String type, Date date) {
        String strDate = DateUtils.formatSwaRequestDateText(date);
        String key = WeatherUtils.swaStandardURLEncoder(String.format(SWA_HOST_NAME_WITHOUT_KEY, new Object[]{id,
                type, strDate, SWA_OPPO_APPID}), SWA_OPPO_SECKEY);
        return String.format(SWA_HOST_NAME_WITH_KEY, new Object[]{id, type, strDate, SWA_OPPO_APPID.substring(0,
                6), key});
    }
}
