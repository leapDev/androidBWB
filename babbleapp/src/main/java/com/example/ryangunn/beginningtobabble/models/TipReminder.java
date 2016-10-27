package com.example.ryangunn.beginningtobabble.models;

import android.app.*;
import android.app.Notification;
import android.content.Context;
import android.content.Intent;
import android.icu.util.Calendar;
import android.os.SystemClock;

import com.example.ryangunn.beginningtobabble.R;
import com.example.ryangunn.beginningtobabble.SetNotficationsBroadcastReciver;
import com.example.ryangunn.beginningtobabble.TipReminderBroadcastReciever;
import com.example.ryangunn.beginningtobabble.VoteActivity;

import java.util.Date;

import io.realm.RealmObject;

import static android.R.attr.delay;
import static android.R.attr.type;
import static android.R.id.message;


public class TipReminder {

    public static void setReminder(int hour, int min, int numberOfTips,int bucketNumber, Context context){
        Intent notificationIntent = new Intent(context, VoteActivity.class);
        notificationIntent.putExtra("NumberOfTips", numberOfTips);
        notificationIntent.putExtra("BucketNumber",bucketNumber);
        notificationIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        PendingIntent notificationPendingIntent = PendingIntent.getActivity(context,0,notificationIntent,0);
        Intent alarmIntent = new Intent(context, TipReminderBroadcastReciever.class);
        alarmIntent.putExtra("notification",getNotification("Read to your Child",notificationPendingIntent,context));
        PendingIntent pendingIntent = PendingIntent.getBroadcast(context.getApplicationContext(), 0, alarmIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        java.util.Calendar now = java.util.Calendar.getInstance();
        java.util.Calendar calendar = java.util.Calendar.getInstance();
        calendar.setTime(new Date());
        now.setTime(new Date());
        calendar.set(java.util.Calendar.MINUTE,min);
        calendar.set(java.util.Calendar.HOUR_OF_DAY,hour);
        calendar.set(java.util.Calendar.SECOND,0);
        calendar.set(java.util.Calendar.MILLISECOND,0);
        if (calendar.compareTo(now) < 0){
            calendar.add(java.util.Calendar.DATE,1);
        }

        AlarmManager alarmManager = (AlarmManager)context.getSystemService(Context.ALARM_SERVICE);
        alarmManager.set(AlarmManager.RTC,System.currentTimeMillis(),pendingIntent);
        //alarmManager.setRepeating(AlarmManager.ELAPSED_REALTIME,calendar.getTimeInMillis(),AlarmManager.INTERVAL_DAY,pendingIntent);
    }

    private static android.app.Notification getNotification(String content, PendingIntent pendingIntent,Context context) {
        android.app.Notification.Builder builder = new android.app.Notification.Builder(context);
        builder.setContentTitle("Answer tips");
        builder.setContentText(content);
        builder.setContentIntent(pendingIntent);
        builder.setSmallIcon(R.drawable.playtoday_icon);
        builder.setAutoCancel(true);
        return builder.build();
    }

    public static void  setUpRepeatingIntentService(Context context){
        java.util.Calendar calendar = java.util.Calendar.getInstance();
        calendar.setTime(new Date());
        calendar.set(java.util.Calendar.HOUR_OF_DAY, 1);
        calendar.set(java.util.Calendar.MINUTE,0);
        calendar.set(java.util.Calendar.SECOND,0);
        AlarmManager alarmMgr = (AlarmManager)context.getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(context, SetNotficationsBroadcastReciver.class);
        PendingIntent alarmIntent = PendingIntent.getBroadcast(context,0,intent,0);
        alarmMgr.setInexactRepeating(AlarmManager.RTC, calendar.getTimeInMillis(),
                AlarmManager.INTERVAL_DAY, alarmIntent);
    }


}
