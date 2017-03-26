package com.learning.leap.bwb.library;

import android.content.Intent;
import android.graphics.Bitmap;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentTransaction;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.learning.leap.bwb.PlayTodayPresenter;
import com.learning.leap.bwb.baseActivity.HomeActivity;
import com.learning.leap.bwb.R;
import com.learning.leap.bwb.helper.LocalLoadSaveHelper;
import com.learning.leap.bwb.notification.NotificationViewViewInterface;
import com.learning.leap.bwb.settings.SettingOptionActivity;
import com.learning.leap.bwb.utility.Constant;
import com.learning.leap.bwb.utility.Utility;

import java.io.File;
import java.io.FileInputStream;

public class PlayTodayFragment extends android.support.v4.app.Fragment implements MediaPlayer.OnPreparedListener, NotificationViewViewInterface{

    TextView mNotficationPrompt;
    ImageView mNextImageView;
    ImageView mPreviousImageView;
    ImageView mAudioButton;
    ImageView mVideoButton;
    ImageView mHomeImageView;
    ImageView mSettingsImageView;
    ImageView mLibraryImageView;
    Button stopButton;
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
        setUpBackground(view);
        mNotficationPrompt = (TextView) view.findViewById(R.id.playTodayFragmentPromptTextView);
        mNextImageView = (ImageView) view.findViewById(R.id.playTodayFragmentNextImageView);
        mPreviousImageView = (ImageView) view.findViewById(R.id.playTodayFragmentPreviousImageView);
        mAudioButton = (ImageView) view.findViewById(R.id.playTodayFragmentPlayAudioImageView);
        mVideoButton = (ImageView)view.findViewById(R.id.playTodayFragmentPlayVideoImageView);
        mFavoriteButton = (Button)view.findViewById(R.id.notificationFragmentFavoriteButton);
        mSettingsImageView = (ImageView)view.findViewById(R.id.playTodayFragmentSettingsImageView);
        mLibraryImageView = (ImageView)view.findViewById(R.id.playTodayFragmentLibraryImageView);
        mHomeImageView = (ImageView)view.findViewById(R.id.playTodayFragmentHomeImageView);
        stopButton = (Button)view.findViewById(R.id.playTodayFragmentStopButton);
    }

    private void setUpBackground(View view){
        ImageView background = (ImageView)view.findViewById(R.id.playTodayBackground);
        Bitmap backgroundBitmap = Utility.decodeSampledBitmapFromResource(getResources(),R.drawable.playtoday_bg,Utility.getDisplayMetrics(getActivity()));
        background.setImageBitmap(backgroundBitmap);
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
        mediaPlayer.setOnCompletionListener(mediaPlayer1 -> {
            releaseMediaPlayer();
            isPlaying = false;
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
        stopButton.setOnClickListener(view -> presenter.onStopButtonPress());

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
            try {
                setupMediaFile(fileName);
            } catch (Exception e) {
                e.printStackTrace();
                getActivity().finish();
            }
    }

    private void setupMediaFile(String fileName) throws Exception{
        Utility.addCustomEventWithNotification(Constant.PLAYED_SOUND, fileName,Utility.getUserID(getActivity()));
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
        Utility.addCustomEventWithNotification(Constant.PLAYED_VIDEO,fileName,Utility.getUserID(getActivity()));
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

    @Override
    public void hideStopButton() {
        stopButton.setVisibility(View.GONE);
    }

    @Override
    public void displayStopButton() {
        stopButton.setVisibility(View.VISIBLE);
    }

    @Override
    public void stopPlayer() {
        releaseMediaPlayer();
    }

    @Override
    public String babyName() {
        LocalLoadSaveHelper localLoadSaveHelper = new LocalLoadSaveHelper(getActivity());
        return localLoadSaveHelper.getBabyName();
    }
}
