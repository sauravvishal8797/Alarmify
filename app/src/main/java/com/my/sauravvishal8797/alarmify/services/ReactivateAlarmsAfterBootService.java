package com.my.sauravvishal8797.alarmify.services;

import android.app.AlarmManager;
import android.app.IntentService;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.support.annotation.Nullable;
import android.util.Log;

import com.my.sauravvishal8797.alarmify.activities.DismissAlarmActivity;
import com.my.sauravvishal8797.alarmify.models.Alarm;
import com.my.sauravvishal8797.alarmify.realm.RealmController;
import com.my.sauravvishal8797.alarmify.receivers.AlarmReceiver;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;

import io.realm.Realm;
import io.realm.RealmResults;

public class ReactivateAlarmsAfterBootService extends IntentService{

    public ReactivateAlarmsAfterBootService(){
        super("ReactivateAlarmsAfterBootService");
    }

    public ReactivateAlarmsAfterBootService(String name) {
        super(name);
    }

    @Override
    protected void onHandleIntent(@Nullable Intent intent) {

      //  RealmController realmController = RealmController.with(getApplicationContext());
        Realm realm = Realm.getDefaultInstance();
        ArrayList<Alarm> activatedAlarms = new ArrayList<>();
        RealmResults<Alarm> allAlarms = realm.where(Alarm.class).findAll();
        for(Alarm a: allAlarms){
            if(a.isActivated()){
                activatedAlarms.add(a);
            }
        }
      //  ArrayList<Alarm> allActivatedAlarms = realmController.getActivatedAlarms();
        AlarmManager alarmManager = (AlarmManager)getApplicationContext().getSystemService(Context.ALARM_SERVICE);
        Calendar now = Calendar.getInstance();
        for (Alarm a: activatedAlarms){
            Calendar calendar = Calendar.getInstance();
            calendar.set(Calendar.HOUR_OF_DAY, a.getHour());
            calendar.set(Calendar.MINUTE, a.getMinute());
            if(calendar.before(now)){
                if(!a.getDays().equals("No Repeat")){
                    calendar.add(Calendar.DAY_OF_MONTH, 1);
                    Intent intent2 = new Intent(getApplicationContext(), AlarmReceiver.class);
                    intent2.putExtra("alarmtime", a.getTime());
                    intent2.putExtra("hour", a.getHour());
                    intent2.putExtra("minutes", a.getMinute());
                    intent2.putExtra("deleteAfterGoingOff", a.isDeleteAfterGoesOff());
                    intent2.putExtra("period", a.getPeriod());
                    intent2.putExtra("snooze", a.getSnoozeTime());
                    intent2.putExtra("label", a.getLabel());
                    intent2.putExtra("repeat", (a.getDays().equals("No Repeat"))?0:1);
                    if(!a.getDays().equals("No Repeat")){
                        List<String> repeatDays = Arrays.asList(a.getDays().split(" "));
                        intent2.putStringArrayListExtra("repeatList", new ArrayList<>(repeatDays));
                    }
                    final int _id = (int) System.currentTimeMillis();
                    intent2.putExtra("id", _id);
                    PendingIntent pendingIntent = PendingIntent.getBroadcast(getApplicationContext(), _id, intent2, PendingIntent.FLAG_UPDATE_CURRENT);
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                        alarmManager.setAlarmClock(new AlarmManager.AlarmClockInfo(calendar.getTimeInMillis(), pendingIntent), pendingIntent);
                        //alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), pendingIntent);
                        //alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), pendingIntent);
                    } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP){
                        alarmManager.setExact(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), pendingIntent);
                    } else {
                        alarmManager.set(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), pendingIntent);
                    }
                }
            } else {
                Intent intent2 = new Intent(getApplicationContext(), AlarmReceiver.class);
                intent2.putExtra("alarmtime", a.getTime());
                intent2.putExtra("hour", a.getHour());
                intent2.putExtra("minutes", a.getMinute());
                intent2.putExtra("deleteAfterGoingOff", a.isDeleteAfterGoesOff());
                intent2.putExtra("period", a.getPeriod());
                intent2.putExtra("snooze", a.getSnoozeTime());
                intent2.putExtra("label", a.getLabel());
                intent2.putExtra("repeat", (a.getDays().equals("No Repeat"))?0:1);
                if(!a.getDays().equals("No Repeat")){
                    ArrayList<String> repeatDays = (ArrayList<String>)Arrays.asList(a.getDays().split(" "));
                    intent2.putStringArrayListExtra("repeatList", repeatDays);
                }
                final int _id = (int) System.currentTimeMillis();
                intent2.putExtra("id", _id);
                PendingIntent pendingIntent = PendingIntent.getBroadcast(getApplicationContext(), _id, intent2, PendingIntent.FLAG_UPDATE_CURRENT);
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    alarmManager.setAlarmClock(new AlarmManager.AlarmClockInfo(calendar.getTimeInMillis(), pendingIntent), pendingIntent);
                    //alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), pendingIntent);
                    //alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), pendingIntent);
                } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP){
                    alarmManager.setExact(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), pendingIntent);
                } else {
                    alarmManager.set(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), pendingIntent);
                }
            }
        }
    }
}
