package com.learning.leap.bwb.download;

import android.content.Context;

import com.amazonaws.mobileconnectors.s3.transferutility.TransferUtility;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3Client;
import com.learning.leap.bwb.utility.Constant;
import com.learning.leap.bwb.utility.Utility;
import com.learning.leap.bwb.helper.ScheduleBucket;
import com.learning.leap.bwb.models.AWSDownload;
import com.learning.leap.bwb.models.Notification;

import io.realm.Realm;
import rx.Subscription;



public class DownloadPresenter implements DownloadPresneterInterface {
    private Boolean paused = false;
    private DownloadViewInterface downloadViewInterface;
   private Context context;
   private AWSDownload awsDownload;
    private int filesDownloadAtPaused = 0;
  private Subscription realmNotificationSubscription;

    public DownloadPresenter(Context context, DownloadViewInterface downloadViewInterface){
        this.context = context;
        this.downloadViewInterface = downloadViewInterface;
    }

    @Override
    public void errorHasOccured() {
        downloadViewInterface.displayErrorMessage();
    }

    @Override
    public void downloadCompleted() {
        Utility.writeIntSharedPreferences(Constant.START_TIME,8,context);
        Utility.writeIntSharedPreferences(Constant.END_TIME,16,context);
        Utility.writeBoolenSharedPreferences(Constant.DID_DOWNLOAD,true,context);
        Utility.writeBoolenSharedPreferences(Constant.UPDATE,true,context);
        Utility.writeBoolenSharedPreferences(Constant.SEND_TIPS_TODAY,true,context);
        ScheduleBucket scheduleBucket = new ScheduleBucket(context);
        scheduleBucket.diviedTheBucketIntoThree();
        downloadViewInterface.downloadCompleted();
    }

    @Override
    public void updateProgress(int prgress) {
        downloadViewInterface.updateProgressBar(prgress);
    }

    @Override
    public void onCreate() {
        AmazonS3 mAmazonS3 = new AmazonS3Client(Utility.getCredientail(context));
        TransferUtility transferUtility = new TransferUtility(mAmazonS3, context.getApplicationContext());
        awsDownload = new AWSDownload(context,transferUtility,this);
        realmNotificationSubscription = new Notification().getNotificationFromRealm(Realm.getDefaultInstance())
                                        .doOnNext(notifications -> awsDownload.addNotificationsFilesToList(notifications))
                                        .doOnError(throwable -> downloadViewInterface.displayErrorMessage())
                                        .subscribe();
    }

    @Override
    public void onDestory() {
        if (realmNotificationSubscription != null && !realmNotificationSubscription.isUnsubscribed()){
            realmNotificationSubscription.unsubscribe();
        }
    }

    @Override
    public void onResume() {
        if (paused) {
            awsDownload.downloadFiles(filesDownloadAtPaused);
            paused = false;
        }
    }

    @Override
    public void onPaused() {
        paused = true;
        filesDownloadAtPaused(awsDownload.getFilesdownloaded());
    }

    private void filesDownloadAtPaused(int filesDownloaded){
        filesDownloadAtPaused = filesDownloaded;
    }
}
