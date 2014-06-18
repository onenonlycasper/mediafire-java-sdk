package com.arkhive.components.test_session_manager_fixes;

import com.arkhive.components.test_session_manager_fixes.module_api.Api;
import com.arkhive.components.test_session_manager_fixes.module_credentials.ApplicationCredentials;
import com.arkhive.components.test_session_manager_fixes.module_http_processor.HttpPeriProcessor;
import com.arkhive.components.test_session_manager_fixes.module_token_farm.TokenFarm;

/**
 * Created by Chris Najar on 6/17/2014.
 */
public class MediaFire {
    private HttpPeriProcessor httpPeriProcessor;
    private ApplicationCredentials applicationCredentials;
    private TokenFarm tokenFarm;
    private Configuration configuration;
    private Api api;

    private static MediaFire instance;

    private MediaFire(Configuration configuration) {
        this.configuration = configuration;
        httpPeriProcessor = new HttpPeriProcessor(configuration);
        applicationCredentials = new ApplicationCredentials(configuration);
        tokenFarm = new TokenFarm(applicationCredentials, httpPeriProcessor);
        api = new Api(tokenFarm, httpPeriProcessor);
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

    public void setConfiguration(Configuration configuration) {
        this.configuration = configuration;
    }

    public HttpPeriProcessor getHttpPeriProcessor() {
        return httpPeriProcessor;
    }

    public Configuration getConfiguration() {
        return configuration;
    }

    public ApplicationCredentials getApplicationCredentials() {
        return applicationCredentials;
    }

    public TokenFarm getTokenFarm() {
        return tokenFarm;
    }

    /**
     * Stars the Token Farm. This does nothing if application credentials have not been set. To set
     * application credentials as validated then call ApplicationCredentials.setCredentialsValid(true)
     */
    public void startup() {
        if (applicationCredentials.isCredentialsValid()) {
            tokenFarm.startup();
        }
    }

    /**
     * Shuts down the modules used by MediaFire and clears application credentials. A new instance of MediaFire will
     * need to be created via MediaFire.newInstance(...)
     */
    public void shutdown() {
        httpPeriProcessor.shutdown();
        tokenFarm.shutdown();
        applicationCredentials.clearCredentials();
        instance = null;
    }
}
