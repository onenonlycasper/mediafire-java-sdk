package com.arkhive.components.test_session_manager_fixes.module_session_token;

import com.arkhive.components.test_session_manager_fixes.module_api_descriptor.ApiRequestObject;

/**
 * Created by Chris Najar on 6/15/2014.
 */
public final class SessionToken extends Token implements TokenInterface {
    String token;
    private String signature;

    private SessionToken(String id) {
        super(id);
    }

    public static SessionToken newInstance(String id) {
        return new SessionToken(id);
    }

    @Override
    public String getTokenString() {
        return token;
    }

    @Override
    public void setTokenString(String token) {
        this.token = token;
    }

    @Override
    public void updateToken(ApiRequestObject apiRequestObject) { }

    @Override
    public String getTokenSignature() {
        return signature;
    }

    @Override
    public void setTokenSignature(String signature) {
        this.signature = signature;
    }
}
