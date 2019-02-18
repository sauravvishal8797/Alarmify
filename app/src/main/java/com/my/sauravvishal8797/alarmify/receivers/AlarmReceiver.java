package com.my.sauravvishal8797.alarmify.receivers;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.os.PowerManager;
import android.util.Log;

import com.my.sauravvishal8797.alarmify.R;
import com.my.sauravvishal8797.alarmify.activities.DismissAlarmActivity;
import com.my.sauravvishal8797.alarmify.helpers.PreferenceUtil;
import com.my.sauravvishal8797.alarmify.realm.RealmController;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.HashMap;

public class AlarmReceiver extends BroadcastReceiver{

    private PreferenceUtil SP;
    public static MediaPlayer mediaPlayer;
    public RealmController realmController;
    private PowerManager.WakeLock screenWakeLock;

    @Override
    public void onReceive(Context context, Intent intent) {
        if (screenWakeLock == null){
            PowerManager powerManager = (PowerManager) context.getSystemService(Context.POWER_SERVICE);
            screenWakeLock = powerManager.newWakeLock(PowerManager.SCREEN_DIM_WAKE_LOCK | PowerManager.ACQUIRE_CAUSES_WAKEUP,
                    "ScreenLock Tag from AlarmListener");
            screenWakeLock.acquire();
        }
        SP = PreferenceUtil.getInstance(context);
        SharedPreferences.Editor editor = SP.getEditor();
        AudioManager manager = (AudioManager)context.getSystemService(Context.AUDIO_SERVICE);
        realmController = RealmController.with(context);
        if(realmController.checkAlarmState(intent.getStringExtra("alarmtime"))&&manager.getMode()!=AudioManager.MODE_IN_CALL&&
                SP.getString("previewMode", "off").equals("off")){
            if(!SP.getBoolean("alarm_ringing", false)){
                editor.putBoolean("alarm_ringing", true);
                editor.commit();
            }
            activeAlarmTasks(realmController, intent, context);
        } else if ((realmController.checkAlarmState(intent.getStringExtra("alarmtime"))&&
                manager.getMode()==AudioManager.MODE_IN_CALL&&SP.getString("previewMode", "off").equals("off"))
                ||(realmController.checkAlarmState(intent.getStringExtra("alarmtime"))&&
                SP.getString("previewMode", "off").equals("on")&&manager.getMode()!=AudioManager.MODE_IN_CALL)){
            if(intent.getBooleanExtra("deleteAfterGoingOff", false) &&
                    intent.getIntExtra("repeat", 0)==0){
                realmController.deleteAlarm(intent.getStringExtra("alarmtime"), intent.getStringExtra("period"));
            } else if(!intent.getBooleanExtra("deleteAfterGoingOff", false) &&
                    intent.getIntExtra("repeat", 0)==0){
                realmController.deactivateAlarm(intent.getStringExtra("alarmtime"));
            } else {
                setNextAlarm(intent.getStringArrayListExtra("repeatList"), intent, context);
            }
        }

        if (screenWakeLock != null){
            screenWakeLock.release();
        }
        //mediaPlayer.
    }

