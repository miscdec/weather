package com.opweather.adapter;

import android.content.Context;
import android.support.v4.view.PagerAdapter;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.opweather.bean.CityData;
import com.opweather.ui.ContentWrapper;
import com.opweather.ui.ContentWrapper.OnUIChangedListener;
import com.opweather.ui.MainActivity.OnViewPagerScrollListener;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MainPagerAdapter extends PagerAdapter {

    private List<CityData> mCitys;
    private Map<Integer, WeakReference<ContentWrapper>> mContentWrapper;
    private Context mContext;
    private OnUIChangedListener mOnUIChangedListener;
    private List<OnViewPagerScrollListener> mOnViewPagerScrollListener;
    private TextView mTextView;

    public MainPagerAdapter(Context context, List<OnViewPagerScrollListener> onViewPagerScrollListener, TextView
            textView) {
        mContext = context;
        mCitys = new ArrayList<>();
        mOnViewPagerScrollListener = onViewPagerScrollListener;
        mContentWrapper = new HashMap<>();
        mTextView = textView;
    }

    @Override
    public int getCount() {
        return mCitys.size();
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == ((ContentWrapper) object).getContent();
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        CityData city = mCitys.get(position);
        WeakReference<ContentWrapper> wr = mContentWrapper.get(position);
        ContentWrapper wrapper = null;
        if (wr != null) {
            wrapper = wr.get();
            if (wrapper == null) {
                mContentWrapper.remove(position);
            }
        }
        if (wrapper == null) {
            wrapper = new ContentWrapper(mContext, city, mTextView);
            wrapper.setOnUIChangedListener(mOnUIChangedListener);
            mContentWrapper.put(position, new WeakReference(wrapper));
            //wrapper.updateWeatherInfo(CacheMode.LOAD_DEFAULT);
        }
        return super.instantiateItem(container, position);
    }
}
