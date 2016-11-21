package com.learning.leap.beginningtobabble.helper;

import java.util.Date;

import io.realm.RealmObject;



public class AnswerNotification extends RealmObject {
    public Date mAnswerTime;
    public int mAnswerBucket;
    private int mAnswerHour;
    private int mAnswerMin;

    public Date getAnswerTime() {
        return mAnswerTime;
    }

    public void setAnswerTime(Date answerTime) {
        mAnswerTime = answerTime;
    }

    public int getAnswerHour() {
        return mAnswerHour;
    }

    public void setAnswerHour(int answerHour) {
        mAnswerHour = answerHour;
    }

    public int getAnswerMin() {
        return mAnswerMin;
    }

    public void setAnswerMin(int answerMin) {
        mAnswerMin = answerMin;
    }

    public int getAnswerBucket() {
        return mAnswerBucket;
    }

    public void setAnswerBucket(int answerBucket) {
        mAnswerBucket = answerBucket;
    }
}
