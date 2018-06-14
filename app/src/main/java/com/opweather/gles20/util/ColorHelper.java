package com.opweather.gles20.util;

public class ColorHelper {
    public static float getGLColorAlpha(int color) {
        return ((float) (color >>> 24)) / 255.0f;
    }

    public static float getGLColorRed(int color) {
        return ((float) ((color >> 16) & 255)) / 255.0f;
    }

    public static float getGLColorGreen(int color) {
        return ((float) ((color >> 8) & 255)) / 255.0f;
    }

    public static float getGLColorBlue(int color) {
        return ((float) (color & 255)) / 255.0f;
    }
}
