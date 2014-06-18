package com.arkhive.components.test_session_manager_fixes;

import com.arkhive.components.test_session_manager_fixes.module_http_processor.HttpPeriProcessor;
import com.arkhive.components.test_session_manager_fixes.module_token_farm.TokenFarm;

/**
 * Created by Chris Najar on 6/17/2014.
 */
public class MediaFire {
    private HttpPeriProcessor httpPeriProcessor;
    private TokenFarm tokenFarm;

    public MediaFire() {}

    public HttpPeriProcessor getHttpPeriProcessor() {
        return httpPeriProcessor;
    }

    public TokenFarm getTokenFarm() {
        return tokenFarm;
    }
}
