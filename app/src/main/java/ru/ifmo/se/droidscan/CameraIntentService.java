package ru.ifmo.se.droidscan;

import android.app.IntentService;
import android.content.Context;
import android.content.Intent;
import android.hardware.camera2.CameraManager;
import android.os.Handler;
import android.util.Log;
import android.view.WindowManager;

import ru.ifmo.se.droidscan.camera.Camera;


public class CameraIntentService extends IntentService {

    private static final String TAG = CameraIntentService.class.getSimpleName();

    public CameraIntentService() {
        super("CameraIntentService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        Camera camera = new Camera(
                (CameraManager) getSystemService(Context.CAMERA_SERVICE),
                (WindowManager) getSystemService(Context.WINDOW_SERVICE),
                new Handler(getMainLooper())
        );

        camera.takePhoto();

        Log.d(TAG, "onHandleIntent");
    }


}
