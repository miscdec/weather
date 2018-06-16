package com.opweather.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.opweather.R;

public class WeatherSingleInfoView extends LinearLayout {
    private ImageView mInfoIcon;
    private TextView mInfoLevel;
    private TextView mInfoType;
    private View mView;

    public WeatherSingleInfoView(Context context) {
        this(context, null);
    }

    public WeatherSingleInfoView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public WeatherSingleInfoView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        TypedArray t = getContext().obtainStyledAttributes(attrs, R.styleable.WeatherSingleInfoView);
        String weatherLevel = t.getString(R.styleable.WeatherSingleInfoView_weatherLevel);
        String weatherType = t.getString(R.styleable.WeatherSingleInfoView_weatherType);
        Drawable weathIcon = t.getDrawable(R.styleable.WeatherSingleInfoView_weatherIcon);
        mView = LayoutInflater.from(context).inflate(R.layout.weather_single_info_view, this);
        mInfoIcon = mView.findViewById(R.id.single_info_icon);
        mInfoType = mView.findViewById(R.id.single_info_type);
        mInfoLevel = mView.findViewById(R.id.single_info_level);
        mInfoIcon.setBackground(weathIcon);
        mInfoType.setText(weatherType);
        mInfoLevel.setText(weatherLevel);
    }

    public WeatherSingleInfoView setInfoIcon(int id) {
        mInfoIcon.setBackgroundResource(id);
        return this;
    }

    public WeatherSingleInfoView setInfoType(int id) {
        mInfoType.setText(id);
        return this;
    }

    public WeatherSingleInfoView setInfoType(String text) {
        mInfoType.setText(text);
        return this;
    }

    public WeatherSingleInfoView setInfoLevel(int id) {
        mInfoLevel.setText(id);
        return this;
    }

    public WeatherSingleInfoView setInfoLevel(String level) {
        mInfoLevel.setText(level);
        return this;
    }
}
