package com.opweather.widget;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.graphics.Path;
import android.graphics.Point;
import android.graphics.RectF;
import android.support.v4.widget.AutoScrollHelper;
import android.util.AttributeSet;

import com.android.volley.DefaultRetryPolicy;
import com.opweather.util.OrientationSensorUtil;
import com.opweather.util.UIUtil;
import com.opweather.widget.openglbase.RainSurfaceView;

import java.util.ArrayList;

public class SunnyView extends BaseWeatherView {
    private static final int BACKGROUND_COLOR;
    private static final int BACKGROUND_NIGHT_COLOR;
    private static final int MAX_TIME = 30;
    protected final float MAX_SPEED;
    protected float acceleration;
    protected boolean isInit;
    private boolean mAnimate;
    private float mDeltaAngleY;
    private float mDeltaAngleZ;
    private float mDesity;
    private ArrayList<Star> mStarList;
    private ArrayList<Sun> mSunList;
    protected float moveSpeedX;
    protected float moveSpeedY;
    protected float offsetX;
    protected float offsetX1;
    protected float offsetY;
    protected float offsetY1;
    protected long oldTime;
    private float rangeX;
    private float rangeY;
    private float scale;
    protected float scaleAcceleration;
    private float scaleSpeed;
    private float sy;

    private class Star {
        protected float mCenterX;
        protected float mCenterY;
        private long mOldTime;
        protected Paint mPaint;
        protected float mPieces;
        protected float mRadius;
        protected float mRotateAngle;
        protected float mRotateSpeed;
        protected float mStartAngle;
        protected float mSweepAngle;
        protected float offset;
        protected float offsetX1;
        protected float offsetY1;

        public Star(float radius, float strokeWidth, float centerX, float centerY, float rotateSpeed, float
                startAngle, float sweepAngle, int pieces, int color) {
            this.offsetX1 = 0.0f;
            this.offsetY1 = 0.0f;
            this.mOldTime = 0;
            this.mRadius = radius;
            this.mCenterX = centerX;
            this.mCenterY = centerY;
            this.mRotateSpeed = rotateSpeed;
            this.mStartAngle = startAngle;
            this.mSweepAngle = sweepAngle;
            this.mPieces = (float) pieces;
            this.mRotateAngle = 0.0f;
            this.mPaint = new Paint();
            this.mPaint.setAntiAlias(true);
            this.mPaint.setStyle(Style.STROKE);
            this.mPaint.setColor(color);
            this.mPaint.setStrokeWidth(strokeWidth);
        }

        public boolean draw(Canvas canvas) {
            RectF oval = new RectF((this.mCenterX + this.offsetX1) - this.mRadius, ((this.mCenterY + this.offsetY1) +
                    1250.0f) - this.mRadius, (this.mCenterX + this.offsetX1) + this.mRadius, ((this.mCenterY + this
                    .offsetY1) + 1250.0f) + this.mRadius);
            this.mRotateAngle += this.mRotateSpeed * ((float) getSpentTime());
            if (this.mRotateAngle > 360.0f) {
                this.mRotateAngle -= 360.0f;
            }
            for (int i = BACKGROUND_NIGHT_COLOR; ((float) i) < this.mPieces; i++) {
                float startAngle;
                if (i < 2) {
                    startAngle = (this.mStartAngle + ((float) (i * 180))) + this.mRotateAngle;
                    if (startAngle > 360.0f) {
                        startAngle -= 360.0f;
                    }
                    if (startAngle <= 90.0f || this.mSweepAngle + startAngle >= 270.0f) {
                        canvas.drawArc(oval, startAngle, this.mSweepAngle, false, this.mPaint);
                    }
                } else {
                    startAngle = (((180.0f - this.mSweepAngle) - this.mStartAngle) + (((float) (i - 2)) * 180.0f)) +
                            this.mRotateAngle;
                    if (startAngle > 360.0f) {
                        startAngle -= 360.0f;
                    }
                    if (startAngle <= 90.0f || this.mSweepAngle + startAngle >= 270.0f) {
                        canvas.drawArc(oval, startAngle, this.mSweepAngle, false, this.mPaint);
                    }
                }
            }
            return true;
        }

