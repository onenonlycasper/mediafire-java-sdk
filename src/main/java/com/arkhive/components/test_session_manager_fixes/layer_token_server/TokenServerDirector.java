package com.arkhive.components.test_session_manager_fixes.layer_token_server;

import com.arkhive.components.test_session_manager_fixes.module_credentials.CredentialsInterface;
import com.arkhive.components.test_session_manager_fixes.module_session_token.ActionToken;
import com.arkhive.components.test_session_manager_fixes.module_session_token.SessionToken;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by Chris Najar on 6/15/2014.
 */
public class TokenServerDirector implements TokenServerCallback, Pausable {
    private volatile boolean isPaused;
    private Object pauseLock = new Object();
    private ActionTokenServer actionTokenServer;
    private SessionTokenServer sessionTokenServer;
    private Logger logger = LoggerFactory.getLogger(TokenServerDirector.class);

    public TokenServerDirector(CredentialsInterface credentialsInterface, int actionTokenLifeSpan, int maxActionTokensWorking, int maxSessionTokens, int maxSessionTokensWorking) {
        actionTokenServer = new ActionTokenServer(credentialsInterface, actionTokenLifeSpan, maxActionTokensWorking);
        sessionTokenServer = new SessionTokenServer(credentialsInterface, maxSessionTokensWorking, maxSessionTokens);
    }

    /*
        Interface methods: TokenServerCallback
     */
    @Override
    public void actionTokenReturned(ActionToken token) {
        logger.debug("actionTokenReturned(" + token.getId() + ")");
    }

    @Override
    public void actionTokenExpired(ActionToken token) {
        logger.debug("actionTokenExpired(" + token.getId() + ")");
    }

    @Override
    public void sessionTokenReturned(SessionToken token) {
        logger.debug("sessionTokenReturned(" + token.getId() + ")");
    }

    @Override
    public void sessionTokenExpired(SessionToken token) {
        logger.debug("sessionTokenExpired(" + token.getId() + ")");
    }

    @Override
    public void newSessionTokenReturned(SessionToken token) {
        logger.debug("newSessionTokenReturned(" + token.getId() + ")");
    }

    /*
        Interface methods: Pausable
     */
    @Override
    public void pause() {
        logger.debug("pause()");
        synchronized (pauseLock) {
            actionTokenServer.pause();
            sessionTokenServer.pause();
        }
    }

    @Override
    public void resume() {
        logger.debug("resume()");
        synchronized (pauseLock) {
            actionTokenServer.resume();
            sessionTokenServer.resume();
        }
    }

    @Override
    public boolean isPaused() {
        logger.debug("isPaused()");
        synchronized (pauseLock) {
            return isPaused();
        }
    }
}
