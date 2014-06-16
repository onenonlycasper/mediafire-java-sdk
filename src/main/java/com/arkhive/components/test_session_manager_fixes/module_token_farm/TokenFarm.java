package com.arkhive.components.test_session_manager_fixes.module_token_farm;

import com.arkhive.components.test_session_manager_fixes.module_api_descriptor.ApiRequestObject;
import com.arkhive.components.test_session_manager_fixes.module_credentials.ApplicationCredentials;
import com.arkhive.components.test_session_manager_fixes.module_http_processor.HttpPeriProcessor;
import com.arkhive.components.test_session_manager_fixes.module_session_token.SessionToken;
import com.arkhive.components.uploadmanager.PausableThreadPoolExecutor;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

/**
 * Created by Chris Najar on 6/16/2014.
 */
public class TokenFarm implements GetSessionTokenRunnable.Callback {
    private ApplicationCredentials applicationCredentials;
    private HttpPeriProcessor httpPeriProcessor;
    private PausableThreadPoolExecutor newSessionTokenExecutor;
    private BlockingQueue<SessionToken> sessionTokens;

    private TokenFarm(ApplicationCredentials applicationCredentials, HttpPeriProcessor httpPeriProcessor) {
        sessionTokens = new LinkedBlockingQueue<SessionToken>(6);
        this.applicationCredentials = applicationCredentials;
        this.httpPeriProcessor = httpPeriProcessor;
        newSessionTokenExecutor = new PausableThreadPoolExecutor(6, 6, 0, TimeUnit.SECONDS, new LinkedBlockingQueue<Runnable>(6), Executors.defaultThreadFactory());
    }

    private static TokenFarm instance;

    public static TokenFarm newInstance(ApplicationCredentials applicationCredentials,
                                        HttpPeriProcessor httpPeriProcessor) throws TokenFarmException {
        if (instance != null) {
            throw new TokenFarmException("Cannot create a new instance without calling shutdown()");
        }
        instance = new TokenFarm(applicationCredentials, httpPeriProcessor);
        instance.startup();
        System.out.println("TokenFarm initialized");
        return instance;
    }

    public static TokenFarm getInstance() {
        return instance;
    }

    public void shutdown() {
        System.out.println("TokenFarm shutting down");
        // (TODO) do other stuff to clean up references.
        instance = null;
        sessionTokens.clear();
        System.out.println("TokenFarm shut down");
    }

    private void getNewSessionToken() {
        System.out.println("getNewSessionToken()");
        GetSessionTokenRunnable getSessionTokenRunnable = new GetSessionTokenRunnable(this, httpPeriProcessor, applicationCredentials);
        newSessionTokenExecutor.execute(getSessionTokenRunnable);
    }

    private void startup() {
        System.out.println("startup()");
        for(int i = 0; i < sessionTokens.remainingCapacity(); i++) {
            getNewSessionToken();
        }
    }

    @Override
    public void sessionTokenFetchCompleted(ApiRequestObject apiResponseObject) {
        System.out.println("sessionTokenFetchCompleted()");
        System.out.println("constructed url: " + apiResponseObject.getConstructedUrl());
        System.out.println("response string: " + apiResponseObject.getHttpResponseString());
        SessionToken sessionToken = (SessionToken) apiResponseObject.getToken();
        System.out.println("session token:   " + sessionToken.getTokenString());
        System.out.println("secret key:      " + sessionToken.getSecretKey());
        System.out.println("time:            " + sessionToken.getTime());
        System.out.println("pkey:            " + sessionToken.getPkey());

        if (!apiResponseObject.isSessionTokenInvalid()) {
            try {
                sessionTokens.add(sessionToken);
            } catch (IllegalStateException e) {
                System.out.println("queue is full, not adding this session token");
            }
        }
    }
}
