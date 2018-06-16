package com.opweather.api.nodes;

import android.os.Parcel;

import com.opweather.api.helper.DateUtils;
import com.opweather.api.helper.StringUtils;
import com.opweather.api.impl.AccuRequest;

import java.util.Date;

public class AccuAlarm extends Alarm {
    public static final Creator<AccuAlarm> CREATOR = new Creator<AccuAlarm>() {
        @Override
        public AccuAlarm createFromParcel(Parcel parcel) {
            return build(parcel.readString(), parcel.readString(), parcel.readString(), parcel.readString(), parcel
                    .readString(), parcel.readLong(), parcel.readLong());
        }

        @Override
        public AccuAlarm[] newArray(int size) {
            return new AccuAlarm[size];
        }
    };
    private String mAlarmAreaName;
    private String mContentText;
    private Date mEndTime;
    private String mLevelName;
    private Date mPublishTime;
    private Date mStartTime;
    private String mTypeName;

    public AccuAlarm(String areaCode, String areaName) {
        super(areaCode, areaName, AccuRequest.DATA_SOURCE_NAME);
    }

    public String getAlarmAreaName() {
        return mAlarmAreaName;
    }

    public Date getPublishTime() {
        return mPublishTime;
    }

    public Date getStartTime() {
        return mStartTime;
    }

    public Date getEndTime() {
        return mEndTime;
    }

    public String getTypeName() {
        return mTypeName;
    }

    public String getLevelName() {
        return mLevelName;
    }

    public String getContentText() {
        return mContentText;
    }

    public static AccuAlarm build(String areaCode, String alarmAreaName, String typeName, String levelName, String 
            contentText, long startTime, long endTime) {
        if (StringUtils.isBlank(areaCode) || StringUtils.isBlank(typeName)) {
            return null;
        }
        AccuAlarm accuAlarm = new AccuAlarm(areaCode, null);
        accuAlarm.mAlarmAreaName = alarmAreaName;
        accuAlarm.mContentText = contentText;
        accuAlarm.mLevelName = levelName;
        accuAlarm.mTypeName = typeName;
        accuAlarm.mStartTime = DateUtils.epochDateToDate(startTime);
        accuAlarm.mPublishTime = DateUtils.epochDateToDate(startTime);
        accuAlarm.mEndTime = DateUtils.epochDateToDate(endTime);
        return accuAlarm;
    }

    public int describeContents() {
        return 0;
    }

    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(getAreaCode());
        dest.writeString(mAlarmAreaName);
        dest.writeString(mTypeName);
        dest.writeString(mLevelName);
        dest.writeString(mContentText);
        dest.writeLong(DateUtils.dateToEpochDate(mStartTime));
        dest.writeLong(DateUtils.dateToEpochDate(mEndTime));
    }

    public AccuAlarm setParcel(Parcel parcel) {
        return build(parcel.readString(), parcel.readString(), parcel.readString(), parcel.readString(), parcel
                .readString(), parcel.readLong(), parcel.readLong());
    }
}
