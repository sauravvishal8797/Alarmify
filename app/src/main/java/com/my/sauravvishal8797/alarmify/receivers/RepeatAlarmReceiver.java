package com.my.sauravvishal8797.alarmify.receivers;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;

import com.my.sauravvishal8797.alarmify.activities.AlarmDetailActivity;
import com.my.sauravvishal8797.alarmify.adapters.repeatAlarmAdapter;
import com.my.sauravvishal8797.alarmify.models.Alarm;
import com.my.sauravvishal8797.alarmify.realm.RealmController;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.HashMap;

import io.realm.Realm;

public class RepeatAlarmReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        RealmController realmController = RealmController.with(context);
        Realm realm = realmController.getRealm();
        ArrayList<Alarm> activatedAlarms = realmController.getActivatedAlarms();
        Calendar calendar = Calendar.getInstance();
        int dayOfWeek = calendar.get(Calendar.DAY_OF_WEEK);
        for (Alarm alarm : activatedAlarms) {
            if (!alarm.getDays().isEmpty()) {
                if (checkforRepeat(alarm.getDays(), dayOfWeek)){
                    setAlarm(alarm, context);
                }
            }
        }
    }

    private boolean checkforRepeat(String days, int dayeek){
        boolean result = false;
        ArrayList<String> daysR = new ArrayList<>(Arrays.asList(days.split(" ")));
        if (daysR.contains(daysMap(dayeek))){
            result = true;
        }
        return result;
    }

    private String daysMap(int day){
        String value = " ";
        HashMap<Integer, String> days = new HashMap<>();
        days.put(1, "Sun");
        days.put(2, "Mon");
        days.put(3, "Tue");
        days.put(4, "Wed");
        days.put(5, "Thr");
        days.put(6, "Fri");
        days.put(7, "Sat");
        if(days.containsKey(day)){
            value = days.get(day);
        }
        return value;
    }

    private void setAlarm(Alarm alarm, Context context){
        AlarmManager alarmManager = (AlarmManager)context.getSystemService(Context.ALARM_SERVICE);
        Calendar now = Calendar.getInstance();
        Calendar calendar = Calendar.getInstance();
        if((now.get(Calendar.HOUR_OF_DAY) == alarm.getHour())&& (now.get(Calendar.MINUTE) == alarm.getMinute())){
            calendar.set(Calendar.HOUR_OF_DAY, alarm.getHour());
            calendar.set(Calendar.MINUTE, alarm.getMinute());
            calendar.set(Calendar.SECOND, 56);
        } else {
            calendar.set(Calendar.HOUR_OF_DAY, alarm.getHour());
            calendar.set(Calendar.MINUTE, alarm.getMinute());
            calendar.set(Calendar.SECOND, 0);
        }
        if(calendar.before(now)){
            calendar.add(Calendar.DAY_OF_MONTH, 1);
        }
        Intent intent = new Intent(context, AlarmReceiver.class);
        intent.putExtra("alarmtime", alarm.getTime());
        intent.putExtra("hour", alarm.getHour());
        intent.putExtra("minutes", alarm.getMinute());
        intent.putExtra("deleteAfterGoingOff", alarm.isDeleteAfterGoesOff());
        intent.putExtra("period", alarm.getPeriod());
        intent.putExtra("snooze", alarm.getSnoozeTime());
        intent.putExtra("nooftimesSnoozed", 0);
        intent.putExtra("label", alarm.getLabel());
        intent.putExtra("repeat", 0);
        final int _id = (int) System.currentTimeMillis();
        intent.putExtra("id", _id);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(context, _id, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        if(repeatAlarmAdapter.repeatDays.size()==7){
            alarmManager.setInexactRepeating(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), AlarmManager.INTERVAL_DAY, pendingIntent);
        } else {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), pendingIntent);
                //alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), pendingIntent);
            } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP){
                alarmManager.setExact(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), pendingIntent);
            } else {
                alarmManager.set(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), pendingIntent);
            }
        }
    }
}
