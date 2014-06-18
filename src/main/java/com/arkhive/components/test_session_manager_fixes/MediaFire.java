package com.arkhive.components.test_session_manager_fixes;

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

    private static MediaFire instance;

    private MediaFire(Configuration configuration) {
        this.configuration = configuration;
        httpPeriProcessor = new HttpPeriProcessor();
        applicationCredentials = new ApplicationCredentials();
        tokenFarm = new TokenFarm(applicationCredentials, httpPeriProcessor);
    }

    public static MediaFire getInstance() {
        return instance;
    }

    public static MediaFire newInstance(Configuration configuration) {
        if (instance == null) {
            instance = new MediaFire(configuration);
        } else {
            instance.shutdown();
        }

        return instance;
    }

    public void setConfiguration(Configuration configuration) {
        this.configuration = configuration;
        httpPeriProcessor.setConnectionTimeout(configuration.getHttpConnectionTimeout());
        httpPeriProcessor.setReadTimeout(configuration.getHttpReadTimeout());
        httpPeriProcessor.setCorePoolSize(configuration);
        tokenFarm.setMinimumSessionTokens(configuration.getMinimumSessionTokens());
        tokenFarm.setMaximumSessionTokens(configuration.getMaximumSessionTokens());
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

    public void startup() {
        tokenFarm.startup();
    }

    public void shutdown() {
        httpPeriProcessor.shutdown();
        tokenFarm.shutdown();
        applicationCredentials.clearCredentials();
    }
}
