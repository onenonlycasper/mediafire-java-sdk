package com.arkhive.components.test_session_manager_fixes.module_credentials;

import java.util.Map;

/**
 * Created by Chris Najar on 6/15/2014.
 */
public interface CredentialsInterface {
    /**
     * Return the credentials needed to perform the API request.
     *
     * @return A Map containing the credentials needed.
     */
    public Map<String, String> getCredentials() throws CredentialsException;

    /**
     * Return the app id needed to perform API requests.
     * @return the app id.
     */
    public String getAppId();

    /**
     * Return the api key needed to perform API requests.
     * @return - the api key.
     */
    public String getApiKey();
}
