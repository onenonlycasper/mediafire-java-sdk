package com.arkhive.components.test_session_manager_fixes.module_session_token;

import com.arkhive.components.test_session_manager_fixes.module_api_descriptor.ApiRequestObject;

/**
 * Created by Chris Najar on 6/15/2014.
 */
public final class ActionToken implements TokenInterface {


    @Override
    public String getTokenString() {
        return null;
    }

    @Override
    public void setTokenString(String token) {

    }

    @Override
    public void updateToken(ApiRequestObject apiRequestObject) {}

    @Override
    public String getTokenSignature() {
        return null;
    }

    @Override
    public void setTokenSignature(String signature) {

    }
}
