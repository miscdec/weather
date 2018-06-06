package com.opweather.api.helper;

import com.opweather.api.WeatherException;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;

public final class IOUtils {
    static final /* synthetic */ boolean $assertionsDisabled = !IOUtils.class.desiredAssertionStatus();
    private static final int DEFAULT_BUFFER_SIZE = 4096;
    private static final int EOF = -1;
    private IOUtils() {
    }

    public static byte[] toByteArray(InputStream input) throws IOException {
        byte[] buffer = new byte[4096];
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        while (true) {
            int n = input.read(buffer);
            if (-1 == n) {
                return output.toByteArray();
            }
            output.write(buffer, 0, n);
        }
    }

    public static byte[] emptyByteArray() {
        return new byte[0];
    }

    public static String byteArrayToString(byte[] data, String charset) throws UnsupportedEncodingException {
        return data == null ? null : new String(data, charset);
    }

    public static InputStream getInputStreamFromByteArray(byte[] data) throws WeatherException {
        if ($assertionsDisabled || data != null) {
            try {
                return new ByteArrayInputStream(data);
            } catch (Exception e) {
                throw new WeatherException(e.getMessage());
            }
        }
        throw new AssertionError();
    }

    public static boolean write(byte[] data, OutputStream output) {
        boolean result = $assertionsDisabled;
        if (data == null) {
            return $assertionsDisabled;
        }
        try {
            output.write(data);
            result = true;
            return true;
        } catch (IOException e) {
            e.printStackTrace();
            return result;
        }
    }
}
