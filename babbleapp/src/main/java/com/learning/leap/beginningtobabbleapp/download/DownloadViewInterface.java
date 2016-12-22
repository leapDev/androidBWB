package com.learning.leap.beginningtobabbleapp.download;


public interface DownloadViewInterface {
    void downloadCompleted();
    void displayErrorMessage();
    void updateProgressBar(int progress);

}
