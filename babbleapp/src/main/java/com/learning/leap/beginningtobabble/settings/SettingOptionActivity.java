package com.learning.leap.beginningtobabble.settings;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import com.learning.leap.beginningtobabble.DetailActivity;
import com.learning.leap.beginningtobabble.R;

public class SettingOptionActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting_option);
        Button tipSettings = (Button)findViewById(R.id.settingsFragmentTipSettingButton);
        Button profileSettings = (Button)findViewById(R.id.settingsFragmentProfileSettingsButton);

        profileSettings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent userSettingIntent = new Intent(SettingOptionActivity.this,UserInfoActivity.class);
                userSettingIntent.putExtra("newUser",false);
                startActivity(userSettingIntent);
            }
        });

        tipSettings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent tipSettingsIntent = new Intent(SettingOptionActivity.this,TipSettingsActivity.class);
                startActivity(tipSettingsIntent);
            }
        });

        ImageView homeImageView = (ImageView)findViewById(R.id.settingsFragmentHomeImageView);
        ImageView playToday = (ImageView)findViewById(R.id.settingsFragmentPlayTodayImageView);
        ImageView libraryImageView = (ImageView)findViewById(R.id.settingsFragmentLibararyImageView);
      ;

        libraryImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent detailIntent = new Intent(SettingOptionActivity.this,DetailActivity.class);
                detailIntent.putExtra(DetailActivity.DETAIL_INTENT,DetailActivity.LIBRARY);
                startActivity(detailIntent);
            }
        });

        playToday.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent detailIntent = new Intent(SettingOptionActivity.this,DetailActivity.class);
                detailIntent.putExtra(DetailActivity.DETAIL_INTENT,DetailActivity.PLAY_TODAY);
                startActivity(detailIntent);
            }
        });

        homeImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });


    }
}
