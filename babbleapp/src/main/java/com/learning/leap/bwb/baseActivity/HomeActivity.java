package com.learning.leap.bwb.baseActivity;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import com.learning.leap.bwb.models.BabblePlayer;
import com.learning.leap.bwb.tipReminder.TipReminder;
import com.learning.leap.bwb.utility.Constant;
import com.learning.leap.bwb.R;
import com.learning.leap.bwb.utility.Utility;
import com.learning.leap.bwb.settings.SettingOptionActivity;

import java.util.Calendar;
import java.util.Date;

public class HomeActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        ImageView libararyImageView = (ImageView) findViewById(R.id.homeActivityLibraryImageView);
        ImageView settignsImageView = (ImageView)findViewById(R.id.homeActivitySettings);
        ImageView playToday = (ImageView)findViewById(R.id.homeActivityPlayTodayImageView);
        libararyImageView.setOnClickListener(view -> detailIntent());
        settignsImageView.setOnClickListener(view -> settingsIntent());
        playToday.setOnClickListener(view -> playTodayIntent());


        if (BabblePlayer.homeScreenAgeCheck(this)){
            //display Updated Dialog
            //set updated shared Pref to false
        }

//                ImageView homeImageView = (ImageView) findViewById(R.id.homeFragmentHomeIcon);
//        homeImageView.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                Calendar calendar = Calendar.getInstance();
//                calendar.add(Calendar.MINUTE,2);
//                TipReminder tipReminder = new TipReminder(4,1,new Date(),calendar.getTime(),HomeActivity.this);
//                tipReminder.setReminder(calendar.getTime());
//
//            }
//        });

    }

    private void playTodayIntent(){
        Intent detailIntent = new Intent(HomeActivity.this,DetailActivity.class);
        detailIntent.putExtra(DetailActivity.DETAIL_INTENT,DetailActivity.PLAY_TODAY);
        Utility.addCustomEvent(Constant.VIEWED_PLAY_TODAY);
        startActivity(detailIntent);
    }

    private void settingsIntent(){
        Intent settingOptionIntent = new Intent(HomeActivity.this,SettingOptionActivity.class);
        Utility.addCustomEvent(Constant.VIEWED_BY_SETTINGS);
        startActivity(settingOptionIntent);
    }
    private void detailIntent() {
        Intent detailIntent = new Intent(HomeActivity.this,DetailActivity.class);
        detailIntent.putExtra(DetailActivity.DETAIL_INTENT,DetailActivity.LIBRARY);
        Utility.addCustomEvent(Constant.VIEWED_LIBRARY);
        startActivity(detailIntent);
    }
}
