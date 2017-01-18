package com.learning.leap.bwb.baseInterface;

import com.learning.leap.bwb.models.Notification;

import java.util.ArrayList;
import java.util.Collections;

import io.realm.Realm;
import io.realm.RealmResults;
import rx.Subscriber;
import rx.Subscription;

/**
 * Created by ryangunn on 12/22/16.
 */

public class BaseNotificationPresenter {
    protected int index = 0;
    protected int totalCount = 0;
    private BaseNotificationViewInterface baseNotificationViewInterface;
    protected ArrayList<Notification> notifications;
    protected Subscription notificationListSubscription;

    public void onDestory() {
        if (notificationListSubscription != null && !notificationListSubscription.isUnsubscribed()){
            notificationListSubscription.unsubscribe();
        }
    }

    protected void getRealmResults(){
        Notification notification = new Notification();
        notificationListSubscription = notification.getNotificationFromRealm(Realm.getDefaultInstance())
                .subscribe(new Subscriber<RealmResults<Notification>>() {
                    @Override
                    public void onCompleted() {
                      updateView();
                    }

                    @Override
                    public void onError(Throwable e) {
                        e.printStackTrace();
                    }

                    @Override
                    public void onNext(RealmResults<Notification> notifications) {
                        setNotifications(notifications);
                        this.onCompleted();
                    }
                });
    }

    protected void updateView(){
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
        baseNotificationViewInterface.displayPrompt(notificationAtIndex().getMessage());
    }

    public void onPlayAudioPress() {
        String notificationSoundFile = notificationAtIndex().getCreated() + "-" + notificationAtIndex().getSoundFileName();
        baseNotificationViewInterface.playSound(notificationSoundFile);
    }


    public void onPlayVideoPress() {
        String notificationVideoFile = notificationAtIndex().getCreated() + "-" + notificationAtIndex().getVideoFileName();
        baseNotificationViewInterface.playVideo(notificationVideoFile);
    }

    protected void setBaseNotificationViewInterface(BaseNotificationViewInterface baseNotificationViewInterface) {
        this.baseNotificationViewInterface = baseNotificationViewInterface;
    }
}
