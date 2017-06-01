package com.learning.leap.bwb;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.support.annotation.Nullable;

import com.amazonaws.mobileconnectors.dynamodbv2.dynamodbmapper.DynamoDBMapper;
import com.amazonaws.mobileconnectors.dynamodbv2.dynamodbmapper.PaginatedScanList;
import com.amazonaws.mobileconnectors.s3.transferutility.TransferUtility;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClient;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3Client;
import com.learning.leap.bwb.download.DownloadPresneterInterface;
import com.learning.leap.bwb.download.DownloadViewInterface;
import com.learning.leap.bwb.helper.LocalLoadSaveHelper;
import com.learning.leap.bwb.helper.ScheduleBucket;
import com.learning.leap.bwb.models.AWSDownload;
import com.learning.leap.bwb.models.BabblePlayer;
import com.learning.leap.bwb.models.Notification;
import com.learning.leap.bwb.research.ResearchNotifications;
import com.learning.leap.bwb.research.ResearchPlayers;
import com.learning.leap.bwb.utility.Constant;
import com.learning.leap.bwb.utility.Utility;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;
import io.realm.Realm;

public class DownloadService extends Service implements DownloadPresneterInterface {
    private final IBinder mBinder = new LocalBinder();
    private Boolean started= false;
    private DownloadViewInterface downloadViewInterface;
    private AWSDownload awsDownload;
    private Disposable realmNotificationSubscription;
    private final CompositeDisposable disposables = new CompositeDisposable();
    private boolean update = false;

    public class LocalBinder extends Binder {
        public DownloadService getServiceInstance(){
            return DownloadService.this;
        }
    }

    public void registerClient(Context context){
        downloadViewInterface = (DownloadViewInterface)context;
    }
    private boolean nullViewCheck(){
        return downloadViewInterface !=null;
    }

    @Override
    public void errorHasOccured() {
        if (nullViewCheck()) {
            downloadViewInterface.displayErrorMessage();
        }
        stopSelf();
    }

    @Override
    public void downloadCompleted() {
        if (nullViewCheck()) {
            downloadViewInterface.downloadCompleted();
        }
        onComplete();
        stopSelf();
    }

    @Override
    public void updateProgress(int prgress) {
        if (nullViewCheck()) {
            downloadViewInterface.updateProgressBar(prgress);
        }
    }

    private void startDownload(){
        if (!started){
            if (BuildConfig.FLAVOR.equals("control")){
                AmazonS3 mAmazonS3 = new AmazonS3Client(Utility.getCredientail(this));
                TransferUtility transferUtility = new TransferUtility(mAmazonS3, this.getApplicationContext());
                awsDownload = new AWSDownload(this, transferUtility, this);
                realmNotificationSubscription = new ResearchNotifications().getNotificationFromRealm(Realm.getDefaultInstance())
                        .subscribe(notifications -> awsDownload.addNotificationsFilesToList(notifications),
                                throwable -> errorHasOccured());
                int filesDownloadAtPaused = 0;
                awsDownload.downloadFiles(filesDownloadAtPaused);
                started = true;
                disposables.add(realmNotificationSubscription);
            }else {
                AmazonS3 mAmazonS3 = new AmazonS3Client(Utility.getCredientail(this));
                TransferUtility transferUtility = new TransferUtility(mAmazonS3, this.getApplicationContext());
                awsDownload = new AWSDownload(this, transferUtility, this);
                realmNotificationSubscription = new Notification().getNotificationFromRealm(Realm.getDefaultInstance())
                        .subscribe(notifications -> awsDownload.addNotificationsFilesToList(notifications),
                                throwable -> errorHasOccured());
                int filesDownloadAtPaused = 0;
                awsDownload.downloadFiles(filesDownloadAtPaused);
                started = true;
                disposables.add(realmNotificationSubscription);
            }
        }
    }


    private void updateNotifications(){

        if (BuildConfig.FLAVOR.equals("control")){
            ResearchPlayers players = new ResearchPlayers();
            LocalLoadSaveHelper localLoadSaveHelper = new LocalLoadSaveHelper(this);
            Utility.writeBoolenSharedPreferences(Constant.UPDATE,true,this);
            AmazonDynamoDBClient amazonDynamoDBClient = new AmazonDynamoDBClient(Utility.getCredientail(this));
            DynamoDBMapper mapper = new DynamoDBMapper(amazonDynamoDBClient);
            Disposable notificationDisposable = players.retriveNotifications(mapper)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(notifications -> {
                        saveNWNotifications(notifications);
                        startDownload();
                    }, throwable -> {
                        errorHasOccured();
                    });
            disposables.add(notificationDisposable);

        }else {
            BabblePlayer babblePlayer = new BabblePlayer();
            LocalLoadSaveHelper localLoadSaveHelper = new LocalLoadSaveHelper(this);
            Utility.writeBoolenSharedPreferences(Constant.UPDATE,true,this);
            babblePlayer = babblePlayer.loadBabblePlayerFronSharedPref(localLoadSaveHelper);
            AmazonDynamoDBClient amazonDynamoDBClient = new AmazonDynamoDBClient(Utility.getCredientail(this));
            DynamoDBMapper mapper = new DynamoDBMapper(amazonDynamoDBClient);
            Disposable notificationDisposable = babblePlayer.retriveNotifications(babblePlayer.getuserAgeInMonth(), mapper)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(notifications -> {
                        saveNotifications(notifications);
                        startDownload();
                    }, throwable -> {
                        errorHasOccured();
                    });
            disposables.add(notificationDisposable);
        }
    }

    private void saveNotifications(PaginatedScanList<Notification> notifications) {
        Realm realm = Realm.getDefaultInstance();
        realm.beginTransaction();
        realm.deleteAll();
        realm.copyToRealm(notifications);
        realm.commitTransaction();
    }

    private void saveNWNotifications(PaginatedScanList<ResearchNotifications> notifications) {
        Realm realm = Realm.getDefaultInstance();
        realm.beginTransaction();
        realm.deleteAll();
        realm.copyToRealm(notifications);
        realm.commitTransaction();
    }


    @Override
    public void onDestroy() {
        if (realmNotificationSubscription != null && !realmNotificationSubscription.isDisposed()){
            realmNotificationSubscription.dispose();
        }
        super.onDestroy();
    }

    @Override
    public int onStartCommand(Intent intent,int flags, int startId) {
        if (intent.getBooleanExtra(Constant.UPDATE,false)){
            updateNotifications();
            update = true;
        }else {
            startDownload();
            }
        return super.onStartCommand(intent, flags, startId);
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return mBinder;
    }

    private void onComplete(){
        Utility.writeIntSharedPreferences(Constant.START_TIME,8,this);
        Utility.writeIntSharedPreferences(Constant.END_TIME,16,this);
        Utility.writeBoolenSharedPreferences(Constant.DID_DOWNLOAD,true,this);
        Utility.writeBoolenSharedPreferences(Constant.UPDATE,false,this);
        Utility.writeBoolenSharedPreferences(Constant.SEND_TIPS_TODAY,true,this);
        ScheduleBucket scheduleBucket = new ScheduleBucket(this);
        scheduleBucket.scheduleForFirstTime();
        if (update){
            if (!BuildConfig.FLAVOR.equals("regular")){
                ResearchPlayers.saveUpdatedInfo(this);
            }else {
                BabblePlayer.saveUpdatedInfo(this);
            }
        }
    }
}
