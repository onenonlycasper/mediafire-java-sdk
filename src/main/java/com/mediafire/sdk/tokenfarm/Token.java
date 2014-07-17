package com.mediafire.sdk.tokenfarm;

/**
 * Created by  on 6/15/2014.
 */
public abstract class Token {
    private volatile String token;

    Token() {}

    public synchronized String getTokenString() {
        return token;
    }

    public synchronized void setTokenString(String token) {
        this.token = token;
    }
}
