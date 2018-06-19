package com.opweather.adapter;

import android.content.Context;
import android.database.Cursor;
import android.graphics.Color;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;

import com.opweather.R;
import com.opweather.bean.CityData;
import com.opweather.db.ChinaCityDB;
import com.opweather.db.CityWeatherDB;
import com.opweather.db.CityWeatherDBHelper;
import com.opweather.db.CityWeatherDBHelper.CityListEntry;
import com.opweather.api.nodes.RootWeather;
import com.opweather.api.WeatherException;
import com.opweather.util.StringUtils;
import com.opweather.util.SystemSetting;
import com.opweather.util.WeatherClientProxy;
import com.opweather.util.WeatherLog;
import com.opweather.util.WeatherResHelper;
import com.opweather.widget.WeatherTemperatureView;

import java.lang.ref.WeakReference;
import java.util.Map;
import java.util.WeakHashMap;
import java.util.concurrent.ConcurrentHashMap;

public class CityListAdapter extends IgnorCursorAdapter implements AnimationListener {
    private static final int NO_TEMP_DATA_FLAG = -2000;
    private boolean isWidgeMode;
    private boolean mCanScroll = false;
    private CityWeatherDB.CityListDBListener mCityListDBListener = new CityWeatherDB.CityListDBListener() {
        @Override
        public void onCityAdded(long j) {
            requery();
        }

        @Override
        public void onCityDeleted(long j) {
            requery();
        }

        @Override
        public void onCityUpdated(long j) {
            requery();
        }
    };
    private CityWeatherDB mCityWeatherDB;
    private Context mContext;
    private Map<String, Boolean> mLoadItems;
    private OnDefaulatChangeListener mOnDefaulatChangeListener;
    private Map<String, RootWeather> mWeatherMap;

    public class ItemHolder {
        String cityDisplayName;
        TextView cityNameView;
        TextView cityTempType;
        TextView cityTempView;
        ImageView cityThemeView;
        ImageView currentLocationView;
        View homeBtnView;
        ImageView homeView;
        private WeakReference<WeatherWorkerClient> workerClient;

        public void setWorkerClient(WeatherWorkerClient client) {
            workerClient = new WeakReference<>(client);
        }

        public WeatherWorkerClient getWorkerClient() {
            return workerClient != null ? workerClient.get() : null;
        }
    }

    public interface OnDefaulatChangeListener {
        void onChanged(int i);
    }

    private class WeatherWorkerClient {
        private final CityData mCityData;
        private final WeakReference<ItemHolder> mItemHolder;

        public WeatherWorkerClient(ItemHolder item, CityData cityData) {
            mItemHolder = new WeakReference<>(item);
            mCityData = cityData;
        }

        public void loadWeather() {
            new WeatherClientProxy(mContext).setCacheMode(WeatherClientProxy.CacheMode.LOAD_CACHE_ELSE_NETWORK)
                    .requestWeatherInfo(mCityData, new WeatherClientProxy.OnResponseListener() {
                        @Override
                        public void onNetworkResponse(RootWeather response) {
                            onResponse(response);
                        }

                        @Override
                        public void onErrorResponse(WeatherException error) {
                            onResponse(null);
                        }

                        @Override
                        public void onCacheResponse(RootWeather response) {
                            onResponse(response);
                        }
                    });
        }

        private void onResponse(RootWeather response) {
            if (response != null) {
                mWeatherMap.put(response.getAreaCode(), response);
            }
            ItemHolder holder = mItemHolder.get();
            if (holder != null && this == holder.getWorkerClient()) {
                updateView(holder, response, mCityData);
            }
            String locationId = mCityData.getLocationId();
            if (!TextUtils.isEmpty(locationId)) {
                mLoadItems.remove(locationId);
            }
        }
    }

    public CityListAdapter(Context context, Cursor cursor, boolean autoRequery) {
        super(context, cursor, autoRequery);
        mContext = context;
        mLoadItems = new ConcurrentHashMap<>();
        mWeatherMap = new WeakHashMap<>();
        mCityWeatherDB = CityWeatherDB.getInstance(context);
        mCityWeatherDB.addDataChangeListener(mCityListDBListener);
    }

