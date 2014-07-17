package com.mediafire.sdk.http;

import com.mediafire.sdk.config.MFConfiguration;
import com.mediafire.sdk.token.MFTokenFarmCallback;

import java.io.UnsupportedEncodingException;

/**
 * Created by Chris Najar on 7/17/2014.
 */
public class MFHttpRunner {
    private MFHttpClientSetup mfHttpClientSetup;
    private MFHttpClient mfHttpClient;
    private MFHttpClientCleanup mfHttpClientCleanup;

    public MFHttpRunner(MFConfiguration mfConfiguration, MFTokenFarmCallback mfTokenFarmCallback) {
        this.mfHttpClientSetup = new MFHttpClientSetup(mfTokenFarmCallback, mfConfiguration);
        this.mfHttpClient = new MFHttpClient(mfConfiguration);
        this.mfHttpClientCleanup = new MFHttpClientCleanup(mfTokenFarmCallback, mfConfiguration);
    }

    public void doRequest(MFRequest mfRequest, MFHttpRunnerCallback callback) {
        if (callback != null) {
            callback.jobStarted();
        }
        MFResponse mfResponse = null;
        try {
            mfHttpClientSetup.prepareMFRequestForHttpClient(mfRequest);
            mfResponse = mfHttpClient.sendRequest(mfRequest);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        mfHttpClientCleanup.returnToken(mfRequest);

        if (callback != null) {
            callback.jobFinished(mfRequest, mfResponse);
        }
    }

    public void doRequest(MFRequest mfRequest) {
        doRequest(mfRequest, null);
    }
}
