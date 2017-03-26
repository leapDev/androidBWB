package com.learning.leap.bwb.baseInterface;

import com.learning.leap.bwb.models.Notification;

import java.util.ArrayList;
import java.util.Collections;

import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.realm.Realm;
import io.realm.RealmResults;


public class BaseNotificationPresenter {
    protected int index = 0;
    protected int totalCount = 0;
    private BaseNotificationViewInterface baseNotificationViewInterface;
    protected ArrayList<Notification> notifications;
    private final CompositeDisposable disposables = new CompositeDisposable();
    private String babyName;
    protected boolean isPlaying;

    public void onDestory() {
        disposables.clear();
    }

    protected void getRealmResults(){
        babyName = baseNotificationViewInterface.babyName();
        Notification notification = new Notification();
        Disposable disposable = notification.getNotificationFromRealm(Realm.getDefaultInstance()).subscribe(this::setNotifications, Throwable::printStackTrace);
        disposables.add(disposable);
    }

    protected void updateView(){
        if (isPlaying){
            onStopButtonPress();
        }
        baseNotificationViewInterface.displayPrompt(notificationAtIndex().getMessage());
        videoButtonCheck();
        soundButtonCheck();
    }

    protected void setNotifications(RealmResults<Notification> notificationRealmResults){
        notifications = new ArrayList<>(notificationRealmResults);
        Collections.shuffle(notifications);
        totalCount = notifications.size();
    }

    protected void soundButtonCheck(){
        if (notificationAtIndex().noSoundFile()){
            baseNotificationViewInterface.hideSoundButton();
        }else {
            baseNotificationViewInterface.displaySoundButton();
        }
    }

    protected void videoButtonCheck(){
        if (notificationAtIndex().noVideFile()){
            baseNotificationViewInterface.hideVideoButton();
        }else {
            baseNotificationViewInterface.displayVideoButton();
        }
    }

    protected Notification notificationAtIndex(){
        return notifications.get(index);
    }


    protected void displayPrompt(){
        baseNotificationViewInterface.displayPrompt(notificationAtIndex().updateMessage(babyName));
    }

    public void onPlayAudioPress() {
        String notificationSoundFile = notificationAtIndex().getCreated() + "-" + notificationAtIndex().getSoundFileName();
        baseNotificationViewInterface.playSound(notificationSoundFile);
        isPlaying = true;
        showStopButton();
    }

    public void onStopButtonPress(){
        removeStopButton();
        baseNotificationViewInterface.stopPlayer();
        isPlaying = false;
    }

    private void showStopButton(){
        baseNotificationViewInterface.displayStopButton();
        baseNotificationViewInterface.hideSoundButton();
    }
    private void removeStopButton(){
        baseNotificationViewInterface.hideStopButton();
        baseNotificationViewInterface.displaySoundButton();
    }

    public void onPlayVideoPress() {
        String notificationVideoFile = notificationAtIndex().getCreated() + "-" + notificationAtIndex().getVideoFileName();
        baseNotificationViewInterface.playVideo(notificationVideoFile);
    }

    protected void setBaseNotificationViewInterface(BaseNotificationViewInterface baseNotificationViewInterface) {
        this.baseNotificationViewInterface = baseNotificationViewInterface;
    }
}
