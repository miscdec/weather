package com.opweather.widget;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.LinearGradient;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.graphics.Shader.TileMode;
import android.support.v4.widget.AutoScrollHelper;
import android.util.AttributeSet;

import com.opweather.util.OrientationSensorUtil;
import com.opweather.widget.openglbase.RainDownpour;

import java.util.ArrayList;

public class FogView extends BaseWeatherView {
    private static final int BACKGROUND_COLOR;
    private static final int BACKGROUND_NIGHT_COLOR;
    private boolean mAnimate;
    private float mDeltaAngleY;
    private float mDeltaAngleZ;
    private ArrayList<FogShape> mFogShapeList;

    private class FogShape {
        private final int DARK_COLOR;
        private final int DARK_NIGIT_COLOR;
        private final int LIGHT_COLOR;
        private final int LIGHT_NIGIT_COLOR;
        private float MAX_SHAKE;
        private final float MAX_SPEED;
        private final int MAX_TIME;
        private final int[] SHADER_COLOR;
        private final int[] SHADER_NIGHT_COLOR;
        private float mAcceleration;
        private boolean mAnim;
        private boolean mDay;
        private float mDisToScreenCenter;
        private int mHeight;
        private boolean mInit;
        private float mMoveSpeedX;
        private float mMoveSpeedY;
        private Paint mNightPaint;
        private long mOldTime;
        private Paint mPaint;
        private float mRadius;
        private float mRealDegrees;
        private float mSensorZDegrees;
        private LinearGradient mShader;
        private int mWidth;
        private float mX;
        private float mX_Shake;
        private float mX_ShakeDirection;
        private float mY;
        private float mY_RealOffset;
        private float mY_SensorOffset;
        private float mY_Shake;
        private float mY_ShakeDirection;

        public FogShape(int x, int y, float radius) {
            this.MAX_SPEED = 1.0f;
            this.MAX_TIME = 30;
            this.LIGHT_COLOR = Color.parseColor("#7798a9");
            this.DARK_COLOR = Color.parseColor("#617d8f");
            this.LIGHT_NIGIT_COLOR = Color.parseColor("#32424a");
            this.DARK_NIGIT_COLOR = Color.parseColor("#2a383f");
            this.mX = 0.0f;
            this.mY = 0.0f;
            this.mX_Shake = 0.0f;
            this.mX_ShakeDirection = 0.04f;
            this.mY_Shake = 0.0f;
            this.mY_ShakeDirection = 0.04f;
            this.mY_SensorOffset = 0.0f;
            this.mY_RealOffset = 0.0f;
            this.mRadius = 0.0f;
            this.mAnim = true;
            this.mSensorZDegrees = 0.0f;
            this.mRealDegrees = 0.0f;
            this.mDisToScreenCenter = 0.0f;
            this.SHADER_COLOR = new int[]{this.DARK_COLOR, this.DARK_COLOR, this.LIGHT_COLOR};
            this.SHADER_NIGHT_COLOR = new int[]{this.DARK_NIGIT_COLOR, this.DARK_NIGIT_COLOR, this.LIGHT_NIGIT_COLOR};
            this.mOldTime = 0;
            this.mAcceleration = 0.001f;
            this.mInit = false;
            this.mDay = true;
            this.mPaint = new Paint();
            this.mNightPaint = new Paint();
            this.mX = (float) x;
            this.mY = (float) y;
            this.mRadius = radius;
            this.MAX_SHAKE = 0.1f * radius;
            float random = (float) Math.random();
            this.mX_ShakeDirection *= random;
            this.mY_ShakeDirection = (1.0f - random) * this.mY_ShakeDirection;
            this.mPaint.setAntiAlias(true);
            this.mPaint.setStyle(Style.FILL);
            this.mNightPaint.setAntiAlias(true);
        }

        public void setDay(boolean day) {
            this.mDay = day;
        }

        public boolean isDay() {
            return this.mDay;
        }

        public void setWidth(int width) {
            this.mWidth = width / 2;
        }

        public void setHeight(int height) {
            this.mHeight = height / 2;
        }

        public FogShape setAnimation(boolean anim) {
            this.mAnim = anim;
            return this;
        }

        private int getSpentTime() {
            long curTime = System.currentTimeMillis();
            int time = this.mOldTime == 0 ? 0 : (int) (curTime - this.mOldTime);
            this.mOldTime = curTime;
            return time > 30 ? RainDownpour.Z_RANDOM_RANGE : time;
        }

        private void calcShakeOffset(int time) {
            if (this.mX_Shake >= this.MAX_SHAKE) {
                this.mX_ShakeDirection = -Math.abs(this.mX_ShakeDirection);
            } else if (this.mX_Shake <= (-this.MAX_SHAKE)) {
                this.mX_ShakeDirection = Math.abs(this.mX_ShakeDirection);
            }
            this.mX_Shake += this.mX_ShakeDirection * ((float) time);
            if (this.mY_Shake >= this.MAX_SHAKE) {
                this.mY_ShakeDirection = -Math.abs(this.mY_ShakeDirection);
            } else if (this.mY_Shake <= (-this.MAX_SHAKE)) {
                this.mY_ShakeDirection = Math.abs(this.mY_ShakeDirection);
            }
            this.mY_Shake += this.mY_ShakeDirection * ((float) time);
        }

