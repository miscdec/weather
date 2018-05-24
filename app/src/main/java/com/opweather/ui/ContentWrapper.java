package com.opweather.ui;

import android.content.Context;
import android.os.Handler;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.widget.TextView;

import com.opweather.bean.CityData;

/**
 * Created by leeyh on 5/20.
 */
public class ContentWrapper implements View.OnTouchListener,Runnable{

    private Context mContext;
    private CityData mCityData;
    Handler mScrollHandler;
    private float mOffset;
    private boolean mHasLocation;
    private boolean mIsFling;
    private boolean mLoading;
    private boolean mMoved;
    private boolean mUp;
    private TextView mToolbar_subTitle;
    private GestureDetector mGestureDetector;

    @Override
    public boolean onTouch(View view, MotionEvent motionEvent) {
        return false;
    }

    @Override
    public void run() {

    }

    class ScrollViewGestureListener extends GestureDetector.SimpleOnGestureListener{

        @Override
        public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
            return false;
        }

        @Override
        public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
            if (velocityY < -200.0f) {
                mIsFling = true;
            }
            return false;
        }
    }

    public ContentWrapper(Context context, CityData cityData, TextView textView) {
        mContext = context;
        mCityData = cityData;
        mToolbar_subTitle = textView;
        mMoved = false;
        mUp = false;
        mIsFling = false;
        mHasLocation = false;
        mScrollHandler = new Handler();
        mGestureDetector = new GestureDetector(mContext, new ScrollViewGestureListener());
    }
}
