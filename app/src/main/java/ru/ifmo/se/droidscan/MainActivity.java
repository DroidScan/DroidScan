package ru.ifmo.se.droidscan;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

public class MainActivity extends AppCompatActivity implements View.OnClickListener{

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        final View btn_storage = findViewById(R.id.btn_storage);
        btn_storage.setOnClickListener(this);
    }
    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.btn_storage) {
            Intent intent = new Intent(MainActivity.this, StorageCameraActivity.class);
            startActivity(intent);

        }
    }
}
