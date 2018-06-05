package com.opweather.widget;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.support.v7.widget.AppCompatEditText;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.view.GestureDetector;
import android.view.GestureDetector.SimpleOnGestureListener;
import android.view.MotionEvent;
import android.widget.EditText;

import com.opweather.R;
import com.opweather.util.StringUtils;
import com.opweather.widget.openglbase.RainSurfaceView;


public class ClearableEditText extends EditText {
    private static final float DEFAULT_CLEAR_BUTTON_PADDING = 5.0f;
    private static final int DEFAULT_CLEAR_BUTTON_POSITION = 2;
    private static final float DEFAULT_CLEAR_BUTTON_SIZE = 16.0f;
    private static final int DEFAULT_CLEAR_DRAWABLE_ID = 2131230954;
    public static final int POSITION_END = 2;
    public static final int POSITION_START = 0;
    private static final String TAG = "ClearableEditText";
    private static final boolean localLOG = false;
    private boolean mClearButtonAlwaysVisible;
    private Drawable mClearButtonDrawable;
    private int mClearButtonHeight;
    private int mClearButtonPadding;
    private int mClearButtonPosition;
    private int mClearButtonWidth;
    private GestureDetector mGestureDetector;
    private OnContentClearListener mOnContentClearListener;

    private class ClearButtonGestureListener extends SimpleOnGestureListener {
        private ClearButtonGestureListener() {
        }

        public boolean onSingleTapUp(MotionEvent e) {
            if (!(getCompoundDrawables()[0] == null && getCompoundDrawables()[2] == null)) {
                int left;
                int rectWidth = mClearButtonDrawable.getBounds().width();
                int rectHeight = mClearButtonDrawable.getBounds().height();
                if (mClearButtonPosition == 0) {
                    left = getPaddingLeft();
                } else {
                    left = (getWidth() - getPaddingRight()) - rectWidth;
                }
                int top = (((getHeight() + getPaddingTop()) - getPaddingBottom()) - rectHeight) / 2;
                int right = left + rectWidth;
                int bottom = top + rectHeight;
                if (e.getX() > ((float) left) && e.getX() < ((float) right) && e.getY() > (
                        (float) top) && e.getY() < ((float) bottom)) {
                    clearContent();
                }
            }
            return false;
        }
    }

    private class ClearableTextWatcher implements TextWatcher {
        private ClearableTextWatcher() {
        }

        public void onTextChanged(CharSequence s, int start, int before, int count) {
            if (s == null || s.length() == 0) {
                setClearButtonVisible(false);
            } else if (isFocused()) {
                setClearButtonVisible(true);
            }
        }

        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
        }

        public void afterTextChanged(Editable s) {
        }
    }

    public interface OnContentClearListener {
        void onContentClear(ClearableEditText clearableEditText);
    }

    public ClearableEditText(Context context) {
        this(context, null);
    }

    public ClearableEditText(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public ClearableEditText(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        mClearButtonAlwaysVisible = false;
        float density = context.getResources().getDisplayMetrics().density;
        TypedArray a = context.getTheme().obtainStyledAttributes(attrs, R.styleable
                .ClearableEditText, defStyleAttr, R.style.ClearableEditText);
        mClearButtonPosition = a.getInt(R.styleable.ClearableEditText_clearButtonPosition,
                POSITION_END);
        if (!(mClearButtonPosition == 0 || mClearButtonPosition == 2)) {
            mClearButtonPosition = 2;
        }
        mClearButtonAlwaysVisible = a.getBoolean(R.styleable
                .ClearableEditText_clearButtonAlwaysVisible, false);
        mClearButtonPadding = a.getDimensionPixelSize(R.styleable
                .ClearableEditText_clearButtonPadding, (int) (5.0f * density));
        mClearButtonDrawable = a.getDrawable(R.styleable.ClearableEditText_clearButtonDrawable);
        if (mClearButtonDrawable == null) {
            mClearButtonDrawable = getResources().getDrawable(DEFAULT_CLEAR_DRAWABLE_ID);
        }
        mClearButtonWidth = a.getDimensionPixelSize(R.styleable
                .ClearableEditText_clearButtonWidth, -1);
        mClearButtonHeight = a.getDimensionPixelSize(R.styleable
                .ClearableEditText_clearButtonHeight, -1);
        if (mClearButtonWidth == -1 || mClearButtonHeight == -1) {
            int i = (int) (16.0f * density);
            mClearButtonHeight = i;
            mClearButtonWidth = i;
        }
        mClearButtonDrawable.setBounds(POSITION_START, POSITION_START, mClearButtonWidth,
                mClearButtonHeight);
        a.recycle();
        mGestureDetector = new GestureDetector(context, new ClearButtonGestureListener());
        addTextChangedListener(new ClearableTextWatcher());
        setClearButtonVisible(false);
    }

    @SuppressLint({"ClickableViewAccessibility"})
    public boolean onTouchEvent(MotionEvent event) {
        boolean superResult = super.onTouchEvent(event);
        if (getCompoundDrawables()[mClearButtonPosition == 0 ? POSITION_START : POSITION_END] != null) {
            mGestureDetector.onTouchEvent(event);
        }
        return superResult;
    }

    private void clearContent() {
        setText(StringUtils.EMPTY_STRING);
        setClearButtonVisible(false);
        if (mOnContentClearListener != null) {
            mOnContentClearListener.onContentClear(this);
        }
    }

    private void setClearButtonVisible(Boolean visible) {
        if (visible || mClearButtonAlwaysVisible) {
            setCompoundDrawablePadding(mClearButtonPadding);
            setClearButton(mClearButtonPosition, mClearButtonDrawable);
            return;
        }
        setClearButton(mClearButtonPosition, null);
    }

    private void setClearButton(int position, Drawable drawable) {
        if (position == 0) {
            setCompoundDrawables(drawable, null, null, null);
        } else if (position == 2) {
            setCompoundDrawables(null, null, drawable, null);
        }
    }

    public Drawable getClearButtonDrawable() {
        return mClearButtonDrawable;
    }

    public void setClearButtonDrawable(Drawable drawable) {
        if (drawable == null) {
            throw new NullPointerException("Drawable can not be null.");
        }
        mClearButtonDrawable = drawable;
        if (getCompoundDrawables()[mClearButtonPosition == 0 ? POSITION_START : POSITION_END] != null) {
            setClearButtonVisible(Boolean.valueOf(true));
        }
    }

    public void setClearButtonAlwaysVisible(boolean visible) {
        mClearButtonAlwaysVisible = visible;
        setClearButtonVisible(true);
    }

    public void setOnContentClearListener(OnContentClearListener l) {
        mOnContentClearListener = l;
    }

    protected void onFocusChanged(boolean focused, int direction, Rect previouslyFocusedRect) {
        super.onFocusChanged(focused, direction, previouslyFocusedRect);
        if (!focused || getText().toString().length() == 0) {
            setClearButtonVisible(false);
        } else {
            setClearButtonVisible(true);
        }
    }

    public void setClearButtonPosition(int position) {
        if (position == 2 || position == 0) {
            setClearButton(position, getClearButtonDrawable());
            mClearButtonPosition = position;
            return;
        }
        throw new IllegalArgumentException("Position can only be one of: POSITION_START or POSITION_END.");
    }
}
