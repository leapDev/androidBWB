package com.learning.leap.bwb;

import android.app.Application;
import android.content.Context;
import androidx.multidex.MultiDex;
import androidx.multidex.MultiDexApplication;

import com.crashlytics.android.Crashlytics;
import com.crashlytics.android.answers.Answers;
import com.evernote.android.job.JobManager;


import java.util.Set;

import io.fabric.sdk.android.Fabric;
import io.realm.Realm;
import io.realm.RealmConfiguration;
import io.realm.RealmMigration;
import io.realm.RealmSchema;


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
    };

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        MultiDex.install(this);
    }
}
