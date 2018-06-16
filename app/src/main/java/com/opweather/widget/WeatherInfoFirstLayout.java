package com.opweather.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.RelativeLayout;

import com.opweather.R;


public class WeatherInfoFirstLayout extends RelativeLayout {
    public WeatherInfoFirstLayout(Context context) {
        super(context);
    }

    public WeatherInfoFirstLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public WeatherInfoFirstLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    public void addView(View view) {
        if (view.getId() == R.id.opweather_info) {
            ((LayoutParams) view.getLayoutParams()).topMargin = -view.getHeight();
        }
        super.addView(view);
    }

    @Override
    public void onLayout(boolean changed, int l, int t, int r, int b) {
        super.onLayout(changed, l, t, r, b);
        View childView = findViewById(R.id.opweather_info);
        ((LayoutParams) childView.getLayoutParams()).topMargin = -childView.getHeight();
    }
}
