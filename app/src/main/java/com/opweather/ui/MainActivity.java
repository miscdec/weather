package com.opweather.ui;

import android.Manifest;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.ActivityNotFoundException;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.opengl.GLSurfaceView;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.support.v4.widget.AutoScrollHelper;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Layout.Alignment;
import android.text.StaticLayout;
import android.text.TextPaint;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MenuItem.OnMenuItemClickListener;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.DefaultRetryPolicy;
import com.opweather.R;
import com.opweather.adapter.MainPagerAdapter;
import com.opweather.api.nodes.Alarm;
import com.opweather.api.nodes.RootWeather;
import com.opweather.bean.CityData;
import com.opweather.constants.GlobalConfig;
import com.opweather.constants.WeatherDescription;
import com.opweather.db.ChinaCityDB;
import com.opweather.db.CityWeatherDB;
import com.opweather.db.CityWeatherDB.CityListDBListener;
import com.opweather.receiver.AlarmReceiver;
import com.opweather.util.AlertUtils;
import com.opweather.util.BitmapUtils;
import com.opweather.util.DateTimeUtils;
import com.opweather.util.MediaUtil;
import com.opweather.util.NetUtil;
import com.opweather.util.OrientationSensorUtil;
import com.opweather.util.PermissionUtil;
import com.opweather.util.PreferenceUtils;
import com.opweather.util.StringUtils;
import com.opweather.util.SystemSetting;
import com.opweather.util.TemperatureUtil;
import com.opweather.util.UIUtil;
import com.opweather.util.WeatherClientProxy.CacheMode;
import com.opweather.util.WeatherResHelper;
import com.opweather.util.WeatherViewCreator;
import com.opweather.widget.AbsWeather;
import com.opweather.widget.openglbase.RainSurfaceView;
import com.opweather.widget.widget.WidgetHelper;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Iterator;
import java.util.Locale;
import java.util.concurrent.CopyOnWriteArrayList;

public class MainActivity extends AppCompatActivity {
    private static final String ACTION_DATE_CHANGED = "android.intent.action.DATE_CHANGED";
    private static final String ACTION_TIME_CHANGED = "android.intent.action.TIME_SET";
    private static final String ACTION_TIME_TICK = "android.intent.action.TIME_TICK";
    public static boolean MOCK_TEST_FLAG = false;
    private static final String TAG = MainActivity.class.getSimpleName();
    private static final int UPDATE_UNIT = 88;
    private int MOCK_BUTTON_ENALBE_CONDITION = 20;
    public int currentPositon;
    private int currentWeatherId;
    private AbsWeather currentWeatherView;
    private float currentWeatherViewAlpha;
    private ViewGroup mBackground;
    private boolean mCityChanged;
    private CityListDBListener mCityListDBListener;
    private CityWeatherDB mCityWeatherDB;
    private View mDecorView;
    private int mLastHour;
    private int mLastIndex;
    private boolean mLastIsDay;
    private MainPagerAdapter mMainPagerAdapter;
    private int mMockButtonClickCount;
    private boolean mNeedUpdateUnit;
    private BroadcastReceiver mReceiver;
    private BroadcastReceiver mTimeChangeReceiver;
    private Toolbar mToolbar;
    private ImageView mToolbar_gps;
    private TextView mToolbar_subtitle;
    private TextView mToolbar_title;
    private ViewPager mViewPager;
    private final CopyOnWriteArrayList<OnViewPagerScrollListener> mViewPagerListener = new CopyOnWriteArrayList<>();
    private int nextPositon;
    private AbsWeather nextWeatherView;
    private Dialog noConnectionDialog;
    private boolean sameWeatherView;
    private Handler mHandler;

    public interface OnViewPagerScrollListener {
        void onScrolled(float f, int i);

        void onSelected(int i);
    }

    private class SavePic extends AsyncTask<String, Void, String> {
        private SavePic() {
        }

        @Override
        protected String doInBackground(String... params) {
            if (!TextUtils.isEmpty(params[0])) {
                BitmapUtils.savePic(BitmapUtils.compressImage(getShareImage()), params[0]);
            }
            return params[0];
        }

        protected void onPostExecute(String path) {
            if (TextUtils.isEmpty(path)) {
                Toast.makeText(MainActivity.this, getString(R.string.no_weather_data), Toast.LENGTH_SHORT).show();
                return;
            }
            Intent intent = new Intent("android.intent.action.SEND");
            intent.setType("image/*");
            intent.putExtra("android.intent.extra.SUBJECT", getString(R.string.share_subject));
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            intent.putExtra("android.intent.extra.STREAM", MediaUtil.getInstace().getImageContentUri(
                    MainActivity.this, new File(path)));
            startActivity(Intent.createChooser(intent, getString(R.string.share_title)));
        }
    }

