package com.opweather.widget;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.graphics.Path;
import android.graphics.Point;
import android.graphics.RectF;
import android.util.AttributeSet;

import com.opweather.constants.GlobalConfig;
import com.opweather.util.OrientationSensorUtil;
import com.opweather.util.UIUtil;

import java.util.ArrayList;

public class SunnyView extends BaseWeatherView {
    private static final int BACKGROUND_COLOR = Color.parseColor("#ff4a97d2");
    private static final int BACKGROUND_NIGHT_COLOR = Color.parseColor("#0a213e");
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
        private long mOldTime = 0;
        protected Paint mPaint;
        protected float mPieces;
        protected float mRadius;
        protected float mRotateAngle;
        protected float mRotateSpeed;
        protected float mStartAngle;
        protected float mSweepAngle;
        protected float offset;
        protected float offsetX1 = 0.0f;
        protected float offsetY1 = 0.0f;

        public Star(float radius, float strokeWidth, float centerX, float centerY, float rotateSpeed, float
                startAngle, float sweepAngle, int pieces, int color) {
            mRadius = radius;
            mCenterX = centerX;
            mCenterY = centerY;
            mRotateSpeed = rotateSpeed;
            mStartAngle = startAngle;
            mSweepAngle = sweepAngle;
            mPieces = (float) pieces;
            mRotateAngle = 0.0f;
            mPaint = new Paint();
            mPaint.setAntiAlias(true);
            mPaint.setStyle(Style.STROKE);
            mPaint.setColor(color);
            mPaint.setStrokeWidth(strokeWidth);
        }

        public boolean draw(Canvas canvas) {
            RectF oval = new RectF((mCenterX + offsetX1) - mRadius, ((mCenterY + offsetY1) +
                    1250.0f) - mRadius, (mCenterX + offsetX1) + mRadius, ((mCenterY + offsetY1) + 1250.0f) + mRadius);
            mRotateAngle += mRotateSpeed * ((float) getSpentTime());
            if (mRotateAngle > 360.0f) {
                mRotateAngle -= 360.0f;
            }
            for (int i = 0; ((float) i) < mPieces; i++) {
                float startAngle;
                if (i < 2) {
                    startAngle = (mStartAngle + ((float) (i * WeatherCircleView.START_ANGEL_180))) + mRotateAngle;
                    if (startAngle > 360.0f) {
                        startAngle -= 360.0f;
                    }
                    if (startAngle <= 90.0f || mSweepAngle + startAngle >= 270.0f) {
                        canvas.drawArc(oval, startAngle, mSweepAngle, false, mPaint);
                    }
                } else {
                    startAngle = (((180.0f - mSweepAngle) - mStartAngle) + (((float) (i - 2)) * 180.0f)) + mRotateAngle;
                    if (startAngle > 360.0f) {
                        startAngle -= 360.0f;
                    }
                    if (startAngle <= 90.0f || mSweepAngle + startAngle >= 270.0f) {
                        canvas.drawArc(oval, startAngle, mSweepAngle, false, mPaint);
                    }
                }
            }
            return true;
        }

        private int getSpentTime() {
            long curTime = System.currentTimeMillis();
            int time = mOldTime == 0 ? 0 : (int) (curTime - mOldTime);
            mOldTime = curTime;
            if (time > 30) {
                return 30;
            }
            return time;
        }

