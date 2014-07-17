package com.mediafire.sdk.tokenfarm;

/**
 * Created by Chris Najar on 7/16/2014.
 */
public abstract class MFActionToken extends MFToken {
    private long expiration;
    private Type type;

    public MFActionToken(String tokenString, Type type, long expiration) {
        super(tokenString);
        this.type = type;
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
}
