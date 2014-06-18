package com.arkhive.components.test_session_manager_fixes;

import java.util.concurrent.TimeUnit;

/**
 * Created by on 6/17/2014.
 */
public final class Configuration {
    public static final int DEFAULT_HTTP_READ_TIMEOUT = 30000;
    public static final int DEFAULT_HTTP_CONNECTION_TIMEOUT = 30000;
    public static final int DEFAULT_MINIMUM_SESSION_TOKENS = 1;
    public static final int DEFAULT_MAXIMUM_SESSION_TOKENS = 3;
    public static final int DEFAULT_HTTP_POOL_SIZE = 6;

    private int httpReadTimeout = DEFAULT_HTTP_READ_TIMEOUT;
    private int httpConnectionTimeout = DEFAULT_HTTP_CONNECTION_TIMEOUT;
    private int minimumSessionTokens = DEFAULT_MINIMUM_SESSION_TOKENS;
    private int maximumSessionTokens = DEFAULT_MAXIMUM_SESSION_TOKENS;
    private int httpPoolSize = DEFAULT_HTTP_POOL_SIZE;
    private String appId;
    private String apiKey;

    public Configuration() {}

    public int getHttpReadTimeout() {
        return httpReadTimeout;
    }

    /**
     * Sets the http connection timeout time.
     * @param httpReadTimeout - between 5 and 60 seconds
     * @return false if not set (due to bad input), true if set.
     */
    public boolean setHttpReadTimeout(int httpReadTimeout) {
        if (httpReadTimeout < 5 || httpReadTimeout > 60) {
            return false;
        }
        this.httpReadTimeout = httpReadTimeout * 1000;
        return true;
    }

    public int getHttpConnectionTimeout() {
        return httpConnectionTimeout;
    }

    /**
     * Sets the http connection timeout time.
     * @param httpConnectionTimeout - between 5 and 60 seconds
     * @return false if not set (due to bad input), true if set.
     */
    public boolean setHttpConnectionTimeout(int httpConnectionTimeout) {
        if (httpConnectionTimeout < 5 || httpConnectionTimeout > 60) {
            return false;
        }
        this.httpConnectionTimeout = httpConnectionTimeout * 1000;
        return true;
    }

    public int getMinimumSessionTokens() {
        return minimumSessionTokens;
    }

    public int getMaximumSessionTokens() {
        return maximumSessionTokens;
    }

    /**
     * Sets the limit on the number of session tokens stored.
     *
     * @param min - between 0 and 10, must be less than maximumSessionTokens
     * @param max - between 1 and 10, must be greater than minimumSessionTokens
     * @return false if not set (due to bad input), true if set.
     */
    public boolean setSessionTokensInBlockingQueueMinMax(int min, int max) {
        if (min > max || min < 0 || max < 1 || min > 10 || max > 10) {
            return false;
        }
        maximumSessionTokens = max;
        minimumSessionTokens = min;
        return true;
    }

    public String getAppId() {
        return appId;
    }

    /**
     * Sets the app id value.
     *
     * @param appId - a string value representing an app id.
     * @return false if not set (due to bad input), true if set.
     */
    public boolean setAppId(String appId) {
        if (appId == null) {
            return false;
        }
        this.appId = appId;
        return true;
    }

    public String getApiKey() {
        return apiKey;
    }

    /**
     * Sets the api key value.
     *
     * @param apiKey - a string value representing an app id.
     * @return false if not set (due to bad input), true if set.
     */
    public boolean setApiKey(String apiKey) {
        if (apiKey == null) {
            return false;
        }
        this.apiKey = apiKey;
        return true;
    }

    public int getHttpPoolSize() {
        return httpPoolSize;
    }

    /**
     * Sets the thread pool size for the http processor
     * @param httpPoolSize - minimum 1
     * @return false if not set (bad input), true if set
     */
    public boolean setHttpPoolSize(int httpPoolSize) {
        if (httpPoolSize < 1) {
            return false;
        }
        this.httpPoolSize = httpPoolSize;
        return true;
    }
}
