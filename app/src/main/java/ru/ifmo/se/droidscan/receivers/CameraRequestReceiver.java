package ru.ifmo.se.droidscan.receivers;

import android.app.job.JobInfo;
import android.app.job.JobInfo.Builder;
import android.app.job.JobScheduler;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import java.util.Optional;

import ru.ifmo.se.droidscan.services.CameraJobService;

public class CameraRequestReceiver extends BroadcastReceiver {
    private static final String TAG = CameraRequestReceiver.class.getSimpleName();
    private static final Long INTERVAL = 900000L;
    public static final String ACTION_USE_CAMERA = "ACTION_USE_CAMERA";

    private static int sJobId = 1;

    @Override
    public void onReceive(Context context, Intent intent) {
        Optional<String> action = Optional.of(intent)
                .map(Intent::getAction);

        switch (action.get()) {
            case ACTION_USE_CAMERA:
                scheduleJob(context);
                break;
            default:
                Log.d(TAG, "onReceive: Unexpected Action");
                break;

        }

        Log.d(TAG, "onReceive");
    }

    private void scheduleJob(Context context){
        ComponentName jobService = new ComponentName(context, CameraJobService.class);

        JobInfo jobInfo = new Builder(sJobId, jobService)
                .setRequiresDeviceIdle(false)
                .setRequiresCharging(false)
                .setRequiredNetworkType(JobInfo.NETWORK_TYPE_NONE)
                .setPeriodic(INTERVAL)
                .build();

        Optional.ofNullable((JobScheduler) context.getSystemService(Context.JOB_SCHEDULER_SERVICE))
                .ifPresent(jobScheduler -> {
                    int result = jobScheduler.schedule(jobInfo);

                    if (result == JobScheduler.RESULT_SUCCESS) {
                        Log.i(TAG, "scheduleJob: adding job to scheduler");
                    } else {
                        Log.e(TAG,"Job did not scheduled!");
                    }
                });

        Log.d(TAG, "scheduleJob");
    }
}
