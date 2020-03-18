package com.hyunro.wtwt;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.SystemClock;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.Calendar;

public class BootReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        // Set the alarm to start at approximately 7:02 a.m.
        Calendar calendarForDailyAlarm = Calendar.getInstance();
        long currentTime = calendarForDailyAlarm.getTimeInMillis();
        calendarForDailyAlarm.set(Calendar.HOUR_OF_DAY, 7);
        calendarForDailyAlarm.set(Calendar.MINUTE, 2);

        long triggerTime = calendarForDailyAlarm.getTimeInMillis();

        if(currentTime > triggerTime) {
            triggerTime+= 1000*60*60*24;
            calendarForDailyAlarm.setTimeInMillis(triggerTime);
        }

        Intent alarmIntent = new Intent(context, MorningAlarmReceiver.class);
        PendingIntent alarmPendingIntent = PendingIntent.getBroadcast(context, 102, alarmIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        AlarmManager alarmManager = (AlarmManager)context.getSystemService(context.ALARM_SERVICE);

        if(Build.VERSION.SDK_INT >= 23) {
            alarmManager.setInexactRepeating(AlarmManager.RTC_WAKEUP, triggerTime, AlarmManager.INTERVAL_DAY, alarmPendingIntent);
        } else if(Build.VERSION.SDK_INT >= 19){
            alarmManager.setExact(AlarmManager.RTC_WAKEUP,calendarForDailyAlarm.getTimeInMillis(), alarmPendingIntent);
        } else {
            alarmManager.set(AlarmManager.RTC_WAKEUP, calendarForDailyAlarm.getTimeInMillis(), alarmPendingIntent);
        }
    }
}
