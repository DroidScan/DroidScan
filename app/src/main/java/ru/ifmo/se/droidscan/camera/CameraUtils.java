package ru.ifmo.se.droidscan.camera;

import android.hardware.camera2.CameraAccessException;
import android.hardware.camera2.CameraCharacteristics;
import android.hardware.camera2.CameraCharacteristics.Key;
import android.hardware.camera2.CameraManager;
import android.util.Log;

import java.util.Optional;

import static java.lang.String.format;

public class CameraUtils {

    private static final String TAG = CameraUtils.class.getSimpleName();

    public static <T> Optional<T> getCameraCharacteristics(final CameraManager manager, final String cameraId, final Key<T> key) {
        CameraCharacteristics cameraCharacteristics = null;

        try {
            cameraCharacteristics = manager.getCameraCharacteristics(cameraId);
        } catch (CameraAccessException e) {
            Log.e(TAG, format("Exception occurred while get camera characteristics %s", cameraId), e);
        }

        return Optional.ofNullable(cameraCharacteristics)
                .map(characteristics -> characteristics.get(key));
    }
}
