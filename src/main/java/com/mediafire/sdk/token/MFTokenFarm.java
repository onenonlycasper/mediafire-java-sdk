package com.mediafire.sdk.token;

import com.mediafire.sdk.config.MFConfiguration;
import com.mediafire.sdk.http.MFApi;
import com.mediafire.sdk.http.MFHost;
import com.mediafire.sdk.http.MFHttpRunner;
import com.mediafire.sdk.http.MFRequest;
import com.mediafire.sdk.util.MFGenericCallback;

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
        this.mfSessionTokens = new LinkedBlockingQueue<MFSessionToken>(mfConfiguration.getMaximumSessionTokens());
        this.mfHttpRunner = new MFHttpRunner(mfConfiguration, this);
    }

    public MFHttpRunner getMfHttpRunner() {
        return mfHttpRunner;
    }

    public void getNewSessionToken(MFGenericCallback<Void> mfGenericCallback) {
        mfConfiguration.getMfLogger().logMessage(TAG, "getNewSessionToken()");
        Map<String, String> requestParameters = new LinkedHashMap<String, String>();
        requestParameters.put("token_version", "2");
        MFRequest mfRequest = new MFRequest(MFHost.LIVE_HTTPS, MFApi.USER_GET_SESSION_TOKEN, requestParameters);
        mfConfiguration.getMfExecutor().execute(new MFTokenFarmInternalRunnable(mfHttpRunner, mfRequest, mfGenericCallback));
    }

    public void getNewSessionToken() {
        getNewSessionToken(null);
    }

    private void getNewImageActionToken(MFGenericCallback<Void> mfGenericCallback) {
        mfConfiguration.getMfLogger().logMessage(TAG, "getNewImageActionToken()");
        Map<String, String> requestParameters = new LinkedHashMap<String, String>();
        requestParameters.put("lifespan", "1440");
        requestParameters.put("type", "image");
        MFRequest mfRequest = new MFRequest(MFHost.LIVE_HTTP, MFApi.USER_GET_ACTION_TOKEN, requestParameters);
        mfConfiguration.getMfExecutor().execute(new MFTokenFarmInternalRunnable(mfHttpRunner, mfRequest, mfGenericCallback));
    }

    public void getNewImageActionToken() {
        getNewImageActionToken(null);
    }

    private void getNewUploadActionToken(MFGenericCallback<Void> mfGenericCallback) {
        mfConfiguration.getMfLogger().logMessage(TAG, "getNewUploadActionToken()");
        Map<String, String> requestParameters = new LinkedHashMap<String, String>();
        requestParameters.put("lifespan", "1440");
        requestParameters.put("type", "upload");
        MFRequest mfRequest = new MFRequest(MFHost.LIVE_HTTP, MFApi.USER_GET_ACTION_TOKEN, requestParameters);
        mfConfiguration.getMfExecutor().execute(new MFTokenFarmInternalRunnable(mfHttpRunner, mfRequest, mfGenericCallback));
    }

    public void getNewUploadActionToken() {
        getNewUploadActionToken(null);
    }

    public void shutdown(MFGenericCallback<Void> mfGenericCallback) {
        mfConfiguration.getMfLogger().logMessage(TAG, "shutdown()");
        if (mfGenericCallback != null) {
            mfGenericCallback.jobStarted();
        }

        mfSessionTokens.clear();
        mfUploadActionToken = null;
        mfImageActionToken = null;

        if (mfGenericCallback != null) {
            mfGenericCallback.jobFinished(null);
        }
    }

    public void shutdown() {
        shutdown(null);
    }

    public void startup(MFGenericCallback<Void> mfGenericCallback) {
        mfConfiguration.getMfLogger().logMessage(TAG, "startup()");
        if (mfGenericCallback != null) {
            mfGenericCallback.jobStarted();
        }

        while (mfSessionTokens.remainingCapacity() > 0) {
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

        if (mfGenericCallback != null) {
            mfGenericCallback.jobFinished(null);
        }
    }

    public void startup() {
        startup(null);
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
        synchronized (sessionTokenLock) {
            if (mfSessionToken != null && mfSessionToken != null) {
                try {
                    mfSessionTokens.offer(mfSessionToken);
                } catch (IllegalStateException e) {
                    getNewSessionToken();
                }
            } else {
                getNewSessionToken();
            }
        }
    }

    @Override
    public void receiveNewImageActionToken(MFImageActionToken mfImageActionToken) {
        mfConfiguration.getMfLogger().logMessage(TAG, "receiveNewImageActionToken()");
        synchronized (imageTokenLock) {
            if (mfImageActionToken != null && !mfImageActionToken.isExpired() && mfImageActionToken.getTokenString() != null) {
                this.mfImageActionToken = mfImageActionToken;
                return;
            } else {
                getNewImageActionToken();
            }
        }
    }

    @Override
    public void receiveNewUploadActionToken(MFUploadActionToken mfUploadActionToken) {
        mfConfiguration.getMfLogger().logMessage(TAG, "receiveNewUploadActionToken()");
        synchronized (uploadTokenLock) {
            if (mfUploadActionToken != null && !mfUploadActionToken.isExpired() && mfUploadActionToken.getTokenString() != null) {
                this.mfUploadActionToken = mfUploadActionToken;
                return;
            } else {
                getNewUploadActionToken();
            }
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
        return sessionToken;
    }

    @Override
    public MFUploadActionToken borrowUploadActionToken() {
        mfConfiguration.getMfLogger().logMessage(TAG, "borrowUploadActionToken()");
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
        mfConfiguration.getMfLogger().logMessage(TAG, "borrowImageActionToken()");
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
