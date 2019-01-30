package com.example.sauravvishal8797.alarmify.activities;

import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.example.sauravvishal8797.alarmify.R;
import com.example.sauravvishal8797.alarmify.fragments.BlankFragment;
import com.example.sauravvishal8797.alarmify.fragments.SettingsFragment;
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
