package ru.ifmo.se.droidscan.camera;

import android.media.Image;
import android.media.ImageReader;
import android.util.Log;

import java.nio.ByteBuffer;

import static ru.ifmo.se.droidscan.camera.CameraUtils.writeImage;

public class ImageAvailableListener implements ImageReader.OnImageAvailableListener {
    private static final String TAG = ImageAvailableListener.class.getSimpleName();

    @Override
    public void onImageAvailable(ImageReader reader) {
        final Image image = reader.acquireLatestImage();
        final ByteBuffer buffer = image.getPlanes()[0].getBuffer();

        final byte[] bytes = new byte[buffer.capacity()];

        buffer.get(bytes);
        writeImage(bytes);
        image.close();

        Log.d(TAG, "onImageAvailable");
    }
}
