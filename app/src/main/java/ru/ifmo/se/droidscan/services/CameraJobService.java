package ru.ifmo.se.droidscan.services;

import android.app.job.JobParameters;
import android.util.Log;


public class CameraJobService extends android.app.job.JobService {
    private static final String TAG = CameraJobService.class.getSimpleName();

    public CameraJobService() {
    }
    @Override
    public boolean onStartJob(JobParameters params) {
        Log.i(TAG, "onStartJob: starting job with id: " + params.getJobId());
        CameraIntentService.startService(getApplicationContext());
        return true;
    }

    @Override
    public boolean onStopJob(JobParameters params) {
        Log.i(TAG, "onStopJob: stopping job with id: " + params.getJobId());
        return true;
    }

}
