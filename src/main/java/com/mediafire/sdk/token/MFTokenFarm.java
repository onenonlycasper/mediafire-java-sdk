package com.mediafire.sdk.token;

import com.mediafire.sdk.http.MFApi;
import com.mediafire.sdk.http.MFHost;
import com.mediafire.sdk.config.MFConfiguration;
import com.mediafire.sdk.http.MFHttpRequest;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * Created by Chris Najar on 7/16/2014.
 */
public final class MFTokenFarm implements MFTokenDistributor {
    private final MFConfiguration mfConfiguration;

    private BlockingQueue<MFSessionToken> mfSessionTokens;
    private MFUploadActionToken mfUploadActionToken;
    private MFImageActionToken mfImageActionToken;

    // receive new token locks
    private final Object imageTokenLock = new Object();
    private final Object uploadTokenLock = new Object();
    private final Object sessionTokenLock = new Object();

    // borrow token locks
    private final Lock lockBorrowImageToken = new ReentrantLock();
    private final Lock lockBorrowUploadToken = new ReentrantLock();
    private final Condition conditionImageTokenNotExpired = lockBorrowImageToken.newCondition();
    private final Condition conditionUploadTokenNotExpired = lockBorrowUploadToken.newCondition();

    public MFTokenFarm(MFConfiguration mfConfiguration) {
        this.mfConfiguration = mfConfiguration;
    }

    public void getNewSessionToken() {
        Map<String, String> requestParameters = new LinkedHashMap<String, String>();
        requestParameters.put("token_version", "2");
        MFHttpRequest mfHttpRequest = new MFHttpRequest(MFHost.LIVE_HTTPS, MFApi.USER_GET_SESSION_TOKEN, requestParameters);

    }

    private void getNewImageActionToken() {
        Map<String, String> requestParameters = new LinkedHashMap<String, String>();
        requestParameters.put("lifespan", "1440");
        requestParameters.put("type", "image");
        MFHttpRequest mfHttpRequest = new MFHttpRequest(MFHost.LIVE_HTTP, MFApi.USER_GET_ACTION_TOKEN, requestParameters);
    }

    private void getNewUploadActionToken() {
        Map<String, String> requestParameters = new LinkedHashMap<String, String>();
        requestParameters.put("lifespan", "1440");
        requestParameters.put("type", "upload");
        MFHttpRequest mfHttpRequest = new MFHttpRequest(MFHost.LIVE_HTTP, MFApi.USER_GET_ACTION_TOKEN, requestParameters);
    }

    public void shutdown() {
        mfSessionTokens.clear();
        mfUploadActionToken = null;
        mfImageActionToken = null;
    }

    @Override
    public void returnSessionToken(MFSessionToken sessionToken) {
        if (sessionToken == null) {
            getNewSessionToken();
            return;
        }

        try {
            mfSessionTokens.put(sessionToken);
            return;
        } catch (InterruptedException e) { }

        getNewSessionToken();
    }

    @Override
    public void receiveNewSessionToken(MFSessionToken sessionToken) {
        synchronized (sessionTokenLock) {
            // store the session token if it is valid
        }
    }

    @Override
    public void receiveNewImageActionToken(MFImageActionToken uploadActionToken) {
        synchronized (imageTokenLock) {
            // store the image token if it is valid
        }
    }

    @Override
    public void receiveNewUploadActionToken(MFUploadActionToken uploadActionToken) {
        synchronized (uploadTokenLock) {
            // store the upload token if it is valid
        }
    }

    @Override
    public MFSessionToken borrowSessionToken() {
        MFSessionToken sessionToken = null;
        try {
            sessionToken = mfSessionTokens.take();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return sessionToken;
    }

    @Override
    public MFUploadActionToken borrowUploadActionToken() {
        // lock and fetch new token if necessary
        lockBorrowUploadToken.lock();
        // fetch new action token if token is null or expired
        if (mfUploadActionToken == null || mfUploadActionToken.isExpired()) {
            getNewUploadActionToken();
        }

        try {
            // wait while we get an image action token, condition is that image
            // action token is null or action token is expired or action token
            // string is null
            while (mfUploadActionToken == null ||
                    mfUploadActionToken.isExpired() ||
                    mfUploadActionToken.getTokenString() == null) {
                conditionUploadTokenNotExpired.await(1, TimeUnit.SECONDS);
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            lockBorrowUploadToken.unlock();
        }
        return mfUploadActionToken;
    }

    @Override
    public MFImageActionToken borrowImageActionToken() {
        // lock and fetch new token if necessary
        lockBorrowImageToken.lock();
        if (mfImageActionToken == null || mfImageActionToken.isExpired()) {
            getNewImageActionToken();
        }
        try {
            // wait while we get an image action token, condition is that image
            // action token is null or action token is expired or action token
            // string is null
            while (mfImageActionToken == null ||
                    mfImageActionToken.isExpired() ||
                    mfImageActionToken.getTokenString() == null) {
                conditionImageTokenNotExpired.await(1, TimeUnit.SECONDS);
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            // attach new one to apiRequestObject
            lockBorrowImageToken.unlock();
        }
        return mfImageActionToken;
    }
}
