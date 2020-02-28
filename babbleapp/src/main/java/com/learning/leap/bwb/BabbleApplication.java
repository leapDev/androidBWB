package com.learning.leap.bwb;

import android.app.Application;
import android.content.Context;
import androidx.multidex.MultiDex;
import com.evernote.android.job.JobManager;
import com.learning.leap.bwb.di.AppComponent;
import com.learning.leap.bwb.di.DaggerAppComponent;
import java.util.Set;
import io.realm.Realm;
import io.realm.RealmConfiguration;
import io.realm.RealmMigration;
import io.realm.RealmSchema;
import io.sentry.Sentry;
import io.sentry.android.AndroidSentryClientFactory;


public class BabbleApplication extends Application{

    public AppComponent appComponent;

    @Override
    public void onCreate() {
        super.onCreate();
        Sentry.init("https://304314b3ecda47fe9439012d7bfbcd61@sentry.io/2888418", new AndroidSentryClientFactory(this));
        Realm.init(this);
        RealmConfiguration config = new RealmConfiguration.Builder()
                .schemaVersion(1)
                .migration(migration)
                .build();
        Realm.setDefaultConfiguration(config);
        JobManager.create(this).addJobCreator(new PlayTodayJobCreator());
        appComponent = DaggerAppComponent.factory().create(this);
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
