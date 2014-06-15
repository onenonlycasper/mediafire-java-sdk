package com.arkhive.components.test_session_manager_fixes.module_credentials;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Chris Najar on 6/15/2014.
 */
public class CombinedCredentials implements CredentialsInterface {
    private HashMap<String, String> combinedCredentials = new HashMap<String, String>();
    private Logger logger = LoggerFactory.getLogger(LoginCredentials.class);

    public CombinedCredentials(CredentialsInterface userCredentials, CredentialsInterface developerCredentials) {
        combinedCredentials.putAll((Map<? extends String, ? extends String>) userCredentials);
        combinedCredentials.putAll((Map<? extends String, ? extends String>) developerCredentials);
    }

    @Override
    public Map<String, String> getCredentials() {
        return combinedCredentials;
    }

    @Override
    public void setCredentials(Map<String, String> credentials) {}

    @Override
    public void clearCredentials() {}
}
