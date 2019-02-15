package com.example.sauravvishal8797.alarmify.services;

import android.app.IntentService;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.annotation.Nullable;

import com.example.sauravvishal8797.alarmify.R;
import com.example.sauravvishal8797.alarmify.activities.DismissAlarmActivity;
import com.example.sauravvishal8797.alarmify.helpers.PreferenceUtil;

public class Alarmservice extends IntentService {

    public Alarmservice(){
        super("AlarmServiceIntent");
    }

    public Alarmservice(String name) {
        super(name);
    }

    @Override
    protected void onHandleIntent(@Nullable Intent intent) {

        String value = intent.getStringExtra("Data");
        PreferenceUtil SP = PreferenceUtil.getInstance(getApplicationContext());
        //DismissAlarmActivity.repeat=false;
        SharedPreferences.Editor editor = SP.getEditor();
        editor.putString(getResources().getString(R.string.home_button_pressed), "no");
        editor.commit();
        /**SharedPreferences.Editor editor = SP.getEditor();
        editor.putString(getResources().getString(R.string.home_button_pressed), "no");
        editor.commit();*/


        Intent intent1 = new Intent(getApplicationContext(), DismissAlarmActivity.class);
        //intent1.putExtra("putPauseFalse", "false");
        intent1.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent1);

    }
}
