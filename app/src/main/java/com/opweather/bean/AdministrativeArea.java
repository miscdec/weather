package com.opweather.bean;

import com.opweather.util.StringUtils;

public class AdministrativeArea {
    String CountryID;
    String EnglishName;
    String EnglishType;
    String ID;
    int Level;
    String LocalizedName;
    String LocalizedType;

    public AdministrativeArea() {
        this.ID = StringUtils.EMPTY_STRING;
        this.LocalizedName = StringUtils.EMPTY_STRING;
        this.EnglishName = StringUtils.EMPTY_STRING;
        this.Level = 0;
        this.LocalizedType = StringUtils.EMPTY_STRING;
        this.EnglishType = StringUtils.EMPTY_STRING;
        this.CountryID = StringUtils.EMPTY_STRING;
    }

    public String getID() {
        return this.ID;
    }

    public String getLocalizedName() {
        return this.LocalizedName;
    }

    public String getEnglishName() {
        return this.EnglishName;
    }

    public int getLevel() {
        return this.Level;
    }

    public String getLocalizedType() {
        return this.LocalizedType;
    }

    public String getEnglishType() {
        return this.EnglishType;
    }

    public String getCountryID() {
        return this.CountryID;
    }
}
