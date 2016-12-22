package com.learning.leap.beginningtobabbleapp.download;

import com.learning.leap.beginningtobabbleapp.baseInterface.LifeCycleInterface;

public interface DownloadPresneterInterface extends LifeCycleInterface {
    void errorHasOccured();
    void downloadCompleted();
    void updateProgress(int prgress);
    void onPaused();

}
