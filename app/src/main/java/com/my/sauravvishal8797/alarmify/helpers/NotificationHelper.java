package com.my.sauravvishal8797.alarmify.helpers;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;

import com.my.sauravvishal8797.alarmify.R;
import com.my.sauravvishal8797.alarmify.services.DisableAlarmFromNotificationService;
import com.my.sauravvishal8797.alarmify.services.ReactivateAlarmsAfterBootService;

public class NotificationHelper {

    private static final String PRIMARY_CHANNEL_ID = "next_alarm_notification_channel";

    private static final int NOTIFICATION_ID = 0;

    private NotificationManager mNotificationManager;
    private Context mContext;

    public NotificationHelper(Context context){
        mContext = context;
    }

    public void createNotificationChannel(){
        mNotificationManager = (NotificationManager) mContext.getSystemService(Context.NOTIFICATION_SERVICE);
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            // Create a NotificationChannel
            NotificationChannel notificationChannel = new NotificationChannel(PRIMARY_CHANNEL_ID, "Alarm Notification",
                    NotificationManager.IMPORTANCE_HIGH);
            notificationChannel.enableLights(false);
            notificationChannel.enableVibration(false);
            notificationChannel.setSound(null, null);
            notificationChannel.setDescription("Alarm Notification");
            mNotificationManager.createNotificationChannel(notificationChannel);
        }
    }

    private NotificationCompat.Builder getNotificationBuilder(String notificationTitle, String notificationtext, String time, String period){
        Intent intent = new Intent(mContext, DisableAlarmFromNotificationService.class);
        intent.putExtra("time", time);
        intent.putExtra("period", period);
        mContext.startService(intent);
        PendingIntent pendingIntent = PendingIntent.getService(mContext, 0, intent, 0);
        NotificationCompat.Builder notifyBuilder = new NotificationCompat.Builder(mContext, PRIMARY_CHANNEL_ID)
                .setSmallIcon(R.mipmap.ic_launcher_round)
                .setContentTitle(notificationTitle)
                .setContentText(notificationtext)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setContentIntent(pendingIntent)
                .setAutoCancel(true);
        return notifyBuilder;
    }

    public void sendNotification(String notificationTitle, String notificationtext, String time, String period){
        NotificationManagerCompat notificationManagerCompat = NotificationManagerCompat.from(mContext);
        notificationManagerCompat.notify(NOTIFICATION_ID, getNotificationBuilder(notificationTitle,
                notificationtext, time, period).build());
    }
}