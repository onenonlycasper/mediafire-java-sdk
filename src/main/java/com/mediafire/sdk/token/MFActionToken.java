package com.mediafire.sdk.token;

public abstract class MFActionToken extends MFToken {
    private final long expiration;
    private final Type type;

    public MFActionToken(String tokenString, Type type, long expiration) {
        super(tokenString);
        this.type = type;
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
        return "MFActionToken [" + type.getValue() + "], expiry [" + expiration + "], token [" + tokenString + "]";
    }
}
