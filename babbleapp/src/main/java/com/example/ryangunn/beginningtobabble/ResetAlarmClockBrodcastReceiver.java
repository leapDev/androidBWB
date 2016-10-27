package com.example.ryangunn.beginningtobabble;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.example.ryangunn.beginningtobabble.models.TipReminder;

/**
 * Created by ryangunn on 10/10/16.
 */

public class ResetAlarmClockBrodcastReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent.getAction().equals("android.intent.action.BOOT_COMPLETED")) {
            // Set the alarm here.
            TipReminder.setUpRepeatingIntentService(context);
        }
    }
}
