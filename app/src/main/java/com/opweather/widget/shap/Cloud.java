package com.opweather.widget.shap;

import android.graphics.Canvas;
import android.graphics.Paint;

public class Cloud {
    private static final float MAX_SPEED = 1.0f;
    private static final int MAX_TIME = 30;
    private final float OFFSET_RATIO;
    private float mAcceleration;
    private boolean mAnim;
    private boolean mDay;
    private float mDensity;
    private int mHeight;
    private boolean mInit;
    private int mLevel;
    private float mMaxHRadio;
    private float mMaxRadiusScale;
    private float mMaxY;
    private float mMoveSpeedX;
    private float mMoveSpeedY;
    private Paint mNightPaint;
    private long mOldTime;
    private Paint mPaint;
    private float mRXRotate;
    private float mRYRotate;
    private float mRadius;
    private float mRadiusNoise;
    private float mRadiusNoiseRate;
    private float mRadiusScale;
    private int mRadiusStep;
    private float mScaleZ;
    private int mWidth;
    private float mX;
    private float mXRotate;
    private float mY;
    private float mYRotate;
    private int mZ;

    public Cloud(int x, int y, int z, int radius, int color) {
        mMaxRadiusScale = 1.1f;
        OFFSET_RATIO = 0.5f;
        mX = 0.0f;
        mY = 0.0f;
        mZ = 0;
        mRadiusScale = 1.0f;
        mRadiusStep = 120;
        mRadiusNoise = 0.003f;
        mRadiusNoiseRate = 1.0f;
        mXRotate = 0.0f;
        mYRotate = 0.0f;
        mRadius = 0.0f;
        mAnim = true;
        mMaxHRadio = 0.4f;
        mOldTime = 0;
        mAcceleration = 0.001f;
        mInit = false;
        mDay = true;
        mDensity = 1.0f;
        mPaint = new Paint();
        mNightPaint = new Paint();
        mMaxY = 0.0f;
        mX = (float) x;
        mY = (float) y;
        mRadius = (float) radius;
        mZ = z;
        mPaint.setAntiAlias(true);
        mPaint.setColor(color);
        mNightPaint.setAntiAlias(true);
        mScaleZ = z == 0 ? 1.0f : ((float) (2500 - z)) / 2500.0f;
        mRadiusNoise = 1.0f;
        mRadiusScale = 1.0f;
    }

    public Cloud setMaxY(float maxY) {
        mMaxY = maxY;
        return this;
    }

    public Cloud setNightColor(int color) {
        mNightPaint.setColor(color);
        return this;
    }

    public Cloud setDensity(float density) {
        mDensity = density;
        return this;
    }

    public Cloud setNightAlpha(int alpha) {
        mNightPaint.setAlpha(alpha);
        return this;
    }

    public void setDay(boolean day) {
        mDay = day;
    }

    public boolean isDay() {
        return mDay;
    }

    public Cloud setHeightRadio(float rate) {
        mMaxHRadio = rate;
        return this;
    }

    public Cloud setRadiusScale(float scale) {
        mMaxRadiusScale = scale;
        return this;
    }

    public Cloud setLevel(int l) {
        mLevel = l;
        return this;
    }

    public Cloud setStep(int step) {
        mRadiusStep = step;
        return this;
    }

    public Cloud setAlpha(int a) {
        mPaint.setAlpha(a);
        return this;
    }

    public Cloud setHeight(int height) {
        mHeight = height;
        return this;
    }

    public void setWidth(int width) {
        mWidth = width;
    }

    public Cloud setAnimation(boolean anim) {
        mAnim = anim;
        return this;
    }

    public boolean draw(Canvas canvas) {
        if (!mInit) {
            mRXRotate = mXRotate;
            mRYRotate = mYRotate;
            mInit = true;
        }
        if (mAnim) {
            int t;
            long curTime = System.currentTimeMillis();
            if (mOldTime == 0) {
                t = 0;
            } else {
                t = (int) (curTime - mOldTime);
            }
            mOldTime = curTime;
            if (t > 30) {
                t = MAX_TIME;
            }
            float disX = Math.abs(mRXRotate - mXRotate);
            float disY = Math.abs(mRYRotate - mYRotate);
            if (mMoveSpeedX < 1.0f) {
                mMoveSpeedX += mAcceleration * ((float) t);
            } else {
                mMoveSpeedX -= mAcceleration * ((float) t);
            }
            if (mMoveSpeedX < 0.0f) {
                mMoveSpeedX = mAcceleration;
            }
            if (disX < 150.0f * mDensity) {
                mMoveSpeedX = Math.min(mMoveSpeedX, (1.0f * disX) / (150.0f * mDensity));
            }
            float moveOffsetX = (mMoveSpeedX * ((float) t)) * mDensity;
            if (disX > moveOffsetX) {
                if (mRXRotate > mXRotate) {
                    mRXRotate -= moveOffsetX;
                } else {
                    mRXRotate += moveOffsetX;
                }
            }
            if (mMoveSpeedY < 1.0f) {
                mMoveSpeedY += mAcceleration * ((float) t);
            } else {
                mMoveSpeedY -= mAcceleration * ((float) t);
            }
            if (mMoveSpeedY < 0.0f) {
                mMoveSpeedY = mAcceleration;
            }
            if (disY < 150.0f * mDensity) {
                mMoveSpeedY = Math.min(mMoveSpeedY, (1.0f * disY) / (150.0f * mDensity));
            }
            float moveOffsetY = (mMoveSpeedY * ((float) t)) * mDensity;
            if (disY > moveOffsetY) {
                if (mRYRotate > mYRotate) {
                    mRYRotate -= moveOffsetY;
                } else {
                    mRYRotate += moveOffsetY;
                }
            }
            if (mRadiusScale >= mMaxRadiusScale) {
                mRadiusNoise = (-(mMaxRadiusScale - 1.0f)) / ((float) mRadiusStep);
                mRadiusNoiseRate = 0.01f;
            } else if (mRadiusScale <= 1.0f) {
                mRadiusNoise = (mMaxRadiusScale - 1.0f) / ((float) mRadiusStep);
                mRadiusNoiseRate = 0.01f;
            }
            if (mRadiusNoiseRate >= 1.0f) {
                mRadiusNoiseRate = 1.0f;
            } else {
                mRadiusNoiseRate += 0.05f;
            }
            mRadiusScale += mRadiusNoise * mRadiusNoiseRate;
        }
        Paint p = mPaint;
        if (!isDay()) {
            p = mNightPaint;
        }
        canvas.drawCircle(mX + mRXRotate, mY + mRYRotate, getRadius(), p);
        return true;
    }

    public float getRadius() {
        return mRadius * mRadiusScale;
    }

    public void updateRoatationInfo(float x, float y, float z) {
        mYRotate = (800.0f * (y / 90.0f)) + ((float) (((double) ((z / 90.0f) * (((float) (mWidth / 2)) - mX))) * 0.5d));
        mYRotate *= mDensity * 0.5f;
        mXRotate = ((float) ((((double) (500.0f * (z / 90.0f))) * 0.5d) * ((double) mDensity))) * 0.5f;
        mYRotate = (float) (((double) mYRotate) * (((double) mLevel) * 0.1d));
        mYRotate += ((float) ((10 - mLevel) * 4)) * mDensity;
        mXRotate += ((float) ((10 - mLevel) * 4)) * mDensity;
        float radius = getRadius();
        if (mMaxY > 0.0f && (mY + mYRotate) + radius > mMaxY) {
            mYRotate = (mMaxY - mY) - radius;
        }
    }
}
