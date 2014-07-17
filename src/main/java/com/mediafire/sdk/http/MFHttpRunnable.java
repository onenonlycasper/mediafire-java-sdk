package com.mediafire.sdk.http;

import com.mediafire.sdk.config.MFConfiguration;
import com.mediafire.sdk.token.MFTokenDistributor;

import java.io.UnsupportedEncodingException;

/**
 * Created by Chris Najar on 7/17/2014.
 */
public class MFHttpRunnable implements Runnable {
    private MFTokenDistributor mfTokenDistributor;
    private MFConfiguration mfConfiguration;
    private MFRequest mfRequest;

    public MFHttpRunnable(MFTokenDistributor mfTokenDistributor, MFConfiguration mfConfiguration, MFRequest mfRequest) {
        this.mfTokenDistributor = mfTokenDistributor;
        this.mfConfiguration = mfConfiguration;
        this.mfRequest = mfRequest;
    }

    @Override
    public void run() {
        MFHttpSetup mfHttpSetup = new MFHttpSetup(mfTokenDistributor, mfConfiguration);
        MFHttpClient mfHttpClient = new MFHttpClient(mfConfiguration);
        MFHttpCleanup mfHttpCleanup = new MFHttpCleanup(mfTokenDistributor, mfConfiguration);

        try {
            mfHttpSetup.prepareMFRequestForHttpClient(mfRequest);
            mfHttpClient.sendRequest(mfRequest);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        mfHttpCleanup.returnToken(mfRequest);
    }
}
