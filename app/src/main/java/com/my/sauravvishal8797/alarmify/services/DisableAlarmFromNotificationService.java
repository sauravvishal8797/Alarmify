package com.my.sauravvishal8797.alarmify.services;

import android.app.IntentService;
import android.content.Intent;
import android.support.annotation.Nullable;
import android.util.Log;

import com.my.sauravvishal8797.alarmify.models.Alarm;
import com.my.sauravvishal8797.alarmify.realm.RealmController;

import io.realm.Realm;

public class DisableAlarmFromNotificationService extends IntentService{

    public DisableAlarmFromNotificationService(){super("DisableAlarmFromNotificationService");}


    @Override
    protected void onHandleIntent(@Nullable Intent intent) {
        Realm realm = Realm.getDefaultInstance();
        Alarm alarm = realm.where(Alarm.class).equalTo("time", intent.getStringExtra("time")).findFirst();
        if(alarm!=null){
            realm.beginTransaction();
            alarm.setActivated(false);
            alarm.setNoOfTimesSnoozed(0);
            realm.commitTransaction();
        } else {
            Log.i("nullmessage", "alarm is null");
        }
    }
}
