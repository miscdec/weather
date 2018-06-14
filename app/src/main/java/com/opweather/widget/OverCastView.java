package com.opweather.widget;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.support.v4.view.MotionEventCompat;
import android.support.v4.widget.AutoScrollHelper;
import android.util.AttributeSet;

import com.opweather.R;
import com.opweather.constants.GlobalConfig;
import com.opweather.util.OrientationSensorUtil;
import com.opweather.util.UIUtil;
import com.opweather.widget.openglbase.RainSurfaceView;
import com.opweather.widget.shap.Cloud;
import com.opweather.widget.shap.FogParticle;

import java.util.ArrayList;

public class OverCastView extends BaseWeatherView {
    private static final int BACKGROUND_COLOR;
    private static final int BACKGROUND_NIGHT_COLOR;
    private boolean mAnimate;
    private ArrayList<Cloud> mCloudList;
    private float mDeltaAngleY;
    private float mDeltaAngleZ;

    static {
        BACKGROUND_COLOR = Color.parseColor("#93a4ae");
        BACKGROUND_NIGHT_COLOR = Color.parseColor("#171e26");
    }

    public OverCastView(Context context, boolean isDay) {
        super(context, isDay);
        this.mCloudList = new ArrayList();
        this.mAnimate = false;
        this.mDeltaAngleZ = 0.0f;
        this.mDeltaAngleY = 0.0f;
        init();
        setDayBackgroundColor(BACKGROUND_COLOR);
        setNightBackgroundColor(BACKGROUND_NIGHT_COLOR);
    }

