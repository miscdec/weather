package com.opweather.gles20;

import android.content.Context;
import android.support.v4.provider.FontsContractCompat.FontRequestCallback;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;

import com.opweather.util.OrientationSensorUtil;
import com.opweather.widget.openglbase.RainSurfaceView;


public class HazeView extends BaseGL2SurfaceView implements OnTouchListener, OrientationSensorUtil
        .OrientationInfoListener {
    private static final String TAG = "HazeView";
    private HazeRenderer mHazeRenderer;

    class AnonymousClass_1 implements Runnable {
        final /* synthetic */ boolean val$day;

        AnonymousClass_1(boolean z) {
            this.val$day = z;
        }

        public void run() {
            HazeView.this.mHazeRenderer.setIsDay(this.val$day);
        }
    }

    class AnonymousClass_2 implements Runnable {
        final /* synthetic */ float val$alpha;

        AnonymousClass_2(float f) {
            this.val$alpha = f;
        }

        public void run() {
            HazeView.this.mHazeRenderer.setAlpha(this.val$alpha);
        }
    }

    class AnonymousClass_3 implements Runnable {
        final /* synthetic */ float val$localX;
        final /* synthetic */ float val$localY;
        final /* synthetic */ float val$localZ;

        AnonymousClass_3(float f, float f2, float f3) {
            this.val$localX = f;
            this.val$localY = f2;
            this.val$localZ = f3;
        }

        public void run() {
            HazeView.this.mHazeRenderer.setRotation(this.val$localX, this.val$localY, this.val$localZ);
        }
    }

    public HazeView(Context context) {
        this(context, true);
    }

    public HazeView(Context context, boolean isDay) {
        super(context);
        setEGLContextClientVersion(RainSurfaceView.RAIN_LEVEL_SHOWER);
        setEGLConfigChooser(ItemTouchHelper.RIGHT, 8, 8, 8, ItemTouchHelper.START, 0);
        setZOrderOnTop(true);
        this.mHazeRenderer = new HazeRenderer(context, isDay);
        setRenderer(this.mHazeRenderer);
        getHolder().setFormat(FontRequestCallback.FAIL_REASON_FONT_LOAD_ERROR);
        setOnTouchListener(this);
    }

    public void setDay(boolean day) {
        queueEvent(new AnonymousClass_1(day));
    }

    public void setAlpha(float alpha) {
        super.setAlpha(alpha);
        queueEvent(new AnonymousClass_2(alpha));
    }

    public boolean onTouch(View v, MotionEvent event) {
        return super.onTouchEvent(event);
    }

    public void onOrientationInfoChange(float x, float y, float z) {
        queueEvent(new AnonymousClass_3(x, y, z));
    }

    public void startAnimate() {
        setRenderMode(1);
    }

    public void stopAnimate() {
        setRenderMode(0);
    }

    public void onPageSelected(boolean isCurrent) {
    }

    protected void onCreateOrientationInfoListener() {
        this.mListener = this;
    }
}
