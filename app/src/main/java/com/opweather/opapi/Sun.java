package com.opweather.opapi;

import com.opweather.api.helper.DateUtils;

import java.util.Date;

public class Sun extends AbstractWeather {
    private final Date mRise;
    private final Date mSet;

    public Sun(String areaCode, String dataSource, long rise, long set) {
        this(areaCode, null, dataSource, rise, set);
    }

    public Sun(String areaCode, String dataSource, Date rise, Date set) {
        this(areaCode, null, dataSource, rise, set);
    }

    public Sun(String areaCode, String areaName, String dataSource, long rise, long set) {
        super(areaCode, areaName, dataSource);
        mRise = DateUtils.epochDateToDate(rise);
        mSet = DateUtils.epochDateToDate(set);
    }

    public Sun(String areaCode, String areaName, String dataSource, Date rise, Date set) {
        super(areaCode, areaName, dataSource);
        mRise = rise;
        mSet = set;
    }

    public String getWeatherName() {
        return "Sunrise and Sunset";
    }

    public Date getRise() {
        return this.mRise;
    }

    public long getEpochRise() {
        return DateUtils.dateToEpochDate(getRise());
    }

    public Date getSet() {
        return this.mSet;
    }

    public long getEpochSet() {
        return DateUtils.dateToEpochDate(getSet());
    }
}
