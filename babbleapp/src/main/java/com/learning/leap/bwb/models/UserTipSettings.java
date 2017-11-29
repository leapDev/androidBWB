package com.learning.leap.bwb.models;

import android.app.AlarmManager;
import android.content.Context;

import com.evernote.android.job.JobManager;
import com.evernote.android.job.JobRequest;
import com.learning.leap.bwb.PlayTodayJob;
import com.learning.leap.bwb.R;
import com.learning.leap.bwb.helper.ScheduleBucket;
import com.learning.leap.bwb.utility.Constant;
import com.learning.leap.bwb.utility.Utility;


public class UserTipSettings {
    private Context context;
    private int userNumberOfTipsIndex;
    private int startTimeIndex;
    private int endTimeIndex;
    private String[] startTimes;
    private String[] endTimes;
    private static final String[] maxTips = {"3","4","5","6","7","8","9","10"};
    private Boolean sendTipsToday;

    public UserTipSettings(Context context){
        this.context = context;
    }

    public void loadUserTipsSettings(){
        setStartTimes();
        setEndTimes();
        loadUserNumberOfTipsIndexFromSharedPref();
        loadEndTimeIndexFromShartedPref();
        loadStartTimeIndexFromSharedPref();
    }

    private void loadStartTimeIndexFromSharedPref(){
        startTimeIndex = Utility.readIntSharedPreferences(Constant.START_TIME,context);
    }

    private void loadEndTimeIndexFromShartedPref(){
        endTimeIndex = Utility.readIntSharedPreferences(Constant.END_TIME,context);
    }

    private void loadUserNumberOfTipsIndexFromSharedPref(){
        userNumberOfTipsIndex = Utility.readIntSharedPreferences(Constant.NUM_OF_TIPS_INDEX,context);
    }

    private void setStartTimes(){
        startTimes = context.getResources().getStringArray(R.array.start_times_settings_array);
    }

    private void setEndTimes(){
        endTimes = context.getResources().getStringArray(R.array.end_times_tips_settings_array);
    }

    private Boolean hidePlusButton(int index, int max) {
        return index == max;
    }
    private Boolean hideMinusButton(int index) {
        return index == 0;
    }

    public Boolean hideStartTimePlusButton(){
        return hidePlusButton(startTimeIndex,startTimes.length -1);
    }

    public Boolean hideStartTimeMinusButton(){
        return hideMinusButton(startTimeIndex);
    }

    public Boolean hideEndTimePlusButton(){
        return hidePlusButton(endTimeIndex, endTimes.length-1);
    }

    public Boolean hideEndTimeMinusButton(){
        return hideMinusButton(endTimeIndex);
    }

    public Boolean hideUserNumberOfTipsPlusButton(){
        return hidePlusButton(userNumberOfTipsIndex,maxTips.length -1);
    }

    public Boolean hideUserNumberOfTipsMinusButton(){
        return hideMinusButton(userNumberOfTipsIndex);
    }

    private int minusButtonTapped(int index){
        if (index != 0){
            index--;
        }
        return index;
    }

    public void startTimeMinusButtonHasBeenTapped(){
        startTimeIndex = minusButtonTapped(startTimeIndex);
    }

    public void startTimePlusButtonHasBeenTapped(){
        startTimeIndex++;
        if (startTimeIndex > endTimeIndex){
            endTimeIndex = startTimeIndex;
        }
    }

    public void endTimeMinusButtonHasBeenTapped(){
        endTimeIndex = minusButtonTapped(endTimeIndex);
        if (startTimeIndex > endTimeIndex){
            endTimeIndex = startTimeIndex;
        }
    }

    public void endTimePlusButtonHasBeenTapped(){
        endTimeIndex++;
    }

    public void userMaxNumberOfTipsMinusButtonHasBeenTapped(){
        userNumberOfTipsIndex = minusButtonTapped(userNumberOfTipsIndex);
    }

    public void userMaxNumberOfTipsPlusButtonHasBeenTapped(){
        userNumberOfTipsIndex++;
    }

    public String startTimeAtIndex(){
        return startTimes[startTimeIndex];
    }

    public String endTimeAtIndex(){
        return endTimes[endTimeIndex];
    }

    public void loadTurnOnTips(){
       sendTipsToday =  Utility.readBoolSharedPreferences(Constant.SEND_TIPS_TODAY,context);
    }

    public void saveTurnOnTips(){
        if (!sendTipsToday){
            JobManager.instance().cancelAllForTag(PlayTodayJob.PLAY_TODAY);
        }else {
           PlayTodayJob.schedule();
        }
        Utility.writeBoolenSharedPreferences(Constant.SEND_TIPS_TODAY, sendTipsToday,context);
    }

    public Boolean endTimeIsBeforeStart(){
        return startTimeIndex > 6 && startTimeIndex > endTimeIndex && endTimeIndex < startTimeIndex -6;

    }
    public void  saveIndexes(){
        Utility.writeIntSharedPreferences(Constant.START_TIME,startTimeIndex,context);
        Utility.writeIntSharedPreferences(Constant.END_TIME,endTimeIndex,context);
        int userMaxTipInt = Integer.parseInt(userMaxNumberOfTipsAtIndex());
        Utility.writeIntSharedPreferences(Constant.NUM_OF_TIPS_INDEX,userNumberOfTipsIndex,context);
        Utility.writeIntSharedPreferences(Constant.NUM_OF_TIPS,userMaxTipInt,context);
    }
    public String userMaxNumberOfTipsAtIndex(){
        return maxTips[userNumberOfTipsIndex];
    }

    public Boolean getSendTipsToday() {
        return sendTipsToday;
    }

    public void setSendTipsToday(Boolean sendTipsToday) {
        this.sendTipsToday = sendTipsToday;
    }


}
