package com.arkhive.components.test_session_manager_fixes.module_credentials;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Chris Najar on 6/15/2014.
 */
public final class CredentialsLayer implements CredentialsInterface {
    private Map<String, String> credentials = new HashMap<String, String>();

    @Override
    public Map<String, String> getCredentials() {
        return credentials;
    }

    @Override
    public void setCredentials(Map<String, String> credentials) {
        this.credentials = credentials;
    }

    @Override
    public void clearCredentials() {
        credentials.clear();
    }
}
