package com.arkhive.components.test_session_manager_fixes.module_session_token;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by Chris Najar on 6/15/2014.
 */
public final class ActionToken extends Token {
    private Type type;
    private Logger logger = LoggerFactory.getLogger(ActionToken.class);

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

    @Override
    public String getTokenSignature() { return null; }

    public Type getType() {
        logger.debug("getType()");
        return type;
    }

    public enum Type {
        UPLOAD, IMAGE,
    }
}
