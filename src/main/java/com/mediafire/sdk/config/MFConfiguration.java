package com.mediafire.sdk.config;

import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;

/**
 * Created by Chris Najar on 7/17/2014.
 */
public final class MFConfiguration {
    private final int httpReadTimeout;
    private final int httpConnectionTimeout;
    private final int minimumSessionTokens;
    private final int maximumSessionTokens;
    private final int httpPoolSize;
    private final String appId;
    private final String apiKey;
    private final MFLogger mfLogger;
    private final MFCredentials mfCredentials;
    private final Executor mfExecutorService;

    private MFConfiguration(MFConfigurationBuilder mfConfigurationBuilder) {
        this.httpReadTimeout = mfConfigurationBuilder.httpReadTimeout;
        this.httpConnectionTimeout = mfConfigurationBuilder.httpConnectionTimeout;
        this.minimumSessionTokens = mfConfigurationBuilder.minimumSessionTokens;
        this.maximumSessionTokens = mfConfigurationBuilder.maximumSessionTokens;
        this.httpPoolSize = mfConfigurationBuilder.httpPoolSize;
        this.appId = mfConfigurationBuilder.appId;
        this.apiKey = mfConfigurationBuilder.apiKey;
        this.mfLogger = mfConfigurationBuilder.mfLogger;
        this.mfCredentials = mfConfigurationBuilder.mfCredentials;
        this.mfExecutorService = mfConfigurationBuilder.mfExecutorService;
    }

    public int getHttpReadTimeout() {
        return httpReadTimeout;
    }

    public int getHttpConnectionTimeout() {
        return httpConnectionTimeout;
    }

    public int getMinimumSessionTokens() {
        return minimumSessionTokens;
    }

    public int getMaximumSessionTokens() {
        return maximumSessionTokens;
    }

    public String getAppId() {
        return appId;
    }

    public String getApiKey() {
        return apiKey;
    }

    public int getHttpPoolSize() {
        return httpPoolSize;
    }

    public MFLogger getMfLogger() {
        return mfLogger;
    }

    public MFCredentials getMfCredentials() {
        return mfCredentials;
    }

    public Executor getMfExecutor() {
        return mfExecutorService;
    }

    public static class MFConfigurationBuilder {
        private static final int DEFAULT_HTTP_READ_TIMEOUT = 45000;
        private static final int DEFAULT_HTTP_CONNECTION_TIMEOUT = 45000;
        private static final int DEFAULT_MINIMUM_SESSION_TOKENS = 1;
        private static final int DEFAULT_MAXIMUM_SESSION_TOKENS = 3;
        private static final int DEFAULT_HTTP_POOL_SIZE = 6;
        private static final MFLogger DEFAULT_MF_LOGGER = new MFDefaultLogger();
        private static final MFCredentials DEFAULT_MF_CREDENTIALS = new MFDefaultCredentials();
        private static final Executor DEFAULT_EXECUTOR_SERVICE = new MFDefaultExecutor();

        private int httpReadTimeout = DEFAULT_HTTP_READ_TIMEOUT;
        private int httpConnectionTimeout = DEFAULT_HTTP_CONNECTION_TIMEOUT;
        private int minimumSessionTokens = DEFAULT_MINIMUM_SESSION_TOKENS;
        private int maximumSessionTokens = DEFAULT_MAXIMUM_SESSION_TOKENS;
        private int httpPoolSize = DEFAULT_HTTP_POOL_SIZE;
        private MFLogger mfLogger = DEFAULT_MF_LOGGER;
        private MFCredentials mfCredentials = DEFAULT_MF_CREDENTIALS;
        private Executor mfExecutorService = DEFAULT_EXECUTOR_SERVICE;
        private String appId;
        private String apiKey;

        public MFConfigurationBuilder(String appId, String apiKey) {
            this.appId = appId;
            this.apiKey = apiKey;
        }

        public MFConfigurationBuilder httpReadTimeout(int httpReadTimeout) {
            if (httpReadTimeout < 0) {
                throw new IllegalStateException("http read timeout must not be negative");
            }
            this.httpReadTimeout = httpReadTimeout;
            return this;
        }

        public MFConfigurationBuilder httpConnectionTimeout(int httpConnectionTimeout) {
            if (httpConnectionTimeout < 0) {
                throw new IllegalStateException("http connection timeout must not be negative");
            }
            this.httpConnectionTimeout = httpConnectionTimeout;
            return this;
        }

        public MFConfigurationBuilder httpPoolSize(int httpPoolSize) {
            if (httpPoolSize < 1) {
                throw new IllegalStateException("http pool size must be greater than 0");
            }
            this.httpPoolSize = httpPoolSize;
            return this;
        }

        public MFConfigurationBuilder mfLogger(MFLogger mfLogger) {
            if (mfLogger == null) {
                throw new IllegalStateException("MFLogger cannot be null");
            }
            this.mfLogger = mfLogger;
            return this;
        }

        public MFConfigurationBuilder mfCredentials(MFCredentials mfCredentials) {
            if (mfCredentials == null) {
                throw new IllegalStateException("MFCredentials cannot be null");
            }
            this.mfCredentials = mfCredentials;
            return this;
        }

        public MFConfigurationBuilder mfExecutorService(ExecutorService mfExecutorService) {
            if (mfExecutorService == null) {
                throw new IllegalStateException("ExecutorService cannot be null");
            }
            this.mfExecutorService = mfExecutorService;
            return this;
        }

        public MFConfigurationBuilder minimumSessionTokens(int minimumSessionTokens) {
            if (minimumSessionTokens < 1) {
                throw new IllegalStateException("minimumSessionTokens session tokens must be greater than 0");
            }
            this.minimumSessionTokens = minimumSessionTokens;
            return this;
        }

        public MFConfigurationBuilder maximumSessionTokens(int maximumSessionTokens) {
            if (maximumSessionTokens < 1) {
                throw new IllegalStateException("maximum session tokens must be greater than 0");
            }
            this.maximumSessionTokens = maximumSessionTokens;
            return this;
        }

        public MFConfiguration build() {
            if (appId == null) {
                throw new IllegalStateException("app id cannot be null");
            }

            if (apiKey == null) {
                throw new IllegalStateException("api key cannot be null");
            }
            return new MFConfiguration(this);
        }
    }
}
