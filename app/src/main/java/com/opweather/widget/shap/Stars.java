package com.opweather.widget.shap;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.support.v4.view.MotionEventCompat;
import android.support.v4.widget.AutoScrollHelper;

import com.android.volley.DefaultRetryPolicy;
import com.opweather.util.UIUtil;
import com.opweather.widget.anim.StartAnimation;

public class Stars implements IViewShap {
    public static final int CIRCLE_COUNT = 50;
    private Circle[] mCircles;
    private int mWHeight;
    private int mWWidth;

    class Circle {
        public int CURRNET;
        public Paint P;
        public float R;
        public int STEP;
        public float X;
        public float Y;

        Circle() {
            STEP = 120;
        }

        public void next() {
            CURRNET = (CURRNET + 1) % STEP;
            if (CURRNET <= 60) {
                P.setAlpha((int) ((((float) CURRNET) / 60.0f) * 100.0f));
            } else {
                P.setAlpha((int) ((((float) (120 - CURRNET)) / 60.0f) * 100.0f));
            }
        }
    }

    public Stars() {
        mWHeight = 1920;
        mWWidth = 1080;
    }

    @Override
    public void draw(Canvas canvas) {
        for (int i = 0; i < mCircles.length; i++) {
            canvas.drawCircle(mCircles[i].X, mCircles[i].Y, mCircles[i].R, mCircles[i].P);
        }
    }

    public void setWindowSize(int w, int h) {
        mWWidth = w;
        mWHeight = h;
    }

    public void init(Context context, int w, int h) {
        setWindowSize(w, h);
        StartAnimation.setRange((float) mWWidth, (float) mWHeight, AutoScrollHelper.RELATIVE_UNSPECIFIED);
        mCircles = new Circle[50];
        for (int i = 0; i < mCircles.length; i++) {
            Paint paint = new Paint();
            paint.setColor(Color.rgb(MotionEventCompat.ACTION_MASK, MotionEventCompat.ACTION_MASK, MotionEventCompat.ACTION_MASK));
            mCircles[i] = new Circle();
            float[] p = StartAnimation.orginXY();
            mCircles[i].X = p[0];
            mCircles[i].Y = p[1];
            mCircles[i].R = (float) UIUtil.dip2px(context, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
            mCircles[i].P = paint;
            mCircles[i].CURRNET = (int) p[2];
        }
    }

    @Override
    public void next() {
        for (int i = 0; i < mCircles.length; i++) {
            mCircles[i].next();
        }
    }
}