        private void calcSensorOffset(int time) {
            float disY = Math.abs(this.mY_RealOffset - this.mY_SensorOffset);
            if (this.mMoveSpeedY < 1.0f) {
                this.mMoveSpeedY += this.mAcceleration * ((float) time);
            } else {
                this.mMoveSpeedY -= this.mAcceleration * ((float) time);
            }
            if (this.mMoveSpeedY < 0.0f) {
                this.mMoveSpeedY = this.mAcceleration;
            }
            if (disY < 400.0f) {
                this.mMoveSpeedY = Math.min(this.mMoveSpeedY, (1.0f * disY) / 400.0f);
            }
            float moveOffsetY = this.mMoveSpeedY * ((float) time);
            if (disY <= moveOffsetY) {
                return;
            }
            if (this.mY_RealOffset > this.mY_SensorOffset) {
                this.mY_RealOffset -= moveOffsetY;
            } else {
                this.mY_RealOffset += moveOffsetY;
            }
        }

        private void calcRotateDegrees(int time) {
            float disRotate = Math.abs(this.mRealDegrees - this.mSensorZDegrees);
            if (this.mMoveSpeedX < 0.1f) {
                this.mMoveSpeedX += ((float) time) * 1.0E-4f;
            } else {
                this.mMoveSpeedX -= ((float) time) * 1.0E-4f;
            }
            if (this.mMoveSpeedX < 0.0f) {
                this.mMoveSpeedX = 1.0E-4f;
            }
            if (disRotate < 30.0f) {
                this.mMoveSpeedX = Math.min(this.mMoveSpeedX, (0.1f * disRotate) / 30.0f);
            }
            float moveOffsetX = this.mMoveSpeedX * ((float) time);
            if (disRotate <= moveOffsetX) {
                return;
            }
            if (this.mRealDegrees > this.mSensorZDegrees) {
                this.mRealDegrees -= moveOffsetX;
            } else {
                this.mRealDegrees += moveOffsetX;
            }
        }

        private void calcDistanceToCenter() {
            this.mDisToScreenCenter = (float) Math.sqrt(Math.pow((double) ((this.mY + this.mY_RealOffset) - ((float) this.mHeight)), 2.0d) + Math.pow((double) (this.mX - ((float) this.mWidth)), 2.0d));
        }

        private double getDegreesToXaxis() {
            double beta = Math.toDegrees(Math.atan((double) (((this.mY + this.mY_RealOffset) - ((float) this.mHeight)) / (this.mX - ((float) this.mWidth))))) + ((double) this.mRealDegrees);
            return this.mX - ((float) this.mWidth) < 0.0f ? beta - 180.0d : beta;
        }

        public boolean draw(Canvas canvas) {
            if (!this.mInit) {
                this.mY_RealOffset = this.mY_SensorOffset;
                this.mInit = true;
            }
            if (this.mAnim) {
                int t = getSpentTime();
                calcSensorOffset(t);
                calcShakeOffset(t);
                calcDistanceToCenter();
                calcRotateDegrees(t);
            }
            float cx = (((float) (((double) this.mDisToScreenCenter) * Math.cos(Math.toRadians(getDegreesToXaxis())))) + ((float) this.mWidth)) + this.mX_Shake;
            float cy = ((((float) (((double) this.mDisToScreenCenter) * Math.sin(Math.toRadians(getDegreesToXaxis())))) + ((float) this.mHeight)) + this.mY_RealOffset) + this.mY_Shake;
            if (cx > ((float) (this.mWidth * 2)) + this.mRadius || cx < (-this.mRadius) || cy > ((float) (this.mHeight * 2)) + this.mRadius || cy < (-this.mRadius)) {
                return false;
            }
            float[] p0 = getPoint(cx, cy, this.mRadius, this.mRealDegrees - 90.0f);
            float[] p1 = getPoint(cx, cy, this.mRadius, this.mRealDegrees + 90.0f);
            int[] tempColor = this.SHADER_COLOR;
            Paint p = this.mPaint;
            if (!isDay()) {
                p = this.mNightPaint;
                tempColor = this.SHADER_NIGHT_COLOR;
            }
            this.mShader = new LinearGradient(p0[0], p0[1], p1[0], p1[1], tempColor, null, TileMode.CLAMP);
            p.setShader(this.mShader);
            canvas.drawCircle(cx, cy, this.mRadius, p);
            return true;
        }

