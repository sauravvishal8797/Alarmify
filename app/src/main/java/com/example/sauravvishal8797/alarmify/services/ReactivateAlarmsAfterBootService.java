package com.example.sauravvishal8797.alarmify.services;

import android.app.AlarmManager;
import android.app.IntentService;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.support.annotation.Nullable;
import android.util.Log;

import com.example.sauravvishal8797.alarmify.activities.AlarmDetailActivity;
import com.example.sauravvishal8797.alarmify.activities.DismissAlarmActivity;
import com.example.sauravvishal8797.alarmify.models.Alarm;
import com.example.sauravvishal8797.alarmify.realm.RealmController;
import com.example.sauravvishal8797.alarmify.receivers.AlarmReceiver;

import java.util.ArrayList;
import java.util.Calendar;

public class ReactivateAlarmsAfterBootService extends IntentService{

    public ReactivateAlarmsAfterBootService(){
        super("ReactivateAlarmsAfterBootService");
    }

    public ReactivateAlarmsAfterBootService(String name) {
        super(name);
    }

    @Override
    protected void onHandleIntent(@Nullable Intent intent) {

        RealmController realmController = RealmController.with(getApplicationContext());
        ArrayList<Alarm> allActivatedAlarms = realmController.getActivatedAlarms();
        AlarmManager alarmManager = (AlarmManager)getApplicationContext().getSystemService(Context.ALARM_SERVICE);
        Calendar now = Calendar.getInstance();
        for (Alarm a: allActivatedAlarms){
            Calendar calendar = Calendar.getInstance();
            calendar.set(Calendar.HOUR_OF_DAY, a.getHour());
            calendar.set(Calendar.MINUTE, a.getMinute());
            if(calendar.before(now)){
                //calendar.add(Calendar.DAY_OF_MONTH, 1);
            } else {
                Intent intent2 = new Intent(getApplicationContext(), AlarmReceiver.class);
                intent.putExtra("alarmtime", a.getTime());
                intent.putExtra("hour", a.getHour());
                intent.putExtra("minutes", a.getMinute());
                intent.putExtra("deleteAfterGoingOff", a.isDeleteAfterGoesOff());
                intent.putExtra("period", a.getPeriod());
                Log.i("monutery", String.valueOf(a.getSnoozeTime()));
                intent.putExtra("snooze", a.getSnoozeTime());
               // Log.i("angmas", String.valueOf(snoozetime));
                intent.putExtra("label", a.getLabel());
                intent.putExtra("repeat", (a.getDays()!=null || !a.getDays().isEmpty()));
                final int _id = (int) System.currentTimeMillis();
                intent.putExtra("id", _id);
                PendingIntent pendingIntent = PendingIntent.getBroadcast(getApplicationContext(), _id, intent, PendingIntent.FLAG_UPDATE_CURRENT);
                //Log.i("fafafafafa", String.valueOf(time_picker.getCurrentHour())+String.valueOf(time_picker.getCurrentMinute()));
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    alarmManager.setAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), pendingIntent);
                } else {
                    alarmManager.set(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), pendingIntent);
                }
            }
        }
    }
}
