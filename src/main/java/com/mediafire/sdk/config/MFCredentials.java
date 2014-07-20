package com.mediafire.sdk.config;

import java.util.Map;

/**
 * TODO: doc
 */
public interface MFCredentials {
    public boolean setCredentials(Map<String, String> userCredentials);

    public Map<String, String> getCredentials();

    public void clearCredentials();

    public MFDefaultCredentials.UserCredentialsType getUserCredentialsType();
}
