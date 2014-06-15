package com.arkhive.components.test_session_manager_fixes.module_session_token;

/**
 * Created by Chris Najar on 6/15/2014.
 */
public abstract class Token implements TokenInterface {
    private String token;
    public final String id;

    protected Token(String id) {
        this.id = id;
    }

    public String getId() {
        return id;
    }

    @Override
    public String getTokenString() {
        return token;
    }

    @Override
    public void setTokenString(String token) {
        this.token = token;
    }

    public abstract String getTokenSignature();
}
