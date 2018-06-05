package com.opweather.util;
import com.opweather.widget.openglbase.RainSurfaceView;

import java.net.URLEncoder;
import java.security.InvalidKeyException;
import java.util.Locale;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

public class EncodeUtil {
    private static final char[] encodeTable;
    private static final char last2byte;
    private static final char last4byte;
    private static final char last6byte;
    private static final char lead2byte;
    private static final char lead4byte;
    private static final char lead6byte;

    static {
        last2byte = (char) Integer.parseInt("00000011", RainSurfaceView.RAIN_LEVEL_SHOWER);
        last4byte = (char) Integer.parseInt("00001111", RainSurfaceView.RAIN_LEVEL_SHOWER);
        last6byte = (char) Integer.parseInt("00111111", RainSurfaceView.RAIN_LEVEL_SHOWER);
        lead6byte = (char) Integer.parseInt("11111100", RainSurfaceView.RAIN_LEVEL_SHOWER);
        lead4byte = (char) Integer.parseInt("11110000", RainSurfaceView.RAIN_LEVEL_SHOWER);
        lead2byte = (char) Integer.parseInt("11000000", RainSurfaceView.RAIN_LEVEL_SHOWER);
        encodeTable = new char[]{'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'I', 'J', 'K', 'L', 'M', 'N', 'O', 'P', 'Q', 'R', 'S', 'T', 'U', 'V', 'W', 'X', 'Y', 'Z', 'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'i', 'j', 'k', 'l', 'm', 'n', 'o', 'p', 'q', 'r', 's', 't', 'u', 'v', 'w', 'x', 'y', 'z', '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', '+', '/'};
    }

    public static String standardURLEncoder(String data, String key) {
        String urlEncoder = StringUtils.EMPTY_STRING;
        try {
            Mac mac = Mac.getInstance("HmacSHA1");
            mac.init(new SecretKeySpec(key.getBytes(), "HmacSHA1"));
            byte[] byteHMAC = mac.doFinal(data.getBytes());
            if (byteHMAC == null) {
                return urlEncoder;
            }
            String oauth = encode(byteHMAC);
            return oauth != null ? URLEncoder.encode(oauth, "utf8") : urlEncoder;
        } catch (InvalidKeyException e1) {
            e1.printStackTrace();
            return urlEncoder;
        } catch (Exception e2) {
            e2.printStackTrace();
            return urlEncoder;
        }
    }

    public static String encode(byte[] from) {
        int i;
        StringBuilder to = new StringBuilder(((int) (((double) from.length) * 1.34d)) + 3);
        int num = 0;
        char c = '\u0000';
        for (i = 0; i < from.length; i++) {
            num %= 8;
            while (num < 8) {
                switch (num) {
                    case RainSurfaceView.RAIN_LEVEL_DRIZZLE:
                        c = (char) (((char) (from[i] & lead6byte)) >>> 2);
                        break;
                    case RainSurfaceView.RAIN_LEVEL_SHOWER:
                        c = (char) (from[i] & last6byte);
                        break;
                    case RainSurfaceView.RAIN_LEVEL_RAINSTORM:
                        c = (char) (((char) (from[i] & last4byte)) << 2);
                        if (i + 1 < from.length) {
                            c = (char) (((from[i + 1] & lead2byte) >>> 6) | c);
                        }
                    case 6:
                        c = (char) (((char) (from[i] & last2byte)) << 4);
                        if (i + 1 < from.length) {
                            c = (char) (((from[i + 1] & lead4byte) >>> 4) | c);
                        }
                    default:
                        break;
                }
                to.append(encodeTable[c]);
                num += 6;
            }
        }
        if (to.length() % 4 != 0) {
            for (i = 4 - (to.length() % 4); i > 0; i--) {
                to.append("=");
            }
        }
        return to.toString();
    }

    public static String getHmacSha1(String data, String key) {
        try {
            return standardURLEncoder(data, key);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public static String androidLocaleToAccuFormat(Locale locale) {
        return locale.getLanguage().concat("-").concat(locale.getCountry());
    }
}
