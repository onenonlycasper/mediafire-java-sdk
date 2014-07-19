package com.mediafire.sdk.token;

public abstract class MFToken {
    protected final String tokenString;

    public MFToken(String tokenString) {
        this.tokenString = tokenString;
    }

    public String getTokenString() {
        return tokenString;
    }

    public String toString() {
        return "MFToken token [" + tokenString + "]";
    }
}
