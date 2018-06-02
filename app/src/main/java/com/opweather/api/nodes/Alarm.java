package com.opweather.api.nodes;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.Date;

public abstract class Alarm extends AbstractWeather implements Parcelable {

    public Alarm(String areaCode, String areaName, String dataSource) {
        super(areaCode, areaName, dataSource);
    }

    public abstract String getAlarmAreaName();

    public abstract String getContentText();

    public abstract Date getEndTime();

    public abstract String getLevelName();

    public abstract Date getPublishTime();

    public abstract Date getStartTime();

    public abstract String getTypeName();

    public abstract Alarm setParcel(Parcel parcel);

    public String getWeatherName() {
        return "Weather alarm";
    }
}
