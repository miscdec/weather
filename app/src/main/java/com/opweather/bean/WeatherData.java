package com.opweather.bean;

import android.os.Parcel;
import android.os.Parcelable;

import com.opweather.util.StringUtils;

public class WeatherData implements Parcelable {
    private int mCurrentRealFeelTemp;
    private int mCurrentTemp;
    private int mHighTemp;
    private int mHumidity;
    private String mLocationId;
    private int mLowTemp;
    private long mSunriseTime;
    private long mSunsetTime;
    private long mTimestamp;
    private int mWeatherDescriptionId;

    public WeatherData() {
        mLocationId = StringUtils.EMPTY_STRING;
        mTimestamp = 0;
        mHighTemp = 0;
        mLowTemp = 0;
        mCurrentTemp = 0;
        mCurrentRealFeelTemp = 0;
        mHumidity = 0;
        mSunriseTime = 0;
        mSunsetTime = 0;
        mWeatherDescriptionId = 0;
    }

    public long getTimestamp() {
        return mTimestamp;
    }

    public void setTimestamp(long timestamp) {
        mTimestamp = timestamp;
    }

    public int getHighTemp() {
        return mHighTemp;
    }

    public void setHighTemp(int highTemp) {
        mHighTemp = highTemp;
    }

    public int getLowTemp() {
        return mLowTemp;
    }

    public void setLowTemp(int lowTemp) {
        mLowTemp = lowTemp;
    }

    public int getCurrentTemp() {
        return mCurrentTemp;
    }

    public void setCurrentTemp(int currentTemp) {
        mCurrentTemp = currentTemp;
    }

    public int getCurrentRealFeelTemp() {
        return mCurrentRealFeelTemp;
    }

    public void setCurrentRealFeelTemp(int currentRealFeelTemp) {
        mCurrentRealFeelTemp = currentRealFeelTemp;
    }

    public int getHumidity() {
        return mHumidity;
    }

    public void setHumidity(int humidity) {
        mHumidity = humidity;
    }

    public long getSunsetTime() {
        return mSunsetTime;
    }

    public void setSunsetTime(long time) {
        mSunsetTime = time;
    }

    public long getSunriseTime() {
        return mSunriseTime;
    }

    public void setSunriseTime(long time) {
        mSunriseTime = time;
    }

    public int getWeatherDescriptionId() {
        return mWeatherDescriptionId;
    }

    public void setWeatherDescriptionId(int id) {
        mWeatherDescriptionId = id;
    }

    public String getLocationId() {
        return mLocationId;
    }

    public void setLocationId(String locId) {
        mLocationId = locId;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(mLocationId);
        dest.writeInt(mHighTemp);
        dest.writeInt(mLowTemp);
        dest.writeInt(mCurrentTemp);
        dest.writeInt(mCurrentRealFeelTemp);
        dest.writeInt(mHumidity);
        dest.writeInt(mWeatherDescriptionId);
        dest.writeLong(mTimestamp);
        dest.writeLong(mSunriseTime);
        dest.writeLong(mSunsetTime);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<WeatherData> CREATOR = new Creator<WeatherData>() {
        @Override
        public WeatherData createFromParcel(Parcel source) {
            WeatherData weatherDate = new WeatherData();
            weatherDate.mLocationId = source.readString();
            weatherDate.mHighTemp = source.readInt();
            weatherDate.mLowTemp = source.readInt();
            weatherDate.mCurrentTemp = source.readInt();
            weatherDate.mCurrentRealFeelTemp = source.readInt();
            weatherDate.mHumidity = source.readInt();
            weatherDate.mWeatherDescriptionId = source.readInt();
            weatherDate.mTimestamp = source.readLong();
            weatherDate.mSunriseTime = source.readLong();
            weatherDate.mSunsetTime = source.readLong();
            return weatherDate;
        }

        @Override
        public WeatherData[] newArray(int size) {
            return new WeatherData[size];
        }
    };
}
