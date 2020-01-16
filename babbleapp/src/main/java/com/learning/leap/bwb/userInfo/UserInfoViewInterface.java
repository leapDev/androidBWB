package com.learning.leap.bwb.userInfo;

import com.learning.leap.bwb.Player;
import com.learning.leap.bwb.models.BabblePlayer;


public interface UserInfoViewInterface{
    void displayErrorDialog(int dialogTitle,int dialogMessage);
    void dismissActivity();
    void downloadIntent();
    void displayUserInfo(Player babblePlayer);
    void displaySaveDialog();
    void dismissSaveDialog();
    String getUserGender();

}
