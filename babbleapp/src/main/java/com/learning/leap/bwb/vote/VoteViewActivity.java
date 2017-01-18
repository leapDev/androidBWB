package com.learning.leap.bwb.vote;

import android.content.Intent;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.learning.leap.bwb.baseActivity.HomeActivity;
import com.learning.leap.bwb.library.VideoActivity;
import com.learning.leap.bwb.R;
import com.learning.leap.bwb.utility.Constant;
import com.learning.leap.bwb.utility.Utility;

import java.io.File;
import java.io.FileInputStream;

public class VoteViewActivity extends AppCompatActivity implements VoteViewViewInterface, MediaPlayer.OnPreparedListener {
    VotePresenter votePresenter;
    ImageView mPlaySoundImageView;
    ImageView mPlayVideoImageView;
    ImageView mVoteThumbUpImageView;
    ImageView mVoteThumbDownImageView;
    TextView mVotePromptTextView;
    MediaPlayer mediaPlayer;
    Boolean isPlaying = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_vote);
        setUpViews();
        mediaPlayer = new MediaPlayer();
        int bucketNumber = getIntent().getIntExtra("BucketNumber",1);
        int numberOfTips  = getIntent().getIntExtra("NumberOfTips",1);
        votePresenter = new VotePresenter(numberOfTips,bucketNumber,this);
        votePresenter.onCreate();
        setOnClickLisnter();
        mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mediaPlayer) {
                releaseMediaPlayer();
                isPlaying = false;
            }
        });

    }

    private void setUpViews(){
        mPlaySoundImageView = (ImageView)findViewById(R.id.voteFragmentPlayAudioImageView);
        mPlayVideoImageView = (ImageView)findViewById(R.id.voteFragmentPlayVideoImageView);
        mVoteThumbUpImageView = (ImageView)findViewById(R.id.voteFragmentThumbsUpImageView);
        mVoteThumbDownImageView = (ImageView)findViewById(R.id.voteFragmentThumbsDownImageView);
        mVotePromptTextView = (TextView)findViewById(R.id.voteFragmentPromptTextView);
    }

    private void setOnClickLisnter(){
        mPlaySoundImageView.setOnClickListener(view -> votePresenter.onPlayAudioPress());
        mPlayVideoImageView.setOnClickListener(view -> votePresenter.onPlayVideoPress());
        mVoteThumbDownImageView.setOnClickListener(view -> votePresenter.thumbDownButtonTapped());
        mVoteThumbUpImageView.setOnClickListener(view -> votePresenter.thumbUpButtonTapped());
    }

    public void onPrepared(MediaPlayer player) {
        player.start();
        isPlaying = true;
    }

    private void releaseMediaPlayer(){
        if (isPlaying) {
            mediaPlayer.stop();
            isPlaying = false;
        }
        mediaPlayer.release();
        mediaPlayer = new MediaPlayer();
    }

    @Override
    public void homeIntent() {
        Intent homeIntent = new Intent(this, HomeActivity.class);
        homeIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(homeIntent);
    }

    @Override
    public void displayPrompt(String prompt) {
        mVotePromptTextView.setText(prompt);
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
                this.finish();
            }
        }
    }

    private void setupMediaFile(String fileName) throws Exception{
        Utility.addCustomEventWithNotification(Constant.PLAYED_SOUND, fileName);
        File file = new File(this.getFilesDir(), fileName);
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
        VideoActivity.showRemoteVideo(this,fileName);
    }

    @Override
    public void displaySoundButton() {
        mPlaySoundImageView.setVisibility(View.VISIBLE);
    }

    @Override
    public void displayVideoButton() {
        mPlayVideoImageView.setVisibility(View.VISIBLE);
    }

    @Override
    public void hideSoundButton() {
        mPlaySoundImageView.setVisibility(View.INVISIBLE);
    }

    @Override
    public void hideVideoButton() {
        mPlayVideoImageView.setVisibility(View.INVISIBLE);
    }
}
