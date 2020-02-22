package ru.ifmo.se.droidscan.camera;

import android.hardware.camera2.CameraAccessException;
import android.hardware.camera2.CameraCaptureSession;
import android.os.Handler;
import android.util.Log;

import androidx.annotation.NonNull;

import static android.hardware.camera2.CaptureRequest.Builder;
import static java.lang.String.format;

public class CameraSessionStateListener extends CameraCaptureSession.StateCallback {

    private static final String TAG = CameraSessionStateListener.class.getSimpleName();

    private final Builder builder;

    private final Handler handler;

    public CameraSessionStateListener(final Builder builder, final Handler handler) {
        super();

        this.builder = builder;
        this.handler = handler;
    }

    @Override
    public void onConfigured(@NonNull CameraCaptureSession session) {
        try {
            session.capture(builder.build(), new CameraCaptureListener(), handler);
        } catch (final CameraAccessException e) {
            Log.e(TAG, format("Exception occurred while accessing %s camera", session.getDevice().getId()), e);
        }

        Log.d(TAG, "onConfigured");
    }

    @Override
    public void onConfigureFailed(@NonNull CameraCaptureSession session) {
        Log.d(TAG, "onConfigureFailed");
    }
}
