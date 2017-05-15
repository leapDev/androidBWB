package com.learning.leap.bwb.baseInterface;

import com.learning.leap.bwb.models.Notification;
import com.learning.leap.bwb.notification.NotificationPresenterInterface;
import com.learning.leap.bwb.notification.NotificationViewViewInterface;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.realm.Realm;
import io.realm.RealmResults;


public abstract class BaseNotificationPresenter implements NotificationPresenterInterface {
    public NotificationViewViewInterface notificationViewInterface;
    public int index = 0;
    public int totalCount = 0;
    public   BaseNotificationViewInterface baseNotificationViewInterface;
    public ArrayList<Notification> notifications = new ArrayList<>();
    public  final CompositeDisposable disposables = new CompositeDisposable();
    public String babyName;
    public boolean isPlaying;

    public void onDestory() {
        disposables.clear();
    }


    public abstract void getRealmResults();

    public void updateView(){
        if (isPlaying){
            onStopButtonPress();
        }
        baseNotificationViewInterface.displayPrompt(notificationAtIndex().getMessage());
        videoButtonCheck();
        soundButtonCheck();
    }

    public void setNotifications(RealmResults<Notification> notificationRealmResults){
        notifications.addAll(notificationRealmResults);
        Collections.shuffle(notifications);
        totalCount = notifications.size();
    }


    public void soundButtonCheck(){
        if (notificationAtIndex().noSoundFile()){
            baseNotificationViewInterface.hideSoundButton();
        }else {
            baseNotificationViewInterface.displaySoundButton();
        }
    }

    public void videoButtonCheck(){
        if (notificationAtIndex().noVideFile()){
            baseNotificationViewInterface.hideVideoButton();
        }else {
            baseNotificationViewInterface.displayVideoButton();
        }
    }

    public String getTag(){
        return notificationAtIndex().getTag();
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

    @Override
    public void onCreate() {
        setBaseNotificationViewInterface(notificationViewInterface);
        getRealmResults();
        notificationViewInterface.hidePreviousButton();
        if (notifications.size() <= 1){
            notificationViewInterface.hideNextButton();
        }

        if (notifications.size() == 0){
            hideAllButtons();
        }else {
            displayPrompt();
        }
    }


    @Override
    public void onNextPress() {
        index++;
        nextButtonCheck();
        previousButtonCheck();
        displayPrompt();
        soundButtonCheck();
        videoButtonCheck();
    }

    @Override
    public void onBackPress() {
        index--;
        previousButtonCheck();
        nextButtonCheck();
        displayPrompt();
        soundButtonCheck();
        videoButtonCheck();
    }


    @Override
    public void onResume() {

    }

    @Override
    public void attachView(ViewInterface viewInterface) {
        notificationViewInterface = (NotificationViewViewInterface)viewInterface;
    }


    private Boolean indexIs0(){
        return index == 0;
    }

    private void nextButtonCheck(){
        if (index == totalCount -1){
            notificationViewInterface.hideNextButton();
        }else {
            notificationViewInterface.displayNextButton();
            if (isPlaying){
                onStopButtonPress();
            }
        }
    }

    private void previousButtonCheck(){
        if (indexIs0()){
            notificationViewInterface.hidePreviousButton();
        }else {
            notificationViewInterface.displayPreviousButton();
        }
    }

    private void hideAllButtons(){
        notificationViewInterface.hideNextButton();
        notificationViewInterface.hidePreviousButton();
        notificationViewInterface.hideSoundButton();
        notificationViewInterface.hideVideoButton();
    }

    @Override
    public void onHomeButtonPressed() {
        notificationViewInterface.onHomePress();
    }

    @Override
    public void onLibraryPressed() {
        notificationViewInterface.onLibraryPress();
    }

    @Override
    public void onSettingPressed() {
        notificationViewInterface.onSettingsPress();
    }

    @Override
    public void onPlayTodayPressed() {
        notificationViewInterface.onPlayToday();
    }
}
