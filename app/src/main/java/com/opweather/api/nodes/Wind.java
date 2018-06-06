package com.opweather.api.nodes;

import android.content.Context;

import com.opweather.R;
import com.opweather.api.helper.NumberUtils;
import com.opweather.util.StringUtils;

import java.util.Locale;

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

    public static Direction getDirectionFromAccu(String text) {
        String toLowerCase = text.toLowerCase(Locale.ENGLISH);
        if (toLowerCase.equals("e")){
            return Direction.E;
        }else if (toLowerCase.equals("n")){
            return Direction.N;
        }else if (toLowerCase.equals("s")){
            return Direction.S;
        }else if (toLowerCase.equals("w")){
            return Direction.W;
        }else if (toLowerCase.equals("ne")){
            return Direction.NE;
        }else if (toLowerCase.equals("nw")){
            return Direction.NW;
        }else if (toLowerCase.equals("se")){
            return Direction.SE;
        }else if (toLowerCase.equals("sw")){
            return Direction.SW;
        }else if (toLowerCase.equals("ene")){
            return Direction.ENE;
        }else if (toLowerCase.equals("ese")){
            return Direction.ESE;
        }else if (toLowerCase.equals("nne")){
            return Direction.NNE;
        }else if (toLowerCase.equals("nwn")){
            return Direction.NWN;
        }else if (toLowerCase.equals("sse")){
            return Direction.SSE;
        }else if (toLowerCase.equals("ssw")){
            return Direction.SSW;
        }else if (toLowerCase.equals("wnw")){
            return Direction.WNW;
        }else if (toLowerCase.equals("wsw")){
            return Direction.WSW;
        }else {
            return Direction.NA;
        }
    }

    public static Direction getDirectionFromOppo(String text) {
        String toLowerCase = text.toLowerCase(Locale.ENGLISH);
        if ("旋转不定".equals(toLowerCase) || "旋转风".equals(toLowerCase)){
            return Direction.RW;
        }else if (toLowerCase.equals("北风")){
            return Direction.N;
        }else if (toLowerCase.equals("东北风")){
            return Direction.NE;
        }else if (toLowerCase.equals("东风")){
            return Direction.E;
        }else if (toLowerCase.equals("东南风")){
            return Direction.SE;
        }else if (toLowerCase.equals("南风")){
            return Direction.S;
        }else if (toLowerCase.equals("西南风")){
            return Direction.SW;
        }else if (toLowerCase.equals("西风")){
            return Direction.W;
        }else if (toLowerCase.equals("西北风")){
            return Direction.NW;
        }else {
            return Direction.NA;
        }
    }

    public static Direction getDirectionFromSwa(String text) {
        switch (Integer.valueOf(text)) {
            case 0:
                return Direction.NA;
            case 1:
                return Direction.NE;
            case 2:
                return Direction.E;
            case 3:
                return Direction.SE;
            case 4:
                return Direction.S;
            case 5:
                return Direction.SW;
            case 6:
                return Direction.W;
            case 7:
                return Direction.NW;
            case 8:
                return Direction.N;
            case 9:
                return Direction.RW;
            default:
                return Direction.NA;
        }
    }

    public Wind(String areaCode, String dataSource, Direction direction) {
        this(areaCode, null, dataSource, direction);
    }

    public Wind(String areaCode, String areaName, String dataSource, Direction direction) {
        super(areaCode, areaName, dataSource);
        this.mDirection = direction;
    }

    public String getWeatherName() {
        return "Wind";
    }

    public Direction getDirection() {
        return this.mDirection;
    }

    public double getSpeed() {
        return NumberUtils.NAN_DOUBLE;
    }

    public String getWindPower() {
        return StringUtils.EMPTY_STRING;
    }

    public String getWindPower(Context context) {
        return StringUtils.EMPTY_STRING;
    }
}
