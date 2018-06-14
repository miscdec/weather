package com.opweather.gles20;

import android.content.Context;
import android.opengl.GLES20;
import android.opengl.GLSurfaceView;
import javax.microedition.khronos.egl.EGLConfig;
import android.opengl.Matrix;
import android.support.v4.widget.AutoScrollHelper;

import com.android.volley.DefaultRetryPolicy;
import com.opweather.R;
import com.opweather.gles20.objects.RainDownpourParticles;
import com.opweather.gles20.objects.RainDrizzleParticles;
import com.opweather.gles20.objects.RainNormalParticles;
import com.opweather.gles20.objects.RainParticles;
import com.opweather.gles20.objects.RainStromParticles;
import com.opweather.gles20.programs.RainShaderProgram;
import com.opweather.gles20.util.Geometry.Vector;
import com.opweather.gles20.util.MatrixHelper;
import com.opweather.gles20.util.TextureHelper;

import javax.microedition.khronos.opengles.GL10;

public class RainRenderer implements GLSurfaceView.Renderer {
    private static final int ORANGE_RAIN_PARTICLE_COLOR_DAY = -13824;
    private static final int ORANGE_RAIN_PARTICLE_COLOR_NIGHT = -5996032;
    public static final int RAIN_LEVEL_DOWNPOUR = 3;
    public static final int RAIN_LEVEL_DRIZZLE = 0;
    public static final int RAIN_LEVEL_NORMAL_RAIN = 1;
    public static final int RAIN_LEVEL_RAINSTORM = 4;
    public static final int RAIN_LEVEL_SHOWER = 2;
    public static final int RAIN_LEVEL_THUNDERSHOWER = 5;
    private static final String TAG = "RainRenderer";
    private static final int WHITE_RAIN_PARTICLE_COLOR_DAY = -1;
    private static final int WHITE_RAIN_PARTICLE_COLOR_NIGHT = -8279340;
    private long globalStartTime;
    private final Context mContext;
    private boolean mIsDay;
    private final float[] modelMatrix;
    private final float[] modelProjectionMatrix;
    private final float[] projectionMatrix;
    private float rainAlpha;
    private final float[] rainDropsOrientationMatrix;
    private int rainDropsTextture;
    private int rainLevel;
    private RainParticles rainParticles;
    private RainShaderProgram rainShaderProgram;
    private int texture;
    private float xRotation;
    private float yRotation;
    private float zRotation;

    public RainRenderer(Context context, boolean isDay, int index) {
        this.rainLevel = 1;
        this.modelMatrix = new float[16];
        this.projectionMatrix = new float[16];
        this.modelProjectionMatrix = new float[16];
        this.rainDropsOrientationMatrix = new float[16];
        this.rainAlpha = 1.0f;
        this.mContext = context;
        this.mIsDay = isDay;
        this.rainLevel = index;
    }

    private RainParticles getRainParticles(Vector direction, int color1, int color2, Context context) {
        switch (this.rainLevel) {
            case RAIN_LEVEL_DRIZZLE:
                return new RainDrizzleParticles(direction, color1, color2, context);
            case RAIN_LEVEL_NORMAL_RAIN:
                return new RainNormalParticles(direction, color1, color2, context);
            case RAIN_LEVEL_DOWNPOUR:
                return new RainDownpourParticles(direction, color1, color2, context);
            case RAIN_LEVEL_RAINSTORM:
                return new RainStromParticles(direction, color1, color2, context);
            default:
                return new RainNormalParticles(direction, color1, color2, context);
        }
    }

    @Override
    public void onSurfaceCreated(GL10 gl, EGLConfig config) {
        GLES20.glClearColor(AutoScrollHelper.RELATIVE_UNSPECIFIED, AutoScrollHelper.RELATIVE_UNSPECIFIED,
                AutoScrollHelper.RELATIVE_UNSPECIFIED, AutoScrollHelper.RELATIVE_UNSPECIFIED);
        this.rainShaderProgram = new RainShaderProgram(this.mContext);
        this.globalStartTime = System.nanoTime();
        Vector particleDirection = new Vector(0.0f, -1.0f, 0.0f);
        int color1 = ORANGE_RAIN_PARTICLE_COLOR_DAY;
        int color2 = WHITE_RAIN_PARTICLE_COLOR_DAY;
        if (!this.mIsDay) {
            color1 = ORANGE_RAIN_PARTICLE_COLOR_NIGHT;
            color2 = WHITE_RAIN_PARTICLE_COLOR_NIGHT;
        }
        this.rainParticles = getRainParticles(particleDirection, color1, color2, this.mContext);
        this.texture = TextureHelper.loadTexture(this.mContext, R.mipmap.rain);
    }

