package com.learning.leap.beginningtobabble.helper;

import android.content.Context;

import com.learning.leap.beginningtobabble.models.TipReminder;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.concurrent.TimeUnit;
import io.realm.Realm;
import io.realm.RealmResults;


public class ScheduleBucket {
    private int startTimeHourForBucket;
    private int startTimeMinForBucket;
    private int endTimeHourForBucket;
    private int endTimeMinForBucket;
    private int mBucketNumber;
    Context mContext;
    Realm mRealm;

    public ScheduleBucket(int startTimeHourForBucket,int startTimeMinForBucket,int endTimeHourForBucket, int endTimeMinForBucket, int bucketNumber,Context context, Realm realm){
        this.startTimeMinForBucket = startTimeMinForBucket;
        this.startTimeHourForBucket = startTimeHourForBucket;
        this.endTimeHourForBucket = endTimeHourForBucket;
        this.endTimeMinForBucket = endTimeMinForBucket;
        this.mBucketNumber = bucketNumber;
        mContext = context;
        mRealm = realm;
    }


    public void findAverageAnswerTime(){
        RealmResults<AnswerNotification> mAnswerNotificatoinsResults = mRealm.where(AnswerNotification.class).equalTo("mAnswerBucket",mBucketNumber).findAll();
        long timeDifference = 0;
        if (mAnswerNotificatoinsResults.size() == 0){
            TipReminder.setReminder(startTimeHourForBucket,startTimeMinForBucket,mBucketNumber,mContext);
        }else {
            for (AnswerNotification answerNotification : mAnswerNotificatoinsResults) {
                long answerTimeDifference = timeDifference(answerNotification.getAnswerTime());
                timeDifference = timeDifference + answerTimeDifference;
            }
            gettime(timeDifference, mAnswerNotificatoinsResults.size());
        }
    }

    public void gettime(long totalTimeDifference, int numberOfAnswerNotification){
        long average = totalTimeDifference/(long)numberOfAnswerNotification;
        long aveageMinutes = TimeUnit.MINUTES.convert(average,TimeUnit.MINUTES);
        int hours = (int)aveageMinutes/ 60;
        int mins = (int)aveageMinutes % 60;
        int notificaionHour = startTimeHourForBucket + hours;
        int notificationMins = mins + startTimeMinForBucket;
        if (notificationMins >= 60){
            notificationMins = notificationMins - 60;
            notificaionHour = notificaionHour + 1;
        }
        if (notificaionHour > endTimeHourForBucket && notificationMins > endTimeMinForBucket){
            TipReminder.setReminder(endTimeHourForBucket,endTimeMinForBucket,mBucketNumber,mContext);
        }else {

            TipReminder.setReminder(notificaionHour, setNoficationMins(notificationMins), mBucketNumber, mContext);
        }
    }

    public int setNoficationMins(int notificationMins){
        return (int)Math.round((notificationMins + 5)/ 10.0) * 10;
    }

    public long timeDifference(Date answerDate){
       Calendar answerDatecalendar = Calendar.getInstance();
        answerDatecalendar.setTime(answerDate);
       int answerHour = answerDatecalendar.get(Calendar.HOUR);
        int answerMins = answerDatecalendar.get(Calendar.MINUTE);
       return getDateForTimeDifference(answerHour,answerMins).getTime() - getDateForTimeDifference(startTimeHourForBucket,startTimeMinForBucket).getTime();

    }

    public Date getDateForTimeDifference(int hour, int min){
        Calendar calendar = new GregorianCalendar();
        calendar.setTime(new Date());
        calendar.set(Calendar.HOUR,hour);
        calendar.set(Calendar.MINUTE,min);
        calendar.set(Calendar.SECOND,0);
        return calendar.getTime();
    }


}
