package com.opweather.widget;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.LinearGradient;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Shader;
import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.Nullable;
import android.support.v4.widget.AutoScrollHelper;
import android.util.AttributeSet;
import android.view.View;

import com.opweather.R;

public class AqiBar extends View {

    private static final int COLOR_LEVEL_FIVE = -1;
    private static final int COLOR_LEVEL_FOUR = -1;
    private static final int COLOR_LEVEL_ONE = -1;
    private static final int COLOR_LEVEL_SIX = -1;
    private static final int COLOR_LEVEL_THREE = -1;
    private static final int COLOR_LEVEL_TWO = -1;
    private static final boolean DBG = false;
    private static final int MAX_VALUE = 500;
    private static final int MIN_HEIGHT = 5;
    private static final int MIN_WIDTH = 5;
    private static final String TAG = "AqiView";
    private boolean hasAqiValue;
    private OnAqiLevelChangeListener mAqiLevelChangeListener;
    private int mAqiValue;
    private Paint mBackgroundPaint;
    private Paint mForegroundPaint;
    private Shader mLinearGradient;
    private Path path;
    private static final int[] COLOR_LINEAR_DATAS = new int[]{-15598962, -205299, -29184, -55552, -11659130, -9762048};


    public enum Level {
        ONE,
        TWO,
        THREE,
        FOUR,
        FIVE,
        SIX
    }

    public interface OnAqiLevelChangeListener {
        void onLevelChanged(Level level, int i);
    }

    public void setOnAqiLevelChangeListener(OnAqiLevelChangeListener listener) {
        mAqiLevelChangeListener = listener;
    }

    static class SavedState extends BaseSavedState {
        int aqiValue;

        public SavedState(Parcelable superState) {
            super(superState);
        }

        private SavedState(Parcel source) {
            super(source);
            aqiValue = source.readInt();
        }

        public static final Parcelable.Creator<SavedState> CREATOR = new Parcelable.Creator<SavedState>() {
            @Override
            public SavedState createFromParcel(Parcel in) {
                return new SavedState(in);
            }

            @Override
            public SavedState[] newArray(int size) {
                return new SavedState[size];
            }
        };
    }

    public AqiBar(Context context) {
        this(context, null);
    }

    public AqiBar(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public AqiBar(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        mBackgroundPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mForegroundPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mLinearGradient = null;
        path = new Path();
        hasAqiValue = true;
        if (isInEditMode()) {
            setAqiValue(50);
        } else {
            setAqiValue(0);
        }
    }

    private void initPaints(int barWidth, int barHeight, int aqiWidth, float offset) {
        mLinearGradient = new LinearGradient(offset, 0.0f, ((float) barWidth) - offset, (float) barHeight,
                COLOR_LINEAR_DATAS, null, Shader.TileMode.REPEAT);
        mBackgroundPaint.setShader(this.mLinearGradient);
        mBackgroundPaint.setStyle(Paint.Style.FILL);
        mForegroundPaint.setStyle(Paint.Style.FILL);
        if (!path.isEmpty()) {
            path.reset();
        }
        if (((float) aqiWidth) > ((float) barWidth) - (2.0f * offset)) {
            aqiWidth = (int) (((float) barWidth) - (2.0f * offset));
        }
        path.moveTo((float) aqiWidth, (float) barHeight);
        path.lineTo(((float) aqiWidth) + (2.0f * offset), (float) barHeight);
        path.lineTo(((float) aqiWidth) + offset, AutoScrollHelper.RELATIVE_UNSPECIFIED);
        path.close();
    }

    public static Level getAqiLevel(int value) {
        Level localLevel = Level.ONE;
        if (value >= 0 && value < 51) {
            return Level.ONE;
        }
        if (value >= 51 && value < 101) {
            return Level.TWO;
        }
        if (value >= 101 && value < 151) {
            return Level.THREE;
        }
        if (value >= 151 && value < 201) {
            return Level.FOUR;
        }
        if (value < 201 || value >= 301) {
            return value >= 301 ? Level.SIX : localLevel;
        } else {
            return Level.FIVE;
        }
    }

    public void setAqiValue(int value) {
        setAqiValue(value, true);
    }

    public void setAqiValue(int value, boolean hasValue) {
        if (value < 0) {
            throw new IllegalArgumentException("Value should not less than zero.");
        }
        hasAqiValue = hasValue;
        mAqiValue = value;
        mForegroundPaint.setColor(getAqiColor(value));
        if (mAqiLevelChangeListener != null) {
            mAqiLevelChangeListener.onLevelChanged(getAqiLevel(), value);
        }
        invalidate();
    }

    public int getAqiValue() {
        return mAqiValue;
    }

    public Level getAqiLevel() {
        return getAqiLevel(getAqiValue());
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        int barWidth = (getWidth() - getPaddingLeft()) - getPaddingRight();
        int barHeight = (getHeight() - getPaddingTop()) - getPaddingBottom();
        int aqiWidth = Math.round((((float) mAqiValue) * ((float) barWidth)) / 500.0f);
        if (aqiWidth < 0) {
            aqiWidth = 0;
        }
        if (aqiWidth > barWidth) {
            aqiWidth = barWidth;
        }
        float offset = getResources().getDimension(R.dimen.dimen_3);
        initPaints(barWidth, barHeight, aqiWidth, offset);
        canvas.save();
        canvas.translate((float) getPaddingLeft(), (float) getPaddingTop());
        canvas.drawRect(offset, AutoScrollHelper.RELATIVE_UNSPECIFIED, ((float) barWidth) - offset, (float)
                (barHeight / 2), mBackgroundPaint);
        if (this.hasAqiValue) {
            canvas.drawPath(path, mForegroundPaint);
        }
        canvas.restore();
    }

    @Override
    public Parcelable onSaveInstanceState() {
        SavedState ss = new SavedState(super.onSaveInstanceState());
        ss.aqiValue = mAqiValue;
        return ss;
    }

    @Override
    public void onRestoreInstanceState(Parcelable state) {
        SavedState ss = (SavedState) state;
        super.onRestoreInstanceState(ss.getSuperState());
        setAqiValue(ss.aqiValue);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        float density = getContext().getResources().getDisplayMetrics().density;
        int dw = Math.round(5.0f * density);
        setMeasuredDimension(resolveSizeAndState(dw + (getPaddingLeft() + getPaddingRight()), widthMeasureSpec, 0),
                resolveSizeAndState(Math.round(5.0f * density) + (getPaddingTop() + getPaddingBottom()),
                        heightMeasureSpec, 0));
    }

    private int getAqiColor(int value) {
        return getAqiColor(getAqiLevel(value));
    }

    private int getAqiColor(Level level) {
        switch (level) {
            case ONE:
                return 1;
            case TWO:
                return 2;
            case THREE:
                return 3;
            case FOUR:
                return 4;
            case FIVE:
                return 5;
            case SIX:
                return 6;
            default:
                return COLOR_LEVEL_TWO;
        }
    }

}
