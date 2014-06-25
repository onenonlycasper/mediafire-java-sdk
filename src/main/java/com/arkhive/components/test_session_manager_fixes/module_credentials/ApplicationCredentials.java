package com.arkhive.components.test_session_manager_fixes.module_credentials;

import com.arkhive.components.test_session_manager_fixes.Configuration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Created by  on 6/15/2014.
 */
public final class ApplicationCredentials {
    private static final String TAG = ApplicationCredentials.class.getSimpleName();
    private static String MEDIAFIRE_PARAMETER_EMAIL = "email";
    private static String MEDIAFIRE_PARAMETER_PASSWORD = "password";
    private static String TWITTER_PARAMETER_TW_OAUTH_TOKEN = "tw_oauth_token";
    private static String TWITTER_PARAMETER_TW_OAUTH_TOKEN_SECRET = "tw_oauth_token_secret";
    private static String FACEBOOK_PARAMETER_FB_ACCESS_TOKEN = "fb_access_token";

    private final Logger logger = LoggerFactory.getLogger(ApplicationCredentials.class);

    private Map<String, String> userCredentials = new HashMap<String, String>();
    private UserCredentialsType userCredentialsType;

    private boolean credentialsValid;

    private String appId;
    private String apiKey;

    public ApplicationCredentials(String appId, String apiKey) {
        this.appId = appId;
        this.apiKey = apiKey;
        credentialsValid = false;
        userCredentialsType = UserCredentialsType.UNSET;
    }

    public ApplicationCredentials(Configuration configuration) {
        this(configuration.getAppId(), configuration.getApiKey());
    }

    /**
     * attempts to add user credentials.
     *
     * @param credentials - a map of user credentials based on the following:
     *
     * @return - true if credentials are stored, false if not.
     */
    public boolean setUserCredentials(Map<String, String> credentials) {
        logger.info(" addUserCredentials()");
        if (isFacebookCredentials(credentials)) {
            setCredentials(credentials);
            userCredentialsType = UserCredentialsType.FACEBOOK;
            return true;
        }

        if (isTwitterCredentials(credentials)) {
            LinkedHashMap<String, String> credentialsMap = new LinkedHashMap<String, String>(2);
            credentialsMap.put(TWITTER_PARAMETER_TW_OAUTH_TOKEN, credentials.get(TWITTER_PARAMETER_TW_OAUTH_TOKEN));
            credentialsMap.put(TWITTER_PARAMETER_TW_OAUTH_TOKEN_SECRET, credentials.get(TWITTER_PARAMETER_TW_OAUTH_TOKEN_SECRET));
            setCredentials(credentialsMap);
            userCredentialsType = UserCredentialsType.TWITTER;
            return true;
        }

        if (isMediaFireCredentials(credentials)) {
            LinkedHashMap<String, String> credentialsMap = new LinkedHashMap<String, String>(2);
            credentialsMap.put(MEDIAFIRE_PARAMETER_EMAIL, credentials.get(MEDIAFIRE_PARAMETER_EMAIL));
            credentialsMap.put(MEDIAFIRE_PARAMETER_PASSWORD, credentials.get(MEDIAFIRE_PARAMETER_PASSWORD));
            setCredentials(credentialsMap);
            userCredentialsType = UserCredentialsType.MEDIAFIRE;
            return true;
        }

        return false;
    }

    private void setCredentials(Map<String, String> credentials) {
        logger.info(" setCredentials()");
        userCredentials = credentials;
    }

    public Map<String, String> getCredentials() {
        return userCredentials;
    }

    public void clearCredentials() {
        logger.info(" clearCredentials()");
        userCredentials.clear();
        userCredentialsType = UserCredentialsType.UNSET;
    }

    public boolean isCredentialsValid() {
        logger.info(" isCredentialsValid()");
        return credentialsValid;
    }

    public void setCredentialsValid(boolean credentialsValid) {
        this.credentialsValid = credentialsValid;
    }

    public String getAppId() {
        logger.info(" getAppId()");
        return appId;
    }

    public String getApiKey() {
        logger.info(" getApiKey()");
        return apiKey;
    }

    public UserCredentialsType getUserCredentialsType() {
        return userCredentialsType;
    }

    private boolean isFacebookCredentials(Map<String, String> credentials) {
        return credentials != null && credentials.size() == 1 && credentials.containsKey(FACEBOOK_PARAMETER_FB_ACCESS_TOKEN);
    }

    private boolean isTwitterCredentials(Map<String, String> credentials) {
        return credentials != null && credentials.size() == 2 && credentials.containsKey(TWITTER_PARAMETER_TW_OAUTH_TOKEN) && credentials.containsKey(TWITTER_PARAMETER_TW_OAUTH_TOKEN_SECRET);
    }

    private boolean isMediaFireCredentials(Map<String, String> credentials) {
        return credentials != null && credentials.size() == 2 && credentials.containsKey(MEDIAFIRE_PARAMETER_EMAIL) && credentials.containsKey(MEDIAFIRE_PARAMETER_PASSWORD);
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