        private int getSpentTime() {
            long curTime = System.currentTimeMillis();
            int time = this.mOldTime == 0 ? BACKGROUND_NIGHT_COLOR : (int) (curTime - this.mOldTime);
            this.mOldTime = curTime;
            return time > 30 ? MAX_TIME : time;
        }

        public void updateRoatationInfo(float x, float y, float z, float offsetX1, float offsetY1) {
            this.offsetX1 = ((this.offset * offsetX1) / 1000.0f) + offsetX1;
            this.offsetY1 = offsetY1;
        }
    }

    private class Sun {
        protected int centerX;
        protected int centerY;
        protected float curB;
        protected float curG;
        protected float curR;
        protected int edges;
        protected float fromB;
        protected float fromG;
        protected float fromR;
        protected float offset;
        protected float offsetX1;
        protected float offsetY1;
        protected long oldTime;
        protected Point[] p;
        protected Paint paint;
        protected double pointDegree;
        protected float radius;
        protected double rotateDegree;
        protected float rotateSpeed;
        protected float scale;
        protected float spB;
        protected float spG;
        protected float spR;
        protected float toB;
        protected float toG;
        protected float toR;

        public Sun(float radius, float startDegree, float rotateSpeed, int edges, int fromR, int fromG, int fromB,
                   int toR, int toG, int toB, int fromRNight, int fromGNight, int fromBNight, int toRNight, int
                           toGNight, int toBNight, float offset) {
            this.offsetX1 = 0.0f;
            this.offsetY1 = 0.0f;
            this.radius = 0.0f;
            this.rotateSpeed = 0.0f;
            this.rotateDegree = 0.0d;
            this.edges = 0;
            this.centerX = 0;
            this.centerY = 0;
            this.pointDegree = 0.0d;
            this.scale = 1.0f;
            this.oldTime = 0;
            this.radius = radius;
            this.rotateDegree = (double) startDegree;
            this.rotateSpeed = rotateSpeed;
            if (edges % 2 != 0) {
                edges++;
            }
            this.edges = edges;
            if (this.edges > 0) {
                this.pointDegree = 360.0d / ((double) this.edges);
            }
            this.paint = new Paint();
            this.fromR = (float) fromR;
            this.fromG = (float) fromG;
            this.fromB = (float) fromB;
            this.toR = (float) toR;
            this.toG = (float) toG;
            this.toB = (float) toB;
            this.curR = this.fromR;
            this.curG = this.fromG;
            this.curB = this.fromB;
            this.spR = ((float) (fromR - toR)) / 2000.0f;
            this.spG = ((float) (fromG - toG)) / 2000.0f;
            this.spB = ((float) (fromB - toB)) / 2000.0f;
            this.paint.setColor(Color.rgb((int) this.curR, (int) this.curG, (int) this.curB));
            this.offset = offset;
            this.p = new Point[this.edges];
        }

        public boolean draw(Canvas canvas) {
            long curTime = System.currentTimeMillis();
            int t = this.oldTime == 0 ? BACKGROUND_NIGHT_COLOR : (int) (curTime - this.oldTime);
            this.oldTime = curTime;
            if (t > 30) {
                t = MAX_TIME;
            }
            this.rotateDegree += (double) ((this.rotateSpeed * ((float) t)) / 1000.0f);
            if (Math.abs(this.rotateDegree % 720.0d) < 0.05000000074505806d) {
                this.rotateDegree = 0.0d;
            }
            setPaintColor(RainSurfaceView.RAIN_LEVEL_THUNDERSHOWER);
            this.centerX = canvas.getWidth() / 2;
            this.centerY = canvas.getHeight() / 2;
            updateAllPoints();
            int j = this.edges / 2;
            for (int i = BACKGROUND_NIGHT_COLOR; i < j; i++) {
                Point from = getCentralPoint(this.p[i], this.p[i + 1]);
                Point to = getCentralPoint(this.p[(i + j) % this.edges], this.p[((i + j) + 1) % this.edges]);
                this.paint.setStrokeWidth(lengthBetweenPoints(this.p[i], this.p[i + 1]));
                canvas.drawLine((float) from.x, (float) from.y, (float) to.x, (float) to.y, this.paint);
            }
            return true;
        }

