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
    private HttpPeriProcessor httpPeriProcessor;
    private ApplicationCredentials applicationCredentials;
    private TokenFarm tokenFarm;
    private Api api;

    private static MediaFire instance;

    private MediaFire(Configuration configuration) {
        httpPeriProcessor = new HttpPeriProcessor(configuration);
        applicationCredentials = new ApplicationCredentials(configuration);
        tokenFarm = new TokenFarm(configuration, applicationCredentials, httpPeriProcessor);
        api = new Api(tokenFarm, tokenFarm, httpPeriProcessor);
    }

    public static MediaFire getInstance() {
        return instance;
    }

    public static MediaFire newInstance(Configuration configuration) {
        if (instance == null) {
            instance = new MediaFire(configuration);
        } else {
            instance.shutdown();
            instance = new MediaFire(configuration);
        }

        return instance;
    }

    public Api apiCall() {
        return api;
    }

    /**
     * Uses a callback to give back a SessionToken object. Verify the SessionToken object via
     * SessionToken.getTokenString() or use the ApiRequestObject to figure out what went wrong via
     * ApiRequestObject.apiResponse().getMessage() or getError() etc. If the SessionToken object is valid
     * then call MediaFire.getApplicationCredentials().setCredentialsValid(true) and MediaFire.startup().
     * @param getNewSessionTokenCallback
     */
    public void tryLogin(GetNewSessionTokenCallback getNewSessionTokenCallback) {
        tokenFarm.getNewSessionToken(getNewSessionTokenCallback);
    }

    /**
     * retrieves the application credentials object.
     * @return the application credentials object.
     */
    public ApplicationCredentials getApplicationCredentials() {
        return applicationCredentials;
    }

    /**
     * Starts the Token Farm. This does nothing if application credentials have not been set. To set
     * application credentials as validated then call ApplicationCredentials.setCredentialsValid(true)
     */
    public void startup() {
        tokenFarm.startup();
    }

    public HttpPeriProcessor getHttpProcessor() {
        return httpPeriProcessor;
    }
    /**
     * Shuts down the modules used by MediaFire and clears application credentials. A new instance of MediaFire will
     * need to be created via MediaFire.newInstance(...)
     */
    public void shutdown() {
        applicationCredentials.clearCredentials();
        httpPeriProcessor.shutdown();
        tokenFarm.shutdown();
        instance = null;
    }
}
