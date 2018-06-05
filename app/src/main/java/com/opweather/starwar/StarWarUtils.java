package com.opweather.starwar;

public class StarWarUtils {
    public static final String STATWAR_CUST_FLAG = "/sys/module/param_read_write/parameters/cust_flag";
    public static final String STATWAR_KEY1 = "ahch-to";
    public static final String STATWAR_KEY2 = "ahchto";
    public static final String STATWAR_KEY3 = "ahch to";
    public static final String STATWAR_KEY_WORD = "Ahch-To";
    public static final String STATWAR_NAME = "STAT WAR";
    public static String value;

    static {
        value = "-1";
    }

    public static boolean isShow(String key) {
        return key.toLowerCase().trim().equals(STATWAR_KEY1) || key.toLowerCase().trim().equals(STATWAR_KEY2) || key
                .toLowerCase().trim().equals(STATWAR_KEY3);
    }

    public static boolean isStarWar() {
        return false;
    }
}
