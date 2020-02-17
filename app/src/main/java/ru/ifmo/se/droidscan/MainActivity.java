package ru.ifmo.se.droidscan;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat.OnRequestPermissionsResultCallback;

import com.example.droidscan.R;

import java.util.Arrays;

import ru.ifmo.se.droidscan.permissions.PermissionUtils;

import static ru.ifmo.se.droidscan.permissions.CameraPermissions.CAMERA_REQUEST_CODE_PERMISSION;
import static ru.ifmo.se.droidscan.permissions.CameraPermissions.CAMERA_REQUIRED_PERMISSIONS;
import static ru.ifmo.se.droidscan.permissions.PermissionUtils.hasPermission;
import static ru.ifmo.se.droidscan.permissions.PermissionUtils.hasPermissions;


public class MainActivity extends AppCompatActivity implements OnRequestPermissionsResultCallback {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        hasPermissions(getApplicationContext(), CAMERA_REQUIRED_PERMISSIONS, neededPermissions ->
                requestPermissions(neededPermissions, CAMERA_REQUEST_CODE_PERMISSION));

        if (Arrays.stream(CAMERA_REQUIRED_PERMISSIONS).allMatch(permission -> hasPermission(getApplicationContext(), permission))) {
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
                if (grantResults.length > 0 && Arrays.stream(grantResults).allMatch(PermissionUtils::isPermissionGranted)) {
                    startService();
                } else {
                    showToast("Сервис не был запущен, теперь мы не можем сфотографировать вас :(");
                }
            }
        }
    }

}
