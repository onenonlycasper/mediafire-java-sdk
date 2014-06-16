package com.arkhive.components.test_session_manager_fixes.layer_token_server;

import com.arkhive.components.test_session_manager_fixes.module_api_descriptor.ApiRequestObject;
import com.arkhive.components.test_session_manager_fixes.module_credentials.CredentialsInterface;
import com.arkhive.components.test_session_manager_fixes.module_session_token.ActionToken;
import com.arkhive.components.test_session_manager_fixes.module_session_token.SessionToken;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * Created by Chris Najar on 6/15/2014.
 */
public class TokenServerDirector implements TokenServerCallback, Pausable {
    private final int actionTokenLifeSpan;
    private volatile boolean isPaused;
    private CredentialsInterface combinedCredentials;
    private BlockingQueue<SessionToken> sessionTokens;

    private ActionToken imageActionToken;
    private ActionToken uploadActionToken;

    private Object pauseLock = new Object();
    private Logger logger = LoggerFactory.getLogger(TokenServerDirector.class);

    PausableThreadPoolExecutor actionTokenExecutor;
    PausableThreadPoolExecutor sessionTokenExecutor;

    public TokenServerDirector(CredentialsInterface combinedCredentials, int actionTokenLifeSpan, int maxActionTokensWorking, final int maxSessionTokens) {
        this.combinedCredentials = combinedCredentials;
        this.actionTokenLifeSpan = actionTokenLifeSpan;
        sessionTokens = new LinkedBlockingQueue<SessionToken>(maxSessionTokens);
        actionTokenExecutor = new PausableThreadPoolExecutor(maxActionTokensWorking);
        sessionTokenExecutor = new PausableThreadPoolExecutor(maxSessionTokens, new LinkedBlockingQueue<Runnable>(maxSessionTokens));
    }

    public void addRequestNoToken(ApiRequestObject apiRequestObject) {

    }

    public void addRequestUseSessionToken(ApiRequestObject apiRequestObject) {

    }

    public void addRequestUseImageActionToken(ApiRequestObject apiRequestObject) {

    }

    public void addRequestUseUploadActionToken(ApiRequestObject apiRequestObject) {

    }

    public void attachImageActionToken(ApiRequestObject apiRequestObject) {

    }

    public void attachUploadActionToken(ApiRequestObject apiRequestObject) {

    }

    public void attachSessionToken(ApiRequestObject apiRequestObject) {

    }

    public void setTokenServerCallback(ApiRequestObject apiRequestObject) {
        apiRequestObject.setTokenServerCallback(this);
    }

    /*
        Interface methods: TokenServerCallback
     */
    @Override
    public void httpRequestFinished(ApiRequestObject apiRequestObject, boolean tokenStillValid) {

    }

    /*
        Interface methods: Pausable
     */
    @Override
    public void pause() {
        logger.debug("pause()");
        synchronized (pauseLock) {
            isPaused = true;
        }
    }

    @Override
    public void resume() {
        logger.debug("resume()");
        synchronized (pauseLock) {
            isPaused = false;
        }
    }

    @Override
    public boolean isPaused() {
        logger.debug("isPaused()");
        return isPaused;
    }
}
