package com.arkhive.components.test_session_manager_fixes.module_token_farm.tokens;

import com.arkhive.components.test_session_manager_fixes.module_token_farm.tokens.interfaces.TokenInterface;

/**
 * Created by Chris Najar on 6/15/2014.
 */
public abstract class Token implements TokenInterface {
    private static final String TAG = Token.class.getSimpleName();
    private String token;
    private final String id;

    protected Token(String id) {
        System.out.println(TAG + " Token created: " + id);
        this.id = id;
    }

    public String getId() {
        System.out.println(TAG + " getId()");
        return id;
    }

    @Override
    public String getTokenString() {
        System.out.println(TAG + " getTokenString()");
        return token;
    }

    @Override
    public void setTokenString(String token) {
        System.out.println(TAG + " setTokenString()");
        this.token = token;
    }
}
