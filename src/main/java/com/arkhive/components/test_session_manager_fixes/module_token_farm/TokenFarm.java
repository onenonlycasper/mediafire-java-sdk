package com.arkhive.components.test_session_manager_fixes.module_token_farm;

import com.arkhive.components.test_session_manager_fixes.module_credentials.ApplicationCredentials;
import com.arkhive.components.test_session_manager_fixes.module_http_processor.HttpPeriProcessor;
import com.arkhive.components.test_session_manager_fixes.module_session_token.SessionToken;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by Chris Najar on 6/16/2014.
 */
public class TokenFarm {
    private static Logger logger = LoggerFactory.getLogger(TokenFarm.class);
    private ApplicationCredentials applicationCredentials;
    private HttpPeriProcessor httpPeriProcessor;

    private TokenFarm(ApplicationCredentials applicationCredentials, HttpPeriProcessor httpPeriProcessor) {
        this.applicationCredentials = applicationCredentials;
        this.httpPeriProcessor = httpPeriProcessor;
    }

    private static TokenFarm instance;

    public static TokenFarm newInstance(ApplicationCredentials applicationCredentials,
                                        HttpPeriProcessor httpPeriProcessor) throws TokenFarmException {
        if (instance != null) {
            throw new TokenFarmException("Cannot create a new instance without calling shutdown()");
        }
        if (instance == null) {
            instance = new TokenFarm(applicationCredentials, httpPeriProcessor);
        }
        logger.debug("TokenFarm initialized");
        return instance;
    }

    public static TokenFarm getInstance() {
        return instance;
    }

    public static void shutdown() {
        logger.debug("TokenFarm shutting down");
        // (TODO) do other stuff to clean up references.
        instance = null;
        logger.debug("TokenFarm shut down");
    }

    public SessionToken getNewSessionToken() {
        logger.debug("getNewSessionToken()");
        return null;
    }
}
