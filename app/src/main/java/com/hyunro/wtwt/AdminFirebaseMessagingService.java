package com.hyunro.wtwt;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.os.Build;

import androidx.core.app.NotificationCompat;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import com.hyunro.wtwt.location.LocSelectActivity;

public class AdminFirebaseMessagingService extends com.google.firebase.messaging.FirebaseMessagingService {
    private static final String TAG = "FMS";
    public AdminFirebaseMessagingService() {

    }

    @Override
    public void onNewToken(String token) {
        super.onNewToken(token);
    }

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
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
        } else {
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
