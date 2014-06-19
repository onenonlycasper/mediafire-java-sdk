package com.arkhive.components.test_session_manager_fixes.module_token_farm;

import com.arkhive.components.sessionmanager.session.ActionTokenResponse;
import com.arkhive.components.test_session_manager_fixes.Configuration;
import com.arkhive.components.test_session_manager_fixes.module_api.ApiUris;
import com.arkhive.components.test_session_manager_fixes.module_api_descriptor.ApiRequestHttpPostProcessor;
import com.arkhive.components.test_session_manager_fixes.module_api_descriptor.ApiRequestHttpPreProcessor;
import com.arkhive.components.test_session_manager_fixes.module_api_descriptor.ApiRequestObject;
import com.arkhive.components.test_session_manager_fixes.module_api_descriptor.interfaces.ApiRequestRunnableCallback;
import com.arkhive.components.test_session_manager_fixes.module_api_descriptor.requests.RunnableApiGetRequest;
import com.arkhive.components.test_session_manager_fixes.module_credentials.ApplicationCredentials;
import com.arkhive.components.test_session_manager_fixes.module_http_processor.HttpPeriProcessor;
import com.arkhive.components.test_session_manager_fixes.module_http_processor.interfaces.HttpRequestCallback;
import com.arkhive.components.test_session_manager_fixes.module_http_processor.runnables.HttpGetRequestRunnable;
import com.arkhive.components.test_session_manager_fixes.module_token_farm.interfaces.TokenFarmDistributor;
import com.arkhive.components.test_session_manager_fixes.module_token_farm.runnables.GetImageActionTokenRunnable;
import com.arkhive.components.test_session_manager_fixes.module_token_farm.runnables.GetSessionTokenRunnable;
import com.arkhive.components.test_session_manager_fixes.module_token_farm.runnables.GetUploadActionTokenRunnable;
import com.arkhive.components.test_session_manager_fixes.module_token_farm.token_action.GetActionTokenResponse;
import com.arkhive.components.test_session_manager_fixes.module_token_farm.token_action.NewActionTokenHttpPostProcessor;
import com.arkhive.components.test_session_manager_fixes.module_token_farm.token_session.NewSessionTokenHttpPostProcessor;
import com.arkhive.components.test_session_manager_fixes.module_token_farm.token_session.NewSessionTokenHttpPreProcessor;
import com.arkhive.components.test_session_manager_fixes.module_token_farm.tokens.ActionToken;
import com.arkhive.components.test_session_manager_fixes.module_token_farm.tokens.SessionToken;
import com.arkhive.components.uploadmanager.PausableThreadPoolExecutor;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * Created by  on 6/16/2014.
 */
public class TokenFarm implements TokenFarmDistributor, ApiRequestRunnableCallback<GetActionTokenResponse> {
    private static final String TAG = TokenFarm.class.getSimpleName();
    private final Lock borrowImageTokenLock = new ReentrantLock();
    private final Lock borrowUploadTokenLock = new ReentrantLock();
    private final Condition imageTokenNotExpired = borrowImageTokenLock.newCondition();
    private final Condition uploadTokenNotExpired = borrowUploadTokenLock.newCondition();
    private final ApplicationCredentials applicationCredentials;
    private final HttpPeriProcessor httpPeriProcessor;
    private final PausableThreadPoolExecutor executor;
    private final BlockingQueue<SessionToken> sessionTokens;
    private ActionToken uploadActionToken;
    private ActionToken imageActionToken;
    private Object imageTokenLock = new Object();
    private Object uploadTokenLock = new Object();
    private int minimumSessionTokens = Configuration.DEFAULT_MINIMUM_SESSION_TOKENS;
    private int maximumSessionTokens = Configuration.DEFAULT_MAXIMUM_SESSION_TOKENS;

    public TokenFarm(Configuration configuration, ApplicationCredentials applicationCredentials, HttpPeriProcessor httpPeriProcessor) {
        minimumSessionTokens = configuration.getMinimumSessionTokens();
        maximumSessionTokens = configuration.getMaximumSessionTokens();
        sessionTokens = new LinkedBlockingQueue<SessionToken>(maximumSessionTokens);
        this.applicationCredentials = applicationCredentials;
        this.httpPeriProcessor = httpPeriProcessor;
        executor = new PausableThreadPoolExecutor(10, 10, 0, TimeUnit.SECONDS, new LinkedBlockingQueue<Runnable>(), Executors.defaultThreadFactory());
    }

