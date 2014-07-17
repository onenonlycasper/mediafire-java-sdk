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

    public void doRequest(MFRequest mfRequest, MFGenericCallback<RunnerHolder> mfGenericCallback) {
        if (mfGenericCallback != null) {
            mfGenericCallback.jobStarted();
        }

        MFResponse mfResponse = null;
        try {
            mfHttpClientSetup.prepareMFRequestForHttpClient(mfRequest);
            mfResponse = mfHttpClient.sendRequest(mfRequest);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        mfHttpClientCleanup.returnToken(mfRequest);

        if (mfGenericCallback != null) {
            RunnerHolder runnerHolder = new RunnerHolder(mfRequest, mfResponse);
            mfGenericCallback.jobFinished(runnerHolder);
        }
    }

    public void doRequest(MFRequest mfRequest) {
        doRequest(mfRequest, null);
    }

    public class RunnerHolder {

        public MFRequest mfRequest;

        public MFResponse mfResponse;

        public RunnerHolder(MFRequest mfRequest, MFResponse mfResponse) {
            this.mfRequest = mfRequest;
            this.mfResponse = mfResponse;
        }

        public MFRequest getMfRequest() {
            return mfRequest;
        }

        public MFResponse getMfResponse() {
            return mfResponse;
        }
    }
}
