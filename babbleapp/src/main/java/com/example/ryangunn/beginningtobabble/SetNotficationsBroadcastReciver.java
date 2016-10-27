package com.example.ryangunn.beginningtobabble;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

/**
 * Created by ryangunn on 10/10/16.
 */

public class SetNotficationsBroadcastReciver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent)
    {
        Intent service1 = new Intent(context, SetNotificationIntentService.class);
        context.startService(service1);

    }
}