    public void shutdown() {
        System.out.println(TAG + " TokenFarm shutting down");
        sessionTokens.clear();
        borrowImageTokenLock.unlock();
        borrowUploadTokenLock.unlock();
        imageTokenNotExpired.signal();
        uploadTokenNotExpired.signal();
        imageActionToken = null;
        uploadActionToken = null;
        executor.shutdownNow();
    }

    private void getNewSessionToken() {
        System.out.println(TAG + " getNewSessionToken()");
        GetSessionTokenRunnable getSessionTokenRunnable =
                new GetSessionTokenRunnable(
                        this,
                        new NewSessionTokenHttpPreProcessor(),
                        new NewSessionTokenHttpPostProcessor(),
                        httpPeriProcessor,
                        applicationCredentials);
        executor.execute(getSessionTokenRunnable);
    }

    private void getNewImageActionToken() {
        System.out.println(TAG + " getNewImageActionToken()");
        GetImageActionTokenRunnable getImageActionTokenRunnable =
                new GetImageActionTokenRunnable(
                        new ApiRequestHttpPreProcessor(),
                        new ApiRequestHttpPostProcessor(),
                        this, httpPeriProcessor);
        executor.execute(getImageActionTokenRunnable);
    }

    private void getNewUploadActionToken() {
        System.out.println(TAG + " getNewUploadActionToken()");
        GetUploadActionTokenRunnable getUploadActionTokenRunnable =
                new GetUploadActionTokenRunnable(
                        new ApiRequestHttpPreProcessor(),
                        new ApiRequestHttpPostProcessor(),
                        this, httpPeriProcessor);
        executor.execute(getUploadActionTokenRunnable);
    }

    public void startup() {
        System.out.println(TAG + " startup()");
        for (int i = 0; i < sessionTokens.remainingCapacity(); i++) {
            getNewSessionToken();
        }
        getNewImageActionToken();
        getNewUploadActionToken();
    }

    @Override
    public void receiveNewSessionToken(ApiRequestObject apiRequestObject) {
        System.out.println(TAG + " receiveNewSessionToken()");
        SessionToken sessionToken = apiRequestObject.getSessionToken();
        if (!apiRequestObject.isSessionTokenInvalid() && sessionToken != null) {
            try {
                sessionTokens.add(sessionToken);
                System.out.println(TAG + " added " + sessionToken.getTokenString());
            } catch (IllegalStateException e) {
                System.out.println(TAG + " interrupted, not adding: " + sessionToken.getTokenString());
                getNewSessionToken();
            }
        } else {
            getNewSessionToken();
        }
    }

    @Override
    public void borrowSessionToken(ApiRequestObject apiRequestObject) {
        System.out.println(TAG + "borrowSessionToken");
        SessionToken sessionToken = null;
        try {
            sessionToken = sessionTokens.take();
            System.out.println(TAG + " session token borrowed: " + sessionToken.getTokenString());
        } catch (InterruptedException e) {
            e.printStackTrace();
            apiRequestObject.addExceptionDuringRequest(e);
            System.out.println(TAG + " no session token borrowed, interrupted.");
        }
        apiRequestObject.setSessionToken(sessionToken);
    }

    @Override
    public void returnSessionToken(ApiRequestObject apiRequestObject) {
        System.out.println(TAG + " returnSessionToken");
        SessionToken sessionToken = apiRequestObject.getSessionToken();
        boolean needToGetNewSessionToken = false;
        if (sessionToken == null) {
            System.out.println(TAG + " request object did not have a session token, " +
                    "but it should have. need new session token");
            needToGetNewSessionToken = true;
        }

        if (sessionToken == null || apiRequestObject.isSessionTokenInvalid()) {
            System.out.println(TAG + " not returning session token. it is invalid or signature " +
                    "calculation went bad. need new session token");
            needToGetNewSessionToken = true;
        } else {
            System.out.println(TAG + " returning session token: " + sessionToken.getTokenString());
            try {
                sessionTokens.put(sessionToken);
            } catch (InterruptedException e) {
                System.out.println(TAG + " could not return session token, interrupted. need new session token");
                needToGetNewSessionToken = true;
            }
        }

        if (needToGetNewSessionToken) {
            System.out.println(TAG + " fetching a new session token");
            getNewSessionToken();
        }
    }

