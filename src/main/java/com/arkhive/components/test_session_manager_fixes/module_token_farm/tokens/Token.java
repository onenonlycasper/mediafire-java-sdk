package com.arkhive.components.test_session_manager_fixes.module_token_farm.tokens;

import com.arkhive.components.test_session_manager_fixes.module_token_farm.tokens.interfaces.TokenInterface;

/**
 * Created by  on 6/15/2014.
 */
public abstract class Token implements TokenInterface {
    private static final String TAG = Token.class.getSimpleName();
    private volatile String token;

    protected Token() {}

    @Override
    public synchronized String getTokenString() {
        System.out.println(TAG + " getTokenString()");
        return token;
    }

    @Override
    public synchronized void setTokenString(String token) {
        System.out.println(TAG + " setTokenString()");
        this.token = token;
    }
}
