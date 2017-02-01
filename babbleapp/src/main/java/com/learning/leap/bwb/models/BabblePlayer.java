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
import com.learning.leap.bwb.utility.Utility;
import com.learning.leap.bwb.helper.LocalLoadSaveHelper;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.exceptions.Exceptions;
import rx.schedulers.Schedulers;


@DynamoDBTable(tableName = "BabblePlayers3")
public class BabblePlayer extends RealmObject {
    @PrimaryKey
    private String mBabbleID;
    private String mBabyBirthday;
    private String mBabyName;
    private int mZipCode;
    private int userAgeInMonth;
    private Date birthdayDate;

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

    public int getuserAgeInMonth() {
        return userAgeInMonth;
    }

    public void setuserAgeInMonth() {
        if (checkDate()){
            double userAgeInMonthDouble = (double) daysBetween(this.birthdayDate) / 30;
            this.userAgeInMonth = (int)Math.floor(userAgeInMonthDouble);
        }

    }

    public Date getBirthdayDate() {
        return birthdayDate;
    }

    private void setBirthdayDate(Date birthdayDate) {
        this.birthdayDate = birthdayDate;
    }

    private void saveBabbleUser(DynamoDBMapper mapper, android.content.Context context) {
        mapper.save(this);
        saveCurrentBabblePlayerSharedPreference(context);
    }

    private void saveCurrentBabblePlayerSharedPreference(android.content.Context context){
        LocalLoadSaveHelper.saveBabyBirthDay(this.getBabyBirthday(),context);
        LocalLoadSaveHelper.saveBabyName(this.getBabyName(),context);
        LocalLoadSaveHelper.saveBabbleID(this.getBabbleID(),context);
        LocalLoadSaveHelper.saveZipCode(this.getZipCode(),context);

    }

    public rx.Observable<Void> savePlayerObservable(final android.content.Context context) {
        AmazonDynamoDBClient ddbClient = new AmazonDynamoDBClient(Utility.getCredientail(context));
        final DynamoDBMapper mapper = new DynamoDBMapper(ddbClient);
        return rx.Observable.create(new rx.Observable.OnSubscribe<Void>() {
            @Override
            public void call(Subscriber<? super Void> subscriber) {
                try {
                    if (Utility.isNetworkAvailable(context)) {
                        saveBabbleUser(mapper, context);
                        subscriber.onCompleted();
                    }else {
                        throw new Exception();
                    }
                }catch (Exception e){
                    subscriber.onError(e);
                }

            }
        }).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread());
    }

    public PaginatedScanList<Notification> retriveNotifications(Context context, int babyAge) throws Exception{
        AmazonDynamoDBClient ddbClient = new AmazonDynamoDBClient(Utility.getCredientail(context));
        DynamoDBMapper mapper = new DynamoDBMapper(ddbClient);
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
    }

    public static boolean homeScreenAgeCheck(Context context){
        String sharedPrefBirthDay = LocalLoadSaveHelper.getBabyBirthDay(context);
        int sharedPrefBirthDayInMonth = LocalLoadSaveHelper.getUserBirthdayInMonth(context);
        BabblePlayer updatedBabblePlayer = new BabblePlayer();
        updatedBabblePlayer.setBabyBirthday(sharedPrefBirthDay);
        updatedBabblePlayer.setuserAgeInMonth();
        if (checkIfAgeIsInRange(0,2,sharedPrefBirthDayInMonth) && updatedBabblePlayer.updatedAgeCheck(3)){
            return true;
        }else if (checkIfAgeIsInRange(3,5,sharedPrefBirthDayInMonth) && updatedBabblePlayer.updatedAgeCheck(6)){
            return true;
        }else if (checkIfAgeIsInRange(6,8,sharedPrefBirthDayInMonth) && updatedBabblePlayer.updatedAgeCheck(9)){
            return true;
        }else if (checkIfAgeIsInRange(9,11,sharedPrefBirthDayInMonth) && updatedBabblePlayer.updatedAgeCheck(12)){
            return true;
        }else if (checkIfAgeIsInRange(12,17,sharedPrefBirthDayInMonth) && updatedBabblePlayer.updatedAgeCheck(18)){
            return true;
        }else if (checkIfAgeIsInRange(18,23,sharedPrefBirthDayInMonth) && updatedBabblePlayer.updatedAgeCheck(24)){
            return true;
        }else if (checkIfAgeIsInRange(24,29,sharedPrefBirthDayInMonth) && updatedBabblePlayer.updatedAgeCheck(30)){
            return true;
        }else if (checkIfAgeIsInRange(30,35,sharedPrefBirthDayInMonth) && updatedBabblePlayer.updatedAgeCheck(36)){
            return true;
        }else if (checkIfAgeIsInRange(36,47,sharedPrefBirthDayInMonth) && updatedBabblePlayer.updatedAgeCheck(48)){
            return true;
        }else {
            return false;
        }
    }

    private static Boolean checkIfAgeIsInRange(int startMonth,int endMonth, int age){
        return startMonth <= age && endMonth >= age;
    }

    private Boolean updatedAgeCheck(int value){
        return this.getuserAgeInMonth() >= value;
    }


    public float daysBetween(Date birthday){
        Date date = new Date();
        return (float)( (date.getTime() - birthday.getTime()) / (1000 * 60 * 60 * 24));
    }

    public BabblePlayer loadBabblePlayerFronSharedPref(android.content.Context context){
        BabblePlayer babblePlayer = new BabblePlayer();
        babblePlayer.setBabyBirthday(LocalLoadSaveHelper.getBabyBirthDay(context));
        babblePlayer.setBabyName(LocalLoadSaveHelper.getBabyName(context));
        babblePlayer.setZipCode(LocalLoadSaveHelper.getZipCode(context));
        return babblePlayer;
    }



    public BabblePlayer loadPlayerFromMapper(Context context) {
        AmazonDynamoDBClient ddbClient = new AmazonDynamoDBClient(Utility.getCredientail(context));
         DynamoDBMapper mapper = new DynamoDBMapper(ddbClient);
        String userID = Settings.Secure.getString(context.getContentResolver(), Settings.Secure.ANDROID_ID);
        return mapper.load(BabblePlayer.class,userID);
    }

    public boolean checkDate(){
        String pattern = "MM/dd/yyyy";
        SimpleDateFormat format = new SimpleDateFormat(pattern);
        format.setLenient(false);
        try {
            this.setBirthdayDate(format.parse(this.getBabyBirthday()));
        }catch (ParseException e) {
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

    public boolean checkName() {
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
