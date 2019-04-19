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
import com.my.sauravvishal8797.alarmify.models.RepeatData;
import com.my.sauravvishal8797.alarmify.realm.RealmController;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
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
        if(realmController.checkAlarmState(intent.getStringExtra("alarmtime"))&&(manager.getMode()!=AudioManager.MODE_IN_CALL && !manager.isMusicActive())&&
                SP.getString("previewMode", "off").equals("off")){
            if(!SP.getBoolean("alarm_ringing", false)){
                editor.putBoolean("alarm_ringing", true);
                editor.commit();
            }
            activeAlarmTasks(realmController, intent, context);
        } else if ((realmController.checkAlarmState(intent.getStringExtra("alarmtime"))&&
                (manager.getMode()==AudioManager.MODE_IN_CALL || manager.isMusicActive())&&SP.getString("previewMode", "off").equals("off"))
                ||(realmController.checkAlarmState(intent.getStringExtra("alarmtime"))&&
                SP.getString("previewMode", "off").equals("on")&&(manager.getMode()!=AudioManager.MODE_IN_CALL && !manager.isMusicActive()))){
            if(intent.getBooleanExtra("deleteAfterGoingOff", false) &&
                    intent.getIntExtra("repeat", 0)==0){
                realmController.deleteAlarm(intent.getStringExtra("alarmtime"), intent.getStringExtra("period"));
            } else if(!intent.getBooleanExtra("deleteAfterGoingOff", false) &&
                    intent.getIntExtra("repeat", 0)==0){
                realmController.deactivateAlarm(intent.getStringExtra("alarmtime"));
            } else {
                //setNextAlarm(intent.getStringArrayListExtra("repeatList"), intent, context);
            }
        }

        if (screenWakeLock != null){
            screenWakeLock.release();
        }
        //mediaPlayer.
    }

    /**
     * Checks if an alarm is already active, activates the current one if no alarm is going off otherwise deactivates
     * the current alarm
     * @param realmController RealmController object to connect to the realm database
     * @param intent Intent object containing all the data related to the current alarm
     * @param context activity context
     */
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
            intent1.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK + Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS);
            context.startActivity(intent1);
            SharedPreferences.Editor editor = SP.getEditor();
            editor.putString("ringing", "yes");
            editor.commit();
        }
    }

    /** Activates next occurence for the repeating alarm
     * @param daysRepeat List containing the repeat days for the alarm
     * @param intent Intent containing alarm data
     * @param context context
     * */
    private void setNextAlarm(ArrayList<String> daysRepeat, Intent intent, Context context){
        //int[] list = new int[daysRepeat.size()];
        ArrayList<RepeatData> repeatData = new ArrayList<>(daysRepeat.size());
        Calendar calendar = Calendar.getInstance();
        for(int i=0; i<daysRepeat.size(); i++){
            RepeatData repeatData1 = new RepeatData();
            Log.i("repeatingAlarmOne", daysRepeat.get(i));
            int dayofweek = daysMap(daysRepeat.get(i));
            if (dayofweek != calendar.get(Calendar.DAY_OF_WEEK)){
                switch (dayofweek){
                    case 1:
                        repeatData1.setDifference(Calendar.SUNDAY + (7 - calendar.get(Calendar.DAY_OF_WEEK)));
                        repeatData1.setDayOfWeek(1);
                        repeatData.add(repeatData1);
                        break;

                    case 2:
                        repeatData1.setDifference(Calendar.MONDAY + (7 - calendar.get(Calendar.DAY_OF_WEEK)));
                        repeatData1.setDayOfWeek(2);
                        repeatData.add(repeatData1);
                        break;

                    case 3:
                        repeatData1.setDifference(Calendar.TUESDAY + (7 - calendar.get(Calendar.DAY_OF_WEEK)));
                        repeatData1.setDayOfWeek(3);
                        repeatData.add(repeatData1);
                        break;

                    case 4:
                        repeatData1.setDifference(Calendar.WEDNESDAY + (7 - calendar.get(Calendar.DAY_OF_WEEK)));
                        repeatData1.setDayOfWeek(4);
                        repeatData.add(repeatData1);
                        break;

                    case 5:
                        repeatData1.setDifference(Calendar.THURSDAY + (7 - calendar.get(Calendar.DAY_OF_WEEK)));
                        repeatData1.setDayOfWeek(5);
                        repeatData.add(repeatData1);
                        break;

                    case 6:
                        repeatData1.setDifference(Calendar.FRIDAY + (7 - calendar.get(Calendar.DAY_OF_WEEK)));
                        repeatData1.setDayOfWeek(6);
                        repeatData.add(repeatData1);
                        // Log.i("lalalauiopo", String.valueOf(list[i]));
                        break;

                    case 7:
                        repeatData1.setDifference(Calendar.SATURDAY + (7 - calendar.get(Calendar.DAY_OF_WEEK)));
                        repeatData1.setDayOfWeek(7);
                        repeatData.add(repeatData1);
                        break;
                }
            }
            Log.i("repeatdays", String.valueOf(dayofweek));
        }
        Collections.sort(repeatData);
        for (int i = 0; i < repeatData.size(); i++) {
            Log.i("abscondiness", String.valueOf(repeatData.get(i).getDifference() + "   " + repeatData.get(i).getDayOfWeek()));
           // calendar.get(Calendar.DAY_OF_WEEK + list[i]);

        }
        //Log.i("foronii", String.valueOf(list[0]));
        AlarmManager alarmManager = (AlarmManager)context.getSystemService(Context.ALARM_SERVICE);
        calendar.add(Calendar.HOUR_OF_DAY, intent.getIntExtra("hour", 0));
        calendar.add(Calendar.MINUTE, intent.getIntExtra("minutes", 0));
        calendar.add(Calendar.DAY_OF_WEEK, repeatData.get(0).getDayOfWeek());
        Log.i("joshussimmons", String.valueOf(repeatData.get(0).getDayOfWeek()) + "  " +
                String.valueOf(intent.getIntExtra("hour", 0)) + "  " +
                String.valueOf(intent.getIntExtra("minutes", 0)));
        Intent intent1 = new Intent(context, AlarmReceiver.class);
        intent.putExtra("alarmtime", intent.getStringExtra("alarmtime"));
        intent.putExtra("hour", intent.getIntExtra("hour", 0));
        intent.putExtra("minutes", intent.getIntExtra("minutes", 0));
        intent.putExtra("deleteAfterGoingOff", false);
        intent.putExtra("period", intent.getStringExtra("period"));
        intent.putExtra("snooze", intent.getIntExtra("snooze", 0));
        intent.putExtra("label", intent.getStringExtra("label"));
        intent.putExtra("repeat", 0);
        final int _id = (int) System.currentTimeMillis();
        intent.putExtra("id", _id);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(context, _id, intent1, PendingIntent.FLAG_UPDATE_CURRENT);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            alarmManager.setAlarmClock(new AlarmManager.AlarmClockInfo(calendar.getTimeInMillis(), pendingIntent), pendingIntent);
            //alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), pendingIntent);
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP){
            alarmManager.setExact(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), pendingIntent);
        } else {
            alarmManager.set(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), pendingIntent);
        }
    }

    /**
     * Maps week days to integers to be used in the @setNextAlarm() for handling the repeating occurence
     * of any alarm
     * @param day The week day value in string
     * @return An integer representing the string value of week day
     */
    private int daysMap(String day){
        Log.i("foriiniii", day);
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
