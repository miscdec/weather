package com.opweather.ui;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Looper;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;

import com.opweather.R;
import com.opweather.adapter.CityListAdapter;
import com.opweather.bean.CityData;
import com.opweather.constants.GlobalConfig;
import com.opweather.db.CityWeatherDB;
import com.opweather.db.CityWeatherDBHelper;
import com.opweather.util.NumberUtils;
import com.opweather.util.PermissionUtil;
import com.opweather.util.PreferenceUtils;
import com.opweather.widget.swipelistview.BaseSwipeListViewListener;
import com.opweather.widget.swipelistview.SwipeListView;
import com.opweather.widget.widget.WidgetHelper;

public class CityListActivity extends BaseBarActivity implements CityListAdapter.OnDefaulatChangeListener{
    private final String TAG = getClass().getSimpleName();
    public static final String DEFAULT_CITY = "default_city";
    public static final String INTENT_SEARCH_CITY = "search_city";
    private SwipeListView cityListView;
    private int mAppWidgetId;
    private CityListAdapter mCityListAdapter;
    private CityListHandler mCityListHandler;
    private CityWeatherDB mCityWeatherDB;
    private Cursor mCursor;
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
        setBarTitle(R.string.city_list_select_city);
        cityListView = findViewById(R.id.cityListView);
        cityListView.setAdapter(mCityListAdapter);
        cityListView.setSwipeListViewListener(new BaseSwipeListViewListener(){
            @Override
            public void onClickFrontView(int position) {
                super.onClickFrontView(position);
                Cursor cursor = CityListActivity.this.mCityWeatherDB.getAllCities();
                Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                if (!(mAppWidgetId == -1 || cursor == null || cursor.getCount() <= position)) {
                    if (cursor.moveToPosition(position)) {
                        int locationId = cursor.getInt(cursor.getColumnIndex(CityWeatherDBHelper.WeatherEntry.COLUMN_1_LOCATION_ID));
                        PreferenceUtils.commitInt(CityListActivity.this, WidgetHelper.WIDGET_ID_PREFIX +
                                CityListActivity.this.mAppWidgetId, locationId);
                        PreferenceUtils.commitInt(CityListActivity.this, WidgetHelper.WIDGET_ID_PREFIX + String
                                .valueOf(locationId), CityListActivity.this.mAppWidgetId);
                        CityData data = CityListActivity.this.mCityWeatherDB.getCityFromLocationId(locationId);
                        if (data != null) {
                            WidgetHelper.getInstance(CityListActivity.this).setCityByID(CityListActivity.this, data);
                            WidgetHelper.getInstance(CityListActivity.this).updateWidgetById(CityListActivity.this
                                    .mAppWidgetId, true);
                            intent.putExtra("appWidgetId", CityListActivity.this.mAppWidgetId);
                        } else {
                            return;
                        }
                    }
                    return;
                }
                intent.putExtra(GlobalConfig.INTENT_EXTRA_CITY_INDEX, position);
                intent.setAction("android.intent.action.MAIN");
                CityListActivity.this.setResult(-1, intent);
                CityListActivity.this.finish();
                CityListActivity.this.overridePendingTransition(R.anim.alpha_in_listclick, R.anim.alpha_out_listclick);
            }

            @Override
            public void onDismiss(int[] reverseSortedPositions) {
                super.onDismiss(reverseSortedPositions);
                long[] preDelete = new long[reverseSortedPositions.length];
                for (int i = 0; i < reverseSortedPositions.length; i++) {
                    Cursor cursor = (Cursor) CityListActivity.this.mCityListAdapter.getItem(reverseSortedPositions[i]);
                    if (cursor != null) {
                        preDelete[i] = cursor.getLong(cursor.getColumnIndex("_id"));
                    }
                }
                for (long delId : preDelete) {
                    if (0 != delId) {
                        CityListActivity.this.mCityListAdapter.delete(delId);
                        Message msg = new Message();
                        msg.what = NumberUtils.parseInt(delId);
                        msg.obj = Long.valueOf(delId);
                        CityListActivity.this.mCityListHandler.sendMessage(msg);
                    }
                }
            }
        });
        cityListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                Intent intent = new Intent();
                intent.putExtra(GlobalConfig.INTENT_EXTRA_CITY_INDEX, position);
                setResult(-1, intent);
                finish();
                overridePendingTransition(R.anim.alpha_in_listclick, R.anim.alpha_out_listclick);
            }
        });
        findViewById(R.id.btn_add).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(CityListActivity.this, CitySearchActivity.class);
                intent.putExtra(INTENT_SEARCH_CITY, CityListActivity.this.cityListView.getCount());
                CityListActivity.this.startActivityForResult(intent, 1);
            }
        });

    }

    private void initWidgetData() {

    }

    @Override
    public void onChanged(int i) {

    }

    public class CityListHandler extends Handler {
        public static final int MESSAGE_DELETE_COMPLETE = -1;
        private Context context;

        public CityListHandler(Looper looper, Context context) {
            super(looper);
            this.context = context;
        }

        public void handleMessage(Message msg) {
            if (!hasMessages(msg.what)) {
                CityWeatherDB.getInstance(context).deleteCity((Long) msg.obj);
            }
        }
    }
}
