package com.learning.leap.beginningtobabbleapp.library;

import android.content.Intent;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.learning.leap.beginningtobabbleapp.utility.Constant;
import com.learning.leap.beginningtobabbleapp.baseActivity.HomeActivity;
import com.learning.leap.beginningtobabbleapp.notification.NotificaitonPresenter;
import com.learning.leap.beginningtobabbleapp.notification.NotificationViewViewInterface;
import com.learning.leap.beginningtobabbleapp.R;
import com.learning.leap.beginningtobabbleapp.utility.Utility;
import com.learning.leap.beginningtobabbleapp.settings.SettingOptionActivity;

import java.io.File;
import java.io.FileInputStream;

import butterknife.ButterKnife;
import butterknife.Unbinder;


public class LibarayFragment extends Fragment implements NotificationViewViewInterface,MediaPlayer.OnPreparedListener {
    NotificaitonPresenter notificationPresenter;
    TextView mNotficationPrompt;
    ImageView mNextImageView;
    ImageView mPreviousImageView;
    ImageView mAudioButton;
    ImageView mVideoButton;
    ImageView mHomeImageView;
    Button mFavoriteButton;
    ImageView mSettingButton;
    ImageView mPlayToday;
    MediaPlayer mediaPlayer;
    Boolean isPlaying = false;
    private Unbinder unbinder;
    Boolean mFavoirte;
    TextView pauseTextView;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_library, container, false);
        unbinder = ButterKnife.bind(this,view);
        ButterKnife.setDebug(true);
        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        setUpViews(view);
       notificationPresenter = new NotificaitonPresenter();
        notificationPresenter.attachView(this);
        notificationPresenter.onCreate();
        mediaPlayer = new MediaPlayer();
        mHomeImageView = (ImageView)view.findViewById(R.id.notificationFragmentHomeImageView);
        mHomeImageView.setOnClickListener(homeButton -> notificationPresenter.onHomeButtonPressed());
        mNextImageView.setOnClickListener(nextImageClick -> notificationPresenter.onNextPress());

        mPreviousImageView.setOnClickListener(previousButton -> notificationPresenter.onBackPress());

        mAudioButton.setOnClickListener(audioButton -> notificationPresenter.onPlayAudioPress());

        mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mediaPlayer) {
                releaseMediaPlayer();
                isPlaying = false;
            }
        });

        mVideoButton.setOnClickListener(videoButton -> notificationPresenter.onPlayVideoPress());
        mSettingButton.setOnClickListener(settingButton -> notificationPresenter.onSettingPressed());
        mPlayToday.setOnClickListener(playTodayButton -> notificationPresenter.onPlayTodayPressed());
    }

    private void setUpViews(View view){
        mNotficationPrompt = (TextView)view.findViewById(R.id.notificationFragmentPromptTextView);
        mNextImageView = (ImageView)view.findViewById(R.id.notificationFragmentNextImageView);
        mPreviousImageView = (ImageView)view.findViewById(R.id.notificationFragmentPreviousImageView);
        mAudioButton = (ImageView)view.findViewById(R.id.notificationFragmentPlayAudioImageView);
        mVideoButton = (ImageView)view.findViewById(R.id.notificationFragmentPlayVideoImageView);
        mHomeImageView = (ImageView)view.findViewById(R.id.notificationFragmentHomeImageView);
        mFavoriteButton = (Button) view.findViewById(R.id.notificationFragmentFavoriteButton);
        mSettingButton = (ImageView)view.findViewById(R.id.notificationFragmentSettingsImageView);
        mPlayToday = (ImageView)view.findViewById(R.id.notificationFragmentPlayTodayImageView);
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
    public void onDestroy() {
        notificationPresenter.onDestory();
        super.onDestroy();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
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
    public void hideNextButton() {
        mNextImageView.setVisibility(View.INVISIBLE);
    }

    @Override
    public void hidePreviousButton() {
    mPreviousImageView.setVisibility(View.INVISIBLE);
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
    public void displayNextButton() {
        mNextImageView.setVisibility(View.VISIBLE);
    }

    @Override
    public void displayPreviousButton() {
        mPreviousImageView.setVisibility(View.VISIBLE);
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
        FragmentTransaction ft = getActivity().getSupportFragmentManager().beginTransaction();
        PlayTodayFragment playTodayFragment = new PlayTodayFragment();
        ft.replace(R.id.detailFragment, playTodayFragment);
        ft.commit();
    }

    @Override
    public void onLibraryPress() {

    }
}
