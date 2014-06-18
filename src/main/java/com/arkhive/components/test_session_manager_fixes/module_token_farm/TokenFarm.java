package com.arkhive.components.test_session_manager_fixes.module_token_farm;

import com.arkhive.components.test_session_manager_fixes.module_api_descriptor.ApiRequestObject;
import com.arkhive.components.test_session_manager_fixes.module_credentials.ApplicationCredentials;
import com.arkhive.components.test_session_manager_fixes.module_http_processor.HttpPeriProcessor;
import com.arkhive.components.test_session_manager_fixes.module_token_farm.interfaces.TokenFarmDistributor;
import com.arkhive.components.test_session_manager_fixes.module_token_farm.runnables.GetSessionTokenRunnable;
import com.arkhive.components.test_session_manager_fixes.module_token_farm.token_session.NewSessionTokenHttpPostProcessor;
import com.arkhive.components.test_session_manager_fixes.module_token_farm.token_session.NewSessionTokenHttpPreProcessor;
import com.arkhive.components.test_session_manager_fixes.module_token_farm.tokens.SessionToken;
import com.arkhive.components.uploadmanager.PausableThreadPoolExecutor;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

/**
 * Created by  on 6/16/2014.
 */
public class TokenFarm implements TokenFarmDistributor {
    private static final String TAG = TokenFarm.class.getSimpleName();
    private ApplicationCredentials applicationCredentials;
    private HttpPeriProcessor httpPeriProcessor;
    private PausableThreadPoolExecutor executor;
    private BlockingQueue<SessionToken> sessionTokens;

    public TokenFarm(ApplicationCredentials applicationCredentials, HttpPeriProcessor httpPeriProcessor) {
        sessionTokens = new LinkedBlockingQueue<SessionToken>(6);
        this.applicationCredentials = applicationCredentials;
        this.httpPeriProcessor = httpPeriProcessor;
        executor = new PausableThreadPoolExecutor(10, 10, 0, TimeUnit.SECONDS, new LinkedBlockingQueue<Runnable>(), Executors.defaultThreadFactory());
        startup();
    }

    public void shutdown() {
        System.out.println(TAG + " TokenFarm shutting down");
        sessionTokens.clear();
        System.out.println(TAG + " TokenFarm shut down");
    }

    private void getNewSessionToken() {
        System.out.println(TAG + " getNewSessionToken()");
        GetSessionTokenRunnable getSessionTokenRunnable = new GetSessionTokenRunnable(this, new NewSessionTokenHttpPreProcessor(), new NewSessionTokenHttpPostProcessor(), httpPeriProcessor, applicationCredentials);
        executor.execute(getSessionTokenRunnable);
    }

    private void startup() {
        System.out.println(TAG + " startup()");
        for (int i = 0; i < sessionTokens.remainingCapacity(); i++) {
            getNewSessionToken();
        }
    }

    @Override
    public void receiveNewSessionToken(ApiRequestObject apiResponseObject) {
        System.out.println(TAG + " receiveNewSessionToken()");
        SessionToken sessionToken = (SessionToken) apiResponseObject.getToken();

        if (!apiResponseObject.isSessionTokenInvalid()) {
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
        apiRequestObject.setToken(sessionToken);
    }

    @Override
    public void returnSessionToken(ApiRequestObject apiRequestObject) {
        System.out.println(TAG + " returnSessionToken");
        SessionToken sessionToken = (SessionToken) apiRequestObject.getToken();
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
}
