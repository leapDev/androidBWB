package com.learning.leap.bwb.tipReminder;

import android.app.*;
import android.content.Context;
import android.content.Intent;
import android.os.Build;

import com.crashlytics.android.answers.Answers;
import com.crashlytics.android.answers.CustomEvent;
import com.learning.leap.bwb.BuildConfig;
import com.learning.leap.bwb.helper.AnswerNotification;
import com.learning.leap.bwb.utility.Constant;
import com.learning.leap.bwb.utility.Utility;

import java.math.BigInteger;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
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

    public void setReminder(Date notificationDate){
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

        setTipToAlarmManger(calendar.getTimeInMillis(),pendingIntent);
        if (BuildConfig.FLAVOR.equals("regular")) {

        }else {

        }
        //Add follow up message
//        if (bucketNumber == 3){
//            calendar.add(Calendar.HOUR,1);
//            setTipToAlarmManger(calendar.getTimeInMillis(),pendingIntent);
//        }
        Utility.addCustomEvent(Constant.TIP_SCHEUDLED_TIME,Utility.getUserID(context),null);
        Answers.getInstance().logCustom(new CustomEvent(Constant.TIP_SCHEUDLED_TIME)
                             .putCustomAttribute("Time",convertDate(calendar.getTime()))
                             .putCustomAttribute("ID",Utility.getUserID(context)));

    }

    private void setTipToAlarmManger(long timeInMills, PendingIntent pendingIntent){
        AlarmManager alarmManager = (AlarmManager)context.getSystemService(Context.ALARM_SERVICE);
        if (Build.VERSION.SDK_INT >= 23){
            alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP,timeInMills,pendingIntent);
        }else if (Build.VERSION.SDK_INT >=19){
            alarmManager.setExact(AlarmManager.RTC_WAKEUP, timeInMills, pendingIntent);
        }else {
            alarmManager.set(AlarmManager.RTC_WAKEUP, timeInMills, pendingIntent);
        }
    }

    private String convertDate(Date date){
        DateFormat dateFormat = new SimpleDateFormat("MM/dd/yyy HH:mm:ss");
        return dateFormat.format(date);
    }


}
