package com.learning.leap.bwb.tipReminder;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.learning.leap.bwb.helper.ScheduleBucket;


public class SetNotficationsBroadcastReciver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent)
    {
        ScheduleBucket scheduleBucket = new ScheduleBucket(context);
        scheduleBucket.diviedTheBucketIntoThree();
    }
}
