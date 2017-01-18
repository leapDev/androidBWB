package com.learning.leap.bwb.library;

import android.content.Context;
import android.content.Intent;
import android.media.MediaPlayer;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.devbrackets.android.exomedia.listener.OnPreparedListener;
import com.devbrackets.android.exomedia.ui.widget.EMVideoView;
import com.learning.leap.bwb.R;

import java.io.File;

public class VideoActivity extends AppCompatActivity implements MediaPlayer.OnCompletionListener {
    EMVideoView emVideoView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video);

     emVideoView = (EMVideoView)findViewById(R.id.videoView);
        emVideoView.setOnPreparedListener(new OnPreparedListener() {
            @Override
            public void onPrepared() {
                emVideoView.start();
            }
        });


        String url = null;
        if (getIntent().getExtras() != null) {
            url = getIntent().getExtras().getString("url");

            if (url != null) {
//                v.setMediaController(new MediaController(this));
//                v.setOnCompletionListener(this);
                File videoFile = new File(getFilesDir(),url);
                emVideoView.setVideoURI(Uri.fromFile(videoFile));

//                v.start();
//                v.requestFocus();

            }
        }

        if (url == null) {
            throw new IllegalArgumentException("Must set url extra paremeter in intent.");
        }
    }


    @Override
    public void onCompletion(MediaPlayer v) {
        finish();
    }

    //Convenience method to show a video
    public static void showRemoteVideo(Context ctx, String url) {
        Intent i = new Intent(ctx, VideoActivity.class);
        i.putExtra("url", url);
        ctx.startActivity(i);

    }
}
