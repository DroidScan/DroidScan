package ru.ifmo.se.droidscan;

import android.annotation.TargetApi;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat.OnRequestPermissionsResultCallback;
import androidx.core.content.ContextCompat;

import com.example.droidscan.R;

import java.util.Arrays;

import static android.Manifest.permission.CAMERA;
import static android.Manifest.permission.WRITE_EXTERNAL_STORAGE;
import static android.content.pm.PackageManager.PERMISSION_GRANTED;


public class MainActivity extends AppCompatActivity implements OnRequestPermissionsResultCallback {

    private static final String[] CAMERA_REQUIRED_PERMISSIONS = {
            WRITE_EXTERNAL_STORAGE, CAMERA
    };

    private static final int CAMERA_REQUEST_CODE_PERMISSION = 42;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        hasPermissions();

        if (Arrays.stream(CAMERA_REQUIRED_PERMISSIONS).allMatch(this::hasPermission)) {
            startService();
        }

    }

    private void startService() {
        Intent cameraIntent = new Intent(MainActivity.this, CameraIntentService.class);
        startService(cameraIntent);
    }

    private void showToast(final String text) {
        runOnUiThread(() -> Toast.makeText(getApplicationContext(), text, Toast.LENGTH_LONG).show());
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String permissions[], @NonNull int[] grantResults) {
        switch (requestCode) {
            case CAMERA_REQUEST_CODE_PERMISSION: {
                if (grantResults.length > 0 && Arrays.stream(grantResults).allMatch(MainActivity::checkPermission)) {
                    startService();
                } else {
                    showToast("Сервис не был запущен, теперь мы не можем сфотографировать вас :(");
                }
            }
        }
    }

    @TargetApi(Build.VERSION_CODES.M)
    private void hasPermissions() {
        final String[] neededPermissions = Arrays.stream(CAMERA_REQUIRED_PERMISSIONS)
                .filter(permission -> !hasPermission(permission))
                .toArray(String[]::new);

        if (neededPermissions.length > 0) {
            requestPermissions(neededPermissions, CAMERA_REQUEST_CODE_PERMISSION);
        }
    }

    private boolean hasPermission(final String permission) {
        return checkPermission(ContextCompat.checkSelfPermission(getApplicationContext(), permission));
    }

    private static boolean checkPermission(final int permissionResult) {
        return permissionResult == PERMISSION_GRANTED;
    }

}
