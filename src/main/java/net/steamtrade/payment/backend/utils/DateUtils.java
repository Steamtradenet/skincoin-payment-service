package net.steamtrade.payment.backend.utils;

import java.util.Calendar;
import java.util.Date;


/**
 * Created by sasha on 05.01.16.
 */
public class DateUtils {

    public static Date addMinutes(Date current, int minutes) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(current);
        calendar.add(Calendar.MINUTE, minutes);
        return calendar.getTime();
    }

    public static Date addSeconds(Date current, int seconds) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(current);
        calendar.add(Calendar.SECOND, seconds);
        return calendar.getTime();
    }
}
