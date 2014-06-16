package com.arkhive.components.test_session_manager_fixes.module_credentials;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Created by Chris Najar on 6/15/2014.
 */
public class ApplicationCredentials implements CredentialsInterface {
    private Map<String, String> userCredentials = new HashMap<String, String>();
    private UserCredentialsType userCredentialsType;
    private boolean credentialsSet;
    private final String appId;
    private final String apiKey;

    public ApplicationCredentials(String appId, String apiKey) throws CredentialsException {
        if (appId == null || apiKey == null) {
            throw new CredentialsException("appId and apiKey must not be null.");
        }
        this.appId = appId;
        this.apiKey = apiKey;
        credentialsSet = false;
        userCredentialsType = UserCredentialsType.UNSET;
    }

    /**
     * attempts to add user credentials.
     *
     * @param credentials - a map of user credentials based on the following:
     * email : The email address of the user's MediaFire account
     *                        AND
     * password : The password of the user's MediaFire account.
     *
     *                        OR
     *
     * fb_access_token : The Facebook access token which corresponds with the user's MediaFire account
     * (this must be obtained from Facebook's API).
     *
     *                        OR
     *
     * tw_oauth_token : The Twitter OAuth token which corresponds with the user's MediaFire account
     * (this must be obtained from Twitter's API)
     *                         AND
     * tw_oauth_token_secret : The Twitter OAuth Token Secret Key (obtained from Twitter's API).
     *
     * @return - true if credentials are stored, false if not.
     */
    public boolean setUserCredentials(HashMap<String, String> credentials) throws CredentialsException {
        System.out.println("addUserCredentials()");
        if (credentialsSet) {
            throw new CredentialsException("credentials are already set. use clearCredentials()");
        }
        if (isFacebookCredentials(credentials)) {
            setCredentials(credentials);
            userCredentialsType = UserCredentialsType.FACEBOOK;
            return true;
        }

        if (isTwitterCredentials(credentials)) {
            LinkedHashMap<String, String> credentialsMap = new LinkedHashMap<String, String>(2);
            credentialsMap.put("tw_oauth_token", credentials.get("tw_oauth_token"));
            credentialsMap.put("tw_oauth_token_secret", credentials.get("tw_oauth_token_secret"));
            setCredentials(credentialsMap);
            userCredentialsType = UserCredentialsType.TWITTER;
            return true;
        }

        if (isMediaFireCredentials(credentials)) {
            LinkedHashMap<String, String> credentialsMap = new LinkedHashMap<String, String>(2);
            credentialsMap.put("email", credentials.get("email"));
            credentialsMap.put("password", credentials.get("password"));
            setCredentials(credentialsMap);
            setCredentials(credentials);
            userCredentialsType = UserCredentialsType.MEDIAFIRE;
            return true;
        }

        return false;
    }

    private void setCredentials(Map<String, String> credentials) {
        System.out.println("setCredentials()");
        credentialsSet = true;
        userCredentials = credentials;
    }

    @Override
    public Map<String, String> getCredentials() throws CredentialsException {
        if (userCredentials == null || userCredentials.isEmpty()) {
            throw new CredentialsException("invalid credentials");
        }
        return userCredentials;
    }

    private void clearCredentials() {
        System.out.println("clearCredentials()");
        userCredentials.clear();
        userCredentialsType = UserCredentialsType.UNSET;
        credentialsSet = false;
    }

    public boolean isCredentialsSet() {
        System.out.println("isCredentialsSet()");
        return credentialsSet;
    }

    @Override
    public String getAppId() {
        System.out.println("getAppId()");
        return appId;
    }

    @Override
    public String getApiKey() {
        System.out.println("getApiKey()");
        return apiKey;
    }

    public UserCredentialsType getUserCredentialsType() {
        return userCredentialsType;
    }

    private boolean isFacebookCredentials(HashMap<String, String> credentials) {
        return credentials != null && credentials.size() == 1 && credentials.containsKey("fb_access_token");
    }

    private boolean isTwitterCredentials(HashMap<String, String> credentials) {
        return credentials != null && credentials.size() == 2 && credentials.containsKey("tw_oauth_token") && credentials.containsKey("tw_oauth_token_secret");
    }

    private boolean isMediaFireCredentials(HashMap<String, String> credentials) {
        return credentials != null && credentials.size() == 2 && credentials.containsKey("email") && credentials.containsKey("password");
    }

    public enum UserCredentialsType {
        FACEBOOK("Facebook"),
        TWITTER("Twitter"),
        MEDIAFIRE("MediaFire"),
        UNSET("N/A");

        private final String value;

        private UserCredentialsType(String value) {
            this.value = value;
        }

        public String getValue() {
            return value;
        }
    }
}
