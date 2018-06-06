package com.opweather.api.nodes;

import android.content.Context;


import com.opweather.R;
import com.opweather.api.helper.StringUtils;
import com.opweather.api.impl.SwaRequest;
import com.opweather.widget.openglbase.RainSurfaceView;

import java.util.ArrayList;
import java.util.List;


public class SwaLifeIndexWeather extends LifeIndexWeather {
    private static String DEFAULT_INDEX_TEXT = StringUtils.EMPTY_STRING;
    private List<LifeIndex> mIndexList;

    public SwaLifeIndexWeather(String areaCode) {
        super(areaCode, null, SwaRequest.DATA_SOURCE_NAME);
        mIndexList = null;
    }

    public List<LifeIndex> getLifeIndexList() {
        return mIndexList;
    }

    public void add(LifeIndex item) {
        if (item != null) {
            if (mIndexList == null) {
                mIndexList = new ArrayList<>();
            }
            mIndexList.add(item);
        }
    }

    public String getUVIndexText(String defaultValue) {
        if (mIndexList == null) {
            return defaultValue;
        }
        for (LifeIndex item : mIndexList) {
            if ("fs".equals(item.getShortName())) {
                return item.getLevel();
            }
        }
        return defaultValue;
    }

    public String getUVIndexInternationalText(Context context, String defaultValue) {
        String shortText = getUVIndexText(DEFAULT_INDEX_TEXT);
        String result = shortText;
        if (StringUtils.isBlank(shortText)) {
            return result;
        }
        if (shortText.equals("很弱") || shortText.equals("最弱")) {
            return context.getString(R.string.ultraviolet_index_level_one);
        } else if (shortText.equals("弱") || shortText.equals("较弱")) {
            return context.getString(R.string.ultraviolet_index_level_two);
        } else if (shortText.equals("中等")) {
            return context.getString(R.string.ultraviolet_index_level_three);
        } else if (shortText.equals("强") || shortText.equals("较强")) {
            return context.getString(R.string.ultraviolet_index_level_four);
        } else if (shortText.equals("很强") || shortText.equals("最强")) {
            return context.getString(R.string.ultraviolet_index_level_five);
        } else {
            return result;
        }
    }

    public String getSportsIndexText(String defaultValue) {
        if (mIndexList == null) {
            return defaultValue;
        }
        for (LifeIndex item : mIndexList) {
            if ("yd".equals(item.getShortName())) {
                return item.getLevel();
            }
        }
        return defaultValue;
    }

    public String getSportsIndexInternationalText(Context context, String defaultValue) {
        String shortText = getSportsIndexText(DEFAULT_INDEX_TEXT);
        String result = shortText;
        if (StringUtils.isBlank(shortText)) {
            return result;
        }
        if (shortText.equals("适宜")) {
            return context.getString(R.string.motion_index_level_one);
        } else if (shortText.equals("较适宜")) {
            return context.getString(R.string.motion_index_level_two);
        } else if (shortText.equals("较不宜")) {
            return context.getString(R.string.motion_index_level_three);
        } else if (shortText.equals("不宜")) {
            return context.getString(R.string.motion_index_level_four);
        } else {
            return result;
        }
    }

    public String getCarwashIndexText(String defaultValue) {
        if (mIndexList == null) {
            return defaultValue;
        }
        for (LifeIndex item : mIndexList) {
            if ("xc".equals(item.getShortName())) {
                return item.getLevel();
            }
        }
        return defaultValue;
    }

    public String getCarwashIndexInternationalText(Context context, String defaultValue) {
        String shortText = getCarwashIndexText(DEFAULT_INDEX_TEXT);
        String result = shortText;
        if (StringUtils.isBlank(shortText)) {
            return result;
        }
        if (shortText.equals("适宜")) {
            return context.getString(R.string.carwash_index_level_one);
        } else if (shortText.equals("较适宜")) {
            return context.getString(R.string.carwash_index_level_two);
        } else if (shortText.equals("较不宜")) {
            return context.getString(R.string.carwash_index_level_three);
        } else if (shortText.equals("不宜")) {
            return context.getString(R.string.carwash_index_level_four);
        } else {
            return result;
        }
    }

    public String getClothingIndexText(String defaultValue) {
        if (mIndexList == null) {
            return defaultValue;
        }
        for (LifeIndex item : mIndexList) {
            if ("ct".equals(item.getShortName())) {
                return item.getLevel();
            }
        }
        return defaultValue;
    }

    public String getClothingIndexInternationalText(Context context, String defaultValue) {
        String shortText = getClothingIndexText(DEFAULT_INDEX_TEXT);
        String result = shortText;
        if (StringUtils.isBlank(shortText)) {
            return result;
        }
        if (shortText.equals("冷")) {
            return context.getString(R.string.dress_index_scarf);
        } else if (shortText.equals("热")) {
            return context.getString(R.string.dress_index_short_sleeve);
        } else if (shortText.equals("寒冷")) {
            return context.getString(R.string.dress_index_earmuff);
        } else if (shortText.equals("炎热")) {
            return context.getString(R.string.dress_index_waistcoat);
        } else if (shortText.equals("舒适")) {
            return context.getString(R.string.dress_index_fleece);
        } else if (shortText.equals("较冷")) {
            return context.getString(R.string.dress_index_sweater);
        } else if (shortText.equals("较舒适")) {
            return context.getString(R.string.dress_index_jacket);
        } else {
            return result;
        }
    }
}