    public void onDestroy() {
        if (mCityWeatherDB != null) {
            mCityWeatherDB.removeDataChangeListener(mCityListDBListener);
            mCityWeatherDB = null;
        }
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        return LayoutInflater.from(mContext).inflate(R.layout.citylist_item, parent, false);
    }

    private void setNewHolder(View view, ItemHolder holder) {
        holder.cityNameView = view.findViewById(R.id.cityName);
        holder.cityTempView = view.findViewById(R.id.cityTemp);
        holder.cityTempType = view.findViewById(R.id.weather_type);
        holder.cityThemeView = view.findViewById(R.id.cityTheme);
        holder.currentLocationView = view.findViewById(R.id.current_location);
        holder.homeView = view.findViewById(R.id.img_city_home);
        holder.homeBtnView = view.findViewById(R.id.btn_city_home);
    }

    public void requery() {
        clearDelete();
        if (mCursor != null) {
            mCursor.requery();
        }
        notifyDataSetChanged();
    }

    private CityData getCityFromCoursor(Cursor cursor) {
        int provider = cursor.getInt(cursor.getColumnIndex(CityListEntry.COLUMN_1_PROVIDER));
        String cityName = cursor.getString(cursor.getColumnIndex(CityListEntry.COLUMN_2_NAME));
        String cityDisplayName = cursor.getString(cursor.getColumnIndex(CityListEntry.COLUMN_3_DISPLAY_NAME));
        String cityLocationId = cursor.getString(cursor.getColumnIndex(CityWeatherDBHelper.WeatherEntry
                .COLUMN_1_LOCATION_ID));
        CityData city = new CityData();
        city.setProvider(provider);
        city.setName(cityName);
        city.setLocalName(cityDisplayName);
        city.setLocationId(cityLocationId);
        return city;
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        if (view != null && cursor != null) {
            view.setTag(cursor.getPosition());
            final ItemHolder itemHolder = new ItemHolder();
            setNewHolder(view, itemHolder);
            long cityId = cursor.getLong(0);
            int orderId = cursor.getInt(9);
            CityData cityData = getCityFromCoursor(cursor);
            String cityLocationId = cityData.getLocationId();
            if (TextUtils.isEmpty(cityLocationId)) {
                updateView(itemHolder, cityData);
                return;
            }
            itemHolder.cityDisplayName = cityData.getLocalName();
            if (0 == cityId) {
                itemHolder.currentLocationView.setVisibility(View.VISIBLE);
            } else {
                itemHolder.currentLocationView.setVisibility(View.GONE);
            }
            RootWeather weather = mWeatherMap.get(cityLocationId);
            updateView(itemHolder, weather, cityData);
            if (weather == null && mLoadItems.get(cityLocationId) == null) {
                mLoadItems.put(cityLocationId, true);
                WeatherWorkerClient client = new WeatherWorkerClient(itemHolder, cityData);
                itemHolder.setWorkerClient(client);
                client.loadWeather();
            }
            if (mCanScroll && cursor.getPosition() == cursor.getCount() - 1) {
                view.clearAnimation();
                Animation animation = AnimationUtils.loadAnimation(mContext, R.anim.spring_from_bottom);
                animation.setAnimationListener(this);
                view.startAnimation(animation);
            }
            if (orderId == -1) {
                itemHolder.homeView.setImageResource(R.mipmap.btn_home_enable);
            } else if (cityId == 0 && orderId == 0 && cursor.getPosition() == 0) {
                itemHolder.homeView.setImageResource(R.mipmap.btn_home_enable);
            }
            if (isWidgeMode()) {
                itemHolder.homeBtnView.setVisibility(View.GONE);
                itemHolder.homeBtnView.setClickable(false);
                return;
            }
            itemHolder.homeBtnView.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View view) {
                    int position = -1;
                    if (view.getTag() != null) {
                        position = (int) view.getTag();
                    }
                    if (position != -1 && mOnDefaulatChangeListener != null && !isDefaultCity(position)) {
                        mOnDefaulatChangeListener.onChanged(position);
                        itemHolder.homeView.setImageResource(R.mipmap.btn_home_enable);
                    }
                }
            });
        }
    }

    public boolean isWidgeMode() {
        return isWidgeMode;
    }

    public void setWidgeMode(boolean widgeMode) {
        isWidgeMode = widgeMode;
    }

    @Override
    public int getItemViewType(int position) {
        return (isLocationCity(position) || isDefaultCity(position)) ? -1 : super.getItemViewType(position);
    }

    @Override
    public int getCount() {
        return super.getCount();
    }

    private boolean isDay(RootWeather data, CityData cityData) {
        try {
            return cityData.isDay(data);
        } catch (IllegalAccessException e) {
            e.printStackTrace();
            return true;
        }
    }

    private void updateView(ItemHolder holder, RootWeather data, CityData cityData) {
        TextView cityNameView = holder.cityNameView;
        TextView cityTempView = holder.cityTempView;
        TextView cityTempType = holder.cityTempType;
        ImageView cityThemeView = holder.cityThemeView;
        ImageView currentLocationView = holder.currentLocationView;
        cityNameView.setText(holder.cityDisplayName);
        cityNameView.setTextColor(mContext.getResources().getColor(R.color.white));
        cityThemeView.setImageResource(R.mipmap.bkg_sunny);
        cityTempView.setText("--");
        if (data != null) {
            float f;
            boolean cOrf = SystemSetting.getTemperature(mContext);
            String tempUnit = "°";
            int currentTemp = data.getTodayCurrentTemp();
            if (cOrf) {
                f = (float) currentTemp;
            } else {
                f = SystemSetting.celsiusToFahrenheit((float) currentTemp);
            }
            int curTemp = (int) f;
            String str = "--";
            if (curTemp < NO_TEMP_DATA_FLAG) {
                str = "--" + tempUnit;
            } else {
                str = curTemp + tempUnit;
            }
            cityTempView.setText(str);
            cityTempType.setText(data.getCurrentWeatherText(mContext));
            if (cityData != null) {
                ChinaCityDB.openCityDB(mContext).getCityTimeZone(cityData.getLocationId());
            }
            boolean isDay = isDay(data, cityData);
            int descriptionId = WeatherResHelper.weatherToResID(mContext, data.getCurrentWeatherId());
            cityThemeView.setImageResource(WeatherResHelper.getWeatherListitemBkgResID(descriptionId, isDay));
            if (descriptionId == 1003 && isDay) {
                currentLocationView.setImageResource(R.drawable.icon_gps_black);
                cityNameView.setTextColor(Color.parseColor("#757575"));
                return;
            }
            currentLocationView.setImageResource(R.drawable.icon_gps);
            cityNameView.setTextColor(Color.parseColor(WeatherTemperatureView.DEFAULT_HTEMP_POINT_COLOR));
        }
    }

    private void updateView(ItemHolder holder, CityData city) {
        TextView cityNameView = holder.cityNameView;
        TextView cityTempView = holder.cityTempView;
        TextView cityTempType = holder.cityTempType;
        ImageView cityThemeView = holder.cityThemeView;
        ImageView imageView = holder.currentLocationView;
        cityNameView.setTextColor(mContext.getResources().getColor(R.color.white));
        cityTempType.setText(mContext.getString(R.string.default_weather));
        cityThemeView.setImageResource(R.mipmap.bkg_sunny);
        cityTempView.setText("--");
        if (city == null) {
            cityNameView.setText(mContext.getString(R.string.current_location));
        } else {
            cityNameView.setText(city.getName());
        }
    }

    public void setCanScroll(boolean canScroll) {
        mCanScroll = canScroll;
    }

    public void onAnimationStart(Animation animation) {
    }

    public void onAnimationEnd(Animation animation) {
        mCanScroll = false;
    }

    public void onAnimationRepeat(Animation animation) {
    }

    public void setOnDefaulatChangeListener(OnDefaulatChangeListener onDefaulatChangeListener) {
        mOnDefaulatChangeListener = onDefaulatChangeListener;
    }

    public boolean isDefaultCity(int position) {
        Cursor cursor = getCursor();
        String orderId = StringUtils.EMPTY_STRING;
        if (cursor.getCount() > position) {
            cursor.moveToPosition(position);
            orderId = cursor.getString(9);
        }
        return "-1".equals(orderId);
    }

    public boolean isLocationCity(int position) {
        Cursor cursor = getCursor();
        String orderId = StringUtils.EMPTY_STRING;
        if (cursor.getCount() > position) {
            cursor.moveToPosition(position);
            orderId = cursor.getString(0);
        }
        return "0".equals(orderId);
    }
}
