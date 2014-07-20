package com.mediafire.sdk.config;

import java.util.concurrent.Executor;

/**
 * TODO: doc
 */
public interface MFPausableExecutorService extends Executor {
    public void pause();

    public void resume();

    public boolean isPaused();
}
