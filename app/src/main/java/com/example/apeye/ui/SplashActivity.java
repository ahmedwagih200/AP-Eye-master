package com.example.apeye.ui;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

import com.example.apeye.ui.main.MainActivity;
import com.example.apeye.R;

import androidx.appcompat.app.AppCompatActivity;

public class SplashActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splach);
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                startActivity(new Intent(SplashActivity.this, MainActivity.class));
                finish();
            }
        }, 1000);

    }
}
