package com.mediafire.sdk.token;

import com.mediafire.sdk.config.MFConfiguration;
import com.mediafire.sdk.http.MFApi;
import com.mediafire.sdk.http.MFHost;
import com.mediafire.sdk.http.MFHttpRunner;
import com.mediafire.sdk.http.MFRequest;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public final class MFTokenFarm implements MFTokenFarmCallback {
    private static final String TAG = MFTokenFarm.class.getCanonicalName();
    private final MFConfiguration mfConfiguration;
    private final MFHttpRunner mfHttpRunner;
    private final int minimumSessionTokens;
    private final int maximumSessionTokens;

    private final BlockingQueue<MFSessionToken> mfSessionTokens;
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

    public MFHttpRunner getMFHttpRunner() {
        return mfHttpRunner;
    }

    public MFConfiguration getMFConfiguration() {
        return mfConfiguration;
    }

    private void getNewSessionToken() {
        MFConfiguration.getStaticMFLogger().v(TAG, "getNewSessionToken()");

        Map<String, String> requestParameters = new LinkedHashMap<String, String>();
        requestParameters.put("token_version", "2");
        MFRequest mfRequest = new MFRequest(MFHost.LIVE_HTTPS, MFApi.USER_GET_SESSION_TOKEN, requestParameters);
        mfHttpRunner.doRequest(mfRequest);
    }

    private void getNewImageActionToken() {
        MFConfiguration.getStaticMFLogger().v(TAG, "getNewImageActionToken()");

        Map<String, String> requestParameters = new LinkedHashMap<String, String>();
        requestParameters.put("lifespan", "1440");
        requestParameters.put("type", "image");
        MFRequest mfRequest = new MFRequest(MFHost.LIVE_HTTP, MFApi.USER_GET_IMAGE_TOKEN, requestParameters);
        mfHttpRunner.doRequest(mfRequest);
    }

    private void getNewUploadActionToken() {
        MFConfiguration.getStaticMFLogger().v(TAG, "getNewUploadActionToken()");

        Map<String, String> requestParameters = new LinkedHashMap<String, String>();
        requestParameters.put("lifespan", "1440");
        requestParameters.put("type", "upload");
        MFRequest mfRequest = new MFRequest(MFHost.LIVE_HTTP, MFApi.USER_GET_UPLOAD_TOKEN, requestParameters);
        mfHttpRunner.doRequest(mfRequest);
    }

    public void shutdown() {
        MFConfiguration.getStaticMFLogger().v(TAG, "shutdown()");

        mfSessionTokens.clear();
        mfUploadActionToken = null;
        mfImageActionToken = null;
    }

    public void startup() {
        MFConfiguration.getStaticMFLogger().v(TAG, "startup()");

        for (int i = 0; i < mfSessionTokens.remainingCapacity(); i++) {
            MFConfiguration.getStaticMFLogger().v(TAG, "fetching new session token (remaining capacity " + mfSessionTokens.remainingCapacity() + ")");
            getNewSessionToken();
        }

        if (mfUploadActionToken == null || mfUploadActionToken.isExpired()) {
            MFConfiguration.getStaticMFLogger().v(TAG, "fetching upload action token");
            getNewUploadActionToken();
        }

        if (mfImageActionToken == null || mfImageActionToken.isExpired()) {
            MFConfiguration.getStaticMFLogger().v(TAG, "fetching image action token()");
            getNewImageActionToken();
        }
    }

    @Override
    public void returnSessionToken(MFSessionToken sessionToken) {
        MFConfiguration.getStaticMFLogger().v(TAG, "returnSessionToken()");
        if (sessionToken == null) {
            MFConfiguration.getStaticMFLogger().w(TAG, "received null session token");
            return;
        }

        try {
            mfSessionTokens.put(sessionToken);
        } catch (InterruptedException e) {
            MFConfiguration.getStaticMFLogger().e(TAG, "exception while trying to return a session token", e);
        }
    }

    @Override
    public void receiveNewSessionToken(MFSessionToken mfSessionToken) {
        MFConfiguration.getStaticMFLogger().v(TAG, "receiveNewSessionToken()");
        if (mfSessionToken != null) {
            MFConfiguration.getStaticMFLogger().v(TAG, "received good session token");
            try {
                mfSessionTokens.offer(mfSessionToken);
            } catch (IllegalStateException e) {
                getNewSessionToken();
            }
        } else {
            MFConfiguration.getStaticMFLogger().v(TAG, "received bad session token, not keeping it");
        }
    }

    @Override
    public void receiveNewImageActionToken(MFImageActionToken mfImageActionToken) {
        MFConfiguration.getStaticMFLogger().v(TAG, "receiveNewImageActionToken()");

        if (isActionTokenValid(mfImageActionToken)) {
            this.mfImageActionToken = mfImageActionToken;
            MFConfiguration.getStaticMFLogger().v(TAG, "received good image action token");
        } else {
            MFConfiguration.getStaticMFLogger().v(TAG, "received bad image action token, not keeping it");
        }
    }

    @Override
    public void receiveNewUploadActionToken(MFUploadActionToken mfUploadActionToken) {
        MFConfiguration.getStaticMFLogger().v(TAG, "receiveNewUploadActionToken()");

        if (isActionTokenValid(mfUploadActionToken)) {
            this.mfUploadActionToken = mfUploadActionToken;
            MFConfiguration.getStaticMFLogger().v(TAG, "received good upload action token");
        } else {
            MFConfiguration.getStaticMFLogger().v(TAG, "received bad upload action token, not keeping it");
        }

    }

    @Override
    public MFSessionToken borrowSessionToken() {
        MFConfiguration.getStaticMFLogger().v(TAG, "borrowSessionToken()");
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
        MFConfiguration.getStaticMFLogger().v(TAG, "borrowUploadActionToken()");
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
            MFConfiguration.getStaticMFLogger().e(TAG, "exception while trying to borrow an upload action token", e);
        } finally {
            lockBorrowUploadToken.unlock();
        }
        return mfUploadActionToken;
    }

    @Override
    public MFImageActionToken borrowImageActionToken() {
        MFConfiguration.getStaticMFLogger().v(TAG, "borrowImageActionToken()");
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
            MFConfiguration.getStaticMFLogger().e(TAG, "exception while trying to borrow an image action token", e);
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
