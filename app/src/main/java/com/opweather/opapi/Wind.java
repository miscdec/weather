package com.opweather.opapi;

import android.content.Context;

import com.opweather.R;

public class Wind extends AbstractWeather {
    private final Direction mDirection;
    public enum Direction {
        NA {
            public String text(Context context) {
                return context.getString(R.string.api_wind_direction_na);
            }
        },
        RW {
            public String text(Context context) {
                return context.getString(R.string.api_wind_direction_rw);
            }
        },
        N {
            public String text(Context context) {
                return context.getString(R.string.api_wind_direction_n);
            }
        },
        NNE {
            public String text(Context context) {
                return context.getString(R.string.api_wind_direction_nne);
            }
        },
        NE {
            public String text(Context context) {
                return context.getString(R.string.api_wind_direction_ne);
            }
        },
        ENE {
            public String text(Context context) {
                return context.getString(R.string.api_wind_direction_ene);
            }
        },
        E {
            public String text(Context context) {
                return context.getString(R.string.api_wind_direction_e);
            }
        },
        ESE {
            public String text(Context context) {
                return context.getString(R.string.api_wind_direction_ese);
            }
        },
        SE {
            public String text(Context context) {
                return context.getString(R.string.api_wind_direction_se);
            }
        },
        SSE {
            public String text(Context context) {
                return context.getString(R.string.api_wind_direction_sse);
            }
        },
        S {
            public String text(Context context) {
                return context.getString(R.string.api_wind_direction_s);
            }
        },
        SSW {
            public String text(Context context) {
                return context.getString(R.string.api_wind_direction_ssw);
            }
        },
        SW {
            public String text(Context context) {
                return context.getString(R.string.api_wind_direction_sw);
            }
        },
        WSW {
            public String text(Context context) {
                return context.getString(R.string.api_wind_direction_wsw);
            }
        },
        W {
            public String text(Context context) {
                return context.getString(R.string.api_wind_direction_w);
            }
        },
        WNW {
            public String text(Context context) {
                return context.getString(R.string.api_wind_direction_wnw);
            }
        },
        NW {
            public String text(Context context) {
                return context.getString(R.string.api_wind_direction_nw);
            }
        },
        NWN {
            public String text(Context context) {
                return context.getString(R.string.api_wind_direction_nwn);
            }
        };

        public abstract String text(Context context);
    }

    public Wind(String areaCode, String areaName, String dataSource, Direction direction) {
        super(areaCode, areaName, dataSource);
        mDirection = direction;
    }
}
