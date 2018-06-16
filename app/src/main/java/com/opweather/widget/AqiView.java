package com.opweather.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.drawable.AnimationDrawable;
import android.graphics.drawable.Drawable;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.opweather.R;

public class AqiView extends LinearLayout implements AqiBar.OnAqiLevelChangeListener {
    private static final boolean DBG = false;
    private static final String TAG = "AqiView";
    private final AqiBar mAqiBar;
    private String mAqiType;
    private final ImageView mIcon;
    private final TextView mTextLevel;
    private final TextView mTextLevelValue;
    private final TextView mTextPMLevel;

    public AqiView(Context context) {
        this(context, null);
    }

    public AqiView(Context context, AttributeSet attrs) {
        this(context, attrs, R.attr.aqiViewStyle);
    }

    public AqiView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.AqiView, defStyleAttr, 0);
        ((LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE)).inflate(a.getResourceId(0, R
                        .layout.aqi_view),
                this, true);
        int aqiValue = a.getInt(R.styleable.AqiView_aqiValue, 0);
        mAqiType = a.getString(R.styleable.AqiView_aqiType);
        a.recycle();
        mTextLevel = findViewById(R.id.level);
        mTextPMLevel = findViewById(R.id.pm_type);
        mTextLevelValue = findViewById(R.id.aqi_level_value);
        mIcon = findViewById(R.id.icon);
        Drawable d = mIcon.getBackground();
        if (d instanceof AnimationDrawable) {
            ((AnimationDrawable) d).start();
        }
        mAqiBar = findViewById(R.id.aqiBar);
        mAqiBar.setOnAqiLevelChangeListener(this);
        mAqiBar.setAqiValue(aqiValue);
    }

    public void setAqiValue(int value) {
        if (value < 0) {
            mAqiBar.setAqiValue(0, false);
            mTextPMLevel.setVisibility(View.GONE);
            mTextLevelValue.setVisibility(View.GONE);
            return;
        }
        mAqiBar.setAqiValue(value);
    }

    public void setAqiType(String type) {
        if (TextUtils.isEmpty(type)) {
            mAqiType = getResources().getString(R.string.aqi_level_na);
        } else {
            mAqiType = type;
        }
        mTextLevel.setText(mAqiType);
    }

    public int getAqiValue() {
        return mAqiBar.getAqiValue();
    }

    public void onLevelChanged(AqiBar.Level level, int value) {
        if (TextUtils.isEmpty(mAqiType)) {
            mAqiType = getResources().getString(R.string.aqi_level_na);
        }
        mTextLevel.setText(mAqiType);
        mTextLevelValue.setVisibility(View.VISIBLE);
        mTextPMLevel.setVisibility(View.VISIBLE);
        mTextLevelValue.setText(String.valueOf(value));
    }
}
