package com.arkhive.components.test_session_manager_fixes.layer_token_server;

import com.arkhive.components.test_session_manager_fixes.module_api_descriptor.ApiRequestObject;
import com.arkhive.components.test_session_manager_fixes.module_credentials.CredentialsInterface;
import com.arkhive.components.test_session_manager_fixes.module_session_token.ActionToken;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by Chris Najar on 6/15/2014.
 */
public class ActionTokenServer implements Pausable {
    private volatile boolean isPaused;
    private Object pauseLock = new Object();

    private int actionTokenLifeSpan;
    private PausableThreadPoolExecutor executor;
    private CredentialsInterface credentialsInterface;
    private ActionToken imageActionToken;
    private ActionToken uploadActionToken;

    private Logger logger = LoggerFactory.getLogger(ActionTokenServer.class);

    public ActionTokenServer(CredentialsInterface credentialsInterface, int actionTokenLifeSpan, int poolSize) {
        this.credentialsInterface = credentialsInterface;
        this.actionTokenLifeSpan = actionTokenLifeSpan;
        executor = new PausableThreadPoolExecutor(poolSize);
    }

    public void addActionTokenRequest(ApiRequestObject apiRequestObject) {
        logger.debug("addActionTokenRequest()");
    }

    public void receiveValidToken(ActionToken actionToken) {
        logger.debug("receiveBackValidToken()");
    }

    public void receiveInvalidToken(ActionToken actionToken) {
        logger.debug("receiveBackInvalidToken()");
    }

    public void receiveNewActionToken(ActionToken actionToken) {
        logger.debug("receiveNewActionToken()");
    }

    private void getNewUploadActionToken() {
        logger.debug("getNewUploadActionToken()");
    }

    private void getNewImageActionToken() {
        logger.debug("getNewImageActionToken()");
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
