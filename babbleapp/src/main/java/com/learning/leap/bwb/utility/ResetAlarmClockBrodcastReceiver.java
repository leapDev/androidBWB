package com.learning.leap.bwb.utility;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.learning.leap.bwb.PlayTodayJob;

/**
 * Created by ryangunn on 10/10/16.
 */

public class ResetAlarmClockBrodcastReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent.getAction().equals("android.intent.action.BOOT_COMPLETED")) {
            // Set the alarm here.
            PlayTodayJob.schedule();
        }
    }
}
