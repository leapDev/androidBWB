package com.example.ryangunn.beginningtobabble;

import android.content.Intent;
import android.os.Handler;
import android.os.Looper;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.example.ryangunn.beginningtobabble.settings.UserInfoActivity;

import io.realm.Realm;
import io.realm.RealmConfiguration;

public class SplashActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        RealmConfiguration realmConfig = new RealmConfiguration.Builder(this.getApplicationContext()).build();
        Realm.setDefaultConfiguration(realmConfig);
        Boolean didDownload = Utility.readBoolSharedPreferences(Constant.DID_DOWNLOAD,this);
        if (didDownload){
            Intent homeIntent = new Intent(this,HomeActivity.class);
            startActivity(homeIntent);


        }else {
            Handler handler = new Handler(Looper.getMainLooper());
            handler.postDelayed(new Runnable() {
                public void run() {
                    Intent userInfoIntent = new Intent(SplashActivity.this,UserInfoActivity.class);
                    userInfoIntent.putExtra("newUser",true);
                    startActivity(userInfoIntent);
                }
            }, 6000);
        }
    }
}
