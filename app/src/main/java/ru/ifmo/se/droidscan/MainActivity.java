package ru.ifmo.se.droidscan;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;


public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    public void recordStart(View view){
        Intent startIntent = new Intent(MainActivity.this, AudioActivity.class);
        startActivity(startIntent);
    }
}
