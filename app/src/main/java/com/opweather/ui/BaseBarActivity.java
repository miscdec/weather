package com.opweather.ui;

import android.app.ActionBar;
import android.app.Activity;
import android.os.Bundle;
import android.view.MenuItem;

public class BaseBarActivity extends Activity {
    private ActionBar bar;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        bar = getActionBar();
        if (bar != null) {
            bar.setDisplayHomeAsUpEnabled(true);
        }
    }

    protected void onResume() {
        super.onResume();
    }

    protected void onPause() {
        super.onPause();
    }

    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() != android.R.id.home) {
            return super.onOptionsItemSelected(item);
        }
        finish();
        return true;
    }

    protected void setBarTitle(String title) {
        if (bar != null) {
            bar.setTitle(title);
        } else {
            setTitle(title);
        }
    }

    protected void setBarTitle(int titleId) {
        if (bar != null) {
            bar.setTitle(getString(titleId));
        } else {
            setTitle(titleId);
        }
    }
}
