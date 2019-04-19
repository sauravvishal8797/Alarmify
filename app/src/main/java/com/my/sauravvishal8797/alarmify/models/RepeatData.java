package com.my.sauravvishal8797.alarmify.models;

import android.support.annotation.NonNull;

public class RepeatData implements Comparable<RepeatData> {

    private int difference;
    private int dayOfWeek;

    public void setDifference(int difference) {
        this.difference = difference;
    }

    public int getDifference() {
        return difference;
    }

    public void setDayOfWeek(int dayOfWeek) {
        this.dayOfWeek = dayOfWeek;
    }

    public int getDayOfWeek() {
        return dayOfWeek;
    }

    @Override
    public int compareTo(@NonNull RepeatData repeatData) {
        if (repeatData.difference <= this.difference)
            return 1;
        else if (repeatData.difference > this.difference)
            return -1;
        else
            return 0;
    }
}
