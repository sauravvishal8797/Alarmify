package com.example.sauravvishal8797.alarmify.receivers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.util.Log;

import com.example.sauravvishal8797.alarmify.R;
import com.example.sauravvishal8797.alarmify.activities.DismissAlarmActivity;
import com.example.sauravvishal8797.alarmify.helpers.Constants;
import com.example.sauravvishal8797.alarmify.helpers.PreferenceUtil;
import com.example.sauravvishal8797.alarmify.realm.RealmController;

import java.util.ArrayList;
import java.util.Calendar;

public class AlarmReceiver extends BroadcastReceiver{

    private PreferenceUtil SP;
    public static MediaPlayer mediaPlayer;
    public RealmController realmController;

    @Override
    public void onReceive(Context context, Intent intent) {
        SP = PreferenceUtil.getInstance(context);
        SharedPreferences.Editor editor = SP.getEditor();
        realmController = RealmController.with(context);
        if(realmController.checkAlarmState(intent.getStringExtra("alarmtime"))){
            if(!SP.getBoolean("alarm_ringing", false)){
                editor.putBoolean("alarm_ringing", true);
                editor.commit();
            }
            activeAlarmTasks(realmController, intent, context);
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
            setNextAlarm(intent.getStringArrayListExtra("repeatDyas"));
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
            Log.i("angmassssssss", String.valueOf(intent.getIntExtra("snooze", 0)));
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

    private void setNextAlarm(ArrayList<String> daysRepeat){
       // getNextAlarmTime(daysRepeat);
        int n = Calendar.MONDAY + (7 - Calendar.DAY_OF_WEEK);
        //Log.i("llllllll", );
    }
}
