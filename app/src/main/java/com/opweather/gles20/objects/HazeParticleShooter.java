package com.opweather.gles20.objects;

import android.opengl.Matrix;

import com.opweather.gles20.util.Geometry;
import com.opweather.gles20.util.Geometry.Vector;
import com.opweather.widget.openglbase.RainSurfaceView;

import java.util.Random;

public class HazeParticleShooter {
    private static final float ANGLE_VARIANCE_MAX = 90.0f;
    private static final int HAZE_SIZE_MAX = 6;
    private static final int HAZE_SIZE_MIN = 2;
    private static final float HAZE_SPEED_MAX = 0.12f;
    private static final float HAZE_SPEED_MIN = 0.08f;
    private static int initialCount;
    private static boolean isInitial;
    private int color1;
    private int color2;
    private float[] directionVector;
    private final Random random;
    private float[] resultVector;
    private float[] rotationMatrix;

    static {
        isInitial = true;
        initialCount = 0;
    }

    public HazeParticleShooter(Vector direction, int color1, int color2) {
        this.random = new Random();
        this.rotationMatrix = new float[16];
        this.directionVector = new float[4];
        this.resultVector = new float[4];
        isInitial = true;
        initialCount = 0;
        this.color1 = color1;
        this.color2 = color2;
        Vector localVector = direction.normalize();
        this.directionVector[0] = localVector.x;
        this.directionVector[1] = localVector.y;
        this.directionVector[2] = localVector.z;
    }

    public void changeHazeColor(int color1, int color2) {
        this.color1 = color1;
        this.color2 = color2;
    }

    public void addParticles(HazeParticleSystem particleSystem, float currentTime, int count) {
        int i;
        if (isInitial) {
            initialCount++;
            if (initialCount > 256) {
                isInitial = false;
            }
            for (i = 0; i < 128; i++) {
                float y = (this.random.nextFloat() * 3.0f) - 1.5f;
                commonAdd(particleSystem, currentTime, y, (this.random.nextFloat() * 4.0f) - 2.0f, (this.random.nextFloat() * 4.0f) - 2.0f);
            }
            return;
        }
        for (i = 0; i < count; i++) {
            commonAdd(particleSystem, currentTime, 1.5f, (this.random.nextFloat() * 4.0f) - 2.0f, (this.random.nextFloat() * 4.0f) - 2.0f);
        }
    }

    private void commonAdd(HazeParticleSystem particleSystem, float currentTime, float y, float x, float z) {
        Geometry.Point point = new Geometry.Point(x, y, z);
        boolean colorPick = this.random.nextBoolean();
        int size = this.random.nextInt(RainSurfaceView.RAIN_LEVEL_RAINSTORM) + 2;
        float speedAdjustment = (this.random.nextFloat() * 0.04f) + 0.08f;
        Matrix.setRotateEulerM(this.rotationMatrix, 0, (this.random.nextFloat() - 0.5f) * 90.0f, (this.random.nextFloat() - 0.5f) * 90.0f, (this.random.nextFloat() - 0.5f) * 90.0f);
        Matrix.multiplyMV(this.resultVector, 0, this.rotationMatrix, 0, this.directionVector, 0);
        particleSystem.addParticle(point, size, colorPick ? this.color1 : this.color2, new Vector(this.resultVector[0] * speedAdjustment, this.resultVector[1] * speedAdjustment, this.resultVector[2] * speedAdjustment), currentTime);
    }
}
