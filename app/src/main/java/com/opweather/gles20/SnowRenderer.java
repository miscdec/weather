package com.opweather.gles20;

import android.content.Context;
import android.opengl.GLES20;
import android.opengl.GLSurfaceView.Renderer;
import android.opengl.Matrix;
import android.support.v4.widget.AutoScrollHelper;

import com.android.volley.DefaultRetryPolicy;
import com.opweather.R;
import com.opweather.gles20.objects.SnowParticleFlurry;
import com.opweather.gles20.objects.SnowParticleNormal;
import com.opweather.gles20.objects.SnowParticleShooter;
import com.opweather.gles20.objects.SnowParticleStorm;
import com.opweather.gles20.objects.SnowParticleSystem;
import com.opweather.gles20.programs.SnowShaderProgram;
import com.opweather.gles20.util.Geometry;
import com.opweather.gles20.util.Geometry.Vector;
import com.opweather.gles20.util.MatrixHelper;
import com.opweather.gles20.util.TextureHelper;


import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

public class SnowRenderer implements Renderer {
    private static final int GREEN_SNOW_PARTICLE_COLOR_DAY = -16724992;
    private static final int GREEN_SNOW_PARTICLE_COLOR_NIGHT = -10039894;
    public static final int SNOW_LEVEL_FLURRY = 0;
    public static final int SNOW_LEVEL_NORMAL = 1;
    public static final int SNOW_LEVEL_STORM = 2;
    private static final String TAG = "SnowRenderer";
    private static final int WHITE_SNOW_PARTICLE_COLOR_DAY = -1;
    private static final int WHITE_SNOW_PARTICLE_COLOR_NIGHT = -8279340;
    private int COUNT_PER_FRAME;
    private int SNOW_PARTICLE_COUNT_POINT;
    private int SNOW_PARTICLE_COUNT_POLYHEDRON;
    private long globalStartTime;
    private float mAlpha;
    private final Context mContext;
    private boolean mIsDay;
    private final float[] modelMatrix;
    private final float[] modelProjectionMatrix;
    private int pointTexture;
    private int polyhedronTexture;
    private final float[] projectionMatrix;
    private int snowLevel;
    private SnowParticleShooter snowParticleShooter;
    private SnowParticleSystem snowParticleSystem;
    private SnowShaderProgram snowShaderProgram;
    private float xRotation;
    private float yRotation;
    private float zRotation;

    public SnowRenderer(Context context, boolean isDay, int snowLevel) {
        this.snowLevel = 1;
        this.SNOW_PARTICLE_COUNT_POLYHEDRON = 100;
        this.SNOW_PARTICLE_COUNT_POINT = 500;
        this.COUNT_PER_FRAME = 1;
        this.modelMatrix = new float[16];
        this.projectionMatrix = new float[16];
        this.modelProjectionMatrix = new float[16];
        this.mAlpha = 1.0f;
        this.mContext = context;
        this.mIsDay = isDay;
        this.snowLevel = snowLevel;
    }

    private SnowParticleShooter getParticleShooter(Vector direction, int color1, int color2) {
        switch (this.snowLevel) {
            case SNOW_LEVEL_FLURRY:
                this.COUNT_PER_FRAME = 1;
                this.SNOW_PARTICLE_COUNT_POLYHEDRON = 70;
                this.SNOW_PARTICLE_COUNT_POINT = 500;
                return new SnowParticleFlurry(direction, color1, color2);
            case SNOW_LEVEL_NORMAL:
                this.COUNT_PER_FRAME = 1;
                this.SNOW_PARTICLE_COUNT_POLYHEDRON = 100;
                this.SNOW_PARTICLE_COUNT_POINT = 900;
                return new SnowParticleNormal(direction, color1, color2);
            case SNOW_LEVEL_STORM:
                this.COUNT_PER_FRAME = 2;
                this.SNOW_PARTICLE_COUNT_POLYHEDRON = 120;
                this.SNOW_PARTICLE_COUNT_POINT = 3000;
                return new SnowParticleStorm(direction, color1, color2);
            default:
                return new SnowParticleNormal(direction, color1, color2);
        }
    }

