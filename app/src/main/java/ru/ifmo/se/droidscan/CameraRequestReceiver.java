package ru.ifmo.se.droidscan;

import android.app.job.JobInfo;
import android.app.job.JobScheduler;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import java.util.Objects;
import java.util.concurrent.TimeUnit;

import ru.ifmo.se.droidscan.services.CameraJobService;

public class CameraRequestReceiver extends BroadcastReceiver {
    private static final String TAG = CameraRequestReceiver.class.getSimpleName();


    public static final String ACTION_USE_CAMERA = "ACTION_USE_CAMERA";
    private static int sJobId = 1;

    @Override
    public void onReceive(Context context, Intent intent) {
        Log.d(TAG, "onReceive: action: " + intent.getAction());
        switch (Objects.requireNonNull(intent.getAction())) {
            case ACTION_USE_CAMERA:
                sheduleJob(context);
                break;
            default:
                throw new IllegalArgumentException("Unknown action");
        }
    }

    private void sheduleJob(Context context){
        ComponentName jobService = new ComponentName(context, CameraJobService.class);
        JobInfo.Builder cameraJobBuilder = new JobInfo.Builder(sJobId, jobService);
        cameraJobBuilder.setRequiresDeviceIdle(false);
        cameraJobBuilder.setRequiresCharging(false);
        cameraJobBuilder.setRequiredNetworkType(JobInfo.NETWORK_TYPE_NONE);

        long PERIOD = TimeUnit.SECONDS.toMillis(10000);
        cameraJobBuilder.setPeriodic(PERIOD);

        JobInfo jobInfo = cameraJobBuilder.build();
        JobScheduler jobScheduler = (JobScheduler) context.getSystemService(Context.JOB_SCHEDULER_SERVICE);
        if (jobScheduler != null) {
            jobScheduler.schedule(jobInfo);
        }

        Log.i(TAG, "scheduleJob: adding job to scheduler");

        int result = jobScheduler.schedule(cameraJobBuilder.build());
        if (result == JobScheduler.RESULT_SUCCESS) {
            Log.i(TAG,"Job scheduled successfully!");
        } else {
            Log.e(TAG,"Job did not scheduled!");
        }
    }
}
