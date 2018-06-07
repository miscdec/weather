package com.opweather.ui;

import android.app.ActionBar;
import android.app.AlertDialog;
import android.content.Context;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.ViewTreeObserver.OnPreDrawListener;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.opweather.R;
import com.opweather.api.nodes.Alarm;
import com.opweather.util.BitmapUtils;
import com.opweather.util.PermissionUtil;
import com.opweather.util.UIUtil;
import com.opweather.util.Utilities;
import com.opweather.widget.openglbase.RainSurfaceView;

import java.util.ArrayList;
import java.util.List;

public class WeatherWarningActivity extends BaseActivity {
    public static final String INTENT_PARA_CITY = "city";
    public static final String INTENT_PARA_WARNING = "warning";
    private WarningAdapter mAdapter;
    private String mCity;
    private ListView mWarningList;
    private int viewWidth;

    class WarningAdapter extends BaseAdapter {
        private boolean isShare;
        private List<Alarm> mAlarms;
        LayoutInflater mInflater;

        class ViewHolder {
            public TextView content;
            public TextView title;

            ViewHolder() {
            }
        }

        public WarningAdapter(Context context, List<Alarm> alarms) {
            mInflater = LayoutInflater.from(context);
            mAlarms = alarms;
        }

        @Override
        public int getCount() {
            return this.mAlarms == null ? 0 : this.mAlarms.size();
        }

        @Override
        public Object getItem(int position) {
            return position;
        }

        @Override
        public long getItemId(int position) {
            return (long) position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder holder;
            if (convertView == null) {
                convertView = mInflater.inflate(R.layout.weather_warning_item, null);
                holder = new ViewHolder();
                holder.content = convertView.findViewById(R.id.weather_warning_content);
                holder.title = convertView.findViewById(R.id.weather_warning_title);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }
            convertView.setTag(holder);
            Alarm alarm = mAlarms.get(position);
            if (isShare) {
                holder.title.setTextColor(ContextCompat.getColor(convertView.getContext(), R.color
                        .oneplus_contorl_text_color_primary_light));
                holder.content.setTextColor(ContextCompat.getColor(convertView.getContext(), R.color
                        .oneplus_contorl_text_color_primary_light));
            } else {
                holder.title.setTextColor(ContextCompat.getColor(convertView.getContext(), R.color
                        .city_search_item_text));
                holder.content.setTextColor(ContextCompat.getColor(convertView.getContext(), R.color
                        .city_search_item_text));
            }
            if (!alarm.getTypeName().equalsIgnoreCase("null")) {
                holder.title.setText(alarm.getTypeName());
            }
            if (!alarm.getContentText().equalsIgnoreCase("null")) {
                holder.content.setText(alarm.getContentText());
            }
            return convertView;
        }
    }

