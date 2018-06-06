package com.opweather.api.nodes;

public class AccuWind extends Wind {
    private final double mSpeed;

    public AccuWind(String areaCode, String dataSource, Direction direction, double speed) {
        super(areaCode, dataSource, direction);
        mSpeed = speed;
    }

    public AccuWind(String areaCode, String areaName, String dataSource, Direction direction, double speed) {
        super(areaCode, areaName, dataSource, direction);
        mSpeed = speed;
    }

    public double getSpeed() {
        return this.mSpeed;
    }
}
