package ru.ifmo.se.droidscan.camera;

import android.graphics.ImageFormat;
import android.hardware.camera2.CameraAccessException;
import android.hardware.camera2.CameraCharacteristics;
import android.hardware.camera2.CameraCharacteristics.Key;
import android.hardware.camera2.CameraManager;
import android.media.ImageReader;
import android.os.Environment;
import android.util.Log;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Optional;
import java.util.UUID;

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

    public static ImageReader buildImageReader(int width, int height) {
        return ImageReader.newInstance(width, height, ImageFormat.JPEG, 1);
    }

    public static void writeImage(final byte[] bytes) {
        final String cameraId = UUID.randomUUID().toString();

        final File file = new File(Environment.getExternalStorageDirectory() + "/" + cameraId + "_pic.jpg");

        try (final OutputStream output = new FileOutputStream(file)) {
            output.write(bytes);
        } catch (final IOException e) {
            Log.e(TAG, "Exception occurred while saving picture to external storage ", e);
        }
    }
}