    public void onSurfaceCreated(GL10 gl, EGLConfig config) {
        GLES20.glClearColor(AutoScrollHelper.RELATIVE_UNSPECIFIED, AutoScrollHelper.RELATIVE_UNSPECIFIED,
                AutoScrollHelper.RELATIVE_UNSPECIFIED, AutoScrollHelper.RELATIVE_UNSPECIFIED);
        Vector particleDirection = new Vector(0.0f, -1.0f, 0.0f);
        int color1 = GREEN_SNOW_PARTICLE_COLOR_DAY;
        int color2 = WHITE_SNOW_PARTICLE_COLOR_DAY;
        if (!this.mIsDay) {
            color1 = GREEN_SNOW_PARTICLE_COLOR_NIGHT;
            color2 = WHITE_SNOW_PARTICLE_COLOR_NIGHT;
        }
        this.snowParticleShooter = getParticleShooter(particleDirection, color1, color2);
        this.snowShaderProgram = new SnowShaderProgram(this.mContext);
        this.snowParticleSystem = new SnowParticleSystem(this.SNOW_PARTICLE_COUNT_POLYHEDRON, this
                .SNOW_PARTICLE_COUNT_POINT);
        this.globalStartTime = System.nanoTime();
        this.polyhedronTexture = TextureHelper.loadTexture(this.mContext, R.mipmap.snow);
        this.pointTexture = TextureHelper.loadTexture(this.mContext, R.mipmap.snow_point);
    }

    public void onSurfaceChanged(GL10 gl, int width, int height) {
        GLES20.glViewport(SNOW_LEVEL_FLURRY, SNOW_LEVEL_FLURRY, width, height);
        MatrixHelper.perspectiveM(this.projectionMatrix, 45.0f, ((float) width) / ((float) height),
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT, 3.0f);
        updateModelMatrices();
    }

    public void onDrawFrame(GL10 gl) {
        GLES20.glClear(16640);
        drawSnowParticles();
    }

    public void setIsDay(boolean isDay) {
        if (isDay != this.mIsDay) {
            this.mIsDay = isDay;
            int color1 = GREEN_SNOW_PARTICLE_COLOR_DAY;
            int color2 = WHITE_SNOW_PARTICLE_COLOR_DAY;
            if (!this.mIsDay) {
                color1 = WHITE_SNOW_PARTICLE_COLOR_NIGHT;
                color2 = GREEN_SNOW_PARTICLE_COLOR_NIGHT;
            }
            this.snowParticleShooter.changeSnowColor(color1, color2);
        }
    }

    public void handleTouch(float x, float y) {
    }

    public void setRotation(float xRotation, float yRotation, float zRotation) {
        this.xRotation = convertXRotation(xRotation, yRotation, zRotation);
        this.yRotation = convertYRotation(xRotation, yRotation, zRotation);
        this.zRotation = convertZRotation(xRotation, yRotation, zRotation);
        updateModelMatrices();
    }

    private float convertXRotation(float sxr, float syr, float szr) {
        return (syr <= -180.0f || syr >= 90.0f) ? syr - 270.0f : syr + 90.0f;
    }

    private float convertYRotation(float sxr, float syr, float szr) {
        return AutoScrollHelper.RELATIVE_UNSPECIFIED;
    }

    private float convertZRotation(float sxr, float syr, float szr) {
        return -szr;
    }

    private void updateModelMatrices() {
        Matrix.setIdentityM(this.modelMatrix, SNOW_LEVEL_FLURRY);
        Matrix.translateM(this.modelMatrix, SNOW_LEVEL_FLURRY, AutoScrollHelper.RELATIVE_UNSPECIFIED,
                AutoScrollHelper.RELATIVE_UNSPECIFIED, -2.0f);
        Matrix.rotateM(this.modelMatrix, SNOW_LEVEL_FLURRY, this.xRotation, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT,
                AutoScrollHelper.RELATIVE_UNSPECIFIED, 0.0f);
        Matrix.rotateM(this.modelMatrix, 0, this.yRotation, 0.0f, 1.0f, 0.0f);
        Matrix.rotateM(this.modelMatrix, 0, this.zRotation, 0.0f, 0.0f, 1.0f);
    }

    private void drawSnowParticles() {
        float currentTime = ((float) (System.nanoTime() - this.globalStartTime)) / 1.0E9f;
        this.snowParticleShooter.addParticles(this.snowParticleSystem, currentTime, this.COUNT_PER_FRAME);
        updateMvpMatrix();
        GLES20.glEnable(3042);
        GLES20.glBlendFunc(770, 771);
        this.snowShaderProgram.useProgram();
        this.snowShaderProgram.setUniforms(this.modelProjectionMatrix, currentTime, this.pointTexture, this.mAlpha);
        this.snowParticleSystem.drawPoint(this.snowShaderProgram);
        this.snowShaderProgram.useProgram();
        this.snowShaderProgram.setUniforms(this.modelProjectionMatrix, currentTime, this.polyhedronTexture, this
                .mAlpha);
        this.snowParticleSystem.drawPolyhedron(this.snowShaderProgram);
        GLES20.glDisable(3042);
    }

    private void updateMvpMatrix() {
        Matrix.multiplyMM(this.modelProjectionMatrix, SNOW_LEVEL_FLURRY, this.projectionMatrix, 0, this.modelMatrix, 0);
    }

    public void setAlpha(float alpha) {
        this.mAlpha = alpha;
    }
}
