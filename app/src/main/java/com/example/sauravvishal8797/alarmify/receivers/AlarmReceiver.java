package com.example.sauravvishal8797.alarmify.receivers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.RingtoneManager;
import android.net.Uri;

import com.example.sauravvishal8797.alarmify.activities.DismissAlarmActivity;
import com.example.sauravvishal8797.alarmify.realm.RealmController;

public class AlarmReceiver extends BroadcastReceiver{
    @Override
    public void onReceive(Context context, Intent intent) {

        AudioManager audioManager = (AudioManager) context.getSystemService(context.AUDIO_SERVICE);
        //
        Intent intent1 = new Intent(context, DismissAlarmActivity.class);
        intent1.putExtra("stop", "normal");
        //intent1.putExtras(intent, );
        intent1.putExtra("time", intent.getStringExtra("alarmtime"));
        intent1.putExtra("period", intent.getStringExtra("period"));
        intent1.putExtra("hour", intent.getIntExtra("hour", 0));
        intent1.putExtra("minutes", intent.getIntExtra("minutes", 0));
        intent1.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(intent1);

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
        MediaPlayer mediaPlayer = MediaPlayer.create(context, alarmUri);
        mediaPlayer.setLooping(true);
        mediaPlayer.start();
        //mediaPlayer.
    }
}
