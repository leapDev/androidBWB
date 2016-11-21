package com.learning.leap.beginningtobabble.models;

import com.amazonaws.mobileconnectors.dynamodbv2.dynamodbmapper.DynamoDBAttribute;
import com.amazonaws.mobileconnectors.dynamodbv2.dynamodbmapper.DynamoDBHashKey;
import com.amazonaws.mobileconnectors.dynamodbv2.dynamodbmapper.DynamoDBRangeKey;
import com.amazonaws.mobileconnectors.dynamodbv2.dynamodbmapper.DynamoDBTable;

import java.util.Date;

import io.realm.Realm;
import io.realm.RealmObject;


@DynamoDBTable(tableName = "ActionHistory")
public class ActionHistory extends RealmObject {
    public String mActionHistoryID;
    public String mCreated;
    public String mBabbleID;
    public String mActionTime;
    public String mActionMessage;
    public String mNotificationID;
    public Notification mNotification;

    public ActionHistory(String actionMessage,Notification notification,String babbleID){
        if (notification!= null){
            mNotificationID = getNotificationID(notification);
        }else {
            mNotificationID = "none";
        }
        mNotification = notification;
        String date = new Date().toString();
        mCreated = date;
        mActionTime = date;
        mActionHistoryID = date;
        mActionMessage = actionMessage;
        mBabbleID = babbleID;


    }

    public ActionHistory(){

    }

    @DynamoDBHashKey(attributeName = "ActionHistoryID")
    public String getActionHistoryID() {
        return mActionHistoryID;
    }

    public void setActionHistoryID(String actionHistoryID) {
        mActionHistoryID = actionHistoryID;
    }

    @DynamoDBAttribute(attributeName = "Created")
    public String getCreated() {
        return mCreated;
    }

    public void setCreated(String created) {
        mCreated = created;
    }

    @DynamoDBRangeKey(attributeName = "BabbleID")
    public String getBabbleID() {
        return mBabbleID;
    }

    public void setBabbleID(String babbleID) {
        mBabbleID = babbleID;
    }

    @DynamoDBAttribute(attributeName = "ActionTime")
    public String getActionTime() {
        return mActionTime;
    }

    public void setActionTime(String actionTime) {
        mActionTime = actionTime;
    }

    @DynamoDBAttribute(attributeName = "ActionMessage")
    public String getActionMessage() {
        return mActionMessage;
    }

    public void setActionMessage(String actionMessage) {
        mActionMessage = actionMessage;
    }

    @DynamoDBAttribute(attributeName = "NotificationID")
    public String getNotificationID() {
        return mNotificationID;
    }

    public void setNotificationID(String notificationID) {
        mNotificationID = notificationID;
    }

    public void saveAction(String actionType,Notification notification){
        Realm realm = Realm.getDefaultInstance();
        realm.beginTransaction();
        realm.copyToRealm(this);
        realm.commitTransaction();
    }

    public String getNotificationID(Notification notification){
        return notification.getCreated() + "-"+notification.getTag();
    }



}
