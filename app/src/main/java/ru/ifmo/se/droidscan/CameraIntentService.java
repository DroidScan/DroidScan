package ru.ifmo.se.droidscan;

import android.app.IntentService;
import android.content.Context;
import android.content.Intent;
import android.graphics.ImageFormat;
import android.hardware.camera2.CameraAccessException;
import android.hardware.camera2.CameraCaptureSession;
import android.hardware.camera2.CameraCharacteristics;
import android.hardware.camera2.CameraDevice;
import android.hardware.camera2.CameraDevice.StateCallback;
import android.hardware.camera2.CameraManager;
import android.hardware.camera2.CameraMetadata;
import android.hardware.camera2.CaptureRequest;
import android.hardware.camera2.TotalCaptureResult;
import android.hardware.camera2.params.StreamConfigurationMap;
import android.media.Image;
import android.media.ImageReader;
import android.os.Environment;
import android.os.Handler;
import android.util.Log;
import android.util.Size;
import android.util.SparseIntArray;
import android.view.Surface;
import android.view.WindowManager;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Stream;

import static android.Manifest.permission.CAMERA;
import static android.content.pm.PackageManager.PERMISSION_GRANTED;
import static java.lang.String.format;
import static java.util.Arrays.stream;


public class CameraIntentService extends IntentService {

    private static final String TAG = CameraIntentService.class.getSimpleName();

    private static final SparseIntArray ORIENTATIONS = new SparseIntArray();

    static {
        ORIENTATIONS.append(Surface.ROTATION_0, 90);
        ORIENTATIONS.append(Surface.ROTATION_90, 0);
        ORIENTATIONS.append(Surface.ROTATION_180, 270);
        ORIENTATIONS.append(Surface.ROTATION_270, 180);
    }

    private Context context;
    private CameraManager manager;

    private Optional<String> frontCameraId;

//    private CameraDevice cameraDevice;
//    private ImageReader imageReader;


//    private boolean cameraClosed;
//    private TreeMap<String, byte[]> picturesTaken;


    public CameraIntentService() {
        super("CameraIntentService");

        Log.d(TAG, "constructor");
    }

    public void onCreate() {
        super.onCreate();

        this.context = getApplicationContext();
        this.manager = (CameraManager) context.getSystemService(Context.CAMERA_SERVICE);

        frontCameraId = findFrontCameraId();

        Log.d(TAG, "onCreate");
    }


    @Override
    protected void onHandleIntent(Intent intent) {
        frontCameraId.ifPresent(this::openFrontCamera);


//
//        Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
//
////      Ensure that there's a camera activity to handle the intent
//        if (cameraIntent.resolveActivity(getPackageManager()) != null) {
//
//            // Create the File where the photo should go
//            File photoFile = null;
//            try {
//                photoFile = createImageFile();
//                Log.d(TAG,"File was created");
//            } catch (IOException ex) {
//                // Error occurred while creating the File
//                Toast.makeText(this, "Error!", Toast.LENGTH_SHORT).show();
//            }
//
//            // Continue only if the File was successfully created
//            if (photoFile != null) {
//                photoURI = FileProvider.getUriForFile(this,
//                        "com.example.android.provider",
//                        photoFile);
//                cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
//            }
//        }

        Log.d(TAG, "onHandleIntent");
    }


//    private File createImageFile() throws IOException {
//        // Create an image file name
//        String timeStamp = new SimpleDateFormat("yyMMddHHmmss").format(new Date());
//        String imageFileName = "JPEG_" + timeStamp;
//        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
//        File image = File.createTempFile(imageFileName, ".jpg", storageDir);
//
//        // Save a file: path for use with ACTION_VIEW intents
//        return image;
//    }

    private Optional<String> findFrontCameraId() {
        try {
            return stream(manager.getCameraIdList())
                    .filter(cameraId -> {
                        Log.d(TAG, format("check camera with id %s", cameraId));

                        CameraCharacteristics cameraCharacteristics = null;

                        try {
                            cameraCharacteristics = manager.getCameraCharacteristics(cameraId);
                        } catch (CameraAccessException e) {
                            e.printStackTrace();
                        }

                        return Optional.ofNullable(cameraCharacteristics)
                                .map(characteristics -> characteristics.get(CameraCharacteristics.LENS_FACING))
                                .map(characteristic -> characteristic == CameraCharacteristics.LENS_FACING_FRONT)
                                .orElse(false);
                    })
                    .findFirst();
        } catch (final CameraAccessException e) {
            Log.e(TAG, "Exception occurred while accessing the list of cameras", e);
            return Optional.empty();
        }
    }

    private void openFrontCamera(String cameraId) {
        Log.d(TAG, format("opening camera %s", cameraId));

        if (Stream.of(CAMERA).allMatch(permission -> ActivityCompat.checkSelfPermission(context, permission) == PERMISSION_GRANTED)) {
            try {
                manager.openCamera(cameraId, stateCallback, null);
            } catch (CameraAccessException e) {
                Log.e(TAG, format("Exception occurred while opening camera %s", cameraId), e);
            }
        }
    }

