package com.learning.leap.bwb.baseActivity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.learning.leap.bwb.models.BabblePlayer;
import com.learning.leap.bwb.tipReminder.TipReminder;
import com.learning.leap.bwb.userInfo.UserInfoViewInterface;
import com.learning.leap.bwb.utility.Constant;
import com.learning.leap.bwb.R;
import com.learning.leap.bwb.utility.Utility;
import com.learning.leap.bwb.settings.SettingOptionActivity;

import java.util.Calendar;
import java.util.Date;

import io.realm.Realm;


public class HomeActivity extends AppCompatActivity  {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        setUpBackground();
        ImageView libararyImageView = (ImageView) findViewById(R.id.homeActivityLibraryImageView);
        ImageView settignsImageView = (ImageView)findViewById(R.id.homeActivitySettings);
        ImageView playToday = (ImageView)findViewById(R.id.homeActivityPlayTodayImageView);
        ImageView leapLogo = (ImageView)findViewById(R.id.homeLeapLogo);
        TextView poweredByTextView = (TextView)findViewById(R.id.powerByTextView);
        libararyImageView.setOnClickListener(view -> detailIntent());
        settignsImageView.setOnClickListener(view -> settingsIntent());
        playToday.setOnClickListener(view -> playTodayIntent());
        leapLogo.setOnClickListener(view -> openWebsite());
        poweredByTextView.setOnClickListener(view -> openWebsite());
        Utility.addCustomEvent(Constant.ACCESSED_APP,Utility.getUserID(this));

//        if (BabblePlayer.homeScreenAgeCheck(this)){
//            //display Updated Dialog
//            //set updated shared Pref to false
//        }

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

    @Override
    protected void onDestroy() {

        super.onDestroy();
    }

    private void displayUpdateDialog(){
//        new AlertDialog.Builder(this)
//                .setMessage(R.string.updateDialogTitle)
//                .setPositiveButton(R.string.update,(dialogInterface, i) -> {dialogInterface.dismiss();downloadIntent();})
//                .setNegativeButton(R.string.remindMeLater,(dialogInterface, i) -> dialogInterface.dismiss())
//                .setIcon(android.R.drawable.ic_dialog_alert)
//                .show();
    }

    private void setUpBackground(){
        ImageView background = (ImageView)findViewById(R.id.homeBackground);
        Bitmap backgroundBitmap = Utility.decodeSampledBitmapFromResource(getResources(),R.drawable.splash_home_bg,Utility.getDisplayMetrics(this));
        background.setImageBitmap(backgroundBitmap);
    }


    private void playTodayIntent(){
        Intent detailIntent = new Intent(HomeActivity.this,DetailActivity.class);
        detailIntent.putExtra(DetailActivity.DETAIL_INTENT,DetailActivity.PLAY_TODAY);
        Utility.addCustomEvent(Constant.VIEWED_PLAY_TODAY,Utility.getUserID(this));
        startActivity(detailIntent);
    }

    private void settingsIntent(){
        Intent settingOptionIntent = new Intent(HomeActivity.this,SettingOptionActivity.class);
        Utility.addCustomEvent(Constant.VIEWED_BY_SETTINGS,Utility.getUserID(this));
        startActivity(settingOptionIntent);
    }
    private void detailIntent() {
        Intent detailIntent = new Intent(HomeActivity.this,DetailActivity.class);
        detailIntent.putExtra(DetailActivity.DETAIL_INTENT,DetailActivity.LIBRARY);
        Utility.addCustomEvent(Constant.VIEWED_LIBRARY,Utility.getUserID(this));
        startActivity(detailIntent);
    }

    private void openWebsite(){
        Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("http://leapempowers.org/"));
        if (Utility.isNetworkAvailable(this)) {
            startActivity(browserIntent);
        }
    }


}
