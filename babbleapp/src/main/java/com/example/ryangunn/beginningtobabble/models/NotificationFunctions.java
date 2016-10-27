package com.example.ryangunn.beginningtobabble.models;

import android.content.Context;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Environment;
import android.support.v4.app.NotificationCompat;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.ryangunn.beginningtobabble.R;
import com.example.ryangunn.beginningtobabble.library.LibarayFragment;
import com.example.ryangunn.beginningtobabble.library.VideoActivity;

import java.io.File;
import java.io.FileInputStream;
import java.util.Calendar;

/**
 * Created by ryangunn on 9/17/16.
 */
public class NotificationFunctions  {

    public static void setNotications(Context context,String childName){
       NotificationCompat.Builder mBuilder =
                new NotificationCompat.Builder(context)
                        .setContentTitle("My notification")
                        .setContentText("Hello World!");


    }

    public static Boolean noSoundFile(Notification notification){
        String fileName = notification.getSoundFileName();
        return  (fileName.equals("no file"));
    }

    public static Boolean noVideFile(Notification notification){
        String fileName =  notification.getVideoFileName();
        return  (fileName.equals("no file"));
    }

    public static void displayButtons(Notification notification,ImageView audioImageView,ImageView videoImageView){
        if (noSoundFile(notification)) {
            audioImageView.setVisibility(View.INVISIBLE);
        } else {
            audioImageView.setVisibility(View.VISIBLE);
        }

        if (noVideFile(notification)){
            videoImageView.setVisibility(View.INVISIBLE);
        }else {
            videoImageView.setVisibility(View.VISIBLE);
        }
    }

    public static void displayPrompt(Notification notification, TextView promptTextView){
        promptTextView.setText(notification.getMessage());
    }


    public static void playSound(Notification notification, MediaPlayer mediaPlayer, MediaPlayer.OnPreparedListener preparedListener) throws Exception {
        String fileName = notification.getCreated() + "-" + notification.getSoundFileName();
        File path = Environment.getExternalStorageDirectory();
        path.mkdirs();
        File file = new File(Environment.getExternalStorageDirectory(), fileName);
        FileInputStream is = new FileInputStream(file);

        mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);

        mediaPlayer.setDataSource(is.getFD());

        is.close();
        mediaPlayer.setOnPreparedListener(preparedListener);

        mediaPlayer.prepareAsync();

    }

    public static void playVideo(Context context,Notification notification){
        VideoActivity.showRemoteVideo(context,notification.getCreated() +"-"+notification.getVideoFileName());
    }


}
