package com.mediafire.sdk.token;

/**
 * TODO: doc
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
