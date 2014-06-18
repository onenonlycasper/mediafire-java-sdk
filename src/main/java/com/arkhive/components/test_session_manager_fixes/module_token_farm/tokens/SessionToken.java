package com.arkhive.components.test_session_manager_fixes.module_token_farm.tokens;

/**
 * Created by  on 6/15/2014.
 */
public final class SessionToken extends Token {
    private String time;
    private String secretKey;
    private String pkey;

    private SessionToken(String id) {
        super(id);
    }

    public static SessionToken newInstance(String id) {
        return new SessionToken(id);
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

    public String getSecretKey() {
        return secretKey;
    }

    public void updateSecretKey() {
        long newKey = Long.valueOf(secretKey) * 16807;
        newKey = newKey % 2147483647;
        secretKey = String.valueOf(newKey);
    }

    public void setPkey(String pkey) {
        this.pkey = pkey;
    }

    public String getPkey() {
        return pkey;
    }
}
