package ru.ifmo.se.droidscan.notifications.channels;

import android.annotation.TargetApi;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.os.Build;

public abstract class AbstractChannel {
    protected final NotificationManager notificationManager;
    protected final NotificationChannel notificationChannel;
    protected final String channel;

    @TargetApi(26)
    protected AbstractChannel(
            NotificationManager notificationManager,
            String channel,
            String name,
            int importance
    ) {
        super();

        this.notificationManager = notificationManager;
        this.channel = channel;
        this.notificationChannel = new NotificationChannel(channel, name, importance);
    }

    protected void create() {
        if (notificationManager != null && Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            notificationManager.createNotificationChannel(this.notificationChannel);
        }
    }

    public String getChannel() {
        return channel;
    }
}
