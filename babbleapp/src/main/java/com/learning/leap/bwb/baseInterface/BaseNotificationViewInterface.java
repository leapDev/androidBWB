package com.learning.leap.bwb.baseInterface;


public interface BaseNotificationViewInterface {
    void displayPrompt(String prompt);
    void playSound(String fileName);
    void playVideo(String fileName);
    void displaySoundButton();
    void displayVideoButton();
    void hideStopButton();
    void displayStopButton();
    void hideSoundButton();
    void hideVideoButton();
    void stopPlayer();
    String babyName();
}
