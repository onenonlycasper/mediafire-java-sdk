package com.arkhive.components.core.module_token_farm.tokens;

import com.arkhive.components.core.Configuration;
import com.arkhive.components.core.module_token_farm.tokens.interfaces.TokenInterface;



/**
 * Created by  on 6/15/2014.
 */
public abstract class Token implements TokenInterface {
    private static final String TAG = Token.class.getCanonicalName();
    private volatile String token;

    Token() {}

    @Override
    public synchronized String getTokenString() {
        Configuration.getErrorTracker().i(TAG, "getTokenString()");
        return token;
    }

    @Override
    public synchronized void setTokenString(String token) {
        Configuration.getErrorTracker().i(TAG, "setTokenString()");
        this.token = token;
    }
}
