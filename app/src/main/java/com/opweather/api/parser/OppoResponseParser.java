package com.opweather.api.parser;

import com.opweather.api.WeatherRequest;
import com.opweather.api.nodes.RootWeather;
import com.opweather.api.WeatherException;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.InputStream;

public abstract class OppoResponseParser implements ResponseParser {
    private static final String CONTENT_ENCODE = "gb2312";
    protected final WeatherRequest mRequest;

    public abstract RootWeather parseAqi(byte[] bArr) throws ParseException;

    public abstract RootWeather parseCurrent(byte[] bArr) throws ParseException;

    public abstract RootWeather parseDailyForecasts(byte[] bArr) throws ParseException;

    public abstract RootWeather parseHourForecasts(byte[] bArr) throws ParseException;

    public OppoResponseParser(WeatherRequest request) {
        this.mRequest = request;
    }

    protected XmlPullParser getXmlPullParser(InputStream stream) throws WeatherException, XmlPullParserException {
        XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
        factory.setNamespaceAware(true);
        XmlPullParser xpp = factory.newPullParser();
        xpp.setInput(stream, CONTENT_ENCODE);
        return xpp;
    }
}
