package com.arkhive.components.test_session_manager_fixes.layer_token_server;

import com.arkhive.components.test_session_manager_fixes.module_credentials.CredentialsInterface;
import com.arkhive.components.test_session_manager_fixes.module_session_token.ActionToken;
import com.arkhive.components.test_session_manager_fixes.module_session_token.SessionToken;

/**
 * Created by Chris Najar on 6/15/2014.
 */
public class TokenServerDirector implements TokenServerCallback, Pausable {
    private volatile boolean isPaused;
    private Object pauseLock = new Object();
    private ActionTokenServer actionTokenServer;
    private SessionTokenServer sessionTokenServer;

    public TokenServerDirector(CredentialsInterface credentialsInterface, int actionTokenLifeSpan, int maxActionTokensWorking, int maxSessionTokens) {
        actionTokenServer = new ActionTokenServer(credentialsInterface, actionTokenLifeSpan, maxActionTokensWorking);
        sessionTokenServer = new SessionTokenServer(credentialsInterface, maxSessionTokens);
    }

    /*
        Interface methods: TokenServerCallback
     */
    @Override
    public void actionTokenReturned(ActionToken token) {

    }

    @Override
    public void actionTokenExpired(ActionToken token) {

    }

    @Override
    public void sessionTokenReturned(SessionToken token) {

    }

    @Override
    public void sessionTokenExpired(SessionToken token) {

    }

    /*
        Interface methods: Pausable
     */
    @Override
    public void pause() {
        synchronized (pauseLock) {
            actionTokenServer.pause();
            sessionTokenServer.pause();
        }
    }

    @Override
    public void resume() {
        synchronized (pauseLock) {
            actionTokenServer.resume();
            sessionTokenServer.resume();
        }
    }

    @Override
    public boolean isPaused() {
        synchronized (pauseLock) {
            return isPaused();
        }
    }
}
