package com.arkhive.components.test_session_manager_fixes.layer_token_server;

import com.arkhive.components.test_session_manager_fixes.module_credentials.CredentialsInterface;
import com.arkhive.components.test_session_manager_fixes.module_session_token.ActionToken;
import com.arkhive.components.test_session_manager_fixes.module_session_token.SessionToken;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by Chris Najar on 6/15/2014.
 */
public class TokenServerDirector implements TokenServerCallback, InterServerCallback,  Pausable {
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
    public void actionTokenReturned(ActionToken actionToken) {
        logger.debug("actionTokenReturned(" + actionToken.getId() + ")");
        actionTokenServer.receiveValidToken(actionToken);
    }

    @Override
    public void actionTokenExpired(ActionToken actionToken) {
        logger.debug("actionTokenExpired(" + actionToken.getId() + ")");
        actionTokenServer.receiveInvalidToken(actionToken);
    }

    @Override
    public void newActionTokenReturned(ActionToken actionToken) {
        logger.debug("newActionTokenReturned(" + actionToken.getId() + ")");
        actionTokenServer.receiveNewActionToken(actionToken);
    }

    @Override
    public void sessionTokenReturned(SessionToken sessionToken) {
        logger.debug("sessionTokenReturned(" + sessionToken.getId() + ")");
        sessionTokenServer.receiveValidToken(sessionToken);
    }

    @Override
    public void sessionTokenExpired(SessionToken sessionToken) {
        logger.debug("sessionTokenExpired(" + sessionToken.getId() + ")");
        sessionTokenServer.receiveInvalidToken(sessionToken);
    }

    @Override
    public void newSessionTokenReturned(SessionToken sessionToken) {
        logger.debug("newSessionTokenReturned(" + sessionToken.getId() + ")");
        sessionTokenServer.receiveNewSessionToken(sessionToken);
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
        return isPaused;
    }

    /*
        Interface methods: InterServerCallback
     */
    @Override
    public void requestSessionTokenInternal() {

    }

    @Override
    public void grantSessionTokenInternal(SessionToken sessionToken) {

    }

    @Override
    public void returnSessionTokenInternal(SessionToken sessionToken) {

    }
}
