package com.opweather.gles20;

import android.content.Context;
import android.opengl.GLES20;
import android.opengl.GLSurfaceView.Renderer;
import android.opengl.Matrix;
import android.support.v4.view.accessibility.AccessibilityNodeInfoCompat;
import android.support.v4.widget.AutoScrollHelper;

import com.android.volley.DefaultRetryPolicy;
import com.opweather.R;
import com.opweather.gles20.objects.HazeParticleShooter;
import com.opweather.gles20.objects.HazeParticleSystem;
import com.opweather.gles20.programs.HazeShaderProgram;
import com.opweather.gles20.util.Geometry.Vector;
import com.opweather.gles20.util.MatrixHelper;
import com.opweather.gles20.util.TextureHelper;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

public class HazeRenderer implements Renderer {
    private static final int COUNT_PER_FRAME = 6;
    private static final int HAZE_PARTICLE_COLOR_DAY_1 = -279473;
    private static final int HAZE_PARTICLE_COLOR_DAY_2 = -2835905;
    private static final int HAZE_PARTICLE_COLOR_NIGHT_1 = -7640011;
    private static final int HAZE_PARTICLE_COLOR_NIGHT_2 = -9675734;
    private static final int HAZE_PARTICLE_COUNT = 10000;
    private static final String TAG = "HazeRenderer";
    private long globalStartTime;
    private HazeParticleShooter hazeParticleShooter;
    private HazeParticleSystem hazeParticleSystem;
    private HazeShaderProgram hazeShaderProgram;
    private float mAlpha;
    private final Context mContext;
    private boolean mIsDay;
    private final float[] modelMatrix;
    private final float[] modelProjectionMatrix;
    private final float[] projectionMatrix;
    private int texture;
    private float xRotation;
    private float yRotation;
    private float zRotation;

    public HazeRenderer(Context context, boolean isDay) {
        this.modelMatrix = new float[16];
        this.projectionMatrix = new float[16];
        this.modelProjectionMatrix = new float[16];
        this.mAlpha = 1.0f;
        this.mContext = context;
        this.mIsDay = isDay;
    }

    public void onSurfaceCreated(GL10 gl, EGLConfig config) {
        GLES20.glClearColor(AutoScrollHelper.RELATIVE_UNSPECIFIED, AutoScrollHelper.RELATIVE_UNSPECIFIED,
                AutoScrollHelper.RELATIVE_UNSPECIFIED, AutoScrollHelper.RELATIVE_UNSPECIFIED);
        this.hazeShaderProgram = new HazeShaderProgram(this.mContext);
        this.hazeParticleSystem = new HazeParticleSystem(10000);
        this.globalStartTime = System.nanoTime();
        Vector particleDirection = new Vector(0.0f, -1.0f, 0.0f);
        int color1 = HAZE_PARTICLE_COLOR_DAY_1;
        int color2 = HAZE_PARTICLE_COLOR_DAY_2;
        if (!this.mIsDay) {
            color1 = HAZE_PARTICLE_COLOR_NIGHT_1;
            color2 = HAZE_PARTICLE_COLOR_NIGHT_2;
        }
        this.hazeParticleShooter = new HazeParticleShooter(particleDirection, color1, color2);
        this.texture = TextureHelper.loadTexture(this.mContext, R.mipmap.haze);
    }

    public void onSurfaceChanged(GL10 gl, int width, int height) {
        GLES20.glViewport(0, 0, width, height);
        MatrixHelper.perspectiveM(this.projectionMatrix, 45.0f, ((float) width) / ((float) height),
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT, 3.0f);
        updateModelMatrices();
    }

    public void onDrawFrame(GL10 gl) {
        GLES20.glClear(AccessibilityNodeInfoCompat.ACTION_COPY);
        drawHazeParticles();
    }

    public void setIsDay(boolean isDay) {
        if (isDay != this.mIsDay) {
            this.mIsDay = isDay;
            int color1 = HAZE_PARTICLE_COLOR_DAY_1;
            int color2 = HAZE_PARTICLE_COLOR_DAY_2;
            if (!this.mIsDay) {
                color1 = HAZE_PARTICLE_COLOR_NIGHT_1;
                color2 = HAZE_PARTICLE_COLOR_NIGHT_2;
            }
            this.hazeParticleShooter.changeHazeColor(color1, color2);
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
        return syr;
    }

    private float convertYRotation(float sxr, float syr, float szr) {
        return AutoScrollHelper.RELATIVE_UNSPECIFIED;
    }

    private float convertZRotation(float sxr, float syr, float szr) {
        if (syr > 0.0f) {
            return (szr <= -90.0f || szr >= 0.0f) ? 180.0f - szr : -180.0f - szr;
        } else {
            return szr;
        }
    }

    private void updateModelMatrices() {
        Matrix.setIdentityM(this.modelMatrix, 0);
        Matrix.translateM(this.modelMatrix, 0, AutoScrollHelper.RELATIVE_UNSPECIFIED, AutoScrollHelper
                .RELATIVE_UNSPECIFIED, -2.0f);
        Matrix.rotateM(this.modelMatrix, 0, -this.xRotation, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT,
                AutoScrollHelper.RELATIVE_UNSPECIFIED, 0.0f);
        Matrix.rotateM(this.modelMatrix, 0, -this.yRotation, 0.0f, 1.0f, 0.0f);
        Matrix.rotateM(this.modelMatrix, 0, -this.zRotation, 0.0f, 0.0f, 1.0f);
    }

    private void drawHazeParticles() {
        float currentTime = ((float) (System.nanoTime() - this.globalStartTime)) / 1.0E9f;
        this.hazeParticleShooter.addParticles(this.hazeParticleSystem, currentTime, COUNT_PER_FRAME);
        updateMvpMatrix();
        GLES20.glEnable(3042);
        GLES20.glBlendFunc(770, 771);
        this.hazeShaderProgram.useProgram();
        this.hazeShaderProgram.setUniforms(this.modelProjectionMatrix, currentTime, this.texture, this.mAlpha);
        this.hazeParticleSystem.bindData(this.hazeShaderProgram);
        this.hazeParticleSystem.draw();
        GLES20.glDisable(3042);
    }

    private void updateMvpMatrix() {
        Matrix.multiplyMM(this.modelProjectionMatrix, 0, this.projectionMatrix, 0, this.modelMatrix, 0);
    }

    public void setAlpha(float alpha) {
        this.mAlpha = alpha;
    }
}
