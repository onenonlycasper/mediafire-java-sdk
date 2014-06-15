package com.arkhive.components.test_session_manager_fixes.module_credentials;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Chris Najar on 6/15/2014.
 */
public final class CredentialsLayer implements CredentialsInterface {
    private Map<String, String> credentials = new HashMap<String, String>();
    private Logger logger = LoggerFactory.getLogger(CredentialsLayer.class);

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