    public void init() {
        mCityListDBListener = new CityListDBListener() {
            public void onCityAdded(long newId) {
                if (newId != 0) {
                    mCityChanged = true;
                }
            }

            public void onCityDeleted(long deletedId) {
                if (deletedId != 0) {
                    mCityChanged = true;
                }
            }

            public void onCityUpdated(long recordId) {
                if (recordId != 0) {
                    mCityChanged = true;
                }
            }
        };
        mReceiver = new BroadcastReceiver() {
            public void onReceive(Context context, Intent intent) {
                if (intent.getAction().equals("android.net.conn.CONNECTIVITY_CHANGE")) {
                    NetworkInfo info = ((ConnectivityManager) getApplicationContext()
                            .getSystemService(Context.CONNECTIVITY_SERVICE)).getActiveNetworkInfo();
                    if (info != null && info.isAvailable()) {
                        if (!(mMainPagerAdapter == null || mViewPager == null)) {
                            mMainPagerAdapter.loadWeather(mViewPager.getCurrentItem());
                        }
                        if (noConnectionDialog != null && noConnectionDialog.isShowing()) {
                            try {
                                noConnectionDialog.dismiss();
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                    }
                }
            }
        };
        mTimeChangeReceiver = new BroadcastReceiver() {
            public void onReceive(Context context, Intent intent) {
                String action = intent.getAction();
                if (ACTION_DATE_CHANGED.equals(action)) {
                }
                if (ACTION_TIME_CHANGED.equals(action)) {
                    System.out.println("ACTION_TIME_CHANGED");
                }
                if (ACTION_TIME_TICK.equals(action)) {
                    int currentHour = DateTimeUtils.longTimeToHour(System.currentTimeMillis(),
                            mMainPagerAdapter.getCityAtPosition(currentPositon).getWeathers()
                                    .getCurrentWeather()
                                    .getLocalTimeZone());
                    if (currentHour != mLastHour) {
                        refreshViewPagerChild();
                        mLastHour = currentHour;
                    }
                }
                if (currentPositon != -1) {
                    boolean isday = isDay(currentPositon);
                    if (mLastIsDay != isday) {
                        updateBackground(currentPositon, false, true);
                        if (mMainPagerAdapter != null) {
                            ContentWrapper cw = mMainPagerAdapter.getContentWrap(currentPositon);
                            if (cw != null) {
                                cw.updateCurrentWeatherUI();
                                mLastIsDay = isday;
                            }
                        }
                    }
                }
            }
        };
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        UIUtil.setWindowStyle(this);
        setContentView(R.layout.main_activity);
        setupActionBar();
        mDecorView = getWindow().getDecorView();
        ChinaCityDB.openCityDB(this);
        mViewPager = findViewById(R.id.pager);
        mHandler = new Handler() {
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                switch (msg.what) {
                    case UPDATE_UNIT:
                        refreshViewPagerChild();
                    default:
                        break;
                }
            }
        };
        getWindow().setBackgroundDrawable(null);
        mHandler.postDelayed(new Runnable() {
            public void run() {
                mCityWeatherDB = CityWeatherDB.getInstance(MainActivity.this);
                initViewPager();
                addOnSettingChangeListener();
                mViewPager.setOffscreenPageLimit(8);
                if (!isNetworkConnected()) {
                    noConnectionDialog = AlertUtils.showNoConnectionDialog(MainActivity.this);
                }
                init3DView();
                init();
                registerReceiver();
                addCityWeatherDBListener();
            }
        }, 70);
        AlarmReceiver.setAlarmClock(getApplicationContext());
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        switch (requestCode) {
            case 1:
                for (int i = 0; i < grantResults.length; i++) {
                    if (grantResults[i] != PackageManager.PERMISSION_GRANTED) {
                        new AlertDialog.Builder(this).setMessage(R.string.dialog_necessary_permissions)
                                .setPositiveButton(R.string.dialog_go_to_settings,
                                        new OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialogInterface, int i) {
                                                Intent intent = new Intent(Settings
                                                        .ACTION_APPLICATION_DETAILS_SETTINGS);
                                                intent.setData(Uri.fromParts("package", getPackageName(), null));
                                                startActivity(intent);
                                            }
                                        }).setNegativeButton(R.string.dialog_exit,
                                new OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        finish();
                                    }
                                }).create().show();
                        return;
                    } else {
                        if (!permissions[i].equals(Manifest.permission.ACCESS_FINE_LOCATION) &&
                                mMainPagerAdapter != null) {
                            mMainPagerAdapter.loadWeather(0, true);
                            return;
                        } else if (permissions[i].equals(Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                            shareImageAndText();
                        }
                    }
                }
            default:
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == -1) {
            switch (requestCode) {
                case 1:
                    Intent intent = data;
                    if (intent != null && intent.getExtras() != null) {
                        int index;
                        Bundle d = intent.getExtras();
                        boolean callByCityListActivity = d.containsKey(GlobalConfig.INTENT_EXTRA_CITY_INDEX);
                        if (callByCityListActivity) {
                            index = d.getInt(GlobalConfig.INTENT_EXTRA_CITY_INDEX);
                        } else {
                            index = mLastIndex;
                        }
                        if (mCityChanged) {
                            mCityChanged = false;
                            mMainPagerAdapter.updateCityList(this);
                            mMainPagerAdapter.notifyDataSetChanged();
                            updateToolbar(index);
                        }
                        if (callByCityListActivity) {
                            nextWeatherView = null;
                            if (mViewPager != null) {
                                mViewPager.setCurrentItem(index, false);
                            }
                            nextPositon = -1;
                            updateBackground(index, true, false);
                            if (mMainPagerAdapter != null) {
                                ContentWrapper cw = mMainPagerAdapter.getContentWrap(index);
                                if (cw != null) {
                                    cw.resetScrollView();
                                }
                                if (currentWeatherView != null) {
                                    currentWeatherView.setAlpha(DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
                                }
                            }
                            currentWeatherViewAlpha = 1.0f;
                        }
                    } else if (mCityChanged) {
                        mCityChanged = false;
                        mMainPagerAdapter.updateCityList(this);
                        mMainPagerAdapter.notifyDataSetChanged();
                    }
                case 2:
                    if (mMainPagerAdapter != null) {
                        mMainPagerAdapter.getContentWrap(0).updateWeatherInfo(CacheMode.LOAD_NO_CACHE);
                    }
                case 3:
                    if (mMainPagerAdapter != null) {
                        mMainPagerAdapter.getContentWrap(0).updateWeatherInfo(CacheMode.LOAD_NO_CACHE);
                    }
                default:
                    break;
            }
        }
    }

    private void init3DView() {
        if (mBackground == null) {
            mBackground = findViewById(R.id.current_opweather_background);
        }
        RainSurfaceView child = new RainSurfaceView(this, -1, isDay(currentPositon));
        child.stopAnimate();
        child.setAlpha(AutoScrollHelper.RELATIVE_UNSPECIFIED);
        mBackground.addView(child);
        child.onPause();
    }

    private void initViewPager() {
        int position = 0;
        int widgetId = getIntent().getIntExtra(WidgetHelper.WIDGET_ID, -1);
        String locationId = String.valueOf(PreferenceUtils.getInt(this, WidgetHelper.WIDGET_ID_PREFIX + widgetId, -1));
        if (widgetId != -1 && !"-1" .equals(locationId)) {
            Cursor cursor = mCityWeatherDB.getAllCities();
            if (cursor != null && cursor.getCount() > 0 && cursor.moveToFirst()) {
                while (!cursor.getString(RainSurfaceView.RAIN_LEVEL_RAINSTORM).equals(locationId)) {
                    if (!cursor.moveToNext()) {
                        break;
                    }
                }
                position = cursor.getPosition();
                cursor.close();
            }
        } else if (widgetId != -1 && "-1" .equals(locationId)) {
            WidgetHelper.getInstance(this).updateWidgetById(widgetId, false);
        }
        mLastIndex = position;
        mMainPagerAdapter = new MainPagerAdapter(this, mViewPagerListener, mToolbar_subtitle);
        mMainPagerAdapter.setOnUIChangedListener(new ContentWrapper.OnUIChangedListener() {
            @Override
            public void onScrollViewChange(float alpha) {
                currentWeatherViewAlpha = alpha;
                setCurrentWeatherViewAlpha(alpha);
            }

            @Override
            public void onChangedCurrentWeather() {
                updateBackground(mViewPager.getCurrentItem(), true, false);
            }

            @Override
            public void ChangePathMenuResource(int index, boolean isBlack, boolean isLoading) {
                RootWeather weather = mMainPagerAdapter.getWeatherDataAtPosition(index);
                if (index != mViewPager.getCurrentItem()) {
                    return;
                }
                CityData cityData;
                if (mToolbar.getMenu().findItem(R.id.action_cities) == null ||
                        mToolbar.getMenu().findItem(R.id.action_warning) == null) {
                    Log.e(TAG, "findItem : is null");
                    cityData = mMainPagerAdapter.getCityAtPosition(index);
                    if (cityData != null) {
                        mToolbar_title.setText(cityData.getProvider() != -1 ?
                                cityData.getLocalName() : getString(R.string.current_location));
                        mToolbar_subtitle.setText(DateTimeUtils.getTimeTitle(null, false,
                                MainActivity.this));
                    } else {
                        mToolbar_title.setText(R.string.current_location);
                        mToolbar_subtitle.setText(DateTimeUtils.getTimeTitle(null, false,
                                MainActivity.this));
                    }
                    mToolbar_title.setTextColor(ContextCompat.getColor(MainActivity.this,
                            R.color.oneplus_contorl_text_color_primary_dark));
                    mToolbar_subtitle.setTextColor(ContextCompat.getColor(MainActivity.this,
                            R.color.oneplus_contorl_text_color_secondary_dark));
                } else if (weather == null) {
                    Log.e(TAG, "weather is null");
                    Log.e(TAG, "isLoading is :" + isLoading);
                    if (!isLoading) {
                        boolean z;
                        cityData = mMainPagerAdapter.getCityAtPosition(index);
                        StringBuilder append = new StringBuilder().append("cityData is :");
                        if (cityData != null) {
                            z = true;
                        } else {
                            z = false;
                        }
                        Log.e(TAG, append.append(z).toString());
                        if (cityData != null) {
                            mToolbar_title.setText(cityData.getProvider() != -1 ?
                                    cityData.getLocalName() : getString(R.string.current_location));
                            mToolbar_subtitle.setText(DateTimeUtils.getTimeTitle(null, false,
                                    MainActivity.this));
                        } else {
                            mToolbar_title.setText(R.string.current_location);
                            mToolbar_subtitle.setText(DateTimeUtils.getTimeTitle(null, false,
                                    MainActivity.this));
                        }
                    }
                    mToolbar_title.setTextColor(ContextCompat.getColor(MainActivity.this,
                            R.color.oneplus_contorl_text_color_primary_dark));
                    mToolbar_subtitle.setTextColor(ContextCompat.getColor(MainActivity.this,
                            R.color.oneplus_contorl_text_color_secondary_dark));
                    showGPSIcon(index, ContextCompat.getDrawable(MainActivity.this,
                            R.drawable.icon_gps), ContextCompat.getDrawable(MainActivity.this,
                            R.mipmap.btn_home_enable));
                } else {
                    updateWeatherWarning(index);
                    Date date = weather.getDate();
                    if (isBlack) {
                        mToolbar.setOverflowIcon(ContextCompat.getDrawable(MainActivity.this,
                                R.drawable.more_setting_black));
                        mToolbar.getMenu().findItem(R.id.action_cities).setIcon(
                                R.drawable.ic_city_black);
                        mToolbar.getMenu().findItem(R.id.action_warning).setIcon(
                                R.drawable.ic_warn_black);
                        if (!isLoading) {
                            mToolbar_title.setVisibility(View.VISIBLE);
                            mToolbar_subtitle.setVisibility(View.VISIBLE);
                            mToolbar_title.setText(mMainPagerAdapter
                                    .getCityAtPosition(index).getLocalName());
                            mToolbar_subtitle.setText(DateTimeUtils.getTimeTitle(date, weather
                                    .getRequestIsSuccess(), MainActivity.this));
                        }
                        mToolbar_title.setTextColor(ContextCompat.getColor(MainActivity.this,
                                R.color.oneplus_contorl_text_color_primary_light));
                        mToolbar_subtitle.setTextColor(ContextCompat.getColor(MainActivity.this,
                                R.color.oneplus_contorl_text_color_secondary_light));
                        showGPSIcon(index, ContextCompat.getDrawable(MainActivity.this,
                                R.drawable.icon_gps_black), ContextCompat.getDrawable
                                (MainActivity.this, R.drawable.ic_home_black));
                        mDecorView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
                                | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                                | View.SYSTEM_UI_FLAG_LAYOUT_STABLE);
                        return;
                    }
                    mToolbar.setOverflowIcon(ContextCompat.getDrawable(MainActivity.this, R.drawable.more_setting));
                    mToolbar.getMenu().findItem(R.id.action_cities).setIcon(R.drawable.ic_city);
                    mToolbar.getMenu().findItem(R.id.action_warning).setIcon(R.drawable.ic_warn);
                    if (!isLoading) {
                        mToolbar_title.setText(mMainPagerAdapter.getCityAtPosition(index).getLocalName());
                        mToolbar_subtitle.setText(DateTimeUtils.getTimeTitle(date, weather.getRequestIsSuccess(),
                                MainActivity.this));
                    }
                    mToolbar_title.setTextColor(ContextCompat.getColor(MainActivity.this,
                            R.color.oneplus_contorl_text_color_primary_dark));
                    mToolbar_subtitle.setTextColor(ContextCompat.getColor(MainActivity.this,
                            R.color.oneplus_contorl_text_color_secondary_dark));
                    showGPSIcon(index, ContextCompat.getDrawable(MainActivity.this,
                            R.drawable.icon_gps), ContextCompat.getDrawable(MainActivity.this,
                            R.mipmap.btn_home_enable));
                    mDecorView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN |
                            View.SYSTEM_UI_FLAG_LAYOUT_STABLE);
                }
            }

            @Override
            public void onWeatherDataUpdate(int index) {
                if (index == mViewPager.getCurrentItem()) {
                    updateBackground(index, true, true);
                }
            }

            @Override
            public void onError() {
                updateBackground(mViewPager.getCurrentItem(), false, false);
            }
        });
        mViewPager.setAdapter(mMainPagerAdapter);
        mViewPager.setCurrentItem(position, false);
        mViewPager.addOnPageChangeListener(new OnPageChangeListener() {
            boolean dragging = false;

            @Override
            public void onPageSelected(int position) {
                mMainPagerAdapter.loadWeather(position);
                Iterator it = mViewPagerListener.iterator();
                while (it.hasNext()) {
                    ((OnViewPagerScrollListener) it.next()).onSelected(position);
                }
                if (mViewPager.getChildCount() > mLastIndex) {
                    ContentWrapper cw = mMainPagerAdapter.getContentWrap(mLastIndex);
                    if (cw != null) {
                        cw.resetScrollView();
                    }
                }
                currentWeatherViewAlpha = DefaultRetryPolicy.DEFAULT_BACKOFF_MULT;
                mLastIndex = position;
                updateWeatherWarning(position);
                updateToolbar(position);
                updateBackground(position, true, false);
            }

            @Override
            public void onPageScrolled(int position, float present, int offset) {
                if (dragging) {
                    dragging = false;
                    if (position < currentPositon) {
                        addNextWeatherView(position);
                    } else {
                        addNextWeatherView(position + 1);
                    }
                }
                if (position == currentPositon) {
                    present = 1.0f - present;
                } else if (position < currentPositon) {
                    position++;
                } else if (position > currentPositon) {
                    return;
                }
                setWeatherViewAlpha(present, position, false);
            }

            @Override
            public void onPageScrollStateChanged(int arg0) {
                switch (arg0) {
                    case 0:
                        updateBackground(mLastIndex, false, false);
                    case 1:
                        dragging = true;
                    default:
                        break;
                }
            }
        });
    }

    private void addCityWeatherDBListener() {
        mCityWeatherDB.addDataChangeListener(mCityListDBListener);
    }

    private void removeCityWeatherDBListener() {
        if (mCityWeatherDB != null) {
            mCityWeatherDB.removeDataChangeListener(mCityListDBListener);
            mCityWeatherDB.close();
            mCityWeatherDB = null;
        }
    }

    private void addOnSettingChangeListener() {
        SystemSetting.addOnDataChangeListener(new SystemSetting.OnDataChangeListener() {
            @Override
            public void onWindChanged(boolean check) {
            }

            @Override
            public void onTemperatureChanged(boolean check) {
            }

            @Override
            public void onHumidityChanged(boolean check) {
            }

            @Override
            public void onUnitChanged(boolean check) {
                mNeedUpdateUnit = true;
            }
        });
    }

    private void setupActionBar() {
        mToolbar = findViewById(R.id.toolbar);
        setSupportActionBar(mToolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayShowTitleEnabled(false);
        }
        mToolbar_gps = findViewById(R.id.toolbar_gps);
        mToolbar_gps.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mMockButtonClickCount > MOCK_BUTTON_ENALBE_CONDITION) {
                    gotoMocLocation();
                } else {
                    mMockButtonClickCount += 1;
                }
            }
        });
        mToolbar_title = findViewById(R.id.toolbar_title);
        mToolbar_subtitle = findViewById(R.id.toolbar_subtitle);
    }

    @Override
    protected void onStart() {
        super.onStart();
        OrientationSensorUtil.requestSensor(this);
        if (currentWeatherView != null) {
            currentWeatherView.onViewStart();
        }
        if (nextWeatherView != null) {
            nextWeatherView.onViewStart();
        }
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        setIntent(intent);
    }

    @Override
    public void onBackPressed() {
        finish();
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        if (mNeedUpdateUnit) {
            mHandler.removeMessages(UPDATE_UNIT);
            mHandler.sendEmptyMessage(UPDATE_UNIT);
        }
        if (mMainPagerAdapter != null) {
            if (PermissionUtil.hasGrantedPermissions(this, Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                mMainPagerAdapter.loadWeather(mViewPager.getCurrentItem(), false);
                refreshViewPagerChild();
            }
        }
        mNeedUpdateUnit = false;
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (currentWeatherView != null && (currentWeatherView instanceof GLSurfaceView)) {
            ((GLSurfaceView) currentWeatherView).onResume();
        }
        if (nextWeatherView != null && (nextWeatherView instanceof GLSurfaceView)) {
            ((GLSurfaceView) nextWeatherView).onResume();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onStop() {
        stopCurrentWeatherView();
        stopNextWeatherView();
        OrientationSensorUtil.releaseSensor();
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        removeCityWeatherDBListener();
        SystemSetting.removeAllDataListener();
        unregisterReceiver();
        OrientationSensorUtil.releaseSensor();
        ChinaCityDB.openCityDB(this).close();
        mViewPager.clearOnPageChangeListeners();
        super.onDestroy();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_cities:
                gotoCityList();
                break;
            case R.id.popup_menu_settings:
                gotoSettings();
                break;
            case R.id.popup_menu_share:
                openShareList();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    public void updateToolbar(int position) {
        RootWeather weather = mMainPagerAdapter.getWeatherDataAtPosition(position);
        if (weather == null) {
            mToolbar_title.setTextColor(ContextCompat.getColor(this, R.color.oneplus_contorl_text_color_primary_dark));
            mToolbar_title.setText(mMainPagerAdapter.getCityAtPosition(position).getLocalName());
            mToolbar_subtitle.setTextColor(ContextCompat.getColor(this,
                    R.color.oneplus_contorl_text_color_secondary_light));
            mToolbar_subtitle.setText(DateTimeUtils.getTimeTitle(null, false, this));
            showGPSIcon(position, ContextCompat.getDrawable(this, R.drawable.icon_gps_black),
                    ContextCompat.getDrawable(this, R.drawable.ic_home_black));
            return;
        }
        Date date = weather.getDate();
        if (mMainPagerAdapter.getWeatherDescriptionId(position) == 1003) {
            mToolbar_title.setTextColor(ContextCompat.getColor(this, R.color.oneplus_contorl_text_color_primary_light));
            mToolbar_title.setText(mMainPagerAdapter.getCityAtPosition(position).getLocalName());
            mToolbar_subtitle.setTextColor(ContextCompat.getColor(this,
                    R.color.oneplus_contorl_text_color_secondary_light));
            mToolbar_subtitle.setText(DateTimeUtils.getTimeTitle(date, weather.getRequestIsSuccess(), this));
            mToolbar.setOverflowIcon(ContextCompat.getDrawable(this, R.drawable.more_setting_black));
            mToolbar.getMenu().findItem(R.id.action_cities).setIcon(R.drawable.ic_city_black);
            mToolbar.getMenu().findItem(R.id.action_warning).setIcon(R.drawable.ic_warn_black);
            showGPSIcon(position, ContextCompat.getDrawable(this, R.drawable.icon_gps_black),
                    ContextCompat.getDrawable(this, R.drawable.ic_home_black));
            mDecorView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
                    | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                    | View.SYSTEM_UI_FLAG_LAYOUT_STABLE);
            return;
        }
        mToolbar_title.setTextColor(ContextCompat.getColor(this, R.color.oneplus_contorl_text_color_primary_dark));
        mToolbar_title.setText(mMainPagerAdapter.getCityAtPosition(position).getLocalName());
        mToolbar_subtitle.setTextColor(ContextCompat.getColor(this,
                R.color.oneplus_contorl_text_color_secondary_dark));
        mToolbar_subtitle.setText(DateTimeUtils.getTimeTitle(date, weather.getRequestIsSuccess(), this));
        mToolbar.setOverflowIcon(ContextCompat.getDrawable(this, R.drawable.more_setting));
        mToolbar.getMenu().findItem(R.id.action_cities).setIcon(R.drawable.ic_city);
        showGPSIcon(position, ContextCompat.getDrawable(this, R.drawable.icon_gps),
                ContextCompat.getDrawable(this, R.mipmap.btn_home_enable));
        mToolbar.getMenu().findItem(R.id.action_warning).setIcon(R.drawable.ic_warn);
        mDecorView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN | View.SYSTEM_UI_FLAG_LAYOUT_STABLE);
    }

    private void updateWeatherWarning(int position) {
        final ArrayList<Alarm> alarms;
        MenuItem menuItem = mToolbar.getMenu().findItem(R.id.action_warning);
        RootWeather weather = mMainPagerAdapter.getWeatherDataAtPosition(position);
        final CityData data = mMainPagerAdapter.getCityAtPosition(position);
        if (weather == null) {
            alarms = null;
        } else {
            alarms = (ArrayList<Alarm>) weather.getWeatherAlarms();
        }
        if (alarms == null || alarms.size() <= 0 || alarms.get(0).getTypeName().equalsIgnoreCase("None")
                || alarms.get(0).getContentText().equalsIgnoreCase("None")
                || alarms.get(0).getTypeName().equalsIgnoreCase("null")
                || alarms.get(0).getContentText().equalsIgnoreCase("null")) {
            menuItem.setVisible(false);
            return;
        }
        menuItem.setVisible(true);
        menuItem.setOnMenuItemClickListener(new OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem menuItem) {
                Intent intent = new Intent(MainActivity.this, WeatherWarningActivity.class);
                intent.putParcelableArrayListExtra(WeatherWarningActivity.INTENT_PARA_WARNING, alarms);
                intent.putExtra(WeatherWarningActivity.INTENT_PARA_CITY, data.getLocalName());
                startActivity(intent);
                return false;
            }
        });
    }

    private void showGPSIcon(int position, Drawable locationResId, Drawable homeResId) {
        if (mMainPagerAdapter.getCityAtPosition(position).isLocatedCity()) {
            mToolbar_gps.setVisibility(View.VISIBLE);
            mToolbar_gps.setImageDrawable(locationResId);
        } else if (mMainPagerAdapter.getCityAtPosition(position).isDefault()) {
            mToolbar_gps.setVisibility(View.VISIBLE);
            mToolbar_gps.setImageDrawable(homeResId);
        } else {
            mToolbar_gps.setVisibility(View.GONE);
        }
    }

    public void gotoMocLocation() {
//        startActivityForResult(new Intent(this, MockLocation.class), RainSurfaceView.RAIN_LEVEL_SHOWER);
//        overridePendingTransition(R.anim.citylist_translate_up, R.anim.alpha_out);
    }

    public void gotoSettings() {
        //startActivity(new Intent(this, SettingPreferenceActivity.class));
    }

    public void gotoCityList() {
        startActivityForResult(new Intent(this, CityListActivity.class), 1);
        overridePendingTransition(R.anim.citylist_translate_up, R.anim.alpha_out);
    }

    public void openShareList() {
        mHandler.post(new Runnable() {
            @Override
            public void run() {
                shareImageAndText();
            }
        });
    }

    private String getShareMsg() {
        float f;
        CityData cityDate = mMainPagerAdapter.getCityAtPosition(mViewPager.getCurrentItem());
        RootWeather weatherData = mMainPagerAdapter.getWeatherDataAtPosition(mViewPager.getCurrentItem());
        int highTemp = 0;
        int lowTemp = 0;
        String currentWeather = StringUtils.EMPTY_STRING;
        String cityName = cityDate.getLocalName();
        if (weatherData != null) {
            highTemp = weatherData.getTodayHighTemperature();
            lowTemp = weatherData.getTodayLowTemperature();
            currentWeather = weatherData.getCurrentWeatherText(this);
        }
        boolean cOrf = SystemSetting.getTemperature(this);
        String tempUnit = cOrf ? "℃" : "℉";
        if (cOrf) {
            f = (float) highTemp;
        } else {
            f = SystemSetting.celsiusToFahrenheit((float) highTemp);
        }
        int highUnitTemp = (int) f;
        if (cOrf) {
            f = (float) lowTemp;
        } else {
            f = SystemSetting.celsiusToFahrenheit((float) lowTemp);
        }
        return cityName + "    " + new SimpleDateFormat(getString(R.string.date_format)).format(
                new Date(System.currentTimeMillis())) + "\n" + currentWeather + "    " + highUnitTemp + tempUnit
                + " / " + ((int) f) + tempUnit + getString(R.string.share_from_oneplus_weather);
    }

    public String getShareMsmFirstLineCityName() {
        CityData cityDate = mMainPagerAdapter.getCityAtPosition(mViewPager.getCurrentItem());
        if (cityDate == null) {
            return StringUtils.EMPTY_STRING;
        }
        return "\u200e" + cityDate.getLocalName();
    }

    public String getShareMsmFirstLineDateAndWeekday() {
        long time = System.currentTimeMillis();
        Calendar c = Calendar.getInstance();
        c.setTimeInMillis(time);
        return "\u200e" + DateTimeUtils.longTimeToMMddTwo(this, time, null) + " " + DateTimeUtils
                .getDayString(this, c.get(Calendar.DAY_OF_WEEK));
    }

    public String getShareMsmSecondCurrentTemp() {
        CityData cityDate = mMainPagerAdapter.getCityAtPosition(mViewPager.getCurrentItem());
        if (cityDate != null) {
            RootWeather data = cityDate.getWeathers();
            if (data != null) {
                int curTemp = data.getTodayCurrentTemp();
                int todayHighTemperature = data.getTodayHighTemperature();
                int todayLowTemperature = data.getTodayLowTemperature();
                return TemperatureUtil.getCurrentTemperature(this, curTemp);
            }
        }
        return StringUtils.EMPTY_STRING;
    }

    public String getShareMsmThirdWeatherTypeAndTemp() {
        CityData cityDate = mMainPagerAdapter.getCityAtPosition(mViewPager.getCurrentItem());
        if (cityDate != null) {
            RootWeather data = cityDate.getWeathers();
            if (data != null) {
                String currentTemp = data.getCurrentWeatherText(this);
                int highTemp = data.getTodayHighTemperature();
                int lowTemp = data.getTodayLowTemperature();
                String hTemp = TemperatureUtil.getHighTemperature(this, highTemp);
                return "\u200e" + currentTemp + "  " + hTemp + "/" + TemperatureUtil
                        .getHighTemperature(this,
                                lowTemp) + (SystemSetting.getTemperature(this) ? "C" : "F");
            }
        }
        return StringUtils.EMPTY_STRING;
    }

    private void shareImageAndText() {
        if (PermissionUtil.check(this, Manifest.permission.WRITE_EXTERNAL_STORAGE, getString(
                R.string.request_permission_storage), 1) && mMainPagerAdapter != null && mViewPager != null) {
            CityData cityDate = mMainPagerAdapter.getCityAtPosition(mViewPager.getCurrentItem());
            if (cityDate != null) {
                String shareIamgePath = BitmapUtils.getPicFileName(cityDate.getLocalName(), this);
                new SavePic().execute(shareIamgePath);
                return;
            }
            Toast.makeText(this, getString(R.string.no_weather_data), Toast.LENGTH_SHORT).show();
        }
    }

    public Bitmap getShareImage() {
        RootWeather weatherData = mMainPagerAdapter.getWeatherDataAtPosition(mViewPager.getCurrentItem());
        int weatherId = 1001;
        if (weatherData != null) {
            weatherId = WeatherResHelper.weatherToResID(this, weatherData.getCurrentWeatherId());
        }
        String cityName = getShareMsmFirstLineCityName();
        String dateAndWeekday = getShareMsmFirstLineDateAndWeekday();
        String currentTemp = getShareMsmSecondCurrentTemp();
        String cWeatherTypeAndTemp = getShareMsmThirdWeatherTypeAndTemp();
        Bitmap canvasBmp = BitmapFactory.decodeResource(getResources(), WeatherResHelper.getWeatherListitemBkgResID
                (weatherId, isDay(mViewPager.getCurrentItem()))).copy(Config.RGB_565, true);
        Canvas cn = new Canvas(canvasBmp);
        TextPaint paint = new TextPaint();
        paint.setAntiAlias(true);
        paint.setARGB(255, 0, 0, 0);
        paint.setTextSize(48.0f);
        if (needGrayColor(getWeatherId())) {
            paint.setColor(getResources().getColor(R.color.top_gray));
        } else {
            paint.setColor(-1);
        }
        StaticLayout layout = new StaticLayout(cityName, paint, 720, Alignment.ALIGN_NORMAL, 1.0f, 0.0f, true);
        cn.save(Canvas.ALL_SAVE_FLAG);
        cn.translate(51.0f, 39.0f);
        layout.draw(cn);
        cn.restore();
        paint.setTextSize(36.0f);
        StaticLayout staticLayout = new StaticLayout(dateAndWeekday, paint, 264, Alignment
                .ALIGN_NORMAL, 1.0f, 0.0f,
                true);
        cn.save(Canvas.ALL_SAVE_FLAG);
        paint.setTextSize(36.0f);
        cn.translate(768.0f, 45.9f);
        paint.setTextSize((float) UIUtil.dip2px(this, 11.0f));
        staticLayout.draw(cn);
        cn.restore();
        paint.setTextSize(104.0f);
        paint.setColor(-1);
        staticLayout = new StaticLayout(currentTemp, paint, 1032, Alignment.ALIGN_NORMAL, 1.0f, 0.0f, true);
        cn.save(Canvas.ALL_SAVE_FLAG);
        cn.translate(41.1f, 243.90001f);
        staticLayout.draw(cn);
        cn.restore();
        paint.setTextSize(36.0f);
        paint.setColor(-1);
        staticLayout = new StaticLayout(cWeatherTypeAndTemp, paint, 645, Alignment.ALIGN_NORMAL, 1.0f, 0.0f, true);
        cn.save(Canvas.ALL_SAVE_FLAG);
        cn.translate(51.0f, 372.0f);
        staticLayout.draw(cn);
        cn.restore();
        paint.setTextSize(36.0f);
        paint.setColor(-1);
        staticLayout = new StaticLayout(getString(R.string.share_from_oneplus_weather), paint,
                336, Alignment
                .ALIGN_NORMAL, 1.0f, 0.0f, true);
        cn.save(Canvas.ALL_SAVE_FLAG);
        cn.translate(696.9f, 368.09998f);
        staticLayout.draw(cn);
        cn.restore();
        return canvasBmp;
    }

    private boolean isNetworkConnected() {
        return NetUtil.isNetworkAvailable(this);
    }

    private void registerReceiver() {
        IntentFilter filter = new IntentFilter();
        filter.addAction("android.net.conn.CONNECTIVITY_CHANGE");
        registerReceiver(mReceiver, filter);
    }

    private void unregisterReceiver() {
        if (mReceiver != null) {
            unregisterReceiver(mReceiver);
            mReceiver = null;
        }
    }

    protected void setCurrentWeatherViewAlpha(float alpha) {
        if (currentWeatherView != null) {
            currentWeatherView.setAlpha(alpha);
        }
    }

    protected void setNextWeatherViewAlpha(float alpha) {
        if (nextWeatherView != null) {
            nextWeatherView.setAlpha(alpha);
        }
    }

    protected void setWeatherViewAlpha(float alpha, int position, boolean refeshAlpha) {
        Iterator it = mViewPagerListener.iterator();
        while (it.hasNext()) {
            ((OnViewPagerScrollListener) it.next()).onScrolled(alpha, position);
        }
        if (alpha < 1.0f && ((double) alpha) >= 0.5d) {
            alpha = (2.0f * alpha) - 1.0f;
        } else if (((double) alpha) < 0.5d) {
            alpha = 0.2f;
        }
        float currAlpha = currentWeatherViewAlpha * alpha;
        setCurrentWeatherViewAlpha(currAlpha);
        float nextAlpha = (1.0f - alpha) * 2.0f;
        setNextWeatherViewAlpha(nextAlpha);
        if (sameWeatherView) {
            setNextWeatherViewAlpha(AutoScrollHelper.RELATIVE_UNSPECIFIED);
            if (currentWeatherViewAlpha == 1.0f) {
                setCurrentWeatherViewAlpha(DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
            } else if (currAlpha < nextAlpha) {
                setCurrentWeatherViewAlpha(nextAlpha);
            }
        }
        if (refeshAlpha) {
            setWeatherViewAlpha(alpha, position);
        }
    }

    protected void setWeatherViewAlpha(float alpha, int position) {
        RootWeather weatherData = mMainPagerAdapter != null ?
                mMainPagerAdapter.getWeatherDataAtPosition(position) : null;
        if (mBackground == null) {
            mBackground = findViewById(R.id.current_opweather_background);
        }
        int weatherId = 1001;
        if (weatherData != null) {
            weatherId = WeatherResHelper.weatherToResID(this, weatherData.getCurrentWeatherId());
        }
        mBackground.setBackgroundColor((0x00ffffff & getWeatherColor(this, weatherId, currentPositon)) |
                (((int) (255.0f * alpha)) << 24));
    }

    private void addNextWeatherView(int position) {
        if (mMainPagerAdapter != null && mBackground != null && nextPositon != position && position >= 0 &&
                position < mMainPagerAdapter.getCount()) {
            nextPositon = position;
            RootWeather weatherData = mMainPagerAdapter.getWeatherDataAtPosition(position);
            int weatherId = 1001;
            if (weatherData != null) {
                weatherId = WeatherResHelper.weatherToResID(this, weatherData.getCurrentWeatherId());
            }
            if (currentWeatherId == weatherId && mLastIsDay == isDay(nextPositon)) {
                sameWeatherView = true;
            } else {
                sameWeatherView = false;
            }
            if (nextWeatherView != null) {
                mBackground.removeView((View) nextWeatherView);
                nextWeatherView.setAlpha(AutoScrollHelper.RELATIVE_UNSPECIFIED);
                nextWeatherView.stopAnimate();
                destoryNextWeatherView();
            }
            nextWeatherView = WeatherViewCreator.getViewFromDescription(this, weatherId, isDay(nextPositon));
            if (nextWeatherView != null) {
                nextWeatherView.startAnimate();
            }
            mBackground.addView((View) nextWeatherView);
        }
    }

    public void updateBackground(int position, boolean force, boolean dayNightChange) {
        if (mMainPagerAdapter != null) {
            if (force) {
                currentPositon = -1;
                destoryNextWeatherView();
            }
            if (currentPositon != position || dayNightChange) {
                currentPositon = position;
                RootWeather weatherData = mMainPagerAdapter.getWeatherDataAtPosition(position);
                if (mBackground == null) {
                    mBackground = findViewById(R.id.current_opweather_background);
                }
                int weatherId = -1;
                if (weatherData != null) {
                    weatherId = WeatherResHelper.weatherToResID(this, weatherData.getCurrentWeatherId());
                }
                if (weatherId == -1) {
                    weatherId = WeatherDescription.WEATHER_DESCRIPTION_UNKNOWN;
                }
                if (currentWeatherId != weatherId || dayNightChange || mLastIsDay != isDay(currentPositon)) {
                    mLastIsDay = isDay(currentPositon);
                    if (nextWeatherView != null) {
                        mBackground.removeView((View) currentWeatherView);
                        if (currentWeatherView != null) {
                            currentWeatherView.setAlpha(AutoScrollHelper.RELATIVE_UNSPECIFIED);
                            currentWeatherView.stopAnimate();
                            destoryCurrentWeatherView();
                        }
                        currentWeatherView = nextWeatherView;
                        nextWeatherView = null;
                    } else {
                        if (currentWeatherView != null) {
                            mBackground.removeView((View) currentWeatherView);
                            currentWeatherView.setAlpha(AutoScrollHelper.RELATIVE_UNSPECIFIED);
                            currentWeatherView.stopAnimate();
                            destoryCurrentWeatherView();
                        }
                        currentWeatherView = WeatherViewCreator.getViewFromDescription(this, weatherId,
                                isDay(currentPositon));
                        currentWeatherView.startAnimate();
                        currentWeatherView.setAlpha(currentWeatherViewAlpha);
                        mBackground.setBackgroundColor(getWeatherColor(this, weatherId, currentPositon));
                        mBackground.addView((View) currentWeatherView);
                    }
                    checkBackgroundChild();
                    mBackground.setBackgroundColor(getWeatherColor(this, weatherId, currentPositon));
                    getWindow().getDecorView().setBackgroundColor(getWeatherColor(this, weatherId, currentPositon));
                    currentWeatherId = weatherId;
                    return;
                }
                return;
            }
            checkBackgroundChild();
        }
    }

    private void stopNextWeatherView() {
        if (nextWeatherView != null) {
            nextWeatherView.onViewPause();
        }
        if (nextWeatherView != null && (nextWeatherView instanceof GLSurfaceView)) {
            ((GLSurfaceView) nextWeatherView).onPause();
        }
    }

    private void stopCurrentWeatherView() {
        if (currentWeatherView != null) {
            currentWeatherView.onViewPause();
        }
        if (currentWeatherView != null && (currentWeatherView instanceof GLSurfaceView)) {
            ((GLSurfaceView) currentWeatherView).onPause();
        }
    }

    private void destoryNextWeatherView() {
        stopNextWeatherView();
        nextWeatherView = null;
    }

    private void destoryCurrentWeatherView() {
        stopCurrentWeatherView();
        currentWeatherView = null;
    }

    private void checkBackgroundChild() {
        if (mBackground != null && mBackground.getChildCount() > 1) {
            mBackground.removeView((View) currentWeatherView);
            for (int i = 0; i < mBackground.getChildCount(); i++) {
                View view = mBackground.getChildAt(i);
                if (view instanceof GLSurfaceView) {
                    ((GLSurfaceView) view).onPause();
                }
            }
            mBackground.removeAllViews();
            if (currentWeatherView != null) {
                mBackground.addView((View) currentWeatherView);
            }
            destoryNextWeatherView();
            nextPositon = -1;
        }
    }

    public int getWeatherColor(Context context, int weatherId, int position) {
        return context.getResources().getColor(WeatherResHelper.getWeatherColorStringID
                (weatherId, isDay(position)));
    }

    public int getWeatherId() {
        RootWeather weatherData = mMainPagerAdapter.getWeatherDataAtPosition(currentPositon);
        return weatherData != null ? WeatherResHelper.weatherToResID(this, weatherData.getCurrentWeatherId()) : 1001;
    }

    public boolean isDay(int position) {
        boolean isDay = true;
        if (position == -1) {
            return DateTimeUtils.isTimeMillisDayTime(System.currentTimeMillis());
        }
        CityData cityData = mMainPagerAdapter.getCityAtPosition(position);
        if (cityData != null) {
            RootWeather weatherData = cityData.getWeathers();
            if (weatherData != null) {
                try {
                    isDay = cityData.isDay(weatherData);
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                }
            }
        }
        return isDay;
    }

    public boolean needGrayColor(int weatherId) {
        CityData cityData = mMainPagerAdapter.getCityAtPosition(currentPositon);
        RootWeather weatherData = cityData.getWeathers();
        boolean isDay = true;
        if (weatherData != null) {
            try {
                isDay = cityData.isDay(weatherData);
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        }
        return weatherId == 1003 && isDay;
    }

    private void registerTimeChangeReceiver() {
        IntentFilter filter = new IntentFilter();
        filter.addAction(ACTION_TIME_TICK);
        filter.addAction(ACTION_TIME_CHANGED);
        registerReceiver(mTimeChangeReceiver, filter);
    }

    private void unRegisterTimeChangeReceiver() {
        unregisterReceiver(mTimeChangeReceiver);
    }

    private void refreshViewPagerChild() {
        int childCount = mViewPager.getChildCount();
        for (int i = 0; i < childCount; i++) {
            mViewPager.getChildAt(i).invalidate();
        }
    }

    public void clikUrl(View view) {
        CityData data = mMainPagerAdapter.getCityAtPosition(currentPositon);
        if (data != null) {
            String idOrkey = data.getLocationId();
            String local = Locale.getDefault().getLanguage();
            boolean isChina = data.getProvider() == 4096;
            RootWeather weather = data.getWeathers();
            if (weather != null && weather.getCurrentWeather() != null) {
                String url;
                switch (view.getId()) {
                    case R.id.aqiView:
                        openBrower(StringUtils.getAqiMobileLink(idOrkey, local), view.getContext());
                    case R.id.click_url_text:
                        if (isChina) {
                            url = StringUtils.getFifteendaysMobileLink(idOrkey, local);
                        } else {
                            url = weather.getFutureLink();
                        }
                        openBrower(url, view.getContext());
                    case R.id.opweather_detail:
                        openBrower(StringUtils.getLifeMobileLink(idOrkey, local), view.getContext
                                ());
                    case R.id.opweather_info:
                        if (isChina) {
                            url = StringUtils.getChinaMainMobileLink(idOrkey, local);
                        } else {
                            url = weather.getCurrentWeather().getMainMoblieLink();
                        }
                        openBrower(url, view.getContext());
                    default:
                        break;
                }
            }
        }
    }

    private void openBrower(String url, Context context) {
        try {
            context.startActivity(new Intent("android.intent.action.VIEW", Uri.parse(url)));
        } catch (ActivityNotFoundException e) {
            Toast.makeText(context, context.getString(R.string.browser_not_found), Toast.LENGTH_SHORT).show();
            e.printStackTrace();
        }
    }
}
