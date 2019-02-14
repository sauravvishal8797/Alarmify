package com.example.sauravvishal8797.alarmify.receivers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.example.sauravvishal8797.alarmify.activities.DismissAlarmActivity;

public class RestartDismissActivityReceiver extends BroadcastReceiver{
    @Override
    public void onReceive(Context context, Intent intent) {
        if("lalaland".equals(intent.getAction())){
            Log.i("receiverclaeed", "receiver called");
            Intent intent1 = new Intent(context, DismissAlarmActivity.class);
            //intent1.putExtra("putPauseFalse", "false");
            intent1.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(intent1);
        }
    }
}
