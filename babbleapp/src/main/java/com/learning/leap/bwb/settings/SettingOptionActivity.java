package com.learning.leap.bwb.settings;

import android.content.Intent;
import android.graphics.Bitmap;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import com.learning.leap.bwb.baseActivity.DetailActivity;
import com.learning.leap.bwb.R;
import com.learning.leap.bwb.utility.Utility;

public class SettingOptionActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setUpBackground();
        setContentView(R.layout.activity_setting_option);
        Button tipSettings = (Button)findViewById(R.id.settingsFragmentTipSettingButton);
        Button profileSettings = (Button)findViewById(R.id.settingsFragmentProfileSettingsButton);
        ImageView homeImageView = (ImageView)findViewById(R.id.settingsFragmentHomeImageView);
        ImageView playToday = (ImageView)findViewById(R.id.settingsFragmentPlayTodayImageView);
        ImageView libraryImageView = (ImageView)findViewById(R.id.settingsFragmentLibararyImageView);

        profileSettings.setOnClickListener(view -> userSettingsIntent());

        tipSettings.setOnClickListener(view -> tipsIntent());

        libraryImageView.setOnClickListener(view -> libraryIntent());

        playToday.setOnClickListener(view -> playTodayIntent());

        homeImageView.setOnClickListener(view -> homeIntent());

    }

    private void setUpBackground(){
        ImageView background = (ImageView)findViewById(R.id.tipSettingsBackground);
        Bitmap backgroundBitmap = Utility.decodeSampledBitmapFromResource(getResources(),R.drawable.settings_bg,Utility.getDisplayMetrics(this));
        background.setImageBitmap(backgroundBitmap);
    }

    private void userSettingsIntent(){
        Intent userSettingIntent = new Intent(SettingOptionActivity.this,UserInfoActivity.class);
        userSettingIntent.putExtra("newUser",false);
        startActivity(userSettingIntent);
    }

    private void tipsIntent(){
        Intent tipSettingsIntent = new Intent(SettingOptionActivity.this,TipSettingsActivity.class);
        startActivity(tipSettingsIntent);
    }

    private void libraryIntent(){
        Intent detailIntent = new Intent(SettingOptionActivity.this,DetailActivity.class);
        detailIntent.putExtra(DetailActivity.DETAIL_INTENT,DetailActivity.LIBRARY);
        startActivity(detailIntent);
    }

    private void playTodayIntent(){
        Intent detailIntent = new Intent(SettingOptionActivity.this,DetailActivity.class);
        detailIntent.putExtra(DetailActivity.DETAIL_INTENT,DetailActivity.PLAY_TODAY);
        startActivity(detailIntent);
    }

    private void homeIntent(){
        finish();
    }
}
