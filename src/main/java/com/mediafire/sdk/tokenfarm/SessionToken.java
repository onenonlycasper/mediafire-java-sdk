package com.mediafire.sdk.tokenfarm;

/**
 * Created by  on 6/15/2014.
 */
public final class SessionToken extends Token {
    private String time;
    private volatile String secretKey;
    private String pkey;

    private SessionToken() {
        super();
    }

    public static SessionToken newInstance() {
        return new SessionToken();
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getTime() {
        return time;
    }

    public void setSecretKey(String secretKey) {
        this.secretKey = secretKey;
    }

    public synchronized String getSecretKey() {
        return secretKey;
    }

    public void setPkey(String pkey) {
        this.pkey = pkey;
    }

    public String getPkey() {
        return pkey;
    }
}
