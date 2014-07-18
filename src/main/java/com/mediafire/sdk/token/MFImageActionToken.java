package com.mediafire.sdk.token;

/**
 * TODO: doc
 */
public final class MFImageActionToken extends MFActionToken {
    public MFImageActionToken(String tokenString, long expiration) {
        super(tokenString, Type.IMAGE, expiration);
    }
}
