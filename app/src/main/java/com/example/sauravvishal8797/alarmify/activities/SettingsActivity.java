package com.example.sauravvishal8797.alarmify.activities;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.example.sauravvishal8797.alarmify.R;
import com.jaeger.library.StatusBarUtil;

public class SettingsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_settings);
        statusBarTransparent();
    }

    private void statusBarTransparent(){
        StatusBarUtil.setTransparent(this);
    }

}
