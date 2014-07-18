package com.mediafire.sdk.token;

/**
 * TODO: doc
 */
public final class MFUploadActionToken extends MFActionToken {
    public MFUploadActionToken(String tokenString, long expiration) {
        super(tokenString, Type.UPLOAD, expiration);
    }


}
