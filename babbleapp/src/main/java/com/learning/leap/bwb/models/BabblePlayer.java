package com.learning.leap.bwb.models;

import android.content.Context;
import android.provider.Settings;

import com.amazonaws.mobileconnectors.dynamodbv2.dynamodbmapper.DynamoDBAttribute;
import com.amazonaws.mobileconnectors.dynamodbv2.dynamodbmapper.DynamoDBHashKey;
import com.amazonaws.mobileconnectors.dynamodbv2.dynamodbmapper.DynamoDBMapper;
import com.amazonaws.mobileconnectors.dynamodbv2.dynamodbmapper.DynamoDBRangeKey;
import com.amazonaws.mobileconnectors.dynamodbv2.dynamodbmapper.DynamoDBScanExpression;
import com.amazonaws.mobileconnectors.dynamodbv2.dynamodbmapper.DynamoDBTable;
import com.amazonaws.mobileconnectors.dynamodbv2.dynamodbmapper.PaginatedScanList;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClient;
import com.amazonaws.services.dynamodbv2.model.AttributeValue;
import com.learning.leap.bwb.utility.NetworkChecker;
import com.learning.leap.bwb.utility.NetworkCheckerInterface;
import com.learning.leap.bwb.utility.Utility;
import com.learning.leap.bwb.helper.LocalLoadSaveHelper;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.concurrent.Callable;

import io.reactivex.Observable;
import io.reactivex.Scheduler;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;
import io.realm.Realm;
import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;



@DynamoDBTable(tableName = "BabblePlayers3")
public class BabblePlayer extends RealmObject {
    @PrimaryKey
    private String mBabbleID;
    private String mBabyBirthday;
    private String mBabyName;
    private int mZipCode = 0;
    private int userAgeInMonth;
    private Date birthdayDate;
    private String babyGender = "Not Now";

    public BabblePlayer(){

    }


    @DynamoDBHashKey(attributeName = "BabbleID")
    public String getBabbleID() {
        return mBabbleID;
    }

    public void setBabbleID(String babbleID) {
        mBabbleID = babbleID;
    }

    @DynamoDBRangeKey(attributeName = "BabyBirthday")
    public String getBabyBirthday() {
        return mBabyBirthday;
    }

    public void setBabyBirthday(String babyBirthday) {
        mBabyBirthday = babyBirthday;
    }

    @DynamoDBAttribute(attributeName = "BabyName")
    public String getBabyName() {
        return mBabyName;
    }

    public void setBabyName(String babyName) {
        mBabyName = babyName;
    }

    @DynamoDBAttribute(attributeName = "ZipCode")
    public int getZipCode() {
        return mZipCode;
    }

    public void setZipCode(int zipCode) {
        mZipCode = zipCode;
    }

    @DynamoDBAttribute(attributeName = "BabyGender")
    public String getBabyGender() {
        return babyGender;
    }

    public void setBabyGender(String babyGender) {
        this.babyGender = babyGender;
    }

    public int getuserAgeInMonth() {
        return userAgeInMonth;
    }

    public void setuserAgeInMonth() {
        if (checkDate()){
            double userAgeInMonthDouble = (double) daysBetween(this.birthdayDate) / 30;
            this.userAgeInMonth = (int)Math.floor(userAgeInMonthDouble);
        }

    }

    private Date getBirthdayDate() {
        return birthdayDate;
    }

    private void setBirthdayDate(Date birthdayDate) {
        this.birthdayDate = birthdayDate;
    }

    private void saveBabbleUser(DynamoDBMapper mapper,LocalLoadSaveHelper saveHelper) {
        mapper.save(this);
        saveCurrentBabblePlayerSharedPreference(saveHelper);
    }

    private void saveCurrentBabblePlayerSharedPreference(LocalLoadSaveHelper saveHelper){
        saveHelper.saveBabyBirthDay(this.getBabyBirthday());
        saveHelper.saveBabyName(this.getBabyName());
        saveHelper.saveBabbleID(this.getBabbleID());
        saveHelper.saveZipCode(this.getZipCode());
        saveHelper.saveBabyGender(this.getBabyGender());

    }

