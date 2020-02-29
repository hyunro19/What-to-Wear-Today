package com.hyunro.layout;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.os.Build;
import android.os.IBinder;
import android.util.Log;

import androidx.core.app.NotificationCompat;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import com.hyunro.layout.location.LocSelectActivity;

import java.util.Map;

public class MyFirebaseMessagingService extends FirebaseMessagingService {
    private static final String TAG = "FMS";
    public MyFirebaseMessagingService() {

    }

    @Override
    public void onNewToken(String token) {
        super.onNewToken(token);
        Log.e(TAG, "onNewToken 호출됨 "+token);
    }

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        Log.e(TAG, "onMessageReceived 호출됨");
        // 여기에 알림바로 보내는 기능 추가
        // showNoti()메소드 인자로 Title, Content를 넣으면, 그게 알림에 떠야함
        String from = remoteMessage.getFrom();
        String title = remoteMessage.getNotification().getTitle();
        String body = remoteMessage.getNotification().getBody();
        showFirebaseMessage(title, body);
    }

    NotificationManager notificationManager;
    public void showFirebaseMessage(String title, String contents) {
        notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        NotificationCompat.Builder builder = null;
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            if (notificationManager.getNotificationChannel("channel1") == null ) {
                notificationManager.createNotificationChannel(new NotificationChannel(
                        "channel1", "Channel1", NotificationManager.IMPORTANCE_DEFAULT
                ));
            }
            builder = new NotificationCompat.Builder(this, "channel1");
            Log.d("VERSION", "AFTER OREO BUILDER CREATED");
        } else {
            Log.d("VERSION", "BEFORE OREO");
            builder = new NotificationCompat.Builder(this);
        }

        Intent intent = new Intent(this, LocSelectActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 101, intent, PendingIntent.FLAG_ONE_SHOT);
        builder.setContentTitle(title);
        builder.setContentText(contents);
        builder.setSmallIcon(android.R.drawable.ic_input_get);
        builder.setAutoCancel(true);
        builder.setContentIntent(pendingIntent);
        Notification noti = builder.build();

        notificationManager.notify(1, noti);
    }
}
