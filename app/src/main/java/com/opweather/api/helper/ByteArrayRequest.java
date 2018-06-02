package com.opweather.api.helper;

import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.toolbox.HttpHeaderParser;
import com.opweather.api.helper.NetworkHelper.ResponseListener;

public class ByteArrayRequest extends Request<byte[]> {

    private String contentCharset;
    private boolean mIsOppo;
    private ResponseListener mListener;

    public ByteArrayRequest(String url, boolean isOppo, ResponseListener listener, Response
            .ErrorListener errorListener) {
        super(0, url, errorListener);
        mListener = listener;
        mIsOppo = isOppo;
    }

    protected void deliverResponse(byte[] response) {
        mListener.onResponse(response, contentCharset);
    }

    protected Response<byte[]> parseNetworkResponse(NetworkResponse response) {
        contentCharset = HttpHeaderParser.parseCharset(response.headers);
        return mIsOppo ? Response.success(response.data, OppoHttpHeaderParser
                .parseCacheHeaders(response, OppoHttpHeaderParser.CACHE_TIME)) : Response.success
                (response.data, HttpHeaderParser.parseCacheHeaders(response));
    }
}
