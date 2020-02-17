package ru.ifmo.se.droidscan.permissions;

import static android.Manifest.permission.CAMERA;
import static android.Manifest.permission.WRITE_EXTERNAL_STORAGE;

public class CameraPermissions {

    public static final String[] CAMERA_REQUIRED_PERMISSIONS = {
            WRITE_EXTERNAL_STORAGE, CAMERA
    };

    public static final int CAMERA_REQUEST_CODE_PERMISSION = 42;
}
