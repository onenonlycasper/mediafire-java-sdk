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
        MFConfiguration.getStaticMFLogger().v(TAG, "getNewSessionToken()");

        Map<String, String> requestParameters = new LinkedHashMap<String, String>();
        requestParameters.put("token_version", "2");
        MFRequest.MFRequestBuilder mfRequestBuilder = new MFRequest.MFRequestBuilder(MFHost.LIVE_HTTPS, MFApi.USER_GET_SESSION_TOKEN);
        mfRequestBuilder.requestParameters(requestParameters);
        MFRequest mfRequest = mfRequestBuilder.build();
        mfDefaultHttpProcessor.doRequest(mfRequest);
    }

    private void getNewImageActionToken() {
        MFConfiguration.getStaticMFLogger().v(TAG, "getNewImageActionToken()");

        Map<String, String> requestParameters = new LinkedHashMap<String, String>();
        requestParameters.put("lifespan", "1440");
        requestParameters.put("type", "image");
        MFRequest.MFRequestBuilder mfRequestBuilder = new MFRequest.MFRequestBuilder(MFHost.LIVE_HTTP, MFApi.USER_GET_IMAGE_TOKEN);
        mfRequestBuilder.requestParameters(requestParameters);
        MFRequest mfRequest = mfRequestBuilder.build();
        mfDefaultHttpProcessor.doRequest(mfRequest);
    }

    private void getNewUploadActionToken() {
        MFConfiguration.getStaticMFLogger().v(TAG, "getNewUploadActionToken()");

        Map<String, String> requestParameters = new LinkedHashMap<String, String>();
        requestParameters.put("lifespan", "1440");
        requestParameters.put("type", "upload");
        MFRequest.MFRequestBuilder mfRequestBuilder = new MFRequest.MFRequestBuilder(MFHost.LIVE_HTTP, MFApi.USER_GET_UPLOAD_TOKEN);
        mfRequestBuilder.requestParameters(requestParameters);
        MFRequest mfRequest = mfRequestBuilder.build();
        mfDefaultHttpProcessor.doRequest(mfRequest);
    }

    public void shutdown() {
        MFConfiguration.getStaticMFLogger().v(TAG, "shutdown()");
        mfSessionTokens.clear();
        mfUploadActionToken = null;
        mfImageActionToken = null;
    }

    public void startup() {
        MFConfiguration.getStaticMFLogger().v(TAG, "startup()");
        // do nothing if credentials aren't stored
        if (!haveStoredCredentials()) {
            throw new IllegalStateException("cannot call startup() without credentials");
        }
        // get one session token on current thread
        getNewSessionToken();
        // get remaining session tokens on another thread
        for (int i = 0; i < mfSessionTokens.remainingCapacity(); i++) {
            MFConfiguration.getStaticMFLogger().v(TAG, "fetching new session token (remaining capacity " + mfSessionTokens.remainingCapacity() + ")");
            startThreadForNewSessionToken();
        }
        // get an upload action token on current thread
        if (mfUploadActionToken == null || mfUploadActionToken.isExpired()) {
            MFConfiguration.getStaticMFLogger().v(TAG, "fetching upload action token");
            getNewUploadActionToken();
        }

        // get an image action token on current thread
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
    public void sessionTokenSpoiled(MFSessionToken mfSessionToken) {
        MFConfiguration.getStaticMFLogger().e(TAG, "MFSessionToken got spoiled: " + mfSessionToken.toString());
        startThreadForNewSessionToken();
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
    public void receiveNewImageActionToken(MFActionToken mfImageActionToken) {
        MFConfiguration.getStaticMFLogger().v(TAG, "receiveNewImageActionToken()");

        if (isActionTokenValid(mfImageActionToken)) {
            this.mfImageActionToken = mfImageActionToken;
            MFConfiguration.getStaticMFLogger().v(TAG, "received good image action token");
        } else {
            MFConfiguration.getStaticMFLogger().v(TAG, "received bad image action token, not keeping it");
        }
    }

    @Override
    public void receiveNewUploadActionToken(MFActionToken mfUploadActionToken) {
        MFConfiguration.getStaticMFLogger().v(TAG, "receiveNewUploadActionToken()");

        if (isActionTokenValid(mfUploadActionToken)) {
            this.mfUploadActionToken = mfUploadActionToken;
            MFConfiguration.getStaticMFLogger().v(TAG, "received good upload action token");
        } else {
            MFConfiguration.getStaticMFLogger().v(TAG, "received bad upload action token, not keeping it");
        }

    }

    @Override
    public MFSessionToken borrowMFSessionToken() {
        MFConfiguration.getStaticMFLogger().v(TAG, "borrowMFSessionToken()");
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
        return sessionToken;
    }

    @Override
    public MFActionToken borrowMFUploadActionToken() {
        MFConfiguration.getStaticMFLogger().v(TAG, "borrowMFUploadActionToken()");
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
        return mfUploadActionToken;
    }

    @Override
    public MFActionToken borrowMFImageActionToken() {
        MFConfiguration.getStaticMFLogger().v(TAG, "borrowMFImageActionToken()");
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
        return mfImageActionToken;
    }

    private boolean needNewActionToken(MFActionToken token) {
        return token == null || token.isExpired() || token.getTokenString() == null;
    }

    private boolean isActionTokenValid(MFActionToken token) {
        MFConfiguration.getStaticMFLogger().i(TAG, "isActionTokenValid()");
        MFConfiguration.getStaticMFLogger().i(TAG, "token null: " + (token == null));
        MFConfiguration.getStaticMFLogger().i(TAG, "token expired: " + token.isExpired());
        MFConfiguration.getStaticMFLogger().i(TAG, "token string null: " + (token != null && token.getTokenString() != null));
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
