package com.example.ryangunn.beginningtobabble;

import android.app.Fragment;
import android.content.Intent;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.ryangunn.beginningtobabble.library.LibarayFragment;
import com.example.ryangunn.beginningtobabble.models.Notification;
import com.example.ryangunn.beginningtobabble.models.NotificationFunctions;
import com.example.ryangunn.beginningtobabble.settings.SettingOptionActivity;

import java.io.File;
import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.Collections;

import io.realm.Realm;
import io.realm.RealmChangeListener;
import io.realm.RealmResults;

/**
 * Created by ryangunn on 10/20/16.
 */

public class PlayTodayFragment extends android.support.v4.app.Fragment implements MediaPlayer.OnPreparedListener{

    TextView mNotficationPrompt;
    ArrayList<Notification> mNotfications = new ArrayList<>();
    ImageView mNextImageView;
    ImageView mPreviousImageView;
    ImageView mAudioButton;
    ImageView mVideoButton;
    ImageView mHomeImageView;
    MediaPlayer mediaPlayer;
    Button mFavoriteButton;
    public int index = 0;
    public int totalCount = 0;
    RealmResults<Notification> realmNotificaions;
    Boolean isPlaying = false;
    Realm mRealm;
    Boolean mFavoirte;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_play_today, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mNotficationPrompt = (TextView) view.findViewById(R.id.playTodayFragmentPromptTextView);
        mNextImageView = (ImageView) view.findViewById(R.id.playTodayFragmentNextImageView);
        mPreviousImageView = (ImageView) view.findViewById(R.id.playTodayFragmentPreviousImageView);
        mAudioButton = (ImageView) view.findViewById(R.id.playTodayFragmentPlayAudioImageView);
        mHomeImageView = (ImageView)view.findViewById(R.id.playTodayFragmentHomeImageView);
        mVideoButton = (ImageView)view.findViewById(R.id.playTodayFragmentPlayVideoImageView);
        mFavoriteButton = (Button)view.findViewById(R.id.notificationFragmentFavoriteButton);
        mediaPlayer = new MediaPlayer();
        mHomeImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                PlayTodayFragment.this.getActivity().finish();
            }
        });
        if (index == 0) {
            mPreviousImageView.setVisibility(View.INVISIBLE);

        }


        mNextImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                index++;
                hideNextButtonCheck();
                hideBackButton();
                pauseMediaPlayer();
                NotificationFunctions.displayPrompt(mNotfications.get(index), mNotficationPrompt);
                //updateFavoriteButton(mNotfications.get(index));
                NotificationFunctions.displayButtons(mNotfications.get(index),mAudioButton,mVideoButton);

            }
        });

        mPreviousImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                index--;
                hideNextButtonCheck();
                hideBackButton();
                pauseMediaPlayer();
                NotificationFunctions.displayPrompt(mNotfications.get(index), mNotficationPrompt);
                NotificationFunctions.displayButtons(mNotfications.get(index),mAudioButton,mVideoButton);

                //updateFavoriteButton(mNotfications.get(index));
            }
        });

        mAudioButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (isPlaying){
                    pauseMediaPlayer();
                }else {
                    try {
                        NotificationFunctions.playSound(mNotfications.get(index),mediaPlayer,PlayTodayFragment.this);
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


        mVideoButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                NotificationFunctions.playVideo(getActivity(),mNotfications.get(index));
            }
        });

//        mFavoriteButton.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                Notification notification = mNotfications.get(index);
//                updateFavorite(notification.getMessage(),notification.getSoundFileName());
//            }
//        });

        mRealm = Realm.getDefaultInstance();
        realmNotificaions = mRealm.where(Notification.class).equalTo("mPlayToday",true).findAllAsync();
        realmNotificaions.addChangeListener(callback);

        ImageView settingButton = (ImageView)view.findViewById(R.id.playTodayFragmentSettingsImageView);
        ImageView playToday = (ImageView)view.findViewById(R.id.playTodayFragmentLibraryImageView);
        settingButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent settingsIntent = new Intent(getActivity(), SettingOptionActivity.class);
                startActivity(settingsIntent);
            }
        });

        playToday.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // add fragment transcation
                FragmentTransaction ft = getActivity().getSupportFragmentManager().beginTransaction();
                LibarayFragment libarayFragment = new LibarayFragment();
                ft.replace(R.id.detailFragment, libarayFragment);

                ft.commit();
            }
        });

    }

    public void pauseMediaPlayer(){
        if (isPlaying) {
            mediaPlayer.pause();
            isPlaying = false;
        }
    }

    public void updateFavorite(final String prompt, final String soundFile){
        mRealm.executeTransactionAsync(new Realm.Transaction() {
            @Override
            public void execute(Realm bgRealm) {
                Notification notification = bgRealm.where(Notification.class).equalTo("mSoundFileName", soundFile).equalTo("mMessage",prompt).findFirst();
                if (notification.getFavorite()){
                    notification.setFavorite(false);
                }else {
                    notification.setFavorite(true);
                }
            }
        }, new Realm.Transaction.OnSuccess() {
            @Override
            public void onSuccess() {
               // updateFavoriteButton(mNotfications.get(index));
                // Original queries and Realm objects are automatically updated.
//                puppies.size(); // => 0 because there are no more puppies younger than 2 years old
//                managedDog.getAge();   // => 3 the dogs age is updated
            }
        });
    }

//    public void updateFavoriteButton(Notification notification){
//        if (notification.getFavorite()){
//            mFavoriteButton.setText("UnFavorite");
//        }else {
//            mFavoriteButton.setText("Favorite");
//        }
//    }



    public void hideNextButtonCheck() {
        if (index == (totalCount - 2) || mNotfications.size() == 0) {
            mNextImageView.setVisibility(View.INVISIBLE);
        }
    }

    public void hideBackButton() {
        if (index != 0) {
            mPreviousImageView.setVisibility(View.VISIBLE);
        } else {
            mPreviousImageView.setVisibility(View.INVISIBLE);
        }
    }


    public void onPrepared(MediaPlayer player) {

        player.start();
        isPlaying = true;
    }

    private RealmChangeListener<RealmResults<Notification>> callback = new RealmChangeListener<RealmResults<Notification>>() {
        @Override
        public void onChange(RealmResults<Notification> element) {
            for (Notification notficaion:element){
                mNotfications.add(notficaion);
                totalCount++;
            }
            if (totalCount != 0) {
                NotificationFunctions.displayPrompt(mNotfications.get(index), mNotficationPrompt);
                NotificationFunctions.displayButtons(mNotfications.get(index),mAudioButton,mVideoButton);
                //updateFavoriteButton(mNotfications.get(index));
            }else {
                hideButtons();
            }
            Collections.shuffle(mNotfications);
        }
    };

    private void hideButtons(){
        mAudioButton.setVisibility(View.INVISIBLE);
        mVideoButton.setVisibility(View.INVISIBLE);
        mNextImageView.setVisibility(View.INVISIBLE);
        mPreviousImageView.setVisibility(View.INVISIBLE);
    }


    @Override
    public void onStop() {
        super.onStop();
        realmNotificaions.removeChangeListener(callback);

    }

}
