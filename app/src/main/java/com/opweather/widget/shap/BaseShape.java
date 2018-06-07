package com.opweather.widget.shap;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

import javax.microedition.khronos.opengles.GL10;

public abstract class BaseShape implements IShap {
    private float mAlpha;
    private float mB;
    private boolean mDay;
    private float mDeadDY;
    private float mDeadLX;
    private float mDeadRX;
    private float mDeadUY;
    private float mG;
    private float mNB;
    private float mNG;
    private float mNR;
    private float mR;
    private float mX;
    private float mXSpeed;
    private float mY;
    private float mYSpeed;
    private float mZ;
    private float mZSpeed;

    public BaseShape() {
        mDeadLX = Float.MIN_VALUE;
        mDeadRX = Float.MIN_VALUE;
        mDeadUY = Float.MIN_VALUE;
        mDeadDY = Float.MIN_VALUE;
        mR = 1.0f;
        mG = 1.0f;
        mB = 1.0f;
        mAlpha = 1.0f;
        mNR = 1.0f;
        mNG = 1.0f;
        mNB = 1.0f;
    }

    public void setDeadLine(float lx, float rx, float uy, float dy) {
        mDeadLX = lx;
        mDeadRX = rx;
        mDeadUY = uy;
        mDeadDY = dy;
    }

    public void setDeadLineLeftX(float lx) {
        mDeadLX = lx;
    }

    public void setDeadLineRightX(float rx) {
        mDeadRX = rx;
    }

    public void setDeadLineUpY(float uy) {
        mDeadUY = uy;
    }

    public void setDeadLineDownY(float dy) {
        mDeadDY = dy;
    }

    public float getDeadLineLeftX() {
        return mDeadLX;
    }

    public float getDeadLineRightX() {
        return mDeadRX;
    }

    public float getDeadLineUpY() {
        return mDeadUY;
    }

    public float getDeadLineDownY() {
        return mDeadDY;
    }

    public void setColor(float r, float g, float b, float alpha) {
        mR = r;
        mG = g;
        mB = b;
        mAlpha = alpha;
    }

    public void setNightColor(float r, float g, float b, float alpha) {
        mNR = r;
        mNG = g;
        mNB = b;
        mAlpha = alpha;
    }

    public void setAlpha(float alpha) {
        mAlpha = alpha;
    }

    public float getAlpha() {
        return mAlpha;
    }

    public void drawColor(GL10 gl) {
        if (isDay()) {
            gl.glColor4f(mR, mG, mB, mAlpha);
        } else {
            gl.glColor4f(mNR, mNG, mNB, mAlpha);
        }
    }

    public boolean dead() {
        boolean z = false;
        if (mDeadLX != Float.MIN_VALUE && mX < mDeadLX) {
            z = true;
        }
        if (mDeadRX != Float.MIN_VALUE && mX > mDeadRX) {
            z = true;
        }
        if (mDeadUY != Float.MIN_VALUE && mY > mDeadUY) {
            z = true;
        }
        return (mDeadDY == Float.MIN_VALUE || mY >= mDeadDY) ? z : true;
    }

    public void setXYZ(float x, float y, float z) {
        mX = x;
        mY = y;
        mZ = z;
    }

    public float getX() {
        return mX;
    }

    public float getY() {
        return mY;
    }

    public float getZ() {
        return mZ;
    }

    public void draw(GL10 gl) {
        drawColor(gl);
    }

    public void setSpeed(float x, float y, float z) {
        mXSpeed = x;
        mYSpeed = y;
        mZSpeed = z;
    }

    public float getXSpeed() {
        return mXSpeed;
    }

    public float getYSpeed() {
        return mYSpeed;
    }

    public float getZSpeed() {
        return mZSpeed;
    }

    public void init(float x, float y, float z) {
        setXYZ(x, y, z);
    }

    public void move() {
    }

    public static FloatBuffer floatToBuffer(float[] a) {
        ByteBuffer mbb = ByteBuffer.allocateDirect(a.length * 4);
        mbb.order(ByteOrder.nativeOrder());
        FloatBuffer buffer = mbb.asFloatBuffer();
        buffer.put(a);
        buffer.position(0);
        return buffer;
    }

    public void onCreate() {
    }

    public void setDay(boolean day) {
        mDay = day;
    }

    public boolean isDay() {
        return mDay;
    }
}
