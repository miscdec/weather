package com.opweather.api.nodes;

public abstract class AbstractWeather {
    private String mAreaCode;
    private String mAreaName;
    private String mDataSource;

    public AbstractWeather(String areaCode, String areaName, String dataSource) {
        notEmpty(areaCode, "AreaCode should not be empty!");
        notEmpty(dataSource, "Data source name should not be empty!");
        mAreaCode = areaCode;
        mAreaName = areaName;
        mDataSource = dataSource;
    }

    public String getAreaCode() {
        return mAreaCode;
    }

    public void setAreaCode(String areaCode) {
        mAreaCode = areaCode;
    }

    public String getAreaName() {
        return mAreaName;
    }

    public void setAreaName(String areaName) {
        mAreaName = areaName;
    }

    public String getDataSourceName() {
        return mDataSource;
    }

    public void setDataSource(String dataSource) {
        mDataSource = dataSource;
    }

    private void notEmpty(String string, String msg) {
        if (string == null || string.length() == 0) {
            throw new IllegalArgumentException(msg);
        }
    }
}
