package com.my.sauravvishal8797.alarmify.services;

import android.app.AlarmManager;
import android.app.IntentService;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.support.annotation.Nullable;

import com.my.sauravvishal8797.alarmify.receivers.AlarmBroadCastReceiver;

public class AlarmActivateService extends IntentService{

    AlarmManager alarmManager;

    public AlarmActivateService(){
        super("AlarmActivateService");
    }
    @Override
    protected void onHandleIntent(@Nullable Intent intent) {

        AlarmManager alarmManager = (AlarmManager)getSystemService(Context.ALARM_SERVICE);
        Intent intent1 = new Intent(this, AlarmBroadCastReceiver.class);
        final int _id = (int) System.currentTimeMillis();

        PendingIntent pendingIntent = PendingIntent.getBroadcast(this,
                _id, intent1, PendingIntent.FLAG_UPDATE_CURRENT);
        if (Build.VERSION.SDK_INT >= 23)
        {
            alarmManager.setAlarmClock(new AlarmManager.AlarmClockInfo(30000, pendingIntent), pendingIntent);
        }
        else if (Build.VERSION.SDK_INT >= 19)
        {
            alarmManager.setExact(AlarmManager.RTC_WAKEUP, 30000, pendingIntent);
        }
        else
        {
            alarmManager.set(AlarmManager.RTC_WAKEUP, 30000, pendingIntent);
        }
    }
}
