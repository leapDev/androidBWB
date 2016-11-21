package com.learning.leap.beginningtobabble.models;

import android.app.*;
import android.content.Context;
import android.content.Intent;
import android.os.Build;

import com.learning.leap.beginningtobabble.SetNotficationsBroadcastReciver;
import com.learning.leap.beginningtobabble.TipReminderBroadcastReciever;

import java.util.Date;


public class TipReminder {

    public static void setReminder(int hour, int min, int bucketNumber, Context context){

        Intent alarmIntent = new Intent(context, TipReminderBroadcastReciever.class);
        alarmIntent.putExtra("id",bucketNumber);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(context.getApplicationContext(), bucketNumber, alarmIntent, PendingIntent.FLAG_UPDATE_CURRENT);
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
        if (Build.VERSION.SDK_INT >= 23){
            alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP,calendar.getTimeInMillis(),pendingIntent);
        }else if (Build.VERSION.SDK_INT >=19){
            alarmManager.setExact(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), pendingIntent);
        }else {
            alarmManager.set(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), pendingIntent);
        }

    }



    public static void  setUpRepeatingIntentService(Context context){
        java.util.Calendar calendar = java.util.Calendar.getInstance();
        java.util.Calendar now = java.util.Calendar.getInstance();
        calendar.setTimeInMillis(System.currentTimeMillis());
        calendar.set(java.util.Calendar.HOUR_OF_DAY, 1);
        calendar.set(java.util.Calendar.MINUTE,0);
        calendar.set(java.util.Calendar.SECOND,0);
        AlarmManager alarmMgr = (AlarmManager)context.getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(context, SetNotficationsBroadcastReciver.class);
        PendingIntent alarmIntent = PendingIntent.getBroadcast(context,0,intent,0);
        if (calendar.compareTo(now) < 0){
            calendar.add(java.util.Calendar.DATE,1);
        }
        alarmMgr.setInexactRepeating(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(),
                AlarmManager.INTERVAL_DAY, alarmIntent);
    }


}