        public void updateRoatationInfo(float x, float y, float z, float offsetX1, float offsetY1) {
            offsetX1 = ((offset * offsetX1) / 1000.0f) + offsetX1;
            offsetY1 = offsetY1;
        }
    }

    private class Sun {
        protected int centerX = 0;
        protected int centerY = 0;
        protected float curB;
        protected float curG;
        protected float curR;
        protected int edges = 0;
        protected float fromB;
        protected float fromG;
        protected float fromR;
        protected float offset;
        protected float offsetX1 = 0.0f;
        protected float offsetY1 = 0.0f;
        protected long oldTime = 0;
        protected Point[] p;
        protected Paint paint;
        protected double pointDegree = 0.0d;
        protected float radius = 0.0f;
        protected double rotateDegree = 0.0d;
        protected float rotateSpeed = 0.0f;
        protected float scale = 1.0f;
        protected float spB;
        protected float spG;
        protected float spR;
        protected float toB;
        protected float toG;
        protected float toR;

        public Sun(float radius, float startDegree, float rotateSpeed, int edges, int fromR, int fromG, int fromB,
                   int toR, int toG, int toB, int fromRNight, int fromGNight, int fromBNight, int toRNight, int
                           toGNight, int toBNight, float offset) {
            this.radius = radius;
            rotateDegree = (double) startDegree;
            this.rotateSpeed = rotateSpeed;
            if (this.edges % 2 != 0) {
                this.edges++;
            }
            this.edges = edges;
            if (edges > 0) {
                pointDegree = 360.0d / ((double) edges);
            }
            paint = new Paint();
            this.fromR = (float) fromR;
            this.fromG = (float) fromG;
            this.fromB = (float) fromB;
            this.toR = (float) toR;
            this.toG = (float) toG;
            this.toB = (float) toB;
            curR = fromR;
            curG = fromG;
            curB = fromB;
            spR = ((float) (fromR - toR)) / 2000.0f;
            spG = ((float) (fromG - toG)) / 2000.0f;
            spB = ((float) (fromB - toB)) / 2000.0f;
            paint.setColor(Color.rgb((int) curR, (int) curG, (int) curB));
            this.offset = offset;
            p = new Point[edges];
        }

        public boolean draw(Canvas canvas) {
            int t;
            long curTime = System.currentTimeMillis();
            if (oldTime == 0) {
                t = 0;
            } else {
                t = (int) (curTime - oldTime);
            }
            oldTime = curTime;
            if (t > 30) {
                t = 30;
            }
            rotateDegree += (double) ((rotateSpeed * ((float) t)) / 1000.0f);
            if (Math.abs(rotateDegree % 720.0d) < 0.05000000074505806d) {
                rotateDegree = 0.0d;
            }
            setPaintColor(5);
            centerX = canvas.getWidth() / 2;
            centerY = canvas.getHeight() / 2;
            updateAllPoints();
            int j = edges / 2;
            for (int i = 0; i < j; i++) {
                Point from = getCentralPoint(p[i], p[i + 1]);
                Point to = getCentralPoint(p[(i + j) % edges], p[((i + j) + 1) % edges]);
                paint.setStrokeWidth(lengthBetweenPoints(p[i], p[i + 1]));
                canvas.drawLine((float) from.x, (float) from.y, (float) to.x, (float) to.y, paint);
            }
            return true;
        }

        protected void setPaintColor(int t) {
            if (fromR > toR) {
                if ((spR > 0.0f && curR >= fromR) || (spR < 0.0f && curR <= toR)) {
                    spR = -spR;
                }
            } else if (fromR < toR && ((spR > 0.0f && curR >= toR) || (spR < 0.0f &&
                    curR <= fromR))) {
                spR = -spR;
            }
            if (fromG > toG) {
                if ((spG > 0.0f && curG >= fromG) || (spG < 0.0f && curG <= toG)) {
                    spG = -spG;
                }
            } else if (fromG < toG && ((spG > 0.0f && curG >= toG) || (spG < 0.0f &&
                    curG <= fromG))) {
                spG = -spG;
            }
            if (fromB > toB) {
                if ((spB > 0.0f && curB >= fromB) || (spB < 0.0f && curB <= toB)) {
                    spB = -spB;
                }
            } else if (fromB < toB && ((spB > 0.0f && curB >= toB) || (spB < 0.0f &&
                    curB <= fromB))) {
                spB = -spB;
            }
            curR += spR * ((float) t);
            curG += spG * ((float) t);
            curB += spB * ((float) t);
            paint.setColor(Color.rgb((int) curR, (int) curG, (int) curB));
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
            for (int i = 0; i < edges; i++) {
                p[i] = getNextPoint(i);
            }
        }

        protected Point getNextPoint(int index) {
            double radian = Math.toRadians((pointDegree * ((double) index)) + rotateDegree);
            return new Point((int) (((((double) (scale * radius)) * Math.cos(radian)) + ((double) this
                    .centerX)) + ((double) offsetX1)), (int) (((((double) (scale * radius)) * Math.sin
                    (radian)) + ((double) centerY)) + ((double) offsetY1)));
        }

        public void updateRoatationInfo(float x, float y, float z, float offsetX1, float offsetY1, float scale) {
            this.offsetX1 = ((offset * offsetX1) / 1000.0f) + offsetX1;
            this.offsetY1 = offsetY1;
            this.scale = scale;
        }
    }

    private class Shine extends Sun {
        protected Path mPath = new Path();

        public Shine(float radius, float startDegree, float rotateSpeed, int edges, int fromR, int fromG, int fromB,
                     int toR, int toG, int toB, float offset) {
            super(radius, startDegree, rotateSpeed, edges, fromR, fromG, fromB, toR, toG, toB, fromR, fromG, fromB,
                    toR, toG, toB, offset);
            paint.setStyle(Style.FILL);
        }

        public boolean draw(Canvas canvas) {
            int t;
            long curTime = System.currentTimeMillis();
            if (oldTime == 0) {
                t = 0;
            } else {
                t = (int) (curTime - oldTime);
            }
            oldTime = curTime;
            if (t > 30) {
                t = 30;
            }
            rotateDegree += (double) ((rotateSpeed * ((float) t)) / 1000.0f);
            if (Math.abs(rotateDegree % 720.0d) < 0.05000000074505806d) {
                rotateDegree = 0.0d;
            }
            centerX = canvas.getWidth() / 2;
            centerY = canvas.getHeight() / 2;
            updateAllPoints();
            mPath.reset();
            mPath.moveTo((float) p[0].x, (float) p[0].y);
            for (int i = 1; i < p.length; i++) {
                mPath.lineTo((float) p[i].x, (float) p[i].y);
            }
            mPath.close();
            canvas.drawPath(mPath, paint);
            return true;
        }

        public void updateRoatationInfo(float x, float y, float z, float offsetX1, float offsetY1, float scale) {
            this.offsetX1 = ((-offset) * offsetX1) / 1600.0f;
            this.offsetY1 = ((-offset) * offsetY1) / 1600.0f;
            float alpha = (110.0f + (offsetY1 / 10.0f)) / 50.0f;
            if (alpha > 0.0f) {
                alpha = 0.0f;
            } else if (alpha < -1.0f) {
                alpha = -1.0f;
            }
            paint.setAlpha((int) (-(150.0f * alpha)));
        }
    }

    public SunnyView(Context context, boolean isDay) {
        super(context, isDay);
        mDeltaAngleZ = 0.0f;
        mDeltaAngleY = 0.0f;
        MAX_SPEED = 1.0f;
        offsetX = 0.0f;
        offsetY = 0.0f;
        offsetX1 = 0.0f;
        offsetY1 = 0.0f;
        moveSpeedX = 0.0f;
        moveSpeedY = 0.0f;
        acceleration = 0.002f;
        rangeX = 800.0f;
        rangeY = 1600.0f;
        scaleAcceleration = 1.0E-5f;
        scaleSpeed = 0.0f;
        scale = 1.0f;
        oldTime = 0;
        sy = 1.0f;
        isInit = false;
        mDesity = context.getResources().getDisplayMetrics().density;
        init();
        setDayBackgroundColor(BACKGROUND_COLOR);
        setNightBackgroundColor(BACKGROUND_NIGHT_COLOR);
        rangeX = (float) UIUtil.dip2px(getContext(), 266.66666f);
        rangeY = (float) UIUtil.dip2px(getContext(), 533.3333f);
        acceleration = 0.1f * mDesity;
    }

    public SunnyView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public SunnyView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        mDeltaAngleZ = 0.0f;
        mDeltaAngleY = 0.0f;
        MAX_SPEED = 1.0f;
        offsetX = 0.0f;
        offsetY = 0.0f;
        offsetX1 = 0.0f;
        offsetY1 = 0.0f;
        moveSpeedX = 0.0f;
        moveSpeedY = 0.0f;
        acceleration = 0.002f;
        rangeX = 800.0f;
        rangeY = 1600.0f;
        scaleAcceleration = 1.0E-5f;
        scaleSpeed = 0.0f;
        scale = 1.0f;
        oldTime = 0;
        sy = 1.0f;
        isInit = false;
    }

    public void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }

    private void init() {
        isInit = false;
        mAnimate = false;
        mSunList = new ArrayList<>();
        int x = (int) (1.6666666f * mDesity);
        ArrayList arrayList = mSunList;
        arrayList.add(new Sun(((float) x) * (98.0f * mDesity), 15.0f, 15.6f, 12, 144, 195, 186, 195, 217, 138,
                8, 24, 46, 11, 31, 57, 0.0f));
        arrayList = mSunList;
        arrayList.add(new Sun(((float) x) * (87.0f * mDesity), 0.0f, 13.7f, 12, 183, 198, 136, 221, GlobalConfig
                .MESSAGE_ACCU_GET_COUNTRY_CHINA, 87, 10, 29, 52, 14, 37, 68, mDesity * 16.666666f));
        arrayList = mSunList;
        arrayList.add(new Sun(((float) x) * (76.0f * mDesity), 15.0f, 11.7f, 12, 206, 186, 99, 228, 174, 63, 12,
                32, 59, 18, 44, 80, mDesity * 33.333332f));
        arrayList = mSunList;
        arrayList.add(new Sun(((float) x) * (63.0f * mDesity), 0.0f, 9.8f, 12, 215, 167, 78, 223, 149, 58, 15,
                37, 66, 25, 55, 95, mDesity * 50.0f));
        arrayList = mSunList;
        arrayList.add(new Sun(((float) x) * (52.333332f * mDesity), 15.0f, 7.8f, 12, 218, 145, 68, 220, 123, 59,
                18, 42, 72, 31, 64, 107, mDesity * 66.666664f));
        arrayList = mSunList;
        arrayList.add(new Sun(((float) x) * (42.333332f * mDesity), 30.0f, 5.9f, 10, 216, 125, 60, 215, 105, 52,
                23, 50, 82, 40, 74, 118, mDesity * 83.333336f));
        arrayList = mSunList;
        arrayList.add(new Sun(((float) x) * (31.666666f * mDesity), 10.0f, 3.9f, 10, 213, 106, 53, 211, 87, 46,
                30, 60, 95, 49, 88, 129, mDesity * 116.666664f));
        arrayList = mSunList;
        arrayList.add(new Sun(((float) x) * (20.333334f * mDesity), 25.0f, 2.0f, 10, 209, 70, 40, 209, 70, 40,
                56, 103, 134, 56, 103, 134, mDesity * 150.0f));
        mSunList.add(new Shine(mDesity * 40.0f, 0.0f, 32.8f, 6, 255, 255, 255, 255, 255, 255, mDesity
                * 300.0f));
        mSunList.add(new Shine(mDesity * 30.0f, 35.0f, 32.8f, 6, 255, 255, 255, 255, 255, 255, mDesity
                * 183.33333f));
        mSunList.add(new Shine(mDesity * 20.0f, 70.0f, 30.0f, 6, 255, 255, 255, 255, 255, 255, mDesity
                * 73.0f));
        mSunList.add(new Shine(mDesity * 10.0f, 0.0f, 32.8f, 6, 255, 255, 255, 255, 255, 255, mDesity
                * -40.0f));
        float centerX = -320.0f * mDesity;
        float centerY = -55.0f * mDesity;
        mStarList = new ArrayList();
        mStarList.add(new Star((float) UIUtil.dip2px(getContext(), 362.66666f), (float) UIUtil.dip2px(getContext
                (), 1.0f), centerX, centerY, 0.0f, -90.0f, 180.0f, 1, Color.parseColor("#82ece1")));
        mStarList.add(new Star((float) UIUtil.dip2px(getContext(), 394.33334f), (float) UIUtil.dip2px(getContext
                (), 0.6666667f), centerX, centerY, 0.0f, -90.0f, 180.0f, 1, Color.parseColor("#6a9b95")));
        mStarList.add(new Star((float) UIUtil.dip2px(getContext(), 446.0f), (float) UIUtil.dip2px(getContext(),
                0.6666667f), centerX, centerY, 0.0f, -90.0f, 180.0f, 1, Color.parseColor("#6a9b95")));
        mStarList.add(new Star((float) UIUtil.dip2px(getContext(), 486.66666f), (float) UIUtil.dip2px(getContext
                (), 1.3333334f), centerX, centerY, 0.0f, -90.0f, 180.0f, 1, Color.parseColor("#c8fff9")));
        mStarList.add(new Star((float) UIUtil.dip2px(getContext(), 530.0f), (float) UIUtil.dip2px(getContext(),
                1.3333334f), centerX, centerY, 0.0f, -90.0f, 180.0f, 1, Color.parseColor("#c8fff9")));
        mStarList.add(new Star((float) UIUtil.dip2px(getContext(), 565.0f), (float) UIUtil.dip2px(getContext(),
                0.6666667f), centerX, centerY, 0.0f, -90.0f, 180.0f, 1, Color.parseColor("#82ece1")));
        mStarList.add(new Star((float) UIUtil.dip2px(getContext(), 638.3333f), (float) UIUtil.dip2px(getContext
                (), 0.6666667f), centerX, centerY, 0.0f, -90.0f, 180.0f, 1, Color.parseColor("#6a9b95")));
        mStarList.add(new Star((float) UIUtil.dip2px(getContext(), 654.6667f), (float) UIUtil.dip2px(getContext
                (), 0.6666667f), centerX, centerY, 0.0f, -90.0f, 180.0f, 1, Color.parseColor("#6a9b95")));
        mStarList.add(new Star((float) UIUtil.dip2px(getContext(), 846.0f), (float) UIUtil.dip2px(getContext(),
                1.3333334f), centerX, centerY, 0.0f, -90.0f, 180.0f, 1, Color.parseColor("#ffffff")));
        mStarList.add(new Star((float) UIUtil.dip2px(getContext(), 963.0f), (float) UIUtil.dip2px(getContext(),
                1.0f), centerX, centerY, 0.0f, -90.0f, 180.0f, 1, Color.parseColor("#6a9b95")));
        mStarList.add(new Star((float) UIUtil.dip2px(getContext(), 377.66666f), (float) UIUtil.dip2px(getContext
                (), 1.1666666f), centerX, centerY, 0.036f, 10.0f, 34.5f, 2, Color.parseColor("#c8fff9")));
        mStarList.add(new Star((float) UIUtil.dip2px(getContext(), 422.66666f), (float) UIUtil.dip2px(getContext
                (), 2.0f), centerX, centerY, 0.018f, 16.0f, 17.7f, 4, Color.parseColor("#ffffff")));
        mStarList.add(new Star((float) UIUtil.dip2px(getContext(), 519.3333f), (float) UIUtil.dip2px(getContext
                (), 1.3333334f), centerX, centerY, 0.018f, 29.0f, 30.5f, 4, Color.parseColor("#82ece1")));
        mStarList.add(new Star((float) UIUtil.dip2px(getContext(), 545.0f), (float) UIUtil.dip2px(getContext(),
                2.6666667f), centerX, centerY, 0.036f, 5.0f, 50.0f, 2, Color.parseColor("#ffffff")));
        mStarList.add(new Star((float) UIUtil.dip2px(getContext(), 554.0f), (float) UIUtil.dip2px(getContext(),
                1.6666666f), centerX, centerY, 0.054f, 5.0f, 37.5f, 4, Color.parseColor("#bdf3ee")));
        mStarList.add(new Star((float) UIUtil.dip2px(getContext(), 593.0f), (float) UIUtil.dip2px(getContext(),
                2.8333333f), centerX, centerY, 0.018f, 33.0f, 24.6f, 4, Color.parseColor("#ffffff")));
        mStarList.add(new Star((float) UIUtil.dip2px(getContext(), 606.6667f), (float) UIUtil.dip2px(getContext
                (), 1.6666666f), centerX, centerY, 0.054f, 11.0f, 24.5f, 4, Color.parseColor("#82ece1")));
        mStarList.add(new Star((float) UIUtil.dip2px(getContext(), 661.0f), (float) UIUtil.dip2px(getContext(),
                1.6666666f), centerX, centerY, 0.036f, 43.0f, 23.0f, 2, Color.parseColor("#c8fff9")));
        mStarList.add(new Star((float) UIUtil.dip2px(getContext(), 685.6667f), (float) UIUtil.dip2px(getContext
                (), 1.8333334f), centerX, centerY, 0.036f, 6.0f, 37.0f, 4, Color.parseColor("#c8fff9")));
        mStarList.add(new Star((float) UIUtil.dip2px(getContext(), 707.0f), (float) UIUtil.dip2px(getContext(),
                1.0f), centerX, centerY, 0.018f, 21.0f, 26.0f, 4, Color.parseColor("#6a9b95")));
        mStarList.add(new Star((float) UIUtil.dip2px(getContext(), 746.6667f), (float) UIUtil.dip2px(getContext
                (), 1.0f), centerX, centerY, 0.018f, 14.0f, 24.5f, 4, Color.parseColor("#6a9b95")));
        mStarList.add(new Star((float) UIUtil.dip2px(getContext(), 757.3333f), (float) UIUtil.dip2px(getContext
                (), 2.0f), centerX, centerY, 0.036f, 4.0f, 26.3f, 2, Color.parseColor("#c8fff9")));
        mStarList.add(new Star((float) UIUtil.dip2px(getContext(), 793.0f), (float) UIUtil.dip2px(getContext(),
                2.0f), centerX, centerY, 0.018f, 18.0f, 26.0f, 4, Color.parseColor("#82ece1")));
    }

    public void draw(Canvas canvas) {
        super.draw(canvas);
        if (mAnimate) {
            int t = getSpentTime();
            updateOffset(t, canvas.getHeight());
            int i;
            if (isDay()) {
                updateScale(t);
                for (i = 0; i < mSunList.size(); i++) {
                    if (!(mSunList.get(i) instanceof Shine) || mDeltaAngleY <= -110.0f) {
                        mSunList.get(i).updateRoatationInfo(0.0f, mDeltaAngleY, mDeltaAngleZ, offsetX1, offsetY1,
                                scale);
                        mSunList.get(i).draw(canvas);
                    }
                }
            } else {
                for (i = 0; i < mStarList.size(); i++) {
                     mStarList.get(i).updateRoatationInfo(0.0f, mDeltaAngleY, mDeltaAngleZ, offsetX1, offsetY1);
                     mStarList.get(i).draw(canvas);
                }
            }
            invalidate();
        }
    }

    private int getSpentTime() {
        long curTime = System.currentTimeMillis();
        int t = oldTime == 0 ? 0 : (int) (curTime - oldTime);
        oldTime = curTime;
        if (t > 30) {
            return 30;
        }
        return t;
    }

    private void updateOffset(int t, int height) {
        if (mDeltaAngleZ > 90.0f) {
            mDeltaAngleZ = 90.0f;
        }
        if (mDeltaAngleZ < -90.0f) {
            mDeltaAngleZ = -90.0f;
        }
        offsetX = rangeX * (mDeltaAngleZ / 90.0f);
        offsetY = rangeY * (mDeltaAngleY / 180.0f);
        if (!isInit) {
            isInit = true;
            offsetY = ((float) ((-height) * 2)) / 3.0f;
            offsetX1 = offsetX;
            offsetY1 = offsetY;
        }
        float disX = Math.abs(offsetX1 - offsetX);
        float disY = Math.abs(offsetY1 - offsetY);
        if (moveSpeedX < 1.0f) {
            moveSpeedX += acceleration * ((float) t);
        } else {
            moveSpeedX -= acceleration * ((float) t);
        }
        if (moveSpeedX < 0.0f) {
            moveSpeedX = acceleration;
        }
        if (disX < 133.33333f * mDesity) {
            moveSpeedX = Math.min(moveSpeedX, (1.0f * disX) / 400.0f);
        }
        float moveOffsetX = ((moveSpeedX * ((float) t)) / 3.0f) * mDesity;
        if (disX > moveOffsetX) {
            if (offsetX1 > offsetX) {
                offsetX1 -= moveOffsetX;
            } else {
                offsetX1 += moveOffsetX;
            }
        }
        if (moveSpeedY < 1.0f) {
            moveSpeedY += acceleration * ((float) t);
        } else {
            moveSpeedY -= acceleration * ((float) t);
        }
        if (moveSpeedY < 0.0f) {
            moveSpeedY = acceleration;
        }
        if (disY < 133.33333f * mDesity) {
            moveSpeedY = Math.min(moveSpeedY, (1.0f * disY) / 400.0f);
        }
        float moveOffsetY = ((moveSpeedY * ((float) t)) / 3.0f) * mDesity;
        if (disY <= moveOffsetY) {
            return;
        }
        if (offsetY1 > offsetY) {
            offsetY1 -= moveOffsetY;
        } else {
            offsetY1 += moveOffsetY;
        }
    }

    private void updateScale(int t) {
        if (mDeltaAngleY < -30.0f && mDeltaAngleY > -90.0f) {
            sy = (-(mDeltaAngleY - 30.0f)) / 120.0f;
        }
        float dis = Math.abs(scale - sy);
        if (scaleSpeed < dis / 200.0f) {
            scaleSpeed += scaleAcceleration * ((float) t);
        } else {
            scaleSpeed -= scaleAcceleration * ((float) t);
        }
        if (scaleSpeed < 0.0f) {
            scaleSpeed = scaleAcceleration;
        }
        float scaleOffset = ((scaleSpeed * ((float) t)) / 3.0f) * mDesity;
        if (dis <= scaleOffset) {
            return;
        }
        if (scale > sy) {
            scale -= scaleOffset;
        } else {
            scale += scaleOffset;
        }
    }

    public void startAnimate() {
        mAnimate = true;
        invalidate();
    }

    public void stopAnimate() {
        mAnimate = false;
    }

    public void onPageSelected(boolean isCurrent) {
    }

    protected void onCreateOrientationInfoListener() {
        mListener = new OrientationSensorUtil.OrientationInfoListener() {
            public void onOrientationInfoChange(float x, float y, float z) {
                mDeltaAngleZ = z;
                if (y < 0.0f) {
                    mDeltaAngleY = -y;
                } else {
                    mDeltaAngleY = y;
                }
                mDeltaAngleY = mDeltaAngleY - 0.024902344f;
            }
        };
    }
}
