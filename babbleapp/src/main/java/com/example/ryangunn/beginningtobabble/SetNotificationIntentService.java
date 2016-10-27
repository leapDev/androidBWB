package com.example.ryangunn.beginningtobabble;

import android.app.IntentService;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.example.ryangunn.beginningtobabble.helper.ScheduleBucket;
import com.example.ryangunn.beginningtobabble.models.Notification;

import java.lang.reflect.Array;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashSet;
import java.util.Set;

import io.realm.Realm;
import io.realm.RealmResults;
import rx.Observable;
import rx.Subscriber;

import static android.R.id.input;
import static android.R.id.list;
import static android.media.CamcorderProfile.get;
import static com.example.ryangunn.beginningtobabble.R.string.userStartTime;


public class SetNotificationIntentService extends IntentService {
    Context mContext;
    public SetNotificationIntentService(){
        super("setNotificationIntentService");
    }

    @Override
    public void onCreate() {
        super.onCreate();
        mContext = getApplicationContext();

    }

    @Override
    protected void onHandleIntent(Intent intent) {
       String[] allStartTime = getResources().getStringArray(R.array.first_tip_settings_array);
        String[] allEndTime = getResources().getStringArray(R.array.second_tips_settings_array);
        int indexForUserStartTime = Utility.readIntSharedPreferences(Constant.START_TIME,mContext);
        int indexForUserEndTime = Utility.readIntSharedPreferences(Constant.END_TIME,mContext);
        String[]  userStartTime = Arrays.copyOfRange(allStartTime,indexForUserStartTime,allStartTime.length);
        String[] userEndTime = Arrays.copyOfRange(allEndTime,0,indexForUserEndTime);
        ArrayList<String> userTime = new ArrayList<>();
        for (String startTime: userStartTime){
            userTime.add(startTime);
        }
        Observable<String>userStartTimeObservable = Observable.from(userStartTime);
        Observable<String>userEndTimeObservable = Observable.from(userEndTime);
        final ArrayList<String>userTimeWithOutDuplicateds = new ArrayList<>();
        Observable.concat(userStartTimeObservable,userEndTimeObservable).distinct().subscribe(new Subscriber<String>() {
            @Override
            public void onCompleted() {
                getThreeBuckets(userTimeWithOutDuplicateds.toArray(new String[userTimeWithOutDuplicateds.size()]));
            }

            @Override
            public void onError(Throwable e) {

            }

            @Override
            public void onNext(String s) {
                userTimeWithOutDuplicateds.add(s);
            }
        });

    }


    public void getThreeBuckets(String[] times){
        int lenght = times.length;
        String[] firstBucket = Arrays.copyOfRange(times,0,(lenght/3));
        String[] secondBucket = Arrays.copyOfRange(times,(lenght/3),(2*(lenght/3)));
        String[] thirdBucket = Arrays.copyOfRange(times,(2*(lenght/3)),lenght);
        startBucket(firstBucket,1);
        startBucket(secondBucket,2);
        startBucket(thirdBucket,3);

    }

    private void startBucket(String[] bucket, int bucketNumber){
        String firstTime = bucket[0];
        String endTime = bucket[bucket.length-1];
        Date firstDate = convertStringToDate(firstTime);
        Date endDate = convertStringToDate(endTime);
        Calendar calendar = GregorianCalendar.getInstance(); // creates a new calendar instance
        calendar.setTime(firstDate);
       int firstDateHour = calendar.get(Calendar.HOUR_OF_DAY);
        int firstDateMin = calendar.get(Calendar.MINUTE);
        calendar.setTime(endDate);
        int endDateHour = calendar.get(Calendar.HOUR_OF_DAY);
        int endDateMin = calendar.get(Calendar.MINUTE);
        Realm realm = Realm.getDefaultInstance();
        RealmResults<Notification> realmResults = realm.where(Notification.class).equalTo("mFavorite",true).findAll();
        for (Notification notification :realmResults){
            notification.setPlayToday(false);
            realm.beginTransaction();
            realm.copyToRealmOrUpdate(notification);
            realm.commitTransaction();
        }

        ScheduleBucket scheduleBucket = new ScheduleBucket(firstDateHour,firstDateMin,endDateHour,endDateMin,bucketNumber,mContext,Realm.getDefaultInstance());
        scheduleBucket.findAverageAnswerTime();

    }

    private Date convertStringToDate(String time){
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("hh:mm a");
        Date date;
        try {
            date = simpleDateFormat.parse(time);
            return  date;

        }catch (ParseException ex){
            Log.d("did", "startBucket: " +ex);
            return null;
        }
    }

}
