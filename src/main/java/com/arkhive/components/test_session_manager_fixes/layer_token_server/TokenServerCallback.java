package com.arkhive.components.test_session_manager_fixes.layer_token_server;

import com.arkhive.components.test_session_manager_fixes.module_api_descriptor.ApiRequestObject;

/**
 * Created by Chris Najar on 6/16/2014.
 */
public interface TokenServerCallback {
    public void httpRequestFinished(ApiRequestObject apiRequestObject, boolean tokenStillValid);
}
