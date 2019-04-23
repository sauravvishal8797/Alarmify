package com.my.sauravvishal8797.alarmify.models;

import android.support.annotation.NonNull;
import android.util.Log;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import io.realm.RealmList;
import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

public class Alarm extends RealmObject implements Comparable<Alarm>{

    @PrimaryKey
    private String time;
    private int hour;
    private int minute;
    private long timeInMillis;
    private String period;
    private String repeatDays;
    private int snoozeTime;
    private int noOfTimesSnoozed;
    private String label;
    private boolean deleteAfterGoesOff;
    private boolean isActivated;
    private int pendingIntentId;

    public void setTime(String time) {
        this.time = time;
    }

    public String getTime() {
        return time;
    }

    public void setHour(int hour) {
        this.hour = hour;
    }

    public int getHour() {
        return hour;
    }

    public void setMinute(int minute) {
        this.minute = minute;
    }

    public int getMinute() {
        return minute;
    }

    public void setTimeInMillis(long timeInMillis) {
        this.timeInMillis = timeInMillis;
    }

    public long getTimeInMillis() {
        return timeInMillis;
    }

    public void setPeriod(String period) {
        this.period = period;
    }

    public String getPeriod() {
        return period;
    }

    public void setDays(String days) {
        this.repeatDays = days;
    }

    public String getDays() {
        return repeatDays;
    }

    public void setSnoozeTime(int snoozeTime) {
        this.snoozeTime = snoozeTime;
    }

    public int getSnoozeTime() {
        return snoozeTime;
    }

    public void setNoOfTimesSnoozed(int noOfTimesSnoozed) {
        this.noOfTimesSnoozed = noOfTimesSnoozed;
    }

    public int getNoOfTimesSnoozed() {
        return noOfTimesSnoozed;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public String getLabel() {
        return label;
    }

    public void setDeleteAfterGoesOff(boolean deleteAfterGoesOff) {
        this.deleteAfterGoesOff = deleteAfterGoesOff;
    }

    public boolean isDeleteAfterGoesOff() {
        return deleteAfterGoesOff;
    }

    public void setActivated(boolean activated) {
        isActivated = activated;
    }

    public boolean isActivated() {
        return isActivated;
    }

    public void setPendingIntentId(int pendingIntentId) {
        this.pendingIntentId = pendingIntentId;
    }

    public int getPendingIntentId() {
        return pendingIntentId;
    }

    @Override
    public int compareTo(@NonNull Alarm alarm) {
        Date alarmObject = null;
        Date date1 = null;
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(this.getTimeInMillis());
        Calendar alarmadata = Calendar.getInstance();
        alarmadata.setTimeInMillis(alarm.getTimeInMillis());
        Log.i("momokhanahai", String.valueOf(this.getTimeInMillis()) + ":" + String.valueOf(alarm.getTimeInMillis()) + "  " +
        String.valueOf(calendar.getTimeInMillis()) + ":" + String.valueOf(alarmadata.getTimeInMillis()));
        SimpleDateFormat format = new SimpleDateFormat("dd/M/yyyy h:mm");
        String currentDate = format.format(calendar.getTime());
        String alarmObjectDate = format.format(alarmadata.getTime());
        try {
            alarmObject = format.parse(alarmObjectDate);
            date1 = format.parse(currentDate);
        } catch (ParseException e) {
            e.printStackTrace();
        }
       // Log.i("datetimw", date1.toString() + "  " + alarmObject.toString());
        return date1.compareTo(alarmObject);
    }
}
