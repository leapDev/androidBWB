package com.learning.leap.beginningtobabbleapp.baseInterface;


public interface BaseNotificationViewInterface {
    void displayPrompt(String prompt);
    void playSound(String fileName);
    void playVideo(String fileName);
    void displaySoundButton();
    void displayVideoButton();
    void hideSoundButton();
    void hideVideoButton();
}