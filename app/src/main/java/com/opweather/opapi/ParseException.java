package com.opweather.opapi;

public class ParseException extends WeatherException {
    private static final long serialVersionUID = 7693674526831230463L;

    public ParseException(String detailMessage) {
        super(detailMessage);
    }
}

