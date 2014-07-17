package com.mediafire.sdk.token;

/**
 * Created by Chris Najar on 7/16/2014.
 */
public abstract class MFToken {
    protected String tokenString;

    public MFToken(String tokenString) {
        this.tokenString = tokenString;
    }

    public String getTokenString() {
        return tokenString;
    }
}
