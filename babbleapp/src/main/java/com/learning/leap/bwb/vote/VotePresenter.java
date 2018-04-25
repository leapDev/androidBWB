package com.learning.leap.bwb.vote;

import com.learning.leap.bwb.BuildConfig;
import com.learning.leap.bwb.baseInterface.BaseNotificationPresenter;
import com.learning.leap.bwb.helper.AnswerNotification;
import com.learning.leap.bwb.models.Notification;
import com.learning.leap.bwb.utility.Constant;
import com.learning.leap.bwb.utility.Utility;

import java.util.Date;

import io.reactivex.disposables.Disposable;
import io.realm.Realm;



public class VotePresenter extends BaseNotificationPresenter {
    private int numberOfTips;
    private int bucketNumber;
    private VoteViewViewInterface voteViewInterface;

    public VotePresenter(int numberOfTips,int bucketNumber,VoteViewViewInterface voteViewInterface){
        this.numberOfTips = numberOfTips;
        this.bucketNumber = bucketNumber;
        this.voteViewInterface = voteViewInterface;
    }

    @Override
    public void getRealmResults() {
        babyName = baseNotificationViewInterface.babyName();
            Notification notification = new Notification();
            Disposable disposable = notification.getNotificationFromRealm(Realm.getDefaultInstance()).subscribe(this::setNotifications, Throwable::printStackTrace);
            disposables.add(disposable);
    }

    @Override
    public void onCreate(){
        setBaseNotificationViewInterface(voteViewInterface);
        getRealmResults();
        if (notifications.size() == 0){
            voteViewInterface.homeIntent();
        }else {
            displayPrompt();
        }

    }

    private Boolean doHomeIntent(){
        return index == numberOfTips - 1;
    }

    void thumbUpButtonTapped(){
       updateRandomNotification(true);
        checkForHomeIntent();
    }

    void thumbDownButtonTapped(){
        updateRandomNotification(false);
        checkForHomeIntent();
    }

    private void checkForHomeIntent(){
        if (doHomeIntent()){
            voteViewInterface.homeIntent();
        }else {
            index++;
            updateView();

        }
    }
    private void updateRandomNotification(Boolean thumbUp){
        Realm realm = Realm.getDefaultInstance();
        realm.beginTransaction();
        notificationAtIndex().setPlayToday(true);
        notificationAtIndex().setFavorite(thumbUp);
        realm.copyToRealmOrUpdate(notificationAtIndex());
        realm.commitTransaction();
        realm.beginTransaction();
        AnswerNotification answerNotification = new AnswerNotification();
        answerNotification.setAnswerBucket(bucketNumber);
        answerNotification.mAnswerTime = new Date();
        realm.copyToRealm(answerNotification);
        realm.commitTransaction();
    }

}
