package com.example.ryangunn.beginningtobabble.models;

import java.util.Date;

import io.realm.RealmObject;

/**
 * Created by ryangunn on 9/17/16.
 */
public class LocalNotification extends RealmObject {
    public int mNotificationID;
    public Date mNotificationDate;
    public String mNotificationTitle;
    public String mNotificationMessage;
}
