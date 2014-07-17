package com.mediafire.sdk.tokenfarm;

/**
 * Created by  on 6/15/2014.
 */
public final class ActionToken extends Token {
    private final Type type;
    private long expiration;

    private ActionToken(Type type) {
        super();
        this.type = type;
    }

    public static ActionToken newInstance(Type type) {
        if (type == null) {
            return null;
        }

        return new ActionToken(type);
    }

    public Type getType() {
        return type;
    }


    public void setExpiration(long expiration) {
        this.expiration = expiration;
    }

    public boolean isExpired() {
        return (System.currentTimeMillis() + 3600000) >= expiration;
    }

    public enum Type {
        UPLOAD("upload"),
        IMAGE("image");

        private final String value;
        private Type(String value) {
            this.value = value;
        }

        public String toString() {
            return value;
        }
    }
}
