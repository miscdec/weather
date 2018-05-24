package com.opweather.widget;

import android.content.Context;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ScrollView;

public class WeatherScrollView extends ScrollView{
    private View contentView;
    private Rect originalRect;
    ScrollViewListener mScrollViewListener;
    private float MOVE_FACTOR;
    private boolean canPullDown;
    private boolean canPullUp;
    private boolean isMoved;

    public interface ScrollViewListener{
        void onScrollChanged(WeatherScrollView weatherScrollView, int i, int i2, int i3, int i4);
    }

    public void setScrollViewListener(ScrollViewListener scrollViewListener){
        mScrollViewListener = scrollViewListener;
    }

    public WeatherScrollView(Context context) {
        super(context);

    }

    public WeatherScrollView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public WeatherScrollView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }
}
