package com.opweather.util;

import android.app.Activity;
import android.content.Context;
import android.graphics.Point;
import android.graphics.PointF;
import android.os.Build.VERSION;
import android.util.DisplayMetrics;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.Window;
import android.view.WindowManager;
import android.view.WindowManager.LayoutParams;
import android.widget.ListView;

import com.opweather.constants.WeatherType;
import com.opweather.provider.CitySearchProvider;
import com.opweather.util.UIUtil.OnDragListener.DragEvent;

public class UIUtil {

    public static class DragOnTouchListener implements OnTouchListener {
        private int currentSlop;
        private boolean dispatchTouchEvent;
        private boolean enable;
        private PointF mLastPoint;
        private boolean mScrolling;
        private int mTouchSlop;
        private OnDragListener onDragListener;
        private View view;

        public DragOnTouchListener(View view, OnDragListener onDragListener, boolean dispatchTouchEvent) {
            this.mTouchSlop = 5;
            this.mScrolling = false;
            this.mLastPoint = null;
            this.currentSlop = 0;
            this.enable = false;
            this.onDragListener = onDragListener;
            this.view = view;
            this.dispatchTouchEvent = dispatchTouchEvent;
        }

        public void setDispatchTouchEvent(boolean dispatchTouchEvent) {
            this.dispatchTouchEvent = dispatchTouchEvent;
        }

        public void setEnable(boolean enable) {
            this.enable = enable;
        }

        public boolean onTouch(View v, final MotionEvent ev) {
            if (!enable) {
                return false;
            }
            float eventFloatY = ev.getY();
            if (ev.getAction() == MotionEvent.ACTION_DOWN) {
                mLastPoint = new PointF(ev.getX(), ev.getY());
            }
            if (ev.getAction() == MotionEvent.ACTION_MOVE) {
                if (mLastPoint == null) {
                    mLastPoint = new PointF(ev.getX(), ev.getY());
                }
                if (mScrolling) {
                    view.post(new Runnable() {
                        @Override
                        public void run() {
                            onDragListener.onDrag(view, ev, DragEvent.ACTION_DRAG, currentSlop);

                        }
                    });
                } else {
                    float slop = eventFloatY - mLastPoint.y;
                    currentSlop = (int) slop;
                    if (Math.abs(slop) >= ((float) mTouchSlop) && Math.abs((int) (mLastPoint.x - ev.getX())) < Math
                            .abs((int) (mLastPoint.y - ev.getY()))) {
                        mScrolling = true;
                        onDragListener.onDrag(view, ev, DragEvent.ACTION_START, currentSlop);
                    }
                }
            } else if (ev.getAction() == MotionEvent.ACTION_UP || ev.getAction() == MotionEvent.ACTION_CANCEL) {
                if (mScrolling) {
                    mScrolling = false;
                    onDragListener.onDrag(view, ev, DragEvent.ACTION_END, currentSlop);
                    return true;
                }
                mLastPoint = null;
            }
            return !dispatchTouchEvent;
        }
    }

    public static class DragOnTouchListenerHolder {
        public DragOnTouchListener dragOnTouchListener;
    }

    public interface OnDragListener {

        enum DragEvent {
            ACTION_START,
            ACTION_DRAG,
            ACTION_END
        }

        void onDrag(View view, MotionEvent motionEvent, DragEvent dragEvent, int i);
    }

    public static Point getPos(View view) {
        int[] pos = new int[2];
        view.getLocationOnScreen(pos);
        return new Point(pos[0], pos[1]);
    }

    public static int dip2px(Context context, float dipValue) {
        return (int) ((dipValue * context.getResources().getDisplayMetrics().density) + 0.5f);
    }

    public static int px2dip(Context context, float pxValue) {
        return (int) ((pxValue / context.getResources().getDisplayMetrics().density) + 0.5f);
    }

    public static int getWindowWidth(Context context) {
        WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        DisplayMetrics metric = new DisplayMetrics();
        wm.getDefaultDisplay().getRealMetrics(metric);
        return metric.widthPixels;
    }

    public static int getWindowHeight(Context context) {
        WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        DisplayMetrics metric = new DisplayMetrics();
        wm.getDefaultDisplay().getMetrics(metric);
        return metric.heightPixels;
    }

    public static int getScreenHeight(Context context) {
        WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        DisplayMetrics metric = new DisplayMetrics();
        wm.getDefaultDisplay().getRealMetrics(metric);
        return metric.heightPixels;
    }

    public static int getScreenWidth(Context context) {
        WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        DisplayMetrics metric = new DisplayMetrics();
        wm.getDefaultDisplay().getRealMetrics(metric);
        return metric.widthPixels;
    }

    public static boolean isLandscape(Context context) {
        return context.getResources().getConfiguration().orientation == 2;
    }

    public static DragOnTouchListenerHolder setOnDragListener(View view, OnDragListener onDragListener, boolean
            dispatchTouchEvent, boolean enable) {
        DragOnTouchListenerHolder dragOnTouchListenerHolder = new DragOnTouchListenerHolder();
        DragOnTouchListener d = new DragOnTouchListener(view, onDragListener, dispatchTouchEvent);
        d.setEnable(enable);
        view.setOnTouchListener(d);
        dragOnTouchListenerHolder.dragOnTouchListener = d;
        if (!(view instanceof ListView)) {
            view.setOnClickListener(null);
        }
        return dragOnTouchListenerHolder;
    }

    public static View getViewByPosition(int position, ListView listView) {
        int firstListItemPosition = listView.getFirstVisiblePosition();
        return (position < firstListItemPosition || position > (listView.getChildCount() + firstListItemPosition) -
                1) ? listView.getAdapter().getView(position, listView.getChildAt(position), listView) : listView
                .getChildAt(position - firstListItemPosition);
    }

    public static void setWindowStyle(Activity activity) {
        Window window = activity.getWindow();
        if (VERSION.SDK_INT >= 21) {
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS
                    | WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
            window.getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                    | View.SYSTEM_UI_FLAG_LAYOUT_STABLE);
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(0);
            return;
        }
        activity.requestWindowFeature(Window.FEATURE_NO_TITLE);
        window.setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
    }

    public static void setSystemBar(Activity activity, int color) {
        if (VERSION.SDK_INT >= 19) {
            Window win = activity.getWindow();
            LayoutParams winParams = win.getAttributes();
            winParams.flags |= 67108864;
            win.setAttributes(winParams);
            SystemBarTintManager tintManager = new SystemBarTintManager(activity);
            tintManager.setStatusBarTintEnabled(true);
            tintManager.setStatusBarTintResource(color);
        }
    }
}
