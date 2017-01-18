package com.learning.leap.bwb.vote;

import com.learning.leap.bwb.baseInterface.BaseNotificationPresenter;
import com.learning.leap.bwb.helper.AnswerNotification;
import com.learning.leap.bwb.utility.Constant;
import com.learning.leap.bwb.utility.Utility;

import java.util.Date;

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

    public void onCreate(){
        setBaseNotificationViewInterface(voteViewInterface);
        getRealmResults();
    }

    private Boolean doHomeIntent(){
        return index == numberOfTips - 1;
    }

    protected void thumbUpButtonTapped(){
       updateRandomNotification(true);
        Utility.addCustomEventWithNotification(Constant.THUMBS_UP,notifications.get(index).getSoundFileName());
        checkForHomeIntent();
    }

    protected void thumbDownButtonTapped(){
        updateRandomNotification(false);
        Utility.addCustomEventWithNotification(Constant.THUMBS_DOWN,notifications.get(index).getSoundFileName());
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
