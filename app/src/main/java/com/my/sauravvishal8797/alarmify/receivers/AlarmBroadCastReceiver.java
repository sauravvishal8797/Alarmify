package com.my.sauravvishal8797.alarmify.receivers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.PowerManager;

import com.my.sauravvishal8797.alarmify.services.AlarmActivateService;

import static android.support.v4.content.WakefulBroadcastReceiver.startWakefulService;

public class AlarmBroadCastReceiver extends BroadcastReceiver {

    private PowerManager.WakeLock screenWakeLock;

    @Override
    public void onReceive(Context context, Intent intent) {

        if (screenWakeLock == null) {
            PowerManager powerManager = (PowerManager) context.getSystemService(Context.POWER_SERVICE);
            screenWakeLock = powerManager.newWakeLock(PowerManager.SCREEN_DIM_WAKE_LOCK | PowerManager.ACQUIRE_CAUSES_WAKEUP,
                    "ScreenLock Tag from AlarmListener");
            screenWakeLock.acquire();
        }
        Intent service = new Intent(context, AlarmActivateService.class);
        startWakefulService(context, service);
        if (screenWakeLock != null)
            screenWakeLock.release();
    }
}
