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

import com.example.sauravvishal8797.alarmify.activities.DismissAlarmActivity;
import com.example.sauravvishal8797.alarmify.helpers.PreferenceUtil;
import com.example.sauravvishal8797.alarmify.realm.RealmController;

public class AlarmReceiver extends BroadcastReceiver{

    private PreferenceUtil SP;
    public static MediaPlayer mediaPlayer;
    @Override
    public void onReceive(Context context, Intent intent) {
        SP = PreferenceUtil.getInstance(context);
        SharedPreferences.Editor editor = SP.getEditor();

        AudioManager audioManager = (AudioManager) context.getSystemService(context.AUDIO_SERVICE);
        //
        RealmController controller = RealmController.with(context);
        if(intent.getBooleanExtra("deleteAfterGoingOff", false)){
            controller.deleteAlarm(intent.getStringExtra("alarmtime"), intent.getStringExtra("period"));
        } else {
            controller.deactivateAlarm(intent.getStringExtra("alarmtime"));
        }
        Uri alarmUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM);
        if (alarmUri == null) {
            alarmUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        }
        //AudioManager audioManager = (AudioManager) context.getSystemService(context.AUDIO_SERVICE);
       // audioManager.setStreamVolume(AudioManager.STREAM_ALARM, 20, 1);
        audioManager.getStreamMaxVolume(AudioManager.STREAM_ALARM);
        mediaPlayer = MediaPlayer.create(context, alarmUri);
        mediaPlayer.setLooping(true);
        mediaPlayer.start();
        Intent intent1 = new Intent(context, DismissAlarmActivity.class);
        intent1.putExtra("stop", "normal");
        //intent1.putExtras(intent, );
        intent1.putExtra("time", intent.getStringExtra("alarmtime"));
        intent1.putExtra("period", intent.getStringExtra("period"));
        intent1.putExtra("hour", intent.getIntExtra("hour", 0));
        intent1.putExtra("minutes", intent.getIntExtra("minutes", 0));
        intent1.putExtra("id", intent.getIntExtra("id", 0));
        intent1.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(intent1);
        if(SP.getBoolean("alarm_ringing", false)){

        } else {

        }
        //mediaPlayer.
    }
}
