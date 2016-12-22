package com.learning.leap.beginningtobabbleapp.tipReminder;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.learning.leap.beginningtobabbleapp.utility.Constant;
import com.learning.leap.beginningtobabbleapp.R;
import com.learning.leap.beginningtobabbleapp.vote.VoteViewActivity;

/**
 * Created by ryangunn on 9/26/16.
 */

public class VoteNotificationBroadcastReciever extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {

        Intent voteIntent = new Intent(context, VoteViewActivity.class);
        voteIntent.putExtra("NumberOfTips", 1);
        int id = intent.getIntExtra("id", 0);
        voteIntent.putExtra("BucketNumber",id);
        voteIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        PendingIntent notificationPendingIntent = PendingIntent.getActivity(context,100,voteIntent,0);
        NotificationManager notificationManager = (NotificationManager)context.getSystemService(Context.NOTIFICATION_SERVICE);
        Notification notification = getNotification("Read to your child",notificationPendingIntent,context);
        notificationManager.notify(id, notification);
    }

    private static android.app.Notification getNotification(String content, PendingIntent pendingIntent, Context context) {
        android.app.Notification.Builder builder = new android.app.Notification.Builder(context);
        builder.setContentTitle(Constant.BWB);
        builder.setContentText(content);
        builder.setContentIntent(pendingIntent);
        builder.setSmallIcon(R.drawable.playtoday_icon);
        builder.setAutoCancel(true);
        builder.setWhen(System.currentTimeMillis());
        return builder.build();
    }
}
