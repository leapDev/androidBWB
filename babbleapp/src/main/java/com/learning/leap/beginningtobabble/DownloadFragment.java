package com.learning.leap.beginningtobabble;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.amazonaws.mobileconnectors.s3.transferutility.TransferListener;
import com.amazonaws.mobileconnectors.s3.transferutility.TransferObserver;
import com.amazonaws.mobileconnectors.s3.transferutility.TransferState;
import com.amazonaws.mobileconnectors.s3.transferutility.TransferType;
import com.amazonaws.mobileconnectors.s3.transferutility.TransferUtility;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3Client;
import com.learning.leap.beginningtobabble.models.Notification;
import com.learning.leap.beginningtobabble.models.TipReminder;

import java.io.File;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

import io.realm.Realm;
import io.realm.RealmChangeListener;
import io.realm.RealmQuery;
import io.realm.RealmResults;


public class DownloadFragment extends Fragment {
    ProgressBar mProgressBar;
    TransferUtility mTransferUtility;
    int totalCount = 0;
    int filesdownloaded = 0;
    private final int requestGranted = 10;
    ArrayList<TransferObserver> mTransferObservers;
    ArrayList<String> mFilesWithError = new ArrayList<>();
    ArrayList<String> mFiles = new ArrayList<>();
    TextView mDownloadPercentTextView;
    RealmResults<Notification> mRealmNotifications;
    Boolean paused = false;
    Boolean downloadCompleted = false;
    AmazonS3 mAmazonS3;


    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mAmazonS3 = new AmazonS3Client(Utility.getCredientail(getActivity()));
        mTransferUtility = new TransferUtility(mAmazonS3, getActivity().getApplicationContext());
    }

    @Override
    public void onResume() {
        super.onResume();
        if (paused) {
            downloadFiles(mFiles.get(filesdownloaded));
            paused = false;
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        paused = true;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (!downloadCompleted) {
            Realm realm = Realm.getDefaultInstance();
            realm.where(Notification.class).findAll().deleteAllFromRealm();
        }
    }

    //
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup parent, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.download_fragment, parent, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        mProgressBar = (ProgressBar)view.findViewById(R.id.downloadProgressBar);
        mProgressBar.setMax(100);
        mProgressBar.setIndeterminate(false);
        mDownloadPercentTextView = (TextView)view.findViewById(R.id.downloadTextPercentage);
        findPrompts();

    }

    private void findPrompts(){
        Realm realm = Realm.getDefaultInstance();
        RealmQuery<Notification> query = realm.where(Notification.class);
        mRealmNotifications = query.findAllAsync();
        mRealmNotifications.addChangeListener(callback);
    }

    private  RealmChangeListener<RealmResults<Notification>> callback = new RealmChangeListener<RealmResults<Notification>>(){
        @Override
        public void onChange(RealmResults<Notification> element) {
            getFiles(element);
            totalCount = mFiles.size();
            if (totalCount != 0 && !mFiles.isEmpty()) {
               downloadFiles(mFiles.get(0));
            }

        }
    };

    public void getFiles(RealmResults<Notification> notifications){
        for (Notification notification:notifications){
            if (!notification.getSoundFileName().equals("no file")){
                addFileToArray(notification.getCreated(),notification.getSoundFileName());
            }

            if (!notification.getVideoFileName().equals("no file")){
                addFileToArray(notification.getCreated(),notification.getVideoFileName());
            }
        }

    }

    public void addFileToArray(String dateCreated,String name){
        String fileName = dateCreated + "-"+ name;
        mFiles.add(fileName);
    }


    public void downloadFiles(final String fileName){
       // totalCount = filesNames.size();

        String bucketName = "leapbtob";
            File file = new File(getActivity().getFilesDir(), fileName);
            TransferObserver observer = mTransferUtility.download(
                    bucketName,     /* The bucket to download from */
                    fileName,    /* The key for the object to download */
                    file        /* The file to download the object to */
            );
            observer.setTransferListener(new TransferListener() {
                @Override
                public void onStateChanged(int id, TransferState state) {
                    if (state == TransferState.COMPLETED){
                        filesdownloaded++;
                        updateProgressBar();
                        if (filesdownloaded != totalCount-1 ) {
                                downloadFiles(mFiles.get(filesdownloaded));

                        }else {
                            downloadCompleted = true;
                            downloadCompleted();
                        }
                    }
                }

                @Override
                public void onProgressChanged(int id, long bytesCurrent, long bytesTotal) {

                }

                @Override
                public void onError(int id, Exception ex) {
                    mFilesWithError.add(fileName);
                    ex.printStackTrace();
                    Log.d("did", "onError: " + fileName +" did not download");
                    //Utility.displayAlertMessage(R.string.BabbleError,R.string.downloadError,getActivity());
                }
            });
    }

    public void downloadCompleted(){
        Utility.writeIntSharedPreferences(Constant.START_TIME,8,getActivity());
        Utility.writeIntSharedPreferences(Constant.END_TIME,16,getActivity());
        Intent homeIntent = new Intent(getActivity(),HomeActivity.class);
        homeIntent.putExtra("errorDownloads",mFilesWithError);
        TipReminder.setUpRepeatingIntentService(getContext().getApplicationContext());
        Utility.writeBoolenSharedPreferences(Constant.DID_DOWNLOAD,true,getActivity());
        Intent notificationReciver = new Intent(getActivity().getApplicationContext(), SetNotificationIntentService.class);
        getActivity().startService(notificationReciver);
        startActivity(homeIntent);
    }

    public void downloadProgress(String fileName){
//        File file = new File(Environment.getExternalStorageDirectory(), fileName);
//        List<TransferObserver> transferObserverList = mTransferUtility.getTransfersWithType(TransferType.DOWNLOAD);

    }

    public void updateProgressBar(){
        double progress = (double) filesdownloaded/(double) totalCount;
        int progressBar =(int) (progress * 100);
        mProgressBar.setProgress(progressBar);

        mDownloadPercentTextView.setText(Integer.toString(progressBar)+"%");
    }

    public static class GetPrompts extends AsyncTask<Void,Void,Boolean>{
        WeakReference<DownloadFragment> mDownloadFragment;
        Context mContext;
        ArrayList<Notification> mNotifications = new ArrayList<>();
        ArrayList<String> mFileNames = new ArrayList<>();

        public GetPrompts(DownloadFragment downloadFragment){
            mDownloadFragment = new WeakReference<DownloadFragment>(downloadFragment);
            mContext = mDownloadFragment.get().getActivity();
        }

        @Override
        protected Boolean doInBackground(Void... voids) {
            try {
                Realm realm = Realm.getDefaultInstance();
                RealmQuery<Notification> query = realm.where(Notification.class);
                RealmResults<Notification> result = query.findAll();
                mNotifications.addAll(result);
               getFiles(mNotifications);
            }catch (Exception e){
                return false;
            }

            return true;
        }

        @Override
        protected void onPostExecute(Boolean aBoolean) {
            super.onPostExecute(aBoolean);
            if (aBoolean){
                if (mDownloadFragment.get() != null){

                    mDownloadFragment.get().mFiles = mFileNames;
                    mDownloadFragment.get().totalCount = mFileNames.size();
                    mDownloadFragment.get().downloadFiles(mFileNames.get(0));
                }
            }else {
                Utility.displayAlertMessage(R.string.BabbleError,R.string.downloadError,mContext);
            }
        }

        public void getFiles(ArrayList<Notification> notifications){
            for (Notification notification:notifications){
                if (!notification.getSoundFileName().equals("no file")){
                    addFileToArray(notification.getCreated(),notification.getSoundFileName());
                }

                if (!notification.getVideoFileName().equals("no file")){
                    addFileToArray(notification.getCreated(),notification.getVideoFileName());
                }
            }

        }

        public void addFileToArray(String dateCreated,String name){
            String fileName = dateCreated + "-"+ name;
             mFileNames.add(fileName);
        }
    }

    public int getNextKey()
    {
        Realm realm = Realm.getDefaultInstance();
        return realm.where(Notification.class).max("id").intValue() + 1;
    }
}