    @Override
    public void borrowImageActionToken(ApiRequestObject apiRequestObject) {
        System.out.println(TAG + "borrowImageActionToken");
        // lock and fetch new token if necessary
        borrowImageTokenLock.lock();
        if (imageActionToken == null || imageActionToken.isExpired()) {
            getNewImageActionToken();
        }
        try {
            // wait while we get an image action token, condition is that image
            // action token is null or action token is expired or action token
            // string is null
            while (imageActionToken == null ||
                    imageActionToken.isExpired() ||
                    imageActionToken.getTokenString() == null) {
                imageTokenNotExpired.await(45, TimeUnit.SECONDS);
            }
        } catch (InterruptedException e) {
          apiRequestObject.addExceptionDuringRequest(e);
        } finally {
            borrowImageTokenLock.unlock();
        }
        // attach new one to apiRequestObject
        apiRequestObject.setActionToken(imageActionToken);
    }


    @Override
    public void borrowUploadActionToken(ApiRequestObject apiRequestObject) {
        System.out.println(TAG + "borrowUploadActionToken");
        // lock and fetch new token if necessary
        borrowUploadTokenLock.lock();
        if (uploadActionToken == null || uploadActionToken.isExpired()) {
            getNewImageActionToken();
        }
        try {
            // wait while we get an image action token, condition is that image
            // action token is null or action token is expired or action token
            // string is null
            while (uploadActionToken == null ||
                    uploadActionToken.isExpired() ||
                    uploadActionToken.getTokenString() == null) {
                uploadTokenNotExpired.await(45, TimeUnit.SECONDS);
            }
        } catch (InterruptedException e) {
            apiRequestObject.addExceptionDuringRequest(e);
        } finally {
            borrowUploadTokenLock.unlock();
        }
        // attach new one to apiRequestObject
        apiRequestObject.setActionToken(imageActionToken);
    }

    @Override
    public void receiveNewImageActionToken(ApiRequestObject apiRequestObject) {
        System.out.println(TAG + " receiveNewImageActionToken()");
        synchronized (imageTokenLock) {
            if (imageActionToken != null &&
                    !imageActionToken.isExpired() &&
                    imageActionToken.getTokenString() != null) {
                System.out.println(TAG + " received action token: " + imageActionToken.getTokenString() +
                        ", type: " + imageActionToken.getType().toString() +
                        ", expired: " + imageActionToken.isExpired());
                return;
            }
            ActionToken actionToken = apiRequestObject.getActionToken();

            if (actionToken == null) {
                System.out.println(TAG + " action token received is null");
            } else if (actionToken.getTokenString() == null) {
                System.out.println(TAG + " action token received is null");
            } else if (apiRequestObject.isActionTokenInvalid()) {
                System.out.println(TAG + " action token received is invalid");
            } else if (actionToken.getType() != ActionToken.Type.IMAGE) {
                System.out.println(TAG + " action token received is not image type");
            } else {
                imageActionToken = actionToken;
                System.out.println(TAG + " received action token: " + imageActionToken.getTokenString() +
                        ", type: " + imageActionToken.getType().toString() +
                        ", expired: " + imageActionToken.isExpired());
            }
        }
    }

    @Override
    public void receiveNewUploadActionToken(ApiRequestObject apiRequestObject) {
        System.out.println(TAG + " receiveNewUploadActionToken()");
        synchronized (uploadTokenLock) {
            if (uploadActionToken != null &&
                    !uploadActionToken.isExpired() &&
                    uploadActionToken.getTokenString() != null) {
                System.out.println(TAG + " received action token: " + uploadActionToken.getTokenString() +
                        ", type: " + uploadActionToken.getType().toString() +
                        ", expired: " + uploadActionToken.isExpired());
                return;
            }
            ActionToken actionToken = apiRequestObject.getActionToken();
            if (actionToken == null) {
                System.out.println(TAG + " action token received is null");
            } else if (actionToken.getTokenString() == null) {
                System.out.println(TAG + " action token received is null");
            } else if (apiRequestObject.isActionTokenInvalid()) {
                System.out.println(TAG + " action token received is invalid");
            } else if (actionToken.getType() != ActionToken.Type.UPLOAD) {
                System.out.println(TAG + " action token received is not upload type");
            } else {
                uploadActionToken = actionToken;
                System.out.println(TAG + " received action token: " + uploadActionToken.getTokenString() +
                        ", type: " + uploadActionToken.getType().toString() +
                        ", expired: " + uploadActionToken.isExpired());
            }
        }
    }

    @Override
    public void apiRequestProcessStarted() {}

    @Override
    public void apiRequestProcessFinished(GetActionTokenResponse gsonResponse) {
        System.out.println(TAG + " apiRequestProcessFinished()");
    }
}
