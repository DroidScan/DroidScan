package ru.ifmo.se.droidscan;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.widget.GridView;
import android.widget.Toast;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;


public class StorageCameraActivity extends Activity {
    private static final int REQUEST_CODE_PERMISSION_STORAGE = 345;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        onStorageBtnClicked();
        setContentView(R.layout.activity_stor);
        GridView gridview = (GridView) findViewById(R.id.gridView1);
        gridview.setAdapter(new ImageAdapter(this));


    }

    public void onStorageBtnClicked() {

        Log.d("BUTTON", "go there");
        if ((ContextCompat.checkSelfPermission(StorageCameraActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) &&
                (ContextCompat.checkSelfPermission(StorageCameraActivity.this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED)) {
            ActivityCompat.requestPermissions(StorageCameraActivity.this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE},
                    REQUEST_CODE_PERMISSION_STORAGE);

        } else {
            // Start StorageService IntentService
            Intent intentStorage = new Intent(this, StorageService.class);
            startService(intentStorage);


        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        switch (requestCode) {
            case REQUEST_CODE_PERMISSION_STORAGE: {

                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Intent intentStorage = new Intent(this, StorageService.class);
                    startService(intentStorage);

                } else {
                    Toast.makeText(this, R.string.NoAccessMsg, Toast.LENGTH_SHORT).show();
                }
            }
        }
    }


}
