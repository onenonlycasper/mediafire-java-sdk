package com.arkhive.components.core.module_api_descriptor.requests;

import com.arkhive.components.core.Configuration;
import com.arkhive.components.core.module_api.Api;
import com.arkhive.components.core.module_api.responses.ApiResponse;
import com.arkhive.components.core.module_api_descriptor.ApiRequestObject;
import com.arkhive.components.core.module_api_descriptor.interfaces.ApiRequestRunnableCallback;
import com.arkhive.components.core.module_http_processor.HttpPeriProcessor;
import com.arkhive.components.core.module_http_processor.interfaces.HttpProcessor;
import com.arkhive.components.core.module_http_processor.interfaces.HttpRequestCallback;
import com.arkhive.components.core.module_token_farm.interfaces.ActionTokenDistributor;
import com.google.gson.Gson;



/**
 * Created by  on 6/19/2014.
 */
public class RunnableApiPostRequestUploadToken<T extends ApiResponse> implements Runnable, HttpRequestCallback {
    private static final String TAG = RunnableApiGetRequest.class.getCanonicalName();
    private final HttpProcessor httpPreProcessor;
    private final HttpProcessor httpPostProcessor;
    private ApiRequestRunnableCallback<T> callback;
    private ActionTokenDistributor actionTokenDistributor;
    private ApiRequestObject apiRequestObject;
    private HttpPeriProcessor httpPeriProcessor;
    private Class<T> clazz;

    public RunnableApiPostRequestUploadToken(Class<T> clazz,
                                             ApiRequestRunnableCallback<T> callback,
                                             HttpProcessor httpPreProcessor,
                                             HttpProcessor httpPostProcessor,
                                             ActionTokenDistributor actionTokenDistributor,
                                             HttpPeriProcessor httpPeriProcessor,
                                             ApiRequestObject apiRequestObject) {
        this.clazz = clazz;
        this.callback = callback;
        this.httpPreProcessor = httpPreProcessor;
        this.httpPostProcessor = httpPostProcessor;
        this.actionTokenDistributor = actionTokenDistributor;
        this.apiRequestObject = apiRequestObject;
        this.httpPeriProcessor = httpPeriProcessor;
    }

    @Override
    public void run() {
        Configuration.getErrorTracker().i(TAG, "sendRequest()");
        synchronized (this) {
            // notify our callback that the request is being processed now
            if (callback != null) {
                callback.apiRequestProcessStarted();
            }
            // "borrow" an upload token from the TokenFarm
            actionTokenDistributor.borrowUploadActionToken(apiRequestObject);
            // send request to http handler
            httpPeriProcessor.sendPostRequest(this, httpPreProcessor, httpPostProcessor, apiRequestObject);
            // wait until we get a response from http handler (or 10 seconds pass)
            try {
                wait(Configuration.DEFAULT_HTTP_CONNECTION_TIMEOUT);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            // notify our callback that the request is being finished now
            if (callback != null) {
                T response = new Gson().fromJson(Api.getResponseString(apiRequestObject.getHttpResponseString()), clazz);
                callback.apiRequestProcessFinished(response);
            }
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