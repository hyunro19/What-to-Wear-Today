package com.hyunro.wtwt;

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

import com.hyunro.wtwt.login.LoginActivity;

public class MorningAlarmReceiver extends BroadcastReceiver {
    NotificationManager notificationManager;

    @Override
    public void onReceive(Context context, Intent intent) {
        SharedPreferences defaultPref = PreferenceManager.getDefaultSharedPreferences(context);
        Boolean agreeDailyAlarm = defaultPref.getBoolean("agreeDailyAlarm",false);

        // No agreement to receive morning alarm, return
        if(agreeDailyAlarm == false) return;

        notificationManager = (NotificationManager)context.getSystemService(Context.NOTIFICATION_SERVICE);
        NotificationCompat.Builder builder = null;
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            if (notificationManager.getNotificationChannel("channel2") == null ) {
                notificationManager.createNotificationChannel(new NotificationChannel(
                        "channel2", "Channel2", NotificationManager.IMPORTANCE_DEFAULT
                ));
            }
            builder = new NotificationCompat.Builder(context, "channel2");
        } else {
            builder = new NotificationCompat.Builder(context);
        }

        Intent morningAlarmIntent = new Intent("com.hyunro.layout.MorningAlarmSet");
        morningAlarmIntent.setClass(context, LoginActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(context, 103, morningAlarmIntent, PendingIntent.FLAG_ONE_SHOT);
        String title = "오늘 날씨엔 어떤 옷을 입을까?";
        String contents = "우리 동네 사람들이 오늘 날씨에 입었던 옷 확인하러가기!";
        builder.setContentTitle(title);
        builder.setContentText(contents);
        builder.setSmallIcon(R.drawable.ic_launcher_wtwt);
        builder.setAutoCancel(true);
        builder.setContentIntent(pendingIntent);

        Notification noti = builder.build();
        notificationManager.notify(2, noti);
    }

}
