package com.opweather.api.nodes;

import java.util.List;

public abstract class LifeIndexWeather extends AbstractWeather {

    public static class LifeIndex {
        private final String mAbbreviations;
        private final String mCnName;
        private final String mCnNameAlias;
        private final String mIndexLevel;
        private final String mIndexText;

        public LifeIndex(String str1, String str2, String str3, String str4, String str5) {
            this.mAbbreviations = str1;
            this.mCnName = str2;
            this.mCnNameAlias = str3;
            this.mIndexLevel = str4;
            this.mIndexText = str5;
        }

        public String getShortName() {
            return this.mAbbreviations;
        }

        public String getCnName() {
            return this.mCnName;
        }

        public String getCnNameAlias() {
            return this.mCnNameAlias;
        }

        public String getLevel() {
            return this.mIndexLevel;
        }

        public String getText() {
            return this.mIndexText;
        }
    }

    public abstract List<LifeIndex> getLifeIndexList();

    public LifeIndexWeather(String areaCode, String areaName, String dataSource) {
        super(areaCode, areaName, dataSource);
    }

    public String getWeatherName() {
        return "Life Index";
    }

    public int size() {
        return getLifeIndexList() == null ? 0 : getLifeIndexList().size();
    }
}
