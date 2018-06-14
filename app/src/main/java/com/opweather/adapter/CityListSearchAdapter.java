package com.opweather.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Filterable;
import android.widget.TextView;

import com.opweather.R;
import com.opweather.bean.CommonCandidateCity;
import com.opweather.util.StringUtils;

import java.util.ArrayList;
import java.util.List;

public class CityListSearchAdapter extends ArrayAdapter<CommonCandidateCity> implements Filterable {
    private List<CommonCandidateCity> mCandidateList;
    private Context mContext;

    public CityListSearchAdapter(@NonNull Context context, List<CommonCandidateCity> candidateList) {
        super(context, R.layout.citylist_search, candidateList);
        mCandidateList = new ArrayList<>();
        mContext = context;
        mCandidateList = candidateList;
    }

    @Nullable
    @Override
    public CommonCandidateCity getItem(int position) {
        return mCandidateList != null ? mCandidateList.get(position) : null;
    }

    @Override
    public int getCount() {
        return mCandidateList != null ? mCandidateList.size() : 0;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        View result = convertView;
        if (result == null) {
            result = ((LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE)).inflate(R
                    .layout.citylist_search, parent, false);
        }
        TextView tv = result.findViewById(R.id.cityListSearchResult);
        CommonCandidateCity city = mCandidateList.get(position);
        String name = city.getCityName(mContext);
        String cityInfo = StringUtils.EMPTY_STRING;
        if (!TextUtils.isEmpty(name)) {
            cityInfo = cityInfo + name;
        }
        if (!TextUtils.isEmpty(city.getCityProvince(mContext))) {
            if (!TextUtils.isEmpty(cityInfo)) {
                cityInfo = cityInfo + "  ";
            }
            cityInfo = cityInfo + city.getCityProvince(mContext);
        }
        if (!TextUtils.isEmpty(city.getCityCountry(mContext))) {
            if (!TextUtils.isEmpty(cityInfo)) {
                cityInfo = cityInfo + "  ";
            }
            cityInfo = cityInfo + city.getCityCountry(mContext);
        }
        tv.setText(cityInfo);
        return result;
    }

    @Override
    public long getItemId(int position) {
        return (mCandidateList == null || mCandidateList.size() <= 0) ? 0 : (long) ((CommonCandidateCity)
                mCandidateList.get(position)).hashCode();

    }
}
