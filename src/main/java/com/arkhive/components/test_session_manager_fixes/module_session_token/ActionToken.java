package com.arkhive.components.test_session_manager_fixes.module_session_token;

/**
 * Created by Chris Najar on 6/15/2014.
 */
public final class ActionToken extends Token {
    private ActionToken(String id) {
        super(id);
    }

    @Override
    public String getTokenSignature() { return null; }

    public static ActionToken newInstance(String id) {
        return new ActionToken(id);
    }
}
