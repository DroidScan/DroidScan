package ru.ifmo.se.droidscan.camera;

import android.content.Context;
import android.graphics.ImageFormat;
import android.hardware.camera2.CameraAccessException;
import android.hardware.camera2.CameraCharacteristics;
import android.hardware.camera2.CameraDevice;
import android.hardware.camera2.CameraManager;
import android.hardware.camera2.CameraMetadata;
import android.hardware.camera2.CaptureRequest;
import android.media.ImageReader;
import android.os.Handler;
import android.util.Log;
import android.util.SparseIntArray;
import android.view.Surface;
import android.view.WindowManager;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;

import ru.ifmo.se.droidscan.camera.imageReader.ImageAvailableListener;

import static java.lang.String.format;
import static java.util.Arrays.stream;
import static ru.ifmo.se.droidscan.camera.CameraUtils.getCameraCharacteristics;
import static ru.ifmo.se.droidscan.camera.imageReader.ImageReaderUtils.buildImageReader;

public class Camera {

    private static final String TAG = Camera.class.getSimpleName();

    private static final SparseIntArray ORIENTATIONS = new SparseIntArray();

    static {
        ORIENTATIONS.append(Surface.ROTATION_0, 270);
        ORIENTATIONS.append(Surface.ROTATION_90, 0);
        ORIENTATIONS.append(Surface.ROTATION_180, 270);
        ORIENTATIONS.append(Surface.ROTATION_270, 180);
    }

    private final CameraManager manager;

    private final WindowManager windowManager;

    private final Handler handler;

    private final CameraDevice.StateCallback stateListener;

    private final Optional<String> frontCameraId;

    public Camera(final Context context, final Handler handler, final Consumer<byte[]> imageWriter) {
        this.manager = (CameraManager) context.getSystemService(Context.CAMERA_SERVICE);
        this.windowManager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);

        this.handler = handler;

        this.frontCameraId = getFrontCameraId();

        this.stateListener = new CameraStateListener(camera -> {
            final String cameraId = camera.getId();

            final ImageReader reader = getCameraCharacteristics(manager, cameraId, CameraCharacteristics.SCALER_STREAM_CONFIGURATION_MAP)
                    .map(streamConfigurationMap -> streamConfigurationMap.getOutputSizes(ImageFormat.JPEG))
                    .filter(sizes -> sizes.length > 0)
                    .map(sizes -> sizes[0])
                    .map(size -> buildImageReader(size.getWidth(), size.getHeight()))
                    .orElseGet(() -> buildImageReader(640, 480));

            final List<Surface> outputSurfaces = Collections.singletonList(reader.getSurface());

            try {
                final CaptureRequest.Builder builder = camera.createCaptureRequest(CameraDevice.TEMPLATE_STILL_CAPTURE);

                builder.addTarget(reader.getSurface());
                builder.set(CaptureRequest.CONTROL_MODE, CameraMetadata.CONTROL_MODE_AUTO);
                builder.set(CaptureRequest.JPEG_ORIENTATION, getOrientation());

                reader.setOnImageAvailableListener(new ImageAvailableListener(imageWriter), handler);

                camera.createCaptureSession(outputSurfaces, new CameraSessionStateListener(builder, handler), handler);
            } catch (CameraAccessException e) {
                Log.e(TAG, format("Exception occurred while accessing %s camera", cameraId), e);
            }
        });

        frontCameraId.ifPresent(this::openFrontCamera);

        Log.d(TAG, "Camera");
    }

    private Optional<String> getFrontCameraId() {
        Optional<String> cameraId;

        try {
            cameraId = stream(manager.getCameraIdList())
                    .filter(id -> {
                        Log.d(TAG, format("check camera with id %s", id));

                        return getCameraCharacteristics(manager, id, CameraCharacteristics.LENS_FACING)
                                .map(characteristic -> characteristic == CameraCharacteristics.LENS_FACING_FRONT)
                                .orElse(false);
                    })
                    .findFirst();
        } catch (final CameraAccessException e) {
            cameraId = Optional.empty();
            Log.e(TAG, "Exception occurred while accessing the list of cameras", e);
        }

        return cameraId;
    }

    private void openFrontCamera(String cameraId) {
        Log.d(TAG, format("opening camera %s", cameraId));

        try {
            manager.openCamera(cameraId, stateListener, handler);
        } catch (CameraAccessException | SecurityException e) {
            Log.e(TAG, format("Exception occurred while opening camera %s", cameraId), e);
        }
    }


    private int getOrientation() {
        final int rotation = windowManager.getDefaultDisplay().getRotation();
        return ORIENTATIONS.get(rotation);
    }
}
