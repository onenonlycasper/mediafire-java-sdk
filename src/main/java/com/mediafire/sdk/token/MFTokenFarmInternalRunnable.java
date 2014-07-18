package com.mediafire.sdk.token;

import com.mediafire.sdk.http.MFHttpRunner;
import com.mediafire.sdk.http.MFRequest;
import com.mediafire.sdk.util.MFGenericCallback;

/**
 * TODO: doc
 */
public class MFTokenFarmInternalRunnable implements Runnable {

    private static final String TAG = MFTokenFarmInternalRunnable.class.getCanonicalName();
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
