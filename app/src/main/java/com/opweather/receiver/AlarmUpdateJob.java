package com.opweather.receiver;

import android.app.job.JobParameters;
import android.app.job.JobService;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

public class AlarmUpdateJob extends JobService {
    public static final int JOB_MESSAGE_WHAT = Integer.MIN_VALUE;
    private static final String TAG = AlarmUpdateJob.class.getSimpleName();
    private Handler mHandler;

    public boolean onStartJob(final JobParameters params) {
        Log.d(TAG, "onStartJob: ");
        mHandler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                if (msg.what == Integer.MIN_VALUE) {
                    jobFinished(params, false);
                    Log.d(TAG, "jobFinished");
                }
            }
        };
        AlarmReceiver.getInstance().updateWarning(this, mHandler);
        return true;
    }

    public boolean onStopJob(JobParameters params) {
        Log.d(TAG, "onStartJob: ");
        mHandler.removeCallbacksAndMessages(null);
        return true;
    }
}
