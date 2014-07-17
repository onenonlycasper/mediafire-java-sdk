package com.mediafire.sdk.http;

import com.mediafire.sdk.config.MFConfiguration;
import com.mediafire.sdk.token.MFTokenFarmCallback;

import java.io.UnsupportedEncodingException;

/**
 * Created by Chris Najar on 7/17/2014.
 */
public class MFHttpRunner {
    private MFTokenFarmCallback mfTokenFarmCallback;
    private MFConfiguration mfConfiguration;
    private MFHttpSetup mfHttpSetup; // = new MFHttpSetup(mfTokenFarmCallback, mfConfiguration);
    private MFHttpClient mfHttpClient; // = new MFHttpClient(mfConfiguration);
    private MFHttpCleanup mfHttpCleanup; // = new MFHttpCleanup(mfTokenFarmCallback, mfConfiguration);

    public MFHttpRunner(MFTokenFarmCallback mfTokenFarmCallback, MFConfiguration mfConfiguration) {
        this.mfTokenFarmCallback = mfTokenFarmCallback;
        this.mfConfiguration = mfConfiguration;
        this.mfHttpSetup = new MFHttpSetup(mfTokenFarmCallback, mfConfiguration);
        this.mfHttpClient = new MFHttpClient(mfConfiguration);
        this.mfHttpCleanup = new MFHttpCleanup(mfTokenFarmCallback, mfConfiguration);
    }

    public void doRequest(MFRequest mfRequest) {

        try {
            mfHttpSetup.prepareMFRequestForHttpClient(mfRequest);
            mfHttpClient.sendRequest(mfRequest);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        mfHttpCleanup.returnToken(mfRequest);
    }

}
