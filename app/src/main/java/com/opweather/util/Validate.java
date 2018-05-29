package com.opweather.util;

public class Validate {
    public static void notEmpty(String string, String msg) {
        if (string == null || string.length() == 0) {
            throw new IllegalArgumentException(msg);
        }
    }
}
