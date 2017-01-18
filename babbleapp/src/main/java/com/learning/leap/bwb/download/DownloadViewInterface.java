package com.learning.leap.bwb.download;


public interface DownloadViewInterface {
    void downloadCompleted();
    void displayErrorMessage();
    void updateProgressBar(int progress);

}