        private float[] getPoint(float centerX, float centerY, float radius, float degrees) {
            return new float[]{(float) ((Math.cos(Math.toRadians((double) degrees)) * ((double) radius)) + ((double) centerX)), (float) ((Math.sin(Math.toRadians((double) degrees)) * ((double) radius)) + ((double) centerY))};
        }

        public void updateRoatationInfo(float x, float y, float z) {
            this.mY_SensorOffset = (y - 90.0f) * 5.0f;
            this.mSensorZDegrees = z;
        }
    }

    static {
        BACKGROUND_COLOR = Color.parseColor("#617d8f");
        BACKGROUND_NIGHT_COLOR = Color.parseColor("#2a383f");
    }

    public FogView(Context context, boolean isDay) {
        super(context, isDay);
        this.mFogShapeList = new ArrayList();
        this.mAnimate = false;
        this.mDeltaAngleZ = 0.0f;
        this.mDeltaAngleY = 0.0f;
        init();
        setDayBackgroundColor(BACKGROUND_COLOR);
        setNightBackgroundColor(BACKGROUND_NIGHT_COLOR);
    }

    public FogView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public FogView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.mFogShapeList = new ArrayList();
        this.mAnimate = false;
        this.mDeltaAngleZ = 0.0f;
        this.mDeltaAngleY = 0.0f;
        init();
    }

    public void startAnimate() {
        this.mAnimate = true;
        invalidate();
    }

    public void stopAnimate() {
        this.mAnimate = false;
    }

    public void onPageSelected(boolean isCurrent) {
    }

    private void init() {
        this.mFogShapeList.add(new FogShape(872, 2436, 395.0f).setAnimation(true));
        this.mFogShapeList.add(new FogShape(196, 2338, 412.0f).setAnimation(true));
        this.mFogShapeList.add(new FogShape(80, 1876, 542.0f).setAnimation(true));
        this.mFogShapeList.add(new FogShape(836, 1864, 426.0f).setAnimation(true));
        this.mFogShapeList.add(new FogShape(1528, 1784, 425.0f).setAnimation(true));
        this.mFogShapeList.add(new FogShape(940, 1564, 419.0f).setAnimation(true));
        this.mFogShapeList.add(new FogShape(288, 1356, 408.0f).setAnimation(true));
        this.mFogShapeList.add(new FogShape(1467, 1211, 412.0f).setAnimation(true));
        this.mFogShapeList.add(new FogShape(-260, 1184, 509.0f).setAnimation(true));
        this.mFogShapeList.add(new FogShape(1840, 1124, 410.0f).setAnimation(true));
        this.mFogShapeList.add(new FogShape(668, 882, 531.0f).setAnimation(true));
        this.mFogShapeList.add(new FogShape(-360, 699, 515.0f).setAnimation(true));
        this.mFogShapeList.add(new FogShape(229, 609, 460.0f).setAnimation(true));
        this.mFogShapeList.add(new FogShape(1028, 532, 250.0f).setAnimation(true));
        this.mFogShapeList.add(new FogShape(1459, 532, 317.0f).setAnimation(true));
        this.mFogShapeList.add(new FogShape(654, 263, 296.0f).setAnimation(true));
        this.mFogShapeList.add(new FogShape(-72, 244, 484.0f).setAnimation(true));
        this.mFogShapeList.add(new FogShape(1480, 12, 509.0f).setAnimation(true));
        this.mFogShapeList.add(new FogShape(1028, -201, 391.0f).setAnimation(true));
        this.mFogShapeList.add(new FogShape(220, -350, 492.0f).setAnimation(true));
        this.mFogShapeList.add(new FogShape(604, -618, 561.0f).setAnimation(true));
        this.mFogShapeList.add(new FogShape(1316, -604, 233.0f).setAnimation(true));
        this.mFogShapeList.add(new FogShape(204, -731, 344.0f).setAnimation(true));
        this.mFogShapeList.add(new FogShape(924, -828, 350.0f).setAnimation(true));
    }

    public void draw(Canvas canvas) {
        super.draw(canvas);
        if (this.mAnimate) {
            int size = this.mFogShapeList.size();
            for (int i = 0; i < size; i++) {
                FogShape fogShape = (FogShape) this.mFogShapeList.get(i);
                fogShape.setDay(isDay());
                fogShape.setWidth(canvas.getWidth());
                fogShape.setHeight(canvas.getHeight());
                fogShape.updateRoatationInfo(AutoScrollHelper.RELATIVE_UNSPECIFIED, this.mDeltaAngleY, this.mDeltaAngleZ);
                fogShape.draw(canvas);
            }
            invalidate();
        }
    }

    protected void onCreateOrientationInfoListener() {
        this.mListener = new OrientationSensorUtil.OrientationInfoListener() {
            public void onOrientationInfoChange(float x, float y, float z) {
                FogView.this.mDeltaAngleZ = z;
                if (y < 0.0f) {
                    FogView.this.mDeltaAngleY = -y;
                } else {
                    FogView.this.mDeltaAngleY = y;
                }
            }
        };
    }
}
