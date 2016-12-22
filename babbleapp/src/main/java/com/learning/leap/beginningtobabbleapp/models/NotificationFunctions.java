package com.learning.leap.beginningtobabbleapp.models;

import android.content.Context;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.support.v4.app.NotificationCompat;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.learning.leap.beginningtobabbleapp.utility.Constant;
import com.learning.leap.beginningtobabbleapp.utility.Utility;
import com.learning.leap.beginningtobabbleapp.library.VideoActivity;

import java.io.File;
import java.io.FileInputStream;

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

    public  Boolean noSoundFile(Notification notification){
        String fileName = notification.getSoundFileName();
        return  (fileName.equals("no file"));
    }

    public Boolean noVideFile(Notification notification){
        String fileName =  notification.getVideoFileName();
        return  (fileName.equals("no file"));
    }

    public void displayButtons(Notification notification,ImageView audioImageView,ImageView videoImageView){
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

    public void displayPrompt(Notification notification, TextView promptTextView){
        promptTextView.setText(notification.getMessage());
    }


    public static void playSound(Notification notification, MediaPlayer mediaPlayer, MediaPlayer.OnPreparedListener preparedListener,Context context) throws Exception {
        String fileName = notification.getCreated() + "-" + notification.getSoundFileName();
        Utility.addCustomEventWithNotification(Constant.PLAYED_SOUND,notification.getSoundFileName());

        File file = new File(context.getFilesDir(), fileName);
        FileInputStream is = new FileInputStream(file);

        mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);

        mediaPlayer.setDataSource(is.getFD());

        is.close();
        mediaPlayer.setOnPreparedListener(preparedListener);

        mediaPlayer.prepareAsync();

    }

    public static void playVideo(Context context,Notification notification){
        Utility.addCustomEventWithNotification(Constant.PLAYED_VIDEO,notification.getMessage());
        VideoActivity.showRemoteVideo(context,notification.getCreated() +"-"+notification.getVideoFileName());
    }


}
