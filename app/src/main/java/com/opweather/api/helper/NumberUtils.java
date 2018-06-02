package com.opweather.api.helper;

public final class NumberUtils {
    public static final double NAN_DOUBLE = Double.NaN;
    public static final float NAN_FLOAT = Float.NaN;
    public static final int NAN_INT = Integer.MIN_VALUE;
    public static final long NAN_LONG = Long.MIN_VALUE;

    private NumberUtils() {
    }

    public static boolean valueToBoolean(String value, boolean defaultValue) {
        boolean result = false;
        if (value == null) {
            return defaultValue;
        }
        if (value.equals("1") || value.equals("true") || value.equals("TRUE")) {
            result = true;
        }
        return result;
    }

    public static final int valueToInt(String value) {
        if (value == null) {
            return NAN_INT;
        }
        try {
            return Integer.parseInt(value);
        } catch (NumberFormatException e) {
            return NAN_INT;
        }
    }

    public static final float valueToFloat(String value) {
        if (value == null) {
            return NAN_FLOAT;
        }
        try {
            return Float.parseFloat(value);
        } catch (NumberFormatException e) {
            return NAN_FLOAT;
        }
    }

    public static final double valueToDouble(String value) {
        if (value == null) {
            return NAN_DOUBLE;
        }
        try {
            return Double.parseDouble(value);
        } catch (NumberFormatException e) {
            return NAN_DOUBLE;
        }
    }

    public static final boolean isNaN(int value) {
        return value == Integer.MIN_VALUE;
    }

    public static final boolean isNaN(float value) {
        return Float.isNaN(value);
    }

    public static final boolean isNaN(double value) {
        return Double.isNaN(value);
    }

    public static final boolean isNaN(long value) {
        return value == Long.MIN_VALUE;
    }
}