    public void onSurfaceChanged(GL10 gl, int width, int height) {
        if (height == 0) {
            height = RAIN_LEVEL_NORMAL_RAIN;
        }
        GLES20.glViewport(RAIN_LEVEL_DRIZZLE, RAIN_LEVEL_DRIZZLE, width, height);
        MatrixHelper.perspectiveM(this.projectionMatrix, 45.0f, ((float) width) / ((float) height),
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT, 200.0f);
        updateModelMatrices();
    }

    public void onDrawFrame(GL10 gl) {
        GLES20.glClear(16640);
        drawRainParticles();
    }

    public void setIsDay(boolean isDay) {
        if (isDay != this.mIsDay) {
            this.mIsDay = isDay;
            int color1 = ORANGE_RAIN_PARTICLE_COLOR_DAY;
            int color2 = WHITE_RAIN_PARTICLE_COLOR_DAY;
            if (!this.mIsDay) {
                color1 = ORANGE_RAIN_PARTICLE_COLOR_NIGHT;
                color2 = WHITE_RAIN_PARTICLE_COLOR_NIGHT;
            }
            this.rainParticles.changeRainColor(color1, color2);
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
        return (syr <= -180.0f || syr >= 90.0f) ? 270.0f - syr : (-syr) - 90.0f;
    }

    private float convertYRotation(float sxr, float syr, float szr) {
        return AutoScrollHelper.RELATIVE_UNSPECIFIED;
    }

    private float convertZRotation(float sxr, float syr, float szr) {
        return szr;
    }

    private void updateModelMatrices() {
        Matrix.setIdentityM(this.modelMatrix, RAIN_LEVEL_DRIZZLE);
        Matrix.translateM(this.modelMatrix, RAIN_LEVEL_DRIZZLE, AutoScrollHelper.RELATIVE_UNSPECIFIED,
                AutoScrollHelper.RELATIVE_UNSPECIFIED, -2.0f);
        Matrix.rotateM(this.modelMatrix, RAIN_LEVEL_DRIZZLE, -this.xRotation, DefaultRetryPolicy
                .DEFAULT_BACKOFF_MULT, AutoScrollHelper.RELATIVE_UNSPECIFIED, 0.0f);
        Matrix.rotateM(this.modelMatrix, 0, -this.yRotation, 0.0f, 1.0f, 0.0f);
        Matrix.rotateM(this.modelMatrix, 0, -this.zRotation, 0.0f, 0.0f, 1.0f);
        Matrix.setIdentityM(this.rainDropsOrientationMatrix, RAIN_LEVEL_DRIZZLE);
        Matrix.rotateM(this.rainDropsOrientationMatrix, 0, this.zRotation, 0.0f, 0.0f, 1.0f);
    }

    private void drawRainParticles() {
        float currentTime = ((float) (System.nanoTime() - this.globalStartTime)) / 1.0E9f;
        updateMvpMatrix();
        GLES20.glEnable(3042);
        GLES20.glBlendFunc(770, 771);
        this.rainParticles.changePosition(currentTime, this.modelProjectionMatrix, this.rainDropsTextture, this
                .rainAlpha, this.xRotation, this.zRotation, this.rainDropsOrientationMatrix);
        this.rainParticles.setAlpha(this.rainAlpha);
        this.rainShaderProgram.useProgram();
        this.rainShaderProgram.setUniforms(this.modelProjectionMatrix, this.texture, this.rainAlpha);
        this.rainParticles.draw(this.rainShaderProgram);
        GLES20.glDisable(3042);
        GLES20.glFinish();
    }

    private void updateMvpMatrix() {
        Matrix.multiplyMM(this.modelProjectionMatrix, RAIN_LEVEL_DRIZZLE, this.projectionMatrix, 0, this.modelMatrix,
                0);
    }

    public void setAlpha(float alpha) {
        this.rainAlpha = alpha;
    }
}
