package com.mediafire.sdk.config;

/**
 * Created by Chris Najar on 7/17/2014.
 */
public interface MFPausable {
    public void pause();

    public void resume();

    public boolean isPaused();
}
