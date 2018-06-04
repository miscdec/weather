package com.opweather.ui;

import android.app.Activity;
import android.os.Bundle;
import android.os.HandlerThread;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import com.opweather.R;
import com.opweather.db.CityWeatherDB;
import com.opweather.util.PermissionUtil;

public class CityListActivity extends Activity {
    private final String TAG = getClass().getSimpleName();
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.citylist_activity);
        initData();
        initUIView();
        initWidgetData();
    }

    private void initData() {
        PermissionUtil.requestPermission((Activity) this, new String[]{"android.permission.WRITE_EXTERNAL_STORAGE",
                "android.permission.ACCESS_COARSE_LOCATION"}, (int) PermissionUtil.ALL_PERMISSION_REQUEST);
        mCityWeatherDB = CityWeatherDB.getInstance(getApplicationContext());
        mCursor = mCityWeatherDB.getAllCities();
        mCityListAdapter = new CityListAdapter(getApplicationContext(), mCursor, false);
        mCityListAdapter.setOnDefaulatChangeListener(this);
        HandlerThread handlerThread = new HandlerThread("handler_hread");
        handlerThread.start();
        mCityListHandler = new CityListHandler(handlerThread.getLooper(), this);
        if (mCursor.getCount() == 0) {
            findViewById(R.id.no_city_view).setVisibility(View.VISIBLE);
        }
    }

    private void initUIView() {


    }

    private void initWidgetData() {

    }
}
