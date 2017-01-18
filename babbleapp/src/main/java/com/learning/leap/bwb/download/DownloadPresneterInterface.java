package com.learning.leap.bwb.download;

import com.learning.leap.bwb.baseInterface.LifeCycleInterface;

public interface DownloadPresneterInterface extends LifeCycleInterface {
    void errorHasOccured();
    void downloadCompleted();
    void updateProgress(int prgress);
    void onPaused();

}
