package ru.ifmo.se.droidscan;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.View;
import android.widget.Toast;


public class AudioActivity extends AppCompatActivity {

    private final static int MY_PERMISSIONS_REQUEST = 100;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_audio);


        if ((ContextCompat.checkSelfPermission(AudioActivity.this, Manifest.permission.RECORD_AUDIO)
                != PackageManager.PERMISSION_GRANTED) ||
                (ContextCompat.checkSelfPermission(AudioActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                        != PackageManager.PERMISSION_GRANTED) ||
                (ContextCompat.checkSelfPermission(AudioActivity.this, Manifest.permission.READ_EXTERNAL_STORAGE)
                        != PackageManager.PERMISSION_GRANTED) ||
                (ContextCompat.checkSelfPermission(AudioActivity.this, Manifest.permission.FOREGROUND_SERVICE)
                        != PackageManager.PERMISSION_GRANTED) ) {

                ActivityCompat.requestPermissions(AudioActivity.this,
                        new String[]{Manifest.permission.RECORD_AUDIO, Manifest.permission.WRITE_EXTERNAL_STORAGE,
                                Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.FOREGROUND_SERVICE},
                        MY_PERMISSIONS_REQUEST);

        } else {
            startTimer();
        }

    }


    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    startTimer();
                } else {
                    Toast.makeText(this, "Сервис не был запущен, теперь мы не можем подслушивать :( ", Toast.LENGTH_SHORT).show();
                }
            }

        }
    }

    public void startTimer(){

        new CountDownTimer(6000000, 300000) {

            public void onTick(long millisUntilFinished) {
                startService(new Intent(AudioActivity.this, AudioRecorderService.class));
            }

            public void onFinish() {
            }

        }.start();
    }

    public void recordStop(View view){
        stopService(new Intent(this, AudioRecorderService.class));
    }

    public void goBack(View view) {
        Intent startIntent = new Intent(AudioActivity.this, MainActivity.class);
        startActivity(startIntent);
    }

}
