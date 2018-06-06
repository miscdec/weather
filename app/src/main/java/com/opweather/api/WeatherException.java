package com.opweather.api;

public class WeatherException extends Exception {
    private static final long serialVersionUID = -6316970406795805454L;

    public WeatherException(String detailMessage) {
        super(detailMessage);
    }

    public WeatherException(Throwable throwable) {
        super(throwable);
    }

    public WeatherException(String detailMessage, Throwable throwable) {
        super(detailMessage, throwable);
    }
}
