package com.learning.leap.bwb.baseActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.work.OneTimeWorkRequest;
import androidx.work.WorkManager;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.evernote.android.job.JobManager;
import com.learning.leap.bwb.ActionHistoryIntentService;
import com.learning.leap.bwb.BuildConfig;
import com.learning.leap.bwb.DailyWorker;
import com.learning.leap.bwb.PlayTodayJob;
import com.learning.leap.bwb.download.DownloadActivity;
import com.learning.leap.bwb.library.LibraryCategoryActivity;
import com.learning.leap.bwb.library.PlayTodayActivity;
import com.learning.leap.bwb.model.BabbleUser;
import com.learning.leap.bwb.models.BabblePlayer;
import com.learning.leap.bwb.tipReminder.TipReminder;
import com.learning.leap.bwb.utility.Constant;
import com.learning.leap.bwb.R;
import com.learning.leap.bwb.utility.Utility;
import com.learning.leap.bwb.settings.SettingOptionActivity;

import java.io.File;
import java.util.Calendar;
import java.util.concurrent.TimeUnit;

import io.reactivex.disposables.Disposable;


public class HomeActivity extends AppCompatActivity  {
    Disposable updateDisposable;
    private int bucketNumber = 4;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }

        int tipPerReminder = Utility.readIntSharedPreferences(Constant.TIPS_PER_DAY,this);
        if (Utility.readBoolSharedPreferences(Constant.TIP_ONE_ON,this)){
            TipReminder tipReminder = new TipReminder(1,tipPerReminder,this);
            tipReminder.setAlarmForTip(Utility.readIntSharedPreferences(Constant.START_TIME,this));
        }

        setUpBackground();
        ImageView libararyImageView = findViewById(R.id.homeActivityLibraryImageView);
        ImageView settignsImageView = findViewById(R.id.homeActivitySettings);
        ImageView playToday = findViewById(R.id.homeActivityPlayTodayImageView);
        ImageView leapLogo = findViewById(R.id.homeLeapLogo);
        TextView poweredByTextView = findViewById(R.id.powerByTextView);
        libararyImageView.setOnClickListener(view -> detailIntent());
        settignsImageView.setOnClickListener(view -> settingsIntent());
        playToday.setOnClickListener(view -> playTodayIntent());
        leapLogo.setOnClickListener(view -> openWebsite());
        Utility.addCustomEvent(Constant.ACCESSED_APP,Utility.getUserID(this),null);
        poweredByTextView.setOnClickListener(view -> openWebsite());
        if (Utility.isNetworkAvailable(this)){
            ActionHistoryIntentService.startActionHistoryIntent(this);
        }
        updateCheck();

    }


    private void updateCheck() {
//        if (BabbleUser.Companion.homeScreenAgeCheck(this) != Age){
//            AlertDialog.Builder builder = new AlertDialog.Builder(this);
//            builder.setTitle(Constant.UPDATE);
//            builder.setMessage(getString(R.string.babble_update));
//            builder.setNegativeButton("Later", (dialog, which) -> dialog.dismiss());
//            builder.setPositiveButton("Update", (dialog, which) -> {
//                downloadIntent();
//                dialog.dismiss();
//            });
//            builder.show();
//        }
    }

    private void downloadIntent(){
        runOnUiThread(() -> {
            Intent updateIntent = new Intent(HomeActivity.this, DownloadActivity.class);
            updateIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            updateIntent.putExtra(Constant.UPDATE,true);
            startActivity(updateIntent);
        });

    }

    @Override
    protected void onDestroy() {
        if (updateDisposable != null && !updateDisposable.isDisposed()) {
            updateDisposable.dispose();
        }
        super.onDestroy();
    }


    private void setUpBackground(){
        ImageView background = (ImageView)findViewById(R.id.homeBackground);
        Bitmap backgroundBitmap = Utility.decodeSampledBitmapFromResource(getResources(),R.drawable.splash_home_bg,Utility.getDisplayMetrics(this));
        background.setImageBitmap(backgroundBitmap);
    }


    private void playTodayIntent(){
        Intent detailIntent = new Intent(HomeActivity.this, PlayTodayActivity.class);
        Utility.addCustomEvent(Constant.VIEWED_PLAY_TODAY,Utility.getUserID(this),null );
        startActivity(detailIntent);
    }

    private void settingsIntent(){
        Intent settingOptionIntent = new Intent(HomeActivity.this,SettingOptionActivity.class);
        Utility.addCustomEvent(Constant.VIEWED_BY_SETTINGS,Utility.getUserID(this),null);
        startActivity(settingOptionIntent);
    }
    private void detailIntent() {
        Intent detailIntent = new Intent(HomeActivity.this, LibraryCategoryActivity.class);
        //Utility.addCustomEvent(Constant.VIEWED_LIBRARY,Utility.getUserID(this),null);
        startActivity(detailIntent);
    }

    private void openWebsite(){
        Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("http://leapempowers.org/"));
        if (Utility.isNetworkAvailable(this)) {
            startActivity(browserIntent);
        }
    }


}
