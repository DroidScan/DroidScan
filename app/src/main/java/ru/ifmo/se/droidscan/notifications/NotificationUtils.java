package ru.ifmo.se.droidscan.notifications;

import android.app.Notification;
import android.app.NotificationManager;
import android.content.Context;
import android.util.Log;

import androidx.core.app.NotificationCompat.Builder;

import ru.ifmo.se.droidscan.R;
import ru.ifmo.se.droidscan.notifications.channels.AbstractChannel;

import static android.app.Notification.CATEGORY_SERVICE;

public class NotificationUtils {
    private static final String TAG = NotificationUtils.class.getSimpleName();

    public static void show(
            final Context context,
            final AbstractChannel channel,
            final NotificationIds notificationId,
            final Integer priority,
            final String title,
            final String text
    ) {
        Notification notification = new Builder(context, channel.getChannel())
                .setSmallIcon(R.drawable.android)
                .setCategory(CATEGORY_SERVICE)
                .setContentTitle(title)
                .setContentText(text)
                .setPriority(priority)
                .build();

        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

        if (notificationManager != null) {
            notificationManager.notify(notificationId.getValue(), notification);
        }

        Log.d(TAG, "show");
    }
}
