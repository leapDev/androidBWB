package com.learning.leap.bwb.baseActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.icu.util.Calendar;
import android.net.Uri;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.learning.leap.bwb.ActionHistoryIntentService;
import com.learning.leap.bwb.BuildConfig;
import com.learning.leap.bwb.download.DownloadActivity;
import com.learning.leap.bwb.models.BabblePlayer;
import com.learning.leap.bwb.tipReminder.TipReminder;
import com.learning.leap.bwb.utility.Constant;
import com.learning.leap.bwb.R;
import com.learning.leap.bwb.utility.Utility;
import com.learning.leap.bwb.settings.SettingOptionActivity;

import java.util.Date;

import io.reactivex.disposables.Disposable;


public class HomeActivity extends AppCompatActivity  {
    Disposable updateDisposable;

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
        Utility.addCustomEvent(Constant.ACCESSED_APP,Utility.getUserID(this),null);
        if (BuildConfig.FLAVOR.equals("regular")) {
            poweredByTextView.setOnClickListener(view -> openWebsite());
        }else {
            leapLogo.setVisibility(View.GONE);
            poweredByTextView.setVisibility(View.GONE);
        }
        Utility.hideButtonCheck(libararyImageView,playToday);
//
        if (Utility.isNetworkAvailable(this)){
            ActionHistoryIntentService.startActionHistoryIntent(this);
        }



//        ImageView homeImageView = (ImageView) findViewById(R.id.homeFragmentHomeIcon);
//        homeImageView.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                java.util.Calendar calendar = java.util.Calendar.getInstance();
//                calendar.add(java.util.Calendar.MINUTE,1);
//                TipReminder tipReminder = new TipReminder(4,1,new Date(),calendar.getTime(),HomeActivity.this);
//                tipReminder.setReminder(calendar.getTime());
//
//            }
//        });

    }

    private boolean updateCheck() {
        if (BabblePlayer.homeScreenAgeCheck(this)){
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle(Constant.UPDATE);
            builder.setMessage(getString(R.string.babble_update));
            builder.setNegativeButton("Later", (dialog, which) -> dialog.dismiss());
            builder.setPositiveButton("Update", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    downloadIntent();
                    dialog.dismiss();
                }
            });
            builder.show();
        }
        return false;
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
        Utility.addCustomEvent(Constant.VIEWED_PLAY_TODAY,Utility.getUserID(this),null );
        startActivity(detailIntent);
    }

    private void settingsIntent(){
        Intent settingOptionIntent = new Intent(HomeActivity.this,SettingOptionActivity.class);
        Utility.addCustomEvent(Constant.VIEWED_BY_SETTINGS,Utility.getUserID(this),null);
        startActivity(settingOptionIntent);
    }
    private void detailIntent() {
        Intent detailIntent = new Intent(HomeActivity.this,DetailActivity.class);
        detailIntent.putExtra(DetailActivity.DETAIL_INTENT,DetailActivity.LIBRARY);
        Utility.addCustomEvent(Constant.VIEWED_LIBRARY,Utility.getUserID(this),null);
        startActivity(detailIntent);
    }

    private void openWebsite(){
        Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("http://leapempowers.org/"));
        if (Utility.isNetworkAvailable(this)) {
            startActivity(browserIntent);
        }
    }


}
