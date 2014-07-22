package com.mediafire.sdk.token;

import com.mediafire.sdk.config.MFConfiguration;
import com.mediafire.sdk.config.MFDefaultHttpProcessor;
import com.mediafire.sdk.http.MFApi;
import com.mediafire.sdk.http.MFHost;
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
    private final MFDefaultHttpProcessor mfDefaultHttpProcessor;
    private final int minimumSessionTokens;
    private final int maximumSessionTokens;
    private boolean started;

    private final BlockingQueue<MFSessionToken> mfSessionTokens;
    private MFActionToken mfUploadActionToken;
    private MFActionToken mfImageActionToken;

    // borrow token locks
    private final Object sessionTokenLock = new Object();
    private final Lock lockBorrowImageToken = new ReentrantLock();
    private final Lock lockBorrowUploadToken = new ReentrantLock();
    private final Condition conditionImageTokenNotExpired = lockBorrowImageToken.newCondition();
    private final Condition conditionUploadTokenNotExpired = lockBorrowUploadToken.newCondition();

    public MFTokenFarm(MFConfiguration mfConfiguration) {
        this.mfConfiguration = mfConfiguration;
        this.minimumSessionTokens = mfConfiguration.getMinimumSessionTokens();
        this.maximumSessionTokens = mfConfiguration.getMaximumSessionTokens();
        this.mfSessionTokens = new LinkedBlockingQueue<MFSessionToken>(maximumSessionTokens);
        this.mfDefaultHttpProcessor = new MFDefaultHttpProcessor(mfConfiguration, this);
    }

    public MFDefaultHttpProcessor getMFHttpRunner() {
        return mfDefaultHttpProcessor;
    }

    public MFConfiguration getMFConfiguration() {
        return mfConfiguration;
    }

    private void getNewSessionToken() {
        MFConfiguration.getStaticMFLogger().w(TAG, "getNewSessionToken()");

        Map<String, String> requestParameters = new LinkedHashMap<String, String>();
        requestParameters.put("token_version", "2");
        MFRequest.MFRequestBuilder mfRequestBuilder = new MFRequest.MFRequestBuilder(MFHost.LIVE_HTTPS, MFApi.USER_GET_SESSION_TOKEN);
        mfRequestBuilder.requestParameters(requestParameters);
        MFRequest mfRequest = mfRequestBuilder.build();
        mfDefaultHttpProcessor.doRequest(mfRequest);
    }

    private void getNewImageActionToken() {
        MFConfiguration.getStaticMFLogger().w(TAG, "getNewImageActionToken()");

        Map<String, String> requestParameters = new LinkedHashMap<String, String>();
        requestParameters.put("lifespan", "1440");
        requestParameters.put("type", "image");
        MFRequest.MFRequestBuilder mfRequestBuilder = new MFRequest.MFRequestBuilder(MFHost.LIVE_HTTP, MFApi.USER_GET_IMAGE_TOKEN);
        mfRequestBuilder.requestParameters(requestParameters);
        MFRequest mfRequest = mfRequestBuilder.build();
        mfDefaultHttpProcessor.doRequest(mfRequest);
    }

    private void getNewUploadActionToken() {
        MFConfiguration.getStaticMFLogger().w(TAG, "getNewUploadActionToken()");

        Map<String, String> requestParameters = new LinkedHashMap<String, String>();
        requestParameters.put("lifespan", "1440");
        requestParameters.put("type", "upload");
        MFRequest.MFRequestBuilder mfRequestBuilder = new MFRequest.MFRequestBuilder(MFHost.LIVE_HTTP, MFApi.USER_GET_UPLOAD_TOKEN);
        mfRequestBuilder.requestParameters(requestParameters);
        MFRequest mfRequest = mfRequestBuilder.build();
        mfDefaultHttpProcessor.doRequest(mfRequest);
    }

    public void shutdown() {
        MFConfiguration.getStaticMFLogger().w(TAG, "shutdown()");
        mfConfiguration.getMfCredentials().clearCredentials();
        mfSessionTokens.clear();
        mfUploadActionToken = null;
        mfImageActionToken = null;
        started = false;
    }

    public boolean isStarted() {
        return started;
    }

    public void startup() {
        MFConfiguration.getStaticMFLogger().w(TAG, "startup()");
        // do nothing if credentials aren't stored
        if (!haveStoredCredentials()) {
            throw new IllegalStateException("cannot call startup() without credentials");
        }
        // get one session token on current thread
        getNewSessionToken();
        // get remaining session tokens on another thread
        for (int i = 0; i < mfSessionTokens.remainingCapacity(); i++) {
            MFConfiguration.getStaticMFLogger().w(TAG, "fetching new session token (remaining capacity " + mfSessionTokens.remainingCapacity() + ")");
            startThreadForNewSessionToken();
        }
        // get an upload action token on current thread
        if (mfUploadActionToken == null || mfUploadActionToken.isExpired()) {
            MFConfiguration.getStaticMFLogger().w(TAG, "fetching upload action token");
            getNewUploadActionToken();
        }

        // get an image action token on current thread
        if (mfImageActionToken == null || mfImageActionToken.isExpired()) {
            MFConfiguration.getStaticMFLogger().w(TAG, "fetching image action token()");
            getNewImageActionToken();
        }

        started = true;
    }

    @Override
    public void returnSessionToken(MFSessionToken sessionToken) {
        MFConfiguration.getStaticMFLogger().w(TAG, "returnSessionToken()");
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
    public void sessionTokenSpoiled(MFSessionToken mfSessionToken) {
        MFConfiguration.getStaticMFLogger().e(TAG, "MFSessionToken got spoiled: " + mfSessionToken.toString());
        startThreadForNewSessionToken();
    }

    @Override
    public void receiveNewSessionToken(MFSessionToken mfSessionToken) {
        MFConfiguration.getStaticMFLogger().w(TAG, "receiveNewSessionToken()");
        if (mfSessionToken != null) {
            MFConfiguration.getStaticMFLogger().w(TAG, "received good session token");
            try {
                mfSessionTokens.offer(mfSessionToken);
            } catch (IllegalStateException e) {
                getNewSessionToken();
            }
        } else {
            MFConfiguration.getStaticMFLogger().w(TAG, "received bad session token, not keeping it");
        }
    }

    @Override
    public void receiveNewImageActionToken(MFActionToken mfImageActionToken) {
        MFConfiguration.getStaticMFLogger().w(TAG, "receiveNewImageActionToken()");

        if (isActionTokenValid(mfImageActionToken)) {
            this.mfImageActionToken = mfImageActionToken;
            MFConfiguration.getStaticMFLogger().w(TAG, "received good image action token");
        } else {
            MFConfiguration.getStaticMFLogger().w(TAG, "received bad image action token, not keeping it");
        }
    }

    @Override
    public void receiveNewUploadActionToken(MFActionToken mfUploadActionToken) {
        MFConfiguration.getStaticMFLogger().w(TAG, "receiveNewUploadActionToken()");

        if (isActionTokenValid(mfUploadActionToken)) {
            this.mfUploadActionToken = mfUploadActionToken;
            MFConfiguration.getStaticMFLogger().w(TAG, "received good upload action token");
        } else {
            MFConfiguration.getStaticMFLogger().w(TAG, "received bad upload action token, not keeping it");
        }

    }

    @Override
    public MFSessionToken borrowMFSessionToken() {
        MFConfiguration.getStaticMFLogger().w(TAG, "borrowMFSessionToken()");
        MFSessionToken sessionToken;
        synchronized (sessionTokenLock) {
            sessionToken = null;
            try {
                sessionToken = mfSessionTokens.take();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            if (mfSessionTokens.remainingCapacity() < minimumSessionTokens) {
                int numberOfTokensToGet = minimumSessionTokens - mfSessionTokens.remainingCapacity();
                for (int i = 0; i < numberOfTokensToGet; i++) {
                    startThreadForNewSessionToken();
                }
            }
        }
        MFConfiguration.getStaticMFLogger().w(TAG, "loaning MFToken: " + sessionToken.toString());
        return sessionToken;
    }

    @Override
    public MFActionToken borrowMFUploadActionToken() {
        MFConfiguration.getStaticMFLogger().w(TAG, "borrowMFUploadActionToken()");
        // lock and fetch new token if necessary
        lockBorrowUploadToken.lock();

        try {
            if (needNewActionToken(mfUploadActionToken)) {
                MFConfiguration.getStaticMFLogger().e(TAG, "fetching new upload token");
                startThreadForNewUploadActionToken();
            }
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
        MFConfiguration.getStaticMFLogger().w(TAG, "loaning MFToken: " + mfUploadActionToken.toString());
        return mfUploadActionToken;
    }

    @Override
    public MFActionToken borrowMFImageActionToken() {
        MFConfiguration.getStaticMFLogger().w(TAG, "borrowMFImageActionToken()");
        // lock and fetch new token if necessary
        lockBorrowImageToken.lock();

        try {
            if (needNewActionToken(mfImageActionToken)) {
                MFConfiguration.getStaticMFLogger().e(TAG, "fetching new action token");
                startThreadForNewImageActionToken();
            }
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
        MFConfiguration.getStaticMFLogger().w(TAG, "loaning MFToken: " + mfImageActionToken.toString());
        return mfImageActionToken;
    }

    private boolean needNewActionToken(MFActionToken token) {
        return token == null || token.isExpired() || token.getTokenString() == null;
    }

    private boolean isActionTokenValid(MFActionToken token) {
        MFConfiguration.getStaticMFLogger().w(TAG, "isActionTokenValid()");
        MFConfiguration.getStaticMFLogger().w(TAG, "token null: " + (token == null));
        MFConfiguration.getStaticMFLogger().w(TAG, "token expired: " + token.isExpired());
        MFConfiguration.getStaticMFLogger().w(TAG, "token string null: " + (token == null ? true : token.getTokenString() == null));
        return token != null && !token.isExpired() && token.getTokenString() != null;
    }

    private boolean haveStoredCredentials() {
        return !mfConfiguration.getMfCredentials().getCredentials().isEmpty();
    }

    private void startThreadForNewSessionToken() {
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                getNewSessionToken();
            }
        });
        thread.start();
    }

    private void startThreadForNewImageActionToken() {
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                getNewImageActionToken();
            }
        });
        thread.start();
    }

    private void startThreadForNewUploadActionToken() {
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                getNewUploadActionToken();
            }
        });
        thread.start();
    }
}
