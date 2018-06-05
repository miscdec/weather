package com.opweather.bean;

import com.opweather.util.StringUtils;

public class Units {
    String Unit;
    int UnitType;
    double Value;

    public Units() {
        this.Value = 0.0d;
        this.Unit = StringUtils.EMPTY_STRING;
        this.UnitType = 0;
    }

    public double getValue() {
        return this.Value;
    }

    public String getUnit() {
        return this.Unit;
    }

    public int getUnitType() {
        return this.UnitType;
    }
}
