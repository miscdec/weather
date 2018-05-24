package com.opweather.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.RelativeLayout;

public class RefreshWeatherUnitView extends RelativeLayout {

    public OnRefreshUnitListener mOnRefreshUnitListener;

    public interface OnRefreshUnitListener {
        void refreshUnit();
    }

    public void setOnRefreshUnitListener(OnRefreshUnitListener onRefreshUnitListener) {
        mOnRefreshUnitListener = onRefreshUnitListener;
    }

    public void invalidate() {
        super.invalidate();
        if (mOnRefreshUnitListener != null) {
            mOnRefreshUnitListener.refreshUnit();
        }
    }

    public RefreshWeatherUnitView(Context context) {
        this(context, null);
    }

    public RefreshWeatherUnitView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public RefreshWeatherUnitView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

}
