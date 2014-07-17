package com.mediafire.sdk.http;

import com.mediafire.sdk.config.MFConfiguration;
import com.mediafire.sdk.token.MFTokenFarmCallback;

import java.io.UnsupportedEncodingException;

/**
 * Created by Chris Najar on 7/17/2014.
 */
public class MFHttpRunnable implements Runnable {
    private MFTokenFarmCallback mfTokenFarmCallback;
    private MFConfiguration mfConfiguration;
    private MFRequest mfRequest;

    public MFHttpRunnable(MFTokenFarmCallback mfTokenFarmCallback, MFConfiguration mfConfiguration, MFRequest mfRequest) {
        this.mfTokenFarmCallback = mfTokenFarmCallback;
        this.mfConfiguration = mfConfiguration;
        this.mfRequest = mfRequest;
    }

    @Override
    public void run() {
        MFHttpSetup mfHttpSetup = new MFHttpSetup(mfTokenFarmCallback, mfConfiguration);
        MFHttpClient mfHttpClient = new MFHttpClient(mfConfiguration);
        MFHttpCleanup mfHttpCleanup = new MFHttpCleanup(mfTokenFarmCallback, mfConfiguration);

        try {
            mfHttpSetup.prepareMFRequestForHttpClient(mfRequest);
            mfHttpClient.sendRequest(mfRequest);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        mfHttpCleanup.returnToken(mfRequest);
    }

}
