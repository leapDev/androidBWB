package com.leap.ryangunn.beginningtobabble;

import android.content.Intent;
import android.media.MediaPlayer;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.leap.ryangunn.beginningtobabble.helper.AnswerNotification;
import com.leap.ryangunn.beginningtobabble.models.Notification;
import com.leap.ryangunn.beginningtobabble.models.NotificationFunctions;

import java.util.ArrayList;
import java.util.Date;
import java.util.Random;

import io.realm.Realm;
import io.realm.RealmChangeListener;
import io.realm.RealmResults;

public class VoteActivity extends AppCompatActivity implements MediaPlayer.OnPreparedListener {
    RealmResults<Notification> mNotifications;
    ArrayList<Notification> mNoficaitonArray = new ArrayList<>();
    ImageView mPlaySoundImageView;
    ImageView mPlayVideoImageView;
    ImageView mVoteThumbUpImageView;
    ImageView mVoteThumbDownImageView;
    TextView mVotePromptTextView;
    MediaPlayer mediaPlayer;
    Notification notificationAtRandomIndex;
    Boolean isPlaying = false;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_vote);
         mPlaySoundImageView = (ImageView)findViewById(R.id.voteFragmentPlayAudioImageView);
        mPlayVideoImageView = (ImageView)findViewById(R.id.voteFragmentPlayVideoImageView);
        mVoteThumbUpImageView = (ImageView)findViewById(R.id.voteFragmentThumbsUpImageView);
        mVoteThumbDownImageView = (ImageView)findViewById(R.id.voteFragmentThumbsDownImageView);
        mVotePromptTextView = (TextView)findViewById(R.id.voteFragmentPromptTextView);
        mediaPlayer = new MediaPlayer();
        final int bucketNumber = getIntent().getIntExtra("BucketNumber",1);
        int numberOfTips = getIntent().getIntExtra("NumberOfTips",1);
        Log.d("did", "onCreate: " + numberOfTips);
        Realm realm = Realm.getDefaultInstance();
        mNotifications = realm.where(Notification.class).findAllAsync();
        mNotifications.addChangeListener(callback);

        mPlaySoundImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (isPlaying){
                    pauseMediaPlayer();
                }else {
                    try {
                        NotificationFunctions.playSound(notificationAtRandomIndex,mediaPlayer,VoteActivity.this);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        });


        mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mediaPlayer) {
                mediaPlayer.release();
                isPlaying = false;
            }
        });

        mPlayVideoImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                NotificationFunctions.playVideo(VoteActivity.this,notificationAtRandomIndex);
            }
        });


        mVoteThumbDownImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                updateRandomNotification(true,bucketNumber);
               homeIntent();
                Utility.addCustomEventWithNotification(Constant.THUMBS_DOWN,notificationAtRandomIndex.getMessage());
            }
        });

        mVoteThumbUpImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                updateRandomNotification(false,bucketNumber);
                homeIntent();
                Utility.addCustomEventWithNotification(Constant.THUMBS_UP,notificationAtRandomIndex.getMessage());
            }
        });

    }

    public void homeIntent(){
        Intent homeIntent = new Intent(VoteActivity.this,HomeActivity.class);
        homeIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(homeIntent);
    }

    public void updateRandomNotification(Boolean thumbUp,int bucketNumber){
        Realm realm = Realm.getDefaultInstance();
        realm.beginTransaction();
        notificationAtRandomIndex.setPlayToday(true);
        realm.copyToRealmOrUpdate(notificationAtRandomIndex);
        AnswerNotification answerNotification = new AnswerNotification();
        answerNotification.setAnswerBucket(bucketNumber);
        answerNotification.setAnswerTime(new Date());
        realm.copyToRealm(answerNotification);
        realm.commitTransaction();
    }
    public void onPrepared(MediaPlayer player) {

        player.start();
        isPlaying = true;
    }


    private RealmChangeListener<RealmResults<Notification>> callback = new RealmChangeListener<RealmResults<Notification>>() {
        @Override
        public void onChange(RealmResults<Notification> element) {
            for (Notification notficaion:element){
                mNoficaitonArray.add(notficaion);
            }

            if (mNoficaitonArray.size() != 0) {
                int rnd = new Random().nextInt(mNoficaitonArray.size());
                notificationAtRandomIndex = mNoficaitonArray.get(rnd);
                NotificationFunctions.displayPrompt(notificationAtRandomIndex, mVotePromptTextView);
                NotificationFunctions.displayButtons(notificationAtRandomIndex,mPlaySoundImageView,mPlayVideoImageView);
            }

        }
    };

    public void pauseMediaPlayer(){
        if (isPlaying) {
            mediaPlayer.pause();
            isPlaying = false;
        }
    }
}
