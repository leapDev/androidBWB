package com.learning.leap.bwb.helper;

import android.content.Context;
import android.util.Log;

import com.learning.leap.bwb.utility.Constant;
import com.learning.leap.bwb.R;
import com.learning.leap.bwb.utility.Utility;
import com.learning.leap.bwb.models.Notification;
import com.learning.leap.bwb.tipReminder.TipReminder;

import org.reactivestreams.Subscriber;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;

import io.reactivex.Observable;
import io.reactivex.functions.Action;
import io.realm.Realm;
import io.realm.RealmResults;



public class ScheduleBucket {
    private Context context;
    private int firstVoteTips;
    private int secondVoteTips;
    private int thirdVoteTips;

    public ScheduleBucket(Context context){
        this.context = context;
    }


    public void diviedTheBucketIntoThree(){
        String[] allStartTime = context.getResources().getStringArray(R.array.start_times_settings_array);
        String[] allEndTime = context.getResources().getStringArray(R.array.end_times_tips_settings_array);
        int indexForUserStartTime = Utility.readIntSharedPreferences(Constant.START_TIME,context);
        int indexForUserEndTime = Utility.readIntSharedPreferences(Constant.END_TIME,context);
        String[]  userStartTime = Arrays.copyOfRange(allStartTime,indexForUserStartTime,allStartTime.length);
        String[] userEndTime = Arrays.copyOfRange(allEndTime,0,indexForUserEndTime);
        Observable<String> userStartTimeObservable = Observable.fromArray(userStartTime);
        Observable<String>userEndTimeObservable = Observable.fromArray(userEndTime);
        ArrayList<String> userTimeWithOutDuplicateds = new ArrayList<>();
        Observable.concat(userStartTimeObservable,userEndTimeObservable).distinct()
                .subscribe(userTimeWithOutDuplicateds::add,
                        Throwable::printStackTrace, () -> {
                            removePlayToday();
                            getNumberOfTipsPerNotification();
                            getThreeBuckets(userTimeWithOutDuplicateds.toArray(new String[userTimeWithOutDuplicateds.size()]));
                        });


    }

    private void removePlayToday() {
        Realm realm = Realm.getDefaultInstance();
        RealmResults<Notification> realmResults = realm.where(Notification.class).equalTo("mPlayToday",true).findAll();
        for (Notification notification :realmResults){
            realm.beginTransaction();
            notification.setPlayToday(false);
            realm.copyToRealmOrUpdate(notification);
            realm.commitTransaction();
        }
    }


    private void getNumberOfTipsPerNotification(){
        int numberOfTotalTips = Utility.readIntSharedPreferences(Constant.NUM_OF_TIPS,context);
        switch (numberOfTotalTips){
            case 3:
                setTipsForBuckets(1,1,1);
                break;
            case 4:
                setTipsForBuckets(1,1,2);
                break;
            case 5:
                setTipsForBuckets(1,2,2);
                break;
            case 6:
                setTipsForBuckets(2,2,2);
                break;
            case 7:
                setTipsForBuckets(2,2,3);
                break;
            case 8:
                setTipsForBuckets(2,3,3);
                break;
            case 9:
                setTipsForBuckets(3,3,3);
                break;
            default:
                setTipsForBuckets(3,3,4);

        }
    }

    private void setTipsForBuckets(int firstBucketTip,int secondBucketTip, int thridBucketTips){
        firstVoteTips = firstBucketTip;
        secondVoteTips = secondBucketTip;
        thirdVoteTips = thridBucketTips;
    }

    private void getThreeBuckets(String[] times){
        int lenght = times.length;
        String[] firstBucket = Arrays.copyOfRange(times,0,(lenght/3));
        String[] secondBucket = Arrays.copyOfRange(times,(lenght/3),(2*(lenght/3)));
        String[] thirdBucket = Arrays.copyOfRange(times,(2*(lenght/3)),lenght);
        startBucket(firstBucket,1,firstVoteTips);
        startBucket(secondBucket,2,secondVoteTips);
        startBucket(thirdBucket,3,thirdVoteTips);

    }

    private void startBucket(String[] bucket,int bucketNumber, int numberOfTips){
        String firstTime = bucket[0];
        String endTime = bucket[bucket.length-1];
        Date firstDateForTheBucket = updateConvertDateToToday(convertStringToDate(firstTime));
        Date endDateForTheBucket = updateConvertDateToToday(convertStringToDate(endTime));
        TipReminder tipReminder = new TipReminder(bucketNumber,numberOfTips,firstDateForTheBucket,endDateForTheBucket,context);
        tipReminder.setNotificationForBucket();
    }

    private Date convertStringToDate(String time){
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("hh:mm a");
        Date date;
        try {
            date = simpleDateFormat.parse(time);
            return  date;

        }catch (ParseException ex){
            return null;
        }
    }
    private Date updateConvertDateToToday(Date date){
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        Calendar updatedCal = Calendar.getInstance();
        updatedCal.set(Calendar.HOUR_OF_DAY,calendar.get(Calendar.HOUR_OF_DAY));
        updatedCal.set(Calendar.MINUTE,calendar.get(Calendar.MINUTE));
        updatedCal.set(Calendar.SECOND,0);
        return updatedCal.getTime();
    }


}
