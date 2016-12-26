package com.learning.leap.beginningtobabbleapp.userInfo;

import android.content.Context;

import com.amazonaws.mobileconnectors.dynamodbv2.dynamodbmapper.PaginatedScanList;
import com.learning.leap.beginningtobabbleapp.R;
import com.learning.leap.beginningtobabbleapp.utility.Utility;
import com.learning.leap.beginningtobabbleapp.models.BabblePlayer;
import com.learning.leap.beginningtobabbleapp.models.Notification;

import java.util.List;

import io.realm.Realm;
import rx.Observable;
import rx.Subscriber;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Created by ryangunn on 12/17/16.
 */

public class UserInfoPresenter implements UserInfoPresenterInterface {
   private UserInfoViewInterface userInfoViewInterface;
    private boolean newUser;
    private Context context;
   private BabblePlayer babblePlayer;
    private Subscription savePlayerSubscrtion;
    private Subscription retriveNotificationsSubscrtion;
    List<Notification> retrivenotifications;


    public UserInfoPresenter(Context context, Boolean newUser, UserInfoViewInterface userInfoViewInterface){
        this.newUser = newUser;
        this.context = context;
        this.userInfoViewInterface = userInfoViewInterface;
    }
    @Override
    public void onCreate() {

    }

    @Override
    public void onDestory() {
        if (savePlayerSubscrtion != null && !savePlayerSubscrtion.isUnsubscribed()){
            savePlayerSubscrtion.unsubscribe();
        }
        if (retriveNotificationsSubscrtion != null && !retriveNotificationsSubscrtion.isUnsubscribed()){
            retriveNotificationsSubscrtion.unsubscribe();
        }
    }

    @Override
    public void onResume() {

    }


    @Override
    public void updatePlayer() {
        savePlayerSubscrtion = babblePlayer.savePlayerObservable(context).doOnCompleted(() -> updatePlayerOnCompleted())
                .doOnError(throwable -> userInfoViewInterface.displayErrorDialog(R.string.BabbleError, R.string.userSave))
                .subscribe();
    }

    private void updatePlayerOnCompleted(){
        if (Utility.isNetworkAvailable(context)) {
            if (newUser) {
                retriveNotificationsFromAmazon();
            } else {
                userInfoViewInterface.dismissActivity();
            }
        }else{
            userInfoViewInterface.displayErrorDialog(R.string.BabbleError,R.string.noConnection);
        }
    }
    @Override
    public void saveNotifications(List<Notification> notifications) {
        for (int i = 0; i< notifications.size(); i++){
            notifications.get(i).setId(i);
        }
        Realm realm = Realm.getDefaultInstance();
        Utility.writeIntSharedPreferences("notficationListSize",notifications.size(),context);
        realm.beginTransaction();
        realm.where(Notification.class).findAll().deleteAllFromRealm();
        realm.copyToRealm(notifications);
        realm.copyToRealmOrUpdate(babblePlayer);
        realm.commitTransaction();
        userInfoViewInterface.downloadIntent();
    }

    @Override
    public void retriveNotificationsFromAmazon() {
        retriveNotificationsSubscrtion = Observable.fromCallable(() ->babblePlayer.retriveNotifications(context,babblePlayer.getuserAgeInMonth()))
                .doOnSubscribe(() -> userInfoViewInterface.displaySaveDialog())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<PaginatedScanList<Notification>>() {
                    @Override
                    public void onCompleted() {
                        saveNotifications(retrivenotifications);
                    }

                    @Override
                    public void onError(Throwable e) {
                        updateViewAfterError();
                    }

                    @Override
                    public void onNext(PaginatedScanList<Notification> notifications) {
                        updateViewAfterRetrivingNotificationList(notifications);
                    }
                });
    }

    private void updateViewAfterError(){
        userInfoViewInterface.dismissSaveDialog();
        userInfoViewInterface.displayErrorDialog(R.string.BabbleError,R.string.downloadError);
    }

    private void updateViewAfterRetrivingNotificationList(List<Notification>notifications){
        userInfoViewInterface.dismissSaveDialog();
        if (notifications.size() == 0){
            userInfoViewInterface.displayErrorDialog(R.string.BabbleError,R.string.noPromptsForUser);
        }else {
            retrivenotifications = notifications;
        }
    }

    @Override
    public void createBabblePlayer(BabblePlayer babblePlayer) {
        this.babblePlayer = babblePlayer;
    }

    @Override
    public void loadPlayerFromSharedPref() {
        BabblePlayer localBabblePlayer = new BabblePlayer();
       babblePlayer = localBabblePlayer.loadBabblePlayerFronSharedPref(context);
        userInfoViewInterface.displayUserInfo(babblePlayer);
    }

    @Override
    public void checkUserInput() {
        babblePlayer.setuserAgeInMonth();
        if (babblePlayer.checkIfPlayerIsValid()){
            updatePlayer();
        }else if (babblePlayer.checkZipCode()){
            userInfoViewInterface.displayErrorDialog(R.string.userZipCodeErrorTitle,R.string.userZipCodeError);
        }else if (babblePlayer.checkNameIsEmpty()){
            userInfoViewInterface.displayErrorDialog(R.string.userNameNameErrorTitle,R.string.userNameEmptyError);
        }else if (babblePlayer.checkNameIsTooLong()) {
            userInfoViewInterface.displayErrorDialog(R.string.userNameNameErrorTitle,R.string.userNameToLongError);
        }else if (babblePlayer.checkDate()){
            userInfoViewInterface.displayErrorDialog(R.string.userBirthdayErrorTitle,R.string.userBirthdayError);
        }else if (babblePlayer.getuserAgeInMonth() <= 0){
            userInfoViewInterface.displayErrorDialog(R.string.BabbleError,R.string.userNotBornYetError);
        }
    }

}
