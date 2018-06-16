package com.opweather.ui;

import android.app.Activity;
import android.content.Context;
import android.location.Location;
import android.location.LocationManager;
import android.location.LocationProvider;
import android.os.Bundle;
import android.os.SystemClock;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationClient;
import com.amap.api.services.geocoder.GeocodeSearch;
import com.opweather.R;
import com.opweather.receiver.AlarmReceiver;
import com.opweather.util.WeatherClientProxy;

public class MockLocation extends Activity implements OnClickListener {
    private static final String TAG = MockLocation.class.getSimpleName();
    private EditText mEditText1;
    private EditText mEditText2;
    private EditText mEditText3;
    private AMapLocation mLocation;
    private Button mLocationButton;
    private AMapLocationClient mLocationClient;
    private LocationManager mLocationManager;
    private Thread mockThead;
    private final String providerStr = GeocodeSearch.GPS;

    public class RunnableMockLocation implements Runnable {
        @Override
        public void run() {
            while (!Thread.currentThread().isInterrupted()) {
                try {
                    Thread.sleep(1000);
                    double lLatitude = Double.parseDouble(mEditText1.getText().toString());
                    double lLongitude = Double.parseDouble(mEditText2.getText().toString());
                    Location mockLocation = new Location(GeocodeSearch.GPS);
                    mockLocation.setLatitude(lLatitude);
                    mockLocation.setLongitude(lLongitude);
                    mockLocation.setAltitude(0.0d);
                    mockLocation.setAccuracy(25.0f);
                    mockLocation.setTime(System.currentTimeMillis());
                    mockLocation.setElapsedRealtimeNanos(SystemClock.elapsedRealtimeNanos());
                    mLocationManager.setTestProviderLocation(GeocodeSearch.GPS, mockLocation);
                    Log.d(MockLocation.TAG, "run: setTestProviderLocation");
                } catch (Exception e) {
                    e.printStackTrace();
                    if (e instanceof InterruptedException) {
                        Thread.currentThread().interrupt();
                    }
                }
            }
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.mock_activity);
        mLocationButton = findViewById(R.id.location_button);
        mEditText1 = findViewById(R.id.editText01);
        mEditText2 = findViewById(R.id.editText02);
        mEditText3 = findViewById(R.id.editText03);
        mLocationButton.setOnClickListener(this);
        mLocationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        if (MainActivity.MOCK_TEST_FLAG) {
            mLocationButton.setText("点击关闭模拟定位");
        } else {
            mLocationButton.setText("点击打开模拟定位");
        }
        if (checkMockEnable()) {
            mockThead = new Thread(new RunnableMockLocation());
        } else {
            Toast.makeText(this, "去设置中选择模拟定位天气", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.location_button:
                Log.d(TAG, "location button");
                if (!checkMockEnable()) {
                    Toast.makeText(this, "去设置中选择模拟定位天气", Toast.LENGTH_SHORT).show();
                    return;
                } else if (!TextUtils.isEmpty(mEditText1.getText().toString()) && !TextUtils.isEmpty
                        (mEditText2.getText().toString())) {
                    if (mockThead == null) {
                        mockThead = new Thread(new RunnableMockLocation());
                    }
                    if (!TextUtils.isEmpty(mEditText3.getText().toString())) {
                        AlarmReceiver.getInstance().getLocation(getApplicationContext(), WeatherClientProxy.CacheMode
                                .LOAD_NO_CACHE, mEditText3.getText().toString());
                    }
                    MainActivity.MOCK_TEST_FLAG = !MainActivity.MOCK_TEST_FLAG;
                    if (MainActivity.MOCK_TEST_FLAG) {
                        Toast.makeText(this, "模拟定位已经启动", Toast.LENGTH_SHORT).show();
                        mockThead.start();
                    } else {
                        Toast.makeText(this, "模拟定位已经关闭", Toast.LENGTH_SHORT).show();
                        ThreadGroup currentGroup = Thread.currentThread().getThreadGroup();
                        int noThreads = currentGroup.activeCount();
                        Thread[] lstThreads = new Thread[noThreads];
                        currentGroup.enumerate(lstThreads);
                        for (int i = 0; i < noThreads; i++) {
                            lstThreads[i].interrupt();
                        }
                    }
                    setResult(-1);
                    finish();
                    return;
                } else {
                    return;
                }
            default:
                return;
        }
    }

    private boolean checkMockEnable() {
        try {
            LocationProvider provider = mLocationManager.getProvider(GeocodeSearch.GPS);
            mLocationManager.addTestProvider(provider.getName(), provider.requiresNetwork(), provider
                    .requiresSatellite(), provider.requiresCell(), provider.hasMonetaryCost(), provider
                    .supportsAltitude(), provider.supportsSpeed(), provider.supportsBearing(), provider
                    .getPowerRequirement(), provider.getAccuracy());
            mLocationManager.setTestProviderEnabled(GeocodeSearch.GPS, true);
            mLocationManager.setTestProviderStatus(GeocodeSearch.GPS, 2, null, System.currentTimeMillis());
            return true;
        } catch (SecurityException e) {
            return false;
        }
    }

    public void stopMockLocation() {
        try {
            mLocationManager.removeTestProvider(GeocodeSearch.GPS);
        } catch (Exception e) {
        }
    }
}
