package com.example.ryangunn.beginningtobabble.models;

import android.content.Context;
import android.os.AsyncTask;

import com.amazonaws.mobileconnectors.dynamodbv2.dynamodbmapper.DynamoDBMapper;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClient;
import com.example.ryangunn.beginningtobabble.Utility;

import java.lang.ref.WeakReference;

import io.realm.Realm;
import io.realm.RealmQuery;
import io.realm.RealmResults;

/**
 * Created by ryangunn on 9/21/16.
 */

public class UploadAsyncTask extends AsyncTask<Void,Void,Boolean> {
    Context mContext;

    public UploadAsyncTask(Context context){
        mContext = context.getApplicationContext();
    }

    @Override
    protected Boolean doInBackground(Void... voids) {
        try {
            Realm realm = Realm.getDefaultInstance();
            RealmQuery<ActionHistory> query = realm.where(ActionHistory.class);
            RealmResults<ActionHistory> result = query.findAll();
            AmazonDynamoDBClient ddbClient = new AmazonDynamoDBClient(Utility.getCredientail(mContext));
            DynamoDBMapper mapper = new DynamoDBMapper(ddbClient);
            for (ActionHistory actionHistory : result) {
                mapper.save(actionHistory);
            }
            result.deleteAllFromRealm();
            // add upload object
        }catch (Exception e){
            e.printStackTrace();
            return false;
        }
        return true;
    }

    @Override
    protected void onPostExecute(Boolean aBoolean) {
        super.onPostExecute(aBoolean);
    }
}
