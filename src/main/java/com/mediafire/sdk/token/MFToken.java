package com.mediafire.sdk.token;

public abstract class MFToken {
    protected final String tokenString;

    /**
     * Constructor for creating a token given a token string.
     * @param tokenString
     */
    public MFToken(String tokenString) {
        this.tokenString = tokenString;
    }

    /**
     * get the token string of this object.
     * @return the token string of this object.
     */
    public String getTokenString() {
        return tokenString;
    }

    public String toString() {
        return "MFToken token [" + tokenString + "]";
    }
}
