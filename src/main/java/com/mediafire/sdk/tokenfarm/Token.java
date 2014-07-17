package com.mediafire.sdk.tokenfarm;

/**
 * Created by  on 6/15/2014.
 */
public abstract class Token {
    protected String tokenString;

    public Token(String tokenString) {
        this.tokenString = tokenString;
    }

    public String getTokenString() {
        return tokenString;
    }
}
