package com.learning.leap.beginningtobabbleapp.notification;

import com.learning.leap.beginningtobabbleapp.baseInterface.BaseNotificationPresenter;
import com.learning.leap.beginningtobabbleapp.baseInterface.ViewInterface;
import com.learning.leap.beginningtobabbleapp.models.Notification;

import java.util.ArrayList;
import java.util.Collections;

import io.realm.Realm;
import io.realm.RealmResults;
import rx.Subscriber;
import rx.Subscription;

/**
 * Created by ryangunn on 12/16/16.
 */

public class NotificaitonPresenter extends BaseNotificationPresenter implements NotificationPresenterInterface {
    protected NotificationViewViewInterface notificationViewInterface;

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
