package com.opweather.api.nodes;

import android.os.Parcel;

import com.opweather.api.helper.DateUtils;
import com.opweather.api.helper.StringUtils;
import com.opweather.api.impl.SwaRequest;

import java.util.Date;

public class SwaAlarm extends Alarm {
    public final Creator<SwaAlarm> CREATOR = new Creator<SwaAlarm>() {
        public SwaAlarm createFromParcel(Parcel source) {
            return setParcel(source);
        }

        public SwaAlarm[] newArray(int size) {
            return new SwaAlarm[size];
        }
    };
    private String mAlarmAreaName;
    private String mContentText;
    private Date mEndTime;
    private String mLevelName;
    private String mLevelNo;
    private Date mPublishTime;
    private Date mStartTime;
    private String mTypeName;
    private String mTypeNo;

    public SwaAlarm(String areaCode, String areaName) {
        super(areaCode, areaName, SwaRequest.DATA_SOURCE_NAME);
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

    public String getLevelNumber() {
        return mLevelNo;
    }

    public String getTypeNumber() {
        return mTypeNo;
    }

    public static SwaAlarm build(String areaCode, String alarmAreaName, String typeName, String typeNo, String
            levelName, String levelNo, String contentText, String publishTime) {
        if (StringUtils.isBlank(areaCode) || StringUtils.isBlank(typeName)) {
            return null;
        }
        SwaAlarm swaAlarm = new SwaAlarm(areaCode, null);
        swaAlarm.mAlarmAreaName = alarmAreaName;
        swaAlarm.mContentText = contentText;
        swaAlarm.mLevelName = levelName;
        swaAlarm.mLevelNo = levelNo;
        swaAlarm.mTypeName = typeName;
        swaAlarm.mTypeNo = typeNo;
        swaAlarm.mPublishTime = DateUtils.parseSwaAlarmDate(publishTime);
        swaAlarm.mStartTime = null;
        swaAlarm.mEndTime = null;
        return swaAlarm;
    }

    public int describeContents() {
        return 0;
    }

    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(getAreaCode());
        dest.writeString(mAlarmAreaName);
        dest.writeString(mTypeName);
        dest.writeString(mTypeNo);
        dest.writeString(mLevelName);
        dest.writeString(mLevelNo);
        dest.writeString(mContentText);
        dest.writeString(DateUtils.formatSwaRequestDateText(mPublishTime));
    }

    public SwaAlarm setParcel(Parcel parcel) {
        return build(parcel.readString(), parcel.readString(), parcel.readString(), parcel.readString(), parcel
                .readString(), parcel.readString(), parcel.readString(), parcel.readString());
    }
}
