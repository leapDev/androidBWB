package com.learning.leap.bwb.userInfo;


import com.amazonaws.mobileconnectors.dynamodbv2.dynamodbmapper.DynamoDBMapper;
import com.learning.leap.bwb.BuildConfig;
import com.learning.leap.bwb.Player;
import com.learning.leap.bwb.R;
import com.learning.leap.bwb.helper.LocalLoadSaveHelper;
import com.learning.leap.bwb.research.ResearchNotifications;
import com.learning.leap.bwb.research.ResearchPlayers;
import com.learning.leap.bwb.utility.NetworkCheckerInterface;
import com.learning.leap.bwb.models.BabblePlayer;
import com.learning.leap.bwb.models.Notification;

import java.util.List;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import io.realm.Realm;

public class UserInfoPresenter{
   private UserInfoViewInterface userInfoViewInterface;
    private boolean newUser;
   private Player babblePlayer;
    private final CompositeDisposable disposables = new CompositeDisposable();
    private NetworkCheckerInterface networkCheckerInterface;
    private LocalLoadSaveHelper saveHelper;
    private Realm realm;
    private DynamoDBMapper mapper;

    public UserInfoPresenter(Boolean newUser, UserInfoViewInterface userInfoViewInterface, LocalLoadSaveHelper saveHelper, NetworkCheckerInterface networkCheckerInterface, Realm realm, DynamoDBMapper mapper){
        this.newUser = newUser;
        this.userInfoViewInterface = userInfoViewInterface;
        this.networkCheckerInterface = networkCheckerInterface;
        this.saveHelper = saveHelper;
        this.realm = realm;
        this.mapper = mapper;
    }


    public void onDestory() {
        disposables.clear();

    }

    private void updatePlayer() {
         disposables.add(babblePlayer.savePlayerObservable(mapper,networkCheckerInterface,saveHelper).subscribe(c -> updatePlayerOnCompleted(),
                 throwable -> userInfoViewInterface.displayErrorDialog(R.string.BabbleError, R.string.userSave)));
    }

    private void updatePlayerOnCompleted(){
        if (networkCheckerInterface.isConnected()) {
            if (newUser) {
                retriveNotificationsFromAmazon();
            } else {
                userInfoViewInterface.dismissActivity();
            }
        }else{
            userInfoViewInterface.displayErrorDialog(R.string.BabbleError,R.string.noConnection);
        }
    }


    private void retriveNotificationsFromAmazon() {
            Disposable notificationDisposable = babblePlayer.retriveNotifications(babblePlayer.getuserAgeInMonth(), mapper)
                    .doOnSubscribe(disposable -> userInfoViewInterface.displaySaveDialog())
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(this::updateViewAfterRetrivingNotificationList, throwable -> updateViewAfterError());
            disposables.add(notificationDisposable);

    }

    private void updateViewAfterError(){
        userInfoViewInterface.dismissSaveDialog();
        userInfoViewInterface.displayErrorDialog(R.string.BabbleError,R.string.downloadError);
    }

    private void updateViewAfterRetrivingNotificationList(List<Notification>notifications){
        userInfoViewInterface.dismissSaveDialog();
        if (notifications == null || notifications.size() == 0){
            userInfoViewInterface.displayErrorDialog(R.string.BabbleError,R.string.noPromptsForUser);
        }else {
            saveNotifications(notifications);
        }
    }

    private void updateNWViewAfterRetrivingNotificationList(List<ResearchNotifications>notifications){
        userInfoViewInterface.dismissSaveDialog();
        if (notifications == null || notifications.size() == 0){
            userInfoViewInterface.displayErrorDialog(R.string.BabbleError,R.string.noPromptsForUser);
        }else {
            saveNWNotifications(notifications);
        }
    }

    private void saveNotifications(List<Notification> notifications) {
        for (int i = 0; i< notifications.size(); i++){
            notifications.get(i).setId(i);
        }
        saveHelper.saveNotificationSize(notifications.size());
        realm.beginTransaction();
        realm.where(Notification.class).findAll().deleteAllFromRealm();
        realm.copyToRealm(notifications);
        babblePlayer.savePlayerToRealm();
        realm.commitTransaction();
        userInfoViewInterface.downloadIntent();
    }

    private void saveNWNotifications(List<ResearchNotifications> notifications) {
        for (int i = 0; i< notifications.size(); i++){
            notifications.get(i).setId(i);
        }
        saveHelper.saveNotificationSize(notifications.size());
        realm.beginTransaction();
        realm.where(Notification.class).findAll().deleteAllFromRealm();
        realm.copyToRealm(notifications);
        babblePlayer.savePlayerToRealm();
        realm.commitTransaction();
        userInfoViewInterface.downloadIntent();
    }


    public void createBabblePlayer(Player babblePlayer) {
        this.babblePlayer = babblePlayer;
    }

    public void loadPlayerFromSharedPref() {
        if (!BuildConfig.FLAVOR.equals("regular")) {
            ResearchPlayers researchPlayers = new ResearchPlayers();
            babblePlayer = researchPlayers.loadBabblePlayerFronSharedPref(saveHelper);
            userInfoViewInterface.displayUserInfo(babblePlayer);
        }else {
            BabblePlayer localBabblePlayer = new BabblePlayer();
            babblePlayer = localBabblePlayer.loadBabblePlayerFronSharedPref(saveHelper);
            userInfoViewInterface.displayUserInfo(babblePlayer);
        }

    }


    public void checkUserInput() {
        babblePlayer.setuserAgeInMonth();
        if (babblePlayer.checkIfPlayerIsValid()){
            updatePlayer();
        }else if (!babblePlayer.checkZipCode()){
            userInfoViewInterface.displayErrorDialog(R.string.userZipCodeErrorTitle,R.string.userZipCodeError);
        }else if (babblePlayer.checkNameIsEmpty()){
            userInfoViewInterface.displayErrorDialog(R.string.userNameNameErrorTitle,R.string.userNameEmptyError);
        }else if (babblePlayer.checkNameIsTooLong()) {
            userInfoViewInterface.displayErrorDialog(R.string.userNameNameErrorTitle,R.string.userNameToLongError);
        }else if (!babblePlayer.checkDate()){
            userInfoViewInterface.displayErrorDialog(R.string.userBirthdayErrorTitle,R.string.userBirthdayError);
        }else if (babblePlayer.getuserAgeInMonth() <= 0){
            userInfoViewInterface.displayErrorDialog(R.string.BabbleError,R.string.userNotBornYetError);
        }
    }

}
