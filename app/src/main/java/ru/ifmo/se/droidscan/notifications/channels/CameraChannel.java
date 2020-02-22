package ru.ifmo.se.droidscan.notifications.channels;

import android.annotation.TargetApi;
import android.app.NotificationManager;
import android.graphics.Color;

public class CameraChannel extends AbstractChannel {

    @TargetApi(26)
    public CameraChannel(
            NotificationManager notificationManager
    ) {
        super(
                notificationManager,
                "camera channel",
                "camera",
                NotificationManager.IMPORTANCE_HIGH
        );

        notificationChannel.enableLights(true);
        notificationChannel.setLightColor(Color.BLUE);

        create();
    }

}
