package com.my.sauravvishal8797.alarmify.receivers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.my.sauravvishal8797.alarmify.services.ReactivateAlarmsAfterBootService;

public class BootCompletedIntentReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        if("android.intent.action.BOOT_COMPLETED".equals(intent.getAction())){
            Intent activateAlarms = new Intent(context, ReactivateAlarmsAfterBootService.class);
            context.startService(activateAlarms);
        }
    }
}
