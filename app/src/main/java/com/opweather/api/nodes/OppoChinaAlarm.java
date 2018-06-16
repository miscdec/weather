package com.opweather.api.nodes;

import android.os.Parcel;


import com.opweather.api.helper.DateUtils;
import com.opweather.api.helper.StringUtils;
import com.opweather.api.impl.OppoChinaRequest;

import java.util.Date;

public class OppoChinaAlarm extends Alarm {
    public final Creator<OppoChinaAlarm> CREATOR = new Creator<OppoChinaAlarm>() {
        @Override
        public OppoChinaAlarm createFromParcel(Parcel parcel) {
            return setParcel(parcel);
        }

        @Override
        public OppoChinaAlarm[] newArray(int i) {
            return new OppoChinaAlarm[i];
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
        return this.mAlarmAreaName;
    }

    public Date getPublishTime() {
        return this.mPublishTime;
    }

    public Date getStartTime() {
        return this.mStartTime;
    }

    public Date getEndTime() {
        return this.mEndTime;
    }

    public String getTypeName() {
        return this.mTypeName;
    }

    public String getLevelName() {
        return this.mLevelName;
    }

    public String getContentText() {
        return this.mContentText;
    }

    public static OppoChinaAlarm build(String areaCode, String areaName, Date observationDate, String warnWeatherTitle, String warnWeatherDetail) {
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
        dest.writeString(this.mAlarmAreaName);
        dest.writeLong(DateUtils.dateToEpochDate(this.mPublishTime));
        dest.writeString(this.mTypeName);
        dest.writeString(this.mContentText);
    }

    public OppoChinaAlarm setParcel(Parcel parcel) {
        return build(parcel.readString(), parcel.readString(), DateUtils.epochDateToDate(parcel.readLong()), parcel.readString(), parcel.readString());
    }
}
