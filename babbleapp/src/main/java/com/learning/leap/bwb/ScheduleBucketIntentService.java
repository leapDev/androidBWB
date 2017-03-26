package com.learning.leap.bwb;

import android.app.IntentService;
import android.content.Context;
import android.content.Intent;

import com.learning.leap.bwb.helper.ScheduleBucket;


public class ScheduleBucketIntentService extends IntentService {

    Context context;
  public ScheduleBucketIntentService(){
      super("ScheduleBucketIntentService");
  }

    @Override
    protected void onHandleIntent(Intent intent) {
        PlayTodayJob.schedule();
        ScheduleBucket scheduleBucket = new ScheduleBucket(context);
        scheduleBucket.diviedTheBucketIntoThree();
    }

    @Override
    public void onCreate() {
        super.onCreate();
        context = getApplicationContext();
    }
}
