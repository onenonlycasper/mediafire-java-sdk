package com.arkhive.components.core.module_credentials;

import com.arkhive.components.core.Configuration;



import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Created by  on 6/15/2014.
 */
public final class ApplicationCredentials {
    private static final String TAG = ApplicationCredentials.class.getSimpleName();
    public static final String MEDIAFIRE_PARAMETER_EMAIL = "email";
    public static final String MEDIAFIRE_PARAMETER_PASSWORD = "password";
    public static final String TWITTER_PARAMETER_TW_OAUTH_TOKEN = "tw_oauth_token";
    public static final String TWITTER_PARAMETER_TW_OAUTH_TOKEN_SECRET = "tw_oauth_token_secret";
    public static final String FACEBOOK_PARAMETER_FB_ACCESS_TOKEN = "fb_access_token";

    private Map<String, String> userCredentials = new HashMap<String, String>();
    private UserCredentialsType userCredentialsType;

    private final String appId;
    private final String apiKey;

    private ApplicationCredentials(String appId, String apiKey) {
        this.appId = appId;
        this.apiKey = apiKey;
        userCredentialsType = UserCredentialsType.UNSET;
    }

    public ApplicationCredentials(Configuration configuration) {
        this(configuration.getAppId(), configuration.getApiKey());
    }

    /**
     * attempts to add user credentials.
     *
     * @param userCredentials - a map of user credentials based on the following:
     *
     * @return - true if credentials are stored, false if not.
     */
    public boolean setCredentials(Map<String, String> userCredentials) {
        Configuration.getErrorTracker().i(TAG, "addUserCredentials()");
        if (isFacebookCredentials(userCredentials)) {
            this.userCredentials = userCredentials;
            userCredentialsType = UserCredentialsType.FACEBOOK;
            return true;
        }

        if (isTwitterCredentials(userCredentials)) {
            LinkedHashMap<String, String> credentialsMap = new LinkedHashMap<String, String>(2);
            credentialsMap.put(TWITTER_PARAMETER_TW_OAUTH_TOKEN, userCredentials.get(TWITTER_PARAMETER_TW_OAUTH_TOKEN));
            credentialsMap.put(TWITTER_PARAMETER_TW_OAUTH_TOKEN_SECRET, userCredentials.get(TWITTER_PARAMETER_TW_OAUTH_TOKEN_SECRET));
            this.userCredentials = credentialsMap;
            userCredentialsType = UserCredentialsType.TWITTER;
            return true;
        }

        if (isMediaFireCredentials(userCredentials)) {
            LinkedHashMap<String, String> credentialsMap = new LinkedHashMap<String, String>(2);
            credentialsMap.put(MEDIAFIRE_PARAMETER_EMAIL, userCredentials.get(MEDIAFIRE_PARAMETER_EMAIL));
            credentialsMap.put(MEDIAFIRE_PARAMETER_PASSWORD, userCredentials.get(MEDIAFIRE_PARAMETER_PASSWORD));
            this.userCredentials = credentialsMap;
            userCredentialsType = UserCredentialsType.MEDIAFIRE;
            return true;
        }

        return false;
    }

    public Map<String, String> getCredentials() {
        return userCredentials;
    }

    public void clearCredentials() {
        Configuration.getErrorTracker().i(TAG, "clearCredentials()");
        userCredentials.clear();
        userCredentialsType = UserCredentialsType.UNSET;
    }

    public String getAppId() {
        Configuration.getErrorTracker().i(TAG, "getAppId()");
        return appId;
    }

    public String getApiKey() {
        Configuration.getErrorTracker().i(TAG, "getApiKey()");
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

        public static UserCredentialsType fromString(String value) {
            if (value == null) {
                return null;
            }

            for (UserCredentialsType type : UserCredentialsType.values()) {
                if (value.equalsIgnoreCase(type.value)) {
                    return type;
                }
            }

            return null;
        }
    }
}
