package ru.ifmo.se.droidscan.camera;

import android.hardware.camera2.CameraAccessException;
import android.hardware.camera2.CameraCaptureSession;
import android.hardware.camera2.CaptureRequest;
import android.util.Log;

import androidx.annotation.NonNull;

import static java.lang.String.format;

public class CameraSessionStateListener extends CameraCaptureSession.StateCallback {
    private static final String TAG = CameraSessionStateListener.class.getSimpleName();

    private final CaptureRequest.Builder builder;

    public CameraSessionStateListener(final CaptureRequest.Builder builder) {
        super();

        this.builder = builder;
    }

    @Override
    public void onConfigured(@NonNull CameraCaptureSession session) {
        try {
            session.capture(builder.build(), new CameraCaptureListener(), null);
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
