package com.opweather.bean;

import com.opweather.util.StringUtils;

public class TimeZone {
    String Code;
    double GmtOffset;
    boolean IsDaylightSaving;
    String Name;
    String NextOffsetChange;

    public TimeZone() {
        this.Code = StringUtils.EMPTY_STRING;
        this.Name = StringUtils.EMPTY_STRING;
        this.GmtOffset = 0.0d;
        this.IsDaylightSaving = false;
        this.NextOffsetChange = StringUtils.EMPTY_STRING;
    }

    public String getCode() {
        return this.Code;
    }

    public String getName() {
        return this.Name;
    }

    public double getGmtOffset() {
        return this.GmtOffset;
    }

    public boolean isDaylightSaving() {
        return this.IsDaylightSaving;
    }

    public String getNextOffsetChange() {
        return this.NextOffsetChange;
    }
}
