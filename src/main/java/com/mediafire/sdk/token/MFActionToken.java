package com.mediafire.sdk.token;

public class MFActionToken extends MFToken {
    private final long expiration;
    private final Type type;

    public MFActionToken(String tokenString, Type type, long expiration) {
        super(tokenString);
        this.type = type;
        if (expiration > 1440) {
            expiration = 1440;
        } else if (expiration < 1) {
            expiration = 1;
        } else {
            // expiration is ok.
        }

        // temporary - set max time.
        expiration = System.currentTimeMillis() + 86400000;

        this.expiration = expiration;
    }

    public boolean isExpired() {
        return (System.currentTimeMillis() + 3600000) >= expiration;
    }

    public String getType() {
        return type.getValue();
    }

    public enum Type {
        UPLOAD("upload"),
        IMAGE("image");

        private final String value;
        private Type(String value) {
            this.value = value;
        }

        public String getValue() {
            return value;
        }
    }

    @Override
    public String toString() {
        return "MFActionToken token [" + tokenString + "], type [" + type.getValue() + "], expiry [" + expiration + "]";
    }
}