        protected void setPaintColor(int t) {
            if (this.fromR > this.toR) {
                if ((this.spR > 0.0f && this.curR >= this.fromR) || (this.spR < 0.0f && this.curR <= this.toR)) {
                    this.spR = -this.spR;
                }
            } else if (this.fromR < this.toR) {
                if ((this.spR > 0.0f && this.curR >= this.toR) || (this.spR < 0.0f && this.curR <= this.fromR)) {
                    this.spR = -this.spR;
                }
            }
            if (this.fromG > this.toG) {
                if ((this.spG > 0.0f && this.curG >= this.fromG) || (this.spG < 0.0f && this.curG <= this.toG)) {
                    this.spG = -this.spG;
                }
            } else if (this.fromG < this.toG) {
                if ((this.spG > 0.0f && this.curG >= this.toG) || (this.spG < 0.0f && this.curG <= this.fromG)) {
                    this.spG = -this.spG;
                }
            }
            if (this.fromB > this.toB) {
                if ((this.spB > 0.0f && this.curB >= this.fromB) || (this.spB < 0.0f && this.curB <= this.toB)) {
                    this.spB = -this.spB;
                }
            } else if (this.fromB < this.toB) {
                if ((this.spB > 0.0f && this.curB >= this.toB) || (this.spB < 0.0f && this.curB <= this.fromB)) {
                    this.spB = -this.spB;
                }
            }
            this.curR += this.spR * ((float) t);
            this.curG += this.spG * ((float) t);
            this.curB += this.spB * ((float) t);
            this.paint.setColor(Color.rgb((int) this.curR, (int) this.curG, (int) this.curB));
        }

        protected Point getCentralPoint(Point p1, Point p2) {
            Point p = new Point();
            p.x = (p1.x + p2.x) / 2;
            p.y = (p1.y + p2.y) / 2;
            return p;
        }

        protected float lengthBetweenPoints(Point p1, Point p2) {
            return (float) Math.sqrt(Math.pow((double) (p1.x - p2.x), 2.0d) + Math.pow((double) (p1.y - p2.y), 2.0d));
        }

        protected void updateAllPoints() {
            for (int i = BACKGROUND_NIGHT_COLOR; i < this.edges; i++) {
                this.p[i] = getNextPoint(i);
            }
        }

        protected Point getNextPoint(int index) {
            double radian = Math.toRadians((this.pointDegree * ((double) index)) + this.rotateDegree);
            return new Point((int) (((((double) (this.scale * this.radius)) * Math.cos(radian)) + ((double) this
                    .centerX)) + ((double) this.offsetX1)), (int) (((((double) (this.scale * this.radius)) * Math.sin
                    (radian)) + ((double) this.centerY)) + ((double) this.offsetY1)));
        }

        public void updateRoatationInfo(float x, float y, float z, float offsetX1, float offsetY1, float scale) {
            this.offsetX1 = ((this.offset * offsetX1) / 1000.0f) + offsetX1;
            this.offsetY1 = offsetY1;
            this.scale = scale;
        }
    }

    private class Shine extends Sun {
        protected Path mPath;

        public Shine(float radius, float startDegree, float rotateSpeed, int edges, int fromR, int fromG, int fromB,
                     int toR, int toG, int toB, float offset) {
            super(radius, startDegree, rotateSpeed, edges, fromR, fromG, fromB, toR, toG, toB, fromR, fromG, fromB,
                    toR, toG, toB, offset);
            this.mPath = new Path();
            this.paint.setStyle(Style.FILL);
        }

