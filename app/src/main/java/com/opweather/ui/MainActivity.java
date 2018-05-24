package com.opweather.ui;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.opweather.R;
import com.opweather.db.CityWeatherDB;
import com.opweather.util.ItemTouchHelper;


public class MainActivity extends AppCompatActivity {

    private static final int UPDATE_UNIT = 88;
    private ViewPager mViewPager;
    private MainFragment mMainFragment;
    private Toolbar mToolbar;
    private ImageView mToolbar_gps;
    private TextView mToolbar_subtitle;
    private TextView mToolbar_title;
    private CityWeatherDB mCityWeatherDB;

    private final Handler mHandler = new Handler() {
        @Override
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

    public interface OnViewPagerScrollListener {
        void onScrolled(float f, int i);

        void onSelected(int i);
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_activity);
        setupActionBar();
        mMainFragment = new MainFragment();
        mViewPager = findViewById(R.id.pager);
        mViewPager.setAdapter(new FragmentAdapter(getSupportFragmentManager()));
        //registerReceiver();
        //init3DView();
        mHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                mCityWeatherDB = CityWeatherDB.getInstance(MainActivity.this);
                initViewPager();
                mViewPager.setOffscreenPageLimit(ItemTouchHelper.RIGHT);

            }
        }, 70);

    }

    private void initViewPager() {
        //String locationId = String.valueOf(PreferenceUtils.getInt(this, WidgetHelper.WIDGET_ID_PREFIX + widgetId,
        // -1));
    }

    private void setupActionBar() {
        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(mToolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayShowTitleEnabled(false);
        }
        mToolbar_gps = (ImageView) findViewById(R.id.toolbar_gps);
        mToolbar_gps.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
              /*  if (MainActivity.this.mMockButtonClickCount > MainActivity.this.MOCK_BUTTON_ENALBE_CONDITION) {
                    MainActivity.this.gotoMocLocation();
                } else {
                    MainActivity.access$2208(MainActivity.this);
                }*/
            }
        });
        mToolbar_title = (TextView) findViewById(R.id.toolbar_title);
        mToolbar_subtitle = (TextView) findViewById(R.id.toolbar_subtitle);
    }


    class FragmentAdapter extends FragmentPagerAdapter {

        public FragmentAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            return mMainFragment;
        }

        @Override
        public int getCount() {
            return 1;
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return true;
    }

    private void refreshViewPagerChild() {
        int childCount = mViewPager.getChildCount();
        for (int i = 0; i < childCount; i++) {
            mViewPager.getChildAt(i).invalidate();
        }
    }
}
