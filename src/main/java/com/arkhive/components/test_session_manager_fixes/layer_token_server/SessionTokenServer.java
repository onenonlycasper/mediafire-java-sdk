package com.arkhive.components.test_session_manager_fixes.layer_token_server;

import com.arkhive.components.test_session_manager_fixes.module_api_descriptor.ApiRequestObject;
import com.arkhive.components.test_session_manager_fixes.module_credentials.CredentialsInterface;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * Created by Chris Najar on 6/15/2014.
 */
public class SessionTokenServer implements Pausable {
    private volatile boolean isPaused;
    private Object pauseLock = new Object();

    private final PausableThreadPoolExecutor executor;
    private final CredentialsInterface credentialsInterface;

    public SessionTokenServer(CredentialsInterface credentialsInterface, int maxSessionTokens) {
        this.credentialsInterface = credentialsInterface;
        BlockingQueue<Runnable> sessionTokenQueue = new LinkedBlockingQueue<Runnable>(maxSessionTokens);
        executor = new PausableThreadPoolExecutor(maxSessionTokens, sessionTokenQueue);
    }

    public void addSessionTokenRequest(ApiRequestObject apiRequestObject) {

    }

    private void spoolUpSessionTokenWorkQueue() {

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
