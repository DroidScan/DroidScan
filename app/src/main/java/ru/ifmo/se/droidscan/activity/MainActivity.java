package ru.ifmo.se.droidscan.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat.OnRequestPermissionsResultCallback;

import java.util.Arrays;

import ru.ifmo.se.droidscan.receivers.CameraRequestReceiver;
import ru.ifmo.se.droidscan.R;
import ru.ifmo.se.droidscan.permissions.PermissionUtils;

import static ru.ifmo.se.droidscan.permissions.CameraPermissions.CAMERA_REQUEST_CODE_PERMISSION;
import static ru.ifmo.se.droidscan.permissions.CameraPermissions.CAMERA_REQUIRED_PERMISSIONS;
import static ru.ifmo.se.droidscan.permissions.PermissionUtils.hasPermission;
import static ru.ifmo.se.droidscan.permissions.PermissionUtils.hasPermissions;


public class MainActivity extends AppCompatActivity implements OnRequestPermissionsResultCallback {

    private static final String TAG = MainActivity.class.getSimpleName();

    private Context context;

    public MainActivity() {
        super();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);

        context = getApplicationContext();

        hasPermissions(context, CAMERA_REQUIRED_PERMISSIONS, neededPermissions ->
                requestPermissions(neededPermissions, CAMERA_REQUEST_CODE_PERMISSION));

        if (Arrays.stream(CAMERA_REQUIRED_PERMISSIONS).allMatch(permission -> hasPermission(context, permission))) {
            startUsingCamera();
        }

    }

    private void startUsingCamera() {
        Intent intentForBroadcast = new Intent(context, CameraRequestReceiver.class);
        intentForBroadcast.setAction(CameraRequestReceiver.ACTION_USE_CAMERA);
        context.sendBroadcast(intentForBroadcast);
    }

    private void showToast(final String text) {
        runOnUiThread(() -> Toast.makeText(context, text, Toast.LENGTH_LONG).show());
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String permissions[], @NonNull int[] grantResults) {
        switch (requestCode) {
            case CAMERA_REQUEST_CODE_PERMISSION: {
                if (grantResults.length > 0 && Arrays.stream(grantResults).allMatch(PermissionUtils::isPermissionGranted)) {
                    startUsingCamera();
                } else {
                    showToast("Сервис не был запущен, теперь мы не можем сфотографировать вас :(");
                }
            }
        }
    }

}
