package com.opweather.gles20.util;

public class MatrixHelper {
    public static void perspectiveM(float[] m, float yFovInDegrees, float aspect, float n, float f) {
        float a = (float) (1.0d / Math.tan(((double) ((float) ((((double) yFovInDegrees) * 3.141592653589793d) / 180.0d))) / 2.0d));
        m[0] = a / aspect;
        m[1] = 0.0f;
        m[2] = 0.0f;
        m[3] = 0.0f;
        m[4] = 0.0f;
        m[5] = a;
        m[6] = 0.0f;
        m[7] = 0.0f;
        m[8] = 0.0f;
        m[9] = 0.0f;
        m[10] = -((f + n) / (f - n));
        m[11] = -1.0f;
        m[12] = 0.0f;
        m[13] = 0.0f;
        m[14] = -(((2.0f * f) * n) / (f - n));
        m[15] = 0.0f;
    }
}
