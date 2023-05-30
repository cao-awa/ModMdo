package com.github.cao.awa.modmdo.utils.times;

import com.github.zhuaidadaya.rikaishinikui.handler.universal.entrust.*;

public class TimeUtil {
    public static long processRemainingSeconds(long million) {
        long onlineSeconds = million / 1000;

        onlineSeconds -= (onlineSeconds > 59 ? onlineSeconds / 60 : 0) * 60;
        return onlineSeconds;
    }

    public static long processRemainingMinutes(long million) {
        long onlineSeconds = million / 1000;

        long onlineMinutes = onlineSeconds / 60;
        onlineMinutes -= (onlineMinutes > 59 ? onlineMinutes / 60 : 0) * 60;
        return onlineMinutes;
    }

    public static long processRemainingHours(long million) {
        long onlineSeconds = million / 1000;

        long onlineMinutes = onlineSeconds / 60;
        long onlineHours = onlineMinutes > 59 ? onlineMinutes / 60 : 0;
        onlineHours -= (onlineHours > 23 ? onlineHours / 24 : 0) * 24;
        return onlineHours;
    }

    public static long processRemainingDays(long million) {
        long onlineSeconds = million / 1000;

        long onlineMinutes = onlineSeconds / 60;
        long onlineHours = onlineMinutes > 59 ? onlineMinutes / 60 : 0;
        long onlineDays = onlineHours > 23 ? onlineHours / 24 : 0;
        onlineDays -= (onlineDays > 29 ? onlineDays / 30 : 0) * 30;
        return onlineDays;
    }

    public static long processRemainingMonths(long million) {
        long onlineSeconds = million / 1000;

        long onlineMinutes = onlineSeconds / 60;
        long onlineHours = onlineMinutes > 59 ? onlineMinutes / 60 : 0;
        long onlineDays = onlineHours > 23 ? onlineHours / 24 : 0;
        long onlineMonths = onlineDays > 29 ? onlineDays / 30 : 0;
        onlineMonths -= (onlineMonths > 11 ? onlineMonths / 12 : 0) * 12;
        return onlineMonths;
    }

    public static long processRemainingYears(long million) {
        long onlineSeconds = million / 1000;

        long onlineMinutes = onlineSeconds / 60;
        long onlineHours = onlineMinutes > 59 ? onlineMinutes / 60 : 0;
        long onlineDays = onlineHours > 23 ? onlineHours / 24 : 0;
        long onlineMonths = onlineDays > 29 ? onlineDays / 30 : 0;
        return onlineMonths > 11 ? onlineMonths / 12 : 0;
    }

    public static long formatSecond(long million) {
        return million / 1000;
    }

    public static long formatMinute(long million) {
        return formatSecond(million) > 59 ? formatSecond(million) / 60 : 0;
    }

    public static long formatHour(long million) {
        return formatMinute(million) > 59 ? formatMinute(million) / 60 : 0;
    }

    public static long formatDay(long million) {
        return formatHour(million) > 23 ? formatHour(million) / 24 : 0;
    }

    public static long formatMonth(long million) {
        return formatDay(million) > 29 ? formatDay(million) / 30 : 0;
    }

    public static long ticksMillionTotal(long ticks) {
        return ticks * 50;
    }

    public static long millions() {
        return System.currentTimeMillis();
    }

    public static long nano() {
        return System.nanoTime();
    }

    public static long processMillion(long million) {
        return millions() - million;
    }

    public static long processNano(long nano) {
        return nano() - nano;
    }

    public static void sleep(long millions) throws InterruptedException {
        if (millions < 0)
            return;
        Thread.sleep(millions);
    }

    public static void coma(long millions) {
        EntrustEnvironment.trys(() -> Thread.sleep(millions));
    }
}
