package com.learning.leap.bwb.tipReminder;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.net.Uri;

import com.learning.leap.bwb.helper.LocalLoadSaveHelper;
import com.learning.leap.bwb.utility.Constant;
import com.learning.leap.bwb.R;
import com.learning.leap.bwb.vote.VoteViewActivity;


public class VoteNotificationBroadcastReciever extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {

        Intent voteIntent = new Intent(context, VoteViewActivity.class);
        int numOfTips = intent.getIntExtra("NumberOfTips",1);
        voteIntent.putExtra("NumberOfTips", numOfTips);
        int id = intent.getIntExtra("id", 0);
        voteIntent.putExtra("BucketNumber",id);
        voteIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        PendingIntent notificationPendingIntent = PendingIntent.getActivity(context,100,voteIntent,PendingIntent.FLAG_CANCEL_CURRENT);
        NotificationManager notificationManager = (NotificationManager)context.getSystemService(Context.NOTIFICATION_SERVICE);
        String babyName = LocalLoadSaveHelper.getBabyName(context);
        Resources res =context.getResources();
        String text = String.format(res.getString(R.string.notification_message), babyName);
        Notification notification = getNotification(text,notificationPendingIntent,context);
        notificationManager.notify(id, notification);
    }

    private static android.app.Notification getNotification(String content, PendingIntent pendingIntent, Context context) {
        android.app.Notification.Builder builder = new android.app.Notification.Builder(context);
        builder.setContentTitle(Constant.BWB);
        builder.setContentText(content);
        Uri path = Uri.parse("android.resource://" + context.getPackageName() + "/" + R.raw.squeak);
        builder.setSound(path);
        builder.setContentIntent(pendingIntent);
        builder.setSmallIcon(R.drawable.playtoday_icon);
        builder.setAutoCancel(true);
        builder.setWhen(System.currentTimeMillis());
        return builder.build();
    }
}