        public boolean draw(Canvas canvas) {
            int t;
            long curTime = System.currentTimeMillis();
            if (this.oldTime == 0) {
                t = 0;
            } else {
                t = (int) (curTime - this.oldTime);
            }
            this.oldTime = curTime;
            if (t > 30) {
                t = MAX_TIME;
            }
            this.rotateDegree += (double) ((this.rotateSpeed * ((float) t)) / 1000.0f);
            if (Math.abs(this.rotateDegree % 720.0d) < 0.05000000074505806d) {
                this.rotateDegree = 0.0d;
            }
            this.centerX = canvas.getWidth() / 2;
            this.centerY = canvas.getHeight() / 2;
            updateAllPoints();
            this.mPath.reset();
            this.mPath.moveTo((float) this.p[0].x, (float) this.p[0].y);
            for (int i = 1; i < this.p.length; i++) {
                this.mPath.lineTo((float) this.p[i].x, (float) this.p[i].y);
            }
            this.mPath.close();
            canvas.drawPath(this.mPath, this.paint);
            return true;
        }

        public void updateRoatationInfo(float x, float y, float z, float offsetX1, float offsetY1, float scale) {
            this.offsetX1 = ((-this.offset) * offsetX1) / 1600.0f;
            this.offsetY1 = ((-this.offset) * offsetY1) / 1600.0f;
            float alpha = (110.0f + (offsetY1 / 10.0f)) / 50.0f;
            if (alpha > 0.0f) {
                alpha = AutoScrollHelper.RELATIVE_UNSPECIFIED;
            } else if (alpha < -1.0f) {
                alpha = -1.0f;
            }
            this.paint.setAlpha((int) (-(150.0f * alpha)));
        }
    }

    static {
        BACKGROUND_COLOR = Color.parseColor("#ff4a97d2");
        BACKGROUND_NIGHT_COLOR = Color.parseColor("#0a213e");
    }

    public SunnyView(Context context, boolean isDay) {
        super(context, isDay);
        this.mDeltaAngleZ = 0.0f;
        this.mDeltaAngleY = 0.0f;
        this.MAX_SPEED = 1.0f;
        this.offsetX = 0.0f;
        this.offsetY = 0.0f;
        this.offsetX1 = 0.0f;
        this.offsetY1 = 0.0f;
        this.moveSpeedX = 0.0f;
        this.moveSpeedY = 0.0f;
        this.acceleration = 0.002f;
        this.rangeX = 800.0f;
        this.rangeY = 1600.0f;
        this.scaleAcceleration = 1.0E-5f;
        this.scaleSpeed = 0.0f;
        this.scale = 1.0f;
        this.oldTime = 0;
        this.sy = 1.0f;
        this.isInit = false;
        this.mDesity = context.getResources().getDisplayMetrics().density;
        init();
        setDayBackgroundColor(BACKGROUND_COLOR);
        setNightBackgroundColor(BACKGROUND_NIGHT_COLOR);
        this.rangeX = (float) UIUtil.dip2px(getContext(), 266.66666f);
        this.rangeY = (float) UIUtil.dip2px(getContext(), 533.3333f);
        this.acceleration = 0.1f * this.mDesity;
    }

