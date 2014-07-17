package com.mediafire.sdk.config;

import java.util.concurrent.Executor;

/**
 * Created by Chris Najar on 7/17/2014.
 */
public interface MFPausableExecutorService extends Executor {
    public void pause();

    public void resume();

    public boolean isPaused();
}
