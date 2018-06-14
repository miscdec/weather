package com.opweather.widget;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.support.v4.app.NotificationCompat.MessagingStyle;
import android.support.v4.view.MotionEventCompat;
import android.support.v4.widget.AutoScrollHelper;
import android.util.AttributeSet;

import com.android.volley.DefaultRetryPolicy;
import com.opweather.R;
import com.opweather.constants.GlobalConfig;
import com.opweather.util.OrientationSensorUtil;
import com.opweather.util.UIUtil;
import com.opweather.widget.openglbase.RainSurfaceView;
import com.opweather.widget.shap.Cloud;
import com.opweather.widget.shap.FogParticle;
import com.opweather.widget.shap.Stars;

import java.util.ArrayList;

public class CloudyView extends BaseWeatherView {
    private static final int BACKGROUND_COLOR;
    private static final int BACKGROUND_NIGHT_COLOR;
    private boolean mAnimate;
    private ArrayList<Cloud> mCloudList;
    private float mDeltaAngleY;
    private float mDeltaAngleZ;
    private Stars mStars;

    static {
        BACKGROUND_COLOR = Color.parseColor("#4a97d2");
        BACKGROUND_NIGHT_COLOR = Color.parseColor("#051325");
    }

    public CloudyView(Context context, boolean isDay) {
        super(context, isDay);
        this.mCloudList = new ArrayList();
        this.mAnimate = false;
        this.mDeltaAngleZ = 0.0f;
        this.mDeltaAngleY = 0.0f;
        init();
        setDayBackgroundColor(BACKGROUND_COLOR);
        setNightBackgroundColor(BACKGROUND_NIGHT_COLOR);
    }

