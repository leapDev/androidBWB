package com.learning.leap.bwb.notification;

import com.learning.leap.bwb.baseInterface.BaseNotificationPresenter;
import com.learning.leap.bwb.models.Notification;

import io.reactivex.disposables.Disposable;
import io.realm.Realm;


public class NotificaitonPresenter extends BaseNotificationPresenter {

    @Override
    public void getRealmResults() {
        babyName = baseNotificationViewInterface.babyName();
        Notification notification = new Notification();
        Disposable disposable = notification.getNotificationFromRealm(Realm.getDefaultInstance()).subscribe(this::setNotifications, Throwable::printStackTrace);
        disposables.add(disposable);
    }
}
