package com.learning.leap.bwb.download;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.learning.leap.bwb.baseActivity.HomeActivity;
import com.learning.leap.bwb.R;
import com.learning.leap.bwb.helper.ScheduleBucket;
import com.learning.leap.bwb.utility.Constant;
import com.learning.leap.bwb.utility.Utility;


public class DownloadFragment extends Fragment implements DownloadViewInterface {
    ProgressBar mProgressBar;
    TextView mDownloadPercentTextView;
     DownloadPresenter downloadPresenter;


    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        downloadPresenter = new DownloadPresenter(getActivity(),this);
    }

    @Override
    public void onResume() {
        super.onResume();
        downloadPresenter.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
        downloadPresenter.onPaused();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
      downloadPresenter.onDestory();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup parent, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.download_fragment, parent, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        setUpProgressBar(view);
       downloadPresenter.onCreate();

    }

    private void setUpProgressBar(View view) {
        mProgressBar = (ProgressBar)view.findViewById(R.id.downloadProgressBar);
        mProgressBar.setMax(100);
        mProgressBar.setIndeterminate(false);
        mDownloadPercentTextView = (TextView)view.findViewById(R.id.downloadTextPercentage);
    }


    @Override
    public void displayErrorMessage() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle(getString(R.string.BabbleError));
        builder.setMessage(getString(R.string.downloadError));
        builder.setNeutralButton(R.string.okay,(dialogInterface, i) -> returnToPreviousActivity(dialogInterface));
        builder.show();
    }

    private void returnToPreviousActivity(DialogInterface dialogInterface){
        dialogInterface.dismiss();
        getActivity();
    }

    @Override
    public void updateProgressBar(int progress) {
        mProgressBar.setProgress(progress);
        mDownloadPercentTextView.setText(Integer.toString(progress)+"%");
    }

    @Override
    public void downloadCompleted() {
        Utility.writeIntSharedPreferences(Constant.START_TIME,8,getActivity());
        Utility.writeIntSharedPreferences(Constant.END_TIME,16,getActivity());
        Utility.writeBoolenSharedPreferences(Constant.DID_DOWNLOAD,true,getActivity());
        Utility.writeBoolenSharedPreferences(Constant.UPDATE,true,getActivity());
        Utility.writeBoolenSharedPreferences(Constant.SEND_TIPS_TODAY,true,getActivity());
        ScheduleBucket scheduleBucket = new ScheduleBucket(getActivity());
        scheduleBucket.scheduleForFirstTime();
        Intent homeIntent = new Intent(getActivity(),HomeActivity.class);
        startActivity(homeIntent);
    }
}
