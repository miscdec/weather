package com.opweather.api.helper;

public final class StringUtils {
    public static final String WEATHER_PARTNER_SUFFIX = "&partner=1000001029";
    private static final String WEATHER_URL = "http://m.weathercn.com/";

    private StringUtils() {
    }

    public static boolean isBlank(String string) {
        if (string == null || string.length() == 0) {
            return true;
        }
        int l = string.length();
        for (int i = 0; i < l; i++) {
            if (!isWhitespace(string.codePointAt(i))) {
                return false;
            }
        }
        return true;
    }

    public static boolean isWhitespace(int c) {
        return c == 32 || c == 9 || c == 10 || c == 12 || c == 13;
    }

    public static String getDailyMobileLink(String areaCode, int index) {
        return "http://m.weathercn.com/daily-weather-forecast.do?language=zh-cn&smartid=" + areaCode + "&day=" + index + WEATHER_PARTNER_SUFFIX;
    }

    public static String getPartner(String baseUrl) {
        if (baseUrl == null) {
            return "";
        }
        return baseUrl.contains("?") ? WEATHER_PARTNER_SUFFIX : "?partner=1000001029";
    }
}
