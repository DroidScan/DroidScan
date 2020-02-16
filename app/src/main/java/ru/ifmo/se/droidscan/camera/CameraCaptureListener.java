package ru.ifmo.se.droidscan.camera;

import android.hardware.camera2.CameraCaptureSession;
import android.hardware.camera2.CaptureRequest;
import android.hardware.camera2.TotalCaptureResult;
import android.util.Log;

import androidx.annotation.NonNull;

public class CameraCaptureListener extends CameraCaptureSession.CaptureCallback {
    private static final String TAG = CameraCaptureListener.class.getSimpleName();

    public CameraCaptureListener() {
        super();
    }

    @Override
    public void onCaptureCompleted(@NonNull CameraCaptureSession session, @NonNull CaptureRequest request, @NonNull TotalCaptureResult result) {
        super.onCaptureCompleted(session, request, result);

        session.getDevice().close();

        Log.d(TAG, "onCaptureCompleted");

    }
}
