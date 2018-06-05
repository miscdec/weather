package com.opweather.util;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.NetworkInfo.State;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.regex.Pattern;

public class NetUtil {
    private static final String ACCU_API_URL = "http://api.accuweather.com/";
    private static final String LAUNG_REGEX = "(?<=\\blanguage=\\b\\w{0,2})-\\w*";
    public static final int NETWORK_BLUETOOTH = 3;
    public static final int NETWORK_MOBILE = 2;
    public static final int NETWORK_NONE = 0;
    public static final int NETWORK_WIFI = 1;

    public static boolean isNetworkAvailable(Context context) {
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        if (cm == null) {
            return false;
        }
        NetworkInfo[] info = cm.getAllNetworkInfo();
        if (info == null) {
            return false;
        }
        int length = info.length;
        for (int i = 0; i < length; i++) {
            if (info[i].getState() == State.CONNECTED) {
                return true;
            }
        }
        return false;
    }

    public static int getNetworkState(Context context) {
        ConnectivityManager connManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connManager != null) {
            State state = connManager.getNetworkInfo(NETWORK_WIFI).getState();
            if ((state != null && state == State.CONNECTED) || state == State.CONNECTING) {
                return NETWORK_WIFI;
            }
            state = connManager.getNetworkInfo(NETWORK_NONE).getState();
            if (state == State.CONNECTED || state == State.CONNECTING) {
                return NETWORK_MOBILE;
            }
            state = connManager.getNetworkInfo(7).getState();
            if (state == State.CONNECTED || state == State.CONNECTING) {
                return NETWORK_BLUETOOTH;
            }
        }
        return 0;
    }

    public static HttpURLConnection getHttpURLConnection(String url) throws IOException {
        HttpURLConnection urlConnection = (HttpURLConnection) new URL(url).openConnection();
        urlConnection.setRequestMethod("GET");
        urlConnection.setDoInput(true);
        urlConnection.setConnectTimeout(5000);
        urlConnection.setReadTimeout(5000);
        return urlConnection;
    }

    public static String httpGet(String url) throws Exception {
        HttpURLConnection urlConnection = null;
        try {
            String result;
            urlConnection = getHttpURLConnection(url);
            if (urlConnection.getResponseCode() == 200) {
                result = getResult(urlConnection);
            } else if (!url.contains(ACCU_API_URL) || urlConnection.getResponseCode() != 400) {
                throw new Exception(urlConnection.getResponseMessage());
            } else if (Pattern.compile(LAUNG_REGEX).matcher(url).find()) {
                urlConnection = getHttpURLConnection(url.replaceAll(LAUNG_REGEX, StringUtils.EMPTY_STRING));
                result = getResult(urlConnection);
            } else {
                throw new Exception(urlConnection.getResponseMessage());
            }
            if (urlConnection != null) {
                urlConnection.disconnect();
            }
            return result;
        } catch (Exception e) {
            e.printStackTrace();
            throw new Exception(e.getMessage());
        } catch (Throwable th) {
            if (urlConnection != null) {
                urlConnection.disconnect();
            }
        }
        return null;
    }

    private static String getResult(HttpURLConnection urlConnection) throws Exception {
        InputStream in = null;
        String result = null;
        try {
            if (urlConnection.getResponseCode() == 200) {
                in = urlConnection.getInputStream();
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                byte[] buffer = new byte[1024];
                while (true) {
                    int len = in.read(buffer);
                    if (len == -1) {
                        break;
                    }
                    baos.write(buffer, NETWORK_NONE, len);
                }
                in.close();
                baos.close();
                result = new String(baos.toByteArray(), "utf8");
            }
            if (in != null) {
                in.close();
            }
            return result;
        } catch (IOException e) {
            e.printStackTrace();
            throw new Exception(e.getMessage());
        } catch (Throwable th) {
            if (in != null) {
                in.close();
            }
        }
        return null;
    }
}
