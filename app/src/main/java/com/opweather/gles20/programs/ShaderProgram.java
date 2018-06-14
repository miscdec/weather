package com.opweather.gles20.programs;

import android.content.Context;
import android.opengl.GLES20;

import com.opweather.gles20.util.ShaderHelper;


abstract class ShaderProgram {
    protected static final String A_ALPHA = "a_Alpha";
    protected static final String A_COLOR = "a_Color";
    protected static final String A_DIRECTION_VECTOR = "a_DirectionVector";
    protected static final String A_PARTICLE_START_TIME = "a_ParticleStartTime";
    protected static final String A_POSITION = "a_Position";
    protected static final String A_SIZE = "a_Size";
    protected static final String U_ALPHA = "u_Alpha";
    protected static final String U_MATRIX = "u_Matrix";
    protected static final String U_TEXTURE_UNIT = "u_TextureUnit";
    protected static final String U_TIME = "u_Time";
    protected final int program;

    protected ShaderProgram(Context context, String vertexShaderCode, String fragmentShaderCode) {
        this.program = ShaderHelper.buildProgram(vertexShaderCode, fragmentShaderCode);
    }

    public void useProgram() {
        GLES20.glUseProgram(this.program);
    }
}
