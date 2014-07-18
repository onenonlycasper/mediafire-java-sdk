package com.mediafire.sdk.token;

/**
 * TODO: doc
 */
public final class MFSessionToken extends MFToken {
    private String time;
    private String secretKey;
    private String pkey;
    private String ekey;

    public MFSessionToken(String tokenString, String secretKey, String time, String pkey, String ekey) {
        super(tokenString);
        this.secretKey = secretKey;
        this.time = time;
        this.pkey = pkey;
        this.ekey = ekey;
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

    public String getEkey() {
        return ekey;
    }

    public MFSessionToken getUpdatedSessionToken() {
        long newKey = Long.valueOf(secretKey) * 16807;
        newKey = newKey % 2147483647;
        String newSecretKey = String.valueOf(newKey);

        return new MFSessionToken(tokenString, newSecretKey, time, pkey, ekey);
    }

    @Override
    public String toString() {
        return "MFSessionToken time [" + time + "], secret key [" + secretKey + "], pkey [" + pkey + "], ekey [" + ekey + "], token [" + tokenString + "]";
    }
}
