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
            this.userAgeInMonth = (int) daysBetween(this.birthdayDate) / 30;
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
        if (checkIfAgeIsInRange(0,3,sharedPrefBirthDayInMonth) && updatedBabblePlayer.updatedAgeCheck(4)){
            return true;
        }else if (checkIfAgeIsInRange(3,6,sharedPrefBirthDayInMonth) && updatedBabblePlayer.updatedAgeCheck(7)){
            return true;
        }else if (checkIfAgeIsInRange(6,9,sharedPrefBirthDayInMonth) && updatedBabblePlayer.updatedAgeCheck(10)){
            return true;
        }else if (checkIfAgeIsInRange(9,12,sharedPrefBirthDayInMonth) && updatedBabblePlayer.updatedAgeCheck(13)){
            return true;
        }else if (checkIfAgeIsInRange(12,18,sharedPrefBirthDayInMonth) && updatedBabblePlayer.updatedAgeCheck(19)){
            return true;
        }else if (checkIfAgeIsInRange(18,24,sharedPrefBirthDayInMonth) && updatedBabblePlayer.updatedAgeCheck(25)){
            return true;
        }else if (checkIfAgeIsInRange(24,30,sharedPrefBirthDayInMonth) && updatedBabblePlayer.updatedAgeCheck(31)){
            return true;
        }else if (checkIfAgeIsInRange(30,36,sharedPrefBirthDayInMonth) && updatedBabblePlayer.updatedAgeCheck(37)){
            return true;
        }else if (checkIfAgeIsInRange(36,48,sharedPrefBirthDayInMonth) && updatedBabblePlayer.updatedAgeCheck(49)){
            return true;
        }else {
            return false;
        }
    }

    private  static Boolean checkIfAgeIsInRange(int startMonth,int endMonth, int age){
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
        try {
            String pattern = "MM/dd/yyyy";
            SimpleDateFormat format = new SimpleDateFormat(pattern);
            format.setLenient(false);
            this.setBirthdayDate(format.parse(this.getBabyBirthday()));
        }catch (ParseException e) {
            return false;
        }
        return true;
    }


    public boolean checkZipCode() {
        return Integer.toString(this.getZipCode()).matches("[0-9]{5}");
    }

    public boolean checkName() {
        return !checkNameIsEmpty() && checkNameIsTooLong();
    }

    public Boolean checkNameIsEmpty() {
        return this.getBabyName().isEmpty();
    }
    public Boolean checkNameIsTooLong() {
        return this.getBabyName().length() <= 20;
    }

    public Boolean checkIfPlayerIsValid() {
        return checkDate() && checkZipCode() && checkName() && this.getuserAgeInMonth() > 0;
    }

}
