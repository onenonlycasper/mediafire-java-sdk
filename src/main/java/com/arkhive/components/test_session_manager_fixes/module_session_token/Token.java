package com.arkhive.components.test_session_manager_fixes.module_session_token;

/**
 * Created by Chris Najar on 6/15/2014.
 */
public abstract class Token implements TokenInterface {
    public final String id;

    public Token(String id) {
        this.id = id;
    }

    public String getId() {
        return id;
    }
}
