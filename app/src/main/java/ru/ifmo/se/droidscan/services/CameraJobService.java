package ru.ifmo.se.droidscan.services;

import android.app.job.JobParameters;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import static java.lang.String.format;
import static ru.ifmo.se.droidscan.receivers.CameraRequestReceiver.ACTION_USE_CAMERA;


public class CameraJobService extends android.app.job.JobService {
    private static final String TAG = CameraJobService.class.getSimpleName();

    public CameraJobService() {
        super();
    }

    @Override
    public boolean onStartJob(JobParameters params) {
        Log.i(TAG, format("onStartJob: starting job with id: %d", params.getJobId()));
        startService();
        return true;
    }

    @Override
    public boolean onStopJob(JobParameters params) {
        Log.i(TAG, format("onStopJob: stopping job with id: %d", params.getJobId()));
        return true;
    }

    public void startService() {
        Context context = getApplicationContext();

        Intent cameraIntent = new Intent(context, CameraIntentService.class);
        cameraIntent.setAction(ACTION_USE_CAMERA);
        context.startService(cameraIntent);

        Log.d(TAG, "startService");
    }

}
