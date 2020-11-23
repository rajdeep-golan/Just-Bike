package com.just.justbikev2.Helper;

import android.annotation.TargetApi;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.ContextWrapper;
import android.net.Uri;
import android.os.Build;

import com.just.justbikev2.R;

public class NotificationHelper extends ContextWrapper {
    private static String JUST_CHANNEL_ID = "com.just.justbikev2.Golan";
    private static String JUST_CHANNEL_NAME = "Just Bike";
    private NotificationManager manager;
    public NotificationHelper(Context base) {
        super(base);
        if(Build.VERSION.SDK_INT > Build.VERSION_CODES.O )
            createChannel();
    }

    @TargetApi(Build.VERSION_CODES.O)
    private void createChannel() {
        NotificationChannel channel = new NotificationChannel(JUST_CHANNEL_ID,JUST_CHANNEL_NAME,NotificationManager.IMPORTANCE_DEFAULT);
        channel.enableLights(true);
        channel.enableVibration(true);
        channel.setLockscreenVisibility(Notification.VISIBILITY_PRIVATE);
        getManager().createNotificationChannel(channel);
    }

    public NotificationManager getManager() {
        if(manager==null)
            manager = (NotificationManager)getSystemService(Context.NOTIFICATION_SERVICE);
        return manager;
    }

    @TargetApi(Build.VERSION_CODES.O)
    public Notification.Builder setJustBikeChannel(String title , String body , PendingIntent pi , Uri soundUri){
        return new Notification.Builder(getApplicationContext(),JUST_CHANNEL_ID)
                .setContentIntent(pi)
                .setContentTitle(title)
                .setContentText(body)
                .setSmallIcon(R.drawable.just_black_logo)
                .setSound(soundUri)
                .setAutoCancel(false);
    }
}
