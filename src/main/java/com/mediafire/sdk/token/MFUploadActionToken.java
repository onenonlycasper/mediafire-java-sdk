package com.mediafire.sdk.token;

/**
 * Created by Chris Najar on 7/17/2014.
 */
public final class MFUploadActionToken extends MFActionToken {
    public MFUploadActionToken(String tokenString, long expiration) {
        super(tokenString, Type.UPLOAD, expiration);
    }


}
