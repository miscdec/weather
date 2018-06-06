package com.opweather.widget.anim;

import android.os.SystemClock;
import android.support.v4.widget.AutoScrollHelper;

import java.util.Random;

public abstract class BaseAnimation {
    protected static int INDEX;
    private static float mCenterX;
    private static float mCenterY;
    private static float mCenterZ;
    private static float mRangeX;
    private static float mRangeY;
    private static float mRangeZ;

    public abstract float[] next();

    static {
        INDEX = 0;
        mRangeX = 1.0f;
        mRangeY = 2.0f;
        mCenterX = 1.0f;
        mCenterY = 2.0f;
        mCenterZ = 1.0f;
    }

    public static float getRangeX() {
        return mRangeX;
    }

    public static float getRangeY() {
        return mRangeY;
    }

    public static float getRangeZ() {
        return mRangeZ;
    }

    public static void setCenterXYZ(float x, float y, float z) {
        mCenterX = x;
        mCenterY = y;
        mCenterZ = z;
    }

    public static void setRange(float xRange, float yRange, float zRange) {
        mRangeX = xRange;
        mRangeY = yRange;
        mRangeZ = zRange;
    }

    protected static Random getRandom() {
        nextSeed();
        return new Random(SystemClock.currentThreadTimeMillis() + ((long) INDEX));
    }

    public static float[] nextXYZ() {
        Random random = getRandom();
        float z = randomRange(random, AutoScrollHelper.RELATIVE_UNSPECIFIED, mRangeZ);
        float x = 2.5f * randomRange(random, mCenterX, ((mCenterZ + (mRangeZ / 2.0f)) - z) * mRangeX);
        float y = (randomRange(random, mCenterY, mRangeY) * 2.0f) + (mRangeZ - z);
        nextSeed();
        return new float[]{x / 5.0f, y / 5.0f, z};
    }

    private static float randomRange(Random random, float center, float range) {
        return (((((float) random.nextInt(10000)) * range) / 10000.0f) - (range / 2.0f)) + center;
    }

    public static float[] orginXYZ() {
        Random random = getRandom();
        float rangeX = ((mCenterZ + (mRangeZ / 2.0f)) - randomRange(random, AutoScrollHelper.RELATIVE_UNSPECIFIED,
                mRangeZ)) * mRangeX;
        float x = 2.5f * ((((((float) random.nextInt(10000)) * rangeX) / 10000.0f) - (rangeX / 2.0f)) + mCenterX);
        float y = randomRange(random, mCenterY, mRangeY) * 4.0f;
        float z = (float) random.nextInt(120);
        nextSeed();
        return new float[]{x / 5.0f, y, z};
    }

    protected static void nextSeed() {
        INDEX += new Random().nextInt(10);
    }
}
