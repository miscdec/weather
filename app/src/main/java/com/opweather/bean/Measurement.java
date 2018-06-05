package com.opweather.bean;

public class Measurement {
    Units Imperial;
    Units Metric;

    public Measurement() {
        this.Metric = new Units();
        this.Imperial = new Units();
    }

    public Units getMetric() {
        return this.Metric;
    }

    public Units getInperial() {
        return this.Imperial;
    }
}
