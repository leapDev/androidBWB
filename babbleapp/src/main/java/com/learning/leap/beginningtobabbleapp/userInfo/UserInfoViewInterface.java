package com.learning.leap.beginningtobabbleapp.userInfo;

import com.learning.leap.beginningtobabbleapp.models.BabblePlayer;


public interface UserInfoViewInterface{
    void displayErrorDialog(int dialogTitle,int dialogMessage);
    void dismissActivity();
    void downloadIntent();
    void displayUserInfo(BabblePlayer babblePlayer);
    void displaySaveDialog();
    void dismissSaveDialog();

}
