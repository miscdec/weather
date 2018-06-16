package com.opweather.api.nodes;

import com.opweather.api.impl.OppoChinaRequest;

import java.util.ArrayList;
import java.util.List;


public class OppoChinaLifeIndexWeather extends LifeIndexWeather {
    private List<LifeIndex> mIndexList = null;

    public OppoChinaLifeIndexWeather(String areaCode) {
        super(areaCode, null, OppoChinaRequest.DATA_SOURCE_NAME);
    }

    public List<LifeIndex> getLifeIndexList() {
        return mIndexList;
    }

    public void add(LifeIndex item) {
        if (item != null) {
            if (mIndexList == null) {
                mIndexList = new ArrayList();
            }
            mIndexList.add(item);
        }
    }

    public String getBodytempIndexText(String defaultValue) {
        if (mIndexList == null) {
            return defaultValue;
        }
        for (LifeIndex item : mIndexList) {
            if ("body_temp".equals(item.getShortName())) {
                return item.getLevel();
            }
        }
        return defaultValue;
    }

    public String getPressureIndexText(String defaultValue) {
        if (mIndexList == null) {
            return defaultValue;
        }
        for (LifeIndex item : mIndexList) {
            if ("pressure".equals(item.getShortName())) {
                return item.getLevel();
            }
        }
        return defaultValue;
    }

    public String getVisibilityIndexText(String defaultValue) {
        if (mIndexList == null) {
            return defaultValue;
        }
        for (LifeIndex item : mIndexList) {
            if ("visibility".equals(item.getShortName())) {
                return item.getLevel();
            }
        }
        return defaultValue;
    }
}
