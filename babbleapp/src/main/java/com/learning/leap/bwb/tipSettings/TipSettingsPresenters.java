package com.learning.leap.bwb.tipSettings;

import android.content.Context;

import com.learning.leap.bwb.helper.AnswerNotification;
import com.learning.leap.bwb.models.UserTipSettings;
import com.learning.leap.bwb.utility.Constant;
import com.learning.leap.bwb.utility.Utility;

import io.realm.Realm;

/**
 * Created by ryangunn on 12/25/16.
 */

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
        displayStartTime();
        displayStartTimePlusAndMinusButton();
    }

    public void startTimePlusButtonPressed(){
        displaySaveButton();
        userTipSettings.startTimePlusButtonHasBeenTapped();
        displayStartTime();
        displayStartTimePlusAndMinusButton();
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
        displayEndTime();
      displayEndTimePlusAndMinusButton();
    }

    public void endTimePlusButtonPressed(){
        displaySaveButton();
        userTipSettings.endTimePlusButtonHasBeenTapped();
        displayEndTime();
        displayEndTimePlusAndMinusButton();

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
