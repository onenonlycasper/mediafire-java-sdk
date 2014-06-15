package com.arkhive.components.test_session_manager_fixes.module_session_token;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by Chris Najar on 6/15/2014.
 */
public abstract class Token implements TokenInterface {
    private String token;
    private final String id;
    private final Logger logger = LoggerFactory.getLogger(Token.class);

    protected Token(String id) {
        logger.debug("Token created: " + id);
        this.id = id;
    }

    public String getId() {
        logger.debug("getId()");
        return id;
    }

    @Override
    public String getTokenString() {
        logger.debug("getTokenString()");
        return token;
    }

    @Override
    public void setTokenString(String token) {
        logger.debug("setTokenString()");
        this.token = token;
    }

    public abstract String getTokenSignature();
}
