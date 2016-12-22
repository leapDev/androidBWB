package com.learning.leap.beginningtobabbleapp.notification;

import com.learning.leap.beginningtobabbleapp.baseInterface.BaseNotificationViewInterface;
import com.learning.leap.beginningtobabbleapp.baseInterface.ViewInterface;

public interface NotificationViewViewInterface extends ViewInterface, BaseNotificationViewInterface {
    void hideNextButton();
    void hidePreviousButton();
    void displayNextButton();
    void displayPreviousButton();
}
