package com.arkhive.components.test_session_manager_fixes.module_token_farm.tokens;

import com.arkhive.components.test_session_manager_fixes.module_token_farm.tokens.interfaces.TokenInterface;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by  on 6/15/2014.
 */
public abstract class Token implements TokenInterface {
    private volatile String token;
    private final Logger logger = LoggerFactory.getLogger(Token.class);

    protected Token() {}

    @Override
    public synchronized String getTokenString() {
        logger.info(" getTokenString()");
        return token;
    }

    @Override
    public synchronized void setTokenString(String token) {
        logger.info(" setTokenString()");
        this.token = token;
    }
}
