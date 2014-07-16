package com.arkhive.components.core;

import com.arkhive.components.core.module_api.Api;
import com.arkhive.components.core.module_credentials.ApplicationCredentials;
import com.arkhive.components.core.module_http_processor.HttpPeriProcessor;
import com.arkhive.components.core.module_token_farm.TokenFarm;
import com.arkhive.components.core.module_token_farm.interfaces.GetNewSessionTokenCallback;

/**
 * Created by  on 6/17/2014.
 */
public class MediaFire {
    private static final String TAG = MediaFire.class.getCanonicalName();
    private HttpPeriProcessor httpPeriProcessor;
    private ApplicationCredentials applicationCredentials;
    private TokenFarm tokenFarm;
    private Api api;
    private Configuration configuration;

    public MediaFire(Configuration configuration) {
        if (configuration == null) {
            throw new IllegalArgumentException("Configuration cannot be null");
        }
        this.configuration = configuration;
        httpPeriProcessor = new HttpPeriProcessor(this.configuration);
        applicationCredentials = new ApplicationCredentials(this.configuration);
        tokenFarm = new TokenFarm(this.configuration, applicationCredentials, httpPeriProcessor);
        api = new Api(tokenFarm, tokenFarm, httpPeriProcessor);
    }

    public Api apiCall() {
        return api;
    }

    public Configuration getConfiguration() {
        return configuration;
    }

    /**
     * Uses a callback to give back a SessionToken object. Verify the SessionToken object via
     * SessionToken.getTokenString() or use the ApiRequestObject to figure out what went wrong via
     * ApiRequestObject.apiResponse().getMessage() or getError() etc. If the SessionToken object is valid
     * then call MediaFire.getApplicationCredentials().setCredentialsValid(true) and MediaFire.startup().
     * @param getNewSessionTokenCallback
     */
    public void tryLogin(GetNewSessionTokenCallback getNewSessionTokenCallback) {
        Configuration.getErrorTracker().i(TAG, "tryLogin()");
        Configuration.getErrorTracker().i(TAG, "tryLogin() on thread: " + Thread.currentThread().getName());
        httpPeriProcessor.startup();
        tokenFarm.resumeExecutor();
        tokenFarm.getNewSessionToken(getNewSessionTokenCallback);
    }

    /**
     * retrieves the application credentials object.
     * @return the application credentials object.
     */
    public ApplicationCredentials getApplicationCredentials() {
        Configuration.getErrorTracker().i(TAG, "getApplicationCredentials()");
        return applicationCredentials;
    }

    /**
     * Starts the Token Farm. This does nothing if application credentials have not been set. To set
     * application credentials as validated then call ApplicationCredentials.setCredentialsValid(true)
     */
    public void startup() {
        Configuration.getErrorTracker().i(TAG, "startup()");
        tokenFarm.startup();
    }

    public HttpPeriProcessor getHttpProcessor() {
        Configuration.getErrorTracker().i(TAG, "getHttpProcessor()");
        return httpPeriProcessor;
    }
    /**
     * Shuts down the modules used by MediaFire and clears application credentials. A new instance of MediaFire will
     * need to be created via MediaFire.newInstance(...)
     */
    public void shutdown() {
        Configuration.getErrorTracker().i(TAG, "shutdown()");
        // TODO: wrap if (instance == null) around below statement.
        applicationCredentials.clearCredentials();
        httpPeriProcessor.shutdown();
        tokenFarm.shutdown();
    }
}
