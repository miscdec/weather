package com.opweather.gles20.programs;

import android.content.Context;
import android.opengl.GLES20;

import com.opweather.gles20.ShaderCodes;


public class SandShaderProgram extends ShaderProgram {
    private final int aColorLocation;
    private final int aDirectionVectorLocation;
    private final int aParticleStartTimeLocation;
    private final int aPositionLocation;
    private final int aSizeLocation;
    private final int uAlphaLocation;
    private final int uMatrixLocation;
    private final int uTextureUnitLocation;
    private final int uTimeLocation;

    public /* bridge */ /* synthetic */ void useProgram() {
        super.useProgram();
    }

    public SandShaderProgram(Context context) {
        super(context, ShaderCodes.VERTEX_SHADER_SAND_STORM, ShaderCodes.FRAGMENT_SHADER_SNOW);
        this.uMatrixLocation = GLES20.glGetUniformLocation(this.program, "u_Matrix");
        this.uTimeLocation = GLES20.glGetUniformLocation(this.program, "u_Time");
        this.uAlphaLocation = GLES20.glGetUniformLocation(this.program, "u_Alpha");
        this.uTextureUnitLocation = GLES20.glGetUniformLocation(this.program, "u_TextureUnit");
        this.aPositionLocation = GLES20.glGetAttribLocation(this.program, "a_Position");
        this.aColorLocation = GLES20.glGetAttribLocation(this.program, "a_Color");
        this.aDirectionVectorLocation = GLES20.glGetAttribLocation(this.program, "a_DirectionVector");
        this.aParticleStartTimeLocation = GLES20.glGetAttribLocation(this.program, "a_ParticleStartTime");
        this.aSizeLocation = GLES20.glGetAttribLocation(this.program, "a_Size");
    }

    public void setUniforms(float[] matrix, float elapsedTime, int textureId, float alpha) {
        GLES20.glUniformMatrix4fv(this.uMatrixLocation, 1, false, matrix, 0);
        GLES20.glUniform1f(this.uTimeLocation, elapsedTime);
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

    public int getDirectionVectorAttributeLocation() {
        return this.aDirectionVectorLocation;
    }

    public int getParticleStartTimeAttributeLocation() {
        return this.aParticleStartTimeLocation;
    }

    public int getSizeAttributeLocation() {
        return this.aSizeLocation;
    }
}
