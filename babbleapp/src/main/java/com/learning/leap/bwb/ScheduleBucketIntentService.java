package com.learning.leap.bwb;

import android.app.IntentService;
import android.content.Context;
import android.content.Intent;

import com.learning.leap.bwb.helper.ScheduleBucket;

/**
 * Created by ryangunn on 12/30/16.
 */

public class ScheduleBucketIntentService extends IntentService {

    Context context;
  public ScheduleBucketIntentService(){
      super("ScheduleBucketIntentService");
  }

    @Override
    protected void onHandleIntent(Intent intent) {
        ScheduleBucket scheduleBucket = new ScheduleBucket(context);
        scheduleBucket.diviedTheBucketIntoThree();
        PlayTodayJob.schedule();
    }

    @Override
    public void onCreate() {
        super.onCreate();
        context = getApplicationContext();
    }
}
