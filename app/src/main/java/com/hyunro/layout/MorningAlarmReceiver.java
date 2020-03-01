package com.hyunro.layout;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.util.Log;
import android.widget.Toast;

import androidx.core.app.NotificationCompat;
import androidx.preference.PreferenceManager;

import com.hyunro.layout.location.LocSelectActivity;
import com.hyunro.layout.login.LoginActivity;

public class MorningAlarmReceiver extends BroadcastReceiver {
    NotificationManager notificationManager;

    @Override
    public void onReceive(Context context, Intent intent) {
        SharedPreferences deafultPref = PreferenceManager.getDefaultSharedPreferences(context);
        Boolean agreeDailyAlarm = deafultPref.getBoolean("agreeDailyAlarm",false);

        // 모닝 알람 비동의시 리턴
        if(agreeDailyAlarm == false) return;

        Log.d(" ", "MorningAlarmReceiver");
        notificationManager = (NotificationManager)context.getSystemService(Context.NOTIFICATION_SERVICE);
        NotificationCompat.Builder builder = null;
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            if (notificationManager.getNotificationChannel("channel2") == null ) {
                notificationManager.createNotificationChannel(new NotificationChannel(
                        "channel2", "Channel2", NotificationManager.IMPORTANCE_DEFAULT
                ));
            }
            builder = new NotificationCompat.Builder(context, "channel1");
            Log.d("Receiver_MA", "AFTER OREO BUILDER CREATED");
        } else {
            Log.d("Receiver_MA", "BEFORE OREO");
            builder = new NotificationCompat.Builder(context);
        }

        Intent morningAlarmIntent = new Intent("com.hyunro.layout.MorningAlarmSet");
        morningAlarmIntent.setClass(context, LoginActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(context, 103, morningAlarmIntent, PendingIntent.FLAG_ONE_SHOT);
        String title = "오늘 날씨엔 어떤 옷을 입을까?";
        String contents = "우리 동네 사람들이 오늘 날씨에 입었던 옷 확인하러가기!";
        builder.setContentTitle(title);
        builder.setContentText(contents);
        builder.setSmallIcon(android.R.drawable.ic_input_get);
        builder.setAutoCancel(true);
        builder.setContentIntent(pendingIntent);
        Notification noti = builder.build();

        notificationManager.notify(2, noti);
    }

}
