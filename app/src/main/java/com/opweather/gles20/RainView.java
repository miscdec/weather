package com.opweather.gles20;

import android.annotation.SuppressLint;
import android.content.Context;
import android.support.v4.provider.FontsContractCompat.FontRequestCallback;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import com.opweather.util.OrientationSensorUtil;
import com.opweather.widget.openglbase.RainSurfaceView;


@SuppressLint({"ClickableViewAccessibility"})
public class RainView extends BaseGL2SurfaceView implements OnTouchListener, OrientationSensorUtil.OrientationInfoListener {
    private static final String TAG = "RainView";
    private RainRenderer rainRenderer;

    class AnonymousClass_1 implements Runnable {
        final /* synthetic */ boolean val$day;

        AnonymousClass_1(boolean z) {
            this.val$day = z;
        }

        public void run() {
            RainView.this.rainRenderer.setIsDay(this.val$day);
        }
    }

    class AnonymousClass_2 implements Runnable {
        final /* synthetic */ float val$alpha;

        AnonymousClass_2(float f) {
            this.val$alpha = f;
        }

        public void run() {
            RainView.this.rainRenderer.setAlpha(this.val$alpha);
        }
    }

    class AnonymousClass_3 implements Runnable {
        final /* synthetic */ float val$x;
        final /* synthetic */ float val$y;

        AnonymousClass_3(float f, float f2) {
            this.val$x = f;
            this.val$y = f2;
        }

        public void run() {
            RainView.this.rainRenderer.handleTouch(this.val$x, this.val$y);
        }
    }

    class AnonymousClass_4 implements Runnable {
        final /* synthetic */ float val$localX;
        final /* synthetic */ float val$localY;
        final /* synthetic */ float val$localZ;

        AnonymousClass_4(float f, float f2, float f3) {
            this.val$localX = f;
            this.val$localY = f2;
            this.val$localZ = f3;
        }

        public void run() {
            RainView.this.rainRenderer.setRotation(this.val$localX, this.val$localY, this.val$localZ);
        }
    }

    public RainView(Context context) {
        this(context, true, 1);
    }

    public RainView(Context context, boolean isDay, int rainLevel) {
        super(context);
        setEGLContextClientVersion(RainSurfaceView.RAIN_LEVEL_SHOWER);
        setEGLConfigChooser(ItemTouchHelper.RIGHT, 8, 8, 8, ItemTouchHelper.START, 0);
        setZOrderOnTop(true);
        this.rainRenderer = new RainRenderer(context, isDay, rainLevel);
        setRenderer(this.rainRenderer);
        getHolder().setFormat(FontRequestCallback.FAIL_REASON_FONT_LOAD_ERROR);
        setOnTouchListener(this);
    }

    public void startAnimate() {
        setRenderMode(1);
    }

    public void stopAnimate() {
        setRenderMode(0);
    }

    public void onPageSelected(boolean isCurrent) {
    }

    public void setDay(boolean day) {
        queueEvent(new AnonymousClass_1(day));
    }

    public void setAlpha(float alpha) {
        super.setAlpha(alpha);
        queueEvent(new AnonymousClass_2(alpha));
    }

    public boolean onTouch(View v, MotionEvent event) {
        if (event != null) {
            queueEvent(new AnonymousClass_3(event.getX(), event.getY()));
        }
        return false;
    }

    public void onOrientationInfoChange(float x, float y, float z) {
        queueEvent(new AnonymousClass_4(x, y, z));
    }

    protected void onCreateOrientationInfoListener() {
        this.mListener = this;
    }
}
