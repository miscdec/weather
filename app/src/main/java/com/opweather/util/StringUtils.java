package com.opweather.util;

public class StringUtils {
    public static final String EMPTY_STRING = "";
    private static final String WEATHER_URL = "http://m.weathercn.com/";

    public static String composeDailyTemperature(int highTemp, int lowTemp) {
        return highTemp + "\u00b0 /" + lowTemp + "\u00b0";
    }

    public static String getAccuMainMobileLink(String areakey, String local) {
        return WEATHER_URL + local + "/cn/baoan-district/" + areakey + "/current-weather/" + areakey +
                "?partner=oneplusglobal";
    }

    public static String getChinaMainMobileLink(String areakey, String local) {
        return "http://m.weathercn.com/index.do?language=" + local + "&smartid=" + areakey +
                com.opweather.api.helper.StringUtils.WEATHER_PARTNER_SUFFIX;
    }

    public static String getAqiMobileLink(String areaCode, String local) {
        return "http://m.weathercn.com/air-quality.do?language=" + local + "&smartid=" + areaCode +
                com.opweather.api.helper.StringUtils.WEATHER_PARTNER_SUFFIX;
    }

    public static String getLifeMobileLink(String areaCode, String local) {
        return "http://m.weathercn.com/livingindex.do?language=" + local + "&smartid=" + areaCode +
                com.opweather.api.helper.StringUtils.WEATHER_PARTNER_SUFFIX;
    }

    public static String getFifteendaysMobileLink(String areaCode, String local) {
        return "http://m.weathercn.com/index.do?language=" + local + "&smartid=" + areaCode +
                com.opweather.api.helper.StringUtils.WEATHER_PARTNER_SUFFIX;
    }
}
