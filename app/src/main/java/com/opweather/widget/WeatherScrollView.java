package com.opweather.widget;

import android.content.Context;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.TranslateAnimation;
import android.widget.ScrollView;

public class WeatherScrollView extends ScrollView {
    private static final int ANIM_TIME = 200;
    private float MOVE_FACTOR = 0.3f;
    private boolean canPullDown = false;
    private boolean canPullUp = false;
    private View contentView;
    private boolean isMoved = false;
    ScrollViewListener mScrollViewListener;
    private Rect originalRect = new Rect();
    private float startY;

    public interface ScrollViewListener {
        void onScrollChanged(WeatherScrollView weatherScrollView, int i, int i2, int i3, int i4);
    }

    public WeatherScrollView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public WeatherScrollView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public WeatherScrollView(Context context) {
        super(context);
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        if (getChildCount() > 0) {
            contentView = getChildAt(0);
        }
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        super.onLayout(changed, l, t, r, b);
        if (contentView != null) {
            originalRect.set(contentView.getLeft(), contentView.getTop(), contentView.getRight(), contentView
                    .getBottom());
        }
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        boolean shouldMove = false;
        if (contentView == null) {
            return super.dispatchTouchEvent(ev);
        }
        switch (ev.getAction()) {
            case MotionEvent.ACTION_DOWN:
                canPullDown = isCanPullDown();
                canPullUp = isCanPullUp();
                startY = ev.getY();
                break;
            case MotionEvent.ACTION_UP:
                if (isMoved) {
                    TranslateAnimation anim = new TranslateAnimation(0.0f, 0.0f, (float) contentView.getTop(),
                            (float) originalRect.top);
                    anim.setDuration(200);
                    contentView.startAnimation(anim);
                    contentView.layout(originalRect.left, originalRect.top, originalRect.right, originalRect.bottom);
                    canPullDown = false;
                    canPullUp = false;
                    isMoved = false;
                    break;
                }
                break;
            case MotionEvent.ACTION_MOVE:
                if (!canPullDown && !canPullUp) {
                    startY = ev.getY();
                    canPullDown = isCanPullDown();
                    canPullUp = isCanPullUp();
                    break;
                }
                int deltaY = (int) (ev.getY() - startY);
                if ((canPullDown && deltaY > 0) || ((canPullUp && deltaY < 0) || (canPullUp && canPullDown))) {
                    shouldMove = true;
                }
                if (shouldMove) {
                    int offset = (int) (((float) deltaY) * MOVE_FACTOR);
                    contentView.layout(originalRect.left, originalRect.top + offset, originalRect.right, originalRect
                            .bottom + offset);
                    isMoved = true;
                    break;
                }
                break;
        }
        return super.dispatchTouchEvent(ev);
    }

    private boolean isCanPullDown() {
        return getScrollY() == 0 || contentView.getHeight() < getHeight() + getScrollY();
    }

    private boolean isCanPullUp() {
        return contentView.getHeight() <= getHeight() + getScrollY();
    }

    public void fling(int velocityY) {
        super.fling((velocityY * 3) / 4);
    }

    @Override
    protected void onScrollChanged(int x, int y, int oldx, int oldy) {
        super.onScrollChanged(x, y, oldx, oldy);
        if (mScrollViewListener != null) {
            mScrollViewListener.onScrollChanged(this, x, y, oldx, oldy);
        }
    }

    public void setScrollViewListener(ScrollViewListener scrollViewListener) {
        mScrollViewListener = scrollViewListener;
    }
}
