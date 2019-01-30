package com.example.sauravvishal8797.alarmify.services;

import android.app.IntentService;
import android.content.Intent;
import android.support.annotation.Nullable;

import com.example.sauravvishal8797.alarmify.activities.DismissAlarmActivity;

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
        //DismissAlarmActivity.repeat=false;

        Intent intent1 = new Intent(getApplicationContext(), DismissAlarmActivity.class);
        //intent1.putExtra("putPauseFalse", "false");
        intent1.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent1);

    }


}
