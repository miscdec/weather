package com.opweather.bean;

public class GeoPosition {
    Measurement Elevation;
    double Latitude;
    double Longitude;

    public GeoPosition() {
        this.Latitude = 0.0d;
        this.Longitude = 0.0d;
        this.Elevation = new Measurement();
    }

    public double getLatitude() {
        return this.Latitude;
    }

    public double getLongitude() {
        return this.Longitude;
    }

    public Measurement getElevation() {
        return this.Elevation;
    }
}
