package com.mediafire.sdk.config;

import java.util.Map;

/**
 * Created by Chris Najar on 7/17/2014.
 */
public interface MFCredentials {
    public boolean setCredentials(Map<String, String> userCredentials);

    public Map<String, String> getCredentials();

    public void clearCredentials();

    public MFDefaultCredentials.UserCredentialsType getUserCredentialsType();
}
