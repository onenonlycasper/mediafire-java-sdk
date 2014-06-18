package com.arkhive.components.test_session_manager_fixes.module_token_farm.tokens;

/**
 * Created by  on 6/15/2014.
 */
public final class ActionToken extends Token {
    private final Type type;

    private ActionToken(Type type, String id) {
        super(id);
        this.type = type;
    }

    public static ActionToken newInstance(Type type, String id) {
        if (type == null) {
            return null;
        }

        return new ActionToken(type, id);
    }

    public Type getType() {
        return type;
    }

    public enum Type {
        UPLOAD, IMAGE,
    }
}
