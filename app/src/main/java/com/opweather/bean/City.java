package com.opweather.bean;

import com.opweather.util.StringUtils;

public class City {
    String EnglishName;
    int Level;
    String city;

    public City() {
        this.Level = -1;
        this.city = StringUtils.EMPTY_STRING;
        this.EnglishName = StringUtils.EMPTY_STRING;
    }

    public void setLevel(int level) {
        this.Level = level;
    }

    public int getLevel() {
        return this.Level;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public void setEnglishName(String englishName) {
        this.EnglishName = englishName;
    }

    public String getEnglishName() {
        return this.EnglishName;
    }

    public String getCity() {
        return this.city;
    }
}
