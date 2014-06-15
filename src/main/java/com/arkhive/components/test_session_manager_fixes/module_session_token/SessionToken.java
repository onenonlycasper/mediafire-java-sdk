package com.arkhive.components.test_session_manager_fixes.module_session_token;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by Chris Najar on 6/15/2014.
 */
public final class SessionToken extends Token {
    private String signature;
    private Logger logger = LoggerFactory.getLogger(SessionToken.class);

    private SessionToken(String id) {
        super(id);
    }

    public static SessionToken newInstance(String id) {
        return new SessionToken(id);
    }

    @Override
    public String getTokenSignature() {
        logger.debug("setTokenString()");
        return signature;
    }

    public void setTokenSignature(String signature) {
        logger.debug("setTokenSignature()");
        this.signature = signature;
    }
}
