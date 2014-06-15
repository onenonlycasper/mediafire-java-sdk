package com.arkhive.components.test_session_manager_fixes.layer_token_server;

import com.arkhive.components.test_session_manager_fixes.module_api_descriptor.ApiRequestObject;
import com.arkhive.components.test_session_manager_fixes.module_credentials.CredentialsInterface;

/**
 * Created by Chris Najar on 6/15/2014.
 */
public class ActionTokenServer implements Pausable {
    private volatile boolean isPaused;
    private Object pauseLock = new Object();

    private int actionTokenLifeSpan;
    private PausableThreadPoolExecutor executor;
    private CredentialsInterface credentialsInterface;

    public ActionTokenServer(CredentialsInterface credentialsInterface, int actionTokenLifeSpan, int maxActionTokensWorking) {
        this.credentialsInterface = credentialsInterface;
        this.actionTokenLifeSpan = actionTokenLifeSpan;
        executor = new PausableThreadPoolExecutor(maxActionTokensWorking);
    }

    public void addActionTokenRequest(ApiRequestObject apiRequestObject) {

    }

    private void getUploadActionToken() {

    }

    private void getImageActionToken() {

    }


    /*
     * PAUSABLE INTERFACE
     */
    @Override
    public void pause() {
        synchronized (pauseLock) {
            isPaused = true;
            executor.pause();
        }
    }

    @Override
    public void resume() {
        synchronized (pauseLock) {
            isPaused = false;
            executor.resume();
        }
    }

    @Override
    public boolean isPaused() {
        return isPaused;
    }
}
