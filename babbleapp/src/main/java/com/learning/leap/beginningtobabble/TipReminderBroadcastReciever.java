package com.learning.leap.beginningtobabble;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

/**
 * Created by ryangunn on 9/26/16.
 */

public class TipReminderBroadcastReciever extends BroadcastReceiver {
    final static String GROUP_KEY_EMAILS = "group_babble";
    @Override
    public void onReceive(Context context, Intent intent) {

        Intent notificationIntent = new Intent(context, VoteActivity.class);
        notificationIntent.putExtra("NumberOfTips", 1);
        notificationIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        PendingIntent notificationPendingIntent = PendingIntent.getActivity(context,0,notificationIntent,0);
        NotificationManager notificationManager = (NotificationManager)context.getSystemService(Context.NOTIFICATION_SERVICE);
        Notification notification = getNotification("Read to your child",notificationPendingIntent,context);
        int id = intent.getIntExtra("id", 0);
        notificationManager.notify(id, notification);
    }

    private static android.app.Notification getNotification(String content, PendingIntent pendingIntent, Context context) {
        android.app.Notification.Builder builder = new android.app.Notification.Builder(context);
        builder.setContentTitle("Answer tips");
        builder.setContentText(content);
        builder.setContentIntent(pendingIntent);
        builder.setSmallIcon(R.drawable.playtoday_icon);
        builder.setAutoCancel(true);
        builder.setWhen(System.currentTimeMillis());
        return builder.build();
    }
}
