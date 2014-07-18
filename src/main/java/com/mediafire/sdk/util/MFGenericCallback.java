package com.mediafire.sdk.util;

/**
* TODO: doc
*/
public interface MFGenericCallback<Param> {
    public void jobStarted();
    public void jobFinished(Param param);
}
