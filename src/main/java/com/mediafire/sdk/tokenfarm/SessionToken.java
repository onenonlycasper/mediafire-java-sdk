package com.mediafire.sdk.tokenfarm;

/**
 * Created by  on 6/15/2014.
 */
public final class SessionToken extends Token {
    private String time;
    private String secretKey;
    private String pkey;

    private SessionToken(String tokenString, String secretKey, String time, String pkey) {
        super(tokenString);
        this.secretKey = secretKey;
        this.time = time;
        this.pkey = pkey;
    }

    public String getTime() {
        return time;
    }

    public String getSecretKey() {
        return secretKey;
    }

    public String getPkey() {
        return pkey;
    }

    private SessionToken getUpdatedSessionToken() {
        long newKey = Long.valueOf(secretKey) * 16807;
        newKey = newKey % 2147483647;
        String newSecretKey = String.valueOf(newKey);

        return new SessionToken(tokenString, newSecretKey, time, pkey);
    }
}
