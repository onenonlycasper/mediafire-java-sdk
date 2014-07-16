package com.arkhive.components.core.module_api_descriptor.requests;

import com.arkhive.components.core.Configuration;
import com.arkhive.components.core.module_api_descriptor.ApiRequestObject;
import com.arkhive.components.core.module_http_processor.HttpPeriProcessor;
import com.arkhive.components.core.module_http_processor.interfaces.HttpProcessor;
import com.arkhive.components.core.module_http_processor.interfaces.HttpRequestCallback;
import com.arkhive.components.core.module_token_farm.interfaces.SessionTokenDistributor;



public class BlockingApiGetRequest implements HttpRequestCallback {
    private static final String TAG = BlockingApiGetRequest.class.getCanonicalName();
    private final HttpProcessor httpPreProcessor;
    private final HttpProcessor httpPostProcessor;
    private SessionTokenDistributor sessionTokenDistributor;
    private ApiRequestObject apiRequestObject;
    private HttpPeriProcessor httpPeriProcessor;

    public BlockingApiGetRequest(HttpProcessor httpPreProcessor,
                                 HttpProcessor httpPostProcessor,
                                 SessionTokenDistributor sessionTokenDistributor,
                                 HttpPeriProcessor httpPeriProcessor,
                                 ApiRequestObject apiRequestObject) {
        this.httpPreProcessor = httpPreProcessor;
        this.httpPostProcessor = httpPostProcessor;
        this.sessionTokenDistributor = sessionTokenDistributor;
        this.apiRequestObject = apiRequestObject;
        this.httpPeriProcessor = httpPeriProcessor;
    }

    public ApiRequestObject sendRequest() {
        Configuration.getErrorTracker().i(TAG, "sendRequest()");
        synchronized (this) {
            // borrow a session token from the TokenFarm
            // workaround for session token issue for calls that don't require session token (will not be needed once sdk is refactored)
            if (!apiRequestObject.getRequiredParameters().containsKey("no_session_token_needed")) {
                sessionTokenDistributor.borrowSessionToken(apiRequestObject);
            }
            // send request to http handler
            httpPeriProcessor.sendGetRequest(this, httpPreProcessor, httpPostProcessor, apiRequestObject);
            // wait until we get a response from http handler (or 10 seconds pass)
            try {
                wait(Configuration.DEFAULT_HTTP_CONNECTION_TIMEOUT);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            // try to return the session token to the TokenFarm
            // workaround for session token issue for calls that don't require session token (will not be needed once sdk is refactored)
            if (!apiRequestObject.getRequiredParameters().containsKey("no_session_token_needed")) {
                sessionTokenDistributor.returnSessionToken(apiRequestObject);
            }
            return apiRequestObject;
        }
    }

    @Override
    public void httpRequestStarted(ApiRequestObject apiRequestObject) {
        Configuration.getErrorTracker().i(TAG, "httpRequestStarted()");
    }

    @Override
    public void httpRequestFinished(ApiRequestObject apiRequestObject) {
        Configuration.getErrorTracker().i(TAG, "httpRequestFinished()");
        synchronized (this) {
            notify();
        }
    }
}
