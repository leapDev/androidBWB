package com.learning.leap.bwb;

import android.app.IntentService;
import android.content.Context;
import android.content.Intent;
import android.support.annotation.Nullable;

import com.learning.leap.bwb.models.ActionHistory;
import com.learning.leap.bwb.research.ResearchActionHistory;
import com.learning.leap.bwb.utility.Constant;

import io.reactivex.disposables.CompositeDisposable;

/**
 * Created by ryanlgunn8 on 5/14/17.
 */

public class ActionHistoryIntentService extends IntentService {
    Context context;
    private final CompositeDisposable disposables = new CompositeDisposable();
    public ActionHistoryIntentService(){
        super("actionHistoryIntentService");
    }

    @Override
    public void onCreate() {
        super.onCreate();
        context = getApplicationContext();
    }

    @Override
    public void onDestroy() {
        disposables.clear();
        super.onDestroy();

    }

    @Override
    protected void onHandleIntent(@Nullable Intent intent) {
        if (!BuildConfig.FLAVOR.equals("regular")) {
            disposables.add(ResearchActionHistory.uploadActionHistory(context));
        }else {
            disposables.add(ActionHistory.uploadActionHistory(context));
        }
    }


    public static void startActionHistoryIntent(Context context){
        Intent intent = new Intent(context,ActionHistoryIntentService.class);
        context.startService(intent);
    }
}
