package com.learning.leap.bwb.download;

import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.learning.leap.bwb.DownloadService;
import com.learning.leap.bwb.R;
import com.learning.leap.bwb.baseActivity.HomeActivity;
import com.learning.leap.bwb.utility.Constant;
import com.learning.leap.bwb.utility.Utility;

public class DownloadActivity extends AppCompatActivity implements DownloadViewInterface {
    DownloadService downloadService;
    ProgressBar mProgressBar;
    TextView mDownloadPercentTextView;
    boolean bound = false;

    private ServiceConnection mConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            DownloadService.LocalBinder binder = (DownloadService.LocalBinder)service;
            downloadService = binder.getServiceInstance();
            downloadService.registerClient(DownloadActivity.this);
            bound = true;

        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            bound = false;
        }
    };

    private void startDownloadService() {
        Intent downloadServiceIntent = new Intent(DownloadActivity.this,DownloadService.class);
        downloadServiceIntent.putExtra(Constant.UPDATE,getIntent().getBooleanExtra(Constant.UPDATE,false));
        startService(downloadServiceIntent);
        bindService(downloadServiceIntent,mConnection, Context.BIND_AUTO_CREATE);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.download_fragment);
        setUpProgressBar();
        //startDownloadService();
    }

    @Override
    protected void onStop() {
        if (bound){
            unbindService(mConnection);
            bound = false;
        }
        super.onStop();
    }

    @Override
    public void onResume() {
            if (Utility.readBoolSharedPreferences(Constant.DID_DOWNLOAD,this)) {
                Utility.homeIntent(this);
            }else {
                if (!bound){
                startDownloadService();
                }
        }
        super.onResume();

    }

    @Override
    public void onPause() {
        super.onPause();

    }

    @Override
    public void onDestroy() {
        super.onDestroy();

    }

    private void setUpProgressBar() {
        mProgressBar = (ProgressBar)findViewById(R.id.downloadProgressBar);
        mProgressBar.setMax(100);
        mProgressBar.setIndeterminate(false);
        mDownloadPercentTextView = (TextView)findViewById(R.id.downloadTextPercentage);
    }

    @Override
    public void displayErrorMessage() {
        if (bound) {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle(getString(R.string.BabbleError));
            builder.setMessage(getString(R.string.downloadError));
            builder.setNegativeButton("Close", (dialog, which) -> DownloadActivity.this.finish());
            builder.setPositiveButton("Retry", (dialogInterface, i) -> startDownloadService());
            builder.show();
        }
    }

    @Override
    public void updateProgressBar(int progress) {
        if (bound) {
            mProgressBar.setProgress(progress);
            mDownloadPercentTextView.setText(Integer.toString(progress) + "%");
        }
    }

    @Override
    public void downloadCompleted() {
        if (bound) {
            Intent homeIntent = new Intent(this, HomeActivity.class);
            homeIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(homeIntent);
        }
    }

}
