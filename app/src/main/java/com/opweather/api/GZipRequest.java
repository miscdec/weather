package com.opweather.api;

import com.android.volley.NetworkResponse;
import com.android.volley.ParseError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.toolbox.HttpHeaderParser;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.zip.GZIPInputStream;

public class GZipRequest extends Request<String> {
    private final Response.Listener<String> mListener;

    public GZipRequest(int method, String url, Response.Listener<String> listener,
                       Response.ErrorListener errorListener) {
        super(method, url, errorListener);
        mListener = listener;
    }

    /**
     * Creates a new GET request.
     *
     * @param url           URL to fetch the string at
     * @param listener      Listener to receive the String response
     * @param errorListener Error listener, or null to ignore errors
     */
    public GZipRequest(String url, Response.Listener<String> listener, Response.ErrorListener errorListener) {
        this(Method.GET, url, listener, errorListener);
    }

    @Override
    protected void deliverResponse(String response) {
        mListener.onResponse(response);
    }

    @Override
    protected Response<String> parseNetworkResponse(NetworkResponse paramNetworkResponse) {
        String str1 = "";
        try {
            GZIPInputStream gzipInputStream = new GZIPInputStream(
                    new ByteArrayInputStream(paramNetworkResponse.data));
            InputStreamReader inputStreamReader = new InputStreamReader(
                    gzipInputStream);
            BufferedReader bufferedReader = new BufferedReader(
                    inputStreamReader);
            while (true) {
                String str2 = bufferedReader.readLine();
                if (str2 == null)
                    break;
                str1 = str1 + str2;
            }
            inputStreamReader.close();
            bufferedReader.close();
            gzipInputStream.close();
            return Response.success(str1, HttpHeaderParser.parseCacheHeaders(paramNetworkResponse));
        } catch (IOException e) {
            return Response.error(new ParseError(e));
        }
    }
}