   /* private class SavePic extends DialogLoadingAsyncTask<String, Void, String> {
        private SavePic(OPProgressDialog dialog) {
            super(dialog);
        }

        protected void onPreExecuteExtend() {
            super.onPreExecuteExtend();
        }

        protected String doInBackground(String... params) {
            if (!TextUtils.isEmpty(params[0])) {
                BitmapUtils.savePicByLimit(ScreenShot.createBitmap(WeatherWarningActivity.this,
                WeatherWarningActivity.this.mWarningList, WeatherWarningActivity.this.mCity),
                params[0]);
            }
            return params[0];
        }

        protected void onPostExecuteExtend(String path) {
            if (TextUtils.isEmpty(path)) {
                Toast.makeText(WeatherWarningActivity.this, WeatherWarningActivity.this.getString
                (R.string
                .no_weather_data), 0).show();
                return;
            }
            File f = new File(path);
            Intent intent = new Intent("android.intent.action.SEND");
            intent.setType("image/*");
            intent.putExtra("android.intent.extra.SUBJECT", WeatherWarningActivity.this.getString
            (R.string
            .share_subject));
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            intent.putExtra("android.intent.extra.STREAM", MediaUtil.getInstace().getImageContentUri
            (WeatherWarningActivity.this, f));
            WeatherWarningActivity.this.startActivity(Intent.createChooser(intent,
            WeatherWarningActivity.this
            .getString(R.string.share_title)));
        }
    }*/

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.weather_warning_activity);
        final View layout = findViewById(android.R.id.content);
        mCity = getIntent().getStringExtra(INTENT_PARA_CITY);
        ActionBar bar = getActionBar();
        if (bar != null) {
            View actionbarLayout = LayoutInflater.from(this).inflate(R.layout.top_bar, null);
            bar.setDisplayShowCustomEnabled(true);
            bar.setDisplayHomeAsUpEnabled(true);
            bar.setCustomView(actionbarLayout);
            final TextView titleText = actionbarLayout.findViewById(R.id.top_bar_title);
            final ImageView shareButton = actionbarLayout.findViewById(R.id.top_bar_button);
            titleText.setText(getString(R.string.weather_warning_title, mCity));
            shareButton.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    share();
                }
            });
            layout.getViewTreeObserver().addOnPreDrawListener(new OnPreDrawListener() {
                @Override
                public boolean onPreDraw() {
                    layout.getViewTreeObserver().removeOnPreDrawListener(this);
                    viewWidth = UIUtil.dip2px(layout.getContext(), 50.0f) - shareButton.getWidth();
                    Utilities.measureTextLengthAndSet(titleText, getString(R.string
                                    .weather_warning_title, mCity),
                            viewWidth, 20);
                    return true;
                }
            });
        }
        ArrayList<Parcelable> weather = getIntent().getParcelableArrayListExtra
                (INTENT_PARA_WARNING);
        List<Alarm> alarms = new ArrayList<>();
        if (weather != null) {
            for (int i = 0; i < weather.size(); i++) {
                alarms.add((Alarm) weather.get(i));
            }
        }
        mAdapter = new WarningAdapter(this, alarms);
        mWarningList = findViewById(R.id.weather_warning_list);
        mWarningList.setAdapter(mAdapter);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() != android.R.id.home) {
            return super.onOptionsItemSelected(item);
        }
        finish();
        overridePendingTransition(R.anim.alpha_in, R.anim.citylist_translate_down);
        return true;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[]
            grantResults) {
        switch (requestCode) {
            case RainSurfaceView.RAIN_LEVEL_NORMAL_RAIN:
                if (grantResults.length > 0 && grantResults[0] == 0) {
                    share();
                }
            default:
                break;
        }
    }

    private void share() {
        if (mWarningList != null && PermissionUtil.check(this, "android.permission" +
                        ".WRITE_EXTERNAL_STORAGE",
                getString(R.string.request_permission_storage), 1)) {
            String shareIamgePath = BitmapUtils.getPicFileName(mCity, this);
            AlertDialog alertDialog = new AlertDialog.Builder(this)
                    .setMessage(getResources().getString(R.string.generate_image))
                    .create();
            //new SavePic(alertDialog, null).execute(new String[]{shareIamgePath});
        }
    }

    private List<Alarm> getAlarms(ArrayList<Parcelable> weather) {
        if (weather == null || weather.size() < 1) {
            return null;
        }
        List<Alarm> resAlarms = new ArrayList();
        resAlarms.add((Alarm) weather.get(0));
        int countWeather = weather.size();
        int i = 1;
        while (i < countWeather) {
            try {
                Alarm tempAlarm = (Alarm) weather.get(i);
                int count = resAlarms.size();
                int j = 0;
                while (j < count && !((Alarm) resAlarms.get(j)).getTypeName().equals(tempAlarm
                        .getTypeName())) {
                    resAlarms.add(tempAlarm);
                    j++;
                }
                i++;
            } catch (Exception e) {
                return null;
            }
        }
        return resAlarms;
    }
}
