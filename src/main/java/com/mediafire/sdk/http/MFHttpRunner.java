package com.mediafire.sdk.http;

import com.mediafire.sdk.config.MFConfiguration;
import com.mediafire.sdk.token.MFTokenFarmCallback;

import java.io.UnsupportedEncodingException;

/**
 * Created by Chris Najar on 7/17/2014.
 */
public class MFHttpRunner {
    private static final String TAG = MFHttpRunner.class.getCanonicalName();
    private final MFConfiguration mfConfiguration;
    private MFHttpClientSetup mfHttpClientSetup;
    private MFHttpClient mfHttpClient;
    private MFHttpClientCleanup mfHttpClientCleanup;

    public MFHttpRunner(MFConfiguration mfConfiguration, MFTokenFarmCallback mfTokenFarmCallback) {
        this.mfConfiguration = mfConfiguration;
        this.mfHttpClientSetup = new MFHttpClientSetup(mfTokenFarmCallback, mfConfiguration);
        this.mfHttpClient = new MFHttpClient(mfConfiguration);
        this.mfHttpClientCleanup = new MFHttpClientCleanup(mfTokenFarmCallback, mfConfiguration);
    }

    public MFResponse doRequest(MFRequest mfRequest) {
        mfConfiguration.getMfLogger().logMessage(TAG, "doRequest()");

        MFResponse mfResponse = null;
        try {
            mfHttpClientSetup.prepareMFRequestForHttpClient(mfRequest);
            mfResponse = mfHttpClient.sendRequest(mfRequest);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        mfConfiguration.getMfLogger().logApiError(TAG, mfRequest, mfResponse);

        mfHttpClientCleanup.returnToken(mfRequest, mfResponse);

        return mfResponse;

    }
}
