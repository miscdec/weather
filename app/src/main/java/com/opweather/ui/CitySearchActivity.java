package com.opweather.ui;

import android.app.ActionBar;
import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.opweather.R;
import com.opweather.adapter.CityListSearchAdapter;
import com.opweather.bean.CommonCandidateCity;
import com.opweather.db.CityWeatherDB;
import com.opweather.provider.CitySearchProvider;
import com.opweather.starwar.StarWarUtils;
import com.opweather.util.AlertUtils;
import com.opweather.util.DateTimeUtils;
import com.opweather.util.EncodeUtil;
import com.opweather.util.GpsUtils;
import com.opweather.util.NetUtil;
import com.opweather.util.StringUtils;
import com.opweather.widget.ClearableEditText;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

public class CitySearchActivity extends BaseActivity {
    public static final String INTENT_RESULT_SEARCH_CITY = "city_search";
    private static final int SEARCH_PROVIDER_CHINA = 4096;
    private static final int SEARCH_PROVIDER_FOREIGN = 2048;
    private static final String TAG = CityListSearchAdapter.class.getSimpleName();
    ;
    private CityListSearchAdapter adapter;
    private List<CommonCandidateCity> citySearchResult;
    private CitySearchProvider mChinaCitySearProvider;
    private int mCityCount;
    private ClearableEditText mCityKeyword;
    private CityWeatherDB mCityWeatherDB;
    private CitySearchProvider mForeignCitySearProvider;
    private Handler mHandler;
    private TextView mNoSearchView;
    private BroadcastReceiver mReceiver;
    private ListView mSearchListView;
    private Dialog noConnectionDialog;

