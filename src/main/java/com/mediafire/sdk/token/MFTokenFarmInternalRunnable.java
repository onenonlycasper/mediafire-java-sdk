package com.mediafire.sdk.token;

import com.mediafire.sdk.http.MFHttpRunner;
import com.mediafire.sdk.http.MFRequest;
import com.mediafire.sdk.util.MFGenericCallback;

/**
 * Created by Chris Najar on 7/17/2014.
 */
public class MFTokenFarmInternalRunnable implements Runnable {

    private MFHttpRunner mfHttpRunner;
    private MFRequest mfRequest;
    private MFGenericCallback<Void> mfGenericCallback;

    public MFTokenFarmInternalRunnable(MFHttpRunner mfHttpRunner, MFRequest mfRequest, MFGenericCallback<Void> mfGenericCallback) {
        this.mfHttpRunner = mfHttpRunner;
        this.mfRequest = mfRequest;
        this.mfGenericCallback = mfGenericCallback;
    }

    public MFTokenFarmInternalRunnable(MFHttpRunner mfHttpRunner, MFRequest mfRequest) {
        this(mfHttpRunner, mfRequest, null);
    }

    @Override
    public void run() {
        if (mfGenericCallback != null) {
            mfGenericCallback.jobStarted();
        }

        mfHttpRunner.doRequest(mfRequest);

        if (mfGenericCallback != null) {
            mfGenericCallback.jobFinished(null);
        }
    }
}
