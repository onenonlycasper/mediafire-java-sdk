package com.arkhive.components.test_session_manager_fixes;

/**
 * Created by on 6/17/2014.
 */
public class Configuration {
    private final int DEFAULT_HTTP_READ_TIMEOUT = 30000;
    private final int DEFAULT_HTTP_CONNECTION_TIMEOUT = 30000;
    private final int DEFAULT_MINIMUM_SESSION_TOKENS = 1;
    private final int DEFAULT_MAXIMUM_SESSION_TOKENS = 3;
    private int httpReadTimeout = DEFAULT_HTTP_READ_TIMEOUT;
    private int httpConnectionTimeout = DEFAULT_HTTP_CONNECTION_TIMEOUT;
    private int minimumSessionTokensInBlockingQueue = DEFAULT_MINIMUM_SESSION_TOKENS;
    private int maximumSessionTokensInBlockingQueue = DEFAULT_MAXIMUM_SESSION_TOKENS;
    private String appId;
    private String apiKey;

    public Configuration() {}

    public int getHttpReadTimeout() {
        return httpReadTimeout;
    }

    public void setHttpReadTimeout(int httpReadTimeout) {
        this.httpReadTimeout = httpReadTimeout;
    }

    public int getHttpConnectionTimeout() {
        return httpConnectionTimeout;
    }

    public void setHttpConnectionTimeout(int httpConnectionTimeout) {
        this.httpConnectionTimeout = httpConnectionTimeout;
    }

    public int getMinimumSessionTokensInBlockingQueue() {
        return minimumSessionTokensInBlockingQueue;
    }

    public int getMaximumSessionTokensInBlockingQueue() {
        return maximumSessionTokensInBlockingQueue;
    }

    /**
     * Sets the limit on the number of session tokens stored.
     * @param min - between 1 and 10, must be less than maximumSessionTokensInBlockingQueue
     * @param maximumSessionTokensInBlockingQueue - between 1 and 10, must be greater than minimumSessionTokensInBlockingQueue
     * @return false if not set (due to bad input), true if set.
     */
    public boolean setSessionTokensInBlockingQueueMinMax(int min, int maximumSessionTokensInBlockingQueue) {
        if (min > maximumSessionTokensInBlockingQueue ||
                min < 0 || maximumSessionTokensInBlockingQueue < 1) {
            return false;
        }
        this.maximumSessionTokensInBlockingQueue = maximumSessionTokensInBlockingQueue;
        return true;
    }

    public String getAppId() {
        return appId;
    }

    public void setAppId(String appId) {
        this.appId = appId;
    }

    public String getApiKey() {
        return apiKey;
    }

    public void setApiKey(String apiKey) {
        this.apiKey = apiKey;
    }
}
