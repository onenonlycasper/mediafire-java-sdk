package com.arkhive.components.test_session_manager_fixes.layer_token_server;

import com.arkhive.components.test_session_manager_fixes.module_api_descriptor.ApiRequestObject;
import com.arkhive.components.test_session_manager_fixes.module_credentials.CredentialsInterface;
import com.arkhive.components.test_session_manager_fixes.module_session_token.SessionToken;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;
import java.util.Queue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * Created by Chris Najar on 6/15/2014.
 */
public class SessionTokenServer implements Pausable {
    private volatile boolean isPaused;
    private Object pauseLock = new Object();

    private final PausableThreadPoolExecutor executor;
    private final CredentialsInterface credentialsInterface;

    private final BlockingQueue<SessionToken> sessionTokens;

    private Logger logger = LoggerFactory.getLogger(SessionTokenServer.class);

    public SessionTokenServer(CredentialsInterface credentialsInterface, int poolSize, int maxSessionTokens) {
        this.credentialsInterface = credentialsInterface;
        executor = new PausableThreadPoolExecutor(poolSize);
        sessionTokens = new LinkedBlockingQueue<SessionToken>(maxSessionTokens);
    }

    public void addSessionTokenRequest(ApiRequestObject apiRequestObject) {
        logger.debug("addSessionTokenRequest()");
    }

    public void receiveValidToken(SessionToken sessionToken) {
        logger.debug("receiveBackValidToken()");
    }

    public void receiveInvalidToken(SessionToken sessionToken) {
        logger.debug("receiveBackInvalidToken()");
    }

    public void receiveNewSessionToken(SessionToken sessionToken) {
        logger.debug("receiveNewSessionToken()");
        boolean added = sessionTokens.add(sessionToken);
    }

    private void getNewSessionToken() {
        logger.debug("getNewSessionToken()");
        if (credentialsInterface == null) {
            logger.debug("-cannot get new session token. credentials interface null");
            return;
        }

        Map<String, String> credentialsMap = credentialsInterface.getCredentials();
        if (credentialsMap == null) {
            logger.debug("-cannot get new session token. credentials null");
            return;
        }

        if (credentialsMap.isEmpty()) {
            logger.debug("-cannot get new session token. credentials empty");
            return;
        }
    }

    private void spoolUpSessionTokenWorkQueue() {
        logger.debug("spoolUpSessionTokenWorkQueue()");
    }

    /*
     * PAUSABLE INTERFACE
     */
    @Override
    public void pause() {
        logger.debug("pause()");
        synchronized (pauseLock) {
            isPaused = true;
            executor.pause();
        }
    }

    @Override
    public void resume() {
        logger.debug("resume()");
        synchronized (pauseLock) {
            isPaused = false;
            executor.resume();
        }
    }

    @Override
    public boolean isPaused() {
        logger.debug("isPaused()");
        return isPaused;
    }
}
