package com.learning.leap.beginningtobabbleapp.tipReminder;

import android.app.*;
import android.content.Context;
import android.content.Intent;
import android.os.Build;

import com.learning.leap.beginningtobabbleapp.helper.AnswerNotification;

import java.math.BigInteger;
import java.util.Date;

import io.realm.Realm;
import io.realm.RealmResults;


public class TipReminder {

    private int bucketNumber;
    private int numberOfTips;
    private Date startTimeDate;
    private Date endTimeDate;
    private Context context;

    public TipReminder(int bucketNumber, int numberOfTips,Date startTimeDate, Date endTimeDate, Context context){
        this.bucketNumber = bucketNumber;
        this.numberOfTips = numberOfTips;
        this.startTimeDate = startTimeDate;
        this.endTimeDate = endTimeDate;
        this.context = context;
    }
    public void setNotificationForBucket(){
        Realm realm = Realm.getDefaultInstance();
        RealmResults<AnswerNotification> mAnswerNotificatoinsResults = realm.where(AnswerNotification.class).equalTo("mAnswerBucket",bucketNumber).findAll();
        if (mAnswerNotificatoinsResults.size() == 0){
            setReminder(startTimeDate);
        }else {
            setReminder(findAverageDateForBucket(mAnswerNotificatoinsResults));
        }
    }

    private Date findAverageDateForBucket(RealmResults<AnswerNotification> answerNotifications){
        BigInteger total = BigInteger.ZERO;
        for (AnswerNotification answerNotification: answerNotifications) {
            total = total.add(BigInteger.valueOf(answerNotification.mAnswerTime.getTime()));
        }
        BigInteger averageMillis = total.divide(BigInteger.valueOf(answerNotifications.size()));
        Date averageDate = new Date(averageMillis.longValue());
        if (averageDate.after(startTimeDate) && averageDate.before(endTimeDate)){
            return averageDate;
        }else {
            return startTimeDate;
        }
    }

    private void setReminder(Date notificationDate){

        Intent alarmIntent = new Intent(context, VoteNotificationBroadcastReciever.class);
        alarmIntent.putExtra("id",bucketNumber);
        alarmIntent.putExtra("NumberOfTips",numberOfTips);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(context.getApplicationContext(), bucketNumber, alarmIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        java.util.Calendar now = java.util.Calendar.getInstance();
        java.util.Calendar calendar = java.util.Calendar.getInstance();
        calendar.setTime(notificationDate);
        now.setTime(new Date());
        calendar.set(java.util.Calendar.SECOND,0);
        calendar.set(java.util.Calendar.MILLISECOND,0);
        if (calendar.getTime().before(now.getTime())){
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
        calendar.set(java.util.Calendar.HOUR_OF_DAY, 1);
        calendar.set(java.util.Calendar.MINUTE,0);
        calendar.set(java.util.Calendar.SECOND,0);
        AlarmManager alarmMgr = (AlarmManager)context.getApplicationContext().getSystemService(Context.ALARM_SERVICE);
        if (calendar.getTimeInMillis() <= now.getTimeInMillis()){
            calendar.add(java.util.Calendar.DATE,1);
        }
        Intent intent = new Intent(context, SetNotficationsBroadcastReciver.class);
        PendingIntent repeatingIntent = PendingIntent.getBroadcast(context.getApplicationContext(),101,intent,PendingIntent.FLAG_UPDATE_CURRENT);
        alarmMgr.setInexactRepeating(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(),
                AlarmManager.INTERVAL_DAY, repeatingIntent);
    }

    public static void cancelRepeatingIntentService(Context context) {
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(context, SetNotficationsBroadcastReciver.class);
        PendingIntent repeatingIntent = PendingIntent.getBroadcast(context.getApplicationContext(),101,intent,PendingIntent.FLAG_UPDATE_CURRENT);
        alarmManager.cancel(repeatingIntent);
    }



}
