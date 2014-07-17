package com.mediafire.sdk.tokenfarm;

/**
 * Created by  on 6/15/2014.
 */
public final class MFUploadActionToken extends MFActionToken {
    public MFUploadActionToken(String tokenString, long expiration) {
        super(tokenString, Type.UPLOAD, expiration);
    }


}
