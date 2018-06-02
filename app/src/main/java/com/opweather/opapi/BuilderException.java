package com.opweather.opapi;

import com.opweather.api.parser.ParseException;

public class BuilderException extends ParseException {
    private static final long serialVersionUID = -4617647961185389355L;

    public BuilderException(String detailMessage) {
        super(detailMessage);
    }
}
