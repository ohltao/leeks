package com.leeks.utils;

import java.util.Calendar;
import java.util.Date;

public class TimeUtil {

    private static final Calendar calendar = Calendar.getInstance();

    public static boolean checkTime() {
        calendar.setTime(new Date());
        int week = calendar.get(Calendar.DAY_OF_WEEK);
        int hour = calendar.get(Calendar.HOUR_OF_DAY);
        System.out.println(week);
        System.out.println(hour);
        return week > 1 && week < 7 && ((hour > 8 && hour < 12) || (hour > 12 && hour < 15));
    }
}
