package com.learning.leap.bwb.helper;

import android.content.Context;

import com.learning.leap.bwb.utility.Constant;
import com.learning.leap.bwb.utility.Utility;

import java.text.SimpleDateFormat;
import java.util.Date;

import io.realm.Realm;


public class LocalLoadSaveHelper {

    public Context context;
    public LocalLoadSaveHelper(Context context){
        this.context = context;
    }



    public int getNotificationMaxTime(){
        return Utility.readIntSharedPreferences(Constant.NOTIFICATION_MAX_TIME,context);
    }
    public void saveNotificationMaxTime(int time){
        Utility.writeIntSharedPreferences(Constant.NOTIFICATION_MAX_TIME,time,context);
    }

    public int getNotificationMinTime(){
        return Utility.readIntSharedPreferences(Constant.NOTIFICATION_MIN_TIME,context);
    }
    public void saveNotificationMinTime(int time){
        Utility.writeIntSharedPreferences(Constant.NOTIFICATION_MIN_TIME,time,context);
    }

    public void clearBabbleID(){
        Utility.writeStringSharedPreferences(Constant.BABBLE_ID,"",context);
    }

    public String getBabbleID(){
       return Utility.readStringSharedPreferences(Constant.BABBLE_ID,context);
    }

    public void saveBabbleID(String babbleID){
        Utility.writeStringSharedPreferences(Constant.BABBLE_ID,babbleID,context);
    }

    public String getBabyBirthDay(){
        return Utility.readStringSharedPreferences(Constant.BABY_BIRTHDAY,context);
    }

    public void saveBabyBirthDay(String babyBirthDay){
        Utility.writeStringSharedPreferences(Constant.BABY_BIRTHDAY,babyBirthDay,context);
    }

    public int getZipCode(){
        return Utility.readIntSharedPreferences(Constant.ZIP_CODE,context);
    }

    public static void saveUserBirthDayInMonth(int month,Context context){
        Utility.writeIntSharedPreferences(Constant.USER_BDAY_IN_MONTH,month,context);
    }

    public int getUserBirthdayInMonth(){
        return Utility.readIntSharedPreferences(Constant.USER_BDAY_IN_MONTH,context);
    }

    public void saveZipCode(int zipCode){
        Utility.writeIntSharedPreferences(Constant.ZIP_CODE,zipCode,context);
    }

    public String getBabyName(){
        return Utility.readStringSharedPreferences(Constant.BABY_NAME,context);
    }

    public void saveBabyName(String babyName){
        Utility.writeStringSharedPreferences(Constant.BABY_NAME,babyName,context);
    }

    public void saveBabyGender(String gender){
        Utility.writeStringSharedPreferences(Constant.GENDER,gender,context);
    }

    public String getBabyGender(){
        return Utility.readStringSharedPreferences(Constant.GENDER,context);
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

    public void  saveNotificationSize(int size){
        Utility.writeIntSharedPreferences(Constant.NOTIFICATION_SIZE,size,context);
    }

    public int getNotificationTotalSize(){
        return Utility.readIntSharedPreferences(Constant.NOTIFICATION_SIZE,context);
    }


}