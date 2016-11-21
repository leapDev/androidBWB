package com.learning.leap.beginningtobabble.helper;

import android.content.Context;

import com.learning.leap.beginningtobabble.Constant;
import com.learning.leap.beginningtobabble.Utility;
import com.learning.leap.beginningtobabble.models.LocalNotification;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import io.realm.Realm;
import io.realm.RealmResults;


public class LocalLoadSaveHelper {
   private static Realm mRealm = Realm.getDefaultInstance();

    public static void ClearNotifications(){
        final RealmResults<LocalNotification> results = mRealm.where(LocalNotification.class).findAll();
        mRealm.executeTransactionAsync(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
            results.deleteAllFromRealm();
            }
        });
    }

    public static void saveNotifications(List<LocalNotification> localNotifications){
        mRealm.beginTransaction();
        mRealm.copyToRealm(localNotifications);
        mRealm.commitTransaction();
    }

    public static RealmResults<LocalNotification> getNotifications(){
        return mRealm.where(LocalNotification.class).findAll();
    }

    public static int getNotificationMaxTime(Context context){
        return Utility.readIntSharedPreferences(Constant.NOTIFICATION_MAX_TIME,context);
    }
    public static void saveNotificationMaxTime(int time,Context context){
        Utility.writeIntSharedPreferences(Constant.NOTIFICATION_MAX_TIME,time,context);
    }

    public static int getNotificationMinTime(Context context){
        return Utility.readIntSharedPreferences(Constant.NOTIFICATION_MIN_TIME,context);
    }
    public static void saveNotificationMinTime(int time,Context context){
        Utility.writeIntSharedPreferences(Constant.NOTIFICATION_MIN_TIME,time,context);
    }

    public static void clearBabbleID(Context context){
        Utility.writeStringSharedPreferences(Constant.BABBLE_ID,null,context);
    }

    public static String getBabbleID(Context context){
       return Utility.readStringSharedPreferences(Constant.BABBLE_ID,context);
    }

    public static void saveBabbleID(String babbleID,Context context){
        Utility.writeStringSharedPreferences(Constant.BABBLE_ID,babbleID,context);
    }

    public static String getBabyBirthDay(Context context){
        return Utility.readStringSharedPreferences(Constant.BABY_BIRTHDAY,context);
    }

    public static void saveBabyBirthDay(String babyBirthDay,Context context){
        Utility.writeStringSharedPreferences(Constant.BABY_BIRTHDAY,babyBirthDay,context);
    }

    public static int getZipCode(Context context){
        return Utility.readIntSharedPreferences(Constant.ZIP_CODE,context);
    }

    public static void saveZipCode(int zipCode,Context context){
        Utility.writeIntSharedPreferences(Constant.ZIP_CODE,zipCode,context);
    }

    public static String getBabyName(Context context){
        return Utility.readStringSharedPreferences(Constant.BABY_NAME,context);
    }

    public static void saveBabyName(String babyName,Context context){
        Utility.writeStringSharedPreferences(Constant.BABY_NAME,babyName,context);
    }

    public static Date getLastDayTurnedOff(Context context) throws Exception{
        String date = Utility.readStringSharedPreferences(Constant.LAST_DAY_TURNED_OFF,context);
        return new SimpleDateFormat("MM/dd/yyyy").parse(date);
    }

    public static void SaveLastDayTurnedOff(Boolean clear,Context context){
        String date;
        if (clear){
           date = null;
        }else {
           date = new SimpleDateFormat("MM/dd/yyyy").format(new Date());
        }

        Utility.writeStringSharedPreferences(Constant.LAST_DAY_TURNED_OFF,date,context);
    }


}