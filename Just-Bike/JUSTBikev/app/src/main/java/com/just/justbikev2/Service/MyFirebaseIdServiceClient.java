package com.just.justbikev2.Service;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import com.just.justbikev2.Admin.ViewOrdersAdmin;
import com.just.justbikev2.Common.Common;
import com.just.justbikev2.Helper.NotificationHelper;
import com.just.justbikev2.Model.Token;
import com.just.justbikev2.R;
import com.just.justbikev2.ViewOrders;

import java.util.Random;

public class MyFirebaseIdServiceClient extends FirebaseMessagingService {
    @Override
    public void onMessageReceived(@NonNull RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);
        if(Build.VERSION.SDK_INT > Build.VERSION_CODES.O )
            sendNotificationAPI26(remoteMessage);
        else
            sendNotification(remoteMessage);
    }

    private void sendNotificationAPI26(RemoteMessage remoteMessage) {
        RemoteMessage.Notification notification = remoteMessage.getNotification();
        String title = notification.getTitle();
        String content = notification.getBody();
        Intent i;
        if(Common.currentUser.getIsStaff().equals("true")) //Admin
            i = new Intent(this , ViewOrdersAdmin.class);
        else
            i = new Intent(this , ViewOrders.class);       //Client
        i.putExtra(Common.USER_PHONE, Common.currentUser.getPhone());
        i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent pi = PendingIntent.getActivity(this,0,i,PendingIntent.FLAG_ONE_SHOT);
     //   Uri ringtone = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        Uri ringtone = Uri.parse(ContentResolver.SCHEME_ANDROID_RESOURCE + "://"+ getApplicationContext().getPackageName() + "/" + R.raw.ringtone);

        NotificationHelper helper = new NotificationHelper(this);
        Notification.Builder builder = helper.setJustBikeChannel(title,content,pi,ringtone);
        builder.setStyle(new Notification.BigPictureStyle());

        helper.getManager().notify(new Random().nextInt(),builder.build());
    }

    private void sendNotification(RemoteMessage remoteMessage) {
        RemoteMessage.Notification notification = remoteMessage.getNotification();
        Intent i ;
        if(Common.currentUser.getIsStaff().equals("true")) //Admin
            i = new Intent(this , ViewOrdersAdmin.class);
        else
            i = new Intent(this , ViewOrders.class);       //Client= new Intent(this , SplashActivity.class);
        i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent pi = PendingIntent.getActivity(this,0,i,PendingIntent.FLAG_ONE_SHOT);
        Uri ringtone = Uri.parse(ContentResolver.SCHEME_ANDROID_RESOURCE + "://"+ getApplicationContext().getPackageName() + "/" + R.raw.ringtone);

      // Uri ringtone = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this)
                .setSmallIcon(R.drawable.just_black_logo)
                .setContentTitle(notification.getTitle())
                .setContentText(notification.getBody())
                .setAutoCancel(true)
                .setSound(ringtone)
                .setStyle(new NotificationCompat.BigPictureStyle())
     .setContentIntent(pi);
        NotificationManager notificationManager = (NotificationManager)getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.notify(0,builder.build());

    }
    @Override
    public void onNewToken(@NonNull String s) {
        super.onNewToken(s);
        String tokenRefresh = FirebaseInstanceId.getInstance().getToken();

        if(Common.currentUser != null)
            updateTokenToFirebase(tokenRefresh);
    }

    private void updateTokenToFirebase(String tokenRefresh) {
        if(Common.currentUser!=null) {
            FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
            DatabaseReference tokens = firebaseDatabase.getReference("Tokens");
            Token token = new Token(tokenRefresh, false);
            tokens.child(Common.currentUser.getPhone()).setValue(token);
        }
    }
}
