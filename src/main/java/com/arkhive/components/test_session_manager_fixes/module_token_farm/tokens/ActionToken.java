package com.arkhive.components.test_session_manager_fixes.module_token_farm.tokens;

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

    /**
     * Sets the expiration of the token. The time should use System.currentTimeMillis() to get the current time
     * and then add 24 hours since all action tokens are set to 24 hour expiration time.
     * @param expiration
     */
    public void setExpiration(long expiration) {
        this.expiration = expiration;
    }

    /**
     * All action tokens are set to 24 hour expiration time, but if the expiration time is in 1 hour, then it is counted
     * as expired.
     * @return false if not within 1 hour of expiration, true if at most within 1 hour of expiration.
     */
    public boolean isExpired() {
        return (System.currentTimeMillis() + 3600000) >= expiration;
//        return (System.currentTimeMillis() + 3600000) >= expiration;
    }

    public enum Type {
        UPLOAD("upload"),
        IMAGE("image");

        private String value;
        private Type(String value) {
            this.value = value;
        }

        public String toString() {
            return value;
        }
    }
}
