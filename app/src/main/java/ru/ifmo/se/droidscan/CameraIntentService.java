package ru.ifmo.se.droidscan;

import android.app.IntentService;
import android.content.Context;
import android.content.Intent;
import android.graphics.ImageFormat;
import android.hardware.camera2.CameraAccessException;
import android.hardware.camera2.CameraCharacteristics;
import android.hardware.camera2.CameraDevice;
import android.hardware.camera2.CameraDevice.StateCallback;
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

import ru.ifmo.se.droidscan.camera.CameraSessionStateListener;
import ru.ifmo.se.droidscan.camera.CameraStateListener;
import ru.ifmo.se.droidscan.camera.ImageAvailableListener;

import static android.os.Looper.getMainLooper;
import static java.lang.String.format;
import static java.util.Arrays.stream;
import static ru.ifmo.se.droidscan.camera.CameraUtils.buildImageReader;
import static ru.ifmo.se.droidscan.camera.CameraUtils.getCameraCharacteristics;


public class CameraIntentService extends IntentService {

    private static final String TAG = CameraIntentService.class.getSimpleName();

    private static final SparseIntArray ORIENTATIONS = new SparseIntArray();

    static {
        ORIENTATIONS.append(Surface.ROTATION_0, 90);
        ORIENTATIONS.append(Surface.ROTATION_90, 0);
        ORIENTATIONS.append(Surface.ROTATION_180, 270);
        ORIENTATIONS.append(Surface.ROTATION_270, 180);
    }

    private final StateCallback stateListener;

    private CameraManager manager;

    private Optional<String> frontCameraId;

    public CameraIntentService() {
        super("CameraIntentService");

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

                reader.setOnImageAvailableListener(new ImageAvailableListener(), null);

                camera.createCaptureSession(outputSurfaces, new CameraSessionStateListener(builder), null);
            } catch (CameraAccessException e) {
                Log.e(TAG, format("Exception occurred while accessing %s camera", cameraId), e);
            }
        });
    }

    public void onCreate() {
        super.onCreate();

        this.manager = (CameraManager) getSystemService(Context.CAMERA_SERVICE);

        this.frontCameraId = findFrontCameraId();

        Log.d(TAG, "onCreate");
    }


    @Override
    protected void onHandleIntent(Intent intent) {
        frontCameraId.ifPresent(this::openFrontCamera);

        Log.d(TAG, "onHandleIntent");
    }

    private Optional<String> findFrontCameraId() {
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
            manager.openCamera(cameraId, stateListener, null);
        } catch (CameraAccessException | SecurityException e) {
            Log.e(TAG, format("Exception occurred while opening camera %s", cameraId), e);
        }
    }

    int getOrientation() {
        final int rotation = ((WindowManager) getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay().getRotation();
        return ORIENTATIONS.get(rotation);
    }


}
