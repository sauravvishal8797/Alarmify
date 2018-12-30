package com.example.sauravvishal8797.alarmify.models;

import java.util.ArrayList;

public class Alarm {

    private String time;
    private String period;
    private ArrayList<String> days;

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

    public void setDays(ArrayList<String> days) {
        this.days = days;
    }

    public ArrayList<String> getDays() {
        return days;
    }
}
