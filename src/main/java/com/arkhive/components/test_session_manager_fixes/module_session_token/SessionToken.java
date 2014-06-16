package com.arkhive.components.test_session_manager_fixes.module_session_token;

/**
 * Created by Chris Najar on 6/15/2014.
 */
public final class SessionToken extends Token {
    private String signature;

    private SessionToken(String id) {
        super(id);
    }

    public static SessionToken newInstance(String id) {
        return new SessionToken(id);
    }

    @Override
    public String getTokenSignature() {
        System.out.println("setTokenString()");
        return signature;
    }

    public void setTokenSignature(String signature) {
        System.out.println("setTokenSignature()");
        this.signature = signature;
    }
}
