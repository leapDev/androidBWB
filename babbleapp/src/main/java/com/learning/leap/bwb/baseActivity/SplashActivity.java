package com.learning.leap.bwb.baseActivity;

import android.content.Intent;
import android.os.Handler;
import android.os.Looper;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.crashlytics.android.Crashlytics;
import com.learning.leap.bwb.utility.Constant;
import com.learning.leap.bwb.R;
import com.learning.leap.bwb.utility.Utility;
import com.learning.leap.bwb.settings.UserInfoActivity;

import io.fabric.sdk.android.Fabric;

public class SplashActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.splash_activity);
        Boolean didDownload = Utility.readBoolSharedPreferences(Constant.DID_DOWNLOAD,this);
        if (didDownload){
          homeIntent();
        }else {
            userInfoIntent();
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
