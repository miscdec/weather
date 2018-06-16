package com.opweather.api.nodes;

import android.os.Parcel;

import com.opweather.api.helper.DateUtils;
import com.opweather.api.helper.StringUtils;
import com.opweather.api.impl.OppoChinaRequest;

import java.util.Date;

public class OppoChinaAlarm extends Alarm {
    public static final Creator<OppoChinaAlarm> CREATOR = new Creator<OppoChinaAlarm>() {
        public OppoChinaAlarm createFromParcel(Parcel source) {
            return build(source.readString(), source.readString(), DateUtils.epochDateToDate(source.readLong()), source
                    .readString(), source.readString());
        }

        public OppoChinaAlarm[] newArray(int size) {
            return new OppoChinaAlarm[size];
        }
    };
    private String mAlarmAreaName;
    private String mContentText;
    private Date mEndTime;
    private String mLevelName;
    private Date mPublishTime;
    private Date mStartTime;
    private String mTypeName;

    public OppoChinaAlarm(String areaCode, String areaName) {
        super(areaCode, areaName, OppoChinaRequest.DATA_SOURCE_NAME);
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

    public static OppoChinaAlarm build(String areaCode, String areaName, Date observationDate, String
            warnWeatherTitle, String warnWeatherDetail) {
        if (StringUtils.isBlank(areaCode)) {
            return null;
        }
        OppoChinaAlarm oppoChinaAlarm = new OppoChinaAlarm(areaCode, areaName);
        oppoChinaAlarm.mAlarmAreaName = areaName;
        oppoChinaAlarm.mContentText = warnWeatherDetail;
        oppoChinaAlarm.mLevelName = warnWeatherTitle;
        oppoChinaAlarm.mTypeName = warnWeatherTitle;
        oppoChinaAlarm.mPublishTime = observationDate;
        oppoChinaAlarm.mStartTime = null;
        oppoChinaAlarm.mEndTime = null;
        return oppoChinaAlarm;
    }

    public int describeContents() {
        return 0;
    }

    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(getAreaCode());
        dest.writeString(mAlarmAreaName);
        dest.writeLong(DateUtils.dateToEpochDate(mPublishTime));
        dest.writeString(mTypeName);
        dest.writeString(mContentText);
    }

    public OppoChinaAlarm setParcel(Parcel parcel) {
        return build(parcel.readString(), parcel.readString(), DateUtils.epochDateToDate(parcel.readLong()), parcel
                .readString(), parcel.readString());
    }
}