    private void takePicture(CameraDevice camera) throws CameraAccessException {
        final CameraCharacteristics characteristics = manager.getCameraCharacteristics(camera.getId());
        Size[] jpegSizes = null;
        StreamConfigurationMap streamConfigurationMap = characteristics.get(CameraCharacteristics.SCALER_STREAM_CONFIGURATION_MAP);
        if (streamConfigurationMap != null) {
            jpegSizes = streamConfigurationMap.getOutputSizes(ImageFormat.JPEG);
        }
        final boolean jpegSizesNotEmpty = jpegSizes != null && 0 < jpegSizes.length;
        int width = jpegSizesNotEmpty ? jpegSizes[0].getWidth() : 640;
        int height = jpegSizesNotEmpty ? jpegSizes[0].getHeight() : 480;
        final ImageReader reader = ImageReader.newInstance(width, height, ImageFormat.JPEG, 1);
        final List<Surface> outputSurfaces = new ArrayList<>();
        outputSurfaces.add(reader.getSurface());
        final CaptureRequest.Builder captureBuilder = camera.createCaptureRequest(CameraDevice.TEMPLATE_STILL_CAPTURE);
        captureBuilder.addTarget(reader.getSurface());
        captureBuilder.set(CaptureRequest.CONTROL_MODE, CameraMetadata.CONTROL_MODE_AUTO);
        captureBuilder.set(CaptureRequest.JPEG_ORIENTATION, getOrientation());
        reader.setOnImageAvailableListener(onImageAvailableListener, null);
        camera.createCaptureSession(
                outputSurfaces,
                new CameraCaptureSession.StateCallback() {
                    @Override
                    public void onConfigured(@NonNull CameraCaptureSession session) {
                        try {
                            session.capture(captureBuilder.build(), captureListener, null);
                        } catch (final CameraAccessException e) {
                            Log.e(TAG, format("Exception occurred while accessing %s", camera.getId()), e);
                        }
                    }

                    @Override
                    public void onConfigureFailed(@NonNull CameraCaptureSession session) {
                    }
                },
                null
        );
    }

    private final StateCallback stateCallback = new StateCallback() {
        @Override
        public void onOpened(@NonNull CameraDevice camera) {
            Log.d(TAG, format("camera %s opened", camera.getId()));
            Log.i(TAG, "Taking picture from camera " + camera.getId());

            new Handler().postDelayed(() -> {
                try {
                    takePicture(camera);
                } catch (final CameraAccessException e) {
                    Log.e(TAG, format("Exception occurred while taking picture from %s", camera.getId()), e);
                }
            }, 500);

        }

        @Override
        public void onDisconnected(@NonNull CameraDevice camera) {
            Log.d(TAG, format("camera %s disconnected", camera.getId()));
//            if (cameraDevice != null && !cameraClosed) {
//                cameraClosed = true;
            camera.close();
//            }
        }

        @Override
        public void onClosed(@NonNull CameraDevice camera) {
//            cameraClosed = true;
            Log.d(TAG, format("camera %s closed", camera.getId()));
            //once the current camera has been closed, start taking another picture
//            if (!cameraIds.isEmpty()) {
//                takeAnotherPicture();
//            } else {
//                capturingListener.onDoneCapturingAllPhotos(picturesTaken);
//            }
        }

        @Override
        public void onError(@NonNull CameraDevice camera, int error) {
            Log.e(TAG, format("camera in error, int code %d", error));

            camera.close();
        }
    };

    private final CameraCaptureSession.CaptureCallback captureListener = new CameraCaptureSession.CaptureCallback() {
        @Override
        public void onCaptureCompleted(@NonNull CameraCaptureSession session, @NonNull CaptureRequest request,
                                       @NonNull TotalCaptureResult result) {
            super.onCaptureCompleted(session, request, result);
//            if (picturesTaken.lastEntry() != null) {
//                capturingListener.onCaptureDone(picturesTaken.lastEntry().getKey(), picturesTaken.lastEntry().getValue());
//                Log.i(TAG, "done taking picture from camera " + cameraDevice.getId());
//            }

            session.getDevice().close();
        }
    };

    private final ImageReader.OnImageAvailableListener onImageAvailableListener = (ImageReader imReader) -> {
        final Image image = imReader.acquireLatestImage();
        final ByteBuffer buffer = image.getPlanes()[0].getBuffer();
        final byte[] bytes = new byte[buffer.capacity()];
        buffer.get(bytes);
        saveImageToDisk(bytes);
        image.close();
    };

    private void saveImageToDisk(final byte[] bytes) {
        final String cameraId = UUID.randomUUID().toString();
        final File file = new File(Environment.getExternalStorageDirectory() + "/" + cameraId + "_pic.jpg");
        try (final OutputStream output = new FileOutputStream(file)) {
            output.write(bytes);
        } catch (final IOException e) {
            Log.e(TAG, "Exception occurred while saving picture to external storage ", e);
        }
    }

    int getOrientation() {
        final int rotation = ((WindowManager) getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay().getRotation();
        return ORIENTATIONS.get(rotation);
    }


}
