package com.example.sauravvishal8797.alarmify;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.support.v4.content.WakefulBroadcastReceiver;
import android.util.Log;

import com.example.sauravvishal8797.alarmify.realm.RealmController;

public class AlarmReceiver extends BroadcastReceiver{
    @Override
    public void onReceive(Context context, Intent intent) {

        RealmController controller = RealmController.with(context);
        controller.deactivateAlarm(intent.getStringExtra("alarmtime"));
        Uri alarmUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM);
        if (alarmUri == null) {
            alarmUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        }
        AudioManager audioManager = (AudioManager) context.getSystemService(context.AUDIO_SERVICE);
       // audioManager.setStreamVolume(AudioManager.STREAM_ALARM, 20, 1);
        audioManager.getStreamMaxVolume(AudioManager.STREAM_ALARM);
        MediaPlayer mediaPlayer = MediaPlayer.create(context, alarmUri);
        mediaPlayer.setLooping(true);
        mediaPlayer.start();
        //mediaPlayer.

        Intent intent1 = new Intent(context, Mathspuzzle.class);
        //intent1.putExtras(intent, );

        intent1.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(intent1);
    }
}
