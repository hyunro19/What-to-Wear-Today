package com.hyunro.layout;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.SystemClock;
import android.util.Log;

import java.text.SimpleDateFormat;
import java.util.Calendar;

public class BootReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        int alarmType = AlarmManager.RTC_WAKEUP;
        // Set the alarm to start at approximately 7:05 a.m.
        Calendar calendarForDailyAlarm = Calendar.getInstance();

        calendarForDailyAlarm.setTimeInMillis(System.currentTimeMillis());
        calendarForDailyAlarm.set(Calendar.HOUR_OF_DAY, 7);
        calendarForDailyAlarm.set(Calendar.MINUTE, 5);

        SimpleDateFormat format1 = new SimpleDateFormat("yyyy.MM.dd/hh.mm.ss");
        String formatted = format1.format(calendarForDailyAlarm.getTime());

        Intent alarmIntent = new Intent(context, MorningAlarmReceiver.class);
        PendingIntent alarmPendingIntent = PendingIntent.getBroadcast(context, 102, alarmIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        AlarmManager alarmManager = (AlarmManager)context.getSystemService(context.ALARM_SERVICE);
        alarmManager.setInexactRepeating(alarmType, calendarForDailyAlarm.getTimeInMillis(), AlarmManager.INTERVAL_DAY, alarmPendingIntent);

//        long period = 1000 * 61;
//        long after = 1000 * 15;
//        long t = SystemClock.elapsedRealtime();
//        alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, t + after, period, alarmPendingIntent);

    }
}
