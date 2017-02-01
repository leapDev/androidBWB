package com.learning.leap.bwb.tipSettings;

import android.content.Context;

import com.learning.leap.bwb.helper.AnswerNotification;
import com.learning.leap.bwb.models.UserTipSettings;
import com.learning.leap.bwb.utility.Constant;
import com.learning.leap.bwb.utility.Utility;

import io.realm.Realm;


public class TipSettingsPresenters {
   private Context context;
   private UserTipSettings userTipSettings;
    private TipSettingsViewInterface tipSettingsViewInterface;

    public TipSettingsPresenters(Context context, TipSettingsViewInterface tipSettingsViewInterface){
        this.context = context;
        this.tipSettingsViewInterface = tipSettingsViewInterface;
    }

    public void onCreate(){
        userTipSettings = new UserTipSettings(context);
        userTipSettings.loadUserTipsSettings();
        userTipSettings.loadTurnOnTips();
        tipSettingsViewInterface.setTipSwitch(userTipSettings.getSendTipsToday());
        displayStartTime();
        displayEndTime();
        displayMaxNumberOfTips();
    }

    public void save() {
        if (!userTipSettings.endTimeIsBeforeStart()){
            userTipSettings.saveIndexes();
            userTipSettings.saveTurnOnTips();
            tipSettingsViewInterface.saveCompleted();
            Utility.addCustomEvent(Constant.CHANGED_NOTIFICATION_SETTINGS);
            deleteAnserNotifications();
            tipSettingsViewInterface.saveCompleted();
        }else {
            tipSettingsViewInterface.displayErrorMessage();
        }
    }

    private void deleteAnserNotifications(){
        Realm.getDefaultInstance().beginTransaction();
        Realm.getDefaultInstance().where(AnswerNotification.class).findAll().deleteAllFromRealm();
        Realm.getDefaultInstance().commitTransaction();
    }

    public void startTimeMinusButtonPressed(){
        displaySaveButton();
        userTipSettings.startTimeMinusButtonHasBeenTapped();
        updateStartTime();
    }

    public void startTimePlusButtonPressed(){
        displaySaveButton();
        userTipSettings.startTimePlusButtonHasBeenTapped();
        updateStartTime();
        updateEndTime();
    }

    private void displayStartTimePlusAndMinusButton(){
        if (userTipSettings.hideStartTimePlusButton()){
            tipSettingsViewInterface.hideStartTimePlusSigned();
        }else {
            tipSettingsViewInterface.displayStartTimePlusSigned();
        }

        if (userTipSettings.hideStartTimeMinusButton()){
            tipSettingsViewInterface.hideStartTimeMinusSigned();
        }else {
            tipSettingsViewInterface.displayStartTimeMinusSigned();
        }
    }

    public void endTimeMinusButtonPressed(){
        displaySaveButton();
        userTipSettings.endTimeMinusButtonHasBeenTapped();
        updateStartTime();
        updateEndTime();
    }

    public void endTimePlusButtonPressed(){
        displaySaveButton();
        userTipSettings.endTimePlusButtonHasBeenTapped();
        updateEndTime();

    }

    private void updateEndTime(){
        displayEndTime();
        displayEndTimePlusAndMinusButton();
    }

    private void updateStartTime(){
        displayStartTime();
        displayStartTimePlusAndMinusButton();
    }

    private void displayEndTimePlusAndMinusButton(){
        if (userTipSettings.hideEndTimePlusButton()){
            tipSettingsViewInterface.hideEndTimePlusSigned();
        }else {
            tipSettingsViewInterface.displayEndTimePlusSigned();
        }

        if (userTipSettings.hideEndTimeMinusButton()){
            tipSettingsViewInterface.hideEndTimeMinusSigned();
        }else {
            tipSettingsViewInterface.displayEndTimeMinusSigned();
        }
    }

    public void userMaxTipMinusButtonPressed(){
        displaySaveButton();
        userTipSettings.userMaxNumberOfTipsMinusButtonHasBeenTapped();
        displayMaxNumberOfTips();
        displayMaxTipsPlusAndMinusButton();
    }


    public void userMaxTipsPlusButtonPressed(){
        displaySaveButton();
        userTipSettings.userMaxNumberOfTipsPlusButtonHasBeenTapped();
        displayMaxNumberOfTips();
        displayMaxTipsPlusAndMinusButton();
    }

    private void displayMaxTipsPlusAndMinusButton(){
        if (userTipSettings.hideUserNumberOfTipsPlusButton()){
            tipSettingsViewInterface.hideMaxNumberOfTipsPlusSigned();
        }else {
            tipSettingsViewInterface.displayMaxNumberOfTipsPlusSigned();
        }

        if (userTipSettings.hideUserNumberOfTipsMinusButton()){
            tipSettingsViewInterface.hideMaxNumberOfTipsMinusSigned();
        }else {
            tipSettingsViewInterface.displayMaxNumberOfTipsMinusSigned();
        }

    }

    public void turnOfSwitchChangedListner(boolean turnOff){
        displaySaveButton();
        userTipSettings.setSendTipsToday(turnOff);
    }

    private void displayEndTime(){
        tipSettingsViewInterface.displayEndTimeAtIndex(userTipSettings.endTimeAtIndex());
    }

    private void displayStartTime(){
        tipSettingsViewInterface.displayStartTimeAtIndex(userTipSettings.startTimeAtIndex());
    }

    private void displayMaxNumberOfTips(){
        tipSettingsViewInterface.displayUserMaxTipsAtIndex(userTipSettings.userMaxNumberOfTipsAtIndex());
    }

    private void displaySaveButton(){
        tipSettingsViewInterface.displaySaveButton();
    }

}
