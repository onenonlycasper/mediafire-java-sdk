package com.mediafire.sdk.http;

/**
* Created by Chris Najar on 7/17/2014.
*/
public interface MFGenericCallback<Param> {
    public void jobStarted();
    public void jobFinished(Param param);
}