    public Observable<Object> savePlayerObservable(DynamoDBMapper mapper, NetworkCheckerInterface checker,LocalLoadSaveHelper saveHelper) {

        return Observable.fromCallable(() -> {
            if (checker.isConnected()) {
                saveBabbleUser(mapper,saveHelper);
            } else {
                throw new Exception();
            }
            return new Object();
        }).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread());

    }

    public Observable<PaginatedScanList<Notification>> retriveNotifications(int babyAge,DynamoDBMapper mapper){
        return Observable.fromCallable(() -> {
            DynamoDBScanExpression scanExpression = new DynamoDBScanExpression();
            scanExpression.setLimit(2000);
            HashMap<String, AttributeValue> attributeValue = new HashMap<>();
            String age = Integer.toString(babyAge);
            AttributeValue falseAttributeValue = new AttributeValue();
            falseAttributeValue.setS("false");
            AttributeValue ageAttributeValue = new AttributeValue();
            ageAttributeValue.setN(age);
            attributeValue.put(":val", ageAttributeValue);
            attributeValue.put(":val2", falseAttributeValue);
            scanExpression.setExpressionAttributeValues(attributeValue);
            scanExpression.setFilterExpression("StartMonthNumber<=:val AND EndMonthNumber>=:val AND Deleted=:val2");
            return mapper.scan(Notification.class, scanExpression);
        });
    }

    public static boolean homeScreenAgeCheck(Context context){
        //String sharedPrefBirthDay = LocalLoadSaveHelper.getBabyBirthDay();
       // int sharedPrefBirthDayInMonth = LocalLoadSaveHelper.getUserBirthdayInMonth();
//        BabblePlayer updatedBabblePlayer = new BabblePlayer();
//        updatedBabblePlayer.setBabyBirthday(sharedPrefBirthDay);
//        updatedBabblePlayer.setuserAgeInMonth();
//        if (checkIfAgeIsInRange(0,2,sharedPrefBirthDayInMonth) && updatedBabblePlayer.updatedAgeCheck(3)){
//            return true;
//        }else if (checkIfAgeIsInRange(3,5,sharedPrefBirthDayInMonth) && updatedBabblePlayer.updatedAgeCheck(6)){
//            return true;
//        }else if (checkIfAgeIsInRange(6,8,sharedPrefBirthDayInMonth) && updatedBabblePlayer.updatedAgeCheck(9)){
//            return true;
//        }else if (checkIfAgeIsInRange(9,11,sharedPrefBirthDayInMonth) && updatedBabblePlayer.updatedAgeCheck(12)){
//            return true;
//        }else if (checkIfAgeIsInRange(12,17,sharedPrefBirthDayInMonth) && updatedBabblePlayer.updatedAgeCheck(18)){
//            return true;
//        }else if (checkIfAgeIsInRange(18,23,sharedPrefBirthDayInMonth) && updatedBabblePlayer.updatedAgeCheck(24)){
//            return true;
//        }else if (checkIfAgeIsInRange(24,29,sharedPrefBirthDayInMonth) && updatedBabblePlayer.updatedAgeCheck(30)){
//            return true;
//        }else if (checkIfAgeIsInRange(30,35,sharedPrefBirthDayInMonth) && updatedBabblePlayer.updatedAgeCheck(36)){
//            return true;
//        }else if (checkIfAgeIsInRange(36,47,sharedPrefBirthDayInMonth) && updatedBabblePlayer.updatedAgeCheck(48)){
//            return true;
//        }else {
//            return false;
//        }
        return false;
    }

    private static Boolean checkIfAgeIsInRange(int startMonth,int endMonth, int age){
        return startMonth <= age && endMonth >= age;
    }

    private Boolean updatedAgeCheck(int value){
        return this.getuserAgeInMonth() >= value;
    }


    private float daysBetween(Date birthday){
        Date date = new Date();
        return (float)( (date.getTime() - birthday.getTime()) / (1000 * 60 * 60 * 24));
    }

    public BabblePlayer loadBabblePlayerFronSharedPref(LocalLoadSaveHelper saveHelper){
        BabblePlayer babblePlayer = new BabblePlayer();
        babblePlayer.setBabyBirthday(saveHelper.getBabyBirthDay());
        babblePlayer.setBabyName(saveHelper.getBabyName());
        babblePlayer.setZipCode(saveHelper.getZipCode());
        babblePlayer.setBabyGender(saveHelper.getBabyGender());
        return babblePlayer;
    }



    public boolean checkDate(){
        String pattern = "MM/dd/yyyy";
        SimpleDateFormat format = new SimpleDateFormat(pattern);
        format.setLenient(false);
        try {
            this.setBirthdayDate(format.parse(this.getBabyBirthday()));
        }catch (Exception e) {
            return false;
        }
        return true;
    }


    public boolean checkZipCode() {
        try {
            return Integer.toString(this.getZipCode()).matches("[0-9]{5}");
        }catch (NumberFormatException e){
            return false;
        }

    }

    private boolean checkName() {
        return !checkNameIsEmpty() && !checkNameIsTooLong();
    }

    public Boolean checkNameIsEmpty() {
        return this.getBabyName().isEmpty() || getBabyName().equals("");
    }
    public Boolean checkNameIsTooLong() {
        return this.getBabyName().length() >= 20;
    }

    public Boolean checkIfPlayerIsValid() {
        return checkDate() && checkZipCode() && checkName() && this.getuserAgeInMonth() > 0;
    }

}