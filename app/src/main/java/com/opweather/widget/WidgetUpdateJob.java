package com.opweather.widget;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v4.app.JobIntentService;

public class WidgetUpdateJob extends JobIntentService {
    public static final int UPDATE_JOBID = 1010;

    public static void enqueueWork(Context context, Intent work) {
        enqueueWork(context, WidgetUpdateJob.class, UPDATE_JOBID, work);
    }

    protected void onHandleWork(@NonNull Intent intent) {
        //WidgetHelper.getInstance(this).updateAllWidget(true);
    }
}