    public OverCastView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public OverCastView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.mCloudList = new ArrayList();
        this.mAnimate = false;
        this.mDeltaAngleZ = 0.0f;
        this.mDeltaAngleY = 0.0f;
        init();
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
                .dip2px(getContext(), 602.0f), UIUtil.dip2px(getContext(), 91.0f), Color.rgb(R.styleable
                .AppCompatTheme_toolbarNavigationButtonStyle, 123, 134)).setAlpha(R.styleable
                .AppCompatTheme_textAppearanceSmallPopupMenu).setAnimation(true).setMaxY((float) UIUtil.dip2px
                (getContext(), 400.0f)).setRadiusScale(1.1f).setStep(120).setLevel(RainSurfaceView
                .RAIN_LEVEL_RAINSTORM).setHeightRadio(0.8f).setNightColor(Color.rgb(R.styleable
                .Toolbar_titleTextAppearance, 36, 42)).setDensity(density).setNightAlpha(170));
        this.mCloudList.add(new Cloud(UIUtil.dip2px(getContext(), 184.0f), -UIUtil.dip2px(getContext(), 16.0f),
                UIUtil.dip2px(getContext(), 600.0f), UIUtil.dip2px(getContext(), 158.0f), Color.rgb(R.styleable
                .AppCompatTheme_toolbarNavigationButtonStyle, 123, 134)).setAlpha(R.styleable
                .AppCompatTheme_textAppearanceSmallPopupMenu).setAnimation(true).setMaxY((float) UIUtil.dip2px
                (getContext(), 400.0f)).setRadiusScale(1.1f).setStep(120).setLevel(RainSurfaceView
                .RAIN_LEVEL_RAINSTORM).setHeightRadio(0.8f).setNightColor(Color.rgb(37, R.styleable
                .AppCompatTheme_colorPrimary, R.styleable
                .AppCompatTheme_listPreferredItemHeightSmall)).setDensity(density).setNightAlpha(R.styleable
                .AppCompatTheme_colorControlActivated));
        this.mCloudList.add(new Cloud(UIUtil.dip2px(getContext(), 333.0f), -UIUtil.dip2px(getContext(), 200.0f),
                UIUtil.dip2px(getContext(), 600.0f), UIUtil.dip2px(getContext(), 133.0f), Color.rgb(R.styleable
                .AppCompatTheme_toolbarNavigationButtonStyle, 123, 134)).setAlpha(R.styleable
                .AppCompatTheme_textAppearanceSmallPopupMenu).setAnimation(true).setMaxY((float) UIUtil.dip2px
                (getContext(), 366.0f)).setRadiusScale(1.1f).setStep(120).setLevel(6).setHeightRadio(0.8f)
                .setNightColor(Color.rgb(R.styleable.AppCompatTheme_listPreferredItemHeight, R.styleable
                        .AppCompatTheme_spinnerDropDownItemStyle, R.styleable
                        .AppCompatTheme_textColorAlertDialogListItem)).setDensity(density).setNightAlpha(178));
        this.mCloudList.add(new Cloud(UIUtil.dip2px(getContext(), 300.0f), -UIUtil.dip2px(getContext(), 116.0f),
                UIUtil.dip2px(getContext(), 400.0f), UIUtil.dip2px(getContext(), 200.0f), Color.rgb(MotionEventCompat
                .ACTION_MASK, MotionEventCompat.ACTION_MASK, MotionEventCompat.ACTION_MASK)).setAlpha(R.styleable
                .AppCompatTheme_textAppearanceSmallPopupMenu).setAnimation(true).setMaxY((float) UIUtil.dip2px
                (getContext(), 366.0f)).setRadiusScale(1.1f).setStep(120).setLevel(RainSurfaceView
                .RAIN_LEVEL_RAINSTORM).setHeightRadio(0.8f).setNightColor(Color.rgb(134, 148, 150)).setDensity
                (density).setNightAlpha(127));
        this.mCloudList.add(new Cloud(UIUtil.dip2px(getContext(), 96.0f), -UIUtil.dip2px(getContext(), 86.0f), UIUtil
                .dip2px(getContext(), 598.0f), UIUtil.dip2px(getContext(), 200.0f), Color.rgb(MotionEventCompat
                .ACTION_MASK, MotionEventCompat.ACTION_MASK, MotionEventCompat.ACTION_MASK)).setAlpha(153)
                .setAnimation(true).setMaxY((float) UIUtil.dip2px(getContext(), 333.0f)).setRadiusScale(1.1f).setStep
                        (GlobalConfig.MESSAGE_ACCU_GET_LOCATION_SUCC).setLevel(RainSurfaceView.RAIN_LEVEL_RAINSTORM)
                .setHeightRadio(0.8f).setNightColor(Color.rgb(R.styleable.AppCompatTheme_listMenuViewStyle, R
                        .styleable.AppCompatTheme_switchStyle, R.styleable.AppCompatTheme_toolbarStyle)).setDensity
                        (density).setNightAlpha(178));
        this.mCloudList.add(new Cloud(UIUtil.dip2px(getContext(), (float) (-UIUtil.dip2px(getContext(), 11.0f))),
                -UIUtil.dip2px(getContext(), 110.0f), UIUtil.dip2px(getContext(), 300.0f), UIUtil.dip2px(getContext()
                , 200.0f), Color.rgb(122, 140, 152)).setAlpha(R.styleable
                .AppCompatTheme_textAppearanceSmallPopupMenu).setAnimation(true).setMaxY((float) UIUtil.dip2px
                (getContext(), 333.0f)).setRadiusScale(1.1f).setStep(140).setLevel(RainSurfaceView
                .RAIN_LEVEL_THUNDERSHOWER).setHeightRadio(0.8f).setNightColor(Color.rgb(R.styleable
                .Toolbar_titleTextColor, R.styleable.AppCompatTheme_colorAccent, R.styleable
                .AppCompatTheme_dialogTheme)).setDensity(density).setNightAlpha(R.styleable
                .AppCompatTheme_textAppearanceSmallPopupMenu));
        this.mCloudList.add(new Cloud(UIUtil.dip2px(getContext(), 297.0f), -UIUtil.dip2px(getContext(), 162.0f),
                UIUtil.dip2px(getContext(), 200.0f), UIUtil.dip2px(getContext(), 216.0f), Color.rgb(WeatherCircleView
                .START_ANGEL_90, R.styleable.AppCompatTheme_tooltipFrameBackground, 120)).setAlpha(R.styleable
                .AppCompatTheme_textAppearanceSmallPopupMenu).setAnimation(true).setMaxY((float) UIUtil.dip2px
                (getContext(), 366.0f)).setRadiusScale(1.1f).setStep(WeatherCircleView.START_ANGEL_180).setLevel
                (6).setHeightRadio(0.25f).setHeightRadio(0.25f).setNightColor
                (Color.rgb(R.styleable.AppCompatTheme_dialogPreferredPadding, R.styleable
                        .AppCompatTheme_listPreferredItemPaddingRight, R.styleable.AppCompatTheme_searchViewStyle))
                .setDensity(density).setNightAlpha(178));
        this.mCloudList.add(new Cloud(UIUtil.dip2px(getContext(), 10.0f), -UIUtil.dip2px(getContext(), 230.0f),
                UIUtil.dip2px(getContext(), 100.0f), UIUtil.dip2px(getContext(), 216.0f), Color.rgb(MotionEventCompat
                .ACTION_MASK, MotionEventCompat.ACTION_MASK, MotionEventCompat.ACTION_MASK)).setAlpha(153)
                .setAnimation(true).setMaxY((float) UIUtil.dip2px(getContext(), 366.0f)).setRadiusScale(1.1f).setStep
                        (160).setLevel(RainSurfaceView.RAIN_LEVEL_THUNDERSHOWER).setHeightRadio(FogParticle
                        .SPEED_UNIT).setNightColor(Color.rgb(R.styleable
                        .AppCompatTheme_textAppearancePopupMenuHeader, R.styleable.AppCompatTheme_windowNoTitle, 120)
                ).setDensity(density).setNightAlpha(178));
        this.mCloudList.add(new Cloud(UIUtil.dip2px(getContext(), 366.0f), -UIUtil.dip2px(getContext(), 231.0f), 0,
                UIUtil.dip2px(getContext(), 233.0f), Color.rgb(221, 226, 229)).setAlpha(204).setAnimation(true)
                .setMaxY((float) UIUtil.dip2px(getContext(), 333.0f)).setRadiusScale(1.3f).setStep(160).setLevel
                        (RainSurfaceView.RAIN_LEVEL_THUNDERSHOWER).setDensity(density).setNightColor(Color.rgb(R
                        .styleable.AppCompatTheme_spinnerDropDownItemStyle, R.styleable
                        .AppCompatTheme_windowActionModeOverlay, 119)).setNightAlpha(MotionEventCompat.ACTION_MASK));
    }

    protected void onCreateOrientationInfoListener() {
        this.mListener = new OrientationSensorUtil.OrientationInfoListener() {
            public void onOrientationInfoChange(float x, float y, float z) {
                OverCastView.this.mDeltaAngleZ = z;
                if (y < 0.0f) {
                    OverCastView.this.mDeltaAngleY = -y;
                } else {
                    OverCastView.this.mDeltaAngleY = y;
                }
            }
        };
    }
}
