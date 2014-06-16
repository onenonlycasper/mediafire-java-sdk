package com.arkhive.components.test_session_manager_fixes.module_token_farm;

import com.arkhive.components.test_session_manager_fixes.module_credentials.ApplicationCredentials;
import com.arkhive.components.test_session_manager_fixes.module_http_processor.HttpPeriProcessor;
import com.arkhive.components.test_session_manager_fixes.module_session_token.SessionToken;

/**
 * Created by Chris Najar on 6/16/2014.
 */
public class TokenFarm {

    private TokenFarm(ApplicationCredentials applicationCredentials, HttpPeriProcessor httpPeriProcessor) {
        ApplicationCredentials applicationCredentials1 = applicationCredentials;
        HttpPeriProcessor httpPeriProcessor1 = httpPeriProcessor;
    }

    private static TokenFarm instance;

    public static TokenFarm newInstance(ApplicationCredentials applicationCredentials,
                                        HttpPeriProcessor httpPeriProcessor) throws TokenFarmException {
        if (instance != null) {
            throw new TokenFarmException("Cannot create a new instance without calling shutdown()");
        }
        instance = new TokenFarm(applicationCredentials, httpPeriProcessor);
        System.out.println("TokenFarm initialized");
        return instance;
    }

    public static TokenFarm getInstance() {
        return instance;
    }

    public static void shutdown() {
        System.out.println("TokenFarm shutting down");
        // (TODO) do other stuff to clean up references.
        instance = null;
        System.out.println("TokenFarm shut down");
    }

    public SessionToken getNewSessionToken() {
        System.out.println("getNewSessionToken()");
        return null;
    }
}
