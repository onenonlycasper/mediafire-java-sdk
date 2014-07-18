package com.mediafire.sdk.token;

import com.mediafire.sdk.api_responses.ApiResponse;
import com.mediafire.sdk.config.MFConfiguration;
import com.mediafire.sdk.http.*;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * Created by Chris Najar on 7/16/2014.
 */
public final class MFTokenFarm implements MFTokenFarmCallback {
    private static final String TAG = MFTokenFarm.class.getCanonicalName();
    private final MFConfiguration mfConfiguration;
    private final MFHttpRunner mfHttpRunner;
    private final int minimumSessionTokens;
    private final int maximumSessionTokens;

    private BlockingQueue<MFSessionToken> mfSessionTokens;
    private MFUploadActionToken mfUploadActionToken;
    private MFImageActionToken mfImageActionToken;

    // borrow token locks
    private final Lock lockBorrowImageToken = new ReentrantLock();
    private final Lock lockBorrowUploadToken = new ReentrantLock();
    private final Condition conditionImageTokenNotExpired = lockBorrowImageToken.newCondition();
    private final Condition conditionUploadTokenNotExpired = lockBorrowUploadToken.newCondition();

    public MFTokenFarm(MFConfiguration mfConfiguration) {
        this.mfConfiguration = mfConfiguration;
        this.minimumSessionTokens = mfConfiguration.getMinimumSessionTokens();
        this.maximumSessionTokens = mfConfiguration.getMaximumSessionTokens();
        this.mfSessionTokens = new LinkedBlockingQueue<MFSessionToken>(maximumSessionTokens);
        this.mfHttpRunner = new MFHttpRunner(mfConfiguration, this);
    }

    public MFHttpRunner getMfHttpRunner() {
        return mfHttpRunner;
    }

    private boolean canGetSessionToken() {
        Map<String, String> requestParameters = new LinkedHashMap<String, String>();
        requestParameters.put("token_version", "2");
        MFRequest mfRequest = new MFRequest(MFHost.LIVE_HTTPS, MFApi.USER_GET_SESSION_TOKEN, requestParameters);
        MFResponse mfResponse = mfHttpRunner.doRequest(mfRequest);

        if (mfResponse == null) {
            return false;
        }

        ApiResponse apiResponse = mfResponse.getResponseObject(ApiResponse.class);
        if (apiResponse.hasError()) {
            return false;
        }

        return true;
    }

    private void getNewSessionToken() {
        mfConfiguration.getMfLogger().logMessage(TAG, "getNewSessionToken()");

        Map<String, String> requestParameters = new LinkedHashMap<String, String>();
        requestParameters.put("token_version", "2");
        MFRequest mfRequest = new MFRequest(MFHost.LIVE_HTTPS, MFApi.USER_GET_SESSION_TOKEN, requestParameters);
        mfHttpRunner.doRequest(mfRequest);
    }

    private void getNewImageActionToken() {
        mfConfiguration.getMfLogger().logMessage(TAG, "getNewImageActionToken()");

        Map<String, String> requestParameters = new LinkedHashMap<String, String>();
        requestParameters.put("lifespan", "1440");
        requestParameters.put("type", "image");
        MFRequest mfRequest = new MFRequest(MFHost.LIVE_HTTP, MFApi.USER_GET_ACTION_TOKEN, requestParameters);
        mfHttpRunner.doRequest(mfRequest);
    }

    private void getNewUploadActionToken() {
        mfConfiguration.getMfLogger().logMessage(TAG, "getNewUploadActionToken()");

        Map<String, String> requestParameters = new LinkedHashMap<String, String>();
        requestParameters.put("lifespan", "1440");
        requestParameters.put("type", "upload");
        MFRequest mfRequest = new MFRequest(MFHost.LIVE_HTTP, MFApi.USER_GET_ACTION_TOKEN, requestParameters);
        mfHttpRunner.doRequest(mfRequest);
    }

    public void shutdown() {
        mfConfiguration.getMfLogger().logMessage(TAG, "shutdown()");

        mfSessionTokens.clear();
        mfUploadActionToken = null;
        mfImageActionToken = null;
    }

    public void startup() {
        mfConfiguration.getMfLogger().logMessage(TAG, "startup()");

        for (int i = 0; i < mfSessionTokens.remainingCapacity(); i++) {
            mfConfiguration.getMfLogger().logMessage(TAG, "fetching new session token (remaining capacity " + mfSessionTokens.remainingCapacity() + ")");
            getNewSessionToken();
        }

        if (mfUploadActionToken == null || mfUploadActionToken.isExpired()) {
            mfConfiguration.getMfLogger().logMessage(TAG, "fetching upload action token");
            getNewUploadActionToken();
        }

        if (mfImageActionToken == null || mfImageActionToken.isExpired()) {
            mfConfiguration.getMfLogger().logMessage(TAG, "fetching image action token()");
            getNewImageActionToken();
        }
    }

