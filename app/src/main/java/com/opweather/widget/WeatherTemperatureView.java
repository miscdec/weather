package com.opweather.widget;

import android.content.Context;
import android.graphics.*;
import android.support.annotation.Nullable;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.util.AttributeSet;
import android.view.View;
import com.opweather.R;
import com.opweather.constants.GlobalConfig;
import com.opweather.util.TemperatureUtil;
import com.opweather.util.UIUtil;
import com.opweather.widget.openglbase.RainSurfaceView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class WeatherTemperatureView extends View {
    public static final String DEFAULT_HTEMP_LINE_COLOR = "#FF00AAFF";
    public static final String DEFAULT_HTEMP_POINT_COLOR = "#FFFFFF";
    public static final String DEFAULT_LTEMP_LINE_COLOR = "#FF00AAFF";
    public static final String DEFAULT_LTEMP_POINT_COLOR = "#CED7DC";
    public static final String DEFAULT_PATH_AREA_ONE_COLOR = "#66FFFFFF";
    public static final String DEFAULT_PATH_AREA_TWO_COLOR = "#26000000";
    public static final String DEFAULT_VALUE_COLOR = "#DDDDDD";
    int[] dx;
    private Context mContext;
    private Paint mHTempLinePaint;
    private Paint mHTempPointPaint;
    private Paint mLTempLine2Paint;
    private Paint mLTempPoint2Paint;
    private ArrayList<Integer> mLowTemp;
    private Paint mPath1Paint;
    private Paint mPath2Paint;
    private int mRealCount;
    int mSpace;
    private Paint mTempValuePaint;
    private List<Integer> mTopTemp;
    private int maxTemp;
    private int minTemp;
    private int viewHeight;
    private int viewWidth;

    public WeatherTemperatureView(Context context) {
        this(context, null);
    }

    public WeatherTemperatureView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public WeatherTemperatureView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        dx = new int[9];
        mContext = context;
        mTopTemp = new ArrayList();
        mTopTemp.add(ItemTouchHelper.RIGHT);
        mTopTemp.add(GlobalConfig.MESSAGE_GET_CURRENT_WEATHER_SUCC);
        mTopTemp.add(GlobalConfig.MESSAGE_GET_CURRENT_WEATHER_FAIL);
        mTopTemp.add(WeatherCircleView.ARC_DIN);
        mTopTemp.add(WeatherCircleView.DIN_ANGEL);
        mTopTemp.add(GlobalConfig.MESSAGE_GET_CURRENT_WEATHER_FAIL);
        mTopTemp.add(R.styleable.Toolbar_contentInsetStart);
        mTopTemp.add(WeatherCircleView.ARC_DIN);
        mTopTemp.add(GlobalConfig.MESSAGE_GET_CURRENT_WEATHER_SUCC);
        mLowTemp = new ArrayList(9);
        mLowTemp.add(RainSurfaceView.RAIN_LEVEL_SHOWER);
        mLowTemp.add(RainSurfaceView.RAIN_LEVEL_RAINSTORM);
        mLowTemp.add(RainSurfaceView.RAIN_LEVEL_THUNDERSHOWER);
        mLowTemp.add(6/*FragmentManagerImpl.ANIM_STYLE_FADE_EXIT*/);
        mLowTemp.add(R.styleable.Toolbar_contentInsetStart);
        mLowTemp.add(RainSurfaceView.RAIN_LEVEL_THUNDERSHOWER);
        mLowTemp.add(RainSurfaceView.RAIN_LEVEL_DOWNPOUR);
        mLowTemp.add(RainSurfaceView.RAIN_LEVEL_RAINSTORM);
        mLowTemp.add(RainSurfaceView.RAIN_LEVEL_SHOWER);
        mHTempPointPaint = new Paint();
        mHTempPointPaint.setAntiAlias(true);
        mHTempPointPaint.setColor(Color.parseColor(DEFAULT_HTEMP_POINT_COLOR));
        mLTempPoint2Paint = new Paint();
        mLTempPoint2Paint.setAntiAlias(true);
        mLTempPoint2Paint.setColor(Color.parseColor(DEFAULT_LTEMP_POINT_COLOR));
        mHTempLinePaint = new Paint();
        mHTempLinePaint.setAntiAlias(true);
        mHTempLinePaint.setStrokeWidth(5.0f);
        mHTempLinePaint.setColor(Color.parseColor(DEFAULT_LTEMP_LINE_COLOR));
        mLTempLine2Paint = new Paint();
        mHTempLinePaint.setColor(Color.parseColor(DEFAULT_LTEMP_LINE_COLOR));
        mLTempLine2Paint.setAntiAlias(true);
        mLTempLine2Paint.setStrokeWidth(5.0f);
        mTempValuePaint = new Paint();
        mTempValuePaint.setAntiAlias(true);
        mTempValuePaint.setColor(Color.parseColor(DEFAULT_VALUE_COLOR));
        mTempValuePaint.setTextSize((float) UIUtil.dip2px(mContext, 14.0f));
        mPath1Paint = new Paint();
        mPath1Paint.setAntiAlias(true);
        mPath1Paint.setColor(Color.parseColor(DEFAULT_PATH_AREA_ONE_COLOR));
        mPath2Paint = new Paint();
        mPath2Paint.setAntiAlias(true);
        mPath2Paint.setColor(Color.parseColor(DEFAULT_PATH_AREA_TWO_COLOR));
    }

    public void setPaint(String htpColor, String ltpColor, String htlColor, String ltlColor, String valueColor, String path1Color, String path2Color) {
        mHTempPointPaint.setColor(Color.parseColor(htpColor));
        mLTempPoint2Paint.setColor(Color.parseColor(ltpColor));
        mHTempLinePaint.setColor(Color.parseColor(htlColor));
        mLTempLine2Paint.setColor(Color.parseColor(ltlColor));
        mTempValuePaint.setColor(Color.parseColor(valueColor));
        mPath1Paint.setColor(Color.parseColor(path1Color));
        mPath2Paint.setColor(Color.parseColor(path2Color));
        postInvalidate();
    }

    public void setPaint(int htpColorId, int ltpColorId, int htlColorId, int ltlColorId, int valueColorId, int path1ColorId, int path2ColorId) {
        mHTempPointPaint.setColor(mContext.getResources().getColor(htpColorId));
        mLTempPoint2Paint.setColor(mContext.getResources().getColor(ltpColorId));
        mHTempLinePaint.setColor(mContext.getResources().getColor(htlColorId));
        mLTempLine2Paint.setColor(mContext.getResources().getColor(ltlColorId));
        mTempValuePaint.setColor(mContext.getResources().getColor(valueColorId));
        mPath1Paint.setColor(mContext.getResources().getColor(path1ColorId));
        mPath2Paint.setColor(mContext.getResources().getColor(path2ColorId));
        postInvalidate();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        setMeasuredDimension(measureWidth(widthMeasureSpec), measureHeight(heightMeasureSpec));
    }

    private int measureHeight(int heightMeasureSpec) {
        int specMode = MeasureSpec.getMode(heightMeasureSpec);
        int specSize = MeasureSpec.getSize(heightMeasureSpec);
        if (specMode == MeasureSpec.EXACTLY) {
            return specSize;
        }
        int result = getPaddingBottom() + getPaddingTop();
        return specMode == Integer.MIN_VALUE ? Math.min(result, specSize) : result;
    }

    private int measureWidth(int widthMeasureSpec) {
        int size = MeasureSpec.getSize(widthMeasureSpec);
        int mode = MeasureSpec.getMode(widthMeasureSpec);
        if (mode == MeasureSpec.EXACTLY) {
            return size;
        }
        return mode == 0 ? getPaddingLeft() + getPaddingRight() : Math.min(0, size);
    }

    public void initTemp(ArrayList<Integer> topTemp, ArrayList<Integer> lowTemp, int realCount) {
        if (topTemp != null && topTemp.size() > 0 && lowTemp != null && lowTemp.size() > 0) {
            mTopTemp.clear();
            mTopTemp.add((topTemp.get(0)) - 2);
            mTopTemp.addAll(topTemp);
            mTopTemp.add((topTemp.get(topTemp.size() - 1)) - 2);
            mLowTemp.clear();
            mLowTemp.add((lowTemp.get(0)) - 2);
            mLowTemp.addAll(lowTemp);
            mLowTemp.add((lowTemp.get(lowTemp.size() - 1)) - 2);
            mRealCount = realCount;
            postInvalidate();
        }
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        viewHeight = getHeight();
        viewWidth = getWidth();
        spaceHeightWidth();
        Paint.FontMetrics fontMetrics = mTempValuePaint.getFontMetrics();
        float fontHeight = fontMetrics.bottom - fontMetrics.top;
        int distance = R.styleable.AppCompatTheme_dialogTheme;
        if (fontHeight > 60.0f) {
            distance = R.styleable.AppCompatTheme_listChoiceBackgroundIndicator;
        }
        drawLinePoint(canvas, distance);
        drawPathArea(canvas, distance);
    }

    public void drawLinePoint(Canvas canvas, int distance) {
        int i;
        Paint.FontMetrics fontMetrics = mTempValuePaint.getFontMetrics();
        float fontHeight = fontMetrics.bottom - fontMetrics.top;
        String tempUnit = "°";
        for (i = 0; i < mTopTemp.size(); i++) {
            int _hTop = ((maxTemp - (mTopTemp.get(i))) * mSpace) + distance;
            if (i < mTopTemp.size() - 1) {
                canvas.drawLine((float) dx[i], (float) _hTop, (float) dx[i + 1], (float) (((maxTemp - ((Integer) mTopTemp.get(i + 1)).intValue()) * mSpace) + distance), mPath1Paint);
            }
            mTempValuePaint.setColor(Color.parseColor(DEFAULT_HTEMP_POINT_COLOR));
            int highTemp =  mTopTemp.get(i);
            String highTempString = TemperatureUtil.getHighTemperature(mContext, highTemp);
            if (i <= mRealCount) {
                canvas.drawText(highTempString, (float) (dx[i] - (getTempStringWidth(highTemp, tempUnit) / 2)), ((float) _hTop) - (fontHeight / 2.0f), mTempValuePaint);
            }
            canvas.drawCircle((float) dx[i], (float) _hTop, 10.0f, mHTempPointPaint);
        }
        for (i = mLowTemp.size() - 1; i >= 0; i--) {
            int _hLow = ((maxTemp - mLowTemp.get(i)) * mSpace) + 60;
            if (i > 0) {
                canvas.drawLine((float) dx[i], (float) _hLow, (float) dx[i - 1], (float) (((maxTemp -  mLowTemp.get(i - 1)) * mSpace) + 60), mPath2Paint);
            }
            mTempValuePaint.setColor(Color.parseColor(DEFAULT_LTEMP_POINT_COLOR));
            int lowTemp =  mLowTemp.get(i);
            String lowTempString = TemperatureUtil.getLowTemperature(mContext, lowTemp);
            if (i <= mRealCount) {
                canvas.drawText(lowTempString, (float) (dx[i] - (getTempStringWidth(lowTemp, tempUnit) / 2)), ((float) _hLow) + fontHeight, mTempValuePaint);
            }
            canvas.drawCircle((float) dx[i], (float) _hLow, 10.0f, mLTempPoint2Paint);
        }
    }

    public int getTempStringWidth(int temp, String tempUnit) {
        String lowTempString = temp + tempUnit;
        Rect bounds = new Rect();
        mTempValuePaint.getTextBounds(lowTempString, 0, lowTempString.length(), bounds);
        return bounds.width();
    }

    public void drawPathArea(Canvas canvas, int distance) {
        int i;
        Path path = new Path();
        for (i = 0; i < mTopTemp.size(); i++) {
            int _hTop = ((maxTemp - mTopTemp.get(i)) * mSpace) + distance;
            if (i == 0) {
                path.moveTo((float) dx[0], (float) _hTop);
            }
            path.lineTo((float) dx[i], (float) _hTop);
        }
        for (i = mLowTemp.size() - 1; i >= 0; i--) {
            path.lineTo((float) dx[i], (float) (((maxTemp - mLowTemp.get(i)) * mSpace) + 60));
        }
        path.close();
        canvas.drawPath(path, mPath1Paint);
    }

    private void spaceHeightWidth() {
        minTemp = Collections.min(mLowTemp);
        maxTemp = Collections.max(mTopTemp);
        mSpace = (viewHeight - 120) / (maxTemp - minTemp);
        dx[0] = (-(viewWidth * 1)) / 12;
        dx[1] = (viewWidth * 1) / 12;
        dx[2] = (viewWidth * 3) / 12;
        dx[3] = (viewWidth * 5) / 12;
        dx[4] = (viewWidth * 7) / 12;
        dx[5] = (viewWidth * 9) / 12;
        dx[6] = (viewWidth * 11) / 12;
        dx[7] = (viewWidth * 13) / 12;
        dx[8] = (viewWidth * 15) / 12;
    }
}
