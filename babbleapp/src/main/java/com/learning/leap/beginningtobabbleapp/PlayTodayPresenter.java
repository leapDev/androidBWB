package com.learning.leap.beginningtobabbleapp;

import com.learning.leap.beginningtobabbleapp.baseInterface.BaseNotificationPresenter;
import com.learning.leap.beginningtobabbleapp.models.Notification;
import com.learning.leap.beginningtobabbleapp.notification.NotificaitonPresenter;
import com.learning.leap.beginningtobabbleapp.notification.NotificationViewViewInterface;

import io.realm.Realm;
import io.realm.RealmResults;
import rx.Subscriber;

/**
 * Created by ryangunn on 12/26/16.
 */

public class PlayTodayPresenter extends NotificaitonPresenter {


    @Override
    protected void getRealmResults() {
//        super.getRealmResults();
        Notification notification = new Notification();
        notificationListSubscription = notification.getPlayTodayFromRealm((Realm.getDefaultInstance()))
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
}