    public SunnyView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public SunnyView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.mDeltaAngleZ = 0.0f;
        this.mDeltaAngleY = 0.0f;
        this.MAX_SPEED = 1.0f;
        this.offsetX = 0.0f;
        this.offsetY = 0.0f;
        this.offsetX1 = 0.0f;
        this.offsetY1 = 0.0f;
        this.moveSpeedX = 0.0f;
        this.moveSpeedY = 0.0f;
        this.acceleration = 0.002f;
        this.rangeX = 800.0f;
        this.rangeY = 1600.0f;
        this.scaleAcceleration = 1.0E-5f;
        this.scaleSpeed = 0.0f;
        this.scale = 1.0f;
        this.oldTime = 0;
        this.sy = 1.0f;
        this.isInit = false;
    }

    public void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }

    private void init() {
        this.isInit = false;
        this.mAnimate = false;
        this.mSunList = new ArrayList();
        int x = (int) (1.6666666f * this.mDesity);
        ArrayList arrayList = this.mSunList;
        arrayList.add(new Sun(((float) x) * (98.0f * this.mDesity), 15.0f, 15.6f, 12, 144, 195, 186, 195, 217, 138,
                8, 24, 46, 11, 31, 57, 0.0f));
        arrayList = this.mSunList;
        arrayList.add(new Sun(((float) x) * (87.0f * this.mDesity), 0.0f, 13.7f, 12, 183, 198, 136, 221, 201, 87, 10,
                29, 52, 14, 37, 68, this.mDesity * 16.666666f));
        arrayList = this.mSunList;
        arrayList.add(new Sun(((float) x) * (76.0f * this.mDesity), 15.0f, 11.7f, 12, 206, 186, 99, 228, 174, 63, 12,
                32, 59, 18, 44, 80, this.mDesity * 33.333332f));
        arrayList = this.mSunList;
        arrayList.add(new Sun(((float) x) * (63.0f * this.mDesity), 0.0f, 9.8f, 12, 215, 167, 78, 223, 149, 58, 15,
                37, 66, 25, 55, 95, this.mDesity * 50.0f));
        arrayList = this.mSunList;
        arrayList.add(new Sun(((float) x) * (52.333332f * this.mDesity), 15.0f, 7.8f, 12, 218, 145, 68, 220, 123, 59,
                18, 42, 72, 31, 64, 107, this.mDesity * 66.666664f));
        arrayList = this.mSunList;
        arrayList.add(new Sun(((float) x) * (42.333332f * this.mDesity), 30.0f, 5.9f, 10, 216, 125, 60, 215, 105, 52,
                23, 50, 82, 40, 74, 118, this.mDesity * 83.333336f));
        arrayList = this.mSunList;
        arrayList.add(new Sun(((float) x) * (31.666666f * this.mDesity), 10.0f, 3.9f, 10, 213, 106, 53, 211, 87, 46,
                30, 60, 95, 49, 88, 129, this.mDesity * 116.666664f));
        arrayList = this.mSunList;
        arrayList.add(new Sun(((float) x) * (20.333334f * this.mDesity), 25.0f, 2.0f, 10, 209, 70, 40, 209, 70, 40,
                56, 103, 134, 56, 103, 134, this.mDesity * 150.0f));
        this.mSunList.add(new Shine(this.mDesity * 40.0f, 0.0f, 32.8f, 6, 255, 255, 255, 255, 255, 255, this.mDesity
                * 300.0f));
        this.mSunList.add(new Shine(this.mDesity * 30.0f, 35.0f, 32.8f, 6, 255, 255, 255, 255, 255, 255, this.mDesity
                * 183.33333f));
        this.mSunList.add(new Shine(this.mDesity * 20.0f, 70.0f, 30.0f, 6, 255, 255, 255, 255, 255, 255, this.mDesity
                * 73.0f));
        this.mSunList.add(new Shine(this.mDesity * 10.0f, 0.0f, 32.8f, 6, 255, 255, 255, 255, 255, 255, this.mDesity
                * -40.0f));
        float centerX = -320.0f * this.mDesity;
        float centerY = -55.0f * this.mDesity;
        this.mStarList = new ArrayList();
        this.mStarList.add(new Star((float) UIUtil.dip2px(getContext(), 362.66666f), (float) UIUtil.dip2px(getContext
                (), DefaultRetryPolicy.DEFAULT_BACKOFF_MULT), centerX, centerY, 0.0f, -90.0f, 180.0f, 1, Color
                .parseColor("#82ece1")));
        this.mStarList.add(new Star((float) UIUtil.dip2px(getContext(), 394.33334f), (float) UIUtil.dip2px(getContext
                (), 0.6666667f), centerX, centerY, 0.0f, -90.0f, 180.0f, 1, Color.parseColor("#6a9b95")));
        this.mStarList.add(new Star((float) UIUtil.dip2px(getContext(), 446.0f), (float) UIUtil.dip2px(getContext(),
                0.6666667f), centerX, centerY, 0.0f, -90.0f, 180.0f, 1, Color.parseColor("#6a9b95")));
        this.mStarList.add(new Star((float) UIUtil.dip2px(getContext(), 486.66666f), (float) UIUtil.dip2px(getContext
                (), 1.3333334f), centerX, centerY, 0.0f, -90.0f, 180.0f, 1, Color.parseColor("#c8fff9")));
        this.mStarList.add(new Star((float) UIUtil.dip2px(getContext(), 530.0f), (float) UIUtil.dip2px(getContext(),
                1.3333334f), centerX, centerY, 0.0f, -90.0f, 180.0f, 1, Color.parseColor("#c8fff9")));
        this.mStarList.add(new Star((float) UIUtil.dip2px(getContext(), 565.0f), (float) UIUtil.dip2px(getContext(),
                0.6666667f), centerX, centerY, 0.0f, -90.0f, 180.0f, 1, Color.parseColor("#82ece1")));
        this.mStarList.add(new Star((float) UIUtil.dip2px(getContext(), 638.3333f), (float) UIUtil.dip2px(getContext
                (), 0.6666667f), centerX, centerY, 0.0f, -90.0f, 180.0f, 1, Color.parseColor("#6a9b95")));
        this.mStarList.add(new Star((float) UIUtil.dip2px(getContext(), 654.6667f), (float) UIUtil.dip2px(getContext
                (), 0.6666667f), centerX, centerY, 0.0f, -90.0f, 180.0f, 1, Color.parseColor("#6a9b95")));
        this.mStarList.add(new Star((float) UIUtil.dip2px(getContext(), 846.0f), (float) UIUtil.dip2px(getContext(),
                1.3333334f), centerX, centerY, 0.0f, -90.0f, 180.0f, 1, Color.parseColor("#ffffff")));
        this.mStarList.add(new Star((float) UIUtil.dip2px(getContext(), 963.0f), (float) UIUtil.dip2px(getContext(),
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT), centerX, centerY, 0.0f, -90.0f, 180.0f, 1, Color.parseColor
                ("#6a9b95")));
        this.mStarList.add(new Star((float) UIUtil.dip2px(getContext(), 377.66666f), (float) UIUtil.dip2px(getContext
                (), 1.1666666f), centerX, centerY, 0.036f, 10.0f, 34.5f, 2, Color.parseColor("#c8fff9")));
        this.mStarList.add(new Star((float) UIUtil.dip2px(getContext(), 422.66666f), (float) UIUtil.dip2px(getContext
                (), 2.0f), centerX, centerY, 0.018f, 16.0f, 17.7f, 4, Color.parseColor("#ffffff")));
        this.mStarList.add(new Star((float) UIUtil.dip2px(getContext(), 519.3333f), (float) UIUtil.dip2px(getContext
                (), 1.3333334f), centerX, centerY, 0.018f, 29.0f, 30.5f, 4, Color.parseColor("#82ece1")));
        this.mStarList.add(new Star((float) UIUtil.dip2px(getContext(), 545.0f), (float) UIUtil.dip2px(getContext(),
                2.6666667f), centerX, centerY, 0.036f, 5.0f, 50.0f, 2, Color.parseColor("#ffffff")));
        this.mStarList.add(new Star((float) UIUtil.dip2px(getContext(), 554.0f), (float) UIUtil.dip2px(getContext(),
                1.6666666f), centerX, centerY, 0.054f, 5.0f, 37.5f, 4, Color.parseColor("#bdf3ee")));
        this.mStarList.add(new Star((float) UIUtil.dip2px(getContext(), 593.0f), (float) UIUtil.dip2px(getContext(),
                2.8333333f), centerX, centerY, 0.018f, 33.0f, 24.6f, 4, Color.parseColor("#ffffff")));
        this.mStarList.add(new Star((float) UIUtil.dip2px(getContext(), 606.6667f), (float) UIUtil.dip2px(getContext
                (), 1.6666666f), centerX, centerY, 0.054f, 11.0f, 24.5f, 4, Color.parseColor("#82ece1")));
        this.mStarList.add(new Star((float) UIUtil.dip2px(getContext(), 661.0f), (float) UIUtil.dip2px(getContext(),
                1.6666666f), centerX, centerY, 0.036f, 43.0f, 23.0f, 2, Color.parseColor("#c8fff9")));
        this.mStarList.add(new Star((float) UIUtil.dip2px(getContext(), 685.6667f), (float) UIUtil.dip2px(getContext
                (), 1.8333334f), centerX, centerY, 0.036f, 6.0f, 37.0f, 4, Color.parseColor("#c8fff9")));
        this.mStarList.add(new Star((float) UIUtil.dip2px(getContext(), 707.0f), (float) UIUtil.dip2px(getContext(),
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT), centerX, centerY, 0.018f, 21.0f, 26.0f, 4, Color.parseColor
                ("#6a9b95")));
        this.mStarList.add(new Star((float) UIUtil.dip2px(getContext(), 746.6667f), (float) UIUtil.dip2px(getContext
                (), DefaultRetryPolicy.DEFAULT_BACKOFF_MULT), centerX, centerY, 0.018f, 14.0f, 24.5f, 4, Color
                .parseColor("#6a9b95")));
        this.mStarList.add(new Star((float) UIUtil.dip2px(getContext(), 757.3333f), (float) UIUtil.dip2px(getContext
                (), 2.0f), centerX, centerY, 0.036f, 4.0f, 26.3f, 2, Color.parseColor("#c8fff9")));
        this.mStarList.add(new Star((float) UIUtil.dip2px(getContext(), 793.0f), (float) UIUtil.dip2px(getContext(),
                2.0f), centerX, centerY, 0.018f, 18.0f, 26.0f, 4, Color.parseColor("#82ece1")));
    }

    public void draw(Canvas canvas) {
        super.draw(canvas);
        if (this.mAnimate) {
            int t = getSpentTime();
            updateOffset(t, canvas.getHeight());
            int i;
            if (isDay()) {
                updateScale(t);
                for (i = BACKGROUND_NIGHT_COLOR; i < this.mSunList.size(); i++) {
                    if (!(this.mSunList.get(i) instanceof Shine) || this.mDeltaAngleY <= -110.0f) {
                        ((Sun) this.mSunList.get(i)).updateRoatationInfo(AutoScrollHelper.RELATIVE_UNSPECIFIED, this
                                .mDeltaAngleY, this.mDeltaAngleZ, this.offsetX1, this.offsetY1, this.scale);
                        ((Sun) this.mSunList.get(i)).draw(canvas);
                    }
                }
            } else {
                for (i = BACKGROUND_NIGHT_COLOR; i < this.mStarList.size(); i++) {
                    ((Star) this.mStarList.get(i)).updateRoatationInfo(AutoScrollHelper.RELATIVE_UNSPECIFIED, this
                            .mDeltaAngleY, this.mDeltaAngleZ, this.offsetX1, this.offsetY1);
                    ((Star) this.mStarList.get(i)).draw(canvas);
                }
            }
            invalidate();
        }
    }

    private int getSpentTime() {
        long curTime = System.currentTimeMillis();
        int t = this.oldTime == 0 ? BACKGROUND_NIGHT_COLOR : (int) (curTime - this.oldTime);
        this.oldTime = curTime;
        return t > 30 ? MAX_TIME : t;
    }

    private void updateOffset(int t, int height) {
        if (this.mDeltaAngleZ > 90.0f) {
            this.mDeltaAngleZ = 90.0f;
        }
        if (this.mDeltaAngleZ < -90.0f) {
            this.mDeltaAngleZ = -90.0f;
        }
        this.offsetX = this.rangeX * (this.mDeltaAngleZ / 90.0f);
        this.offsetY = this.rangeY * (this.mDeltaAngleY / 180.0f);
        if (!this.isInit) {
            this.isInit = true;
            this.offsetY = ((float) ((-height) * 2)) / 3.0f;
            this.offsetX1 = this.offsetX;
            this.offsetY1 = this.offsetY;
        }
        float disX = Math.abs(this.offsetX1 - this.offsetX);
        float disY = Math.abs(this.offsetY1 - this.offsetY);
        if (this.moveSpeedX < 1.0f) {
            this.moveSpeedX += this.acceleration * ((float) t);
        } else {
            this.moveSpeedX -= this.acceleration * ((float) t);
        }
        if (this.moveSpeedX < 0.0f) {
            this.moveSpeedX = this.acceleration;
        }
        if (disX < 133.33333f * this.mDesity) {
            this.moveSpeedX = Math.min(this.moveSpeedX, (1.0f * disX) / 400.0f);
        }
        float moveOffsetX = ((this.moveSpeedX * ((float) t)) / 3.0f) * this.mDesity;
        if (disX > moveOffsetX) {
            if (this.offsetX1 > this.offsetX) {
                this.offsetX1 -= moveOffsetX;
            } else {
                this.offsetX1 += moveOffsetX;
            }
        }
        if (this.moveSpeedY < 1.0f) {
            this.moveSpeedY += this.acceleration * ((float) t);
        } else {
            this.moveSpeedY -= this.acceleration * ((float) t);
        }
        if (this.moveSpeedY < 0.0f) {
            this.moveSpeedY = this.acceleration;
        }
        if (disY < 133.33333f * this.mDesity) {
            this.moveSpeedY = Math.min(this.moveSpeedY, (1.0f * disY) / 400.0f);
        }
        float moveOffsetY = ((this.moveSpeedY * ((float) t)) / 3.0f) * this.mDesity;
        if (disY <= moveOffsetY) {
            return;
        }
        if (this.offsetY1 > this.offsetY) {
            this.offsetY1 -= moveOffsetY;
        } else {
            this.offsetY1 += moveOffsetY;
        }
    }

    private void updateScale(int t) {
        if (this.mDeltaAngleY < -30.0f && this.mDeltaAngleY > -90.0f) {
            this.sy = (-(this.mDeltaAngleY - 30.0f)) / 120.0f;
        }
        float dis = Math.abs(this.scale - this.sy);
        if (this.scaleSpeed < dis / 200.0f) {
            this.scaleSpeed += this.scaleAcceleration * ((float) t);
        } else {
            this.scaleSpeed -= this.scaleAcceleration * ((float) t);
        }
        if (this.scaleSpeed < 0.0f) {
            this.scaleSpeed = this.scaleAcceleration;
        }
        float scaleOffset = ((this.scaleSpeed * ((float) t)) / 3.0f) * this.mDesity;
        if (dis <= scaleOffset) {
            return;
        }
        if (this.scale > this.sy) {
            this.scale -= scaleOffset;
        } else {
            this.scale += scaleOffset;
        }
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

    protected void onCreateOrientationInfoListener() {
        this.mListener = new OrientationSensorUtil.OrientationInfoListener() {
            public void onOrientationInfoChange(float x, float y, float z) {
                SunnyView.this.mDeltaAngleZ = z;
                if (y < 0.0f) {
                    SunnyView.this.mDeltaAngleY = -y;
                } else {
                    SunnyView.this.mDeltaAngleY = y;
                }
                SunnyView.this.mDeltaAngleY = SunnyView.this.mDeltaAngleY - 0.024902344f;
            }
        };
    }
}
