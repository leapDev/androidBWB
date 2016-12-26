package com.learning.leap.beginningtobabbleapp.library;

import android.content.Intent;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.learning.leap.beginningtobabbleapp.PlayTodayPresenter;
import com.learning.leap.beginningtobabbleapp.baseActivity.HomeActivity;
import com.learning.leap.beginningtobabbleapp.R;
import com.learning.leap.beginningtobabbleapp.models.Notification;
import com.learning.leap.beginningtobabbleapp.models.NotificationFunctions;
import com.learning.leap.beginningtobabbleapp.notification.NotificationViewViewInterface;
import com.learning.leap.beginningtobabbleapp.settings.SettingOptionActivity;
import com.learning.leap.beginningtobabbleapp.utility.Constant;
import com.learning.leap.beginningtobabbleapp.utility.Utility;

import java.io.File;
import java.io.FileInputStream;
import java.util.ArrayList;

import io.realm.Realm;
import io.realm.RealmChangeListener;
import io.realm.RealmResults;

/**
 * Created by ryangunn on 10/20/16.
 */

public class PlayTodayFragment extends android.support.v4.app.Fragment implements MediaPlayer.OnPreparedListener, NotificationViewViewInterface{

    TextView mNotficationPrompt;
    ImageView mNextImageView;
    ImageView mPreviousImageView;
    ImageView mAudioButton;
    ImageView mVideoButton;
    ImageView mHomeImageView;
    ImageView mSettingsImageView;
    ImageView mLibraryImageView;
    MediaPlayer mediaPlayer;
    Button mFavoriteButton;
    Boolean isPlaying = false;
    Boolean mFavoirte;
    PlayTodayPresenter presenter;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_play_today, container, false);
    }

    private void setUpViews(View view){
        mNotficationPrompt = (TextView) view.findViewById(R.id.playTodayFragmentPromptTextView);
        mNextImageView = (ImageView) view.findViewById(R.id.playTodayFragmentNextImageView);
        mPreviousImageView = (ImageView) view.findViewById(R.id.playTodayFragmentPreviousImageView);
        mAudioButton = (ImageView) view.findViewById(R.id.playTodayFragmentPlayAudioImageView);
        mVideoButton = (ImageView)view.findViewById(R.id.playTodayFragmentPlayVideoImageView);
        mFavoriteButton = (Button)view.findViewById(R.id.notificationFragmentFavoriteButton);
        mSettingsImageView = (ImageView)view.findViewById(R.id.playTodayFragmentSettingsImageView);
        mLibraryImageView = (ImageView)view.findViewById(R.id.playTodayFragmentLibraryImageView);
        mHomeImageView = (ImageView)view.findViewById(R.id.playTodayFragmentHomeImageView);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        setUpViews(view);
        setUpOnClickLisnter();
        presenter = new PlayTodayPresenter();
        presenter.attachView(this);
        presenter.onCreate();
        mediaPlayer = new MediaPlayer();
        mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mediaPlayer) {
                releaseMediaPlayer();
                isPlaying = false;
            }
        });

    }

    private void setUpOnClickLisnter(){
        mVideoButton.setOnClickListener(view -> presenter.onPlayVideoPress());
        mAudioButton.setOnClickListener(view -> presenter.onPlayAudioPress());
        mNextImageView.setOnClickListener(view -> presenter.onNextPress());
        mPreviousImageView.setOnClickListener(view -> presenter.onBackPress());
        mHomeImageView.setOnClickListener(view -> onHomePress());
        mSettingsImageView.setOnClickListener(view -> onSettingsPress());
        mLibraryImageView.setOnClickListener(view -> onLibraryPress());

    }

    private void releaseMediaPlayer(){
        if (isPlaying) {
            mediaPlayer.stop();
            isPlaying = false;
        }
        mediaPlayer.release();
        mediaPlayer = new MediaPlayer();
    }

    public void onPrepared(MediaPlayer player) {
        player.start();
        isPlaying = true;
    }


    @Override
    public void hideNextButton() {
        mNextImageView.setVisibility(View.INVISIBLE);
    }

    @Override
    public void hidePreviousButton() {
        mPreviousImageView.setVisibility(View.INVISIBLE);
    }

    @Override
    public void displayNextButton() {
        mNextImageView.setVisibility(View.VISIBLE);
    }

    @Override
    public void displayPreviousButton() {
        mPreviousImageView.setVisibility(View.VISIBLE);
    }

    @Override
    public void displayPrompt(String prompt) {
        mNotficationPrompt.setText(prompt);
    }

    @Override
    public void playSound(String fileName) {
        if (isPlaying){
            releaseMediaPlayer();
        }else {
            try {
                setupMediaFile(fileName);
            } catch (Exception e) {
                e.printStackTrace();
                getActivity().finish();
            }
        }
    }

    private void setupMediaFile(String fileName) throws Exception{
        Utility.addCustomEventWithNotification(Constant.PLAYED_SOUND, fileName);
        File file = new File(getActivity().getFilesDir(), fileName);
        FileInputStream is = new FileInputStream(file);
        mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
        mediaPlayer.setDataSource(is.getFD());
        is.close();
        mediaPlayer.setOnPreparedListener(this);
        mediaPlayer.prepareAsync();
    }

    @Override
    public void playVideo(String fileName) {
        Utility.addCustomEventWithNotification(Constant.PLAYED_VIDEO,fileName);
        VideoActivity.showRemoteVideo(getActivity(),fileName);
    }

    @Override
    public void displaySoundButton() {
        mAudioButton.setVisibility(View.VISIBLE);
    }

    @Override
    public void displayVideoButton() {
        mVideoButton.setVisibility(View.VISIBLE);
    }

    @Override
    public void hideSoundButton() {
        mAudioButton.setVisibility(View.INVISIBLE);
    }

    @Override
    public void hideVideoButton() {
        mVideoButton.setVisibility(View.INVISIBLE);
    }

    @Override
    public void onHomePress() {
        Intent homeIntent = new Intent(getActivity(), HomeActivity.class);
        homeIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(homeIntent);
    }

    @Override
    public void onSettingsPress() {
        Intent settingsIntent = new Intent(getActivity(), SettingOptionActivity.class);
        startActivity(settingsIntent);
    }

    @Override
    public void onPlayToday() {

    }

    @Override
    public void onLibraryPress() {
        FragmentTransaction ft = getActivity().getSupportFragmentManager().beginTransaction();
        LibarayFragment libarayFragment = new LibarayFragment();
        ft.replace(R.id.detailFragment, libarayFragment);
        ft.commit();
    }
}
