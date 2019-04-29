package com.my.sauravvishal8797.alarmify.helpers;

import java.util.HashMap;

public class DaysHelper {

    /**
     * Maps week days to integral value
     * @param day The week day value in string
     * @return An integer representing the string value of week day
     */
    public static int mapDayToValue(String day){
        int value = 0;
        HashMap<String, Integer> days = new HashMap<>();
        days.put("Sun", 1);
        days.put("Mon", 2);
        days.put("Tue", 3);
        days.put("Wed", 4);
        days.put("Thr", 5);
        days.put("Fri", 6);
        days.put("Sat", 7);
        if(days.containsKey(day)){
            value = days.get(day);
        }
        return value;
    }

    /**
     * Maps integral values representing a particular weekday to its String representation
     * @param day The weekday value as integer
     * @return The string value for the weekday
     */
    public static String mapValueToDay(int day){
        String value = " ";
        HashMap<Integer, String> days = new HashMap<>();
        days.put(1, "Sun");
        days.put(2, "Mon");
        days.put(3, "Tue");
        days.put(4, "Wed");
        days.put(5, "Thr");
        days.put(6, "Fri");
        days.put(7, "Sat");
        if(days.containsKey(day)){
            value = days.get(day);
        }
        return value;
    }
}
