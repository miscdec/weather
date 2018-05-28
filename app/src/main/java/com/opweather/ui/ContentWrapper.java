package com.opweather.ui;

import android.content.Context;
import android.os.Handler;
import android.support.v4.widget.AutoScrollHelper;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.widget.TextView;

import com.amap.api.services.core.AMapException;
import com.opweather.R;
import com.opweather.bean.CityData;
import com.opweather.opapi.RootWeather;
import com.opweather.ui.MainActivity.OnViewPagerScrollListener;
import com.opweather.util.WeatherResHelper;
import com.opweather.widget.RefreshWeatherUnitView;
import com.opweather.widget.RefreshWeatherUnitView.OnRefreshUnitListener;

public class ContentWrapper implements OnViewPagerScrollListener, OnRefreshUnitListener {

    private Context mContext;
    private CityData mCityData;
    Handler mScrollHandler;
    private int cacheWeatherID;
    private float mOffset;
    private int index;
    private boolean mHasLocation;
    private boolean mIsFling;
    private boolean mLoading;
    private boolean mMoved;
    private boolean mUp;
    private TextView mToolbar_subTitle;
    private GestureDetector mGestureDetector;
    private OnUIChangedListener mUIListener;
    private RefreshWeatherUnitView content;


    public interface OnUIChangedListener {
        void ChangePathMenuResource(int i, boolean z, boolean z2);

        void onChangedCurrentWeather();

        void onError();

        void onScrollViewChange(float f);

        void onWeatherDataUpdate(int i);
    }

    public void setOnUIChangedListener(OnUIChangedListener onUIChangedListener) {
        mUIListener = onUIChangedListener;
    }

    class ScrollViewGestureListener extends GestureDetector.SimpleOnGestureListener {

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

    public View getContent() {
        return content;
    }

    public View getChild(int id) {
        return content.findViewById(id);
    }

    @Override
    public void onScrolled(float a, int position) {
        if (index >= position - 1 && index <= position + 1) {
            changeTopColor();
            float f = a;
            if (this.index == position) {
                if (a < 1.0f && ((double) a) >= 0.5d) {
                    f = (2.0f * a) - 1.0f;
                } else if (((double) a) < 0.5d) {
                }
            } else if (this.index < position) {
                f = (1.0f - a) * 2.0f;
            } else if (this.index > position) {
                float a1 = a * 2.0f;
                if (a <= 0.5f) {
                    f = 1.0f - a1;
                }
            }
            resetTopViewTextColor();
        }
    }

    public void changeTopColor() {
        int margin;
        View secondView = getChild(R.id.opweather_info);
        int moveToOffset = (int) secondView.getY();
        RootWeather data = mCityData.getWeathers();
        boolean isChinaCity = true;
        if (data != null) {
            isChinaCity = data.isFromChina();
        }
        if (isChinaCity) {
            changeTopViewTextColor(moveToOffset);
        } else {
            changeTopViewTextColor(AMapException.CODE_AMAP_SUCCESS);
        }
        if (this.mOffset > 0.0f) {
            float dy = ((float) moveToOffset) - this.mOffset;
            if (dy < 0.0f) {
                dy = AutoScrollHelper.RELATIVE_UNSPECIFIED;
            }
            float alpha = dy / ((float) moveToOffset);
            if (mUIListener != null) {
                mUIListener.onScrollViewChange(alpha);
            }
        }
        int marginMove = (int) ((((float) secondView.getHeight()) * mOffset) / ((float) moveToOffset));
        if (marginMove > secondView.getHeight()) {
            margin = secondView.getHeight();
        } else {
            margin = marginMove;
        }
        getChild(R.id.current_opweather_overlay).scrollTo(0, margin * 2);
    }

    public void changeTopViewTextColor(int moveToOffset) {
        int y = (int) getChild(R.id.opweather_info).getY();
        if (mCityData.getWeathers() == null) {
            changePathMenuResource(false, mLoading);
            return;
        }
        int weatherId = WeatherResHelper.weatherToResID(mContext, mCityData.getWeathers().getCurrentWeatherId());
        if (weatherId == 9999) {
            weatherId = WeatherResHelper.weatherToResID(mContext, cacheWeatherID);
        }
        if (!needGrayColor(weatherId)) {
            changePathMenuResource(false, mLoading);
        } else if (this.mOffset >= ((float) moveToOffset)) {
            changePathMenuResource(false, mLoading);
        } else {
            changePathMenuResource(true, mLoading);
        }
    }

    public boolean needGrayColor(int weatherId) {
        RootWeather data = mCityData.getWeathers();
        boolean isDay = true;
        if (data != null) {
            try {
                isDay = mCityData.isDay(data);
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        }
        return weatherId == 1003 && isDay;
    }

    private void changePathMenuResource(boolean isBlack, boolean isLoading) {
        if (mUIListener != null) {
            mUIListener.ChangePathMenuResource(index, isBlack, isLoading);
        }
    }

    public void resetTopViewTextColor() {
        if (this.mCityData.getWeathers() != null) {
            int weatherId = WeatherResHelper.weatherToResID(this.mContext, this.mCityData.getWeathers().getCurrentWeatherId());
            if (weatherId == 9999) {
                weatherId = WeatherResHelper.weatherToResID(this.mContext, this.cacheWeatherID);
            }
            View secondView = getChild(R.id.opweather_info);
            RootWeather data = this.mCityData.getWeathers();
            boolean isChinaCity = true;
            if (data != null) {
                isChinaCity = data.isFromChina();
            }
            if (this.mOffset >= ((float) (isChinaCity ? (int) secondView.getY() : AMapException.CODE_AMAP_SUCCESS))) {
                return;
            }
            if (needGrayColor(weatherId)) {
                changePathMenuResource(true, this.mLoading);
            } else {
                changePathMenuResource(false, this.mLoading);
            }
        }
    }

    @Override
    public void onSelected(int i) {

    }

    @Override
    public void refreshUnit() {

    }
}
