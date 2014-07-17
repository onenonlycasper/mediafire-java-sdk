package com.mediafire.sdk.tokenfarm;

/**
 * Created by Chris Najar on 7/16/2014.
 */
public final class ImageActionToken extends ActionToken {
    public ImageActionToken(String tokenString, long expiration) {
        super(tokenString, Type.IMAGE, expiration);
    }
}
