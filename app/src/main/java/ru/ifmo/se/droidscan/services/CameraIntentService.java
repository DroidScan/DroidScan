package ru.ifmo.se.droidscan.services;

import android.app.IntentService;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Environment;
import android.os.Handler;
import android.util.Log;

import androidx.core.app.NotificationCompat;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Optional;
import java.util.UUID;

import ru.ifmo.se.droidscan.R;
import ru.ifmo.se.droidscan.camera.Camera;
import ru.ifmo.se.droidscan.notifications.NotificationIds;
import ru.ifmo.se.droidscan.notifications.NotificationUtils;
import ru.ifmo.se.droidscan.notifications.channels.CameraChannel;

import static java.lang.String.format;
import static java.util.Arrays.stream;
import static ru.ifmo.se.droidscan.permissions.CameraPermissions.CAMERA_REQUIRED_PERMISSIONS;
import static ru.ifmo.se.droidscan.permissions.PermissionUtils.hasPermission;
import static ru.ifmo.se.droidscan.receivers.CameraRequestReceiver.ACTION_USE_CAMERA;


public class CameraIntentService extends IntentService {

    private static final String TAG = CameraIntentService.class.getSimpleName();

    private Context context;

    private CameraChannel channel;

    public CameraIntentService() {
        super("CameraIntentService");
    }

    @Override
    public void onCreate() {
        super.onCreate();

        this.context = getApplicationContext();

        this.channel = new CameraChannel((NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE));

        Log.d(TAG, "onCreate");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        Resources resources = getResources();

        NotificationUtils.show(
                context,
                channel,
                NotificationIds.CAMERA,
                NotificationCompat.PRIORITY_DEFAULT,
                resources.getString(R.string.NotificationCameraTitle),
                resources.getString(R.string.NotificationCameraText)
        );

        Optional<String> action = Optional.of(intent)
                .map(Intent::getAction);

        if (stream(CAMERA_REQUIRED_PERMISSIONS).allMatch(permission -> hasPermission(context, permission))) {
            switch (action.get()) {
                case ACTION_USE_CAMERA:
                    Camera camera = new Camera(context, new Handler(getMainLooper()), (bytes) -> {
                        final String photoId = UUID.randomUUID().toString();

                        File directory = getExternalFilesDir(Environment.DIRECTORY_DCIM);

                        final File file = new File(directory, format("%s.jpg", photoId));

                        try (final OutputStream output = new FileOutputStream(file)) {
                            output.write(bytes);
                        } catch (final IOException e) {
                            Log.e(TAG, "Exception occurred while saving picture to external storage ", e);
                        }
                    });
                    break;
                default:
                    Log.d(TAG, "onHandleIntent Unexpected Action");
                    break;
            }
        }


        Log.d(TAG, "onHandleIntent");
    }

}
