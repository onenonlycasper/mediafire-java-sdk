package com.mediafire.sdk.config;

import java.util.concurrent.ExecutorService;

/**
 * Created by Chris Najar on 7/17/2014.
 */
public interface MFPausableExecutorService extends ExecutorService {
    public void pause();

    public void resume();

    public boolean isPaused();
}
