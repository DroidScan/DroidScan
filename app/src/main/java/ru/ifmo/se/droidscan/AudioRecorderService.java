package ru.ifmo.se.droidscan;

import android.annotation.TargetApi;
import android.app.IntentService;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Intent;
import android.content.Context;
import android.graphics.Color;
import android.media.MediaRecorder;
import android.os.Build;
import android.os.Environment;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;

import java.io.File;
import java.util.concurrent.TimeUnit;

import static androidx.core.app.NotificationCompat.PRIORITY_HIGH;

public class AudioRecorderService extends IntentService {

    private File audioDir;
    private MediaRecorder mediaRecorder;
    private String fileName;
    private int count;


    public AudioRecorderService() {
        super("AudioRecorderService");
    }


    @Override
    protected void onHandleIntent(Intent intent) {

        startForeground(1, getNotification());

        try {
            if (audioDir.exists()) {
                count = audioDir.listFiles().length + 1;
            }

            fileName = audioDir.getAbsolutePath() + "/record" + count + ".mp3";
            Log.println(Log.INFO, "", "\n"  + fileName);

            prepareRecorder();
            recordStart();

            Toast.makeText(this, "Запись началась", Toast.LENGTH_SHORT).show();

            TimeUnit.SECONDS.sleep(10);

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            stopForeground(true);
        }
    }

    @Override
    public void onCreate() {
        super.onCreate();

        audioDir = getApplicationContext().getExternalFilesDir(Environment.DIRECTORY_MUSIC);
        Log.println(Log.INFO, "", audioDir.getAbsolutePath());

        Toast.makeText(this, " Сервис создан", Toast.LENGTH_SHORT).show();

    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        recordStop();
        Toast.makeText(this, "Запись остановлена", Toast.LENGTH_SHORT).show();

    }

    public Notification getNotification() {
        String channel;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)
            channel = createChannel();
        else {
            channel = "";
        }
        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(this, channel)
                .setSmallIcon(android.R.drawable.ic_media_ff)
                .setContentTitle("Используется микрофон")
                .setContentText("Мы тут секунд 10 просто позаписываем все, что вы говорите :) "); // use something from something from

        Notification notification = mBuilder
                .setPriority(PRIORITY_HIGH)
                .setCategory(Notification.CATEGORY_SERVICE)
                .build();

        return notification;
    }

    @NonNull
    @TargetApi(26)
    private synchronized String createChannel() {
        NotificationManager mNotificationManager = (NotificationManager) this.getSystemService(Context.NOTIFICATION_SERVICE);

        String name = "micro";
        int importance = NotificationManager.IMPORTANCE_HIGH;

        NotificationChannel mChannel = new NotificationChannel("micro channel", name, importance);

        mChannel.enableLights(true);
        mChannel.setLightColor(Color.BLUE);
        if (mNotificationManager != null) {
            mNotificationManager.createNotificationChannel(mChannel);
        } else {
            stopSelf();
        }
        return "micro channel";
    }

    public void prepareRecorder() {
        try {
            releaseRecorder();

            mediaRecorder = new MediaRecorder();
            mediaRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
            mediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.MPEG_4);
            mediaRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);
            mediaRecorder.setMaxDuration(10000);
            mediaRecorder.setOutputFile(fileName);
            mediaRecorder.prepare();

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public void recordStart() {
        try {
            mediaRecorder.start();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void recordStop() {
        if (mediaRecorder != null) {
            mediaRecorder.stop();
            releaseRecorder();
        }
    }


    private void releaseRecorder() {
        if (mediaRecorder != null) {
            mediaRecorder.release();
            mediaRecorder = null;
        }
    }
}
