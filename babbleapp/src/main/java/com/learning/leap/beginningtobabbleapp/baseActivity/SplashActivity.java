package com.learning.leap.beginningtobabbleapp.baseActivity;

import android.content.Intent;
import android.os.Handler;
import android.os.Looper;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.crashlytics.android.Crashlytics;
import com.learning.leap.beginningtobabbleapp.utility.Constant;
import com.learning.leap.beginningtobabbleapp.R;
import com.learning.leap.beginningtobabbleapp.utility.Utility;
import com.learning.leap.beginningtobabbleapp.settings.UserInfoActivity;

import io.fabric.sdk.android.Fabric;
import io.realm.Realm;
import io.realm.RealmConfiguration;

public class SplashActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.splash_activity);
        Boolean didDownload = Utility.readBoolSharedPreferences(Constant.DID_DOWNLOAD,this);
        if (didDownload){
          homeIntent();
        }else {
            Handler handler = new Handler(Looper.getMainLooper());
            handler.postDelayed(() -> userInfoIntent(),6000);
        }
    }

    private void userInfoIntent(){
        Intent userInfoIntent = new Intent(SplashActivity.this,UserInfoActivity.class);
        userInfoIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
        userInfoIntent.putExtra("newUser",true);
        startActivity(userInfoIntent);
    }

    private void homeIntent(){
        Intent homeIntent = new Intent(this,HomeActivity.class);
        homeIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
        startActivity(homeIntent);
    }
}
