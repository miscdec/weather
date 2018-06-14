package com.opweather.gles20.programs;

import android.content.Context;
import android.opengl.GLES20;

import com.opweather.gles20.ShaderCodes;


public class RainShaderProgram extends ShaderProgram {
    private final int aColorLocation;
    private final int aPositionLocation;
    private final int uAlphaLocation;
    private final int uMatrixLocation;
    private final int uTextureUnitLocation;

    public /* bridge */ /* synthetic */ void useProgram() {
        super.useProgram();
    }

    public RainShaderProgram(Context context) {
        super(context, ShaderCodes.VERTEX_SHADER_RAIN, ShaderCodes.FRAGMENT_SHADER_RAIN);
        this.uMatrixLocation = GLES20.glGetUniformLocation(this.program, "u_Matrix");
        this.uAlphaLocation = GLES20.glGetUniformLocation(this.program, "u_Alpha");
        this.uTextureUnitLocation = GLES20.glGetUniformLocation(this.program, "u_TextureUnit");
        this.aPositionLocation = GLES20.glGetAttribLocation(this.program, "a_Position");
        this.aColorLocation = GLES20.glGetAttribLocation(this.program, "a_Color");
    }

    public void setUniforms(float[] matrix, int textureId, float alpha) {
        GLES20.glUniformMatrix4fv(this.uMatrixLocation, 1, false, matrix, 0);
        GLES20.glUniform1f(this.uAlphaLocation, alpha);
        GLES20.glActiveTexture(33984);
        GLES20.glBindTexture(3553, textureId);
        GLES20.glUniform1i(this.uTextureUnitLocation, 0);
    }

    public int getPositionAttributeLocation() {
        return this.aPositionLocation;
    }

    public int getColorAttributeLocation() {
        return this.aColorLocation;
    }
}