    private void activeAlarmTasks(RealmController realmController, Intent intent, Context context){
        AudioManager audioManager = (AudioManager) context.getSystemService(context.AUDIO_SERVICE);
        if(intent.getBooleanExtra("deleteAfterGoingOff", false) &&
                intent.getIntExtra("repeat", 0)==0){
            realmController.deleteAlarm(intent.getStringExtra("alarmtime"), intent.getStringExtra("period"));
        } else if(!intent.getBooleanExtra("deleteAfterGoingOff", false) &&
                intent.getIntExtra("repeat", 0)==0){
            realmController.deactivateAlarm(intent.getStringExtra("alarmtime"));
        } else {
            setNextAlarm(intent.getStringArrayListExtra("repeatList"), intent, context);
        }
        Uri alarmUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM);
        if (alarmUri == null) {
            alarmUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        }
        if(SP.getString("ringing", "not").equals("not")){
            mediaPlayer = MediaPlayer.create(context, alarmUri);
            mediaPlayer.setLooping(true);
            if(SP.getBoolean(context.getResources().getString(R.string.set_ringer_value_max_mssg), false)){
                audioManager.setStreamVolume(
                        AudioManager.STREAM_MUSIC,
                        audioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC),
                        0);
            }
            mediaPlayer.start();
            Intent intent1 = new Intent(context, DismissAlarmActivity.class);
            intent1.putExtra("stop", "normal");
            //intent1.putExtras(intent, );
            intent1.putExtra("time", intent.getStringExtra("alarmtime"));
            intent1.putExtra("period", intent.getStringExtra("period"));
            intent1.putExtra("hour", intent.getIntExtra("hour", 0));
            intent1.putExtra("minutes", intent.getIntExtra("minutes", 0));
            intent1.putExtra("id", intent.getIntExtra("id", 0));
            intent1.putExtra("label", intent.getStringExtra("label"));
            intent1.putExtra("snooze", intent.getIntExtra("snooze", 0));
            intent1.putExtra("noftimesSnoozed", intent.getIntExtra("nooftimesSnoozed", 0));
            intent1.putExtra("deleteAfterGoingOff", intent.getBooleanExtra("deleteAfterGoingOff", false));
            // intent1.putExtra("time", intent.getStringExtra("alarmtime"));
            intent1.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(intent1);
            SharedPreferences.Editor editor = SP.getEditor();
            editor.putString("ringing", "yes");
            editor.commit();
        }
        /**if(SP.getBoolean("alarm_ringing", false)){
        } else {
            mediaPlayer = MediaPlayer.create(context, alarmUri);
            mediaPlayer.setLooping(true);
            mediaPlayer.start();
        }*/
    }

    private void setNextAlarm(ArrayList<String> daysRepeat, Intent intent, Context context){
        int[] list = new int[10];
        for(int i=0; i<daysRepeat.size(); i++){
            int dayofweek = daysMap(daysRepeat.get(i));
            switch (dayofweek){
                case 1:
                   list[i] = (Calendar.SUNDAY + (7 - Calendar.DAY_OF_WEEK));
                   break;

                case 2:
                    list[i] = (Calendar.MONDAY + (7 - Calendar.DAY_OF_WEEK));
                    break;

                case 3:
                    list[i] = (Calendar.TUESDAY + (7 - Calendar.DAY_OF_WEEK));
                    break;

                case 4:
                    list[i] = (Calendar.WEDNESDAY + (7 - Calendar.DAY_OF_WEEK));
                    break;

                case 5:
                    list[i] = (Calendar.THURSDAY + (7 - Calendar.DAY_OF_WEEK));
                    break;

                case 6:
                    list[i] = (Calendar.FRIDAY + (7 - Calendar.DAY_OF_WEEK));
                    break;

                case 7:
                    list[i] = (Calendar.SATURDAY + (7 - Calendar.DAY_OF_WEEK));
                    break;
            }
        }
        Arrays.sort(list);
        AlarmManager alarmManager = (AlarmManager)context.getSystemService(Context.ALARM_SERVICE);
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.HOUR_OF_DAY, intent.getIntExtra("hour", 0));
        calendar.add(Calendar.MINUTE, intent.getIntExtra("minutes", 0));
        calendar.add(Calendar.DAY_OF_MONTH, list[0]);
        Intent intent1 = new Intent(context, AlarmReceiver.class);
        intent.putExtra("alarmtime", intent.getStringExtra("alarmtime"));
        intent.putExtra("hour", intent.getStringExtra("hour"));
        intent.putExtra("minutes", intent.getStringExtra("minutes"));
        intent.putExtra("deleteAfterGoingOff", false);
        intent.putExtra("period", intent.getStringExtra("period"));
        intent.putExtra("snooze", intent.getStringExtra("snooze"));
        intent.putExtra("label", intent.getStringExtra("label"));
        intent.putExtra("repeat", 0);
        final int _id = (int) System.currentTimeMillis();
        intent.putExtra("id", _id);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(context, _id, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), pendingIntent);
            //alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), pendingIntent);
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP){
            alarmManager.setExact(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), pendingIntent);
        } else {
            alarmManager.set(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), pendingIntent);
        }
    }

    private int daysMap(String day){
        int value = 0;
        HashMap<String, Integer> days = new HashMap<>();
        days.put("Sun", 1);
        days.put("Mon", 2);
        days.put("Tue", 3);
        days.put("Wed", 4);
        days.put("Thr", 5);
        days.put("Fri", 6);
        days.put("Sat", 7);
        if(days.containsKey(day)){
            value = days.get(day);
        }
        return value;
    }
}
