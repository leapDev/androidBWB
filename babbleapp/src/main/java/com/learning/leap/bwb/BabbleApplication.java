package com.learning.leap.bwb;

import android.app.Application;
import android.content.Context;
import android.support.multidex.MultiDex;
import android.support.multidex.MultiDexApplication;

import com.crashlytics.android.Crashlytics;
import com.crashlytics.android.answers.Answers;
import com.evernote.android.job.JobManager;
import com.learning.leap.bwb.models.Notification;


import java.util.Date;
import java.util.Set;

import io.fabric.sdk.android.Fabric;
import io.realm.DynamicRealm;
import io.realm.FieldAttribute;
import io.realm.Realm;
import io.realm.RealmConfiguration;
import io.realm.RealmMigration;
import io.realm.RealmSchema;
import io.realm.annotations.PrimaryKey;


public class BabbleApplication extends Application{

    @Override
    public void onCreate() {
        super.onCreate();
        Fabric.with(this, new Crashlytics());
        Fabric.with(this, new Answers());
        Realm.init(this);
        RealmConfiguration config = new RealmConfiguration.Builder()
                .schemaVersion(1)
                .migration(migration)
                .build();
        Realm.setDefaultConfiguration(config);
        JobManager.create(this).addJobCreator(new PlayTodayJobCreator());
    }

    RealmMigration migration = (realm, oldVersion, newVersion) -> {;
        RealmSchema schema = realm.getSchema();
        if (oldVersion == 0){
             Set<String> fields = schema.get("BabblePlayer").getFieldNames();
            String babyGender = "babyGender";
            for (String field:fields){
                if (field.equals(babyGender)){
                    oldVersion++;
                    return;
                }
            }
            schema.get("BabblePlayer").addField("babyGender",String.class);
            oldVersion++;
        }

        if (oldVersion == 1){
            if (schema.get("ResearchNotifications") == null) {
                schema.create("ResearchNotifications")
                        .addField("mCreated", String.class)
                        .addField("mTag", String.class)
                        .addField("mAgeRange", String.class)
                        .addField("mDeleted", String.class)
                        .addField("mEndMonth", String.class)
                        .addField("mMessage", String.class)
                        .addField("mSoundFileName", String.class)
                        .addField("mStartMonth", String.class)
                        .addField("mVideoFileName", String.class)
                        .addField("mPlayToday", Boolean.class)
                        .addField("mFavorite", Boolean.class)
                        .addField("id",int.class,FieldAttribute.PRIMARY_KEY);
            }
            if (schema.get("ResearchActionHistory") == null) {
                schema.create("ResearchActionHistory")
                        .addField("mActionHistoryID", String.class)
                        .addField("mCreated", String.class)
                        .addField("mBabbleID", String.class)
                        .addField("mActionTime", String.class)
                        .addField("mActionMessage", String.class)
                        .addField("mNotificationID", String.class)
                        .addRealmObjectField("mNotification", schema.get("Notification"));
            }

            if (schema.get("ResearchPlayers") == null) {
                schema.create("ResearchPlayers")
                        .addField("mBabbleID", String.class, FieldAttribute.PRIMARY_KEY)
                        .addField("mBabyBirthday", String.class)
                        .addField("mBabyName", String.class)
                        .addField("mZipCode", int.class)
                        .addField("userAgeInMonth", int.class)
                        .addField("birthdayDate", Date.class)
                        .addField("babyGender", String.class);
            }
            oldVersion++;
        }
        if (oldVersion == 2){
            Set<String> fields = schema.get("ResearchActionHistory").getFieldNames();
            String notificaitonID = "mNotificationID";
            boolean addNotificationIDField = true;
            for (String field:fields){
                if (field.equals(notificaitonID)){
                    addNotificationIDField = false;
                }
                if (field.equals("mNotificatinID")){
                    schema.get("ResearchActionHistory").removeField("mNotificatinID");
                }
            }
            if (addNotificationIDField) {
                schema.get("ResearchActionHistory").addField(notificaitonID, String.class);
            }

            Set<String> notificationFields = schema.get("ResearchNotifications").getFieldNames();
            boolean addIDField = true;
            for (String field:notificationFields){
                if (field.equals("id")){
                   addIDField = false;
                }
            }

            if (addIDField){
                schema.get("ResearchNotifications").addField("id",int.class,FieldAttribute.PRIMARY_KEY);
            }
        }
    };

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        MultiDex.install(this);
    }
}
