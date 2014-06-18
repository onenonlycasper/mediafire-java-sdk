package com.arkhive.components.test_session_manager_fixes;

import com.arkhive.components.test_session_manager_fixes.module_credentials.ApplicationCredentials;
import com.arkhive.components.test_session_manager_fixes.module_http_processor.HttpPeriProcessor;
import com.arkhive.components.test_session_manager_fixes.module_token_farm.TokenFarm;

/**
 * Created by Chris Najar on 6/17/2014.
 */
public class MediaFire {
    private HttpPeriProcessor httpPeriProcessor;
    private ApplicationCredentials applicationCredentials;
    private TokenFarm tokenFarm;
    private Configuration configuration;

    public MediaFire() {}

    public void setConfiguration(Configuration configuration) {
        this.configuration = configuration;
    }

    public HttpPeriProcessor getHttpPeriProcessor() {
        return httpPeriProcessor;
    }

    public TokenFarm getTokenFarm() {
        return tokenFarm;
    }

    public void startup() {

    }

    public void shutdown() {

    }
}
