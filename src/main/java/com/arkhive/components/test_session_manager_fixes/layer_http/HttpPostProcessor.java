package com.arkhive.components.test_session_manager_fixes.layer_http;

import com.arkhive.components.test_session_manager_fixes.module_api_descriptor.ApiRequestObject;
import com.arkhive.components.test_session_manager_fixes.module_session_token.ActionToken;
import com.arkhive.components.test_session_manager_fixes.module_session_token.Token;

/**
 * Created by Chris Najar on 6/15/2014.
 */
public final class HttpPostProcessor {
    private final ApiRequestObject apiRequestObject;

    public HttpPostProcessor(ApiRequestObject apiRequestObject) {
        this.apiRequestObject = apiRequestObject;
    }

    public void processApiRequestObject() {
        Token token = apiRequestObject.getToken();
        if (ActionToken.class.isInstance(token)) {

        } else {

        }
    }
}
