package com.example.sauravvishal8797.alarmify.activities;

import android.content.Intent;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.example.sauravvishal8797.alarmify.R;
import com.jaeger.library.StatusBarUtil;

public class SplashScreen extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.splashscreen_xml);
        statusBarTransparent();
        Handler handler = new Handler();
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                Intent intent = new Intent(SplashScreen.this, MainActivity.class);
                startActivity(intent);
            }
        };
        handler.postDelayed(runnable, 1000);
    }

    private void statusBarTransparent(){
        StatusBarUtil.setTransparent(this);
    }
}
