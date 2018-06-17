package com.opweather.ui;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.HandlerThread;
import android.os.Message;
import android.support.annotation.NonNull;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Toast;

import com.opweather.R;
import com.opweather.adapter.CityListAdapter;
import com.opweather.adapter.CityListAdapter.OnDefaulatChangeListener;
import com.opweather.adapter.CityListSearchAdapter;
import com.opweather.bean.CityData;
import com.opweather.constants.GlobalConfig;
import com.opweather.db.CityWeatherDB;
import com.opweather.util.AlertUtils;
import com.opweather.util.NumberUtils;
import com.opweather.util.PermissionUtil;
import com.opweather.util.PreferenceUtils;
import com.opweather.widget.swipelistview.BaseSwipeListViewListener;
import com.opweather.widget.swipelistview.SwipeListView;
import com.opweather.widget.widget.WidgetHelper;


public class CityListActivity extends BaseBarActivity implements OnDefaulatChangeListener {
    public static final String DEFAULT_CITY = "default_city";
    public static final String INTENT_SEARCH_CITY = "search_city";
    private static final String TAG = CityListSearchAdapter.class.getSimpleName();
    private SwipeListView cityListView;
    private int mAppWidgetId = -1;
    private CityListAdapter mCityListAdapter;
    private CityListHandler mCityListHandler;
    private CityWeatherDB mCityWeatherDB;
    private Cursor mCursor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.citylist_activity);
        init();
    }

    private void init() {
        initData();
        initUIView();
        initWidgetData();
    }

    private void initWidgetData() {
        Bundle extras = getIntent().getExtras();
        if (extras != null && mCursor != null) {
            mCityListAdapter.setWidgeMode(true);
            cityListView.setSwipeMode(0);
            mAppWidgetId = extras.getInt("appWidgetId", 0);
        }
    }

    private void initData() {
        PermissionUtil.check(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE,
                Manifest.permission.ACCESS_COARSE_LOCATION}, PermissionUtil.ALL_PERMISSION_REQUEST);
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
        cityListView.setSwipeListViewListener(new BaseSwipeListViewListener() {
            @Override
            public void onClickFrontView(int position) {
                super.onClickFrontView(position);
                Cursor cursor = mCityWeatherDB.getAllCities();
                Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                if (!(mAppWidgetId == -1 || cursor == null || cursor.getCount() <= position)) {
                    if (cursor.moveToPosition(position)) {
                        int locationId = cursor.getInt(cursor.getColumnIndex("locationId"));
                        PreferenceUtils.commitInt(CityListActivity.this, WidgetHelper.WIDGET_ID_PREFIX +
                                mAppWidgetId, locationId);
                        PreferenceUtils.commitInt(CityListActivity.this, WidgetHelper.WIDGET_ID_PREFIX + String
                                .valueOf(locationId), mAppWidgetId);
                        CityData data = mCityWeatherDB.getCityFromLocationId(locationId);
                        if (data != null) {
                            WidgetHelper.getInstance(CityListActivity.this).setCityByID(CityListActivity.this, data);
                            WidgetHelper.getInstance(CityListActivity.this).updateWidgetById(mAppWidgetId, true);
                            intent.putExtra("appWidgetId", mAppWidgetId);
                        } else {
                            return;
                        }
                    }
                    return;
                }
                intent.putExtra(GlobalConfig.INTENT_EXTRA_CITY_INDEX, position);
                intent.setAction("android.intent.action.MAIN");
                setResult(-1, intent);
                finish();
                overridePendingTransition(R.anim.alpha_in_listclick, R.anim.alpha_out_listclick);
            }

            @Override
            public void onDismiss(int[] reverseSortedPositions) {
                super.onDismiss(reverseSortedPositions);
                long[] preDelete = new long[reverseSortedPositions.length];
                for (int i = 0; i < reverseSortedPositions.length; i++) {
                    Cursor cursor = (Cursor) mCityListAdapter.getItem(reverseSortedPositions[i]);
                    if (cursor != null) {
                        preDelete[i] = cursor.getLong(cursor.getColumnIndex("_id"));
                    }
                }
                for (long delId : preDelete) {
                    if (0 != delId) {
                        mCityListAdapter.delete(delId);
                        Message msg = new Message();
                        msg.what = NumberUtils.parseInt(delId);
                        msg.obj = delId;
                        mCityListHandler.sendMessage(msg);
                    }
                }
            }
        });
        cityListView.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
                Intent intent = new Intent();
                intent.putExtra(GlobalConfig.INTENT_EXTRA_CITY_INDEX, position);
                setResult(-1, intent);
                finish();
                overridePendingTransition(R.anim.alpha_in_listclick, R.anim.alpha_out_listclick);
            }
        });
        findViewById(R.id.btn_add).setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(CityListActivity.this, CitySearchActivity.class);
                intent.putExtra(CityListActivity.INTENT_SEARCH_CITY, cityListView.getCount());
                startActivityForResult(intent, 1);
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() != android.R.id.home) {
            return super.onOptionsItemSelected(item);
        }
        if (mAppWidgetId == -1) {
            setResult(-1);
        }
        finish();
        return true;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == -1 && data.getBooleanExtra(CitySearchActivity.INTENT_RESULT_SEARCH_CITY, false)) {
            findViewById(R.id.no_city_view).setVisibility(View.GONE);
            mCityListAdapter.setCanScroll(true);
            cityListView.smoothScrollToPosition(cityListView.getCount());
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[]
            grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == PermissionUtil.ALL_PERMISSION_REQUEST && !PermissionUtil.hasGrantedPermissions(this,
                permissions)) {
            AlertUtils.showNonePermissionDialog(this);
        }
    }

    @Override
    public void onBackPressed() {
        if (mAppWidgetId == -1) {
            setResult(-1);
        }
        finish();
        super.onBackPressed();
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    public void onStop() {
        super.onStop();
    }

    @Override
    public void onDestroy() {
        if (mCityListAdapter != null) {
            mCityListAdapter.onDestroy();
        }
        if (mCursor != null) {
            mCursor.close();
            mCursor = null;
        }
        super.onDestroy();
    }

    private void startActivityByTransition(View view, Intent intent) {
        startActivity(intent);
        overridePendingTransition(R.anim.alpha_in_listclick, R.anim.alpha_out_listclick);
    }

    @Override
    public void onChanged(final int postion) {
        new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(Void... voids) {
                mCityWeatherDB.changeDefaultCity(postion);
                return null;
            }

            @Override
            protected void onPostExecute(Void aVoid) {
                Intent intent = new Intent();
                intent.putExtra(GlobalConfig.INTENT_EXTRA_CITY_INDEX, 0);
                setResult(-1, intent);
                Toast.makeText(CityListActivity.this, R.string.default_city_changed, Toast.LENGTH_SHORT).show();
                finish();
            }
        }.execute();
    }
}
