package com.arkhive.components.test_session_manager_fixes.module_credentials;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Chris Najar on 6/15/2014.
 */
public final class LoginCredentials implements CredentialsInterface {
    private static final String KEY_EMAIL = "email";
    private static final String KEY_PASSWORD = "password";
    private static final String KEY_FB_ACCESS_TOKEN = "fb_access_token";
    private static final String KEY_FB_PERMANENT_ACCESS_TOKEN = "permanent_token";
    private static final String KEY_TW_OAUTH_TOKEN = "tw_oauth_token";
    private static final String KEY_TW_OAUTH_TOKEN_SECRET = "tw_oauth_token_secret";

    private Map<String, String> credentials = new HashMap<String, String>();
    private Logger logger = LoggerFactory.getLogger(LoginCredentials.class);

    @Override
    public Map<String, String> getCredentials() {
        logger.debug("getCredentials()");
        return credentials;
    }

    @Override
    public void setCredentials(Map<String, String> credentials) {
        logger.debug("setCredentials()");
        this.credentials = credentials;
    }

    @Override
    public void clearCredentials() {
        logger.debug("clearCredentials()");
        credentials.clear();
    }
}
