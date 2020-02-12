package ru.ifmo.se.droidscan;

import android.app.IntentService;
import android.content.Intent;
import android.content.Context;
import android.icu.text.SimpleDateFormat;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.widget.Toast;

import androidx.core.content.FileProvider;

import java.io.File;
import java.io.IOException;
import java.util.Date;

import static ru.ifmo.se.droidscan.MainActivity.REQUEST_TAKE_PHOTO;


public class CameraIntentService extends IntentService {

    private final String TAG = "IntentServiceLogs";

    private Uri photoURI;


    public CameraIntentService() {
        super("CameraIntentService");
    }

    public void onCreate() {
        super.onCreate();
        Log.d(TAG, "OnCreate");
    }


    @Override
    protected void onHandleIntent(Intent intent) {

        Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

//      Ensure that there's a camera activity to handle the intent
        if (cameraIntent.resolveActivity(getPackageManager()) != null) {

            // Create the File where the photo should go
            File photoFile = null;
            try {
                photoFile = createImageFile();
                Log.d(TAG,"File was created");
            } catch (IOException ex) {
                // Error occurred while creating the File
                Toast.makeText(this, "Error!", Toast.LENGTH_SHORT).show();
            }

            // Continue only if the File was successfully created
            if (photoFile != null) {
                photoURI = FileProvider.getUriForFile(this,
                        "com.example.android.provider",
                        photoFile);
                cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
            }
        }

        Log.d(TAG, "onHandleIntent");
    }


    private File createImageFile() throws IOException {
        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyMMddHHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp;
        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(imageFileName, ".jpg", storageDir);

        // Save a file: path for use with ACTION_VIEW intents
        return image;
    }

}
