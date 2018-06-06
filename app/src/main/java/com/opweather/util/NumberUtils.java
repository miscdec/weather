package com.opweather.util;

public class NumberUtils {
    public static int parseInt(String intString, int defaultValue) {
        return parseInt(intString, 10, defaultValue);
    }

    public static int parseInt(String intString, int radix, int defaultValue) {
        if (intString == null || intString.length() == 0) {
            return defaultValue;
        }
        try {
            return Integer.parseInt(intString, radix);
        } catch (NumberFormatException e) {
            e.printStackTrace();
            return Integer.MIN_VALUE;
        }
    }

    public static int parseInt(double d) {
        long l = Math.round(d);
        if (l >= -2147483648L && l <= 2147483647L) {
            return (int) l;
        }
        throw new IllegalArgumentException(l + " cannot be cast to int without changing its value.");
    }

    public static int parseInt(long d) {
        if (d >= -2147483648L && d <= 2147483647L) {
            return Long.valueOf(d).intValue();
        }
        throw new IllegalArgumentException(d + " cannot be cast to int without changing its value.");
    }
}
