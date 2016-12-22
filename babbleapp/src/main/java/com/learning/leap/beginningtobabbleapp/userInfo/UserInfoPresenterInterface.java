package com.learning.leap.beginningtobabbleapp.userInfo;

import com.learning.leap.beginningtobabbleapp.baseInterface.LifeCycleInterface;
import com.learning.leap.beginningtobabbleapp.models.BabblePlayer;
import com.learning.leap.beginningtobabbleapp.models.Notification;

import java.util.List;

/**
 * Created by ryangunn on 12/17/16.
 */

public interface UserInfoPresenterInterface extends LifeCycleInterface {
    void updatePlayer();
    void saveNotifications(List<Notification> notifications);
    void retriveNotificationsFromAmazon();
    void createBabblePlayer(BabblePlayer babblePlayer);
    void checkUserInput();
    void loadPlayerFromSharedPref();
}
