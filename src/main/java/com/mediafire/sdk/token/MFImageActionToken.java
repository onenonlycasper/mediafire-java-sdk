package com.mediafire.sdk.token;

/**
 * Created by Chris Najar on 7/16/2014.
 */
public final class MFImageActionToken extends MFActionToken {
    public MFImageActionToken(String tokenString, long expiration) {
        super(tokenString, Type.IMAGE, expiration);
    }
}