    public CitySearchActivity() {
        citySearchResult = new ArrayList<>();
        mReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                if (intent.getAction().equals("android.net.conn.CONNECTIVITY_CHANGE")) {
                    NetworkInfo info = ((ConnectivityManager) getApplicationContext()
                            .getSystemService(Context.CONNECTIVITY_SERVICE)).getActiveNetworkInfo();
                    if (info == null || !info.isAvailable()) {
                        mNoSearchView.setText(R.string.no_network_statu);
                        mNoSearchView.setCompoundDrawablesWithIntrinsicBounds(null, context.getDrawable(R.drawable
                                .no_network), null, null);
                        return;
                    }
                    if (noConnectionDialog != null && noConnectionDialog.isShowing()) {
                        try {
                            noConnectionDialog.dismiss();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                    mNoSearchView.setText(R.string.no_search_data);
                    mNoSearchView.setCompoundDrawablesWithIntrinsicBounds(null, context.getDrawable(R.drawable
                            .no_search_icon), null, null);
                }
            }
        };
    }

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.search_citylist_activity);
        registerReceiver();
        if (!isNetworkConnected()) {
            noConnectionDialog = AlertUtils.showNoConnectionDialog(this);
        }
        init();
    }

    private void registerReceiver() {
        IntentFilter filter = new IntentFilter();
        filter.addAction("android.net.conn.CONNECTIVITY_CHANGE");
        registerReceiver(mReceiver, filter);
    }

    private void unRegisterReceiver() {
        if (mReceiver != null) {
            unregisterReceiver(mReceiver);
            mReceiver = null;
        }
    }

    private boolean isNetworkConnected() {
        return NetUtil.isNetworkAvailable(this);
    }

    private void init() {
        initData();
        initUIView();
    }

    private void initData() {
        mCityCount = getIntent().getIntExtra(CityListActivity.INTENT_SEARCH_CITY, 0);
        mCityWeatherDB = CityWeatherDB.getInstance(getApplicationContext());
        mHandler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                String country;
                if (GpsUtils.isH2OS() && msg.what == 4096) {
                    List<CommonCandidateCity> chinaCitySearchResult = mChinaCitySearProvider.getCandidateCityList();
                    if (chinaCitySearchResult != null) {
                        Iterator<CommonCandidateCity> chinaCityIterator = chinaCitySearchResult.iterator();
                        while (chinaCityIterator.hasNext()) {
                            CommonCandidateCity city = (CommonCandidateCity) chinaCityIterator.next();
                            country = city.getCityCountryID();
                            String provinceEn = city.getCityProvinceEn();
                            if (!country.equals("China") || provinceEn.equals("Taiwan Province")) {
                                chinaCityIterator.remove();
                            }
                        }
                        if (chinaCitySearchResult.size() == 0) {
                            mNoSearchView.setVisibility(View.VISIBLE);
                        }
                        citySearchResult.addAll(chinaCitySearchResult);
                    }
                }
                if (msg.what == 2048) {
                    List<CommonCandidateCity> foreignCitySearchResult = mForeignCitySearProvider.getCandidateCityList();
                    if (foreignCitySearchResult != null) {
                        Iterator<CommonCandidateCity> foreignIterator = foreignCitySearchResult.iterator();
                        while (GpsUtils.isH2OS() && foreignIterator.hasNext()) {
                            country = ((CommonCandidateCity) foreignIterator.next()).getCityCountryID();
                            if (country.equals("CN") || country.equals("HK") || country.equals("MO")) {
                                foreignIterator.remove();
                            }
                        }
                        if (foreignCitySearchResult.size() == 0) {
                            mNoSearchView.setVisibility(View.VISIBLE);
                        }
                        citySearchResult.addAll(foreignCitySearchResult);
                    }
                }
                if (StarWarUtils.isStarWar() && StarWarUtils.isShow(mCityKeyword.getText().toString())) {
                    citySearchResult.add(0, new CommonCandidateCity(StarWarUtils.STATWAR_NAME, StarWarUtils
                            .STATWAR_KEY_WORD, null, null, null, 0));
                }
                if (citySearchResult != null && citySearchResult.size() > 0) {
                    Set<CommonCandidateCity> primesWithoutDuplicates = new LinkedHashSet(citySearchResult);
                    citySearchResult.clear();
                    citySearchResult.addAll(primesWithoutDuplicates);
                    mNoSearchView.setVisibility(View.INVISIBLE);
                    adapter = new CityListSearchAdapter(CitySearchActivity.this, citySearchResult);
                    mSearchListView.setAdapter(adapter);
                }
            }
        };
        mChinaCitySearProvider = new CitySearchProvider(getApplicationContext(), 4096, mHandler);
        mForeignCitySearProvider = new CitySearchProvider(getApplicationContext(), 2048, mHandler);
    }

    private void initUIView() {
        ActionBar bar = getActionBar();
        if (bar != null) {
            View actionbarLayout = LayoutInflater.from(this).inflate(R.layout.citylist_search_bar, null);
            bar.setDisplayShowCustomEnabled(true);
            bar.setDisplayHomeAsUpEnabled(true);
            bar.setCustomView(actionbarLayout);
            mCityKeyword = (ClearableEditText) actionbarLayout.findViewById(R.id.search_bar_input_field);
        }
        mNoSearchView = (TextView) findViewById(R.id.no_search_view);
        mCityKeyword.addTextChangedListener(new TextWatcher() {
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            public void afterTextChanged(Editable s) {
                String searchText = s.toString();
                if (searchText.equals("\u4e00\u52a0\u96fe")) {
                    /*Intent intent = new Intent(CitySearchActivity.this, ShowWeatherActivity.class);
                    intent.putExtra("type", WeatherDescription.WEATHER_DESCRIPTION_FOG);
                    startActivity(intent);*/
                } else if (searchText.length() < 1) {
                } else {
                    if (isNetworkConnected()) {
                        if (citySearchResult.size() > 0 && adapter != null) {
                            citySearchResult.clear();
                            adapter.notifyDataSetInvalidated();
                        }
                        mChinaCitySearProvider.searchCitiesByKeyword(searchText, EncodeUtil.androidLocaleToAccuFormat
                                (mCityKeyword.getTextLocale()));
                        mForeignCitySearProvider.searchCitiesByKeyword(searchText, EncodeUtil
                                .androidLocaleToAccuFormat(mCityKeyword.getTextLocale()));
                        return;
                    }
                    Toast.makeText(CitySearchActivity.this, getString(R.string.no_network), Toast.LENGTH_SHORT).show();
                }
            }
        });
        mSearchListView = (ListView) findViewById(R.id.search_list);
        mSearchListView.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                hideSoftKeyboard(mCityKeyword);
                if (mCityCount == 8) {
                    Toast.makeText(getApplicationContext(), R.string.city_count_limit, Toast.LENGTH_SHORT).show();
                    return;
                }
                CommonCandidateCity city = ((CityListSearchAdapter) parent.getAdapter()).getItem(position);
                if (city == null || !city.getCityCode().equals(StarWarUtils.STATWAR_NAME)) {
                    if (mCityWeatherDB.addCity(city.getCityProvider() == 1 ? SEARCH_PROVIDER_CHINA :
                            SEARCH_PROVIDER_FOREIGN, city.getCityName(CitySearchActivity.this), city.getCityName
                            (CitySearchActivity.this), city.getCityCode(), DateTimeUtils.longTimeToRefreshTime
                            (CitySearchActivity.this, System.currentTimeMillis()), true) < 0) {
                        Toast.makeText(getApplicationContext(), R.string.city_exit, Toast.LENGTH_SHORT).show();
                        return;
                    }
                    Intent intent = new Intent();
                    intent.putExtra(INTENT_RESULT_SEARCH_CITY, true);
                    setResult(-1, intent);
                    finish();
                    return;
                }
                //startActivity(new Intent(CitySearchActivity.this, VidePlayActivity.class));
                finish();
            }
        });
        mCityKeyword.setText(StringUtils.EMPTY_STRING);
        if (mCityKeyword.requestFocus()) {
            ((InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE)).showSoftInput(this.mCityKeyword, 1);
        }
    }

    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() != android.R.id.home) {
            return super.onOptionsItemSelected(item);
        }
        hideSoftKeyboard(this.mCityKeyword);
        finish();
        overridePendingTransition(R.anim.alpha_in, R.anim.citylist_translate_down);
        return true;
    }

    private void hideSoftKeyboard(View view) {
        ((InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE)).hideSoftInputFromWindow(view
                .getWindowToken(), 0);
    }

    public void onBackPressed() {
        finish();
        overridePendingTransition(R.anim.alpha_in, R.anim.citylist_translate_down);
        super.onBackPressed();
    }

    protected void onResume() {
        super.onResume();
    }

    public void onStop() {
        super.onStop();
    }

    public void onDestroy() {
        super.onDestroy();
        unRegisterReceiver();
    }

    private void startActivityByTransition(View view, Intent intent) {
        startActivity(intent);
        overridePendingTransition(R.anim.alpha_in_listclick, R.anim.alpha_out_listclick);
    }
}
