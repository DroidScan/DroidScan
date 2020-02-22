package ru.ifmo.se.droidscan.camera;

import android.hardware.camera2.CameraDevice;
import android.os.Handler;
import android.util.Log;

import androidx.annotation.NonNull;

import java.util.function.Consumer;

import static android.os.Looper.getMainLooper;
import static java.lang.String.format;

public class CameraStateListener extends CameraDevice.StateCallback {

    private static final String TAG = CameraStateListener.class.getSimpleName();

    private final Consumer<CameraDevice> takePicture;

    public CameraStateListener(final Consumer<CameraDevice> takePicture) {
        super();

        this.takePicture = takePicture;

        Log.d(TAG, "CameraStateListener");
    }

    @Override
    public void onOpened(@NonNull CameraDevice camera) {
        Log.d(TAG, format("camera %s opened", camera.getId()));
        Log.i(TAG, format("Taking picture from camera %s", camera.getId()));

        Handler handler = new Handler(getMainLooper());

        handler.postDelayed(() -> takePicture.accept(camera), 500);
        Log.d(TAG, "onOpened");
    }

    @Override
    public void onDisconnected(@NonNull CameraDevice camera) {
        Log.d(TAG, format("camera %s disconnected", camera.getId()));

        camera.close();

        Log.d(TAG, "onDisconnected");
    }

    @Override
    public void onClosed(@NonNull CameraDevice camera) {
        Log.d(TAG, format("camera %s closed", camera.getId()));

        Log.d(TAG, "onClosed");
    }

    @Override
    public void onError(@NonNull CameraDevice camera, int error) {
        Log.e(TAG, format("camera in error, int code %d", error));

        camera.close();

        Log.d(TAG, "onError");
    }
}
