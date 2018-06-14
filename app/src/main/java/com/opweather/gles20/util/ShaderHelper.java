package com.opweather.gles20.util;

import android.opengl.GLES20;
import android.util.Log;

public class ShaderHelper {
    private static final String TAG = "ShaderHelper";

    public static int compileVertexShader(String shaderCode) {
        return compileShader(35633, shaderCode);
    }

    public static int compileFragmentShader(String shaderCode) {
        return compileShader(35632, shaderCode);
    }

    private static int compileShader(int type, String shaderCode) {
        int shaderObjectId = GLES20.glCreateShader(type);
        if (shaderObjectId == 0) {
            return 0;
        }
        GLES20.glShaderSource(shaderObjectId, shaderCode);
        GLES20.glCompileShader(shaderObjectId);
        int[] compileStatus = new int[1];
        GLES20.glGetShaderiv(shaderObjectId, 35713, compileStatus, 0);
        if (compileStatus[0] != 0) {
            return shaderObjectId;
        }
        GLES20.glDeleteShader(shaderObjectId);
        return 0;
    }

    public static int linkProgram(int vertexShaderId, int fragmentShaderId) {
        int programObjectId = GLES20.glCreateProgram();
        if (programObjectId == 0) {
            return 0;
        }
        GLES20.glAttachShader(programObjectId, vertexShaderId);
        GLES20.glAttachShader(programObjectId, fragmentShaderId);
        GLES20.glLinkProgram(programObjectId);
        int[] linkStatus = new int[1];
        GLES20.glGetProgramiv(programObjectId, 35714, linkStatus, 0);
        if (linkStatus[0] != 0) {
            return programObjectId;
        }
        GLES20.glDeleteProgram(programObjectId);
        return 0;
    }

    public static boolean validateProgram(int programObjectId) {
        GLES20.glValidateProgram(programObjectId);
        int[] validateStatus = new int[1];
        GLES20.glGetProgramiv(programObjectId, 35715, validateStatus, 0);
        Log.v(TAG, "Results of validating program: " + validateStatus[0] + "\nLog:" + GLES20.glGetProgramInfoLog(programObjectId));
        return validateStatus[0] != 0;
    }

    public static int buildProgram(String vertexShaderSource, String fragmentShaderSource) {
        return linkProgram(compileVertexShader(vertexShaderSource), compileFragmentShader(fragmentShaderSource));
    }
}
