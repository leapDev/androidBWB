package com.learning.leap.bwb;

import android.content.Intent;
import android.support.annotation.NonNull;

import com.evernote.android.job.Job;
import com.evernote.android.job.JobRequest;

import java.util.Calendar;

/**
 * Created by ryangunn on 12/28/16.
 */

public class PlayTodayJob extends Job {

    public static final String PLAY_TODAY = "PLAY_TODAY";


    public static void schedule() {
        java.util.Calendar calendar = java.util.Calendar.getInstance();
        java.util.Calendar now = java.util.Calendar.getInstance();
        calendar.set(java.util.Calendar.HOUR_OF_DAY, 1);
        calendar.set(java.util.Calendar.MINUTE,0);
        calendar.set(java.util.Calendar.SECOND,0);
        calendar.set(Calendar.MILLISECOND,0);

        if (calendar.compareTo(now) < 0){
            calendar.add(java.util.Calendar.DATE,1);
        }

        long startTime = calendar.getTimeInMillis() - System.currentTimeMillis();


        new JobRequest.Builder(PLAY_TODAY)
                .setExact(startTime)
                .setPersisted(true)
                .setUpdateCurrent(false)
                .build()
                .schedule();

    }


    @NonNull
    @Override
    protected Result onRunJob(Params params) {
        Intent scheduleBucketIntentServiceIntent = new Intent(getContext(),ScheduleBucketIntentService.class);
        getContext().startService(scheduleBucketIntentServiceIntent);
        return Result.SUCCESS;
    }
}