    public CloudyView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
        init();
    }

    public CloudyView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.mCloudList = new ArrayList();
        this.mAnimate = false;
        this.mDeltaAngleZ = 0.0f;
        this.mDeltaAngleY = 0.0f;
        init();
    }

    public void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }

    public void draw(Canvas canvas) {
        super.draw(canvas);
        if (this.mAnimate) {
            for (int i = 0; i < this.mCloudList.size(); i++) {
                Cloud cloud = (Cloud) this.mCloudList.get(i);
                cloud.setHeight(getHeight());
                cloud.setWidth(getWidth());
                cloud.setDay(isDay());
                cloud.updateRoatationInfo(AutoScrollHelper.RELATIVE_UNSPECIFIED, this.mDeltaAngleY, this.mDeltaAngleZ);
                cloud.draw(canvas);
            }
            if (!isDay()) {
                if (this.mStars == null) {
                    this.mStars = new Stars();
                    this.mStars.init(getContext(), getWidth(), (getHeight() * 2) / 3);
                }
                this.mStars.next();
                this.mStars.draw(canvas);
            }
            invalidate();
        }
    }

    public void startAnimate() {
        this.mAnimate = true;
        invalidate();
    }

    public void stopAnimate() {
        this.mAnimate = false;
    }

    public void onPageSelected(boolean isCurrent) {
    }

    private void init() {
        float density = getContext().getResources().getDisplayMetrics().density;
        this.mCloudList.add(new Cloud(UIUtil.dip2px(getContext(), 333.0f), UIUtil.dip2px(getContext(), 73.0f), UIUtil
                .dip2px(getContext(), 602.0f), UIUtil.dip2px(getContext(), 91.0f), Color.rgb(MotionEventCompat
                .ACTION_MASK, MotionEventCompat.ACTION_MASK, MotionEventCompat.ACTION_MASK)).setAlpha(R.styleable
                .AppCompatTheme_textAppearanceSmallPopupMenu).setAnimation(true).setRadiusScale(1.1f).setStep(120)
                .setLevel(RainSurfaceView.RAIN_LEVEL_RAINSTORM).setMaxY((float) UIUtil.dip2px(getContext(), 400.0f))
                .setHeightRadio(0.8f).setNightColor(Color.rgb(MessagingStyle.MAXIMUM_RETAINED_MESSAGES, R.styleable
                        .AppCompatTheme_colorAccent, R.styleable.AppCompatTheme_listPreferredItemPaddingRight))
                .setDensity(density).setNightAlpha(R.styleable.AppCompatTheme_textAppearanceSmallPopupMenu));
        this.mCloudList.add(new Cloud(UIUtil.dip2px(getContext(), 184.0f), -UIUtil.dip2px(getContext(), 16.0f),
                UIUtil.dip2px(getContext(), 600.0f), UIUtil.dip2px(getContext(), 158.0f), Color.rgb(MotionEventCompat
                .ACTION_MASK, MotionEventCompat.ACTION_MASK, MotionEventCompat.ACTION_MASK)).setAlpha(R.styleable
                .AppCompatTheme_textAppearanceSmallPopupMenu).setAnimation(true).setMaxY((float) UIUtil.dip2px
                (getContext(), 400.0f)).setRadiusScale(1.1f).setStep(120).setLevel(RainSurfaceView
                .RAIN_LEVEL_RAINSTORM).setHeightRadio(0.8f).setNightColor(Color.rgb(40, 74, R.styleable
                .AppCompatTheme_windowNoTitle)).setDensity(density).setNightAlpha(R.styleable
                .AppCompatTheme_textAppearanceSmallPopupMenu));
        this.mCloudList.add(new Cloud(UIUtil.dip2px(getContext(), 250.0f), UIUtil.dip2px(getContext(), 93.0f), UIUtil
                .dip2px(getContext(), 634.0f), UIUtil.dip2px(getContext(), 50.0f), Color.rgb(MotionEventCompat
                .ACTION_MASK, 210, 0)).setAlpha(204).setAnimation(true).setMaxY((float) UIUtil.dip2px(getContext(),
                400.0f)).setRadiusScale(DefaultRetryPolicy.DEFAULT_BACKOFF_MULT).setStep(R.styleable
                .AppCompatTheme_dialogTheme).setLevel(RainSurfaceView.RAIN_LEVEL_RAINSTORM).setHeightRadio
                (AutoScrollHelper.RELATIVE_UNSPECIFIED).setNightColor(Color.rgb(251, MotionEventCompat.ACTION_MASK,
                192)).setDensity(density).setNightAlpha(MotionEventCompat.ACTION_MASK));
        this.mCloudList.add(new Cloud(UIUtil.dip2px(getContext(), 333.0f), -UIUtil.dip2px(getContext(), 200.0f),
                UIUtil.dip2px(getContext(), 600.0f), UIUtil.dip2px(getContext(), 133.0f), Color.rgb(MotionEventCompat
                .ACTION_MASK, MotionEventCompat.ACTION_MASK, MotionEventCompat.ACTION_MASK)).setAlpha(R.styleable
                .AppCompatTheme_textAppearanceSmallPopupMenu).setAnimation(true).setMaxY((float) UIUtil.dip2px
                (getContext(), 366.0f)).setRadiusScale(1.1f).setStep(120).setLevel(6).setHeightRadio(0.8f).setNightColor(Color.rgb(R.styleable
                .AppCompatTheme_colorAccent, R.styleable.AppCompatTheme_ratingBarStyleSmall, 130)).setDensity
                (density).setNightAlpha(R.styleable.AppCompatTheme_textAppearanceSmallPopupMenu));
        this.mCloudList.add(new Cloud(UIUtil.dip2px(getContext(), 300.0f), -UIUtil.dip2px(getContext(), 116.0f),
                UIUtil.dip2px(getContext(), 400.0f), UIUtil.dip2px(getContext(), 200.0f), Color.rgb(MotionEventCompat
                .ACTION_MASK, MotionEventCompat.ACTION_MASK, MotionEventCompat.ACTION_MASK)).setAlpha(R.styleable
                .AppCompatTheme_textAppearanceSmallPopupMenu).setAnimation(true).setMaxY((float) UIUtil.dip2px
                (getContext(), 366.0f)).setRadiusScale(1.1f).setStep(120).setLevel(RainSurfaceView
                .RAIN_LEVEL_RAINSTORM).setHeightRadio(0.8f).setNightColor(Color.rgb(40, R.styleable.AppCompatTheme_listPreferredItemHeight, R.styleable
                .AppCompatTheme_windowNoTitle)).setDensity(density).setNightAlpha(178));
        this.mCloudList.add(new Cloud(UIUtil.dip2px(getContext(), 96.0f), -UIUtil.dip2px(getContext(), 86.0f), UIUtil
                .dip2px(getContext(), 598.0f), UIUtil.dip2px(getContext(), 200.0f), Color.rgb(MotionEventCompat
                .ACTION_MASK, MotionEventCompat.ACTION_MASK, MotionEventCompat.ACTION_MASK)).setAlpha(153)
                .setAnimation(true).setMaxY((float) UIUtil.dip2px(getContext(), 333.0f)).setRadiusScale(1.1f).setStep
                        (GlobalConfig.MESSAGE_ACCU_GET_LOCATION_SUCC).setLevel(RainSurfaceView.RAIN_LEVEL_RAINSTORM)
                .setHeightRadio(0.8f).setNightColor(Color.rgb(39, R
                        .styleable.AppCompatTheme_listDividerAlertDialog, R.styleable
                        .AppCompatTheme_tooltipForegroundColor)).setDensity(density).setNightAlpha(178));
        this.mCloudList.add(new Cloud(UIUtil.dip2px(getContext(), (float) (-UIUtil.dip2px(getContext(), 11.0f))),
                -UIUtil.dip2px(getContext(), 110.0f), UIUtil.dip2px(getContext(), 300.0f), UIUtil.dip2px(getContext()
                , 200.0f), Color.rgb(MotionEventCompat.ACTION_MASK, MotionEventCompat.ACTION_MASK, MotionEventCompat
                .ACTION_MASK)).setAlpha(R.styleable.AppCompatTheme_textAppearanceSmallPopupMenu).setAnimation(true)
                .setMaxY((float) UIUtil.dip2px(getContext(), 333.0f)).setRadiusScale(1.1f).setStep(140).setLevel
                        (RainSurfaceView.RAIN_LEVEL_THUNDERSHOWER).setHeightRadio(0.8f).setNightColor(Color.rgb
                        (WeatherCircleView.ARC_DIN, 35, R.styleable
                                .AppCompatTheme_editTextBackground)).setDensity(density).setNightAlpha(R.styleable
                        .AppCompatTheme_textAppearanceSmallPopupMenu));
        this.mCloudList.add(new Cloud(UIUtil.dip2px(getContext(), 297.0f), -UIUtil.dip2px(getContext(), 162.0f),
                UIUtil.dip2px(getContext(), 200.0f), UIUtil.dip2px(getContext(), 216.0f), Color.rgb(MotionEventCompat
                .ACTION_MASK, MotionEventCompat.ACTION_MASK, MotionEventCompat.ACTION_MASK)).setAlpha(R.styleable
                .AppCompatTheme_textAppearanceSmallPopupMenu).setAnimation(true).setMaxY((float) UIUtil.dip2px
                (getContext(), 366.0f)).setRadiusScale(1.1f).setStep(WeatherCircleView.START_ANGEL_180).setLevel
                (6).setHeightRadio(0.25f).setNightColor(Color.rgb(R.styleable
                .AppCompatTheme_colorSwitchThumbNormal, R.styleable.AppCompatTheme_textColorAlertDialogListItem, 142)
        ).setDensity(density).setNightAlpha(153));
        this.mCloudList.add(new Cloud(UIUtil.dip2px(getContext(), 10.0f), -UIUtil.dip2px(getContext(), 230.0f),
                UIUtil.dip2px(getContext(), 100.0f), UIUtil.dip2px(getContext(), 216.0f), Color.rgb(MotionEventCompat
                .ACTION_MASK, MotionEventCompat.ACTION_MASK, MotionEventCompat.ACTION_MASK)).setAlpha(153)
                .setAnimation(true).setMaxY((float) UIUtil.dip2px(getContext(), 366.0f)).setRadiusScale(1.1f).setStep
                        (160).setLevel(RainSurfaceView.RAIN_LEVEL_THUNDERSHOWER).setHeightRadio(FogParticle
                        .SPEED_UNIT).setNightColor(Color.rgb(R.styleable.AppCompatTheme_imageButtonStyle, 122, 156))
                .setDensity(density).setNightAlpha(204));
    }

    protected void onCreateOrientationInfoListener() {
        this.mListener = new OrientationSensorUtil.OrientationInfoListener() {
            public void onOrientationInfoChange(float x, float y, float z) {
                CloudyView.this.mDeltaAngleZ = z;
                if (y < 0.0f) {
                    CloudyView.this.mDeltaAngleY = -y;
                } else {
                    CloudyView.this.mDeltaAngleY = y;
                }
            }
        };
    }
}
