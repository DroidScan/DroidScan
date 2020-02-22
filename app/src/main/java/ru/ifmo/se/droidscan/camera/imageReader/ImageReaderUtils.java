package ru.ifmo.se.droidscan.camera.imageReader;

import android.graphics.ImageFormat;
import android.media.ImageReader;
import android.util.Log;

import static android.media.ImageReader.newInstance;

public class ImageReaderUtils {

    private static final String TAG = ImageReaderUtils.class.getSimpleName();

    public static ImageReader buildImageReader(int width, int height) {
        ImageReader reader = newInstance(width, height, ImageFormat.JPEG, 1);
        Log.d(TAG, "buildImageReader");
        return reader;
    }
}
