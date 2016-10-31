package com.leap.ryangunn.beginningtobabble;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import com.leap.ryangunn.beginningtobabble.settings.SettingOptionActivity;

public class HomeActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        ImageView libararyImageView = (ImageView) findViewById(R.id.homeActivityLibraryImageView);
        ImageView settignsImageView = (ImageView)findViewById(R.id.homeActivitySettings);
        ImageView playToday = (ImageView)findViewById(R.id.homeActivityPlayTodayImageView);
        libararyImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent detailIntent = new Intent(HomeActivity.this,DetailActivity.class);
                detailIntent.putExtra(DetailActivity.DETAIL_INTENT,DetailActivity.LIBRARY);
                Utility.addCustomEvent(Constant.VIEWED_LIBRARY);
                startActivity(detailIntent);
            }
        });

        settignsImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent settignOptionIntent = new Intent(HomeActivity.this,SettingOptionActivity.class);
                Utility.addCustomEvent(Constant.VIEWED_BY_SETTINGS);
                startActivity(settignOptionIntent);

            }
        });

        playToday.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent detailIntent = new Intent(HomeActivity.this,DetailActivity.class);
                detailIntent.putExtra(DetailActivity.DETAIL_INTENT,DetailActivity.PLAY_TODAY);
                Utility.addCustomEvent(Constant.VIEWED_PLAY_TODAY);
                startActivity(detailIntent);
            }
        });

    }
}
