package com.leap.ryangunn.beginningtobabble;

import android.app.Notification;
import android.app.NotificationManager;
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
        NotificationManager notificationManager = (NotificationManager)context.getSystemService(Context.NOTIFICATION_SERVICE);

        Notification notification = intent.getParcelableExtra("notification");
        int id = intent.getIntExtra("id", 0);
        notificationManager.notify(id, notification);
    }
}
