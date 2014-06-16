package com.arkhive.components.test_session_manager_fixes.module_session_token;

/**
 * Created by Chris Najar on 6/15/2014.
 */
public abstract class Token implements TokenInterface {
    private String token;
    private final String id;

    protected Token(String id) {
        System.out.println("Token created: " + id);
        this.id = id;
    }

    public String getId() {
        System.out.println("getId()");
        return id;
    }

    @Override
    public String getTokenString() {
        System.out.println("getTokenString()");
        return token;
    }

    @Override
    public void setTokenString(String token) {
        System.out.println("setTokenString()");
        this.token = token;
    }
}
