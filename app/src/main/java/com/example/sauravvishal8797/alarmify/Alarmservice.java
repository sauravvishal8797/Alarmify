package com.example.sauravvishal8797.alarmify;

import android.app.IntentService;
import android.content.Intent;
import android.support.annotation.Nullable;

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
        Intent intent1 = new Intent(getApplicationContext(), Mathspuzzle.class);
        intent1.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent1);

    }
}
