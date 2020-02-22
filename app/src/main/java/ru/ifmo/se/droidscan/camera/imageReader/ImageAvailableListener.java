package ru.ifmo.se.droidscan.camera.imageReader;

import android.media.Image;
import android.media.ImageReader;
import android.util.Log;

import java.nio.ByteBuffer;
import java.util.function.Consumer;

public class ImageAvailableListener implements ImageReader.OnImageAvailableListener {

    private static final String TAG = ImageAvailableListener.class.getSimpleName();

    private final Consumer<byte[]> writer;

    public ImageAvailableListener(final Consumer<byte[]> writer) {
        this.writer = writer;
    }

    @Override
    public void onImageAvailable(ImageReader reader) {
        final Image image = reader.acquireLatestImage();
        final ByteBuffer buffer = image.getPlanes()[0].getBuffer();

        final byte[] bytes = new byte[buffer.capacity()];

        buffer.get(bytes);

        this.writer.accept(bytes);

        image.close();

        Log.d(TAG, "onImageAvailable");
    }
}
