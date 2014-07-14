package com.arkhive.components.core.module_api_descriptor.requests;

import com.arkhive.components.core.Configuration;
import com.arkhive.components.core.module_api_descriptor.ApiRequestObject;
import com.arkhive.components.core.module_http_processor.HttpPeriProcessor;
import com.arkhive.components.core.module_http_processor.interfaces.HttpProcessor;
import com.arkhive.components.core.module_http_processor.interfaces.HttpRequestCallback;

/**
 * Created by  on 7/14/2014.
 */
public class BlockingApiHttpsPostRequest implements HttpRequestCallback {
    private static final String TAG = BlockingApiGetRequest.class.getSimpleName();
    private final HttpProcessor httpPreProcessor;
    private final HttpProcessor httpPostProcessor;
    private ApiRequestObject apiRequestObject;
    private HttpPeriProcessor httpPeriProcessor;

    public BlockingApiHttpsPostRequest(HttpProcessor httpPreProcessor,
                                      HttpProcessor httpPostProcessor,
                                      HttpPeriProcessor httpPeriProcessor,
                                      ApiRequestObject apiRequestObject) {
        this.httpPreProcessor = httpPreProcessor;
        this.httpPostProcessor = httpPostProcessor;
        this.apiRequestObject = apiRequestObject;
        this.httpPeriProcessor = httpPeriProcessor;
    }

    public ApiRequestObject sendRequest() {
        Configuration.getErrorTracker().i(TAG, "sendRequest()");
        synchronized (this) {
            // send request to http handler
            httpPeriProcessor.sendHttpsPostRequest(this, httpPreProcessor, httpPostProcessor, apiRequestObject);
            // wait until we get a response from http handler
            try {
                wait(Configuration.DEFAULT_HTTP_CONNECTION_TIMEOUT);
            } catch (InterruptedException e) {
                e.printStackTrace();
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
