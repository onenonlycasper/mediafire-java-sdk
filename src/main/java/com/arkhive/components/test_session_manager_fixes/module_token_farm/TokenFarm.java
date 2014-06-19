package com.arkhive.components.test_session_manager_fixes.module_token_farm;

import com.arkhive.components.test_session_manager_fixes.Configuration;
import com.arkhive.components.test_session_manager_fixes.module_api.ApiUris;
import com.arkhive.components.test_session_manager_fixes.module_api_descriptor.ApiRequestHttpPreProcessor;
import com.arkhive.components.test_session_manager_fixes.module_api_descriptor.ApiRequestObject;
import com.arkhive.components.test_session_manager_fixes.module_credentials.ApplicationCredentials;
import com.arkhive.components.test_session_manager_fixes.module_http_processor.HttpPeriProcessor;
import com.arkhive.components.test_session_manager_fixes.module_http_processor.interfaces.HttpRequestCallback;
import com.arkhive.components.test_session_manager_fixes.module_http_processor.runnables.HttpGetRequestRunnable;
import com.arkhive.components.test_session_manager_fixes.module_token_farm.interfaces.TokenFarmDistributor;
import com.arkhive.components.test_session_manager_fixes.module_token_farm.runnables.GetSessionTokenRunnable;
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

/**
 * Created by  on 6/16/2014.
 */
public class TokenFarm implements TokenFarmDistributor, HttpRequestCallback {
    private static final String TAG = TokenFarm.class.getSimpleName();
    private ApplicationCredentials applicationCredentials;
    private HttpPeriProcessor httpPeriProcessor;
    private PausableThreadPoolExecutor executor;
    private BlockingQueue<SessionToken> sessionTokens;
    private ActionToken uploadActionToken;
    private ActionToken imageActionToken;
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
        executor.shutdownNow();
    }

    private void getNewSessionToken() {
        System.out.println(TAG + " getNewSessionToken()");
        GetSessionTokenRunnable getSessionTokenRunnable = new GetSessionTokenRunnable(this, new NewSessionTokenHttpPreProcessor(), new NewSessionTokenHttpPostProcessor(), httpPeriProcessor, applicationCredentials);
        executor.execute(getSessionTokenRunnable);
    }

    private void getNewImageActionToken() {
        System.out.println(TAG + " getNewImageActionToken()");
        if (imageActionToken != null && !imageActionToken.isExpired()) {
            return;
        }
        ApiRequestObject apiRequestObject = new ApiRequestObject(ApiUris.LIVE_HTTP, ApiUris.URI_USER_GET_ACTION_TOKEN);
        Map<String, String> requiredParameters = new LinkedHashMap<String, String>();
        requiredParameters.put("type", "image");
        apiRequestObject.setRequiredParameters(requiredParameters);
        Map<String, String> optionalParameters = new LinkedHashMap<String, String>();
        optionalParameters.put("lifespan", "1440");
        apiRequestObject.setOptionalParameters(optionalParameters);
        // 1 - callback is TokenFarm, callback is where processing occurs for action token.
        // 2 - pre processor is same for regular requests since we ask TokenFarm for session token.
        // 3 - post processor is different than regular api request because we don't "return" the token
        // 4 - generated api request object with 24 hr lifespan and image type
        // 5 - sending the request
        HttpGetRequestRunnable getNewImageActionTokenRunnable =
                new HttpGetRequestRunnable(
                        this, // 1
                        new ApiRequestHttpPreProcessor(), // 2
                        new NewActionTokenHttpPostProcessor(), // 3
                        apiRequestObject, // 4
                        httpPeriProcessor); //5
        executor.execute(getNewImageActionTokenRunnable);
    }

    private void getNewUploadActionToken() {
        System.out.println(TAG + " getNewUploadActionToken()");
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
            System.out.println(TAG + " request object did not have a session token, but it should have. need new session token");
            needToGetNewSessionToken = true;
        }

        if (sessionToken != null && apiRequestObject.isSessionTokenInvalid()) {
            System.out.println(TAG + " not returning session token. it is invalid or signature calculation went bad. need new session token");
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
    public void borrowActionToken(ApiRequestObject apiRequestObject, ActionToken.Type type) {
        System.out.println(TAG + "borrowActionToken");
        // if either the image action token or upload action token is expired or nearing expiration (4 hours)
        // then request a new one before delivering. need to put wait on thread until the token is received.
        if (imageActionToken == null || imageActionToken.isExpired()) {

        }
        if (type == ActionToken.Type.IMAGE) {
            apiRequestObject.setActionToken(imageActionToken);
        } else {
            apiRequestObject.setActionToken(uploadActionToken);
        }
    }

    @Override
    public void receiveNewImageActionToken(ApiRequestObject apiRequestObject) {
        System.out.println(TAG + " receiveNewImageActionToken()");
        ActionToken actionToken = apiRequestObject.getActionToken();
        if (!apiRequestObject.isActionTokenInvalid() && actionToken != null) {
            // if we already have an upload action token then update the string user Token.setTokenString()
            // if we do not have an upload action token then create a new one and set it.
            if (imageActionToken != null) {
                imageActionToken.setTokenString(actionToken.getTokenString());
                System.out.println(TAG + " updated ActionToken: " + imageActionToken.getTokenString());
            } else {
                imageActionToken = ActionToken.newInstance(ActionToken.Type.IMAGE);
                System.out.println(TAG + " added ActionToken: " + imageActionToken.getTokenString());
            }
        } else {
            getNewImageActionToken();
        }
    }

    @Override
    public void receiveNewUploadActionToken(ApiRequestObject apiRequestObject) {
        System.out.println(TAG + " receiveNewUploadActionToken()");
        ActionToken actionToken = apiRequestObject.getActionToken();
        if (!apiRequestObject.isActionTokenInvalid() && actionToken != null) {
            // if we already have an upload action token then update the string user Token.setTokenString()
            // if we do not have an upload action token then create a new one and set it.
            if (uploadActionToken != null) {
                uploadActionToken.setTokenString(actionToken.getTokenString());
                System.out.println(TAG + " updated ActionToken: " + uploadActionToken.getTokenString());
            } else {
                uploadActionToken = ActionToken.newInstance(ActionToken.Type.UPLOAD);
                System.out.println(TAG + " added ActionToken: " + uploadActionToken.getTokenString());
            }
        } else {
            getNewUploadActionToken();
        }
    }

    /**
     * HttpRequestCallback which is needed for callbacks from requesting ActionToken.
     * @param apiRequestObject - the apiRequestObject
     */
    @Override
    public void httpRequestStarted(ApiRequestObject apiRequestObject) {

    }

    @Override
    public void httpRequestFinished(ApiRequestObject apiRequestObject) {
        System.out.println(TAG + " httpRequestFinished()");
    }
}
