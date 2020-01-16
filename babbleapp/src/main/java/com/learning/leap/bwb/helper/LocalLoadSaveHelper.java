package com.learning.leap.bwb.helper;

import android.content.Context;
import android.content.SharedPreferences;

import com.learning.leap.bwb.utility.Constant;
import com.learning.leap.bwb.utility.Utility;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import javax.inject.Inject;

import io.realm.Realm;


public class LocalLoadSaveHelper {

    public SharedPreferences sharedPreferences;
    @Inject
    public LocalLoadSaveHelper(SharedPreferences sharedPreferences){
        this.sharedPreferences = sharedPreferences;

    }



//    public int getNotificationMaxTime(){
//        return Utility.readIntSharedPreferences(Constant.NOTIFICATION_MAX_TIME,context);
//    }
//    public void saveNotificationMaxTime(int time){
//        Utility.writeIntSharedPreferences(Constant.NOTIFICATION_MAX_TIME,time,context);
//    }
//
//    public int getNotificationMinTime(){
//        return Utility.readIntSharedPreferences(Constant.NOTIFICATION_MIN_TIME,context);
//    }
//    public void saveNotificationMinTime(int time){
//        Utility.writeIntSharedPreferences(Constant.NOTIFICATION_MIN_TIME,time,context);
//    }
//
//    public void clearBabbleID(){
//        Utility.writeStringSharedPreferences(Constant.BABBLE_ID,"",context);
//    }

    public String getBabbleID(){
       return sharedPreferences.getString(Constant.BABBLE_ID,"");
    }

    public void saveBabbleID(String babbleID){
        sharedPreferences.edit().putString(Constant.BABBLE_ID,babbleID).apply();
    }

    public String getBabyBirthDay(){
        return sharedPreferences.getString(Constant.BABY_BIRTHDAY,"");
    }

    public void saveBabyBirthDay(String babyBirthDay){
        sharedPreferences.edit().putString(Constant.BABY_BIRTHDAY,babyBirthDay).apply();
    }

    public boolean checkedSaveBabyAged(){
        return sharedPreferences.getBoolean(Constant.SAVED_BABY_AGE,false);
    }

    public void updatedSavedBabyAged(boolean savedBabyAged){
        sharedPreferences.edit().putBoolean(Constant.SAVED_BABY_AGE,savedBabyAged).apply();
    }


    public int getZipCode(){
        return sharedPreferences.getInt(Constant.ZIP_CODE,0);
    }

    public  void saveUserBirthDayInMonth(int month){
       sharedPreferences.edit().putInt(Constant.USER_BDAY_IN_MONTH,month).apply();
    }

    public int getUserBirthdayInMonth(){
        return sharedPreferences.getInt(Constant.USER_BDAY_IN_MONTH,0);
    }

    public void saveZipCode(int zipCode){
        sharedPreferences.edit().putInt(Constant.ZIP_CODE,zipCode).apply();
    }

    public String getBabyName(){
        return sharedPreferences.getString(Constant.BABY_NAME,"");
    }

    public void saveBabyName(String babyName){
        sharedPreferences.edit().putString(Constant.BABY_NAME,babyName).apply();
    }

    public void saveBabyGender(String gender){
        sharedPreferences.edit().putString(Constant.GENDER,gender).apply();
    }

    public String getBabyGender(){
        return sharedPreferences.getString(Constant.GENDER,"");
    }

    public static Date getLastDayTurnedOff(Context context) throws Exception{
        String date = Utility.readStringSharedPreferences(Constant.LAST_DAY_TURNED_OFF,context);
        return new SimpleDateFormat("MM/dd/yyyy",Locale.getDefault()).parse(date);
    }

    public static void SaveLastDayTurnedOff(Boolean clear,Context context){
        String date;
        if (clear){
           date = null;
        }else {
           date = new SimpleDateFormat("MM/dd/yyyy", Locale.getDefault()).format(new Date());
        }

        //sharedPreferences.edit().putInt(Constant.LAST_DAY_TURNED_OFF,date);
    }

    public void  saveNotificationSize(int size){
        sharedPreferences.edit().putInt(Constant.NOTIFICATION_SIZE,size).apply();
    }

//    public int getNotificationTotalSize(){
//        return Utility.readIntSharedPreferences(Constant.NOTIFICATION_SIZE,context);
//    }


}
