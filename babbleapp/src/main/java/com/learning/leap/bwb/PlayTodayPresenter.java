package com.learning.leap.bwb;

import com.learning.leap.bwb.models.Notification;
import com.learning.leap.bwb.notification.NotificaitonPresenter;

import org.reactivestreams.Subscriber;

import io.reactivex.disposables.Disposable;
import io.realm.Realm;
import io.realm.RealmResults;


public class PlayTodayPresenter extends NotificaitonPresenter {
    private Disposable playTodayDisopsable;

    @Override
    public void onDestory() {
        if (playTodayDisopsable != null && !playTodayDisopsable.isDisposed()){
            playTodayDisopsable.dispose();
        }
        super.onDestory();
    }

    @Override
    protected void getRealmResults() {
        Notification notification = new Notification();
        playTodayDisopsable = notification.getPlayTodayFromRealm(Realm.getDefaultInstance()).subscribe(this::setNotifications, Throwable::printStackTrace);
    }
}