    @Override
    public void returnSessionToken(MFSessionToken sessionToken) {
        mfConfiguration.getMfLogger().logMessage(TAG, "returnSessionToken()");
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
    public void receiveNewSessionToken(MFSessionToken mfSessionToken) {
        mfConfiguration.getMfLogger().logMessage(TAG, "receiveNewSessionToken()");
        if (mfSessionToken != null) {
            mfConfiguration.getMfLogger().logMessage(TAG, "received good session token");
            try {
                mfSessionTokens.offer(mfSessionToken);
            } catch (IllegalStateException e) {
                getNewSessionToken();
            }
        } else {
            mfConfiguration.getMfLogger().logMessage(TAG, "received bad session token, not keeping it");
        }
    }

    @Override
    public void receiveNewImageActionToken(MFImageActionToken mfImageActionToken) {
        mfConfiguration.getMfLogger().logMessage(TAG, "receiveNewImageActionToken()");

        if (isActionTokenValid(mfImageActionToken)) {
            this.mfImageActionToken = mfImageActionToken;
            mfConfiguration.getMfLogger().logMessage(TAG, "received good image action token");
            return;
        } else {
            mfConfiguration.getMfLogger().logMessage(TAG, "received bad image action token, not keeping it");
        }
    }

    @Override
    public void receiveNewUploadActionToken(MFUploadActionToken mfUploadActionToken) {
        mfConfiguration.getMfLogger().logMessage(TAG, "receiveNewUploadActionToken()");

        if (isActionTokenValid(mfUploadActionToken)) {
            this.mfUploadActionToken = mfUploadActionToken;
            mfConfiguration.getMfLogger().logMessage(TAG, "received good upload action token");
            return;
        } else {
            mfConfiguration.getMfLogger().logMessage(TAG, "received bad upload action token, not keeping it");
        }

    }

    @Override
    public MFSessionToken borrowSessionToken() {
        mfConfiguration.getMfLogger().logMessage(TAG, "borrowSessionToken()");
        MFSessionToken sessionToken = null;
        try {
            sessionToken = mfSessionTokens.take();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        if (mfSessionTokens.remainingCapacity() < minimumSessionTokens) {
            int numberOfTokensToGet = minimumSessionTokens - mfSessionTokens.remainingCapacity() ;
            for (int i = 0; i < numberOfTokensToGet; i++) {
                Thread thread = new Thread(new Runnable() {
                    @Override
                    public void run() {
                        getNewSessionToken();
                    }
                });
                thread.start();
            }
        }
        return sessionToken;
    }

    @Override
    public MFUploadActionToken borrowUploadActionToken() {
        mfConfiguration.getMfLogger().logMessage(TAG, "borrowUploadActionToken()");
        // lock and fetch new token if necessary
        lockBorrowUploadToken.lock();

        try {
            // wait while we get an image action token, condition is that image
            // action token is null or action token is expired or action token
            // string is null
            while (needNewActionToken(mfUploadActionToken)) {
                conditionUploadTokenNotExpired.await(1, TimeUnit.SECONDS);
            }
        } catch (InterruptedException e) {
            MFConfiguration.getStaticMFLogger().logException(TAG, e);
        } finally {
            lockBorrowUploadToken.unlock();
        }
        return mfUploadActionToken;
    }

    @Override
    public MFImageActionToken borrowImageActionToken() {
        mfConfiguration.getMfLogger().logMessage(TAG, "borrowImageActionToken()");
        // lock and fetch new token if necessary
        lockBorrowImageToken.lock();

        try {
            // wait while we get an image action token, condition is that image
            // action token is null or action token is expired or action token
            // string is null
            while (needNewActionToken(mfImageActionToken)) {
                conditionImageTokenNotExpired.await(1, TimeUnit.SECONDS);
            }
        } catch (InterruptedException e) {
            MFConfiguration.getStaticMFLogger().logException(TAG, e);
        } finally {
            // attach new one to apiRequestObject
            lockBorrowImageToken.unlock();
        }
        return mfImageActionToken;
    }

    private boolean needNewActionToken(MFActionToken token) {
        return token == null || token.isExpired() || token.getTokenString() == null;
    }

    private boolean isActionTokenValid(MFActionToken token) {
        return token != null && !token.isExpired() && token.getTokenString() != null;
    }
}
