package com.opweather.api;

import okhttp3.MediaType;
import okhttp3.ResponseBody;
import okio.BufferedSource;

public class DownloadProgressResponseBody extends ResponseBody {

    private ResponseBody responseBody;
    private BufferedSource bufferedSource;

    public DownloadProgressResponseBody(ResponseBody responseBody) {
        this.responseBody = responseBody;
    }

    @Override
    public MediaType contentType() {
        return null;
    }

    @Override
    public long contentLength() {
        return 0;
    }

    @Override
    public BufferedSource source() {
        return null;
    }
}
