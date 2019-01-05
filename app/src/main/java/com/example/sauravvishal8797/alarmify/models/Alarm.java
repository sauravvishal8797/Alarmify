package com.example.sauravvishal8797.alarmify.models;

import java.util.ArrayList;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

public class Alarm extends RealmObject{

    @PrimaryKey
    private String time;
    private String period;
    private String repeatDays;
    private int snoozeTime;
    private String label;
    private boolean deleteAfterGoesOff;

    public void setTime(String time) {
        this.time = time;
    }

    public String getTime() {
        return time;
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
}
